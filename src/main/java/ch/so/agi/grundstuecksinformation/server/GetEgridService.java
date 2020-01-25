package ch.so.agi.grundstuecksinformation.server;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Service;

@Service
public class GetEgridService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    Jaxb2Marshaller marshaller;
    
    private List<String> oerebServiceBaseUrlList = Stream.of(
            "https://oereb.geo.sh.ch/oereb/", "https://geo.so.ch/api/oereb/")
            .collect(Collectors.toList());    
    
    public void getEgrid() {
        
    }

}
