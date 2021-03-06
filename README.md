# Flamegraph Profiler
Instrumentation Java Profiler & Flamegraph Visualizer.

## Download Plugin
https://plugins.jetbrains.com/plugin/10305-flamegraph-profiler

## Table of contents
* [Quick Start](#quick-start)
* [Performance Recording](#performance-recording)
* [Visualizing Results](#visualizing-results)
* [Flamegraph Visualizer Features](#flamegraph-visualizer-features)
    * [Call Traces](#call-traces)
    * [Back Traces](#back-traces)
    * [Zoom](#zoom)    
    * [Filtering](#filtering)    
    * [Search](#search)    
    * [Hot Spots](#hot-spots)    
    * [Call Tree](#call-tree)    
    * [Detailed View of Thread](#detailed-view-of-thread)
* [Roadmap](#roadmap)
* [Contribution](#contribution)
* [Building from sources](#building-from-sources)
    
## Quick Start
Use Flight Recorder to profile your program and then open flamegraphs:
1. Make sure that you are using Oracle JDK (not OpenJDK) because Java Mission Control comes only with Oracle JDK. To do it open **File | Project Structure... | Project** then click **Edit** beside **Project SDK**, look at **JDK home path** it should be something like this: `.../jdk1.8.0_162` **not** like this: `.../java-8-openjdk-amd64`. You can download needed version from Oracle website: [JDK 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html), [JDK 9](http://www.oracle.com/technetwork/java/javase/downloads/jdk9-downloads-3848520.html)
2. Run JVM with following VM options: `-XX:+UnlockCommercialFeatures -XX:+FlightRecorder -XX:StartFlightRecording=duration=30s,filename=my_recording.jfr -XX:FlightRecorderOptions=stackdepth=256`
3. Open visualizer **Tools | Flamegraph Profiler | Upload File...**
4. Upload my_recording.jfr

## Performance Recording
Plugin lets you record performance of Java program with instrumentation profiler.

1. To specify methods that will be recorded open **Tools | Flamegraph Profiler | Edit Configuration...**  
The configuration below tells profiler to record all methods from my.package.util and my.package.messages packages except methods that start with 'get' or 'set'.  
![](screenshots/profiler_config.png)
2. To run program with profiler select the desired run configuration and choose **Run <name> with profiler**  
![](screenshots/run_with_profiler.png)
3. Also you can configure profiler to save value of method's parameters or it's return value. This should be done if you want to see how parameters influence method's performance. To enable this option check 'Save' checkbox beside type of parameter(s) when editing pattern in configuration.
4. Methods of system classes are not recorded by default. You may include them if you specify full name of a system class in configuration. For example: _java.io.FileOutputStream.\*(\*)_

When your program finishes you will see following message:
```
Methods count: 42
```
If `Method count: 0` it means that either configuration is incorrect or all methods took less than 1ms. To profile small quick methods use sampling profiler (see [Quick Start](#quick-start)). 

## Visualizing Results
You can analyze performance of your program with **Flamegraph Visualizer** that is included in the plugin.  
It supports following files:
* Files in flamegraph format
* _.ser_ files that are created by Flamegraph Profiler
* _.jfr_ files created by Java Flight Recorder
* Yourkit _csv_ files. To generate csv file from a snapshot run following script:  
```bash
java -jar -Dexport.call.tree.cpu -Dexport.csv <path-to-yourkit>/lib/yjp.jar -export ~/Snapshots/<snapshot-name>.snapshot <dir-of-converted-file>
```

Ways to open visualizer:
1. After executing program with the profiler choose **Tools | Flamegraph Profiler | Open Results...** to see the result.
2. To upload your _.jfr_ or _.ser_ file choose **Tools | Flamegraph Profiler | Upload File...**

## Flamegraph Visualizer Features
Flamegraph Visualizer helps you to analyze performance of Java program. It draws a flamegraph where x-axis represents time and y-axis shows stack depth.

Each rectangle represents a method. If a rectangle is wide it means that your program spent a lot of time executing this method and methods that were called within it.

Basically you are looking for rectangles that have a wide "roof" that does not have any other method calls. It means that your program spent a lot of time executing this method.

### Call Traces
This flamegraph accumulates all stacktraces of profiled program. One rectangle represents one or multiple calls of method.

If you place the cursor on the method's rectangle you will see popup with detailed information about method.

If you click on call-traces/back-traces icon on a popup (blue icons at the top of popup) you will see call-traces/back-traces tree for the method (this tree accumulates information from all calls of the method).
![](screenshots/call-traces.png)

### Back Traces
_Back Traces_ is the bottom-up tree. It shows methods that called the method on the top of flamegraph. This flamegraph may be helpful if you know that some method was called a lot of times and you want to know what is the reason for it.  
![](screenshots/back-traces.png)

### Zoom  
Click on a rectangle to zoom in on it.  
![](screenshots/zoom.png)

### Filtering
If you want to see method that are located in some particular package you can apply filter.  
![](screenshots/filter.png)

### Search
You can find any method, class or package using search.

**Tips**:  
* Character '*' matches any sequence of characters.
* If profiler saved values of parameters, you may include them in search string. For example: _resolve(*, *IdeaPlugin.xml_

![](screenshots/search.png)

### Hot Spots
On Hot Spots page you can see where your program spent the most time.  
![](screenshots/hot-spots.png)

### Call Tree
_This page is only for _.ser_ files_  
On **Call Tree** page you can see activity of all threads. To see what was happening inside particular thread you should click on it's name.   
![threads preview](screenshots/preview.png)

### Detailed View of Thread
_This page is only for _.ser_ files_  
On this page you can see what was happening inside some thread. All method calls have original order. Each rectangle represents only one method call.  
You can see popup with detailed information about method if you place the cursor on the method (also there are parameters and return value if they were saved).  
![](screenshots/thread.png)

## Roadmap
1. Start using [Cap'n Proto](https://capnproto.org/) instead of [Google Protobuf](https://github.com/google/protobuf) for client-server communications. This will speed up visualization and reduce memory consumption.
2. Add subsecond-offset heatmap

## Contribution
If you would like to contribute please ping me on telegram @lkornilova, there are plenty of tasks to do :)

If you have any suggestions, just [open an issue](https://github.com/kornilova-l/flamegraph-visualizer/issues)

## Building from sources
If only want to use plugin then you should simply install ready-to-use [jar](https://plugins.jetbrains.com/plugin/10305-flamegraph-profiler).

Install [node.js](https://nodejs.org/en/) and link [js files](/visualization/README.md)

Windows:
```
gradlew compilePlugin
gradlew runIdea
```

Linux:
```bash
./gradlew compilePlugin
./gradlew runIdea
```
