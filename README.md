# grundstuecksinformation-client

## Development

First Terminal:
```
mvn clean spring-boot:run
```

Second Terminal:
```
mvn clean gwt:generate-module gwt:codeserver
```

Or simple devmode (which worked better for java.xml.bind on client side):
```
mvn gwt:generate-module gwt:devmode 
```

Docker:

```
docker-compose -f docker-compose.yml up
docker-compose -f docker-compose.yml up
```


## Build

Build fat jar and docker image:
```
BUILD_NUMBER=9999 mvn package
```

## Run 
```
docker run -p 9090:8080 sogis/grundstuecksinformation-client
```

Testrequests:
- https://geo.so.ch/api/oereb/extract/reduced/xml/geometry/CH593289130610
- https://geo.so.ch/api/oereb/getegrid/xml/?XY=2600470,1215429
- https://geo.so.ch/api/oereb/getegrid/xml/?XY=2600593,1215639

- https://oereb.geo.sh.ch/oereb/extract/reduced/pdf/CH330871542766 
- https://oereb.geo.sh.ch/oereb/getegrid/xml/?XY=2688777,1283230
 

- http://grundstuecksinformation.ch/?egrid=CH344022777626 (Schwyz)
- http://grundstuecksinformation.ch/?egrid=CH352022786904 (Glarus)
- http://grundstuecksinformation.ch/?egrid=CH590878145427 (Schaffhausen)
- http://grundstuecksinformation.ch/?egrid=CH955832730623 (Messen)
- http://grundstuecksinformation.ch/?egrid=CH575291772384 (Unterentfelden)
- http://grundstuecksinformation.ch/?egrid=CH527789999186 (Züri)

- https://map.geo.sz.ch/oereb/wsgi/oereb/extract/reduced/xml/CH207740742262

```
https://wfs.geodienste.ch/av/deu?&VERSION=1.0.0&SERVICE=WFS&REQUEST=DescribeFeatureType&TYPENAME=ms:DPRSF

https://wfs.geodienste.ch/av/deu?&VERSION=1.0.0&SERVICE=WFS&REQUEST=GetFeature&TYPENAME=ms:DPRSF&Filter=<Filter><ogc:Intersect><PropertyName>ms:msGeometry</PropertyName><gml:Point srsName="EPSG:2056"><gml:coordinates>2610925.833,1230102.086</gml:coordinates></gml:Point></ogc:Intersect></Filter>

https://wfs.geodienste.ch/av/deu?&VERSION=1.0.0&SERVICE=WFS&REQUEST=GetFeature&TYPENAME=ms:DPRSF&Filter=<Filter><PropertyIsEqualTo><PropertyName>EGRIS_EGRID</PropertyName><Literal>CH484832069937</Literal></PropertyIsEqualTo></Filter>
``` 
