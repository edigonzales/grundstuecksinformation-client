package ch.so.agi.grundstuecksinformation.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.dominokit.domino.ui.badges.Badge;
import org.dominokit.domino.ui.button.Button;
import org.dominokit.domino.ui.button.ButtonSize;
import org.dominokit.domino.ui.cards.Card;
import org.dominokit.domino.ui.chips.Chip;
import org.dominokit.domino.ui.collapsible.Accordion;
import org.dominokit.domino.ui.collapsible.AccordionPanel;
import org.dominokit.domino.ui.collapsible.Collapsible;
import org.dominokit.domino.ui.collapsible.Collapsible.ShowCompletedHandler;
import org.dominokit.domino.ui.dialogs.MessageDialog;
import org.dominokit.domino.ui.dropdown.DropDownMenu;
import org.dominokit.domino.ui.dropdown.DropDownPosition;
import org.dominokit.domino.ui.forms.SuggestBox;
import org.dominokit.domino.ui.forms.SuggestBoxStore;
import org.dominokit.domino.ui.forms.SuggestItem;
import org.dominokit.domino.ui.grid.Column;
import org.dominokit.domino.ui.grid.Column.Span;
import org.dominokit.domino.ui.grid.Row;
import org.dominokit.domino.ui.grid.flex.FlexItem;
import org.dominokit.domino.ui.grid.flex.FlexLayout;
import org.dominokit.domino.ui.icons.Icons;
import org.dominokit.domino.ui.lists.ListGroup;
import org.dominokit.domino.ui.loaders.Loader;
import org.dominokit.domino.ui.loaders.LoaderEffect;
import org.dominokit.domino.ui.notifications.Notification;
import org.dominokit.domino.ui.style.ColorScheme;
import org.dominokit.domino.ui.style.Elevation;
import org.dominokit.domino.ui.style.StyleType;
import org.dominokit.domino.ui.style.Styles;
import org.dominokit.domino.ui.tabs.Tab;
import org.dominokit.domino.ui.tabs.TabsPanel;
import org.dominokit.domino.ui.themes.Theme;
import org.dominokit.domino.ui.utils.DominoElement;
import org.dominokit.domino.ui.utils.HasSelectionHandler;
import org.dominokit.domino.ui.utils.HasSelectionHandler.SelectionHandler;
import org.dominokit.domino.ui.utils.TextNode;
import org.gwtproject.safehtml.shared.SafeHtml;
import org.gwtproject.safehtml.shared.SafeHtmlBuilder;
import org.gwtproject.safehtml.shared.SafeHtmlUtils;
import org.jboss.elemento.EventType;
import org.jboss.elemento.HtmlContentBuilder;
import org.dominokit.domino.ui.forms.SuggestBox.DropDownPositionDown;
import org.dominokit.domino.ui.forms.SuggestBox.PopupPositionTopDown;
import org.dominokit.domino.ui.style.Color;
import static org.dominokit.domino.ui.style.Unit.px;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.FontStyle;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.event.logical.shared.SelectionEvent;
//import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
//import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.safehtml.shared.UriUtils;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;

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
import ch.so.agi.grundstuecksinformation.shared.models.ConcernedTheme;
import ch.so.agi.grundstuecksinformation.shared.models.Document;
import ch.so.agi.grundstuecksinformation.shared.models.Egrid;
import ch.so.agi.grundstuecksinformation.shared.models.NotConcernedTheme;
import ch.so.agi.grundstuecksinformation.shared.models.Office;
import ch.so.agi.grundstuecksinformation.shared.models.RealEstateDPR;
import ch.so.agi.grundstuecksinformation.shared.models.ReferenceWMS;
import ch.so.agi.grundstuecksinformation.shared.models.Restriction;
import ch.so.agi.grundstuecksinformation.shared.models.ThemeWithoutData;
import elemental2.core.Global;
import elemental2.core.JsArray;
import elemental2.core.JsMap;
import elemental2.core.JsObject;
import elemental2.core.JsSet;
import elemental2.core.JsString;
import elemental2.core.JsNumber;
import elemental2.dom.CSSProperties;
import elemental2.dom.DataTransfer;
import elemental2.dom.DomGlobal;
import elemental2.dom.DragEvent;
import elemental2.dom.Element;
import elemental2.dom.Event;
import elemental2.dom.EventListener;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLDocument;
import elemental2.dom.HTMLElement;
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

import static elemental2.dom.DomGlobal.fetch;
import static elemental2.dom.DomGlobal.console;
import elemental2.dom.Response;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;

import static org.jboss.elemento.Elements.*;
import static org.jboss.elemento.EventType.*;

//import gwt.material.design.addins.client.autocomplete.MaterialAutoComplete;
//import gwt.material.design.addins.client.autocomplete.constants.AutocompleteType;
//import gwt.material.design.addins.client.window.MaterialWindow;
//import gwt.material.design.client.constants.ButtonSize;
//import gwt.material.design.client.constants.ButtonType;
//import gwt.material.design.client.constants.Color;
//import gwt.material.design.client.constants.Display;
//import gwt.material.design.client.constants.IconType;
//import gwt.material.design.client.constants.Position;
//import gwt.material.design.client.constants.TextAlign;
//import gwt.material.design.client.constants.WavesType;
//import gwt.material.design.client.ui.MaterialButton;
//import gwt.material.design.client.ui.MaterialCard;
//import gwt.material.design.client.ui.MaterialCardContent;
//import gwt.material.design.client.ui.MaterialChip;
//import gwt.material.design.client.ui.MaterialCollapsible;
//import gwt.material.design.client.ui.MaterialCollapsibleBody;
//import gwt.material.design.client.ui.MaterialCollapsibleHeader;
//import gwt.material.design.client.ui.MaterialCollapsibleItem;
//import gwt.material.design.client.ui.MaterialCollection;
//import gwt.material.design.client.ui.MaterialCollectionItem;
//import gwt.material.design.client.ui.MaterialColumn;
//import gwt.material.design.client.ui.MaterialIcon;
//import gwt.material.design.client.ui.MaterialLabel;
//import gwt.material.design.client.ui.MaterialLink;
//import gwt.material.design.client.ui.MaterialLoader;
//import gwt.material.design.client.ui.MaterialPanel;
//import gwt.material.design.client.ui.MaterialRange;
//import gwt.material.design.client.ui.MaterialRow;
//import gwt.material.design.client.ui.MaterialTab;
//import gwt.material.design.client.ui.MaterialTabItem;
//import gwt.material.design.client.ui.MaterialToast;
//import gwt.material.design.client.ui.html.Div;
//import gwt.material.design.client.ui.html.Span;

public class AppEntryPoint implements EntryPoint {
    private MainMessages messages = GWT.create(MainMessages.class);
    private final SettingsServiceAsync settingsService = GWT.create(SettingsService.class);
    private final EgridServiceAsync egridService = GWT.create(EgridService.class);
    private final ExtractServiceAsync extractService = GWT.create(ExtractService.class);

    // Settings
    private String MY_VAR;
    private String SEARCH_SERVICE_URL = "https://api3.geo.admin.ch/rest/services/api/SearchServer?sr=2056&limit=15&type=locations&origins=address,parcel&searchText=";

    private String SUB_HEADER_FONT_SIZE = "16px";
    private String BODY_FONT_SIZE = "14px";
    private String SMALL_FONT_SIZE = "12px";

    private String RESULT_CARD_HEIGHT = "calc(100% - 215px)";

    private String ID_ATTR_NAME = "id";
    private String REAL_ESTATE_VECTOR_LAYER_ID = "real_estate_vector_layer";
    private String REAL_ESTATE_VECTOR_FEATURE_ID = "real_estate_fid";

    private NumberFormat fmtDefault = NumberFormat.getDecimalFormat();
    private NumberFormat fmtPercent = NumberFormat.getFormat("#0.0");

    private Loader loader;
    private Map map;
    private HTMLElement mapDiv;
    private Overlay realEstatePopup;
//    private MaterialCard searchCard;  
//    private MaterialAutoComplete autocomplete;    
    private HTMLElement resultCard;    
    private HTMLElement resultCardContent;    
    private HTMLDivElement headerRow;
//    private MaterialTab resultTab;
    private HTMLElement resultDiv;
//    private MaterialColumn cadastralSurveyingResultColumn;
//    private MaterialColumn oerebResultColumn;
//    private String expandedOerebLayerId;
//    private MaterialCollapsible oerebCollapsibleConcernedTheme;
//    private MaterialCollapsible oerebInnerCollapsibleConcernedTheme;
//    private MaterialCollapsible oerebCollapsibleNotConcernedTheme;
//    private MaterialCollapsible oerebCollapsibleThemesWithoutData;
//    private MaterialCollapsible oerebCollapsibleGeneralInformation;

    private boolean oerebAccordionPanelConcernedThemeState = false;
    private boolean oerebAccordionPanelNotConcernedThemeState = false;
    private boolean oerebAccordionPanelThemesWithoutDataState = false;
    private boolean oerebAccordionPanelGeneralInformationState = false;
    
    private String identifyRequestTemplate = "https://api3.geo.admin.ch/rest/services/all/MapServer/identify?geometry=%s,%s&geometryFormat=geojson&geometryType=esriGeometryPoint&imageDisplay=1780,772,96&lang=de&layers=all:ch.kantone.cadastralwebmap-farbe&limit=10&mapExtent=%s,%s,%s,%s&returnGeometry=true&sr=2056&tolerance=10";

    // List with all oereb wms layer that will be added to the ol3 map
    // and removed from it afterwards.
    private ArrayList<String> oerebWmsLayers = new ArrayList<String>();

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
                init();
            }
        });
    }
    
    @SuppressWarnings("unchecked")
    private void init() {
        GWT.log("Rock the Casbah!");
        
        loader = Loader.create((HTMLElement) DomGlobal.document.body, LoaderEffect.ROTATION).setLoadingText(null);

//      Theme theme = new Theme(ColorScheme.BLACK);
//      theme.apply();
        
        mapDiv = div().id("map").element();
        body().add(mapDiv);

        HTMLElement searchCard = div().id("searchCard").element();
//      searchCard.style.position = "absolute";
//      searchCard.style.top = "200px";
//      searchCard.style.left = "200px";
//      searchCard.appendChild(span().add("Hallo Welt.").element());
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
                
                // fetch(url, init) -> https://www.javadoc.io/doc/com.google.elemental2/elemental2-dom/1.0.0-RC1/elemental2/dom/RequestInit.html
                // abort -> https://github.com/react4j/react4j-flux-challenge/blob/b9b28250fd3f954c690f874605f67e2a24a7274d/src/main/java/react4j/sithtracker/model/SithPlaceholder.java
                DomGlobal.fetch(SEARCH_SERVICE_URL + value.trim().toLowerCase())
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
                        JsPropertyMap<?> feature = Js.cast(results.getAt(i));
                        JsPropertyMap<?> attrs = Js.cast(feature.get("attrs"));

                        SearchResult searchResult = new SearchResult();
                        searchResult.setLabel(((JsString) attrs.get("label")).normalize());
                        searchResult.setOrigin(((JsString) attrs.get("origin")).normalize());
                        searchResult.setBbox(((JsString) attrs.get("geom_st_box2d")).normalize());
                        searchResult.setEasting(((JsNumber) attrs.get("y")).valueOf());
                        searchResult.setNorthing(((JsNumber) attrs.get("x")).valueOf());
                        
//                      // TODO icon type depending on address and parcel ?
                        SuggestItem<SearchResult> suggestItem = SuggestItem.create(searchResult, searchResult.getLabel(),
                                Icons.ALL.place());
                        suggestItems.add(suggestItem);
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
        DropDownMenu suggestionsMenu = suggestBox.getSuggestionsMenu();
        suggestionsMenu.setPosition(new DropDownPositionDown());
        
        suggestBox.addSelectionHandler(new SelectionHandler() {
            @Override
            public void onSelection(Object value) {
                loader.stop();
                resetGui();

                SuggestItem<SearchResult> item = (SuggestItem<SearchResult>) value;
                SearchResult result = (SearchResult) item.getValue();
                console.log(result.getLabel());
                
                String[] coords = result.getBbox().substring(4,result.getBbox().length()-1).split(",");
                String[] coordLL = coords[0].split(" ");
                String[] coordUR = coords[1].split(" ");
                Extent extent = new Extent(Double.valueOf(coordLL[0]).doubleValue(), Double.valueOf(coordLL[1]).doubleValue(), 
                Double.valueOf(coordUR[0]).doubleValue(), Double.valueOf(coordUR[1]).doubleValue());
                
                double easting = Double.valueOf(result.getEasting()).doubleValue();
                double northing = Double.valueOf(result.getNorthing()).doubleValue();
                
                Coordinate coordinate = new Coordinate(easting, northing);
                sendCoordinateToServer(coordinate.toStringXY(3), null);
                
                // TODO: remove focus
                // -> Tried a lot but failed. Ask the authors.
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
//        if (Window.Location.getParameter("egrid") != null) {
//            String egrid = Window.Location.getParameter("egrid").toString();
//            MaterialLoader.loading(true);
//            resetGui();
//            Egrid egridObj = new Egrid();
//            egridObj.setEgrid(egrid);
//            sendEgridToServer(egridObj);
//        }
    }

    private void resetGui() {
        removeOerebWmsLayers();

//        expandedOerebLayerId = null;
//
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

                    HTMLElement closeButton = span().add(Icons.ALL.close_mdi()).element();                    
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
                        
                        String label = new String(messages.realEstateAbbreviation() + ": " + number + " (unknown type...)");
                        HTMLDivElement row = div().id(egrid).css("realEstatePopupRow")
                                .add(span().textContent(label)).element();
                        
                        bind(row, mouseover, event -> {
                            row.style.backgroundColor = "#efefef";
                            row.style.cursor = "pointer";
                            // Since there is no geometry, we cannot highlight the real estate.
//                          ol.layer.Vector vlayer = createRealEstateVectorLayer(feature.getGeometry());
//                          map.addLayer(vlayer);
                        });

                        bind(row, mouseout, event -> {
                            row.style.backgroundColor = "white";
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
                // TODO:
//                updateURLWithoutReloading(newUrl);
                
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
                .setButtonType(StyleType.PRIMARY)
                .setTooltip(messages.resultCloseTooltip())
                .style()
                .setMargin(px.of(3)).get();
                
                closeBtn.addClickListener(event -> {
                   resetGui();
                });
                                
                Button expandBtn = Button.createPrimary(Icons.ALL.remove())
                .circle()
                .setSize(ButtonSize.SMALL)
                .setButtonType(StyleType.PRIMARY)
                .setTooltip(messages.resultMinimizeTooltip())                
                .style()
                .setMargin(px.of(3)).get();
                                
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
                    .setBackgroundColor(Color.WHITE)
                    .setColor(Color.BLUE)
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

                
                
                tabPlr.addClickListener(event -> {
                    console.log("oereb tab clicked");
                    console.log(tabPlr.isActive());
                });
                
                
                
                resultDiv.appendChild(tabsPanel);
                resultCardContent.appendChild(resultDiv);
                resultCard.style.height = CSSProperties.HeightUnionType.of(RESULT_CARD_HEIGHT);
                resultCard.style.overflow = "auto";
                resultCard.style.visibility = "visible";

                
                
//                MaterialRow tabRow = new MaterialRow();
//                tabRow.setId("tabRow");
//
//                MaterialColumn tabHeaderColumn = new MaterialColumn();
//                tabHeaderColumn.setId("tabHeaderColumn");
//                tabHeaderColumn.setGrid("s12");
//
//                resultTab = new MaterialTab();
//                resultTab.setId("resultTab");
//                resultTab.setShadow(1);
//                // #e8c432
//                // #aed634
//                // #52b1a7
//                // #75a5d4
//                resultTab.setBackgroundColor(Color.RED_LIGHTEN_1);
//                resultTab.setIndicatorColor(Color.WHITE);
//
//                // Cadastral Surveying Tab
//                MaterialTabItem cadastralSurveyingHeaderTabItem = new MaterialTabItem();
//                cadastralSurveyingHeaderTabItem.setWaves(WavesType.LIGHT);
//                cadastralSurveyingHeaderTabItem.setGrid("s4");
//
//                MaterialLink cadastralSurveyingHeaderTabLink = new MaterialLink();
//                cadastralSurveyingHeaderTabLink.setText(messages.tabTitleCadastralSurveying());
//                cadastralSurveyingHeaderTabLink.setHref("#cadastralSurveyingResultColumn");
//                cadastralSurveyingHeaderTabLink.setTextColor(Color.WHITE);
//                cadastralSurveyingHeaderTabItem.add(cadastralSurveyingHeaderTabLink);
//                resultTab.add(cadastralSurveyingHeaderTabItem);
//
//                // Land Register Tab
//                MaterialTabItem grundbuchHeaderTabItem = new MaterialTabItem();
//                grundbuchHeaderTabItem.setWaves(WavesType.LIGHT);
//                grundbuchHeaderTabItem.setGrid("s4");
//                grundbuchHeaderTabItem.setEnabled(false);
//
//                MaterialLink grundbuchHeaderTabLink = new MaterialLink();
//                grundbuchHeaderTabLink.setText(messages.tabTitleLandRegister());
//                grundbuchHeaderTabLink.setHref("#tab2");
//                grundbuchHeaderTabLink.setTextColor(Color.WHITE);
//                grundbuchHeaderTabItem.add(grundbuchHeaderTabLink);
//                resultTab.add(grundbuchHeaderTabItem);
//
//                // OEREB Tab
//                MaterialTabItem oerebHeaderTabItem = new MaterialTabItem();
//                oerebHeaderTabItem.setWaves(WavesType.LIGHT);
//                oerebHeaderTabItem.setGrid("s4");
//
//                MaterialLink oerebHeaderTabLink = new MaterialLink();
//                oerebHeaderTabLink.setText(messages.tabTitlePlr());
//                oerebHeaderTabLink.setHref("#oerebResultColumn");
//                oerebHeaderTabLink.setTextColor(Color.WHITE);
//                oerebHeaderTabItem.add(oerebHeaderTabLink);
//                resultTab.add(oerebHeaderTabItem);
//
//                tabHeaderColumn.add(resultTab);
//                tabRow.add(tabHeaderColumn);
//
//                // Add specific cadastre content.
//                cadastralSurveyingResultColumn = new MaterialColumn();
//                cadastralSurveyingResultColumn.setId("cadastralSurveyingResultColumn");
//                cadastralSurveyingResultColumn.addStyleName("resultColumn");
//                cadastralSurveyingResultColumn.setGrid("s12");
//                addCadastralSurveyingContent(realEstateDPR);
//                tabRow.add(cadastralSurveyingResultColumn);
//
//                // Add specific oereb content.
//                oerebResultColumn = new MaterialColumn();
//                oerebResultColumn.setId("oerebResultColumn");
//                oerebResultColumn.addStyleName("resultColumn");
//                oerebResultColumn.setGrid("s12");
//                addOerebContent(realEstateDPR);
//                tabRow.add(oerebResultColumn);
//
//                resultTab.addSelectionHandler(event -> {
//                    // 2 == OEREB
//                    if (event.getSelectedItem() == 2) {                        
//                        for (String layerId : oerebWmsLayers) {
//                            Image wmsLayer = (Image) getMapLayerById(layerId);
//                            if (layerId.equalsIgnoreCase(expandedOerebLayerId)) {
//                                wmsLayer.setVisible(true);
//                            } else {
//                                wmsLayer.setVisible(false);
//                            }
//                        }
//                    } else {
//                        for (String layerId : oerebWmsLayers) {
//                            Image wmsLayer = (Image) getMapLayerById(layerId);
//                            wmsLayer.setVisible(false);
//                        }
//                    }
//                });
//
//                resultDiv.add(tabRow);
//
//                resultCardContent.add(resultDiv);
//                resultCard.getElement().getStyle().setProperty("height", RESULT_CARD_HEIGHT);
//                resultCard.getElement().getStyle().setProperty("overflowY", "auto");
//                resultCard.getElement().getStyle().setProperty("visibility", "visible");
            }
        });
    }

    private HTMLDivElement addOerebContent(RealEstateDPR realEstateDPR) {
        HTMLDivElement div = div().element();

        {
            Button pdfBtn = Button.create(Icons.ALL.file_pdf_box_outline_mdi())
                .setContent("PDF")
                .setBackground(Color.LIGHT_BLUE)
                .style()
                .setPadding("5px 5px 5px 0px;")
                .setMinWidth(px.of(120)).get();
                    
            pdfBtn.addClickListener(event -> {
                Window.open(realEstateDPR.getOerebPdfExtractUrl(), "_blank", null);
            });
                       
            div.appendChild(pdfBtn.element());
        }


//        Div oerebCollapsibleDiv = new Div();
        
        
        // TODO: eventuell brauche ich das gar nicht. 
        // Drei einzelne Accordions?
        Accordion oerebAccordion = Accordion.create()
                .setHeaderBackground(Color.GREY_LIGHTEN_2)
                .style()
                .setMarginTop("20px")
                .get();
        
        // Darf man das bereits hier einhängen?
        div.appendChild(oerebAccordion.element());
        

        {
//            AccordionPanel oerebAccordionPanelConcernedTheme = AccordionPanel.create(messages.concernedThemes());
//            oerebAccordion.appendChild(oerebAccordionPanelConcernedTheme);

            AccordionPanel oerebAccordionPanelConcernedTheme = AccordionPanel.create(messages.concernedThemes());
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
            
            List<String> concernedThemeItems = realEstateDPR.getOerebConcernedThemes().stream().map(ConcernedTheme::getName).collect(Collectors.toList());
            ListGroup<String> listGroup = ListGroup.<String>create()
                    .setBordered(false)
                    .setItemRenderer((listGroup1, listItem) -> {
                        listItem.appendChild(div()
                                .css(Styles.padding_10)
                                .css("themeList")
                                .add(span().textContent(listItem.getValue())));                        
                    })
                    .setItems(concernedThemeItems);
            oerebAccordionPanelConcernedTheme.setContent(listGroup);
            oerebAccordion.appendChild(oerebAccordionPanelConcernedTheme);

            oerebAccordionPanelConcernedTheme.addEventListener(EventType.click, new EventListener() {
                @Override
                public void handleEvent(Event evt) {
                    if (!oerebAccordionPanelConcernedThemeState) {
                        oerebAccordionPanelConcernedTheme.show();
                        oerebAccordionPanelConcernedThemeState = true;
                    } else {
                        oerebAccordionPanelConcernedTheme.hide();
                        oerebAccordionPanelConcernedThemeState = false;
                    }            
                }
            });  
            
            
            
//            oerebAccordionPanelNotConcernedTheme.addClickListener(event -> {
//                List<AccordionPanel> accordionPanels = oerebAccordion.getPanels();
//                accordionPanels.forEach(accordionPanel -> {
//                    console.log(accordionPanel);
//                        accordionPanel.hide();
//                });
//                
////                event.preventDefault();
////                event.stopPropagation();
////                
////                console.log("oerebAccordionPanelNotConcernedThemeState " + oerebAccordionPanelNotConcernedThemeState);
////                if (!oerebAccordionPanelNotConcernedThemeState) {
////                    oerebAccordionPanelNotConcernedTheme.show();                    
////                    oerebAccordionPanelNotConcernedThemeState = true;
////                } else {
////                    oerebAccordionPanelNotConcernedTheme.hide();
////                    oerebAccordionPanelNotConcernedThemeState = false;
//////                    oerebAccordionPanelNotConcernedTheme.getCollapsible().toggleDisplay();
////                }                
//            });
            

            
            
            
//            oerebCollapsibleConcernedTheme = new MaterialCollapsible();
//            oerebCollapsibleConcernedTheme.addStyleName("plrTopLevelCollapsible");
//            oerebCollapsibleConcernedTheme.setShadow(0);
//
//            oerebCollapsibleConcernedTheme.addExpandHandler(event -> {
//                oerebCollapsibleNotConcernedTheme.closeAll();
//                oerebCollapsibleThemesWithoutData.closeAll();
//                oerebCollapsibleGeneralInformation.closeAll();
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
//            collapsibleThemesHeaderChip.setText(String.valueOf(realEstateDPR.getOerebConcernedThemes().size()));
//            collapsibleConcernedThemeColumnRight.add(collapsibleThemesHeaderChip);
//
//            collapsibleConcernedThemeHeaderRow.add(collapsibleConcernedThemeColumnLeft);
//            collapsibleConcernedThemeHeaderRow.add(collapsibleConcernedThemeColumnRight);
//
//            collapsibleConcernedThemeHeader.add(collapsibleConcernedThemeHeaderRow);
//
//            MaterialCollapsibleBody collapsibleConcernedThemeBody = new MaterialCollapsibleBody();
//            if (realEstateDPR.getOerebConcernedThemes().size() > 0) {
//                collapsibleConcernedThemeBody.setPadding(0);
//
//                oerebInnerCollapsibleConcernedTheme = new MaterialCollapsible();
//                oerebInnerCollapsibleConcernedTheme.addStyleName("concernedThemeCollapsible");
//                oerebInnerCollapsibleConcernedTheme.setAccordion(true);
//                int i = 0;
//
//                for (ConcernedTheme theme : realEstateDPR.getOerebConcernedThemes()) {
//                    i++;
//
//                    oerebInnerCollapsibleConcernedTheme.setShadow(0);
//
//                    Image wmsLayer = createOerebWmsLayer(theme.getReferenceWMS());
//                    map.addLayer(wmsLayer);
//
//                    MaterialCollapsibleItem item = new MaterialCollapsibleItem();
//
//                    // Cannot use the code since all subthemes share the same code.
//                    String layerId = theme.getReferenceWMS().getLayers();
//                    item.setId(layerId);
//                    oerebWmsLayers.add(layerId);
//
//                    MaterialCollapsibleHeader header = new MaterialCollapsibleHeader();
//                    header.addStyleName("collapsibleThemeLayerHeader");
//                    if (i < realEstateDPR.getOerebConcernedThemes().size()) {
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
//                    
//                    /*
//                     * Wegen des unterschiedlichen Umgangs mit Subthemen wird
//                     * es ein klein wenig kompliziert...
//                     */
//                    if (theme.getSubtheme() != null && !theme.getSubtheme().isEmpty()) {
//                        if (!theme.getSubtheme().substring(0,2).equals("ch")) {
//                            link.setText(theme.getName() + " - " + theme.getSubtheme());
//                        } else {
//                            link.setText(theme.getName());
//                        }
//                    } else {
//                        link.setText(theme.getName());
//                    }
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
//                    if (i < realEstateDPR.getOerebConcernedThemes().size()) {
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
//                                        
//                    int opacity;
//                    if (Double.valueOf(theme.getReferenceWMS().getLayerOpacity()) != null && theme.getReferenceWMS().getLayerOpacity() != 0) {
//                        opacity = Double.valueOf((theme.getReferenceWMS().getLayerOpacity() * 100)).intValue();
//                    } else {
//                        opacity = 60;
//                    }
//                    
//                    slider.setValue(opacity);
//                    slider.addValueChangeHandler(event -> {
//                        double sliderOpacity = slider.getValue() / 100.0;
//                        wmsLayer.setOpacity(sliderOpacity);
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
//                            
//                            // FIXME (?) Falls kein Share mitgeliefert wird (z.B. ZH). Ist es mandatory?
//                            if (restriction.getNrOfPoints() == null && restriction.getLengthShare() == null && restriction.getAreaShare() == null) {
//                                MaterialRow informationRow = processRestrictionRow(restriction, null);
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
//                        // TODO: was ist alles möglich?
//                        // Weil die Extension auch fehlen kann, weiss man nie sicher, ob es 
//                        // sich um ein Bild oder Dokument handelt.
//                        String legendAtWeb = theme.getLegendAtWeb();
//                        if (legendAtWeb.toLowerCase().endsWith(".xml") || legendAtWeb.toLowerCase().endsWith(".html") || legendAtWeb.toLowerCase().endsWith(".pdf")) {
//                            MaterialLink legendLink = new MaterialLink();
//                            legendLink.addStyleName("resultLink");
//                            legendLink.setText(legendAtWeb);
//                            legendLink.setHref(legendAtWeb);
//                            legendLink.setTarget("_blank");
//                            legendColumn.add(legendLink);
//
//                            legendRow.add(legendColumn);
//                            body.add(legendRow);
//                        } else {
//                            MaterialLink legendLink = new MaterialLink();
//                            legendLink.addStyleName("resultLink");
//                            legendLink.setText(messages.resultShowLegend());
//                            legendColumn.add(legendLink);
//
//                            legendRow.add(legendColumn);
//                            body.add(legendRow);
//
//                            com.google.gwt.user.client.ui.Image legendImage = new com.google.gwt.user.client.ui.Image();
//                            legendImage.setUrl(theme.getLegendAtWeb());
//                            legendImage.setVisible(false);
//                            body.add(legendImage);
//
//                            MaterialRow fakeRow = new MaterialRow();
//                            fakeRow.setBorderBottom("1px #bdbdbd solid");
//                            body.add(fakeRow);
//                            
//                            legendLink.addClickHandler(event -> {
//                                if (legendImage.isVisible()) {
//                                    legendImage.setVisible(false);
//                                    legendLink.setText(messages.resultShowLegend());
//                                } else {
//                                    legendImage.setVisible(true);
//                                    legendLink.setText(messages.resultHideLegend());
//                                }
//                            });                            
//                        }
//                    }
//
//                    {
//                        MaterialRow legalProvisionsHeaderRow = new MaterialRow();
//                        legalProvisionsHeaderRow.addStyleName("documentsHeaderRow");
//                        legalProvisionsHeaderRow.add(new Label(messages.legalProvisions()));
//                        body.add(legalProvisionsHeaderRow);
//
//                        for (Document legalProvision : theme.getLegalProvisions()) {
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
//                        for (Document law : theme.getLaws()) {
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
//                        
//                        if (theme.getHints().size() > 0) {
//                            MaterialRow hintsHeaderRow = new MaterialRow();
//                            hintsHeaderRow.addStyleName("documentsHeaderRow");
//                            hintsHeaderRow.add(new Label(messages.hints()));
//                            body.add(hintsHeaderRow);
//
//                            for (Document hint : theme.getHints()) {
//                                MaterialRow row = new MaterialRow();
//                                row.addStyleName("hintRow");
//
//                                MaterialLink hintLink = new MaterialLink();
//
//                                String linkText = "";
//                                if (hint.getOfficialTitle() != null) {
//                                    linkText = hint.getOfficialTitle();
//                                } else {
//                                    linkText = hint.getTitle();
//                                }
//                                if (hint.getAbbreviation() != null) {
//                                    linkText += " (" + hint.getAbbreviation() + ")";
//                                }
//                                if (hint.getOfficialNumber() != null) {
//                                    linkText += ", " + hint.getOfficialNumber();
//                                }
//                                hintLink.setText(linkText);
//                                hintLink.setHref(hint.getTextAtWeb());
//                                hintLink.setTarget("_blank");
//                                hintLink.addStyleName("resultLink");
//                                row.add(hintLink);
//                                body.add(row);
//                            }                
//                        }
//                        
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
//                    oerebInnerCollapsibleConcernedTheme.add(item);
//                }
//
//                // Handle visibility of oereb wms layers.
//                // Show them only if oereb tab is selected.
//                oerebInnerCollapsibleConcernedTheme.addExpandHandler(event -> {
//                    expandedOerebLayerId = event.getTarget().getId();
//                    if (resultTab.getTabIndex() == 2) {
//                        for (String layerId : oerebWmsLayers) {
//                            Image wmsLayer = (Image) getMapLayerById(layerId);
//                            if (layerId.equalsIgnoreCase(expandedOerebLayerId)) {
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
//                oerebInnerCollapsibleConcernedTheme.addCollapseHandler(event -> {
//                    expandedOerebLayerId = null;
//                    Image wmsLayer = (Image) getMapLayerById(event.getTarget().getId());
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
//                // Do not open accordion automatically.
//                //oerebInnerCollapsibleConcernedTheme.open(1);
//                collapsibleConcernedThemeBody.add(oerebInnerCollapsibleConcernedTheme);
//            }
//
//            collapsibleConcernedThemeItem.add(collapsibleConcernedThemeHeader);
//            if (realEstateDPR.getOerebConcernedThemes().size() > 0) {
//                collapsibleConcernedThemeItem.add(collapsibleConcernedThemeBody);
//            }
//
//            oerebCollapsibleConcernedTheme.add(collapsibleConcernedThemeItem);
//
//            if (realEstateDPR.getOerebConcernedThemes().size() > 0) {
//                oerebCollapsibleConcernedTheme.open(1);
//            }
//
//            oerebCollapsibleDiv.add(oerebCollapsibleConcernedTheme);
        }

        {
            AccordionPanel oerebAccordionPanelNotConcernedTheme = AccordionPanel.create(messages.notConcernedThemes());
            oerebAccordionPanelNotConcernedTheme.css("oerebAccordionPanelTheme");
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

            oerebAccordionPanelNotConcernedTheme.addEventListener(EventType.click, new EventListener() {
                @Override
                public void handleEvent(Event evt) {
                    if (!oerebAccordionPanelNotConcernedThemeState) {
                        oerebAccordionPanelNotConcernedTheme.show();
                        oerebAccordionPanelNotConcernedThemeState = true;
                    } else {
                        oerebAccordionPanelNotConcernedTheme.hide();
                        oerebAccordionPanelNotConcernedThemeState = false;
                    }            
                }
            });            
//            oerebCollapsibleNotConcernedTheme = new MaterialCollapsible();
//            oerebCollapsibleNotConcernedTheme.addStyleName("plrTopLevelCollapsible");
//            oerebCollapsibleNotConcernedTheme.setShadow(0);
//
//            oerebCollapsibleNotConcernedTheme.addExpandHandler(event -> {
//                oerebCollapsibleConcernedTheme.close(1);
//                oerebCollapsibleThemesWithoutData.closeAll();
//                oerebCollapsibleGeneralInformation.closeAll();
//            });
//
//            MaterialCollapsibleItem collapsibleNotConcernedThemeItem = new MaterialCollapsibleItem();
//
//            MaterialCollapsibleHeader collapsibleNotConcernedThemeHeader = new MaterialCollapsibleHeader();
//            collapsibleNotConcernedThemeHeader.addStyleName("plrCollapsibleThemeHeader");
//
//            MaterialRow collapsibleNotConcernedThemeHeaderRow = new MaterialRow();
//            collapsibleNotConcernedThemeHeaderRow.addStyleName("collapsibleThemeHeaderRow");
//
//            MaterialColumn collapsibleNotConcernedThemeColumnLeft = new MaterialColumn();
//            collapsibleNotConcernedThemeColumnLeft.addStyleName("collapsibleThemeColumnLeft");
//            collapsibleNotConcernedThemeColumnLeft.setGrid("s10");
//            MaterialColumn collapsibleNotConcernedThemeColumnRight = new MaterialColumn();
//            collapsibleNotConcernedThemeColumnRight.addStyleName("collapsibleThemeColumnRight");
//            collapsibleNotConcernedThemeColumnRight.setGrid("s2");
//
//            MaterialLink collapsibleNotConcernedHeaderLink = new MaterialLink();
//            collapsibleNotConcernedHeaderLink.addStyleName("collapsibleThemesHeaderLink");
//            collapsibleNotConcernedHeaderLink.setText(messages.notConcernedThemes());
//            collapsibleNotConcernedThemeColumnLeft.add(collapsibleNotConcernedHeaderLink);
//
//            MaterialChip collapsibleNotConcernedHeaderChip = new MaterialChip();
//            collapsibleNotConcernedHeaderChip.addStyleName("collapsibleThemesHeaderChip");
//            collapsibleNotConcernedHeaderChip
//                    .setText(String.valueOf(realEstateDPR.getOerebNotConcernedThemes().size()));
//            collapsibleNotConcernedThemeColumnRight.add(collapsibleNotConcernedHeaderChip);
//
//            collapsibleNotConcernedThemeHeaderRow.add(collapsibleNotConcernedThemeColumnLeft);
//            collapsibleNotConcernedThemeHeaderRow.add(collapsibleNotConcernedThemeColumnRight);
//            collapsibleNotConcernedThemeHeader.add(collapsibleNotConcernedThemeHeaderRow);
//
//            MaterialCollapsibleBody collapsibleBody = new MaterialCollapsibleBody();
//            collapsibleBody.addMouseOverHandler(event -> {
//                collapsibleBody.getElement().getStyle().setCursor(Cursor.DEFAULT);
//            });
//            collapsibleBody.setPadding(0);
//            MaterialCollection collection = new MaterialCollection();
//
//            for (NotConcernedTheme theme : realEstateDPR.getOerebNotConcernedThemes()) {
//                MaterialCollectionItem item = new MaterialCollectionItem();
//                MaterialLabel label = new MaterialLabel(theme.getName());
//                label.addStyleName("notConcernedThemesLabel");
//                item.add(label);
//                collection.add(item);
//            }
//            collapsibleBody.add(collection);
//
//            collapsibleNotConcernedThemeItem.add(collapsibleNotConcernedThemeHeader);
//            collapsibleNotConcernedThemeItem.add(collapsibleBody);
//            oerebCollapsibleNotConcernedTheme.add(collapsibleNotConcernedThemeItem);
//
//            oerebCollapsibleDiv.add(oerebCollapsibleNotConcernedTheme);
        }

        {
            AccordionPanel oerebAccordionPanelThemesWithoutData = AccordionPanel.create(messages.themesWithoutData());
            oerebAccordionPanelThemesWithoutData.css("oerebAccordionPanelTheme");            
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

            oerebAccordionPanelThemesWithoutData.addEventListener(EventType.click, new EventListener() {
                @Override
                public void handleEvent(Event evt) {
                    if (!oerebAccordionPanelThemesWithoutDataState) {
                        oerebAccordionPanelThemesWithoutData.show();
                        oerebAccordionPanelThemesWithoutDataState = true;
                    } else {
                        oerebAccordionPanelThemesWithoutData.hide();
                        oerebAccordionPanelThemesWithoutDataState = false;
                    }            
                }
            });            
            
//            oerebCollapsibleThemesWithoutData = new MaterialCollapsible();
//            oerebCollapsibleThemesWithoutData.addStyleName("plrTopLevelCollapsible");
//            oerebCollapsibleThemesWithoutData.setShadow(0);
//
//            oerebCollapsibleThemesWithoutData.addExpandHandler(event -> {
//                oerebCollapsibleConcernedTheme.close(1);
//                oerebCollapsibleNotConcernedTheme.closeAll();
//                oerebCollapsibleGeneralInformation.closeAll();
//            });
//
//            MaterialCollapsibleItem collapsibleThemesWithoutDataItem = new MaterialCollapsibleItem();
//
//            MaterialCollapsibleHeader collapsibleThemesWithoutDataHeader = new MaterialCollapsibleHeader();
//            collapsibleThemesWithoutDataHeader.addStyleName("plrCollapsibleThemeHeader");
//            collapsibleThemesWithoutDataHeader.setBackgroundColor(Color.GREY_LIGHTEN_3);
//
//            MaterialRow collapsibleThemesWithoutDataHeaderRow = new MaterialRow();
//            collapsibleThemesWithoutDataHeaderRow.addStyleName("collapsibleThemeHeaderRow");
//
//            MaterialColumn collapsibleThemesWithoutDataColumnLeft = new MaterialColumn();
//            collapsibleThemesWithoutDataColumnLeft.addStyleName("collapsibleThemeColumnLeft");
//            collapsibleThemesWithoutDataColumnLeft.setGrid("s10");
//            MaterialColumn collapsibleThemesWithoutDataColumnRight = new MaterialColumn();
//            collapsibleThemesWithoutDataColumnRight.addStyleName("collapsibleThemeColumnRight");
//            collapsibleThemesWithoutDataColumnRight.setGrid("s2");
//
//            MaterialLink collapsibleThemesWithoutHeaderLink = new MaterialLink();
//            collapsibleThemesWithoutHeaderLink.addStyleName("collapsibleThemesHeaderLink");
//            collapsibleThemesWithoutHeaderLink.setText(messages.themesWithoutData());
//            collapsibleThemesWithoutDataColumnLeft.add(collapsibleThemesWithoutHeaderLink);
//
//            MaterialChip collapsibleThemesWithoutHeaderChip = new MaterialChip();
//            collapsibleThemesWithoutHeaderChip.addStyleName("collapsibleThemesHeaderChip");
//            collapsibleThemesWithoutHeaderChip
//                    .setText(String.valueOf(realEstateDPR.getOerebThemesWithoutData().size()));
//            collapsibleThemesWithoutDataColumnRight.add(collapsibleThemesWithoutHeaderChip);
//
//            collapsibleThemesWithoutDataHeaderRow.add(collapsibleThemesWithoutDataColumnLeft);
//            collapsibleThemesWithoutDataHeaderRow.add(collapsibleThemesWithoutDataColumnRight);
//            collapsibleThemesWithoutDataHeader.add(collapsibleThemesWithoutDataHeaderRow);
//
//            MaterialCollapsibleBody collapsibleBody = new MaterialCollapsibleBody();
//            collapsibleBody.addMouseOverHandler(event -> {
//                collapsibleBody.getElement().getStyle().setCursor(Cursor.DEFAULT);
//            });
//            collapsibleBody.setPadding(0);
//            MaterialCollection collection = new MaterialCollection();
//
//            for (ThemeWithoutData theme : realEstateDPR.getOerebThemesWithoutData()) {
//                MaterialCollectionItem item = new MaterialCollectionItem();
//                MaterialLabel label = new MaterialLabel(theme.getName());
//                label.addStyleName("withoutDataThemesLabel");
//                item.add(label);
//                collection.add(item);
//            }
//            collapsibleBody.add(collection);
//
//            collapsibleThemesWithoutDataItem.add(collapsibleThemesWithoutDataHeader);
//            collapsibleThemesWithoutDataItem.add(collapsibleBody);
//            oerebCollapsibleThemesWithoutData.add(collapsibleThemesWithoutDataItem);
//
//            oerebCollapsibleDiv.add(oerebCollapsibleThemesWithoutData);
        }
        {
            AccordionPanel oerebAccordionPanelGeneralInformation = AccordionPanel.create(messages.generalInformation());
            oerebAccordionPanelGeneralInformation.css("oerebAccordionPanelTheme");            
            DominoElement<HTMLDivElement> oerebAccordionPanelNotConcernedThemeHeaderElement = oerebAccordionPanelGeneralInformation.getHeaderElement();
            oerebAccordionPanelNotConcernedThemeHeaderElement.addCss("oerebAccordionPanelHeaderElement");
            
            Office office = realEstateDPR.getOerebCadastreAuthority();
            HTMLDivElement content = div().css(Styles.padding_10, "generalInformation")
                   .add(div().css(Styles.font_bold).textContent("Katasterverantwortliche Stelle"))
                   .add(div().textContent(office.getName()))
                   .add(div().textContent(office.getStreet() + office.getNumber()))
                   .add(div().textContent(office.getPostalCode() + office.getCity())).element();
            
            oerebAccordionPanelGeneralInformation.setContent(content);
            oerebAccordion.appendChild(oerebAccordionPanelGeneralInformation);

            oerebAccordionPanelGeneralInformation.addEventListener(EventType.click, new EventListener() {
                @Override
                public void handleEvent(Event evt) {
                    if (!oerebAccordionPanelGeneralInformationState) {
                        oerebAccordionPanelGeneralInformation.show();
                        oerebAccordionPanelGeneralInformationState = true;
                    } else {
                        oerebAccordionPanelGeneralInformation.hide();
                        oerebAccordionPanelGeneralInformationState = false;
                    }            
                }
            });            
            
            
//            oerebCollapsibleGeneralInformation = new MaterialCollapsible();
//            oerebCollapsibleGeneralInformation.addStyleName("plrTopLevelCollapsible");
//            oerebCollapsibleGeneralInformation.setShadow(0);
//
//            oerebCollapsibleGeneralInformation.addExpandHandler(event -> {
//                oerebCollapsibleConcernedTheme.close(1);
//                oerebCollapsibleNotConcernedTheme.closeAll();
//                oerebCollapsibleThemesWithoutData.closeAll();
//            });
//
//            MaterialCollapsibleItem collapsibleGeneralInformationItem = new MaterialCollapsibleItem();
//
//            MaterialCollapsibleHeader collapsibleGeneralInformationHeader = new MaterialCollapsibleHeader();
//            collapsibleGeneralInformationHeader.addStyleName("plrCollapsibleThemeHeader");
//
//            MaterialCollapsibleBody body = new MaterialCollapsibleBody();
//            body.addStyleName("collapsibleGeneralInformationBody");
//            body.addMouseOverHandler(event -> {
//                body.getElement().getStyle().setCursor(Cursor.DEFAULT);
//            });
//
//            HTML infoHtml = new HTML();
//
//            Office cadastreAuthority = realEstateDPR.getOerebCadastreAuthority();
//            StringBuilder html = new StringBuilder();
//            html.append("<b>Katasterverantwortliche Stelle</b>");
//            html.append("<br>");
//            html.append(cadastreAuthority.getName());
//            html.append("<br>");
//            html.append(cadastreAuthority.getStreet() + " " + cadastreAuthority.getNumber());
//            html.append("<br>");
//            html.append(cadastreAuthority.getPostalCode() + " " + cadastreAuthority.getCity());
//            html.append("<br>");
//            html.append(makeHtmlLink(cadastreAuthority.getOfficeAtWeb()));
//
//            infoHtml.setHTML(html.toString());
//            body.add(infoHtml);
//
//            MaterialRow collapsibleGeneralInformationHeaderRow = new MaterialRow();
//            collapsibleGeneralInformationHeaderRow.addStyleName("collapsibleThemeHeaderRow");
//
//            MaterialColumn collapsibleGeneralInformationColumnLeft = new MaterialColumn();
//            collapsibleGeneralInformationColumnLeft.addStyleName("collapsibleThemeColumnLeft");
//            collapsibleGeneralInformationColumnLeft.setGrid("s10");
//
//            MaterialLink collapsibleThemesWithoutHeaderLink = new MaterialLink();
//            collapsibleThemesWithoutHeaderLink.addStyleName("collapsibleThemesHeaderLink");
//            collapsibleThemesWithoutHeaderLink.setText(messages.generalInformation());
//            collapsibleGeneralInformationColumnLeft.add(collapsibleThemesWithoutHeaderLink);
//
//            collapsibleGeneralInformationHeaderRow.add(collapsibleGeneralInformationColumnLeft);
//            collapsibleGeneralInformationHeader.add(collapsibleGeneralInformationHeaderRow);
//
//            collapsibleGeneralInformationItem.add(collapsibleGeneralInformationHeader);
//            collapsibleGeneralInformationItem.add(body);
//            oerebCollapsibleGeneralInformation.add(collapsibleGeneralInformationItem);
//
//            oerebCollapsibleDiv.add(oerebCollapsibleGeneralInformation);
        }

//        oerebResultColumn.add(oerebCollapsibleDiv);
        
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

        HTMLDivElement div = div().element();
        
        div.appendChild(addCadastralSurveyingContentKeyValue("E-GRID:", egrid));
        div.appendChild(addCadastralSurveyingContentKeyValue("NBIdent:", new String(identnd == null ? "&ndash;" : identnd)));
        div.appendChild(addCadastralSurveyingContentKeyValue("&nbsp;", "&nbsp;"));
        div.appendChild(addCadastralSurveyingContentKeyValue("Grundstücksart:", type));
        div.appendChild(addCadastralSurveyingContentKeyValue("Grundstücksfläche:", fmtDefault.format(area) + " m<sup>2</sup>"));
        div.appendChild(addCadastralSurveyingContentKeyValue("&nbsp;", "&nbsp;"));        
        div.appendChild(addCadastralSurveyingContentKeyValue("Gemeinde:", municipality));
        div.appendChild(addCadastralSurveyingContentKeyValue("Grundbuch:",  new String(subunitOfLandRegister == null ? "&ndash;" : subunitOfLandRegister)));
        div.appendChild(addCadastralSurveyingContentKeyValue("&nbsp;", "&nbsp;"));
        //addCadastralSurveyingContentKeyValue(new Label("Flurnamen:"), new Label(String.join(", ", localNames)));
        div.appendChild(addCadastralSurveyingContentKeyValue("Flurnamen:", "&ndash;"));

        div.appendChild(div().css("fakeColumn").element());
        
        return div;

//        {
//            MaterialColumn fakeColumn = new MaterialColumn();
//            fakeColumn.addStyleName("fakeColumn mt15");
//            fakeColumn.setGrid("s12");
//            cadastralSurveyingResultColumn.add(fakeColumn);
//        }
    }

    public final class MapSingleClickListener implements ol.event.EventListener<MapBrowserEvent> {
        @Override
        public void onEvent(MapBrowserEvent event) {
            loader.start();
            Coordinate coordinate = event.getCoordinate();
            sendCoordinateToServer(coordinate.toStringXY(3), event);
        }
    }

    // Creates an material row from the restriction object.
//    private MaterialRow processRestrictionRow(Restriction restriction, GeometryType type) {
//        MaterialRow informationRow = new MaterialRow();
//        informationRow.setMarginBottom(10);
//
//        MaterialColumn typeColumn = new MaterialColumn();
//        typeColumn.setGrid("s6");
//        typeColumn.setPadding(0);
//        typeColumn.setMarginRight(0);
//        typeColumn.setFontSize(BODY_FONT_SIZE);
//        Label lbl = new Label(restriction.getInformation());
//        typeColumn.add(lbl);
//
//        MaterialColumn symbolColumn = new MaterialColumn();
//        symbolColumn.setPadding(0);
//        symbolColumn.setMarginRight(0);
//        symbolColumn.setGrid("s1");
//        symbolColumn.setTextAlign(TextAlign.CENTER);
//        symbolColumn.setVerticalAlign(VerticalAlign.MIDDLE);
//
//        Span helper = new Span();
//        helper.setDisplay(Display.INLINE_BLOCK);
//        helper.setVerticalAlign(VerticalAlign.MIDDLE);
//        symbolColumn.add(helper);
//
//        com.google.gwt.user.client.ui.Image symbolImage;
//        if (restriction.getSymbol() != null) {
//            symbolImage = new com.google.gwt.user.client.ui.Image(restriction.getSymbol());
//        } else {
//            symbolImage = new com.google.gwt.user.client.ui.Image(
//                    UriUtils.fromSafeConstant(restriction.getSymbolRef()));
//        }
//        symbolImage.setWidth("30px");
//        symbolImage.getElement().getStyle().setProperty("border", "1px solid black");
//        symbolImage.getElement().getStyle().setProperty("verticalAlign", "middle");
//        symbolColumn.add(symbolImage);
//
//        /*
//         * symbolColumn.addMouseOverHandler(event -> { GWT.log("mouse over symbol"); });
//         * 
//         * symbolColumn.addMouseOutHandler(event -> { GWT.log("mouse out symbol"); });
//         */
//
//        MaterialColumn shareColumn = new MaterialColumn();
//        shareColumn.setTextAlign(TextAlign.RIGHT);
//        shareColumn.setPadding(0);
//        shareColumn.setGrid("s3");
//        shareColumn.setFontSize(BODY_FONT_SIZE);
//
//        if (type == GeometryType.POLYGON) {
//            HTML htmlArea;
//            if (restriction.getAreaShare() < 0.1) {
//                htmlArea = new HTML("< 0.1 m<sup>2</sup>");
//            } else {
//                htmlArea = new HTML(fmtDefault.format(restriction.getAreaShare()) + " m<sup>2</sup>");
//            }
//            shareColumn.add(htmlArea);
//        }
//        if (type == GeometryType.LINE) {
//            HTML htmlLength;
//            if (restriction.getLengthShare() < 0.1) {
//                htmlLength = new HTML("< 0.1 m");
//            } else {
//                htmlLength = new HTML(fmtDefault.format(restriction.getLengthShare()) + " m");
//            }
//            shareColumn.add(htmlLength);
//        }
//        if (type == GeometryType.POINT) {
//            HTML htmlPoints = new HTML(fmtDefault.format(restriction.getNrOfPoints()) + " " + messages.plrNrOfPoints());
//            shareColumn.add(htmlPoints);
//        }
//        if (type == null) {
//            HTML html = new HTML();
//            shareColumn.add(html);
//        }
//
//        MaterialColumn sharePercentColumn = new MaterialColumn();
//        sharePercentColumn.setTextAlign(TextAlign.RIGHT);
//        sharePercentColumn.setPadding(0);
//        sharePercentColumn.setGrid("s2");
//        sharePercentColumn.setFontSize(BODY_FONT_SIZE);
//
//        if (type == GeometryType.POLYGON && restriction.getPartInPercent() != null) {
//            HTML htmlArea;
//            if (restriction.getPartInPercent() < 0.1) {
//                htmlArea = new HTML("< 0.1");
//            } else {
//                htmlArea = new HTML(fmtPercent.format(restriction.getPartInPercent()));
//            }
//            sharePercentColumn.add(htmlArea);
//        } else {
//            sharePercentColumn.add(new HTML("&nbsp;"));
//        }
//
//        informationRow.add(typeColumn);
//        informationRow.add(symbolColumn);
//        informationRow.add(shareColumn);
//        informationRow.add(sharePercentColumn);
//
//        return informationRow;
//    }

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

    // Make a html link from a string.
    private String makeHtmlLink(String text) {
        String html = "<a class='resultLink' href='" + text + "' target='_blank'>" + text + "</a>";
        return html;
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