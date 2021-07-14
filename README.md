Account Balance Calculator
===================

[![Build status](https://badge.buildkite.com/4b6801183d5fa7c45202b7f264577db4d1ce8443e213796f5c.svg)]()

## Prerequisites

- Java 14 ([SDKMAN](https://sdkman.io/install) is a tool to manage multiple JDKs)

To run test cases & build the application:
```
./gradlew clean build
./gradlew test
```
To run the application:
```
./gradlew --console plain run --args="transactionFilePathHere.csv"
```
### Implementation, Assumptions and Improvements
- Assumption as per the Instructions provided in the assignment : 
  - Input file and records are all in a valid format
  - Transaction are recorded in order
- Place transaction file at the same folder OR provide absolute file path.
- Solution uses univocity-parsers in Batched mode. No. of records in a batch can be configured. 
