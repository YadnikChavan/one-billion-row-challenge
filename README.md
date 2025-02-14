# One Billion Row Challenge - CalculateAverage_YadnikChavan

This repository contains my implementation of the [One Billion Row Challenge](https://github.com/gunnarmorling/1brc).

The challenge tasks you with processing a massive text file containing 1,000,000,000 rows of temperature measurements. For each weather station, you are required to calculate the minimum, maximum, and average temperature.

---

## About the Challenge

The One Billion Row Challenge is a performance challenge focused on efficiently processing large datasets. In this challenge, a text file with temperature records is provided where:
- Each record consists of a weather station identifier and a temperature measurement, separated by a semicolon (`;`).
- Your task is to compute, for each station, the minimum, maximum, and average temperature.

---

## My Implementation

The core of my solution is implemented in Java. The main class, `CalculateAverage_YadnikChavan`, processes the measurements file and computes the desired statistics in a highly concurrent manner.

### Key Components

- **StationStats Class:**
  - **Purpose:** Maintains running statistics for a given station.
  - **Fields:**
    - `min`: Tracks the minimum temperature.
    - `max`: Tracks the maximum temperature.
    - `sum`: Accumulates the total temperature.
    - `count`: Counts the number of temperature entries.
  - **Methods:**
    - **Constructor:** Initializes all fields based on the first temperature value.
    - **update(double temp):** Updates `min`, `max`, `sum`, and `count` with a new temperature value.
    - **average():** Computes the average temperature for the station.

- **Main Processing Logic:**
  - **File Reading:** The file is read using a parallel stream (`Files.lines(...).parallel()`), ensuring that processing is done concurrently.
  - **Data Parsing:** Each line is split at the semicolon (`;`). The first part is treated as the station identifier, and the second part is parsed as a `double` for the temperature.
  - **Concurrent Data Collection:** A `ConcurrentHashMap` is used to aggregate statistics for each station. The `compute` method is used to either create a new `StationStats` instance or update the existing one.
  - **Sorting and Formatting:** After processing, the results are sorted alphabetically using a `TreeMap`. Each station’s statistics are then formatted as a string in the format: `min/mean/max` (rounded to one decimal place).


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


### Code Overview

```java
public class CalculateAverage_YadnikChavan {

    static class StationStats {
        double min, max, sum;
        int count;

        StationStats(double temp) {
            this.min = temp;
            this.max = temp;
            this.sum = temp;
            this.count = 1;
        }

        void update(double temp) {
            min = Math.min(min, temp);
            max = Math.max(max, temp);
            sum += temp;
            count++;
        }

        double average() {
            return sum / count;
        }
    }

    public static void main(String[] args) throws FileNotFoundException {
        final String fileName = "./measurements.txt";

        long startTime = System.currentTimeMillis();
        // Use a ConcurrentMap to collect stats per station
        ConcurrentMap<String, StationStats> statsMap = new ConcurrentHashMap<>(420);

        try {
            Files.lines(Paths.get(fileName)).parallel().forEach(line -> {
                int sep = line.indexOf(';');
                if (sep == -1) return; // Skip malformed lines
                String station = line.substring(0, sep);
                double temp = Double.parseDouble(line.substring(sep + 1));

                statsMap.compute(station, (key, stats) -> {
                    if (stats == null) {
                        return new StationStats(temp);
                    } else {
                        stats.update(temp);
                        return stats;
                    }
                });
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.printf("Time to calculate data :: %d%n", System.currentTimeMillis() - startTime);

        // Sort results alphabetically by station name using a TreeMap
        startTime = System.currentTimeMillis();
        TreeMap<String, String> sortedResults = new TreeMap<>();
        for (Map.Entry<String, StationStats> entry : statsMap.entrySet()) {
            String station = entry.getKey();
            StationStats s = entry.getValue();
            double avg = s.average();
            // Format: min/mean/max rounded to one decimal place
            String formatted = String.format("%.1f/%.1f/%.1f", s.min, avg, s.max);
            sortedResults.put(station, formatted);
        }
        System.out.printf("Time for sorting :: %d%n", System.currentTimeMillis() - startTime);
        // Print the final result in the required format
        System.out.println(sortedResults);
    }
}
