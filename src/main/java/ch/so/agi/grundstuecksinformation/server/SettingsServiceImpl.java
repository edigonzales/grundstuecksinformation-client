package ch.so.agi.grundstuecksinformation.server;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import ch.so.agi.grundstuecksinformation.shared.SettingsResponse;
import ch.so.agi.grundstuecksinformation.shared.SettingsService;

@SuppressWarnings("serial")
public class SettingsServiceImpl extends RemoteServiceServlet implements SettingsService {

    @Value("${app.myVar}")
    private String myVar;
    
    @Value("${app.opensearchDescriptionUrl}")
    private String opensearchDescriptionUrl;

    @Value("${app.searchServiceUrl}")
    private String searchServiceUrl;

    @Value("${app.dataServiceUrl}")
    private String dataServiceUrl;

    @Value("${app.oerebServiceUrl}")
    private String oerebServiceUrl;

    @Value("${app.cadastreServiceUrl}")
    private String cadastreServiceUrl;

    @Override
    public void init() throws ServletException {
         super.init();
         SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this, getServletContext());
    }
    
    @Override
    public SettingsResponse settingsServer() throws IllegalArgumentException, IOException {
        HashMap<String,Object> settings = new HashMap<String,Object>();
        
        settings.put("MY_VAR", myVar);
        settings.put("OPENSEARCH_DESCRIPTION_URL", opensearchDescriptionUrl);
        settings.put("SEARCH_SERVICE_URL", searchServiceUrl);
        settings.put("DATA_SERVICE_URL", dataServiceUrl);
        settings.put("OEREB_SERVICE_URL", oerebServiceUrl);
        settings.put("CADASTRE_SERVICE_URL", cadastreServiceUrl);

        SettingsResponse response = new SettingsResponse();
        response.setSettings(settings);
        
        return response;
    }
}
