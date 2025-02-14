package dev.yadnik.onebrc;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Stream;

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
        // Use a HashMap to collect stats per station
        ConcurrentMap<String, StationStats> statsMap = new ConcurrentHashMap<>(420);

        try {
            Files.lines(Paths.get(fileName)).parallel().forEach(line -> {
                int sep = line.indexOf(';');
                if (sep == -1) return; // Skip malformed lines
                String station = line.substring(0, sep);
                double temp = Double.parseDouble(line.substring(sep + 1));

                statsMap.compute(station.toString(), (key, stats) -> {
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
