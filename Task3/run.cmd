java -version

@echo validation tests ---------------------------
java -jar .\out\artifacts\Task3_jar\Task3.jar
java -jar .\out\artifacts\Task3_jar\Task3.jar VW
java -jar .\out\artifacts\Task3_jar\Task3.jar VW BMW
java -jar .\out\artifacts\Task3_jar\Task3.jar VW BMW Mercedes Opel

@echo.
@echo run demo - 3 elements ------------------------------
java -jar .\out\artifacts\Task3_jar\Task3.jar VW BMW Mercedes

@echo.
@echo run demo - 7 elements ------------------------------
java -jar .\out\artifacts\Task3_jar\Task3.jar VW BMW Mercedes Opel Renault Toyota Honda
