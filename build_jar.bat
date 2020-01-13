REM Change JAVA_PATH below to point to the directory where
REM javac, jar, etc. are located
SET JAVA_PATH=C:\Program Files\Java\jdk-13.0.1\bin

cd src/main/java/
"%JAVA_PATH%\javac" -cp ../../../2048.jar nic/*.java
"%JAVA_PATH%\jar" -cf ../../../GeneticAgent.jar nic/*.class ../../../tuples.bin
cd ../../..
