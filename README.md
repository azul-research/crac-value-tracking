# Usafe Code Analysis

## What is it?

This is a tool, that shows program dependencies on environment (program arguments, system properties, environmental variables). <br>


## Result
Result is a json file, it contains 2 parts: *static* - info about static fields, *local* - info about local variables. 



## Usage
How to use: execute main method from UnsafeCodeAnalysis class with arguments: <br>
**first argument** - path to .jar file <br>
**second argument** - full name of Main class <br>
**third argument** - output file name (json file) <br>
Example of arguments: `src/test/java/testJarFiles/mainifandwhile.jar examples.ifandwhile.Main result.json`

Result will be saved to file `result.json`


## Authors and acknowledgment
Author: Daria Suvorova (Azul Cyprus Summer 2024 internship)<br>
Mentor: Anton Kozlov

## License

BSD 2-Clause "Simplified" License

## Project status
Project has unfinished parts, what is not done:

* add interpretation of `invokeinterface` and `invokedynamic` byte code instructions. 
* add forward references.
* fix bug: when analysis goes deeply inside standard library, infinite cycle appears.
