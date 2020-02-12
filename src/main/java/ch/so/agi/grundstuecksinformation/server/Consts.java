package ch.so.agi.grundstuecksinformation.server;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class Consts {
    
    // TODO: Inject from application.properties
    // Cannot be static anymore. 
    public static List<String> OEREB_SERVICE_BASE_URL = Stream.of(
            "https://oereb.geo.sh.ch/oereb/", 
            "https://geo.so.ch/api/oereb/",
            "https://map.geo.gl.ch/oereb/wsgi/oereb/",
            "https://map.geo.sz.ch/oereb/wsgi/oereb/",
            "https://api.geo.ag.ch/v1/oereb/")
            .collect(Collectors.toList());
    
//    public List<String> wfsUrlTemplateList = Stream.of(
//            "https://wfs.geodienste.ch/av/deu?&VERSION=1.0.0&SERVICE=WFS&REQUEST=GetFeature&TYPENAME=ms:DPRSF&Filter=<Filter><PropertyIsEqualTo><PropertyName>EGRIS_EGRID</PropertyName><Literal>%s</Literal></PropertyIsEqualTo></Filter>",
//            "https://wfs.geodienste.ch/av/deu?&VERSION=1.0.0&SERVICE=WFS&REQUEST=GetFeature&TYPENAME=ms:RESF&Filter=<Filter><PropertyIsEqualTo><PropertyName>EGRIS_EGRID</PropertyName><Literal>%s</Literal></PropertyIsEqualTo></Filter>")
//            .collect(Collectors.toList()); 
}
