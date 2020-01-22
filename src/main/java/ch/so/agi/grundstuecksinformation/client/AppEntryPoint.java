package ch.so.agi.grundstuecksinformation.client;

import org.jboss.gwt.elemento.core.Elements;
import static org.jboss.gwt.elemento.core.Elements.b;
import static org.jboss.gwt.elemento.core.Elements.div;

import org.dominokit.domino.ui.loaders.Loader;
import org.dominokit.domino.ui.loaders.LoaderEffect;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;

import ch.so.agi.grundstuecksinformation.shared.SettingsResponse;
import ch.so.agi.grundstuecksinformation.shared.SettingsService;
import ch.so.agi.grundstuecksinformation.shared.SettingsServiceAsync;

import elemental2.dom.CSSProperties;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import ol.Map;

public class AppEntryPoint implements EntryPoint {
    private MainMessages messages = GWT.create(MainMessages.class);
    private final SettingsServiceAsync settingsService = GWT.create(SettingsService.class);
    
    private String MY_VAR;
    
    private NumberFormat fmtDefault = NumberFormat.getDecimalFormat();
    private NumberFormat fmtPercent = NumberFormat.getFormat("#0.0");
    
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
        
        Map map = MapPresets.getBlackAndWhiteMap(el.getAttribute("id"));
        //Loader loader = Loader.create(el, LoaderEffect.PULSE).start();
        
        
        
    }

   private static native void updateURLWithoutReloading(String newUrl) /*-{
        $wnd.history.pushState(newUrl, "", newUrl);
    }-*/;
}