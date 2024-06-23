# Formal Specifications Analysis

This project analyze specifications written in various formal languages such as SAT, SMT, Alloy, and NuSMV. 
It includes features to run the specifications, capture results or error messages, count comments, calculate lines of code (LOC), compute Halstead complexity metrics, operators, and operands.

## Usage

 These are steps to Run the program On **Windows**. First, clone the repository to your local machine:

```bash
git clone https://github.com/salarkalan/Specs-Analysis.git
```

Navigate to the project directory and build the project using the Gradle wrapper:
```bash
cd path/to/your/project/root
./gradlew.bat build
```

Compile the Java Files. 
```bash
javac -d out -sourcepath src src/main/java/com/salarkalantari/specsanalysis/*.java
```

Run the Compiled Class with the appropriate arguments.
```bash
java -cp out com.salarkalantari.specsanalysis.App <spec-type> <file-path>
```

Replace **'spec-type'** with the type of specification (sat, smt, alloy, nusmv) and **'file-path'** with the path to your specification file.

## Third-Party Licenses

- Limboole - https://github.com/maximaximal/limboole/blob/master/LICENSE
- Z3 - https://github.com/Z3Prover/z3/blob/master/LICENSE.txt
- Alloy - https://github.com/AlloyTools/org.alloytools.alloy/blob/master/LICENSE
- nuXmv - https://nuxmv.fbk.eu/downloads/LICENSE.txt
