# CRaC Value Tracking

## What is it?

This tool analyzes Java applications to identify dependencies on environmental factors such as program arguments, system properties, and environment variables. 
It is particularly useful for assessing the suitability of applications for Coordinated Restore at Checkpoint (CRaC).

## Getting Started


### Prerequisites

- Java Development Kit (JDK) 17 or higher
- Maven

### Building the Project

Clone the repository and build the project using Maven:

```bash
git clone https://github.com/azul-research/crac-value-tracking.git
cd crac-value-tracking
mvn clean package
```


## Usage

Run the analysis tool with the following arguments:

1. Path to the .jar file to analyze
2. Fully qualified name of the main class
3. Output file name for the backward analysis JSON report
4. Output file name for the forward analysis JSON report

Example:

```bash
java -cp target/crac-value-tracking.jar \
org.example.UnsafeCodeAnalysis \
path/to/application.jar \
com.example.Main \
backward.json \
forward.json
```
Result will be saved to files `backward.json` and `forward.json`

## Output format
The tool produces two JSON files:

* backward.json: Information about static fields and their environmental dependencies.
* forward.json: Details on local variables and their reliance on external inputs.
These reports help in understanding how the application interacts with its environment, which is crucial for CRaC compatibility.

## Tests
Before running test, [jar-generation.sh](jar-generation.sh) should be executed <br>

To add new test:
* add new test case to [cases](src/main/java/test/cases) 
* increment `testNumber` in [CRaCValueTrackerTest.java](src/test/java/CRaCValueTrackerTest.java)
* execute [jar-generation.sh](jar-generation.sh)
* run [CRaCValueTrackerTest.java](src/test/java/CRaCValueTrackerTest.java) 


## Authors and acknowledgment
Author: Daria Suvorova (Azul Cyprus Summer 2024 internship)<br>
Mentor: Anton Kozlov

## License

BSD 2-Clause "Simplified" License
