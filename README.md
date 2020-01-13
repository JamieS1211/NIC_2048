2048 Controller Submission for GECCO 2015
========================================
Competition website: http://www.cs.put.poznan.pl/wjaskowski/game-2048-competition-gecco-2015

Prerequisites
-------------
Java 8.

Building
--------

The repository includes a readily built GeneticAgent.jar

If you want to build it yourself, execute:

#### Windows
Edit the third line in build_jar.bat so that
`JAVA_PATH` points to your Java Installation's `bin` directory.
Then simply double click build_jar.bat or run from the command line:
```batch
> build_jar.bat
```

#### Linux
```bash
> ./build_jar.sh
```

Evaluation
----------

To evaluate the agent, run:
```bash
> java -jar 2048.jar GeneticAgent.jar nic.GeneticAgent 10000 1.0 123
```

The values at the end of the resulting row are the percentages of games in which the controller achieved a tile of a given value.

License
-------
Licensed under the Apache License, Version 2.0 (the "License");
you may not use code in this repository except in compliance with 
the License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
