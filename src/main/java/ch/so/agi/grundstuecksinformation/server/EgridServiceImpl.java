package ch.so.agi.grundstuecksinformation.server;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.xml.bind.JAXBElement;
import javax.xml.transform.stream.StreamSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import ch.so.agi.grundstuecksinformation.shared.EgridResponse;
import ch.so.agi.grundstuecksinformation.shared.EgridService;
import ch.so.agi.grundstuecksinformation.shared.models.Egrid;
import ch.so.geo.schema.agi.cadastre._0_9.extract.GetEGRIDResponse;
import ch.so.geo.schema.agi.cadastre._0_9.extract.RealEstateType;

import org.slf4j.Logger;

@SuppressWarnings("serial")
public class EgridServiceImpl extends RemoteServiceServlet implements EgridService {
    Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());

    @Autowired
    Jaxb2Marshaller marshaller;

    @Value("${app.cadastreServiceUrl}")
    private String cadastreServiceUrl;

    // see:
    // https://stackoverflow.com/questions/51874785/gwt-spring-boot-autowired-is-not-working
    @Override
    public void init() throws ServletException {
        super.init();
        SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this, getServletContext());
    }

    @Override
    public EgridResponse egridServer(String XY) throws IllegalArgumentException, IOException {
        HttpURLConnection connection = null;
        int responseCode = 0;

        URL url = new URL(cadastreServiceUrl + "getegrid/xml/?XY=" + XY.replace(" ", ""));
        logger.info(url.toString());

        connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", "application/xml");
        responseCode = connection.getResponseCode();

        if (responseCode == 200) {
            logger.debug("E-GRID found: " + url);
        } else {
            EgridResponse response = new EgridResponse();
            response.setResponseCode(responseCode);
            return response;
        }

        File xmlFile = Files.createTempFile("egrid_", ".xml").toFile();

        InputStream initialStream = connection.getInputStream();
        java.nio.file.Files.copy(initialStream, xmlFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        initialStream.close();
        logger.debug("File downloaded: " + xmlFile.getAbsolutePath());

        StreamSource xmlSource = new StreamSource(xmlFile);
        GetEGRIDResponse obj = (GetEGRIDResponse) marshaller.unmarshal(xmlSource);
        List<JAXBElement<?>> egridXmlList = obj.getEGRIDSAndPlannedsAndLimits();

        List<Egrid> egridList = new ArrayList<Egrid>();
        for (int i = 0; i < egridXmlList.size(); i = i + 7) {
            Egrid egridObj = new Egrid();
            egridObj.setEgrid((String) egridXmlList.get(i).getValue());
            egridObj.setNumber((String) egridXmlList.get(i + 1).getValue());
            egridObj.setIdentDN((String) egridXmlList.get(i + 2).getValue());
            RealEstateType realEstateType = (RealEstateType) egridXmlList.get(i+3).getValue();
            if (realEstateType.value().equalsIgnoreCase("Distinct_and_permanent_rights.BuildingRight")) {
                egridObj.setType("Baurecht");
            } else {
                egridObj.setType("Liegenschaft");
            }
            // TODO stateOf
            egridObj.setLimit((String) egridXmlList.get(i + 5).getValue());
            egridObj.setPlanned((Boolean)egridXmlList.get(i + 6).getValue());
            egridList.add(egridObj);
        }
        
        EgridResponse response = new EgridResponse();
        response.setResponseCode(responseCode);
        response.setEgrid(egridList);
        return response;
    }
}
