package ch.so.agi.grundstuecksinformation.server;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.xml.transform.stream.StreamSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Service;

import ch.so.agi.grundstuecksinformation.shared.models.Building;
import ch.so.agi.grundstuecksinformation.shared.models.BuildingEntry;
import ch.so.agi.grundstuecksinformation.shared.models.Egrid;
import ch.so.agi.grundstuecksinformation.shared.models.LandCoverShare;
import ch.so.agi.grundstuecksinformation.shared.models.Office;
import ch.so.agi.grundstuecksinformation.shared.models.PostalAddress;
import ch.so.agi.grundstuecksinformation.shared.models.RealEstateDPR;
import ch.so.geo.schema.agi.cadastre._0_9.extract.AddressType;
import ch.so.geo.schema.agi.cadastre._0_9.extract.BuildingEntryType;
import ch.so.geo.schema.agi.cadastre._0_9.extract.BuildingType;
import ch.so.geo.schema.agi.cadastre._0_9.extract.Extract;
import ch.so.geo.schema.agi.cadastre._0_9.extract.GetExtractByIdResponse;
import ch.so.geo.schema.agi.cadastre._0_9.extract.LandCoverShareType;
import ch.so.geo.schema.agi.cadastre._0_9.extract.LocalNameType;

@Service
public class CadastreExtractService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    Jaxb2Marshaller marshaller;

    @Value("${app.cadastreServiceUrl}")
    private String cadastreServiceUrl;

    public RealEstateDPR getExtract(Egrid egrid, RealEstateDPR realEstateDPR) throws IOException {  
        HttpURLConnection connection = null;
        int responseCode = 0;
        URL url = new URL(cadastreServiceUrl + "extract/xml/geometry/" + egrid.getEgrid());
        logger.info(url.toString());
        
        connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", "application/xml");
        responseCode = connection.getResponseCode();
        
        if (responseCode != 200) {
            logger.error("response code: {}", responseCode );
            return realEstateDPR;            
        } 
       
        File xmlFile = Files.createTempFile("cadastre_extract_", ".xml").toFile();            
        InputStream initialStream = connection.getInputStream();
        java.nio.file.Files.copy(initialStream, xmlFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        initialStream.close();
        logger.info("File downloaded: " + xmlFile.getAbsolutePath());  

        StreamSource xmlSource = new StreamSource(xmlFile);
        GetExtractByIdResponse obj = (GetExtractByIdResponse) marshaller.unmarshal(xmlSource);
        Extract xmlExtract = obj.getExtract();
        
        realEstateDPR.setCadastrePdfExtractUrl(cadastreServiceUrl + "extract/pdf/geometry/" + egrid.getEgrid());

        ch.so.geo.schema.agi.cadastre._0_9.extract.RealEstateDPR xmlRealEstate = xmlExtract.getRealEstate();
        
        Office cadastreAuthority = new Office();
        cadastreAuthority.setName(xmlRealEstate.getSupervisionOffice().getName());
        cadastreAuthority.setStreet(xmlRealEstate.getSupervisionOffice().getAddress().getStreet());
        cadastreAuthority.setNumber(xmlRealEstate.getSupervisionOffice().getAddress().getNumber());
        cadastreAuthority.setPostalCode(String.valueOf(xmlRealEstate.getSupervisionOffice().getAddress().getPostalCode()));
        cadastreAuthority.setCity(xmlRealEstate.getSupervisionOffice().getAddress().getCity());
        cadastreAuthority.setOfficeAtWeb(xmlRealEstate.getSupervisionOffice().getWeb());
        cadastreAuthority.setEmail(xmlRealEstate.getSupervisionOffice().getEmail());
        realEstateDPR.setCadastreCadastreAuthority(cadastreAuthority);
        
        Office surveyorOffice = new Office();
        surveyorOffice.setLastName(xmlRealEstate.getSurveyorOffice().getPerson().getLastName());
        surveyorOffice.setFirstName(xmlRealEstate.getSurveyorOffice().getPerson().getFirstName());
        surveyorOffice.setName(xmlRealEstate.getSurveyorOffice().getName());
        if (xmlRealEstate.getSurveyorOffice().getLine1() != null) {
            surveyorOffice.setLine1(xmlRealEstate.getSurveyorOffice().getLine1());
        }
        surveyorOffice.setStreet(xmlRealEstate.getSurveyorOffice().getAddress().getStreet());
        surveyorOffice.setNumber(xmlRealEstate.getSurveyorOffice().getAddress().getNumber());
        surveyorOffice.setPostalCode(String.valueOf(xmlRealEstate.getSurveyorOffice().getAddress().getPostalCode()));
        surveyorOffice.setCity(xmlRealEstate.getSurveyorOffice().getAddress().getCity());
        surveyorOffice.setOfficeAtWeb(xmlRealEstate.getSurveyorOffice().getWeb());
        surveyorOffice.setEmail(xmlRealEstate.getSurveyorOffice().getEmail());
        realEstateDPR.setCadastreSurveyorOffice(surveyorOffice);
        
        Office landRegisterOffice = new Office();
        landRegisterOffice.setName(xmlRealEstate.getLandRegisterOffice().getName());
        landRegisterOffice.setStreet(xmlRealEstate.getLandRegisterOffice().getAddress().getStreet());
        landRegisterOffice.setNumber(xmlRealEstate.getLandRegisterOffice().getAddress().getNumber());
        landRegisterOffice.setPostalCode(String.valueOf(xmlRealEstate.getLandRegisterOffice().getAddress().getPostalCode()));
        landRegisterOffice.setCity(xmlRealEstate.getLandRegisterOffice().getAddress().getCity());
        landRegisterOffice.setOfficeAtWeb(xmlRealEstate.getLandRegisterOffice().getWeb());
        landRegisterOffice.setEmail(xmlRealEstate.getLandRegisterOffice().getEmail());
        realEstateDPR.setCadastreLandRegisterOffice(landRegisterOffice);
        
        realEstateDPR.setIdentND(xmlRealEstate.getIdentND());
        
        ArrayList<String> localNames = new ArrayList<String>();
        List<LocalNameType> xmlLocalNames = xmlRealEstate.getLocalNames();
        for (LocalNameType xmlLocalName : xmlLocalNames) {
            localNames.add(xmlLocalName.getName());
        }
        realEstateDPR.setLocalNames(localNames);
        
        ArrayList<LandCoverShare> landCoverShares = new ArrayList<LandCoverShare>();
        List<LandCoverShareType> xmlLandCoverShares = xmlRealEstate.getLandCoverShares();
        for (LandCoverShareType xmlLandCoverShare : xmlLandCoverShares) {
            LandCoverShare landCoverShare = new LandCoverShare();
            landCoverShare.setType(xmlLandCoverShare.getType().value());
            landCoverShare.setTypeDescription(xmlLandCoverShare.getTypeDescription());
            landCoverShare.setArea(xmlLandCoverShare.getArea());
            landCoverShares.add(landCoverShare);
        }
        landCoverShares.sort(Comparator.comparing(LandCoverShare::getTypeDescription, String.CASE_INSENSITIVE_ORDER));
        
        realEstateDPR.setLandCoverShares(landCoverShares);
        
        ArrayList<Building> buildings = new ArrayList<Building>();
        List<BuildingType> xmlBuildings = xmlRealEstate.getBuildings();
        for (BuildingType xmlBuilding : xmlBuildings) {
            Building building = new Building();
            if (xmlBuilding.getEgid() != null) {
                building.setEgid(xmlBuilding.getEgid());
            }
            building.setArea(xmlBuilding.getArea());
            building.setPlanned(xmlBuilding.isPlanned());
            building.setUndergroundStructure(xmlBuilding.isUndergroundStructure());
            
            ArrayList<BuildingEntry> buildingEntries = new ArrayList<BuildingEntry>();
            List<BuildingEntryType> xmlBuildingEntries = xmlBuilding.getBuildingEntries();
            for (BuildingEntryType xmlBuildingEntry : xmlBuildingEntries) {
                BuildingEntry buildingEntry = new BuildingEntry();
                if (xmlBuildingEntry.getEdid() != null) {
                    buildingEntry.setEdid(xmlBuildingEntry.getEdid());
                }
                AddressType xmlAddress = xmlBuildingEntry.getPostalAddress();
                PostalAddress postalAddress = new PostalAddress();
                postalAddress.setStreet(xmlAddress.getStreet());
                postalAddress.setNumber(xmlAddress.getNumber());
                postalAddress.setPostalCode(xmlAddress.getPostalCode());
                postalAddress.setCity(xmlAddress.getCity());
                buildingEntry.setPostalAddress(postalAddress);
                buildingEntries.add(buildingEntry);
            }
            building.setBuildingEntries(buildingEntries);
            buildings.add(building);
        }
        realEstateDPR.setBuildings(buildings);
        return realEstateDPR;
    }
}
