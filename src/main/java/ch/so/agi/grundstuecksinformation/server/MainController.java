package ch.so.agi.grundstuecksinformation.server;

import java.math.BigInteger;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.a9.opensearch._11.Image;
import com.a9.opensearch._11.OpenSearchDescription;
import com.a9.opensearch._11.Url;

@Controller
public class MainController {

    @GetMapping(value = "/opensearchdescription.xml", produces=MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<OpenSearchDescription> getOpenSearchDescription() {
        OpenSearchDescription openSearchDescription = new OpenSearchDescription();
        openSearchDescription.setShortName("grundstuecksinformation.ch");
        openSearchDescription.setLongName("Grundstücksinformation - Suche");
        Image image = new Image();
        image.setType("image/x-icon");
        image.setWidth(BigInteger.valueOf(16));
        image.setHeight(BigInteger.valueOf(16));
        image.setValue("data:image/x-icon;base64,AAABAAEAEBAAAAEAIABoBAAAFgAAACgAAAAQAAAAIAAAAAEAIAAAAAAAAAQAAAAAAAAAAAAAAAAAAAAAAABxaUwAcWlMAHFpTABxaUwAcWlMAHFpTABxaUwAcWlMAHFpTABxaUwAcWlMAHFpTABxaUwAcWlMAHFpTABxaUwAcWlMAHFpTABxaUwAcWlMAHFpTAADJP8IAiL/NAEh/0kBIf9JAyP/JgMk/whxaUwAcWlMAHFpTABxaUwAcWlMAHFpTABxaUwAcWlMAAMk/wgBIf9dASD/vgUn/+cTM//4EzP/+AUn/+cBIP+rAiL/NHFpTABxaUwAcWlMAHFpTABxaUwAcWlMAAMk/wgBH/+OEzP/+G2A//++xv//3uL//9PY//+irv//PFX//wUn/+cBIf9dcWlMAHFpTABxaUwAcWlMAHFpTAABIP92EzP/+KKu///6+///7vD//8rR///T2P//+vv//+7w//9tgP//BSf/5wIi/zRxaUwAcWlMAHFpTAADI/8mBSf/546c///6+///sLr//zxV//8QMf//EDH//1Bn///e4v//7vD//1Bn//8BIP+rAyT/CHFpTABxaUwAASH/XSZC///e4v//09j//yZC//8CIv//AiL//wIi//8CIv//UGf///r7//+irv//BSf/5wMj/yZxaUwAcWlMAAEf/448Vf//+vv//46c//8CIv//AiL//wIi//8CIv//AiL//xAx///T2P//09j//xMz//gBIf9JcWlMAHFpTAABH/+OUGf///r7//+OnP//AiL//wIi//8CIv//AiL//wIi//8QMf//ytH//9PY//8TM//4ASH/SXFpTABxaUwAASD/diZC///u8P//vsb//xAx//8CIv//AiL//wIi//8CIv//PFX//+7w//+wuv//BSf/5wIi/zRxaUwAcWlMAAIi/zQFJ//noq7///r7//+OnP//EDH//wIi//8CIv//JkL//77G///6+///UGf//wEg/74DJP8IcWlMAHFpTABxaUwAAR//jiZC///K0f//+vv//8rR//+irv//oq7//97i///6+///jpz//xMz//gBIf9JcWlMAHFpTABxaUwAcWlMAAMk/xkBIP+rJkL//46c///e4v//+vv//+7w///T2P//bYD//xMz//gBIP92AyT/CHFpTABxaUwAcWlMAHFpTABxaUwAAyT/GQEf/44FJ//nJkL//zxV//88Vf//EzP/+AIi/84BIf9dAyT/CHFpTABxaUwAcWlMAHFpTABxaUwAcWlMAHFpTABxaUwAAyP/JgEh/10BIP92ASD/dgEh/0kDJP8ZcWlMAHFpTABxaUwAcWlMAHFpTABxaUwAcWlMAHFpTABxaUwAcWlMAHFpTABxaUwAcWlMAHFpTABxaUwAcWlMAHFpTABxaUwAcWlMAHFpTABxaUwA//8AAPgfAADgDwAAwAcAAMADAACAAQAAgAEAAIABAACAAQAAgAEAAIABAADAAwAAwAMAAOAHAAD4HwAA//8AAA==");
        openSearchDescription.getImages().add(image);
        
        Url searchUrl = new Url();
        searchUrl.setType("text/html");
        searchUrl.setMethod("get");
        searchUrl.setTemplate(getHost() + "/api/search?q={searchTerms}");
        openSearchDescription.getUrls().add(searchUrl);

        Url suggestJsonUrl = new Url();
        suggestJsonUrl.setType("application/x-suggestions+json");
        suggestJsonUrl.setMethod("get");
        suggestJsonUrl.setTemplate(getHost() + "/api/search/suggestions?q={searchTerms}");
//        suggestJsonUrl.setTemplate("http://ff.search.yahoo.com/gossip?output=fxjson&command={searchTerms}");
        openSearchDescription.getUrls().add(suggestJsonUrl);
        
//        Url suggestXmlUrl = new Url();
//        suggestXmlUrl.setType("application/x-suggestions+xml");
//        suggestXmlUrl.setMethod("get");
//        suggestXmlUrl.setTemplate("https://de.wikipedia.org/w/api.php?action=opensearch&amp;format=xml&amp;search={searchTerms}");
//        openSearchDescription.getUrls().add(suggestXmlUrl);
        
//        <Url type="text/html" method="get" template="https://de.wikipedia.org/w/index.php?title=Spezial:Suche&amp;search={searchTerms}"/>
//        <Url type="application/x-suggestions+json" method="get" template="https://de.wikipedia.org/w/api.php?action=opensearch&amp;search={searchTerms}&amp;namespace=0"/>
//        <Url type="application/x-suggestions+xml" method="get" template="https://de.wikipedia.org/w/api.php?action=opensearch&amp;format=xml&amp;search={searchTerms}&amp;namespace=0"/>
        
//        logger.info(ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString());
        
        return new ResponseEntity<OpenSearchDescription>(openSearchDescription, HttpStatus.OK);
    }
     
    private String getHost() {
        return ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
    }
}
