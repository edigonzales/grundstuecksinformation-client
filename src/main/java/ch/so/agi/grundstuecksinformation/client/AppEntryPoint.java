package ch.so.agi.grundstuecksinformation.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.dominokit.domino.ui.button.Button;
import org.dominokit.domino.ui.button.ButtonSize;
import org.dominokit.domino.ui.chips.Chip;
import org.dominokit.domino.ui.collapsible.Accordion;
import org.dominokit.domino.ui.collapsible.AccordionPanel;
import org.dominokit.domino.ui.collapsible.Collapsible.HideCompletedHandler;
import org.dominokit.domino.ui.collapsible.Collapsible.ShowCompletedHandler;
import org.dominokit.domino.ui.dialogs.MessageDialog;
import org.dominokit.domino.ui.dropdown.DropDownMenu;
import org.dominokit.domino.ui.forms.SuggestBox;
import org.dominokit.domino.ui.forms.SuggestBoxStore;
import org.dominokit.domino.ui.forms.SuggestItem;
import org.dominokit.domino.ui.grid.Column;
import org.dominokit.domino.ui.grid.Row;
import org.dominokit.domino.ui.icons.Icon;
import org.dominokit.domino.ui.icons.Icons;
import org.dominokit.domino.ui.lists.ListGroup;
import org.dominokit.domino.ui.loaders.Loader;
import org.dominokit.domino.ui.loaders.LoaderEffect;
import org.dominokit.domino.ui.notifications.Notification;
import org.dominokit.domino.ui.sliders.Slider;
import org.dominokit.domino.ui.style.StyleType;
import org.dominokit.domino.ui.style.Styles;
import org.dominokit.domino.ui.tabs.Tab;
import org.dominokit.domino.ui.tabs.TabsPanel;
import org.dominokit.domino.ui.themes.Theme;
import org.dominokit.domino.ui.utils.DominoElement;
import org.dominokit.domino.ui.utils.TextNode;
import org.dominokit.domino.ui.utils.HasSelectionHandler.SelectionHandler;
import org.dominokit.domino.ui.forms.SuggestBox.DropDownPositionDown;
import org.dominokit.domino.ui.style.Color;
import org.dominokit.domino.ui.style.ColorScheme;
import org.dominokit.domino.ui.grid.flex.FlexItem;
import org.dominokit.domino.ui.grid.flex.FlexLayout;

import org.gwtproject.safehtml.shared.SafeHtmlUtils;
import org.jboss.elemento.EventType;
import org.jboss.elemento.HtmlContentBuilder;
import static org.dominokit.domino.ui.style.Unit.px;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.LinkElement;
import com.google.gwt.dom.client.ScriptElement;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

import ch.so.agi.grundstuecksinformation.shared.EgridResponse;
import ch.so.agi.grundstuecksinformation.shared.EgridService;
import ch.so.agi.grundstuecksinformation.shared.EgridServiceAsync;
import ch.so.agi.grundstuecksinformation.shared.ExtractResponse;
import ch.so.agi.grundstuecksinformation.shared.ExtractService;
import ch.so.agi.grundstuecksinformation.shared.ExtractServiceAsync;
import ch.so.agi.grundstuecksinformation.shared.SettingsResponse;
import ch.so.agi.grundstuecksinformation.shared.SettingsService;
import ch.so.agi.grundstuecksinformation.shared.SettingsServiceAsync;
import ch.so.agi.grundstuecksinformation.shared.models.Building;
import ch.so.agi.grundstuecksinformation.shared.models.BuildingEntry;
import ch.so.agi.grundstuecksinformation.shared.models.ConcernedTheme;
import ch.so.agi.grundstuecksinformation.shared.models.Document;
import ch.so.agi.grundstuecksinformation.shared.models.Egrid;
import ch.so.agi.grundstuecksinformation.shared.models.LandCoverShare;
import ch.so.agi.grundstuecksinformation.shared.models.NotConcernedTheme;
import ch.so.agi.grundstuecksinformation.shared.models.Office;
import ch.so.agi.grundstuecksinformation.shared.models.PostalAddress;
import ch.so.agi.grundstuecksinformation.shared.models.RealEstateDPR;
import ch.so.agi.grundstuecksinformation.shared.models.ReferenceWMS;
import ch.so.agi.grundstuecksinformation.shared.models.Restriction;
import ch.so.agi.grundstuecksinformation.shared.models.ThemeWithoutData;
import elemental2.core.Global;
import elemental2.core.JsArray;
import elemental2.core.JsString;
import elemental2.core.JsNumber;
import elemental2.dom.CSSProperties;
import elemental2.dom.DomGlobal;
import elemental2.dom.Event;
import elemental2.dom.EventListener;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.Headers;
import elemental2.dom.RequestInit;
import ol.Coordinate;
import ol.Extent;
import ol.Feature;
import ol.FeatureOptions;
import ol.Map;
import ol.MapBrowserEvent;
import ol.OLFactory;
import ol.Overlay;
import ol.OverlayOptions;
import ol.View;
import ol.format.GeoJson;
import ol.format.Wkt;
import ol.geom.Geometry;
import ol.layer.Base;
import ol.layer.Image;
import ol.layer.LayerOptions;
import ol.layer.VectorLayerOptions;
import ol.source.ImageWms;
import ol.source.ImageWmsOptions;
import ol.source.ImageWmsParams;
import ol.source.Vector;
import ol.source.VectorOptions;
import ol.style.Stroke;
import ol.style.Style;

import static elemental2.dom.DomGlobal.console;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;

import static org.jboss.elemento.Elements.*;
import static org.jboss.elemento.EventType.*;

public class AppEntryPoint implements EntryPoint {
    private MainMessages messages = GWT.create(MainMessages.class);
    private final SettingsServiceAsync settingsService = GWT.create(SettingsService.class);
    private final EgridServiceAsync egridService = GWT.create(EgridService.class);
    private final ExtractServiceAsync extractService = GWT.create(ExtractService.class);

    // Settings
    private String MY_VAR;
    private String OPENSEARCH_DESCRIPTION_URL;
    private String SEARCH_SERVICE_URL;
    private String DATA_SERVICE_URL;
    private String OEREB_SERVICE_URL;
    private String CADASTRE_SERVICE_URL;

    private String SUB_HEADER_FONT_SIZE = "16px";
    private String BODY_FONT_SIZE = "14px";
    private String SMALL_FONT_SIZE = "12px";

    private String RESULT_CARD_HEIGHT = "calc(100% - 215px)";

    private String ID_ATTR_NAME = "id";
    private String REAL_ESTATE_VECTOR_LAYER_ID = "real_estate_vector_layer";
    private String REAL_ESTATE_VECTOR_FEATURE_ID = "real_estate_fid";

    private NumberFormat fmtDefault = NumberFormat.getDecimalFormat();
    private NumberFormat fmtPercent = NumberFormat.getFormat("#,##0.0");
    private NumberFormat fmtSquareMeter = NumberFormat.getFormat("#,##0");

    private Loader loader;
    private Map map;
    private HTMLElement mapDiv;
    private Overlay realEstatePopup;
    private HTMLElement resultCard;    
    private HTMLElement resultCardContent;    
    private HTMLDivElement headerRow;
    private HTMLElement resultDiv;

    private Accordion oerebInnerAccordion;
    private boolean oerebAccordionPanelConcernedThemeState = false;
    private boolean oerebAccordionPanelNotConcernedThemeState = false;
    private boolean oerebAccordionPanelThemesWithoutDataState = false;
    private boolean oerebAccordionPanelGeneralInformationState = false;
    
    private String identifyRequestTemplate = "https://api3.geo.admin.ch/rest/services/all/MapServer/identify?geometry=%s,%s&geometryFormat=geojson&geometryType=esriGeometryPoint&imageDisplay=1780,772,96&lang=de&layers=all:ch.kantone.cadastralwebmap-farbe&limit=10&mapExtent=%s,%s,%s,%s&returnGeometry=true&sr=2056&tolerance=10";

    // List with all oereb wms layer that will be added to the ol3 map
    // and removed from it afterwards.
    private ArrayList<String> oerebWmsLayers = new ArrayList<String>();
    
    private HashMap<String, Boolean> innerOerebPanelStateMap = new HashMap<String, Boolean>();

    public void onModuleLoad() {
        settingsService.settingsServer(new AsyncCallback<SettingsResponse>() {
            @Override
            public void onFailure(Throwable caught) {
                Notification.createDanger(caught.getMessage()).setPosition(Notification.TOP_CENTER).show();
                console.log("error: " + caught.getMessage());
            }

            @Override
            public void onSuccess(SettingsResponse result) {
                MY_VAR = (String) result.getSettings().get("MY_VAR");
                OPENSEARCH_DESCRIPTION_URL = (String) result.getSettings().get("OPENSEARCH_DESCRIPTION_URL");
                SEARCH_SERVICE_URL = (String) result.getSettings().get("SEARCH_SERVICE_URL");
                DATA_SERVICE_URL = (String) result.getSettings().get("DATA_SERVICE_URL");
                OEREB_SERVICE_URL = (String) result.getSettings().get("OEREB_SERVICE_URL");
                CADASTRE_SERVICE_URL = (String) result.getSettings().get("CADASTRE_SERVICE_URL");
                init();
            }
        });
    }
    
    @SuppressWarnings("unchecked")
    private void init() {
        Element head = com.google.gwt.dom.client.Document.get().getElementsByTagName("head").getItem(0);        
        LinkElement opensearchdescription = com.google.gwt.dom.client.Document.get().createLinkElement();
        opensearchdescription.setRel("search");
        opensearchdescription.setType("application/opensearchdescription+xml");
        opensearchdescription.setHref(OPENSEARCH_DESCRIPTION_URL);
        opensearchdescription.setTitle("Grundstücksinformationssuche");
        head.appendChild(opensearchdescription);
        
        Theme theme = new Theme(ColorScheme.RED);
        theme.apply();

        loader = Loader.create((HTMLElement) DomGlobal.document.body, LoaderEffect.ROTATION).setLoadingText(null);
        
        mapDiv = div().id("map").element();
        body().add(mapDiv);

        HTMLElement searchCard = div().id("searchCard").element();
        body().add(searchCard);

        HTMLElement logoDiv = div().id("logoDiv")
                .add(img().attr("src", GWT.getHostPageBaseURL() + "logo-grundstuecksinformation.png")
                        .attr("alt", "Logo Grundstücksinformation").attr("width", "62%"))
                .element();
        searchCard.appendChild(logoDiv);

        SuggestBoxStore dynamicStore = new SuggestBoxStore() {
            @Override
            public void filter(String value, SuggestionsHandler suggestionsHandler) {
                if (value.trim().length() == 0) {
                    return;
                }
                
                RequestInit requestInit = RequestInit.create();
                Headers headers = new Headers();
                headers.append("Content-Type", "application/x-www-form-urlencoded"); 
                requestInit.setHeaders(headers);

                DomGlobal.fetch(SEARCH_SERVICE_URL + value.trim().toLowerCase(), requestInit)
                .then(response -> {
                    if (!response.ok) {
                        return null;
                    }
                    return response.text();
                })
                .then(json -> {
                    List<SuggestItem<SearchResult>> suggestItems = new ArrayList<>();
                    JsPropertyMap<?> parsed = Js.cast(Global.JSON.parse(json));
                    JsArray<?> results = Js.cast(parsed.get("results"));
                    for (int i = 0; i < results.length; i++) {
                        JsPropertyMap<?> resultObj = Js.cast(results.getAt(i));
                        if (resultObj.has("feature")) {
                            JsPropertyMap feature = (JsPropertyMap) resultObj.get("feature");
                            String display = ((JsString) feature.get("display")).normalize();
                            String dataproductId = ((JsString) feature.get("dataproduct_id")).normalize();
                            String idFieldName = ((JsString) feature.get("id_field_name")).normalize();
                            int featureId = new Double(((JsNumber) feature.get("feature_id")).valueOf()).intValue();
                            List<Double> bbox = ((JsArray) feature.get("bbox")).asList();
 
                            SearchResult searchResult = new SearchResult();
                            searchResult.setLabel(display);
                            searchResult.setDataproductId(dataproductId);
                            searchResult.setIdFieldName(idFieldName);
                            searchResult.setFeatureId(featureId);
                            searchResult.setBbox(bbox);
                            searchResult.setType("feature");
                            
                            Icon icon;
                            if (dataproductId.contains("gebaeudeadressen")) {
                                icon = Icons.ALL.mail();
                            } else if (dataproductId.contains("grundstueck")) {
                                icon = Icons.ALL.home();
                            } else {
                                icon = Icons.ALL.place();
                            }
                            
                            SuggestItem<SearchResult> suggestItem = SuggestItem.create(searchResult, searchResult.getLabel(), icon);
                            suggestItems.add(suggestItem);
                        }
                    }
                    suggestionsHandler.onSuggestionsReady(suggestItems);
                    return null;
                }).catch_(error -> {
                    console.log(error);
                    return null;
                });
            }

            @Override
            public void find(Object searchValue, Consumer handler) {
                if (searchValue == null) {
                    return;
                }
                SearchResult searchResult = (SearchResult) searchValue;
                SuggestItem<SearchResult> suggestItem = SuggestItem.create(searchResult, null);
                handler.accept(suggestItem);
            }
        };

        SuggestBox suggestBox = SuggestBox.create(messages.searchPlaceholder(), dynamicStore);
        suggestBox.setIcon(Icons.ALL.search());
        suggestBox.getInputElement().setAttribute("autocomplete", "off");
        suggestBox.getInputElement().setAttribute("spellcheck", "false");
        suggestBox.setFocusOnClose(false);
        suggestBox.setFocusColor(Color.RED);
        DropDownMenu suggestionsMenu = suggestBox.getSuggestionsMenu();
        suggestionsMenu.setPosition(new DropDownPositionDown());
        
        suggestBox.addSelectionHandler(new SelectionHandler() {
            @Override
            public void onSelection(Object value) {
                loader.stop();
                resetGui();

                RequestInit requestInit = RequestInit.create();
                Headers headers = new Headers();
                headers.append("Content-Type", "application/x-www-form-urlencoded"); 
                requestInit.setHeaders(headers);

                SuggestItem<SearchResult> item = (SuggestItem<SearchResult>) value;
                SearchResult result = (SearchResult) item.getValue();
                
                // Grundstück: E-GRID beim Dataservice anfragen.
                // Adresse: Aus der BBOX, d.h. der Koordinate des Eingangs das Grundstück filtern.
                
                String searchUrl = null;
                if (result.getDataproductId().equalsIgnoreCase("ch.so.agi.av.grundstuecke.rechtskraeftig")) {
                    String dataproductId = result.getDataproductId();
                    String idFieldName = result.getIdFieldName();
                    String featureId = String.valueOf(result.getFeatureId());
                    
                    searchUrl = DATA_SERVICE_URL + dataproductId + "/?filter=[[\""+idFieldName+"\",\"=\","+featureId+"]]";
                   
                } else if (result.getDataproductId().equalsIgnoreCase("ch.so.agi.av.gebaeudeadressen.gebaeudeeingaenge")) {
                    List<Double> bboxList = result.getBbox();
                    String bbox = bboxList.stream().map(String::valueOf).collect(Collectors.joining(","));
                    
                    searchUrl = DATA_SERVICE_URL + "ch.so.agi.av.grundstuecke.rechtskraeftig/?bbox=" + bbox;
                }
                
                if (searchUrl == null) {
                    // TODO -> Fehlermeldung im GUI
                    console.error("should not reach here");
                    return;
                }
                
                DomGlobal.fetch(searchUrl, requestInit)
                .then(response -> {
                    if (!response.ok) {
                        return null;
                    }
                    return response.text();
                })
                .then(json -> {
                    Feature[] features = (new GeoJson()).readFeatures(json);
                    
                    // Bei einer Adresssuche und anschliessender BBOX-Filterung der
                    // Grundstücke können mehrere Resultate zurückgeliefert werden:
                    // Eine Liegenschaft und 0 bis n SelbstRechte. In solchen Fällen
                    // soll die Liegenschaft verwendet werden.
                    for(int i=0; i<features.length; i++) {
                        String art = (String) features[i].getProperties().get("art_txt");
                        if (art.equalsIgnoreCase("Liegenschaft")) {
                            String egrid = (String) features[i].getProperties().get("egrid");

                            loader.start();

                            Egrid egridObj = new Egrid();
                            egridObj.setEgrid(egrid);
                            sendEgridToServer(egridObj);
                        }
                    }
                    return null;
                }).catch_(error -> {
                    console.log(error);
                    return null;
                });
            }
        });
        
        HTMLElement suggestBoxDiv = div().id("suggestBoxDiv").add(suggestBox).element();
        searchCard.appendChild(suggestBoxDiv);
        
        // Card that shows the results from the extracts.
        resultCard = div().id("resultCard").element();
        resultCardContent = div().id("resultCardContent").element();
        resultCard.appendChild(resultCardContent); // TODO: what is this good for?
        
        HTMLDivElement fadeoutBottomDiv = div().id("fadeoutBottomDiv").element();
        resultCard.appendChild(fadeoutBottomDiv);
        
        body().add(resultCard);

        // It seems that initializing the map and/or adding the click listener
        // must be done after adding the value changed handler of the search.
        // Selecting a search result was also triggering a map single click event.
        map = MapPresets.getBlackAndWhiteMap(mapDiv.id);
        map.addSingleClickListener(new MapSingleClickListener());

        // If there is an egrid query parameter in the url,
        // we request the extract without further interaction.
        if (Window.Location.getParameter("egrid") != null) {
            String egrid = Window.Location.getParameter("egrid").toString();
            loader.start();
            resetGui();
            Egrid egridObj = new Egrid();
            egridObj.setEgrid(egrid);
            sendEgridToServer(egridObj);
        }
    }

    private void resetGui() {
        removeOerebWmsLayers();

        if (resultDiv != null) {
            resultDiv.remove();
        }

        if (realEstatePopup != null) {
            map.removeOverlay(realEstatePopup);
        }

        if (headerRow != null) {
            headerRow.remove();
        }

        resultCard.style.visibility = "hidden";
        
        oerebAccordionPanelConcernedThemeState = false;
        oerebAccordionPanelNotConcernedThemeState = false;
        oerebAccordionPanelThemesWithoutDataState = false;
        oerebAccordionPanelGeneralInformationState = false;   
        
        innerOerebPanelStateMap.clear();
    }

    private void sendCoordinateToServer(String XY, MapBrowserEvent event) {
        egridService.egridServer(XY, new AsyncCallback<EgridResponse>() {
            @Override
            public void onFailure(Throwable caught) {
                loader.stop();
                MessageDialog warningMessage = MessageDialog.createMessage("", messages.errorMessage()).setId("errorModal").error();
                warningMessage.open();
                console.log("error: " + caught.getMessage());
            }

            @Override
            public void onSuccess(EgridResponse result) {
                resetGui();

                if (result.getResponseCode() != 200) {
                    loader.stop();
                    MessageDialog warningMessage = MessageDialog.createMessage("", messages.errorMessage()).setId("errorModal").warning();
                    warningMessage.open();
                    return;
                }
                
                // MapBrowserEvent is null if we end up here by a text search.
                // We just use the first egrid from the list w/o asking the
                // user. Wouldn't be too easy I guess to interact with the
                // user without confusing him.
                String egrid;
                List<Egrid> egridList = result.getEgrid();
                if (egridList.size() > 1 && event != null) {
                    loader.stop();

                    HTMLElement closeButton = span().add(Icons.ALL.close()).element();                    
                    HtmlContentBuilder popupBuilder = div().id("realEstatePopup");
                    popupBuilder.add(
                            div().id("realEstatePopupHeader")
                            .add(span().textContent(messages.realEstatePlural()))
                            .add(span().id("realEstatePopupClose").add(closeButton))
                            ); 
                    
                    HashMap<String, Egrid> egridMap = new HashMap<String, Egrid>();
                    for (Egrid egridObj : egridList) {
                        egrid = (String) egridObj.getEgrid();
                        String number = egridObj.getNumber();
                        egridMap.put(egrid, egridObj);
                        
                        String label = new String(messages.realEstateAbbreviation() + ": " + number + " ("+egridObj.getType()+")");
                        HTMLDivElement row = div().id(egrid).css("realEstatePopupRow")
                                .add(span().textContent(label)).element();
                        
                        bind(row, mouseover, event -> {
                            row.style.backgroundColor = "#efefef";
                            row.style.cursor = "pointer";
                            ol.layer.Vector vlayer = createRealEstateVectorLayer(egridObj.getLimit());
                            map.addLayer(vlayer);
                        });

                        bind(row, mouseout, event -> {
                            row.style.backgroundColor = "white";
                            removeRealEstateVectorLayer();
                        });
                        
                        bind(row, click, event -> {
                            console.log("Get extract from a map click (multiple click result): " + row.getAttribute("id"));                            
                            map.removeOverlay(realEstatePopup);
                            
                            loader.start();
                            sendEgridToServer(egridMap.get(row.getAttribute("id")));
                        });                        
                        popupBuilder.add(row);
                    }
                    
                    HTMLElement popupElement = popupBuilder.element();     
                    bind(closeButton, click, event -> {
                        map.removeOverlay(realEstatePopup);
                    });
                    
                    // TODO: Hackish, but it works.
                    // Wenn ich ohne ol.Overlay arbeite, dann ist das Popup nicht an die Karte
                    // geheftet (was mir noch egal wäre) aber ich schaffe das drag n droppen 
                    // nicht, was ich in diesem Fall notwendig fände.
                    DivElement overlay = Js.cast(popupElement);
                    OverlayOptions overlayOptions = OLFactory.createOptions();
                    overlayOptions.setElement(overlay);
                    overlayOptions.setPosition(event.getCoordinate());
                    overlayOptions.setOffset(OLFactory.createPixel(0, 0));
                    realEstatePopup = new Overlay(overlayOptions);
                    map.addOverlay(realEstatePopup);
                } else if (egridList.size() > 1) {
                    console.log("Get extract from a text search: " + egridList.get(0).getEgrid());
                    loader.start();
                    sendEgridToServer(egridList.get(0));                    
                } else {
                    console.log("Get extract from a map click (single click result): " + egridList.get(0).getEgrid());
                    if (egridList.get(0).getEgrid() == null) {
                        loader.stop();                    
                        MessageDialog warningMessage = MessageDialog.createMessage("", messages.errorMessage()).setId("errorModal").warning();
                        warningMessage.open();
                        return;
                    }
                    loader.start();                    
                    sendEgridToServer(egridList.get(0));
                }               
            }
        });
    }

    private void sendEgridToServer(Egrid egrid) {
        extractService.extractServer(egrid, new AsyncCallback<ExtractResponse>() {
            @Override
            public void onFailure(Throwable caught) {
                loader.stop();
                MessageDialog warningMessage = MessageDialog.createMessage("", messages.errorMessage()).setId("errorModal").warning();
                warningMessage.open();
                console.log("error: " + caught.getMessage());
            }

            @Override
            public void onSuccess(ExtractResponse result) {
                loader.stop();
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

                    // TODO:
                    // Das ist jetzt ziemlich heuristisch...
                    // 500 = Breite des Suchresultates
                    view.setCenter(new Coordinate(x - (resultCard.offsetWidth * view.getResolution()) / 2, y));                    
                } 

                vlayer.setZIndex(1001);
                map.addLayer(vlayer);
                
                // Add the extract results to the card.
                // But not the header itself.
                resultDiv = div().id("resultDiv").element();

                // Header row with some buttons.
                Button closeBtn = Button.createPrimary(Icons.ALL.clear())
                .circle()
                .setSize(ButtonSize.SMALL)
                .setButtonType(StyleType.DANGER)
                .setTooltip(messages.resultCloseTooltip())
                .style()
                .setMargin(px.of(3)).setBackgroundColor("#ef5350").get();
                
                closeBtn.addClickListener(event -> {
                   resetGui();
                });
                                
                Button expandBtn = Button.createPrimary(Icons.ALL.remove())
                .circle()
                .setSize(ButtonSize.SMALL)
                .setButtonType(StyleType.DANGER)
                .setTooltip(messages.resultMinimizeTooltip())                
                .style()
                .setMargin(px.of(3)).setBackgroundColor("#ef5350").get();
                                
                expandBtn.addClickListener(event -> {
                    if (resultCard.offsetHeight > headerRow.offsetHeight) {
                        expandBtn.setIcon(Icons.ALL.add());
                        expandBtn.setTooltip(messages.resultMaximizeTooltip());

                        resultCard.style.overflow = "hidden";
                        resultCard.style.height = CSSProperties.HeightUnionType.of(String.valueOf(headerRow.offsetHeight) + "px");
                        resultDiv.style.visibility = "hidden";
                        
                    } else {
                        expandBtn.setIcon(Icons.ALL.remove());
                        expandBtn.setTooltip(messages.resultMinimizeTooltip());

                        resultDiv.style.visibility = "visible";
                        resultCard.style.overflow = "auto";
                        resultCard.style.height = CSSProperties.HeightUnionType.of(RESULT_CARD_HEIGHT);
                    }
                });
                
                headerRow = div().id("resultHeaderRow").element(); 
                HTMLElement resultParcelSpan  = span().id("resultParcelSpan").textContent(messages.resultHeader(number)).element();
                
                HTMLElement resultButtonSpan = span().id("resultButtonSpan").element();
                resultButtonSpan.appendChild(expandBtn.element());
                resultButtonSpan.appendChild(closeBtn.element());
                
                headerRow.appendChild(resultParcelSpan);
                headerRow.appendChild(resultButtonSpan);
                resultCardContent.appendChild(headerRow);
                
                // Create the tabs objects. We need them later to add stuff.
                Tab tabCadastre = Tab.create(messages.tabTitleCadastralSurveying().toUpperCase())
                        .style()
                        .setWidth("33%")
                        .get();
                Tab tabLandRegister = Tab.create(messages.tabTitleLandRegister().toUpperCase())
                        .style()
                        .setWidth("33%")
                        .get();
                Tab tabPlr = Tab.create(messages.tabTitlePlr().toUpperCase())
                        .style()
                        .setWidth("33%")
                        .get();

                HTMLElement tabsPanel = TabsPanel.create().setId("resultTabs")
                    .setBackgroundColor(Color.RED_LIGHTEN_1)
                    .setColor(Color.WHITE)
                    .appendChild(tabCadastre)
                    .appendChild(tabLandRegister.appendChild(span().textContent("N/A")))
                    .appendChild(tabPlr)
                    .element();
           
                // Add cadastre content.
                HTMLElement cadastralSurveyingContent = addCadastralSurveyingContent(realEstateDPR);                
                tabCadastre.appendChild(cadastralSurveyingContent);
                
                // Add oereb content.
                HTMLElement oerebContent = addOerebContent(realEstateDPR);
                tabPlr.appendChild(oerebContent);

                // Hide wms layers only if ÖREB tab is active.
                tabsPanel.addEventListener("click", new EventListener() {
                    @Override
                    public void handleEvent(Event evt) { 
                        if (!tabPlr.isActive()) {
                            for (String layerId : oerebWmsLayers) {
                                Image wmsLayer = (Image) getMapLayerById(layerId);
                                wmsLayer.setVisible(false); 
                            }
                        } else {
                            List<AccordionPanel> panels = oerebInnerAccordion.getPanels();
                            for (AccordionPanel panel : panels) {
                                if (!panel.isHidden()) {
                                    Image wmsLayer = (Image) getMapLayerById(panel.getId());
                                    wmsLayer.setVisible(true);
                                }
                            }          
                        }
                    }
                });
                
                resultDiv.appendChild(tabsPanel);
                resultCardContent.appendChild(resultDiv);
                resultCard.style.height = CSSProperties.HeightUnionType.of(RESULT_CARD_HEIGHT);
                resultCard.style.overflow = "auto";
                resultCard.style.visibility = "visible";
            }
        });
    }

    private HTMLDivElement addOerebContent(RealEstateDPR realEstateDPR) {
        HTMLDivElement div = div().element();

        {
            Button pdfBtn = Button.create(Icons.ALL.file_pdf_box_outline_mdi())
                .setContent("PDF")
                .setBackground(Color.WHITE)
                .elevate(0)
                .style()
                .setColor("#ef5350")
                .setBorder("1px #ef5350 solid")
                .setPadding("5px 5px 5px 0px;")
                .setMinWidth(px.of(120)).get();
            
            pdfBtn.setTooltip(messages.resultPDFTooltip());
                    
            pdfBtn.addClickListener(event -> {
                Window.open(realEstateDPR.getOerebPdfExtractUrl(), "_blank", null);
            });
                       
            div.appendChild(pdfBtn.element());
        }

        // TODO: eventuell brauche ich das gar nicht. 
        // Drei einzelne Accordions?
        Accordion oerebAccordion = Accordion.create()
                .setHeaderBackground(Color.GREY_LIGHTEN_3)
                .style()
                .setMarginTop("20px")
                .get();
        
        div.appendChild(oerebAccordion.element());
        

        {
            AccordionPanel oerebAccordionPanelConcernedTheme = AccordionPanel.create(messages.concernedThemes());
            oerebAccordionPanelConcernedTheme.elevate(0);
            oerebAccordionPanelConcernedTheme.css("oerebAccordionPanelTheme");
            DominoElement<HTMLDivElement> oerebAccordionPanelConcernedThemeHeaderElement = oerebAccordionPanelConcernedTheme.getHeaderElement();
            oerebAccordionPanelConcernedThemeHeaderElement.addCss("oerebAccordionPanelHeaderElement");
            
            Chip chip = Chip.create().setValue(String.valueOf(realEstateDPR.getOerebConcernedThemes().size()))
                    .setColor(Color.GREY_LIGHTEN_1)
                    .style()
                    .setPadding("0px")
                    .setMargin("4px")
                    .setTextAlign("center").get();
            oerebAccordionPanelConcernedThemeHeaderElement.appendChild(span().css("oerebAccordionPanelHeaderChip").add(chip));
            
            oerebAccordion.appendChild(oerebAccordionPanelConcernedTheme);

            // TODO / FIXME
            // Event listener nur auf dem Header Element. Ansonsten schliesst es sich 
            // auch wenn ich auf einen Sub-Panel klicke.
            oerebAccordionPanelConcernedTheme.getHeaderElement().addEventListener(EventType.click, new EventListener() {
                @Override
                public void handleEvent(Event evt) {                    
                    if (!oerebAccordionPanelConcernedThemeState) {
                        oerebAccordionPanelConcernedTheme.show();
                        oerebAccordionPanelConcernedThemeState = true;
                        oerebAccordionPanelNotConcernedThemeState = false;
                        oerebAccordionPanelThemesWithoutDataState = false;
                        oerebAccordionPanelGeneralInformationState = false;
                        List<AccordionPanel> panels = oerebAccordion.getPanels();
                        for (AccordionPanel panel : panels) {
                            if(!panel.equals(oerebAccordionPanelConcernedTheme)) {
                                panel.hide();
                            }
                        }                          
                    } else {
                        oerebAccordionPanelConcernedTheme.hide();
                        oerebAccordionPanelConcernedThemeState = false;
                    }            
                }
            });  
            
            oerebInnerAccordion = Accordion.create()
                    .setId("oerebAccordionConcernedTheme")
                    .setHeaderBackground(Color.GREY_LIGHTEN_4);
            
            if (realEstateDPR.getOerebConcernedThemes().size() > 0) {
                for (ConcernedTheme theme : realEstateDPR.getOerebConcernedThemes()) {
                     
                    Image wmsLayer = createOerebWmsLayer(theme.getReferenceWMS());
                    map.addLayer(wmsLayer);

                    // Cannot use the code since all subthemes share the same code.
                    String layerId = theme.getReferenceWMS().getLayers();
                    oerebWmsLayers.add(layerId);                    
                    
                    innerOerebPanelStateMap.put(layerId, false);
                    
                    /*
                    * Wegen des unterschiedlichen Umgangs mit Subthemen wird
                    * es ein klein wenig kompliziert...
                    * Falls wir nur SO unterstützen, wäre es einfacher.
                    */
                    String panelTitle;
                    if (theme.getSubtheme() != null && !theme.getSubtheme().isEmpty()) {
                        if (!theme.getSubtheme().substring(0, 2).equals("ch")) {
                            panelTitle = theme.getName() + " - " + theme.getSubtheme();
                        } else {
                            panelTitle = theme.getName();
                        }
                    } else {
                        panelTitle = theme.getName();
                    }
                    
                    AccordionPanel accordionPanel = AccordionPanel.create(panelTitle).css("oerebAccordionPanelConcernedTheme");
                    accordionPanel.elevate(0);
                    accordionPanel.setId(layerId);
                    
                    accordionPanel.addShowListener(new ShowCompletedHandler() {
                        @Override
                        public void onShown() {
                            Image wmsLayer = (Image) getMapLayerById(layerId);
                            wmsLayer.setVisible(true); 
                        } 
                    });
                    
                    accordionPanel.addHideListener(new HideCompletedHandler() {
                        @Override
                        public void onHidden() {
                            Image wmsLayer = (Image) getMapLayerById(layerId);
                            wmsLayer.setVisible(false); 
                            innerOerebPanelStateMap.put(accordionPanel.getId(), false);
                        } 
                    });
                    
                    // Damit wird der Click Event nicht in das Tab Panel weitergereicht.
                    // Und somit wird nicht unnötiger Code ausgeführt.
                    accordionPanel.getHeaderElement().addEventListener(EventType.click, new EventListener() {
                        @Override
                        public void handleEvent(Event evt) {
                            //console.log("vorher: " + accordionPanel.getId() + " " + innerOerebPanelStateMap.get(accordionPanel.getId()));
                            if (innerOerebPanelStateMap.get(accordionPanel.getId())) {
                                innerOerebPanelStateMap.put(accordionPanel.getId(), false);
                                accordionPanel.hide();
                            } else {
                                innerOerebPanelStateMap.put(accordionPanel.getId(), true);
                                accordionPanel.show();
                            }
                            //console.log("nachher: " + accordionPanel.getId() + " " + innerOerebPanelStateMap.get(accordionPanel.getId()));

                            evt.stopPropagation();
                        }
                    });
                    
                    // Create a div element for the content.
                    HTMLDivElement contentDiv = div().css("oerebThemeContent").element();
                    
                    // Slider
                    int opacity;
                    if (Double.valueOf(theme.getReferenceWMS().getLayerOpacity()) != null && theme.getReferenceWMS().getLayerOpacity() != 0) {
                        opacity = Double.valueOf((theme.getReferenceWMS().getLayerOpacity() * 100)).intValue();
                    } else {
                        opacity = 60;
                    }
                    Slider slider = Slider.create(100).setMinValue(0).setValue(opacity).withoutThumb();
                    slider.addChangeHandler(handler -> {
                        wmsLayer.setOpacity(handler.intValue() / 100.0);
                    });
                                            
                    FlexLayout sliderRow = FlexLayout.create();
                    sliderRow.appendChild(FlexItem.create().setFlexGrow(0).style().setPaddingRight("20px").get().appendChild(span().textContent(messages.resultOpacity()).element()));
                    sliderRow.appendChild(FlexItem.create().setFlexGrow(1).appendChild(slider.element()));
                    contentDiv.appendChild(sliderRow.element());
                    
                    contentDiv.appendChild(div().css("fakeColumn").element());
                    contentDiv.appendChild(div().css(Styles.padding_10).element());
                    
                    // Eigentumsbeschränkungen
                    Row restrictionHeaderRow = Row.create();
                    restrictionHeaderRow.appendChild(Column.span6().style().setFontSize(SMALL_FONT_SIZE).get().setTextContent(messages.resultType()));
                    restrictionHeaderRow.appendChild(Column.span1().style().setFontSize(SMALL_FONT_SIZE).get().setTextContent(""));
                    restrictionHeaderRow.appendChild(Column.span3().style().setFontSize(SMALL_FONT_SIZE).setTextAlign("right").get().setTextContent(messages.resultShare()));
                    restrictionHeaderRow.appendChild(Column.span2().style().setFontSize(SMALL_FONT_SIZE).setTextAlign("right").get().setTextContent(messages.resultShareInPercent()));
                    contentDiv.appendChild(restrictionHeaderRow.element());

                    for (Restriction restriction : theme.getRestrictions()) {
                        if (restriction.getAreaShare() != null) {
                            contentDiv.appendChild(processRestrictionRow(restriction, GeometryType.POLYGON));
                        }

                        if (restriction.getLengthShare() != null) {
                            contentDiv.appendChild(processRestrictionRow(restriction, GeometryType.LINE));
                        }

                        if (restriction.getNrOfPoints() != null) {
                            contentDiv.appendChild(processRestrictionRow(restriction, GeometryType.POINT));
                        }

                        // TODO / FIXME (?) Falls kein Share mitgeliefert wird (z.B. ZH). Ist es mandatory?
                        if (restriction.getNrOfPoints() == null && restriction.getLengthShare() == null
                                && restriction.getAreaShare() == null) {
                            // do something
                        }
                    }

                    contentDiv.appendChild(div().css("fakeColumn").element());
                    contentDiv.appendChild(div().css(Styles.padding_10).element());

                    // Legende
                    if (theme.getLegendAtWeb() != null) {
                        String legendAtWeb = theme.getLegendAtWeb();

                        if (legendAtWeb.toLowerCase().endsWith(".xml") || legendAtWeb.toLowerCase().endsWith(".html") || legendAtWeb.toLowerCase().endsWith(".pdf")) {
                            // other cantons
                            HTMLElement legendLink = a().attr("class", "resultLink")
                                    .attr("href", legendAtWeb)
                                    .attr("target", "_blank")
                                    .add(TextNode.of(legendAtWeb)).element();
                            contentDiv.appendChild(div().add(legendLink).element());
                        } else {
                            HTMLElement legendLink = a()
                                    .attr("class", "resultLink")
                                    .add(TextNode.of(messages.resultShowLegend())).element();
                            contentDiv.appendChild(div().add(legendLink).element());

                            HTMLElement legendImage = img()
                                    .attr("src", theme.getLegendAtWeb())
                                    .attr("alt", "Legende").element();
                            legendImage.style.display = "none";
                            contentDiv.appendChild(legendImage);

                            legendLink.addEventListener("click", new EventListener() {
                                @Override
                                public void handleEvent(Event evt) {
                                    if (legendImage.style.display == "none") {
                                        legendImage.style.display = "block";
                                        legendLink.innerHTML = messages.resultHideLegend();
                                    } else {
                                        legendImage.style.display = "none";
                                        legendLink.innerHTML = messages.resultShowLegend();
                                    }
                                }
                            });
                        }
                        contentDiv.appendChild(div().css("fakeColumn").element());
                        contentDiv.appendChild(div().css(Styles.padding_10).element());
                    }
                    
                    // Rechtsvorschriften
                    contentDiv.appendChild(div().css("fontSemiBold").textContent(messages.legalProvisions()).element());
                    
                    for (Document legalProvision : theme.getLegalProvisions()) {
                        String linkName;
                        if (legalProvision.getOfficialTitle() != null) {
                            linkName = legalProvision.getOfficialTitle();
                        } else {
                            linkName = legalProvision.getTitle();
                        }
                        HTMLElement link = a().css("resultLink")
                                .attr("href", legalProvision.getTextAtWeb())
                                .attr("target", "_blank")
                                .add(TextNode.of(linkName)).element();
                        contentDiv.appendChild(div().add(link).element());

                        String additionalText = legalProvision.getTitle();
                        if (legalProvision.getOfficialNumber() != null) {
                            additionalText += " Nr. " + legalProvision.getOfficialNumber();
                        }
                        contentDiv.appendChild(div().add(TextNode.of(additionalText)).element());
                        contentDiv.appendChild(div().css(Styles.padding_5).element());
                    }
                    
                    // Gesetze
                    contentDiv.appendChild(div().css(Styles.padding_5).element());                    
                    contentDiv.appendChild(div().css("fontSemiBold").textContent(messages.laws()).element());

                    for (Document law : theme.getLaws()) {
                        String linkName;
                        if (law.getOfficialTitle() != null) {
                            linkName = law.getOfficialTitle();
                        } else {
                            linkName = law.getTitle();
                        }
                        
                        if (law.getAbbreviation() != null) {
                            linkName += " (" + law.getAbbreviation() + ")";
                        }
                        
                        if (law.getOfficialNumber() != null) {
                            linkName += ", " + law.getOfficialNumber();
                        }
                            
                        HTMLElement link = a().css("resultLink")
                                .attr("href", law.getTextAtWeb())
                                .attr("target", "_blank")
                                .add(TextNode.of(linkName)).element();
                        contentDiv.appendChild(div().add(link).element());
                        contentDiv.appendChild(div().css(Styles.padding_5).element());
                    }
                    
                    // Hinweise
                    if (theme.getHints().size() > 0) {
                        contentDiv.appendChild(div().css(Styles.padding_5).element());                    
                        contentDiv.appendChild(div().css("fontSemiBold").textContent(messages.hints()).element());
 
                        for (Document hint : theme.getHints()) {
                            String linkName = "";
                            if (hint.getOfficialTitle() != null) {
                                linkName = hint.getOfficialTitle();
                            } else {
                                linkName = hint.getTitle();
                            }
                            if (hint.getAbbreviation() != null) {
                                linkName += " (" + hint.getAbbreviation() + ")";
                            }
                            if (hint.getOfficialNumber() != null) {
                                linkName += ", " + hint.getOfficialNumber();
                            }
                            
                            HTMLElement link = a().css("resultLink")
                                    .attr("href", hint.getTextAtWeb())
                                    .attr("target", "_blank")
                                    .add(TextNode.of(linkName)).element();
                            contentDiv.appendChild(div().add(link).element());
                            contentDiv.appendChild(div().css(Styles.padding_5).element());
                        }
                    }
                    
                    contentDiv.appendChild(div().css("fakeColumn").element());
                    contentDiv.appendChild(div().css(Styles.padding_10).element());

                    // Zuständige Stelle(n)
                    contentDiv.appendChild(div().css("fontSemiBold").textContent(messages.responsibleOffice()).element());

                    for (Office office : theme.getResponsibleOffice()) {
                        HTMLElement link = a().css("resultLink")
                                .attr("href", office.getOfficeAtWeb())
                                .attr("target", "_blank")
                                .add(TextNode.of(office.getName())).element();
                        contentDiv.appendChild(div().add(link).element());
                        contentDiv.appendChild(div().css(Styles.padding_5).element());
                    }
                    accordionPanel.setContent(contentDiv);
                    oerebInnerAccordion.appendChild(accordionPanel);
                }
                oerebAccordionPanelConcernedTheme.appendChild(oerebInnerAccordion);
            }
        }

        {
            AccordionPanel oerebAccordionPanelNotConcernedTheme = AccordionPanel.create(messages.notConcernedThemes());
            oerebAccordionPanelNotConcernedTheme.css("oerebAccordionPanelTheme");
            oerebAccordionPanelNotConcernedTheme.elevate(0);            
            DominoElement<HTMLDivElement> oerebAccordionPanelNotConcernedThemeHeaderElement = oerebAccordionPanelNotConcernedTheme.getHeaderElement();
            oerebAccordionPanelNotConcernedThemeHeaderElement.addCss("oerebAccordionPanelHeaderElement");
            
            Chip chip = Chip.create().setValue(String.valueOf(realEstateDPR.getOerebNotConcernedThemes().size()))
                    .setColor(Color.GREY_LIGHTEN_1)
                    .style()
                    .setPadding("0px")
                    .setMargin("4px")
                    .setTextAlign("center").get();
            oerebAccordionPanelNotConcernedThemeHeaderElement.appendChild(span().css("oerebAccordionPanelHeaderChip").add(chip));
            
            List<String> notConcernedThemeItems = realEstateDPR.getOerebNotConcernedThemes().stream().map(NotConcernedTheme::getName).collect(Collectors.toList());
            ListGroup<String> listGroup = ListGroup.<String>create()
                    .setBordered(false)
                    .setItemRenderer((listGroup1, listItem) -> {
                        listItem.appendChild(div()
                                .css(Styles.padding_10)
                                .css("themeList")
                                .add(span().textContent(listItem.getValue())));                        
                    })
                    .setItems(notConcernedThemeItems);
            oerebAccordionPanelNotConcernedTheme.setContent(listGroup);
            oerebAccordion.appendChild(oerebAccordionPanelNotConcernedTheme);

            oerebAccordionPanelNotConcernedTheme.getHeaderElement().addEventListener(EventType.click, new EventListener() {
                @Override
                public void handleEvent(Event evt) {
                    if (!oerebAccordionPanelNotConcernedThemeState) {
                        oerebAccordionPanelNotConcernedTheme.show();
                        oerebAccordionPanelConcernedThemeState = false;
                        oerebAccordionPanelNotConcernedThemeState = true;
                        oerebAccordionPanelThemesWithoutDataState = false;
                        oerebAccordionPanelGeneralInformationState = false;
                        List<AccordionPanel> panels = oerebAccordion.getPanels();
                        for (AccordionPanel panel : panels) {
                            if(!panel.equals(oerebAccordionPanelNotConcernedTheme)) {
                                panel.hide();
                            }
                        }                        
                    } else {
                        oerebAccordionPanelNotConcernedTheme.hide();
                        oerebAccordionPanelNotConcernedThemeState = false;
                    }            
                }
            });            
        }

        {
            AccordionPanel oerebAccordionPanelThemesWithoutData = AccordionPanel.create(messages.themesWithoutData());
            oerebAccordionPanelThemesWithoutData.css("oerebAccordionPanelTheme");  
            oerebAccordionPanelThemesWithoutData.elevate(0);       
            DominoElement<HTMLDivElement> oerebAccordionPanelThemesWithoutDataElement = oerebAccordionPanelThemesWithoutData.getHeaderElement();
            oerebAccordionPanelThemesWithoutDataElement.addCss("oerebAccordionPanelHeaderElement");
            
            Chip chip = Chip.create().setValue(String.valueOf(realEstateDPR.getOerebThemesWithoutData().size()))
                    .setColor(Color.GREY_LIGHTEN_1)
                    .style()
                    .setPadding("0px")
                    .setMargin("4px")
                    .setTextAlign("center").get();
            oerebAccordionPanelThemesWithoutDataElement.appendChild(span().css("oerebAccordionPanelHeaderChip").add(chip));
            
            List<String> themesWithoutDataItems = realEstateDPR.getOerebThemesWithoutData().stream().map(ThemeWithoutData::getName).collect(Collectors.toList());
            ListGroup<String> listGroup = ListGroup.<String>create()
                    .setBordered(false)
                    .setItemRenderer((listGroup1, listItem) -> {
                        listItem.appendChild(div()
                                .css(Styles.padding_10)
                                .css("themeList")
                                .add(span().textContent(listItem.getValue())));                        
                    })
                    .setItems(themesWithoutDataItems);
            oerebAccordionPanelThemesWithoutData.setContent(listGroup);
            oerebAccordion.appendChild(oerebAccordionPanelThemesWithoutData);

            oerebAccordionPanelThemesWithoutData.getHeaderElement().addEventListener(EventType.click, new EventListener() {
                @Override
                public void handleEvent(Event evt) {
                    if (!oerebAccordionPanelThemesWithoutDataState) {
                        oerebAccordionPanelThemesWithoutData.show();
                        oerebAccordionPanelConcernedThemeState = false;
                        oerebAccordionPanelNotConcernedThemeState = false;
                        oerebAccordionPanelThemesWithoutDataState = true;
                        oerebAccordionPanelGeneralInformationState = false;
                        List<AccordionPanel> panels = oerebAccordion.getPanels();
                        for (AccordionPanel panel : panels) {
                            if(!panel.equals(oerebAccordionPanelThemesWithoutData)) {
                                panel.hide();
                            }
                        }
                    } else {
                        oerebAccordionPanelThemesWithoutData.hide();
                        oerebAccordionPanelThemesWithoutDataState = false;
                    }            
                }
            });            
        }
        
        {
            AccordionPanel oerebAccordionPanelGeneralInformation = AccordionPanel.create(messages.generalInformation());
            oerebAccordionPanelGeneralInformation.css("oerebAccordionPanelTheme");            
            oerebAccordionPanelGeneralInformation.elevate(0);
            DominoElement<HTMLDivElement> oerebAccordionPanelNotConcernedThemeHeaderElement = oerebAccordionPanelGeneralInformation.getHeaderElement();
            oerebAccordionPanelNotConcernedThemeHeaderElement.addCss("oerebAccordionPanelHeaderElement");
            
            Office office = realEstateDPR.getOerebCadastreAuthority();
            HTMLDivElement content = div().css(Styles.padding_10, "generalInformation")
                   .add(div().css("fontSemiBold").textContent("Katasterverantwortliche Stelle"))
                   .add(div().textContent(office.getName()))
                   .add(div().textContent(office.getStreet() + " " + office.getNumber()))
                   .add(div().textContent(office.getPostalCode() + " " + office.getCity())).element();
            
            oerebAccordionPanelGeneralInformation.setContent(content);
            oerebAccordion.appendChild(oerebAccordionPanelGeneralInformation);

            oerebAccordionPanelGeneralInformation.getHeaderElement().addEventListener(EventType.click, new EventListener() {
                @Override
                public void handleEvent(Event evt) {
                    if (!oerebAccordionPanelGeneralInformationState) {
                        oerebAccordionPanelGeneralInformation.show();
                        oerebAccordionPanelConcernedThemeState = false;
                        oerebAccordionPanelNotConcernedThemeState = false;
                        oerebAccordionPanelThemesWithoutDataState = false;
                        oerebAccordionPanelGeneralInformationState = true;
                        List<AccordionPanel> panels = oerebAccordion.getPanels();
                        for (AccordionPanel panel : panels) {
                            if(!panel.equals(oerebAccordionPanelGeneralInformation)) {
                                panel.hide();
                            }
                        }
                    } else {
                        oerebAccordionPanelGeneralInformation.hide();
                        oerebAccordionPanelGeneralInformationState = false;
                    }            
                }
            });            
        }
        return div;
    }

    private HTMLElement addCadastralSurveyingContent(RealEstateDPR realEstateDPR) {
        String number = realEstateDPR.getNumber();
        String identnd = realEstateDPR.getIdentND();
        String egrid = realEstateDPR.getEgrid();
        int area = realEstateDPR.getLandRegistryArea();
        String type = realEstateDPR.getRealEstateType();
        String municipality = realEstateDPR.getMunicipality();
        String subunitOfLandRegister = realEstateDPR.getSubunitOfLandRegister();
        List<String> localNames = realEstateDPR.getLocalNames();

        HTMLDivElement div = div().element();
        
        {
            Button pdfBtn = Button.create(Icons.ALL.file_pdf_box_outline_mdi())
                .setContent("PDF")
                .setBackground(Color.WHITE)
                .elevate(0)
                .style()
                .setColor("#ef5350")
                .setBorder("1px #ef5350 solid")
                .setPadding("5px 5px 5px 0px;")
                .setMinWidth(px.of(120)).get();
            
            pdfBtn.setTooltip(messages.resultPDFTooltip());
                    
            pdfBtn.addClickListener(event -> {
                Window.open(realEstateDPR.getCadastrePdfExtractUrl(), "_blank", null);
            });
                       
            div.appendChild(pdfBtn.element());
        }
        
        div.appendChild(addCadastralSurveyingContentKeyValue("&nbsp;", "&nbsp;"));
        div.appendChild(addCadastralSurveyingContentKeyValue("E-GRID:", egrid));
        div.appendChild(addCadastralSurveyingContentKeyValue("NBIdent:", new String(identnd == null ? "&ndash;" : identnd)));
        div.appendChild(addCadastralSurveyingContentKeyValue("&nbsp;", "&nbsp;"));
        div.appendChild(addCadastralSurveyingContentKeyValue("Grundstücksart:", type));
        div.appendChild(addCadastralSurveyingContentKeyValue("Grundstücksfläche:", fmtDefault.format(area) + " m<sup>2</sup>"));
        div.appendChild(addCadastralSurveyingContentKeyValue("&nbsp;", "&nbsp;"));        
        div.appendChild(addCadastralSurveyingContentKeyValue("Gemeinde:", municipality));
        div.appendChild(addCadastralSurveyingContentKeyValue("Grundbuch:",  new String(subunitOfLandRegister == null ? "&ndash;" : subunitOfLandRegister)));
        div.appendChild(addCadastralSurveyingContentKeyValue("&nbsp;", "&nbsp;"));
        div.appendChild(addCadastralSurveyingContentKeyValue("Flurnamen:", String.join(", ", localNames)));
        
        div.appendChild(div().css("fakeColumn").element());
        div.appendChild(addCadastralSurveyingContentBuildings(realEstateDPR.getBuildings()));

        div.appendChild(div().css("fakeColumn").element());
        div.appendChild(addCadastralSurveyingContentLandCoverShares(realEstateDPR.getLandCoverShares()));

        div.appendChild(div().css("fakeColumn").element());
        div.appendChild(addCadastralSurveyingContentOffices(realEstateDPR));

        return div;
    }

    public final class MapSingleClickListener implements ol.event.EventListener<MapBrowserEvent> {
        @Override
        public void onEvent(MapBrowserEvent event) {
            loader.start();
            Coordinate coordinate = event.getCoordinate();
            sendCoordinateToServer(coordinate.toStringXY(3), event);
        }
    }

    private HTMLElement processRestrictionRow(Restriction restriction, GeometryType type) {
        Row row = Row.create().css("restrictionRow");
        
        row.appendChild(Column.span6().setTextContent(restriction.getInformation()));
        
        // TODO
        // Testen, ob es mit base64 funktionert.
        // Anhand des base64 Strings sieht man, ob es png ist oder was anderes. 
        String srcAttr;
        if (restriction.getSymbol() != null) {
            srcAttr = "data:image/png;base64, " + restriction.getSymbol();
        } else {
            srcAttr = restriction.getSymbolRef();
        }
        HTMLElement symbol = img().attr("src", srcAttr)
                .attr("alt", "Symbol " + restriction.getInformation())
                .attr("width", "30px")
                .style("border: 1px solid black").element();

        row.appendChild(Column.span1().appendChild(symbol));

        if (type == GeometryType.POLYGON) {
            Column col = Column.span3().style().setTextAlign("right").get();
            if (restriction.getAreaShare() < 0.1) {
                col.appendChild(span().innerHtml(SafeHtmlUtils.fromTrustedString("< 0.1 m<sup>2</sup>")));
            } else {
                col.appendChild(span().innerHtml(SafeHtmlUtils.fromTrustedString(fmtDefault.format(restriction.getAreaShare()) + " m<sup>2</sup>")));
            }
            row.appendChild(col);
        }

        if (type == GeometryType.POLYGON && restriction.getPartInPercent() != null) {
            Column col = Column.span2().style().setTextAlign("right").get();
            if (restriction.getPartInPercent() < 0.1) {
                col.appendChild(span().textContent("< 0.1"));
            } else {
                col.appendChild(span().textContent(fmtPercent.format(restriction.getPartInPercent())));
            }
            row.appendChild(col);
        }

        if (type == GeometryType.LINE) {
            Column col = Column.span3().style().setTextAlign("right").get();            
            if (restriction.getLengthShare() < 0.1) {
                col.appendChild(span().textContent("< 0.1 m"));
            } else {
                col.appendChild(span().textContent(fmtDefault.format(restriction.getLengthShare()) + " m"));

            }
            row.appendChild(col);            
        }

        if (type == GeometryType.POINT) {
            Column col = Column.span3().style().setTextAlign("right").get();
            String str = fmtDefault.format(restriction.getNrOfPoints()) + " " + messages.plrNrOfPoints();
            col.appendChild(span().textContent(str));
            row.appendChild(col);                        
        }
        return row.element();        
    }

    // Creates an ol3 wms layer.
    private Image createOerebWmsLayer(ReferenceWMS referenceWms) {
        ImageWmsParams imageWMSParams = OLFactory.createOptions();
        imageWMSParams.setLayers(referenceWms.getLayers());

        ImageWmsOptions imageWMSOptions = OLFactory.createOptions();

        String baseUrl = referenceWms.getBaseUrl();

        imageWMSOptions.setUrl(baseUrl);
        imageWMSOptions.setParams(imageWMSParams);
        imageWMSOptions.setRatio(1.5f);

        ImageWms imageWMSSource = new ImageWms(imageWMSOptions);

        LayerOptions layerOptions = OLFactory.createOptions();
        layerOptions.setSource(imageWMSSource);

        Image wmsLayer = new Image(layerOptions);
        wmsLayer.set(ID_ATTR_NAME, referenceWms.getLayers());
        wmsLayer.setVisible(false);

        // FIXME: ZH is always 0 which is completely transparent.
        if (referenceWms.getLayerOpacity() == 0) {
            wmsLayer.setOpacity(0.6);
        } else {
            wmsLayer.setOpacity(referenceWms.getLayerOpacity());
        }
        // Kann leider nicht wirklich angewendet werden, da die 
        // Hintergrundkarten nicht zwingend transparent sind.
        // wmsLayer.setZIndex(referenceWms.getLayerIndex());
        return wmsLayer;
    }

    // Add a key / value to cadastral surveying result column
    private HTMLElement addCadastralSurveyingContentKeyValue(String key, String value) {
        HTMLDivElement row = div().element();
        HTMLElement keyElement = span().css("cadastralSurveyingInfoKey").innerHtml(SafeHtmlUtils.fromTrustedString(key)).element();
        HTMLElement valueElement = span().innerHtml(SafeHtmlUtils.fromTrustedString(value)).element();
        
        row.appendChild(keyElement);
        row.appendChild(valueElement);
        return row;        
    }
    
    private HTMLElement addCadastralSurveyingContentBuildings(ArrayList<Building> buildings) {
        HTMLDivElement div = div().element();
        
        Row buildingTitle = Row.create().appendChild(span().css("cadastralSurveyingInfoKey").style("padding-top:15px;").textContent("Gebäude"));
        div.appendChild(buildingTitle.element());
        
        Row buildingHeaderRow = Row.create();
        buildingHeaderRow.appendChild(Column.span3().style().setFontSize(SMALL_FONT_SIZE).get().setTextContent("EGID"));
        buildingHeaderRow.appendChild(Column.span2().style().setFontSize(SMALL_FONT_SIZE).setTextAlign("right").get().setTextContent("Fläche"));
//        buildingHeaderRow.appendChild(Column.span2().style().setFontSize(SMALL_FONT_SIZE).setTextAlign("left").get().setTextContent("projektiert"));
//        buildingHeaderRow.appendChild(Column.span2().style().setFontSize(SMALL_FONT_SIZE).setTextAlign("left").get().setTextContent("unterirdisch"));
        buildingHeaderRow.appendChild(Column.span7().style().setFontSize(SMALL_FONT_SIZE).setTextAlign("left").get().setTextContent("Adressen"));
        div.appendChild(buildingHeaderRow.element());
        
        for (Building building: buildings) {
            Row buildingRow = Row.create().css("buildingRow");
            if (building.getEgid() != 0) {
                buildingRow.appendChild(Column.span3().setTextContent(String.valueOf(building.getEgid())));
            } else {
                buildingRow.appendChild(Column.span3().appendChild(span().innerHtml(SafeHtmlUtils.fromTrustedString("&ndash;"))));
            }
            buildingRow.appendChild(Column.span2().style().setTextAlign("right").get().appendChild(span().innerHtml(SafeHtmlUtils.fromTrustedString(fmtSquareMeter.format(building.getArea()) + " m<sup>2</sup>"))));

            List<BuildingEntry> entries = building.getBuildingEntries();
            String allAddressesString = "";
            for (int i=0; i<entries.size(); i++) {
                PostalAddress address = entries.get(i).getPostalAddress();
                if (address != null) {
                    String addressString = address.getStreet() + " " + address.getNumber() + ", " + address.getPostalCode() + " " + address.getCity();
                    if (i!=0) {
                        allAddressesString += "<br>" + addressString;
                    } else {
                        allAddressesString += addressString;
                    }
                }
            }
            buildingRow.appendChild(Column.span7().appendChild(span().innerHtml(SafeHtmlUtils.fromTrustedString(allAddressesString))));
            div.appendChild(buildingRow.element()); 
        }
        return div;
    }
    
    private HTMLElement addCadastralSurveyingContentLandCoverShares(ArrayList<LandCoverShare> landCoverShares) {
        HTMLDivElement div = div().element();

        Row landCoverTitle = Row.create().appendChild(span().css("cadastralSurveyingInfoKey").style("padding-top:15px;").textContent("Bodenbedeckung"));
        div.appendChild(landCoverTitle.element());
        
        double landCoverShareSum = 0.0;
        for (LandCoverShare landCoverShare : landCoverShares) {
            Row landCoverShareRow = Row.create().css("buildingRow");
            landCoverShareRow.appendChild(Column.span4().setTextContent(landCoverShare.getTypeDescription()));
            landCoverShareRow.appendChild(Column.span3().style().setTextAlign("right").get().appendChild(span().innerHtml(SafeHtmlUtils.fromTrustedString(fmtSquareMeter.format(landCoverShare.getArea()) + " m<sup>2</sup>"))));
            div.appendChild(landCoverShareRow.element());
            
            landCoverShareSum += landCoverShare.getArea();
        }
        Row landCoverShareSumRow = Row.create().css("buildingRow");
        landCoverShareSumRow.appendChild(Column.span4().appendChild(span().innerHtml(SafeHtmlUtils.fromTrustedString("<i>Total</i>"))));
        landCoverShareSumRow.appendChild(Column.span3().style().setTextAlign("right").get().appendChild(span().innerHtml(SafeHtmlUtils.fromTrustedString("<i>"+fmtSquareMeter.format(landCoverShareSum) + " m<sup>2</sup></i>"))));
        div.appendChild(landCoverShareSumRow.element());
        
        return div;
    }

    private HTMLElement addCadastralSurveyingContentOffices(RealEstateDPR realEstateDPR) {
        HTMLDivElement div = div().element();
        
        {
            Row landRegisterOfficeTitle = Row.create().appendChild(span().css("cadastralSurveyingInfoKey").style("padding-top:15px;").textContent("Grundbuchamt"));
            div.appendChild(landRegisterOfficeTitle.element());

            Row addressRow = Row.create().css("cadastreAddressRow");
            Office landRegisterOffice = realEstateDPR.getCadastreLandRegisterOffice();
            String name = landRegisterOffice.getName();
            String street = landRegisterOffice.getStreet();
            String number = landRegisterOffice.getNumber();
            String postalCode = landRegisterOffice.getPostalCode();
            String city = landRegisterOffice.getCity();
            String web = landRegisterOffice.getOfficeAtWeb();
            String email = landRegisterOffice.getEmail();
            
            String addressHtml = ""
                    + name + "<br>"
                    + street + " " + number + "<br>"
                    + postalCode + " " + city + "<br>"
                    + "<a class='resultLink' href=" + web + " target='_blank'>"+web+"</a><br>"
                    + "<a class='resultLink' mailto=" + email + ">"+email+"</a>";
            
            addressRow.appendChild(Column.span12().style().setTextAlign("left").get().appendChild(span().innerHtml(SafeHtmlUtils.fromTrustedString(addressHtml))));
            div.appendChild(addressRow.element());
        }
        
        {
            Row surveyorOfficeTitle = Row.create().appendChild(span().css("cadastralSurveyingInfoKey").style("padding-top:15px;").textContent("Nachführungsgeometer"));
            div.appendChild(surveyorOfficeTitle.element());

            Row addressRow = Row.create().css("cadastreAddressRow");
            Office surveyorOffice = realEstateDPR.getCadastreSurveyorOffice();
            String firstName = surveyorOffice.getFirstName();
            String lastName = surveyorOffice.getLastName();
            String name = surveyorOffice.getName();
            String line1 = surveyorOffice.getLine1();
            String street = surveyorOffice.getStreet();
            String number = surveyorOffice.getNumber();
            String postalCode = surveyorOffice.getPostalCode();
            String city = surveyorOffice.getCity();
            String web = surveyorOffice.getOfficeAtWeb();
            String email = surveyorOffice.getEmail();
            
            String addressHtml = ""
                    + firstName + " " + lastName + "<br>"
                    + name + "<br>";
            if (line1 != null) {
                addressHtml += line1 + "<br>";
            }
            addressHtml += ""
                    + street + " " + number + "<br>"
                    + postalCode + " " + city + "<br>"
                    + "<a class='resultLink' href=" + web + " target='_blank'>"+web+"</a><br>"
                    + "<a class='resultLink' mailto=" + email + ">"+email+"</a>";
            
            addressRow.appendChild(Column.span12().style().setTextAlign("left").get().appendChild(span().innerHtml(SafeHtmlUtils.fromTrustedString(addressHtml))));
            div.appendChild(addressRow.element());
        }

        return div;
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
    
    private void removeRealEstateVectorLayer() {
        Base vlayer = getMapLayerById(REAL_ESTATE_VECTOR_LAYER_ID);
        map.removeLayer(vlayer);
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

    // Make a html link from a string.
    private String makeHtmlLink(String text) {
        String html = "<a class='resultLink' href='" + text + "' target='_blank'>" + text + "</a>";
        return html;
    }

    // Update the URL in the browser without reloading the page.
    private static native void updateURLWithoutReloading(String newUrl) /*-{
        $wnd.history.pushState(newUrl, "", newUrl);
    }-*/;
}
