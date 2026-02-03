package ru.mokujin.filter;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.ArrayList;

import com.beust.jcommander.Parameter;

public class Config {
    @Parameter(names = { "-o" }, description = "Output path")
    private Path outputPath = Paths.get(".");

    @Parameter(names = { "-p" }, description = "Prefix")
    private String prefix = "";

    @Parameter(names = { "-a" }, description = "Append mode")
    private boolean appendMode = false;

    @Parameter(names = { "-s" }, description = "Short stats")
    private boolean shortStats = false;

    @Parameter(names = { "-f" }, description = "Full stats")
    private boolean fullStats = false;

    @Parameter(description = "Input files")
    private List<String> inputFiles = new ArrayList<>();

    public Path getOutputPath() {
        return outputPath;
    }

    public void setOutputPath(Path outputPath) {
        this.outputPath = outputPath;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public boolean isAppendMode() {
        return appendMode;
    }

    public void setAppendMode(boolean appendMode) {
        this.appendMode = appendMode;
    }

    public boolean isShortStats() {
        return shortStats;
    }

    public void setShortStats(boolean shortStats) {
        this.shortStats = shortStats;
    }

    public boolean isFullStats() {
        return fullStats;
    }

    public void setFullStats(boolean fullStats) {
        this.fullStats = fullStats;
    }

    public List<String> getInputFiles() {
        return new ArrayList<>(inputFiles);
    }

    public void setInputFiles(List<String> inputFiles) {
        this.inputFiles = new ArrayList<>(inputFiles);
    }

    public String getOutputFilename(DataType type) {
        return outputPath.resolve(prefix + type.name().toLowerCase() + "s.txt").toString();
    }
}