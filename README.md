# Java Code Readability Analysis Tool

A Java-based tool for analyzing and classifying the readability of Java code snippets using various software metrics and machine learning techniques.

## Overview

This project implements a comprehensive code readability analysis system that can:
- Extract various software metrics from Java code snippets
- Preprocess code data for machine learning analysis
- Classify code readability using trained models
- Support multiple feature metrics for comprehensive analysis

## Features

### Supported Metrics

The tool supports the following readability metrics:

- **LINES**: Number of lines in the code snippet
- **TOKEN_ENTROPY**: Token entropy measure for code complexity
- **H_VOLUME**: Halstead Volume metric for program complexity
- **CYCLOMATIC_COMPLEXITY**: McCabe's cyclomatic complexity measure

### Core Functionality

- **Preprocessing**: Extract features from Java code snippets and generate training datasets
- **Classification**: Train and evaluate machine learning models for readability prediction
- **Flexible Input**: Support for `.jsnp` (Java snippet) files and CSV ground truth data
- **Command Line Interface**: Easy-to-use CLI with subcommands for different operations

## Project Structure

```
├── src/
│   └── de/uni_passau/fim/se2/sa/readability/
│       ├── features/           # Feature metric implementations
│       ├── subcommands/        # CLI subcommands
│       ├── utils/              # Core utility classes
│       └── ReadabilityAnalysisMain.java  # Main entry point
├── resources/
│   ├── snippets/               # Sample Java code snippets (.jsnp files)
│   └── truth_scores.csv        # Human readability ratings dataset
├── test/                       # Comprehensive test suite
└── pom.xml                     # Maven configuration
```

## Requirements

- **Java**: JDK 21 or higher
- **Maven**: 3.6+ for building and dependency management
- **Dependencies**: See `pom.xml` for complete list

## Installation

1. Clone the repository:
```bash
git clone <repository-url>
cd Readability-Analysis
```

2. Build the project:
```bash
mvn clean compile
```

3. Create executable JAR:
```bash
mvn package
```

## Usage

The tool provides two main subcommands:

### 1. Preprocess Command

Extract features from Java code snippets and generate training datasets.

```bash
java -jar target/Readability-Analysis-1.0.jar preprocess \
    -s <source-directory> \
    -g <ground-truth-file> \
    -t <target-csv-file> \
    [LINES] [TOKEN_ENTROPY] [H_VOLUME] [CYCLOMATIC_COMPLEXITY]
```

**Parameters:**
- `-s, --source`: Directory containing `.jsnp` files
- `-g, --ground-truth`: CSV file with human readability ratings
- `-t, --target`: Output CSV file for training data
- Feature metrics: Choose one or more from the supported metrics

**Example:**
```bash
java -jar target/Readability-Analysis-1.0.jar preprocess \
    -s resources/snippets \
    -g resources/truth_scores.csv \
    -t training_data.csv \
    LINES TOKEN_ENTROPY CYCLOMATIC_COMPLEXITY
```

### 2. Classify Command

Train and evaluate machine learning models for readability classification.

```bash
java -jar target/Readability-Analysis-1.0.jar classify \
    -d <training-data-file>
```

**Parameters:**
- `-d, --data`: CSV file containing preprocessed training data

**Example:**
```bash
java -jar target/Readability-Analysis-1.0.jar classify \
    -d training_data.csv
```

## Input Format

### Java Snippets (.jsnp files)

The tool expects Java code snippets in `.jsnp` files. These should contain valid Java code that can be parsed by JavaParser.

### Ground Truth CSV

The ground truth file should contain human readability ratings in the following format:
- First column: Rater identifier
- Subsequent columns: Snippet1, Snippet2, ..., SnippetN
- Values: Readability scores (typically 1-5 scale)

### Output Format

The preprocessing step generates a CSV file with:
- First column: Snippet filename
- Feature columns: Values for each selected metric
- Target column: Average human readability rating

## Development

### Building

```bash
# Compile
mvn compile

# Run tests
mvn test

# Generate test coverage report
mvn jacoco:report

# Run mutation testing
mvn pitest:mutationCoverage

# Package
mvn package
```

### Testing

The project includes comprehensive tests for all components:
- Unit tests for feature metrics
- Integration tests for utility classes
- Test coverage reporting with JaCoCo
- Mutation testing with PIT

### Code Quality

- **Coverage**: JaCoCo integration for test coverage
- **Mutation Testing**: PIT for mutation testing
- **Static Analysis**: Maven plugins for code quality

## Dependencies

- **JavaParser**: Java code parsing and AST manipulation
- **Weka**: Machine learning library for classification
- **Picocli**: Command-line interface framework
- **Guava**: Google's core Java libraries
- **JUnit 5**: Testing framework


## License

[Add your license information here]

## Acknowledgments

- University of Passau, Faculty of Computer Science and Mathematics
- Software Engineering 2 course
- Human evaluators who provided readability ratings

## Related Work

This project is part of research on automated code readability assessment and software quality metrics. It builds upon established software engineering principles and machine learning techniques for code analysis. 
