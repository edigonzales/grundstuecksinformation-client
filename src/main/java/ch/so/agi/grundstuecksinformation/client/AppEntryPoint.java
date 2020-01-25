package ch.so.agi.grundstuecksinformation.client;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

import ch.so.agi.grundstuecksinformation.shared.EgridResponse;
import ch.so.agi.grundstuecksinformation.shared.EgridService;
import ch.so.agi.grundstuecksinformation.shared.EgridServiceAsync;
import ch.so.agi.grundstuecksinformation.shared.SettingsResponse;
import ch.so.agi.grundstuecksinformation.shared.SettingsService;
import ch.so.agi.grundstuecksinformation.shared.SettingsServiceAsync;
import ch.so.agi.grundstuecksinformation.shared.models.Egrid;
import elemental2.core.Global;
import elemental2.dom.CSSProperties;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import ol.Coordinate;
import ol.Feature;
import ol.Map;
import ol.MapBrowserEvent;
import ol.event.EventListener;
import ol.format.GeoJson;

import static elemental2.dom.DomGlobal.fetch;
import static elemental2.dom.DomGlobal.console;
import elemental2.dom.Response;
import gwt.material.design.addins.client.window.MaterialWindow;
import gwt.material.design.client.constants.Color;
import gwt.material.design.client.ui.MaterialIcon;
import gwt.material.design.client.ui.MaterialLoader;
import gwt.material.design.client.ui.MaterialPanel;
import gwt.material.design.client.ui.MaterialRow;
import gwt.material.design.client.ui.html.Div;

public class AppEntryPoint implements EntryPoint {
    private MainMessages messages = GWT.create(MainMessages.class);
    private final SettingsServiceAsync settingsService = GWT.create(SettingsService.class);
    private final EgridServiceAsync egridService = GWT.create(EgridService.class);

    private String MY_VAR;
    
    private NumberFormat fmtDefault = NumberFormat.getDecimalFormat();
    private NumberFormat fmtPercent = NumberFormat.getFormat("#0.0");
    
    private MaterialWindow realEstateWindow;
    
    private String identifyRequestTemplate = "https://api3.geo.admin.ch/rest/services/all/MapServer/identify?geometry=%s,%s&geometryFormat=geojson&geometryType=esriGeometryPoint&imageDisplay=1780,772,96&lang=de&layers=all:ch.kantone.cadastralwebmap-farbe&limit=10&mapExtent=%s,%s,%s,%s&returnGeometry=true&sr=2056&tolerance=10";

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
        
        Div mapDiv = new Div();
        mapDiv.setId("map");

        RootPanel.get().add(mapDiv);
        
        //Map map = MapPresets.getBlackAndWhiteMap(mapDiv.getId());
        Map map = MapPresets.getCadastralSurveyingWms(mapDiv.getId());
        map.addSingleClickListener(new MapSingleClickListener());
    }
    
    private void resetGui() {
        if (realEstateWindow != null) {
            realEstateWindow.removeFromParent();
        }
    }
    
    private void sendCoordinateToServer(String XY, MapBrowserEvent event) {
        egridService.egridServer(XY, new AsyncCallback<EgridResponse>() {
            @Override
            public void onFailure(Throwable caught) {
                //MaterialLoader.loading(false);

                if (caught.getMessage().equalsIgnoreCase("204")) {
                    //MaterialToast.fireToast(messages.responseError204(egrid));
                } else if (caught.getMessage().equalsIgnoreCase("500")) {
                    //MaterialToast.fireToast(messages.responseError500());
                    //MaterialToast.fireToast(caught.getMessage());
                } else {
                    //MaterialToast.fireToast(messages.responseError500());
                    //MaterialToast.fireToast(caught.getMessage());
                }
                GWT.log("error: " + caught.getMessage());
            }

            @Override
            public void onSuccess(EgridResponse result) {
                GWT.log("SUCCESS!!!!"); 
                resetGui();
                
                String egrid;
                List<Egrid> egridList = result.getEgrid();
                if (egridList.size() > 1) {
                    realEstateWindow = new MaterialWindow();
                    realEstateWindow.setTitle(messages.realEstatePlural());
                    realEstateWindow.setFontSize("16px");
                    realEstateWindow.setMarginLeft(0);
                    realEstateWindow.setMarginRight(0);
                    realEstateWindow.setWidth("300px");
                    realEstateWindow.setToolbarColor(Color.GREEN_LIGHTEN_1);

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
                            GWT.log("get extract from click for (multiple result): " + row.getId());

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
                }                
            }
        });
    }
    
    public final class MapSingleClickListener implements EventListener<MapBrowserEvent> {
        @Override
        public void onEvent(MapBrowserEvent event) {
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
    
    // Update the URL in the browser without reloading the page.
    private static native void updateURLWithoutReloading(String newUrl) /*-{
        $wnd.history.pushState(newUrl, "", newUrl);
    }-*/;
    
    // String.format() is not available in GWT.
    public static String format(final String format, final String... args) {
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