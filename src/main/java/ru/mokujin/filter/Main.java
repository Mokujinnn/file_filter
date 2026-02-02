package ru.mokujin.filter;

import com.beust.jcommander.JCommander;

public class Main {
    public static void main(String[] args) {
        try {

            if (args.length == 0) {
                printUsage();
                return;
            }

            Config config = parseArgs(args);

            FileFilter fileFilter = new FileFilter(config);
            fileFilter.process();

        } catch (IllegalArgumentException e) {
            System.err.println("Error: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static Config parseArgs(String[] args) {
        Config config = new Config();

        JCommander.newBuilder()
                .addObject(config)
                .build()
                .parse(args);

        if (config.getInputFiles().isEmpty()) {
            throw new IllegalArgumentException("No input files specified");
        }

        return config;
    }

    private static void printUsage() {
        String usage = """
                Usage: java -jar file_filter.jar [options] file1.txt file2.txt ...

                Options:
                    -o <path>     Output directory path (default: current directory)
                    -p <prefix>   Prefix for output filenames
                    -a            Append to existing files (default: overwrite)
                    -s            Show short statistics (count only)
                    -f            Show full statistics
                    -h,           Show this help message

                Output files:
                    <prefix>integers.txt   - for integer numbers
                    <prefix>floats.txt     - for floating point numbers
                    <prefix>strings.txt    - for strings

                Examples:
                    java -jar file_filter.jar -s file1.txt file2.txt
                    java -jar file_filter.jar -o /output -p result_ -a -f data.txt""";

        System.out.println(usage);
    }
}
