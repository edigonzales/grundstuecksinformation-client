package ch.so.agi.grundstuecksinformation.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.FontStyle;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

import ch.qos.logback.classic.Logger;
import ch.so.agi.grundstuecksinformation.shared.EgridResponse;
import ch.so.agi.grundstuecksinformation.shared.EgridService;
import ch.so.agi.grundstuecksinformation.shared.EgridServiceAsync;
import ch.so.agi.grundstuecksinformation.shared.ExtractResponse;
import ch.so.agi.grundstuecksinformation.shared.ExtractService;
import ch.so.agi.grundstuecksinformation.shared.ExtractServiceAsync;
import ch.so.agi.grundstuecksinformation.shared.SettingsResponse;
import ch.so.agi.grundstuecksinformation.shared.SettingsService;
import ch.so.agi.grundstuecksinformation.shared.SettingsServiceAsync;
import ch.so.agi.grundstuecksinformation.shared.models.Egrid;
import ch.so.agi.grundstuecksinformation.shared.models.NotConcernedTheme;
import ch.so.agi.grundstuecksinformation.shared.models.RealEstateDPR;
import ch.so.agi.grundstuecksinformation.shared.models.ThemeWithoutData;
import elemental2.core.Global;
import elemental2.dom.CSSProperties;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import ol.Coordinate;
import ol.Extent;
import ol.Feature;
import ol.FeatureOptions;
import ol.Map;
import ol.MapBrowserEvent;
import ol.OLFactory;
import ol.View;
import ol.event.EventListener;
import ol.format.GeoJson;
import ol.format.Wkt;
import ol.geom.Geometry;
import ol.layer.Base;
import ol.layer.Image;
import ol.layer.VectorLayerOptions;
import ol.source.Vector;
import ol.source.VectorOptions;
import ol.style.Stroke;
import ol.style.Style;

import static elemental2.dom.DomGlobal.fetch;
import static elemental2.dom.DomGlobal.console;
import elemental2.dom.Response;
import gwt.material.design.addins.client.window.MaterialWindow;
import gwt.material.design.client.constants.ButtonType;
import gwt.material.design.client.constants.Color;
import gwt.material.design.client.constants.IconType;
import gwt.material.design.client.constants.Position;
import gwt.material.design.client.constants.WavesType;
import gwt.material.design.client.ui.MaterialButton;
import gwt.material.design.client.ui.MaterialCard;
import gwt.material.design.client.ui.MaterialCardContent;
import gwt.material.design.client.ui.MaterialChip;
import gwt.material.design.client.ui.MaterialCollapsible;
import gwt.material.design.client.ui.MaterialCollapsibleBody;
import gwt.material.design.client.ui.MaterialCollapsibleHeader;
import gwt.material.design.client.ui.MaterialCollapsibleItem;
import gwt.material.design.client.ui.MaterialCollection;
import gwt.material.design.client.ui.MaterialCollectionItem;
import gwt.material.design.client.ui.MaterialColumn;
import gwt.material.design.client.ui.MaterialIcon;
import gwt.material.design.client.ui.MaterialLabel;
import gwt.material.design.client.ui.MaterialLink;
import gwt.material.design.client.ui.MaterialLoader;
import gwt.material.design.client.ui.MaterialPanel;
import gwt.material.design.client.ui.MaterialRow;
import gwt.material.design.client.ui.MaterialTab;
import gwt.material.design.client.ui.MaterialTabItem;
import gwt.material.design.client.ui.MaterialToast;
import gwt.material.design.client.ui.html.Div;

public class AppEntryPoint implements EntryPoint {
    private MainMessages messages = GWT.create(MainMessages.class);
    private final SettingsServiceAsync settingsService = GWT.create(SettingsService.class);
    private final EgridServiceAsync egridService = GWT.create(EgridService.class);
    private final ExtractServiceAsync extractService = GWT.create(ExtractService.class);
    
    // Settings
    private String MY_VAR;
    
    private String SUB_HEADER_FONT_SIZE = "16px";
    private String BODY_FONT_SIZE = "14px";
    private String SMALL_FONT_SIZE = "12px";

    private String RESULT_CARD_HEIGHT = "calc(100% - 215px)";

    private String ID_ATTR_NAME = "id";
    private String REAL_ESTATE_VECTOR_LAYER_ID = "real_estate_vector_layer";
    private String REAL_ESTATE_VECTOR_FEATURE_ID = "real_estate_fid";

    private NumberFormat fmtDefault = NumberFormat.getDecimalFormat();
    private NumberFormat fmtPercent = NumberFormat.getFormat("#0.0");
  
    private Map map;
    private MaterialCard searchCard;  
    private MaterialCard resultCard;    
    private MaterialCardContent searchCardContent;   
    private MaterialCardContent resultCardContent;    
    private MaterialWindow realEstateWindow;
    private MaterialRow resultHeaderRow;
    private MaterialTab resultTab;
    private Div resultDiv;
    private MaterialColumn cadastralSurveyingResultColumn;
    private MaterialColumn oerebResultColumn;
    private String expandedOerebLayerId;
    private MaterialCollapsible plrCollapsibleConcernedTheme;
    private MaterialCollapsible plrInnerCollapsibleConcernedTheme;
    private MaterialCollapsible oerebCollapsibleNotConcernedTheme;
    private MaterialCollapsible oerebCollapsibleThemesWithoutData;
    private MaterialCollapsible oerebCollapsibleGeneralInformation;

    private String identifyRequestTemplate = "https://api3.geo.admin.ch/rest/services/all/MapServer/identify?geometry=%s,%s&geometryFormat=geojson&geometryType=esriGeometryPoint&imageDisplay=1780,772,96&lang=de&layers=all:ch.kantone.cadastralwebmap-farbe&limit=10&mapExtent=%s,%s,%s,%s&returnGeometry=true&sr=2056&tolerance=10";

    private ArrayList<String> oerebWmsLayers = new ArrayList<String>();
    
    public void onModuleLoad() {
        settingsService.settingsServer(new AsyncCallback<SettingsResponse>() {
            @Override
            public void onFailure(Throwable caught) {
                GWT.log("error: " + caught.getMessage());
            }

            @Override
            public void onSuccess(SettingsResponse result) {
                MY_VAR = (String) result.getSettings().get("MY_VAR");
                init();
            }
        });
    }

    private void init() {                        
        GWT.log("fubar");
        
        // Add the allmighty map.
        Div mapDiv = new Div();
        mapDiv.setId("map");

        RootPanel.get().add(mapDiv);
        
        map = MapPresets.getCadastralSurveyingWms(mapDiv.getId());
        map.addSingleClickListener(new MapSingleClickListener());
        
        // Search card on in the top left corner.
        searchCard = new MaterialCard();
        searchCard.setId("searchCard");

        searchCardContent = new MaterialCardContent();
        searchCardContent.setId("searchCardContent");
     
        MaterialRow logoRow = new MaterialRow();

        com.google.gwt.user.client.ui.Image plrImage = new com.google.gwt.user.client.ui.Image();
        plrImage.setUrl(GWT.getHostPageBaseURL() + "logo-grundstuecksinformation.png");
        plrImage.setWidth("65%");

        MaterialColumn logoColumn = new MaterialColumn();
        logoColumn.setId("logoColumn");
        logoColumn.setGrid("s12");
        logoColumn.add(plrImage);

        logoRow.add(logoColumn);
        searchCardContent.add(logoRow);
        
        MaterialRow searchRow = new MaterialRow();
        searchRow.setId("searchRow");

        searchCardContent.add(searchRow);
        searchCardContent.add(new Label("Search will be placed here."));
        searchCard.add(searchCardContent);
        
        RootPanel.get().add(searchCard);
        
        // Card that shows the results from the extracts.
        resultCard = new MaterialCard();
        resultCard.setId("resultCard");

        resultCardContent = new MaterialCardContent();
        resultCardContent.setId("resultCardContent");
        resultCard.add(resultCardContent);
        
        Div fadeoutBottomDiv = new Div();
        fadeoutBottomDiv.setId("fadeoutBottomDiv");
        resultCard.add(fadeoutBottomDiv);
        
        RootPanel.get().add(resultCard);
    }
    
    private void resetGui() {
        removeOerebWmsLayers();

        if (resultDiv != null) {
            resultCardContent.remove(resultDiv);
        }

        if (realEstateWindow != null) {
            realEstateWindow.removeFromParent();
        }

        if (resultHeaderRow != null) {
            resultHeaderRow.removeFromParent();
        }

        resultCard.getElement().getStyle().setProperty("visibility", "hidden");
    }
    
    private void sendCoordinateToServer(String XY, MapBrowserEvent event) {
        egridService.egridServer(XY, new AsyncCallback<EgridResponse>() {
            @Override
            public void onFailure(Throwable caught) {
                MaterialLoader.loading(false);
                MaterialToast.fireToast("An error occured.");
                
                // TODO: Make logging production ready.
                console.log("error: " + caught.getMessage());
            }

            @Override
            public void onSuccess(EgridResponse result) {
                GWT.log("SUCCESS!!!!"); 
                resetGui();
                
                GWT.log(String.valueOf(result.getResponseCode()));
                
                if (result.getResponseCode() != 200) {
                    MaterialLoader.loading(false);

                    MaterialToast.fireToast("E-GRID not found.");
                    return;
                }

                String egrid;
                List<Egrid> egridList = result.getEgrid();
                if (egridList.size() > 1) {
                    MaterialLoader.loading(false);

                    realEstateWindow = new MaterialWindow();
                    realEstateWindow.setTitle(messages.realEstatePlural());
                    realEstateWindow.setFontSize("16px");
                    realEstateWindow.setMarginLeft(0);
                    realEstateWindow.setMarginRight(0);
                    realEstateWindow.setWidth("300px");
                    realEstateWindow.setToolbarColor(Color.RED_LIGHTEN_1);

                    MaterialIcon maximizeIcon = realEstateWindow.getIconMaximize();
                    maximizeIcon.getElement().getStyle().setProperty("visibility", "hidden");

                    realEstateWindow.setMaximize(false);
                    realEstateWindow.setTop(event.getPixel().getY());
                    realEstateWindow.setLeft(event.getPixel().getX());
  
                    MaterialPanel realEstatePanel = new MaterialPanel();

                    for (Egrid egridObj : egridList) {
                        egrid = (String) egridObj.getEgrid(); 
                        String number = egridObj.getNumber();

                        MaterialRow row = new MaterialRow();
                        row.setId(egrid);
                        row.setMarginBottom(0);
                        row.setPadding(5);
                        row.add(new Label(messages.realEstateAbbreviation() + ": " + number + " (unknown...)"));

                        row.addClickHandler(event -> {
                            realEstateWindow.removeFromParent();
                            GWT.log("Get extract from a map click (multiple click result): " + row.getId());
//                            Egrid  = new Egrid();

//                            MaterialLoader.loading(true);
//                            sendEgridToServer(row.getId());
                        });

                        row.addMouseOverHandler(event -> {
                            row.setBackgroundColor(Color.GREY_LIGHTEN_3);
                            row.getElement().getStyle().setCursor(Cursor.POINTER);
//                            ol.layer.Vector vlayer = createRealEstateVectorLayer(feature.getGeometry());
//                            map.addLayer(vlayer);
                        });

                        row.addMouseOutHandler(event -> {
                            row.setBackgroundColor(Color.WHITE);
                            row.getElement().getStyle().setCursor(Cursor.DEFAULT);
//                            removeRealEstateVectorLayer();
                        });
                        
                        realEstatePanel.add(row);
                    }
                    
                    realEstateWindow.add(realEstatePanel);
                    realEstateWindow.open();
                } else {
                    GWT.log("Get extract from a map click (single click result): " + egridList.get(0).getEgrid());
                    
                    MaterialLoader.loading(true);
                    sendEgridToServer(egridList.get(0));
                }               
            }
        });
    }
    
    private void sendEgridToServer(Egrid egrid) {
        extractService.extractServer(egrid, new AsyncCallback<ExtractResponse>() {

            @Override
            public void onFailure(Throwable caught) {
                MaterialLoader.loading(false);
                MaterialToast.fireToast("An error occured.");
                
                // TODO: Make logging production ready.
                console.log("error: " + caught.getMessage());
            }

            @Override
            public void onSuccess(ExtractResponse result) {
                MaterialLoader.loading(false);
                GWT.log("foo bar");
                
                String newUrl = Window.Location.getProtocol() + "//" + Window.Location.getHost() + Window.Location.getPath() + "?egrid=" + egrid.getEgrid();
                updateURLWithoutReloading(newUrl);
                
                removeOerebWmsLayers();
                
                RealEstateDPR realEstateDPR = result.getRealEstateDPR();
                String number = realEstateDPR.getNumber();
                String municipality = realEstateDPR.getMunicipality();
                String subunitOfLandRegister = realEstateDPR.getSubunitOfLandRegister();
                String canton = realEstateDPR.getCanton();
                String egrid = realEstateDPR.getEgrid();
                int area = realEstateDPR.getLandRegistryArea();
                String realEstateType = realEstateDPR.getRealEstateType();
                
                ol.layer.Vector vlayer = createRealEstateVectorLayer(realEstateDPR.getLimit());
                
                if (realEstateDPR.getLimit() != null) {
                    Geometry geometry = new Wkt().readGeometry(realEstateDPR.getLimit());
                    Extent extent = geometry.getExtent();
                    
                    View view = map.getView();
                    double resolution = view.getResolutionForExtent(extent);
                    view.setZoom(Math.floor(view.getZoomForResolution(resolution)) - 1);

                    double x = extent.getLowerLeftX() + extent.getWidth() / 2;
                    double y = extent.getLowerLeftY() + extent.getHeight() / 2;

                    // Das ist jetzt ziemlich heuristisch...
                    // 500 = Breite des Suchresultates
                    view.setCenter(new Coordinate(x - (resultCard.getWidth() * view.getResolution()) / 2, y));                    
                } 

                vlayer.setZIndex(1001);
                map.addLayer(vlayer);
                
                // Add the extract results to the card.
                resultDiv = new Div();
                resultDiv.setId("resultDiv");
                resultDiv.setBackgroundColor(Color.GREY_LIGHTEN_5);

                resultHeaderRow = new MaterialRow();
                resultHeaderRow.setId("resultHeaderRow");

                MaterialColumn resultParcelColumn = new MaterialColumn();
                resultParcelColumn.setId("resultParcelColumn");
                resultParcelColumn.setGrid("s8");

                String lblString = messages.resultHeader(number);
                Label lbl = new Label(lblString);
                resultParcelColumn.add(lbl);
                resultHeaderRow.add(resultParcelColumn);

                MaterialColumn resultButtonColumn = new MaterialColumn();
                resultButtonColumn.setId("resultButtonColumn");
                resultButtonColumn.setGrid("s4");

                MaterialButton deleteExtractButton = new MaterialButton();
                deleteExtractButton.setId("deleteExtractButton");
                deleteExtractButton.setIconType(IconType.CLOSE);
                deleteExtractButton.setType(ButtonType.FLOATING);
                deleteExtractButton.setTooltip(messages.resultCloseTooltip());
                deleteExtractButton.setTooltipPosition(Position.TOP);
                deleteExtractButton.addClickHandler(event -> {
                    resetGui();
                });
                resultButtonColumn.add(deleteExtractButton);

                MaterialButton minMaxExtractButton = new MaterialButton();
                minMaxExtractButton.setId("minmaxExtractButton");
                minMaxExtractButton.setMarginLeft(10);
                minMaxExtractButton.setIconType(IconType.REMOVE);
                minMaxExtractButton.setType(ButtonType.FLOATING);
                minMaxExtractButton.setTooltip(messages.resultMinimizeTooltip());
                minMaxExtractButton.setTooltipPosition(Position.TOP);

                minMaxExtractButton.addClickHandler(event -> {
                    if (resultCard.getOffsetHeight() > resultHeaderRow.getOffsetHeight()) {
                        minMaxExtractButton.setIconType(IconType.ADD);
                        minMaxExtractButton.setTooltip(messages.resultMaximizeTooltip());

                        resultCard.getElement().getStyle().setProperty("overflowY", "hidden");
                        resultCard.setHeight(String.valueOf(resultHeaderRow.getOffsetHeight()) + "px");
                        resultDiv.setVisibility(com.google.gwt.dom.client.Style.Visibility.HIDDEN);
                    } else {
                        minMaxExtractButton.setIconType(IconType.REMOVE);
                        minMaxExtractButton.setTooltip(messages.resultMinimizeTooltip());

                        resultDiv.setVisibility(com.google.gwt.dom.client.Style.Visibility.VISIBLE);
                        resultCard.getElement().getStyle().setProperty("overflowY", "auto");
                        resultCard.getElement().getStyle().setProperty("height", RESULT_CARD_HEIGHT);
                    }
                });
                resultButtonColumn.add(minMaxExtractButton);

                resultHeaderRow.add(resultButtonColumn);
                resultCardContent.add(resultHeaderRow);  
                
                MaterialRow tabRow = new MaterialRow();
                tabRow.setId("tabRow");

                MaterialColumn tabHeaderColumn = new MaterialColumn();
                tabHeaderColumn.setId("tabHeaderColumn");
                tabHeaderColumn.setGrid("s12");

                resultTab = new MaterialTab();
                resultTab.setId("resultTab");
                resultTab.setShadow(1);
                // #e8c432
                // #aed634
                // #52b1a7
                // #75a5d4
                resultTab.setBackgroundColor(Color.RED_LIGHTEN_1);
                resultTab.setIndicatorColor(Color.WHITE);

                // Cadastral Surveying Tab
                MaterialTabItem cadastralSurveyingHeaderTabItem = new MaterialTabItem();
                cadastralSurveyingHeaderTabItem.setWaves(WavesType.LIGHT);
                cadastralSurveyingHeaderTabItem.setGrid("s4");

                MaterialLink cadastralSurveyingHeaderTabLink = new MaterialLink();
                cadastralSurveyingHeaderTabLink.setText(messages.tabTitleCadastralSurveying());
                cadastralSurveyingHeaderTabLink.setHref("#cadastralSurveyingResultColumn");
                cadastralSurveyingHeaderTabLink.setTextColor(Color.WHITE);
                cadastralSurveyingHeaderTabItem.add(cadastralSurveyingHeaderTabLink);
                resultTab.add(cadastralSurveyingHeaderTabItem);

                // Land Register Tab
                MaterialTabItem grundbuchHeaderTabItem = new MaterialTabItem();
                grundbuchHeaderTabItem.setWaves(WavesType.LIGHT);
                grundbuchHeaderTabItem.setGrid("s4");
                grundbuchHeaderTabItem.setEnabled(false);

                MaterialLink grundbuchHeaderTabLink = new MaterialLink();
                grundbuchHeaderTabLink.setText(messages.tabTitleLandRegister());
                grundbuchHeaderTabLink.setHref("#tab2");
                grundbuchHeaderTabLink.setTextColor(Color.WHITE);
                grundbuchHeaderTabItem.add(grundbuchHeaderTabLink);
                resultTab.add(grundbuchHeaderTabItem);

                // OEREB Tab
                MaterialTabItem oerebHeaderTabItem = new MaterialTabItem();
                oerebHeaderTabItem.setWaves(WavesType.LIGHT);
                oerebHeaderTabItem.setGrid("s4");

                MaterialLink oerebHeaderTabLink = new MaterialLink();
                oerebHeaderTabLink.setText(messages.tabTitlePlr());
                oerebHeaderTabLink.setHref("#oerebResultColumn");
                oerebHeaderTabLink.setTextColor(Color.WHITE);
                oerebHeaderTabItem.add(oerebHeaderTabLink);
                resultTab.add(oerebHeaderTabItem);

                tabHeaderColumn.add(resultTab);
                tabRow.add(tabHeaderColumn);

                // Add specific cadastre content.
                cadastralSurveyingResultColumn = new MaterialColumn();
                cadastralSurveyingResultColumn.setId("cadastralSurveyingResultColumn");
                cadastralSurveyingResultColumn.addStyleName("resultColumn");
                cadastralSurveyingResultColumn.setGrid("s12");
                addCadastralSurveyingContent(realEstateDPR);
                tabRow.add(cadastralSurveyingResultColumn);

                // Add specific oereb content.
                oerebResultColumn = new MaterialColumn();
                oerebResultColumn.setId("oerebResultColumn");
                oerebResultColumn.addStyleName("resultColumn");
                oerebResultColumn.setGrid("s12");
                addOerebContent(realEstateDPR);
                tabRow.add(oerebResultColumn);

                resultTab.addSelectionHandler(event -> {
                    // 2 == OEREB
                    if (event.getSelectedItem() == 2) {
                        for (String layerId : oerebWmsLayers) {
                            Image wmsLayer = (Image) getMapLayerById(layerId);
                            if (layerId.equalsIgnoreCase(expandedOerebLayerId)) {
                                wmsLayer.setVisible(true);
                            } else {
                                wmsLayer.setVisible(false);
                            }
                        }
                    } else {
                        for (String layerId : oerebWmsLayers) {
                            Image wmsLayer = (Image) getMapLayerById(layerId);
                            wmsLayer.setVisible(false);
                        }
                    }
                });

                resultDiv.add(tabRow);

                resultCardContent.add(resultDiv);
                resultCard.getElement().getStyle().setProperty("height", RESULT_CARD_HEIGHT);
                resultCard.getElement().getStyle().setProperty("overflowY", "auto");
                resultCard.getElement().getStyle().setProperty("visibility", "visible");
            }
        });
    }
    
    private void addOerebContent(RealEstateDPR realEstateDPR) {
        {
            MaterialRow pdfRow = new MaterialRow();
            pdfRow.setId("oerebPdfRow");

            MaterialColumn pdfButtonColumn = new MaterialColumn();
            pdfButtonColumn.setId("plrPdfButtonColumn");
            pdfButtonColumn.setGrid("s4");
            MaterialButton pdfButton = new MaterialButton();
            pdfButton.setType(ButtonType.OUTLINED);
            pdfButton.setBackgroundColor(Color.WHITE);
            pdfButton.setBorder("1px solid #f44336");
            pdfButton.setTextColor(Color.RED_LIGHTEN_1);
            pdfButton.setText("PDF");
            // pdfButton.setIconType(IconType.INSERT_DRIVE_FILE);
            pdfButton.setWidth("100%");
            pdfButton.setTooltip(messages.resultPDFTooltip());
            pdfButtonColumn.add(pdfButton);

            pdfRow.add(pdfButtonColumn);
            oerebResultColumn.add(pdfRow);
            
            GWT.log(realEstateDPR.getOerebPdfExtractUrl());

            pdfButton.addClickHandler(event -> {
                Window.open(realEstateDPR.getOerebPdfExtractUrl(), "_blank", null);
            });
        }

        Div plrCollapsibleDiv = new Div();

//        {
//            plrCollapsibleConcernedTheme = new MaterialCollapsible();
//            plrCollapsibleConcernedTheme.addStyleName("plrTopLevelCollapsible");
//            plrCollapsibleConcernedTheme.setShadow(0);
//
//            plrCollapsibleConcernedTheme.addExpandHandler(event -> {
//                plrCollapsibleNotConcernedTheme.closeAll();
//                plrCollapsibleThemesWithoutData.closeAll();
//                plrCollapsibleGeneralInformation.closeAll();
//            });
//
//            MaterialCollapsibleItem collapsibleConcernedThemeItem = new MaterialCollapsibleItem();
//
//            MaterialCollapsibleHeader collapsibleConcernedThemeHeader = new MaterialCollapsibleHeader();
//            collapsibleConcernedThemeHeader.addStyleName("plrCollapsibleThemeHeader");
//
//            MaterialRow collapsibleConcernedThemeHeaderRow = new MaterialRow();
//            collapsibleConcernedThemeHeaderRow.addStyleName("collapsibleThemeHeaderRow");
//
//            MaterialColumn collapsibleConcernedThemeColumnLeft = new MaterialColumn();
//            collapsibleConcernedThemeColumnLeft.addStyleName("collapsibleThemeColumnLeft");
//            collapsibleConcernedThemeColumnLeft.setGrid("s10");
//            MaterialColumn collapsibleConcernedThemeColumnRight = new MaterialColumn();
//            collapsibleConcernedThemeColumnRight.addStyleName("collapsibleThemeColumnRight");
//            collapsibleConcernedThemeColumnRight.setGrid("s2");
//
//            MaterialLink collapsibleThemesHeaderLink = new MaterialLink();
//            collapsibleThemesHeaderLink.addStyleName("collapsibleThemesHeaderLink");
//            collapsibleThemesHeaderLink.setText(messages.concernedThemes());
//            collapsibleConcernedThemeColumnLeft.add(collapsibleThemesHeaderLink);
//
//            MaterialChip collapsibleThemesHeaderChip = new MaterialChip();
//            collapsibleThemesHeaderChip.addStyleName("collapsibleThemesHeaderChip");
//            collapsibleThemesHeaderChip.setText(String.valueOf(extract.getRealEstate().getConcernedThemes().size()));
//            collapsibleConcernedThemeColumnRight.add(collapsibleThemesHeaderChip);
//
//            collapsibleConcernedThemeHeaderRow.add(collapsibleConcernedThemeColumnLeft);
//            collapsibleConcernedThemeHeaderRow.add(collapsibleConcernedThemeColumnRight);
//
//            collapsibleConcernedThemeHeader.add(collapsibleConcernedThemeHeaderRow);
//
//            MaterialCollapsibleBody collapsibleConcernedThemeBody = new MaterialCollapsibleBody();
//            if (extract.getRealEstate().getConcernedThemes().size() > 0) {
//                collapsibleConcernedThemeBody.setPadding(0);
//
//                plrInnerCollapsibleConcernedTheme = new MaterialCollapsible();
//                plrInnerCollapsibleConcernedTheme.addStyleName("concernedThemeCollapsible");
//                plrInnerCollapsibleConcernedTheme.setAccordion(true);
//                int i = 0;
//
//                for (ConcernedTheme theme : extract.getRealEstate().getConcernedThemes()) {
//                    i++;
//
//                    plrInnerCollapsibleConcernedTheme.setShadow(0);
//
//                    Image wmsLayer = createPlrWmsLayer(theme.getReferenceWMS());
//                    map.addLayer(wmsLayer);
//
//                    MaterialCollapsibleItem item = new MaterialCollapsibleItem();
//
//                    // Cannot use the code since all subthemes share
//                    // the same code.
//                    String layerId = theme.getReferenceWMS().getLayers();
//                    item.setId(layerId);
//                    concernedWmsLayers.add(layerId);
//
//                    MaterialCollapsibleHeader header = new MaterialCollapsibleHeader();
//                    header.addStyleName("collapsibleThemeLayerHeader");
//                    if (i < extract.getRealEstate().getConcernedThemes().size()) {
//                        header.setBorderBottom("1px solid #dddddd");
//                    } else {
//                        header.setBorderBottom("0px solid #dddddd");
//                    }
//
//                    Div aParent = new Div();
//                    aParent.addStyleName("helperParent");
//
//                    MaterialLink link = new MaterialLink();
//                    link.addStyleName("collapsibleThemeLayerLink");
//                    link.setText(theme.getName());
//
//                    aParent.add(link);
//                    header.add(aParent);
//                    item.add(header);
//
//                    MaterialCollapsibleBody body = new MaterialCollapsibleBody();
//                    body.addStyleName("collapsibleThemeLayerBody");
//                    body.addMouseOverHandler(event -> {
//                        body.getElement().getStyle().setCursor(Cursor.DEFAULT);
//                    });
//                    if (i < extract.getRealEstate().getConcernedThemes().size()) {
//                        body.setBorderBottom("1px solid #dddddd");
//                    } else {
//                        body.setBorderBottom("0px solid #dddddd");
//                        body.setBorderTop("1px solid #dddddd");
//                    }
//
//                    MaterialRow sliderRow = new MaterialRow();
//                    sliderRow.addStyleName("opacitySliderRow");
//
//                    MaterialColumn sliderRowLeft = new MaterialColumn();
//                    sliderRowLeft.setGrid("s3");
//                    MaterialColumn sliderRowRight = new MaterialColumn();
//                    sliderRowRight.setGrid("s9");
//
//                    MaterialRange slider = new MaterialRange();
//                    slider.addStyleName("opacitySlider");
//                    slider.setMin(0);
//                    slider.setMax(100);
//                    slider.setValue(Double.valueOf((theme.getReferenceWMS().getLayerOpacity() * 100)).intValue());
//                    slider.addValueChangeHandler(event -> {
//                        double opacity = slider.getValue() / 100.0;
//                        wmsLayer.setOpacity(opacity);
//                    });
//                    sliderRowLeft.add(new Label(messages.resultOpacity() + ":"));
//                    sliderRowLeft.addStyleName("opacitySliderRowLeft");
//
//                    sliderRowRight.add(slider);
//                    sliderRow.add(sliderRowLeft);
//                    sliderRow.add(sliderRowRight);
//                    body.add(sliderRow);
//
//                    {
//                        MaterialRow informationHeaderRow = new MaterialRow();
//                        informationHeaderRow.addStyleName("layerInfoHeaderRow");
//
//                        MaterialColumn typeColumn = new MaterialColumn();
//                        typeColumn.addStyleName("layerTypeColumn");
//                        typeColumn.setGrid("s6");
//                        typeColumn.add(new Label(messages.resultType()));
//
//                        MaterialColumn symbolColumn = new MaterialColumn();
//                        symbolColumn.addStyleName("layerSymbolColumn");
//                        symbolColumn.setGrid("s1");
//                        symbolColumn.add(new HTML("&nbsp;"));
//
//                        MaterialColumn shareColumn = new MaterialColumn();
//                        shareColumn.addStyleName("layerShareColumn");
//                        shareColumn.setGrid("s3");
//                        shareColumn.add(new Label(messages.resultShare()));
//
//                        MaterialColumn sharePercentColumn = new MaterialColumn();
//                        sharePercentColumn.addStyleName("layerPercentColumn");
//                        sharePercentColumn.setGrid("s2");
//                        sharePercentColumn.add(new Label(messages.resultShareInPercent()));
//
//                        informationHeaderRow.add(typeColumn);
//                        informationHeaderRow.add(symbolColumn);
//                        informationHeaderRow.add(shareColumn);
//                        informationHeaderRow.add(sharePercentColumn);
//                        body.add(informationHeaderRow);
//                    }
//
//                    {
//                        for (Restriction restriction : theme.getRestrictions()) {
//                            if (restriction.getAreaShare() != null) {
//                                MaterialRow informationRow = processRestrictionRow(restriction, GeometryType.POLYGON);
//                                body.add(informationRow);
//                            }
//
//                            if (restriction.getLengthShare() != null) {
//                                MaterialRow informationRow = processRestrictionRow(restriction, GeometryType.LINE);
//                                body.add(informationRow);
//                            }
//
//                            if (restriction.getNrOfPoints() != null) {
//                                MaterialRow informationRow = processRestrictionRow(restriction, GeometryType.POINT);
//                                body.add(informationRow);
//                            }
//                        }
//                        MaterialRow fakeRow = new MaterialRow();
//                        fakeRow.setBorderBottom("1px #bdbdbd solid");
//                        body.add(fakeRow);
//                    }
//
//                    if (theme.getLegendAtWeb() != null) {
//                        MaterialRow legendRow = new MaterialRow();
//                        legendRow.addStyleName("layerLegendRow");
//
//                        MaterialColumn legendColumn = new MaterialColumn();
//                        legendColumn.addStyleName("layerLegendColumn");
//                        legendColumn.setGrid("s12");
//
//                        MaterialLink legendLink = new MaterialLink();
//                        legendLink.addStyleName("resultLink");
//                        legendLink.setText(messages.resultShowLegend());
//                        legendColumn.add(legendLink);
//
//                        legendRow.add(legendColumn);
//                        body.add(legendRow);
//
//                        com.google.gwt.user.client.ui.Image legendImage = new com.google.gwt.user.client.ui.Image();
//                        legendImage.setUrl(theme.getLegendAtWeb());
//                        legendImage.setVisible(false);
//                        body.add(legendImage);
//
//                        MaterialRow fakeRow = new MaterialRow();
//                        fakeRow.setBorderBottom("1px #bdbdbd solid");
//                        body.add(fakeRow);
//
//                        legendLink.addClickHandler(event -> {
//                            if (legendImage.isVisible()) {
//                                legendImage.setVisible(false);
//                                legendLink.setText(messages.resultShowLegend());
//                            } else {
//                                legendImage.setVisible(true);
//                                legendLink.setText(messages.resultHideLegend());
//                            }
//                        });
//                    }
//
//                    {
//                        MaterialRow legalProvisionsHeaderRow = new MaterialRow();
//                        legalProvisionsHeaderRow.addStyleName("documentsHeaderRow");
//                        legalProvisionsHeaderRow.add(new Label(messages.legalProvisions()));
//                        body.add(legalProvisionsHeaderRow);
//
//                        for (ch.so.agi.oereb.webclient.shared.models.plr.Document legalProvision : theme
//                                .getLegalProvisions()) {
//                            MaterialRow row = new MaterialRow();
//                            row.addStyleName("documentRow");
//
//                            MaterialLink legalProvisionLink = new MaterialLink();
//
//                            if (legalProvision.getOfficialTitle() != null) {
//                                legalProvisionLink.setText(legalProvision.getOfficialTitle());
//                            } else {
//                                legalProvisionLink.setText(legalProvision.getTitle());
//                            }
//                            legalProvisionLink.setHref(legalProvision.getTextAtWeb());
//                            legalProvisionLink.setTarget("_blank");
//                            legalProvisionLink.addStyleName("resultLink");
//                            row.add(legalProvisionLink);
//                            body.add(row);
//
//                            MaterialRow additionalInfoRow = new MaterialRow();
//                            additionalInfoRow.addStyleName("documentAdditionalInfoRow");
//
//                            String labelText = legalProvision.getTitle();
//                            if (legalProvision.getOfficialNumber() != null) {
//                                labelText += " Nr. " + legalProvision.getOfficialNumber();
//                            }
//                            Label label = new Label(labelText);
//                            additionalInfoRow.add(label);
//                            body.add(additionalInfoRow);
//                        }
//
//                        MaterialRow lawsHeaderRow = new MaterialRow();
//                        lawsHeaderRow.addStyleName("documentsHeaderRow");
//                        lawsHeaderRow.add(new Label(messages.laws()));
//                        body.add(lawsHeaderRow);
//
//                        for (ch.so.agi.oereb.webclient.shared.models.plr.Document law : theme.getLaws()) {
//                            MaterialRow row = new MaterialRow();
//                            row.addStyleName("lawRow");
//
//                            MaterialLink lawLink = new MaterialLink();
//
//                            String linkText = "";
//                            if (law.getOfficialTitle() != null) {
//                                linkText = law.getOfficialTitle();
//                            } else {
//                                linkText = law.getTitle();
//                            }
//                            if (law.getAbbreviation() != null) {
//                                linkText += " (" + law.getAbbreviation() + ")";
//                            }
//                            if (law.getOfficialNumber() != null) {
//                                linkText += ", " + law.getOfficialNumber();
//                            }
//                            lawLink.setText(linkText);
//                            lawLink.setHref(law.getTextAtWeb());
//                            lawLink.setTarget("_blank");
//                            lawLink.addStyleName("resultLink");
//                            row.add(lawLink);
//                            body.add(row);
//                        }
//                        MaterialRow fakeRow = new MaterialRow();
//                        fakeRow.setBorderBottom("1px #bdbdbd solid");
//                        fakeRow.setPaddingTop(5);
//                        body.add(fakeRow);
//                    }
//                    {
//                        MaterialRow responsibleOfficeHeaderRow = new MaterialRow();
//                        responsibleOfficeHeaderRow.addStyleName("documentsHeaderRow");
//                        responsibleOfficeHeaderRow.add(new Label(messages.responsibleOffice()));
//                        body.add(responsibleOfficeHeaderRow);
//
//                        for (Office office : theme.getResponsibleOffice()) {
//                            MaterialRow row = new MaterialRow();
//                            row.addStyleName("documentRow");
//
//                            MaterialLink officeLink = new MaterialLink();
//                            officeLink.setText(office.getName());
//                            officeLink.setHref(office.getOfficeAtWeb());
//                            officeLink.setTarget("_blank");
//                            officeLink.addStyleName("resultLink");
//                            row.add(officeLink);
//                            body.add(row);
//                        }
//                    }
//                    item.add(body);
//                    plrInnerCollapsibleConcernedTheme.add(item);
//                }
//
//                // Handle visibility of plr wms layers.
//                // Show them only if plr tab is selected.
//                plrInnerCollapsibleConcernedTheme.addExpandHandler(event -> {
//                    plrExpandedLayerId = event.getTarget().getId();
//                    if (resultTab.getTabIndex() == 2) {
//                        for (String layerId : concernedWmsLayers) {
//                            Image wmsLayer = (Image) getLayerById(layerId);
//                            if (layerId.equalsIgnoreCase(plrExpandedLayerId)) {
//                                wmsLayer.setVisible(true);
//                            } else {
//                                wmsLayer.setVisible(false);
//                            }
//                        }
////                      MaterialCollapsibleItem item = event.getTarget();
////                      MaterialCollapsibleHeader header = item.getHeader();
////                      List<Widget> children = header.getChildrenList();
////                      for (Widget child : children) {
////                          if (child instanceof gwt.material.design.client.ui.MaterialLink) {
////                              MaterialLink link = (MaterialLink) child;
////                              link.setIconType(IconType.EXPAND_LESS);
////                          }
////                      }
//                    }
//                });
//
//                plrInnerCollapsibleConcernedTheme.addCollapseHandler(event -> {
//                    plrExpandedLayerId = null;
//                    Image wmsLayer = (Image) getLayerById(event.getTarget().getId());
//                    wmsLayer.setVisible(false);
////                  MaterialCollapsibleItem item = event.getTarget();
////                  MaterialCollapsibleHeader header = item.getHeader();
////                  List<Widget> children = header.getChildrenList();
////                  for (Widget child : children) {
////                      if (child instanceof gwt.material.design.client.ui.MaterialLink) {
////                          MaterialLink link = (MaterialLink) child;
////                          link.setIconType(IconType.EXPAND_MORE);
////                      }
////                  }
//                });
//
//                plrInnerCollapsibleConcernedTheme.open(1);
//                collapsibleConcernedThemeBody.add(plrInnerCollapsibleConcernedTheme);
//            }
//
//            collapsibleConcernedThemeItem.add(collapsibleConcernedThemeHeader);
//            if (extract.getRealEstate().getConcernedThemes().size() > 0) {
//                collapsibleConcernedThemeItem.add(collapsibleConcernedThemeBody);
//            }
//
//            plrCollapsibleConcernedTheme.add(collapsibleConcernedThemeItem);
//
//            if (extract.getRealEstate().getConcernedThemes().size() > 0) {
//                plrCollapsibleConcernedTheme.open(1);
//            }
//
//            plrCollapsibleDiv.add(plrCollapsibleConcernedTheme);
//        }
//
        {
            oerebCollapsibleNotConcernedTheme = new MaterialCollapsible();
            oerebCollapsibleNotConcernedTheme.addStyleName("plrTopLevelCollapsible");
            oerebCollapsibleNotConcernedTheme.setShadow(0);

            oerebCollapsibleNotConcernedTheme.addExpandHandler(event -> {
                plrCollapsibleConcernedTheme.close(1);
                oerebCollapsibleThemesWithoutData.closeAll();
                oerebCollapsibleGeneralInformation.closeAll();
            });

            MaterialCollapsibleItem collapsibleNotConcernedThemeItem = new MaterialCollapsibleItem();

            MaterialCollapsibleHeader collapsibleNotConcernedThemeHeader = new MaterialCollapsibleHeader();
            collapsibleNotConcernedThemeHeader.addStyleName("plrCollapsibleThemeHeader");

            MaterialRow collapsibleNotConcernedThemeHeaderRow = new MaterialRow();
            collapsibleNotConcernedThemeHeaderRow.addStyleName("collapsibleThemeHeaderRow");

            MaterialColumn collapsibleNotConcernedThemeColumnLeft = new MaterialColumn();
            collapsibleNotConcernedThemeColumnLeft.addStyleName("collapsibleThemeColumnLeft");
            collapsibleNotConcernedThemeColumnLeft.setGrid("s10");
            MaterialColumn collapsibleNotConcernedThemeColumnRight = new MaterialColumn();
            collapsibleNotConcernedThemeColumnRight.addStyleName("collapsibleThemeColumnRight");
            collapsibleNotConcernedThemeColumnRight.setGrid("s2");

            MaterialLink collapsibleNotConcernedHeaderLink = new MaterialLink();
            collapsibleNotConcernedHeaderLink.addStyleName("collapsibleThemesHeaderLink");
            collapsibleNotConcernedHeaderLink.setText(messages.notConcernedThemes());
            collapsibleNotConcernedThemeColumnLeft.add(collapsibleNotConcernedHeaderLink);

            MaterialChip collapsibleNotConcernedHeaderChip = new MaterialChip();
            collapsibleNotConcernedHeaderChip.addStyleName("collapsibleThemesHeaderChip");
            collapsibleNotConcernedHeaderChip
                    .setText(String.valueOf(realEstateDPR.getOerebNotConcernedThemes().size()));
            collapsibleNotConcernedThemeColumnRight.add(collapsibleNotConcernedHeaderChip);

            collapsibleNotConcernedThemeHeaderRow.add(collapsibleNotConcernedThemeColumnLeft);
            collapsibleNotConcernedThemeHeaderRow.add(collapsibleNotConcernedThemeColumnRight);
            collapsibleNotConcernedThemeHeader.add(collapsibleNotConcernedThemeHeaderRow);

            MaterialCollapsibleBody collapsibleBody = new MaterialCollapsibleBody();
            collapsibleBody.addMouseOverHandler(event -> {
                collapsibleBody.getElement().getStyle().setCursor(Cursor.DEFAULT);
            });
            collapsibleBody.setPadding(0);
            MaterialCollection collection = new MaterialCollection();

            for (NotConcernedTheme theme : realEstateDPR.getOerebNotConcernedThemes()) {
                MaterialCollectionItem item = new MaterialCollectionItem();
                MaterialLabel label = new MaterialLabel(theme.getName());
                label.addStyleName("notConcernedThemesLabel");
                item.add(label);
                collection.add(item);
            }
            collapsibleBody.add(collection);

            collapsibleNotConcernedThemeItem.add(collapsibleNotConcernedThemeHeader);
            collapsibleNotConcernedThemeItem.add(collapsibleBody);
            oerebCollapsibleNotConcernedTheme.add(collapsibleNotConcernedThemeItem);

            plrCollapsibleDiv.add(oerebCollapsibleNotConcernedTheme);
        }
        
        {
            oerebCollapsibleThemesWithoutData = new MaterialCollapsible();
            oerebCollapsibleThemesWithoutData.addStyleName("plrTopLevelCollapsible");
            oerebCollapsibleThemesWithoutData.setShadow(0);

            oerebCollapsibleThemesWithoutData.addExpandHandler(event -> {
                plrCollapsibleConcernedTheme.close(1);
                oerebCollapsibleNotConcernedTheme.closeAll();
                oerebCollapsibleGeneralInformation.closeAll();
            });

            MaterialCollapsibleItem collapsibleThemesWithoutDataItem = new MaterialCollapsibleItem();

            MaterialCollapsibleHeader collapsibleThemesWithoutDataHeader = new MaterialCollapsibleHeader();
            collapsibleThemesWithoutDataHeader.addStyleName("plrCollapsibleThemeHeader");
            collapsibleThemesWithoutDataHeader.setBackgroundColor(Color.GREY_LIGHTEN_3);

            MaterialRow collapsibleThemesWithoutDataHeaderRow = new MaterialRow();
            collapsibleThemesWithoutDataHeaderRow.addStyleName("collapsibleThemeHeaderRow");

            MaterialColumn collapsibleThemesWithoutDataColumnLeft = new MaterialColumn();
            collapsibleThemesWithoutDataColumnLeft.addStyleName("collapsibleThemeColumnLeft");
            collapsibleThemesWithoutDataColumnLeft.setGrid("s10");
            MaterialColumn collapsibleThemesWithoutDataColumnRight = new MaterialColumn();
            collapsibleThemesWithoutDataColumnRight.addStyleName("collapsibleThemeColumnRight");
            collapsibleThemesWithoutDataColumnRight.setGrid("s2");

            MaterialLink collapsibleThemesWithoutHeaderLink = new MaterialLink();
            collapsibleThemesWithoutHeaderLink.addStyleName("collapsibleThemesHeaderLink");
            collapsibleThemesWithoutHeaderLink.setText(messages.themesWithoutData());
            collapsibleThemesWithoutDataColumnLeft.add(collapsibleThemesWithoutHeaderLink);

            MaterialChip collapsibleThemesWithoutHeaderChip = new MaterialChip();
            collapsibleThemesWithoutHeaderChip.addStyleName("collapsibleThemesHeaderChip");
            collapsibleThemesWithoutHeaderChip
                    .setText(String.valueOf(realEstateDPR.getOerebThemesWithoutData().size()));
            collapsibleThemesWithoutDataColumnRight.add(collapsibleThemesWithoutHeaderChip);

            collapsibleThemesWithoutDataHeaderRow.add(collapsibleThemesWithoutDataColumnLeft);
            collapsibleThemesWithoutDataHeaderRow.add(collapsibleThemesWithoutDataColumnRight);
            collapsibleThemesWithoutDataHeader.add(collapsibleThemesWithoutDataHeaderRow);

            MaterialCollapsibleBody collapsibleBody = new MaterialCollapsibleBody();
            collapsibleBody.addMouseOverHandler(event -> {
                collapsibleBody.getElement().getStyle().setCursor(Cursor.DEFAULT);
            });
            collapsibleBody.setPadding(0);
            MaterialCollection collection = new MaterialCollection();

            for (ThemeWithoutData theme : realEstateDPR.getOerebThemesWithoutData()) {
                MaterialCollectionItem item = new MaterialCollectionItem();
                MaterialLabel label = new MaterialLabel(theme.getName());
                label.addStyleName("withoutDataThemesLabel");
                item.add(label);
                collection.add(item);
            }
            collapsibleBody.add(collection);

            collapsibleThemesWithoutDataItem.add(collapsibleThemesWithoutDataHeader);
            collapsibleThemesWithoutDataItem.add(collapsibleBody);
            oerebCollapsibleThemesWithoutData.add(collapsibleThemesWithoutDataItem);

            plrCollapsibleDiv.add(oerebCollapsibleThemesWithoutData);
        }
        {
            oerebCollapsibleGeneralInformation = new MaterialCollapsible();
            oerebCollapsibleGeneralInformation.addStyleName("plrTopLevelCollapsible");
            oerebCollapsibleGeneralInformation.setShadow(0);

            oerebCollapsibleGeneralInformation.addExpandHandler(event -> {
                plrCollapsibleConcernedTheme.close(1);
                oerebCollapsibleNotConcernedTheme.closeAll();
                oerebCollapsibleThemesWithoutData.closeAll();
            });

            MaterialCollapsibleItem collapsibleGeneralInformationItem = new MaterialCollapsibleItem();

            MaterialCollapsibleHeader collapsibleGeneralInformationHeader = new MaterialCollapsibleHeader();
            collapsibleGeneralInformationHeader.addStyleName("plrCollapsibleThemeHeader");

            MaterialCollapsibleBody body = new MaterialCollapsibleBody();
            body.addStyleName("collapsibleGeneralInformationBody");
            body.addMouseOverHandler(event -> {
                body.getElement().getStyle().setCursor(Cursor.DEFAULT);
            });

            HTML infoHtml = new HTML();

            StringBuilder html = new StringBuilder();
            html.append("<b>Katasterverantwortliche Stelle</b>");
            html.append("<br>");
            html.append(realEstateDPR.getOerebCadastreAuthority().getName());

            infoHtml.setHTML(html.toString());
            body.add(infoHtml);

            MaterialRow collapsibleGeneralInformationHeaderRow = new MaterialRow();
            collapsibleGeneralInformationHeaderRow.addStyleName("collapsibleThemeHeaderRow");

            MaterialColumn collapsibleGeneralInformationColumnLeft = new MaterialColumn();
            collapsibleGeneralInformationColumnLeft.addStyleName("collapsibleThemeColumnLeft");
            collapsibleGeneralInformationColumnLeft.setGrid("s10");

            MaterialLink collapsibleThemesWithoutHeaderLink = new MaterialLink();
            collapsibleThemesWithoutHeaderLink.addStyleName("collapsibleThemesHeaderLink");
            collapsibleThemesWithoutHeaderLink.setText(messages.generalInformation());
            collapsibleGeneralInformationColumnLeft.add(collapsibleThemesWithoutHeaderLink);

            collapsibleGeneralInformationHeaderRow.add(collapsibleGeneralInformationColumnLeft);
            collapsibleGeneralInformationHeader.add(collapsibleGeneralInformationHeaderRow);

            collapsibleGeneralInformationItem.add(collapsibleGeneralInformationHeader);
            collapsibleGeneralInformationItem.add(body);
            oerebCollapsibleGeneralInformation.add(collapsibleGeneralInformationItem);

            plrCollapsibleDiv.add(oerebCollapsibleGeneralInformation);
        }

        oerebResultColumn.add(plrCollapsibleDiv);
    }
    
    private void addCadastralSurveyingContent(RealEstateDPR realEstateDPR) {
        String number = realEstateDPR.getNumber();
        String identnd = realEstateDPR.getIdentND();
        String egrid = realEstateDPR.getEgrid();
        int area = realEstateDPR.getLandRegistryArea();
        String type = realEstateDPR.getRealEstateType();
        String municipality = realEstateDPR.getMunicipality();
        String subunitOfLandRegister = realEstateDPR.getSubunitOfLandRegister();

        addCadastralSurveyingContentKeyValue(new Label("E-GRID:"), new Label(egrid));
        addCadastralSurveyingContentKeyValue(new Label("NBIdent:"), new Label(identnd == null ? "N/A" : identnd));
        addCadastralSurveyingContentKeyValue(new HTML("&nbsp;"), new HTML("&nbsp;"));

        addCadastralSurveyingContentKeyValue(new Label("Grundstücksart:"), new Label(type));
        addCadastralSurveyingContentKeyValue(new Label("Grundstücksfläche:"),
                new HTML(fmtDefault.format(area) + " m<sup>2</sup>"));
        addCadastralSurveyingContentKeyValue(new HTML("&nbsp;"), new HTML("&nbsp;"));

        addCadastralSurveyingContentKeyValue(new Label("Gemeinde:"), new Label(municipality));
                
        addCadastralSurveyingContentKeyValue(new Label("Grundbuch:"), new Label(subunitOfLandRegister == null ? "N/A" : subunitOfLandRegister));
        addCadastralSurveyingContentKeyValue(new HTML("&nbsp;"), new HTML("&nbsp;"));

        //addCadastralSurveyingContentKeyValue(new Label("Flurnamen:"), new Label(String.join(", ", localNames)));
        addCadastralSurveyingContentKeyValue(new Label("Flurnamen:"), new Label("N/A"));

        {
            MaterialColumn fakeColumn = new MaterialColumn();
            fakeColumn.addStyleName("fakeColumn mt15");
            fakeColumn.setGrid("s12");
            cadastralSurveyingResultColumn.add(fakeColumn);
        }
    }
    
    public final class MapSingleClickListener implements EventListener<MapBrowserEvent> {
        @Override
        public void onEvent(MapBrowserEvent event) {
            MaterialLoader.loading(true);
            
            Coordinate coordinate = event.getCoordinate();
            sendCoordinateToServer(coordinate.toStringXY(3), event);
            
            
            
            // does not return egrid :(
            /*
            //https://api3.geo.admin.ch/rest/services/all/MapServer/identify?geometry=2607381.2857129965,1228422.772096185&geometryFormat=geojson&geometryType=esriGeometryPoint&imageDisplay=1780,772,96&lang=de&layers=all:ch.kantone.cadastralwebmap-farbe&limit=10&mapExtent=2607182.461501755,1228369.7490333454,2607507.9384982456,1228510.9109666548&returnGeometry=true&sr=2056&tolerance=10
            //String requestUrl = "https://api3.geo.admin.ch/rest/services/all/MapServer/identify?geometry=2607381.2857129965,1228422.772096185&geometryFormat=geojson&geometryType=esriGeometryPoint&imageDisplay=1780,772,96&lang=de&layers=all:ch.kantone.cadastralwebmap-farbe&limit=10&mapExtent=2607182.461501755,1228369.7490333454,2607507.9384982456,1228510.9109666548&returnGeometry=true&sr=2056&tolerance=10";
            //String requestUrl = "https://api3.geo.admin.ch/rest/services/all/MapServer/identify?geometry=2607389.224071799,1228423.0556600185&geometryFormat=geojson&geometryType=esriGeometryPoint&imageDisplay=1780,772,96&lang=de&layers=all:ch.kantone.cadastralwebmap-farbe&limit=10&mapExtent=2607182.461501755,1228369.7490333454,2607507.9384982456,1228510.9109666548&returnGeometry=true&sr=2056&tolerance=10";
            String identifyRequest = format(identifyRequestTemplate, String.valueOf(coordinate.getX()), String.valueOf(coordinate.getY()), String.valueOf(coordinate.getX()), String.valueOf(coordinate.getY()), String.valueOf(coordinate.getX()), String.valueOf(coordinate.getY()));
            
            fetch(identifyRequest)
            .then(Response::json)
            .then(data -> {
                // Create a valid GeoJSON from returned JSON. 
                // Later on we can create an ol3 vector layer with the GeoJSON features.
                JSONObject newFeatureCollection = new JSONObject();
                newFeatureCollection.put("type", new JSONString("FeatureCollection"));
                JSONArray newFeaturesArray = new JSONArray();

                JSONObject obj = new JSONObject(JsonUtils.safeEval(Global.JSON.stringify(data)));
                JSONArray array = obj.get("results").isArray();
                for (int i=0; i<array.size(); i++) {
                    
                    JSONObject feature = array.get(i).isObject();
                    JSONObject geometry = feature.get("geometry").isObject();
                    JSONObject properties = feature.get("properties").isObject();
                    
                    JSONObject newFeature = new JSONObject();
                    newFeature.put("type",  new JSONString("Feature"));
                    newFeature.put("properties", properties);
                    newFeature.put("geometry", geometry);
                    
                    newFeaturesArray.set(i, newFeature);
                }
                newFeatureCollection.put("features", newFeaturesArray);
                
                Feature[] features = (new GeoJson()).readFeatures(newFeatureCollection.toString());
                
                for (Feature feature : features) {
                    console.log(feature.getProperties().get("label"));
                    console.log(feature.getGeometry().getExtent().getLowerLeftX());
                }
                return null;
            }).
            catch_(error -> {
                console.log(error);
                return null;
            }); 
            */ 
        }
    }
    
    // Add a key / value to cadastral surveying result column
    private void addCadastralSurveyingContentKeyValue(Label key, Label value) {
        Div row = new Div();
        row.addStyleName("cadastralSurveyingInfoRow");

        Div keyColumn = new Div();
        keyColumn.addStyleName("cadastralSurveyingInfoKeyColumn");
        keyColumn.setGrid("s5");
        keyColumn.add(key);
        cadastralSurveyingResultColumn.add(keyColumn);

        Div valueColumn = new Div();
        valueColumn.addStyleName("cadastralSurveyingInfoValueColumn");
        valueColumn.setGrid("s7");
        valueColumn.add(value);
        cadastralSurveyingResultColumn.add(valueColumn);
    }

    // Create the vector layer for highlighting the 
    // real estate.
    private ol.layer.Vector createRealEstateVectorLayer(String geometry) {
        Geometry realEstateGeometry = new Wkt().readGeometry(geometry);
        return createRealEstateVectorLayer(realEstateGeometry);
    }
    
    // Create the vector layer for highlighting the 
    // real estate.
    private ol.layer.Vector createRealEstateVectorLayer(Geometry geometry) {
        FeatureOptions featureOptions = OLFactory.createOptions();
        featureOptions.setGeometry(geometry);

        Feature feature = new Feature(featureOptions);
        feature.setId(REAL_ESTATE_VECTOR_FEATURE_ID);

        Style style = new Style();
        Stroke stroke = new Stroke();
        stroke.setWidth(8);
        stroke.setColor(new ol.color.Color(230, 0, 0, 0.6));
        style.setStroke(stroke);
        feature.setStyle(style);

        ol.Collection<Feature> lstFeatures = new ol.Collection<Feature>();
        lstFeatures.push(feature);

        VectorOptions vectorSourceOptions = OLFactory.createOptions();
        vectorSourceOptions.setFeatures(lstFeatures);
        Vector vectorSource = new Vector(vectorSourceOptions);

        VectorLayerOptions vectorLayerOptions = OLFactory.createOptions();
        vectorLayerOptions.setSource(vectorSource);
        ol.layer.Vector vectorLayer = new ol.layer.Vector(vectorLayerOptions);
        vectorLayer.set(ID_ATTR_NAME, REAL_ESTATE_VECTOR_LAYER_ID);

        return vectorLayer;
    }

    // Remove all WMS (= oereb concerned themes) layers and the
    // vector layer for highlighting the real estate.
    private void removeOerebWmsLayers() {
        // Remove WMS (concerned themes) layers.
        // I cannot iterate over map.getLayers() and
        // use map.removeLayers(). Seems to get some
        // confusion with the indices or whatever...
        for (String layerId : oerebWmsLayers) {
            Image rlayer = (Image) getMapLayerById(layerId);
            map.removeLayer(rlayer);
        }

        // Remove highlighting layer. 
        Base vlayer = getMapLayerById(REAL_ESTATE_VECTOR_LAYER_ID);
        map.removeLayer(vlayer);

        oerebWmsLayers.clear();
    }
    
    // Get Openlayers map layer by id.
    private Base getMapLayerById(String id) {
        ol.Collection<Base> layers = map.getLayers();
        for (int i = 0; i < layers.getLength(); i++) {
            Base item = layers.item(i);
            try {
                String layerId = item.get(ID_ATTR_NAME);
                if (layerId == null) {
                    continue;
                }
                if (layerId.equalsIgnoreCase(id)) {
                    return item;
                }
            } catch (Exception e) {
                GWT.log(e.getMessage());
                GWT.log("should not reach here");
            }
        }
        return null;
    }
    
    // Update the URL in the browser without reloading the page.
    private static native void updateURLWithoutReloading(String newUrl) /*-{
        $wnd.history.pushState(newUrl, "", newUrl);
    }-*/;
    
    // String.format() is not available in GWT hence we need to 
    // create our own implementation.
    private static String format(final String format, final String... args) {
        String[] split = format.split("%s");
        final StringBuffer msg = new StringBuffer();
        for (int pos = 0; pos < split.length - 1; pos += 1) {
            msg.append(split[pos]);
            msg.append(args[pos]);
        }
        msg.append(split[split.length - 1]);
        return msg.toString();
    }   
}