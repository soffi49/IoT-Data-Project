## ASSIST-IoT Data Analysis Project

The main aim of the following project was to analyze the data collected as part of the
ASSIST-IoT European project (https://assist-iot.eu/) and play along the way with different
methods of knowledge graph processing.

The technological stack used to implement the project include:

- **Apache Jena** (https://jena.apache.org/) - used to read .ttl-based data and retrieve from it information using
  SPARQL queries
- **Apache Pekko Stream** (https://pekko.apache.org/docs/pekko/current/stream/index.html) - used to process streams of
  graphs
- **PowerBi Desktop** (https://www.microsoft.com/en-us/power-platform/products/power-bi) - used to perform visualization
  of the results

### Project configuration and running

#### Requirements

In order to run the project it is required to have:

1. Java JDK 21 (e.g https://www.oracle.com/java/technologies/javase/jdk21-archive-downloads.html)
2. IDE allowing to run JVM-based programs (e.g. Intellij IDE https://www.jetbrains.com/idea/)

#### Configuration

Before running the project, it is necessary to align its configuration.
All options that can be configured are placed within `config.properties` files, which is within `src\main\resources`
directory. In particular, the configurable options include:

- _batch.size_ - size of the sliding window that is to be used to process the graphs stream.
- _input.path_ - path to the directory in which the data to be processed can be found.
- _output.path_ - path to the directory in which the output is to be stored. The user can change this option, however it
  is not advisable, since changing it would require adjusting the paths indicated by default within the PowerBi
  visualization.

#### Providing input

**IMPORTANT! Input data is NOT provided by default within the project!**

To run the analysis, the user has to provide the streams of input graphs.
By default, it is assumed that 3 types of streams are to be processed: _CAMERAS_, _TAGS_ and _WEATHER_.
Each of these 3 types of streams is assigned the name of subdirectory, which the program will traverse while performing
processing:

- _CAMERAS_ -> `.\cameras`
- _TAGS_ -> `.\tags`
- _WEATHER_ -> `.\weather`

In order to provide input data, the decompressed _.ttl_ files have to placed within the directory indicated in the
configuration under option _input.path_ and, additionally, in subdirectories as outlined above. The internal structure
of files within subdirectories does not matter until the files are (1) decompressed, (2) graphs are stored in Turtle
files and (3) name of the highest subdirectory is correct.

To illustrate the proper configuration, let us use a following example:

Assume that the _CAMERAS_ stream is to be processed and that data is stored within _1.ttl_ and _2.ttl_
files. The path indicated in the configuration file is as follows: _input.path=./data/_

In such settings, the correct input files placement would be:

```
|--/data
|  |--/cameras
|  |  |--1.ttl
|  |  |--2.ttl
|--/src
```

#### Running project

In order to run the project use a dedicated IDE.