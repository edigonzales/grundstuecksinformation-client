package ch.so.agi.grundstuecksinformation.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Service;

import ch.ehi.oereb.schemas.oereb._1_0.extract.GetExtractByIdResponse;
import ch.ehi.oereb.schemas.oereb._1_0.extractdata.ExtractType;
import ch.ehi.oereb.schemas.oereb._1_0.extractdata.LanguageCodeType;
import ch.so.agi.grundstuecksinformation.shared.models.Egrid;
import ch.so.agi.grundstuecksinformation.shared.models.RealEstateDPR;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.transform.stream.StreamSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class OerebExtractService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    Jaxb2Marshaller marshaller;

    private static final LanguageCodeType DE = LanguageCodeType.DE;

    Map<String, String> realEstateTypesMap = Stream.of(new String[][] {
        { "Distinct_and_permanent_rights.BuildingRight", "Baurecht" }, 
        { "RealEstate", "Liegenschaft" }, 
    })
    .collect(Collectors.toMap(data -> data[0], data -> data[1]));

    public RealEstateDPR getExtract(Egrid egrid, RealEstateDPR realEstateDPR) throws IOException {
        logger.info("******: " + egrid.getOerebServiceBaseUrl());
        
        File xmlFile;
        xmlFile = Files.createTempFile("oereb_extract_", ".xml").toFile();
        URL url = new URL(egrid.getOerebServiceBaseUrl() + "extract/reduced/xml/geometry/" + egrid.getEgrid());
        logger.info(url.toString());

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", "application/xml");

        if (connection.getResponseCode() != 200) {
            throw new IOException(connection.getResponseMessage());
        }
        
        InputStream initialStream = connection.getInputStream();
        java.nio.file.Files.copy(initialStream, xmlFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        initialStream.close();
        logger.info("File downloaded: " + xmlFile.getAbsolutePath());

        StreamSource xmlSource = new StreamSource(xmlFile);
        GetExtractByIdResponse obj = (GetExtractByIdResponse) marshaller.unmarshal(xmlSource);
        ExtractType xmlExtract = obj.getValue().getExtract().getValue();
        logger.info("Extract-Id: " + xmlExtract.getExtractIdentifier());
        
        realEstateDPR.setOerebExtractIdentifier(xmlExtract.getExtractIdentifier());


        return realEstateDPR;
    }
}
