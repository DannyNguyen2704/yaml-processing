###Capabilities of this tool
1. Receive swagger yaml file and convert embedded schema to $ref following swagger syntax
2. Output the result into another yaml file at the current position

###To build
```
cd to root which has pom.xml
mvn clean package
```
###To run
```
java -jar target/cooljar.jar // which uses src/main/resources/api/ev_api.yaml_1.org as input
java -jar target/cooljar.jar '/Users/Danny/Desktop/ParsingYaml/src/main/resources/api/ev_api.yaml_1.org' 
// which uses indicated file as input
```
