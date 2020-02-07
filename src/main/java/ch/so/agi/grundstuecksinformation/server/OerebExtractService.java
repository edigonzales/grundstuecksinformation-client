package ch.so.agi.grundstuecksinformation.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import ch.ehi.oereb.schemas.oereb._1_0.extract.GetExtractByIdResponse;
import ch.ehi.oereb.schemas.oereb._1_0.extractdata.DocumentBaseType;
import ch.ehi.oereb.schemas.oereb._1_0.extractdata.DocumentType;
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
import ch.so.agi.grundstuecksinformation.shared.models.Document;
import ch.so.agi.grundstuecksinformation.shared.models.Egrid;
import ch.so.agi.grundstuecksinformation.shared.models.NotConcernedTheme;
import ch.so.agi.grundstuecksinformation.shared.models.Office;
import ch.so.agi.grundstuecksinformation.shared.models.RealEstateDPR;
import ch.so.agi.grundstuecksinformation.shared.models.ReferenceWMS;
import ch.so.agi.grundstuecksinformation.shared.models.Restriction;
import ch.so.agi.grundstuecksinformation.shared.models.ThemeWithoutData;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.xerces.impl.dv.util.Base64;

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
        
        logger.debug("===========Not concerned themes===========");
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
        logger.debug("===========Not concerned themes===========");
 
        // Map mit gruppierten Restrictions (gruppiert nach Prosa-Text).
        Map<String, List<RestrictionOnLandownershipType>> groupedXmlRestrictions = xmlExtract.getRealEstate().getRestrictionOnLandownership()
                .stream()
                .collect(Collectors.groupingBy(r -> r.getTheme().getText().getText()));
        logger.debug("groupedXmlRestrictions: " + groupedXmlRestrictions.toString());
        
        // Es gibt ein ConcerncedTheme-Objekt pro Thema mit allen ÖREBs zu diesem Thema. 
        // Diese ConcernedThemes werden in einer Liste gespeichert. Dies entspricht
        // dem späteren Handling im GUI.
        logger.debug("===========Concerned themes===========");
        ArrayList<ConcernedTheme> concernedThemesList = new ArrayList<ConcernedTheme>();
        for (Map.Entry<String, List<RestrictionOnLandownershipType>> entry : groupedXmlRestrictions.entrySet()) {
            logger.debug("---------------------------------------------");
            logger.debug("ConcernedTheme: " + entry.getKey());

            List<RestrictionOnLandownershipType> xmlRestrictions = entry.getValue();
            logger.debug("Anzahl einzelne OEREB-Objekte im XML für dieses Thema: " + String.valueOf(xmlRestrictions.size()));

            // Es wird eine Map erzeugt mit einem vereinfachten Restriction-Objekt
            // resp. OEREB-Objekt pro Artcode.
            // 'groupingBy' kann nicht verwendet werden, weil das eine Liste pro Artcode
            // zurückliefert.
            // Später werden dem vereinfachten Restriction-Objekt mehr Infos hinzugefügt.
            // FIXME: Auch hier besteht das Problem, dass 'nur' über den
            // TypeCode gruppiert wird. Das reicht nicht immer.
            Map<String, Restriction> restrictionsMap = xmlRestrictions
                    .stream()
                    .filter(distinctByKey(RestrictionOnLandownershipType::getTypeCode))
                    .map(r -> {
                        Restriction restriction = new Restriction();
                        restriction.setInformation(getLocalisedText(r.getInformation(), DE));
                        restriction.setTypeCode(r.getTypeCode());
                        if (r.getSymbol() != null) {
                            String encodedImage = Base64.encode(r.getSymbol());
                            encodedImage = "data:image/png;base64," + encodedImage;
                            restriction.setSymbol(encodedImage);
                        } else if (r.getSymbolRef() != null) {
                            try {
                                String symbolUrl = URLDecoder.decode(r.getSymbolRef(), StandardCharsets.UTF_8.toString());
                                restriction.setSymbolRef(symbolUrl);
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }
                        return restriction;
                    })
                    .collect(Collectors.toMap(Restriction::getTypeCode, Function.identity()));
            logger.debug("Typcode/SimpleRestriction-Map: " + restrictionsMap.toString());

            // Die Summe der sogenannten Shares (Fläche(prozent)/Länge/Anzahl Punkte) pro
            // Typecode.
            Map<String, Integer> sumAreaShare = xmlRestrictions
                    .stream()
                    .filter(r -> r.getAreaShare() != null)
                    .collect(Collectors.groupingBy(r -> r.getTypeCode(), Collectors.summingInt(r -> r.getAreaShare())));

            Map<String, Integer> sumLengthShare = xmlRestrictions
                    .stream()
                    .filter(r -> r.getLengthShare() != null)
                    .collect(Collectors.groupingBy(r -> r.getTypeCode(), Collectors.summingInt(r -> r.getLengthShare())));

            Map<String, Integer> sumNrOfPoints = xmlRestrictions
                    .stream()
                    .filter(r -> r.getNrOfPoints() != null)
                    .collect(Collectors.groupingBy(r -> r.getTypeCode(), Collectors.summingInt(r -> r.getNrOfPoints())));

            Map<String, Double> sumAreaPercentShare = xmlRestrictions
                    .stream()
                    .filter(r -> r.getPartInPercent() != null)
                    .collect(Collectors.groupingBy(r -> r.getTypeCode(), Collectors.summingDouble(r -> r.getPartInPercent().doubleValue())));

            logger.debug("sumAreaShare: " + sumAreaShare.toString());
            logger.debug("sumLengthShare: " + sumLengthShare.toString());
            logger.debug("sumNrOfPoints: " + sumNrOfPoints.toString());
            logger.debug("sumAreaPercentShare: " + sumAreaPercentShare.toString());

            // Die vorher berechnete Summe wird dem jeweiligen vereinfachten
            // OEREB-Objekt zugewiesen. Dieses wird in einer Liste
            // von vereinfachten OEREB-Objekten eingefügt. Eine solche definitive Liste
            // gibt es pro ConcernedTheme.
            List<Restriction> restrictionsList = new ArrayList<Restriction>();
            for (Map.Entry<String, Restriction> restrictionEntry : restrictionsMap.entrySet()) {
                String typeCode = restrictionEntry.getKey();
                if (sumAreaShare.get(typeCode) != null) {
                    restrictionEntry.getValue().setAreaShare(sumAreaShare.get(typeCode));
                }
                if (sumLengthShare.get(typeCode) != null) {
                    restrictionEntry.getValue().setLengthShare(sumLengthShare.get(typeCode));
                }
                if (sumNrOfPoints.get(typeCode) != null) {
                    restrictionEntry.getValue().setNrOfPoints(sumNrOfPoints.get(typeCode));
                }
                if (sumAreaPercentShare.get(typeCode) != null) {
                    restrictionEntry.getValue().setPartInPercent(sumAreaPercentShare.get(typeCode));
                }
                restrictionsList.add(restrictionEntry.getValue());
            }
            logger.debug("restrictionsList: " + restrictionsList);
            
            // Collect responsible offices in a office list.
            // Distinct by office url.
            ArrayList<Office> officeList = (ArrayList<Office>) xmlRestrictions.stream()
                    .filter(distinctByKey(r -> {
                        String officeName = r.getResponsibleOffice().getOfficeAtWeb().getValue();
                        return officeName;
                    }))
                    .map(r -> {
                        Office office = new Office();
                        if (r.getResponsibleOffice().getName() != null) {
                            office.setName(getLocalisedText(r.getResponsibleOffice().getName(), DE));
                        }      
                        try {
                            office.setOfficeAtWeb(URLDecoder.decode(r.getResponsibleOffice().getOfficeAtWeb().getValue(), StandardCharsets.UTF_8.toString()));
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                            office.setOfficeAtWeb(r.getResponsibleOffice().getOfficeAtWeb().getValue());
                        }
                        return office;
                    })
                    .collect(Collectors.toList());

            logger.debug("Size of office: " + officeList.size());

            // Get legal provisions and laws. Put them in Lists. 
            // Die Gesetze stammen nicht aus der gleichen Hierarchie wie
            // die Rechtsgrundlagen. Sondern sind in den Rechtsgrundlagen
            // verschachtelt. Man schaue sich ein korrektes XML an.
            // Aus diesem Grund können Gesetze vielfach vorkommen und
            // müssen anschliessend distincted werden. Das gilt natürlich
            // aber auch für die Rechtsgrundlagen.
            List<Document> legalProvisionsList = new ArrayList<Document>();
            List<Document> lawsList = new ArrayList<Document>();

            for (RestrictionOnLandownershipType xmlRestriction : xmlRestrictions) {
                List<DocumentBaseType> xmlLegalProvisions = xmlRestriction.getLegalProvisions();
                for (DocumentBaseType xmlDocumentBase : xmlLegalProvisions) {
                    DocumentType xmlLegalProvision = (DocumentType) xmlDocumentBase;
                    Document legalProvision = new Document();
                    if (xmlLegalProvision.getTitle() != null) {
                        legalProvision.setTitle(getLocalisedText(xmlLegalProvision.getTitle(), DE));
                    }
                    if (xmlLegalProvision.getOfficialTitle() != null) {
                        legalProvision.setOfficialTitle(getLocalisedText(xmlLegalProvision.getOfficialTitle(), DE));
                    }
                    legalProvision.setOfficialNumber(xmlLegalProvision.getOfficialNumber());
                    if (xmlLegalProvision.getAbbreviation() != null) {
                        legalProvision.setAbbreviation(getLocalisedText(xmlLegalProvision.getAbbreviation(), DE));
                    }
                    if (xmlLegalProvision.getTextAtWeb() != null) { 
                       try {
                           legalProvision.setTextAtWeb(URLDecoder.decode(getLocalisedText(xmlLegalProvision.getTextAtWeb(), DE), StandardCharsets.UTF_8.toString()));
                       } catch (UnsupportedEncodingException e) {
                           legalProvision.setTextAtWeb(getLocalisedText(xmlLegalProvision.getTextAtWeb(), DE));
                       }
                    }
                    legalProvisionsList.add(legalProvision);

                    List<DocumentType> xmlLaws = xmlLegalProvision.getReference();
                    for (DocumentType xmlLaw : xmlLaws) {
                        Document law = new Document();
                        if (xmlLaw.getTitle() != null) {
                            law.setTitle(getLocalisedText(xmlLaw.getTitle(), DE));
                        }
                        if (xmlLaw.getOfficialTitle() != null) {
                            law.setOfficialTitle(getLocalisedText(xmlLaw.getOfficialTitle(), DE));
                        }
                        law.setOfficialNumber(xmlLaw.getOfficialNumber());
                        if (xmlLaw.getAbbreviation() != null) {
                            law.setAbbreviation(getLocalisedText(xmlLaw.getAbbreviation(), DE));
                        }
                        if (xmlLaw.getTextAtWeb() != null) {
                            try {
                                law.setTextAtWeb(URLDecoder.decode(getLocalisedText(xmlLaw.getTextAtWeb(), DE), StandardCharsets.UTF_8.toString()));
                            } catch (UnsupportedEncodingException e) {
                                law.setTextAtWeb(getLocalisedText(xmlLaw.getTextAtWeb(), DE));
                            }
                        }
                        lawsList.add(law);
                    }
                }
            }

            // Because restrictions can share the same legal provision and laws,
            // we need to distinct them.
            List<Document> distinctLegalProvisionsList = legalProvisionsList.stream()
                    .filter(distinctByKey(Document::getTextAtWeb)).collect(Collectors.toList());

            List<Document> distinctLawsList = lawsList.stream().filter(distinctByKey(Document::getTextAtWeb))
                    .collect(Collectors.toList());
            logger.debug("distinct legal provisions: " + distinctLegalProvisionsList.toString());
            logger.debug("distinct laws: " + distinctLawsList.toString());
            
            // WMS: Muss auseinandergenommen werden, damit man im GUI mit OL3 arbeiten kann.
            double layerOpacity = xmlRestrictions.get(0).getMap().getLayerOpacity();
            int layerIndex = xmlRestrictions.get(0).getMap().getLayerIndex();
            String wmsUrl = xmlRestrictions.get(0).getMap().getReferenceWMS();

            UriComponents uriComponents = UriComponentsBuilder.fromUriString(URLDecoder.decode(wmsUrl, StandardCharsets.UTF_8.toString())).build();            
            String schema = uriComponents.getScheme();
            String host = uriComponents.getHost();
            String path = uriComponents.getPath();

            String layers = null;
            String imageFormat = null;
            Iterator<Map.Entry<String, List<String>>> iterator = uriComponents.getQueryParams().entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, List<String>> e = iterator.next();
                if (e.getKey().equalsIgnoreCase("layers")) {
                    layers = e.getValue().get(0);
                }
                if (e.getKey().equalsIgnoreCase("format")) {
                    imageFormat = e.getValue().get(0);
                }
            }
            
            StringBuilder baseUrlBuilder = new StringBuilder();
            baseUrlBuilder.append(schema).append("://").append(host);
            if (uriComponents.getPort() != -1) {
                baseUrlBuilder.append(":" + String.valueOf(uriComponents.getPort()));
            }
            baseUrlBuilder.append(path);
            String baseUrl = baseUrlBuilder.toString();

            ReferenceWMS referenceWMS = new ReferenceWMS();
            referenceWMS.setBaseUrl(baseUrl);
            referenceWMS.setLayers(layers);
            referenceWMS.setImageFormat(imageFormat);
            referenceWMS.setLayerOpacity(layerOpacity);
            referenceWMS.setLayerIndex(layerIndex);
            logger.debug("referenceWMS: " + referenceWMS.toString()); 
            
            // Bundesthemen haben, Stand heute, keine LegendeImWeb
            String legendAtWeb = null;
            if (xmlRestrictions.get(0).getMap().getLegendAtWeb() != null) {
                legendAtWeb = URLDecoder.decode(xmlRestrictions.get(0).getMap().getLegendAtWeb().getValue(), StandardCharsets.UTF_8.toString());
            }
            
            // Finally we create the concerned theme with all information.
            ConcernedTheme concernedTheme = new ConcernedTheme();
            concernedTheme.setRestrictions(restrictionsList);
            concernedTheme.setLegalProvisions(distinctLegalProvisionsList);
            concernedTheme.setLaws(distinctLawsList);
            concernedTheme.setReferenceWMS(referenceWMS);
            concernedTheme.setLegendAtWeb(legendAtWeb);
            concernedTheme.setCode(xmlRestrictions.get(0).getTheme().getCode());
            concernedTheme.setName(xmlRestrictions.get(0).getTheme().getText().getText());
            concernedTheme.setSubtheme(xmlRestrictions.get(0).getSubTheme());
            concernedTheme.setResponsibleOffice(officeList);

            concernedThemesList.add(concernedTheme);

            logger.debug("---------------------------------------------");
        }
        // TODO: 'Generic' sorting of themes... including possible subthemes?!        
        //concernedThemesList.sort(compare);        
        logger.debug("===========Concerned themes===========");
        
        
        
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
        realEstateDPR.setOerebConcernedThemes(concernedThemesList);
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
    
    private static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
        Map<Object, Boolean> map = new ConcurrentHashMap<>();
        return t -> map.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
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
