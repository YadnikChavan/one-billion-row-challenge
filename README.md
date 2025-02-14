# One Billion Row Challenge - CalculateAverage_YadnikChavan

This repository contains my implementation of the [One Billion Row Challenge](https://github.com/gunnarmorling/1brc).

## About the Challenge

The One Billion Row Challenge is a performance challenge that tasks you with processing a 1,000,000,000-row text file containing temperature measurements. For each weather station, the goal is to calculate the minimum, maximum, and average temperature.

## My Implementation

My version of the challenge is implemented in the Java class file **CalculateAverage_YadnikChavan.java**. In my solution, I use a multi-threaded approach with the Executors framework to read and process the file concurrently while measuring the time taken for both the data calculation and the sorting phases.

## How to Run

1. **Prerequisites:**
   - Java 21 (or later) must be installed.

2. **Generate the Measurements File:**
   - Use the provided shell script to create the measurements file. Run:
     ```bash
     ./create_measurements.sh <number_of_records>
     ```
     For example, to create a file with 1,000,000,000 records:
     ```bash
     ./create_measurements.sh 1000000000
     ```

3. **Compilation:**
   ```bash
   javac CalculateAverage_YadnikChavan.java
