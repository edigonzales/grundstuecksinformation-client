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
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.ServletException;
import javax.xml.bind.JAXBElement;
import javax.xml.transform.stream.StreamSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import ch.ehi.oereb.schemas.oereb._1_0.extract.GetEGRIDResponse;
import ch.ehi.oereb.schemas.oereb._1_0.extract.GetEGRIDResponseType;
import ch.so.agi.grundstuecksinformation.shared.EgridResponse;
import ch.so.agi.grundstuecksinformation.shared.EgridService;
import ch.so.agi.grundstuecksinformation.shared.models.Egrid;

import org.slf4j.Logger;

@SuppressWarnings("serial")
public class EgridServiceImpl extends RemoteServiceServlet implements EgridService {
    Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());

    @Autowired
    Jaxb2Marshaller marshaller;

    // TODO: inject from application.properties
    private List<String> oerebServiceBaseUrlList = Stream.of(
            "https://oereb.geo.sh.ch/oereb/", 
            "https://geo.so.ch/api/oereb/")
            .collect(Collectors.toList());    
    
    private List<String> wfsUrlTemplateList = Stream.of(
            "https://wfs.geodienste.ch/av/deu?&VERSION=1.0.0&SERVICE=WFS&REQUEST=GetFeature&TYPENAME=ms:DPRSF&Filter=<Filter><PropertyIsEqualTo><PropertyName>EGRIS_EGRID</PropertyName><Literal>%s</Literal></PropertyIsEqualTo></Filter>", 
            "https://wfs.geodienste.ch/av/deu?&VERSION=1.0.0&SERVICE=WFS&REQUEST=GetFeature&TYPENAME=ms:RESF&Filter=<Filter><PropertyIsEqualTo><PropertyName>EGRIS_EGRID</PropertyName><Literal>%s</Literal></PropertyIsEqualTo></Filter>")
            .collect(Collectors.toList());    
        
    // see:
    // https://stackoverflow.com/questions/51874785/gwt-spring-boot-autowired-is-not-working
    @Override
    public void init() throws ServletException {
         super.init();
         SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this, getServletContext());
    }
    
    @Override
    public EgridResponse egridServer(String XY) throws IllegalArgumentException, IOException {
        URL egridUrl = null;
        HttpURLConnection connection = null;
        for (String baseUrl : oerebServiceBaseUrlList) {
            URL url = new URL(baseUrl + "getegrid/xml/?XY=" + XY.replace(" ",""));
            logger.info(url.toString());
            
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/xml");

            if (connection.getResponseCode() == 200) {
                egridUrl = url;
                break;
            }
        }
        
        if (egridUrl == null) {
            // TODO: handle this case.
            // Do not throw exception but return some value (?).
        }
        
        File xmlFile = Files.createTempFile("egrid_", ".xml").toFile();
        
        InputStream initialStream = connection.getInputStream();
        java.nio.file.Files.copy(initialStream, xmlFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        initialStream.close();
        logger.info("File downloaded: " + xmlFile.getAbsolutePath());

        StreamSource xmlSource = new StreamSource(xmlFile);
        GetEGRIDResponse obj = (GetEGRIDResponse) marshaller.unmarshal(xmlSource);
        GetEGRIDResponseType egridResponseType = obj.getValue();
        List<JAXBElement<String>> egridXmlList = egridResponseType.getEgridAndNumberAndIdentDN();
        
        List<Egrid> egridList = new ArrayList<Egrid>();
        for (int i=0; i<egridXmlList.size(); i=i+3) {
            Egrid egridObj = new Egrid();
            egridObj.setEgrid(egridXmlList.get(i).getValue());
            egridObj.setNumber(egridXmlList.get(i+1).getValue());
            egridObj.setIdentDN(egridXmlList.get(i+2).getValue());
            
            // Does not work either since RESF (Liegenschaften) are
            // too slow to query.
            // No index on EGRIS_EGRID?
            /*
            for (String wfsUrlTemplate : wfsUrlTemplateList) {
                String wfsUrl = String.format(wfsUrlTemplate, egridObj.getEgrid());
                logger.info("wfs url: " + wfsUrl);
                
                URL url = new URL(wfsUrl);
                String content;
                try (Scanner scanner = new Scanner(url.openStream(), String.valueOf("UTF-8"))) {
                    content = scanner.useDelimiter("\\A").next();
                }
                logger.info(content);
            }
            */

            logger.info(egridObj.toString());
            egridList.add(egridObj);
        }

        EgridResponse response = new EgridResponse();
        response.setEgrid(egridList);
        return response;
    }
}
