#/bin/bash
cd src/main/java/
javac -cp ../../../2048.jar nic/*.java
jar -cf ../../../GeneticAgent.jar nic/*.class ../../../tuples.bin
