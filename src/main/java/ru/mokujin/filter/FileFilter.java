package ru.mokujin.filter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.regex.Pattern;

public class FileFilter {
    private static final Pattern INTEGER_PATTERN = Pattern.compile("^[-+]?\\d+$");
    private static final Pattern FLOAT_PATTERN = Pattern.compile("^[-+]?\\d*\\.?\\d+([eE][-+]?\\d+)?$");

    private final Config config;
    private final Map<DataType, Statistics> statistics = new EnumMap<>(DataType.class);
    private final Map<DataType, PrintWriter> writers = new EnumMap<>(DataType.class);
    private boolean hasErrors = false;

    public FileFilter(Config config) {
        this.config = config;
        for (DataType type : DataType.values()) {
            statistics.put(type, StatisticsFactory.creStatistics(type));
        }
    }

    public void process() {
        try {
            Files.createDirectories(config.getOutputPath());

            for (String filePath : config.getInputFiles()) {
                processFile(filePath);
            }

            printStatistics();
        } catch (Exception e) {
            System.err.println("Critical error: " + e.getMessage());
            hasErrors = true;
        } finally {
            closeWriters();
        }

        if (hasErrors) {
            System.err.println("Processing completed with errors.");
        }
    }

    private void processFile(String filename) {
        Path filePath = Paths.get(filename);

        if (!Files.exists(filePath)) {
            System.err.println("Warning: File '" + filename + "' not found. Skipping.");
            hasErrors = true;
            return;
        }

        try (BufferedReader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)) {
            String line;

            while ((line = reader.readLine()) != null) {
                line = line.trim();

                if (line.isEmpty()) {
                    continue;
                }

                DataType type = classifyLine(line);
                writeData(type, line);
                updateStatistics(type, line);
            }
        } catch (IOException e) {
            System.err.printf("Error reading file '%s': %s%n", filename, e.getMessage());
            hasErrors = true;
        }
    }

    private void printStatistics() {
        if (!config.isShortStats() && !config.isFullStats()) {
            return;
        }

        System.out.println("Statistics:");

        for (DataType type : DataType.values()) {
            Statistics stats = statistics.get(type);
            if (stats.getCount() > 0) {
                if (config.isShortStats()) {
                    System.out.println("\t" + stats.getShortStats());
                } else {
                    System.out.println("\t" + stats.getFullStats());
                }
            }
        }
    }

    private void closeWriters() {
        for (PrintWriter writer : writers.values()) {
            writer.close();
        }
    }

    private DataType classifyLine(String line) {
        if (INTEGER_PATTERN.matcher(line).matches()) {
            return DataType.INTEGER;
        } else if (FLOAT_PATTERN.matcher(line).matches()) {
            return DataType.FLOAT;
        } else {
            return DataType.STRING;
        }
    }

    private void writeData(DataType type, String data) throws IOException {
        PrintWriter writer = writers.get(type);

        if (writer == null) {
            writer = createWriter(type);
        }

        writer.println(data);
    }

    private PrintWriter createWriter(DataType type) throws IOException {
        PrintWriter writer;
        String filename = config.getOutputFilename(type);
        StandardOpenOption option = config.isAppendMode() ? StandardOpenOption.APPEND : StandardOpenOption.CREATE;
        Path path = Paths.get(filename);

        option = Files.exists(path) ? option : StandardOpenOption.CREATE;

        writer = new PrintWriter(Files.newBufferedWriter(path, StandardCharsets.UTF_8, option));

        writers.put(type, writer);
        return writer;
    }

    private void updateStatistics(DataType type, String data) {
        statistics.get(type).addValue(data);
    }
}
