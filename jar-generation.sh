#!/bin/bash

# Define paths
SRC_DIR="src/main/java/test/cases"
OUTPUT_DIR="src/test/java/jars"

# Ensure the output directory exists
mkdir -p "$OUTPUT_DIR"

# Counter for test packages
i=0

# Loop through test packages
for package in "$SRC_DIR"/test*; do
    if [ -d "$package" ]; then
        echo "Processing package: $package"

        # Define compilation output directory
        COMPILE_DIR="out/test_$i"
        mkdir -p "$COMPILE_DIR"

        # Compile Java files
        javac -d "$COMPILE_DIR" "$package"/*.java
        if [ $? -ne 0 ]; then
            echo "Compilation failed for $package"
            exit 1
        fi

        # Create JAR file
        JAR_FILE="$OUTPUT_DIR/test$i.jar"
        jar cf "$JAR_FILE" -C "$COMPILE_DIR" .

        echo "Created JAR: $JAR_FILE"

        # Increment test index
        ((i++))
    fi
done

echo "All JAR files created successfully."
