package ch.so.agi.grundstuecksinformation.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Service;

import ch.ehi.oereb.schemas.oereb._1_0.extract.GetExtractByIdResponse;
import ch.ehi.oereb.schemas.oereb._1_0.extractdata.ExtractType;
import ch.ehi.oereb.schemas.oereb._1_0.extractdata.LanguageCodeType;
import ch.ehi.oereb.schemas.oereb._1_0.extractdata.LocalisedMTextType;
import ch.ehi.oereb.schemas.oereb._1_0.extractdata.LocalisedTextType;
import ch.ehi.oereb.schemas.oereb._1_0.extractdata.LocalisedUriType;
import ch.ehi.oereb.schemas.oereb._1_0.extractdata.MultilingualMTextType;
import ch.ehi.oereb.schemas.oereb._1_0.extractdata.MultilingualTextType;
import ch.ehi.oereb.schemas.oereb._1_0.extractdata.MultilingualUriType;
import ch.ehi.oereb.schemas.oereb._1_0.extractdata.RealEstateDPRType;
import ch.ehi.oereb.schemas.oereb._1_0.extractdata.RestrictionOnLandownershipType;
import ch.so.agi.grundstuecksinformation.shared.models.ConcernedTheme;
import ch.so.agi.grundstuecksinformation.shared.models.Egrid;
import ch.so.agi.grundstuecksinformation.shared.models.NotConcernedTheme;
import ch.so.agi.grundstuecksinformation.shared.models.Office;
import ch.so.agi.grundstuecksinformation.shared.models.RealEstateDPR;
import ch.so.agi.grundstuecksinformation.shared.models.ThemeWithoutData;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.transform.stream.StreamSource;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

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
        File xmlFile;
        xmlFile = Files.createTempFile("oereb_extract_", ".xml").toFile();
        URL url = new URL(egrid.getOerebServiceBaseUrl() + "extract/reduced/xml/geometry/" + egrid.getEgrid());
        logger.debug("extract url: " + url.toString());

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
        
        ArrayList<ThemeWithoutData> themesWithoutData = xmlExtract.getThemeWithoutData()
                .stream()
                .map(theme -> {
                    ThemeWithoutData themeWithoutData = new ThemeWithoutData();
                    themeWithoutData.setCode(theme.getCode());
                    themeWithoutData.setName(theme.getText().getText());
                    return themeWithoutData;
                })
                .collect(collectingAndThen(toList(), ArrayList<ThemeWithoutData>::new));
        // TODO: 'Generic' sorting of themes... including possible subthemes?!
        //themesWithoutData.sort(compare);
        
        logger.debug("Not concerned themes:");
        ArrayList<NotConcernedTheme> notConcernedThemes = xmlExtract.getNotConcernedTheme()
                .stream()
                .map(theme -> {
                    NotConcernedTheme notConcernedTheme = new NotConcernedTheme();
                    notConcernedTheme.setCode(theme.getCode());
                    notConcernedTheme.setName(theme.getText().getText());
                    
                    logger.debug("-------");
                    logger.debug(theme.getCode());
                    logger.debug(theme.getText().getText());
                    
                    return notConcernedTheme;
                })
                .collect(collectingAndThen(toList(), ArrayList<NotConcernedTheme>::new));
        // TODO: 'Generic' sorting of themes... including possible subthemes?!
        //notConcernedThemes.sort(compare);
        logger.debug("======================");
 
        
        
        // Create a map with all restrictions grouped by theme text.
        // Map mit gruppierten Restrictions (gruppiert nach Prosa-Text).
        Map<String, List<RestrictionOnLandownershipType>> groupedXmlRestrictions = xmlExtract.getRealEstate().getRestrictionOnLandownership()
                .stream()
                .collect(Collectors.groupingBy(r -> r.getTheme().getText().getText()));
        logger.debug("groupedXmlRestrictions: " + groupedXmlRestrictions.toString());
        
        // We create one ConcernedTheme object per theme with all restrictions belonging
        // to the same theme since this is the way we present the restriction in the GUI.
        logger.debug("Concerned themes:");
        ArrayList<ConcernedTheme> concernedThemesList = new ArrayList<ConcernedTheme>();
        for (Map.Entry<String, List<RestrictionOnLandownershipType>> entry : groupedXmlRestrictions.entrySet()) {
            logger.debug("---------------------------------------------");
            logger.debug("ConcernedTheme: " + entry.getKey());
            logger.debug("---------------------------------------------");

            List<RestrictionOnLandownershipType> xmlRestrictions = entry.getValue();

        }
        // TODO: 'Generic' sorting of themes... including possible subthemes?!        
        //concernedThemesList.sort(compare);        
        logger.debug("======================");
        
        
        
        RealEstateDPRType xmlRealEstateDPR = xmlExtract.getRealEstate();
        realEstateDPR.setEgrid(xmlRealEstateDPR.getEGRID());
        realEstateDPR.setFosnNr(xmlRealEstateDPR.getFosNr());
        realEstateDPR.setMunicipality(xmlRealEstateDPR.getMunicipality());
        realEstateDPR.setCanton(xmlRealEstateDPR.getCanton().value());
        realEstateDPR.setNumber(xmlRealEstateDPR.getNumber());
        realEstateDPR.setSubunitOfLandRegister(xmlRealEstateDPR.getSubunitOfLandRegister());
        realEstateDPR.setLandRegistryArea(xmlRealEstateDPR.getLandRegistryArea());
        
        try {
            realEstateDPR.setLimit(new Gml32ToJts().convertMultiSurface(xmlRealEstateDPR.getLimit()).toText()); 
        } catch (ParseException e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            realEstateDPR.setLimit(null);
        }
        
        realEstateDPR.setOerebThemesWithoutData(themesWithoutData);
        realEstateDPR.setOerebNotConcernedThemes(notConcernedThemes);        
        realEstateDPR.setRealEstateType(realEstateTypesMap.get(xmlRealEstateDPR.getType().value()));
        
        // TODO: which one is correct (according spec)?
        //realEstateDPR.setOerebPdfExtractUrl(egrid.getOerebServiceBaseUrl() + "extract/reduced/pdf/geometry/" + egrid.getEgrid());
        realEstateDPR.setOerebPdfExtractUrl(egrid.getOerebServiceBaseUrl() + "extract/reduced/pdf/" + egrid.getEgrid());
                
        Office oerebCadastreAuthority = new Office();
        oerebCadastreAuthority.setName(getLocalisedText(xmlExtract.getPLRCadastreAuthority().getName(), DE));
        oerebCadastreAuthority.setOfficeAtWeb(xmlExtract.getPLRCadastreAuthority().getOfficeAtWeb().getValue());
        oerebCadastreAuthority.setStreet(xmlExtract.getPLRCadastreAuthority().getStreet());
        oerebCadastreAuthority.setNumber(xmlExtract.getPLRCadastreAuthority().getNumber());
        oerebCadastreAuthority.setPostalCode(xmlExtract.getPLRCadastreAuthority().getPostalCode());
        oerebCadastreAuthority.setCity(xmlExtract.getPLRCadastreAuthority().getCity());
        
        realEstateDPR.setOerebCadastreAuthority(oerebCadastreAuthority);

               
        return realEstateDPR;
    }
    
    private String getLocalisedText(MultilingualTextType multilingualTextType, LanguageCodeType languageCodeType) {
        Iterator<LocalisedTextType> it = multilingualTextType.getLocalisedText().iterator();
        while(it.hasNext()) {
            LocalisedTextType textType = it.next();
            if (textType.getLanguage().compareTo(languageCodeType) == 0) {
                return textType.getText();
            }
        }
        return null;
    }
    
    private String getLocalisedText(MultilingualMTextType multilingualMTextType, LanguageCodeType languageCodeType) {
        Iterator<LocalisedMTextType> it = multilingualMTextType.getLocalisedText().iterator();
        while(it.hasNext()) {
            LocalisedMTextType textType = it.next();
            if (textType.getLanguage().compareTo(languageCodeType) == 0) {
                return textType.getText();
            }
        }
        return null;
    }    
    
    private String getLocalisedText(MultilingualUriType multilingualUriType, LanguageCodeType languageCodeType) {
        Iterator<LocalisedUriType> it = multilingualUriType.getLocalisedText().iterator();
        while(it.hasNext()) {
            LocalisedUriType textType = it.next();
            if (textType.getLanguage().compareTo(languageCodeType) == 0) {
                return textType.getText();
            }
        }
        return null;
    }    
}
