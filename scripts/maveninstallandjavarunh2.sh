mvn clean install -Dhttps.protocols=TLSv1.2 -DskipTests && 
java -Dadmin.username=admin -Dadmin.password=pass123 -jar target/*.jar
