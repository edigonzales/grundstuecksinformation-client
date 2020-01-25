package ch.so.agi.grundstuecksinformation.client;

import org.jboss.gwt.elemento.core.Elements;
import static org.jboss.gwt.elemento.core.Elements.b;
import static org.jboss.gwt.elemento.core.Elements.div;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.dominokit.domino.ui.Typography.Strong;
import org.dominokit.domino.ui.alerts.Alert;
import org.dominokit.domino.ui.loaders.Loader;
import org.dominokit.domino.ui.loaders.LoaderEffect;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.rpc.AsyncCallback;

import ch.so.agi.grundstuecksinformation.shared.EgridResponse;
import ch.so.agi.grundstuecksinformation.shared.EgridService;
import ch.so.agi.grundstuecksinformation.shared.EgridServiceAsync;
import ch.so.agi.grundstuecksinformation.shared.SettingsResponse;
import ch.so.agi.grundstuecksinformation.shared.SettingsService;
import ch.so.agi.grundstuecksinformation.shared.SettingsServiceAsync;
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

public class AppEntryPoint implements EntryPoint {
    private MainMessages messages = GWT.create(MainMessages.class);
    private final SettingsServiceAsync settingsService = GWT.create(SettingsService.class);
    private final EgridServiceAsync egridService = GWT.create(EgridService.class);

    private String MY_VAR;
    
    private NumberFormat fmtDefault = NumberFormat.getDecimalFormat();
    private NumberFormat fmtPercent = NumberFormat.getFormat("#0.0");
    
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
        
        //HTMLElement el = Elements.div().style("background-color: hotpink; width: 600px; height: 700px;").element();
        HTMLElement el = Elements.div().element();
        el.setAttribute("id", "map");
        Elements.body().add(el);
        
        //Map map = MapPresets.getBlackAndWhiteMap(el.getAttribute("id"));
        Map map = MapPresets.getCadastralSurveyingWms(el.getAttribute("id"));
        map.addSingleClickListener(new MapSingleClickListener());

        //Loader loader = Loader.create(el, LoaderEffect.PULSE).start();
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
                
                
                GWT.log(result.getEgrid().get(0).getEgrid());
                
                
                Alert alert = Alert.success()
                .appendChild(Strong.of("Well done! "))
                .appendChild("You successfully read this important alert message.")
                .dismissible();
                
                
                alert.style().cssText("position: absolute !important; top:20px !important;");
                
//                alert.element().clientHeight = 500;
//                alert.element().clientWidth = 500;
//                alert.element().clientTop = 20;
//                alert.element().clientLeft = 20;
                
                Elements.body().add(alert);
                
                
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
    

    private static native void updateURLWithoutReloading(String newUrl) /*-{
        $wnd.history.pushState(newUrl, "", newUrl);
    }-*/;
    
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