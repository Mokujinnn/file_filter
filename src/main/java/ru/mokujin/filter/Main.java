package ru.mokujin.filter;

import com.beust.jcommander.JCommander;

public class Main {
    public static void main(String[] args) {
        try {
            Config config = parseArgs(args);

            System.out.println("Output path: " + config.getOutputPath());
            System.out.println("Prefix: " + config.getPrefix());
            System.out.println("Append mode: " + config.isAppendMode());
            System.out.println("Short stats: " + config.isShortStats());
            System.out.println("Full stats: " + config.isFullStats());
            System.out.println("Input files: " + config.getInputFiles());

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static Config parseArgs(String[] args) {
        Config config = new Config();

        JCommander.newBuilder()
                .addObject(config)
                .build()
                .parse(args);

        return config;
    }
}