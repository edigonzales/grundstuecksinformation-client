# grundstuecksinformation-client

## Development

First Terminal:
```
mvn clean spring-boot:run
```

Second Terminal:
```
mvn gwt:generate-module gwt:codeserver
```

Or simple devmode (which worked better for java.xml.bind on client side):
```
mvn gwt:generate-module gwt:devmode 
```

Build fat jar and docker image:
```
TRAVIS_BUILD_NUMBER=9999 mvn package
```


Testrequests:
- https://geo.so.ch/api/oereb/extract/reduced/xml/geometry/CH593289130610
- https://geo.so.ch/api/oereb/getegrid/xml/?XY=2600470,1215429
- https://geo.so.ch/api/oereb/getegrid/xml/?XY=2600593,1215639

- https://oereb.geo.sh.ch/oereb/extract/reduced/pdf/CH330871542766 
- https://oereb.geo.sh.ch/oereb/getegrid/xml/?XY=2688777,1283230
 