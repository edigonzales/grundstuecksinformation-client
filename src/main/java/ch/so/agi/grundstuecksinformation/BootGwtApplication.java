package ch.so.agi.grundstuecksinformation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.xml.MarshallingHttpMessageConverter;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.web.filter.ForwardedHeaderFilter;

import ch.so.agi.grundstuecksinformation.server.EgridServiceImpl;
import ch.so.agi.grundstuecksinformation.server.ExtractServiceImpl;
import ch.so.agi.grundstuecksinformation.server.SettingsServiceImpl;

@ServletComponentScan
@SpringBootApplication
@Configuration
public class BootGwtApplication {
	public static void main(String[] args) {
		SpringApplication.run(BootGwtApplication.class, args);
	}
  
    @Bean
    public ForwardedHeaderFilter forwardedHeaderFilter() {
        return new ForwardedHeaderFilter();
    }
    
    @Bean
    public HttpMessageConverter<Object> createXmlHttpMessageConverter(Jaxb2Marshaller marshaller) {
        MarshallingHttpMessageConverter xmlConverter = new MarshallingHttpMessageConverter();
        xmlConverter.setMarshaller(marshaller);
        xmlConverter.setUnmarshaller(marshaller);
        return xmlConverter;
    }

    @Bean
    public Jaxb2Marshaller createMarshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setPackagesToScan("ch.ehi.oereb.schemas", "ch.so.geo.schema");
        marshaller.setSupportJaxbElementClass(true);
        marshaller.setLazyInit(true);
        return marshaller;
    }

    @Bean
    public ServletRegistrationBean egridServletBean() {
        ServletRegistrationBean bean = new ServletRegistrationBean(new EgridServiceImpl(), "/module1/egrid");
        bean.setLoadOnStartup(1);
        return bean;
    }  
    
    @Bean
    public ServletRegistrationBean extractServletBean() {
        ServletRegistrationBean bean = new ServletRegistrationBean(new ExtractServiceImpl(), "/module1/extract");
        bean.setLoadOnStartup(1);
        return bean;
    }     
    
    @Bean
    public ServletRegistrationBean settingsServletBean() {
        ServletRegistrationBean bean = new ServletRegistrationBean(new SettingsServiceImpl(), "/module1/settings");
        bean.setLoadOnStartup(1);
        return bean;
    }
}
