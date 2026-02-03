package ru.mokujin.filter;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileFilterTest {

    @TempDir
    Path tempDir;

    private Config config;
    private FileFilter fileFilter;

    @BeforeEach
    void setUp() {
        config = new Config();
        config.setOutputPath(tempDir);
    }

    @Test
    void testProcessWithMixedData() throws IOException {
        Path inputFile = tempDir.resolve("test.txt");
        Files.write(inputFile, List.of(
                "123",
                "45.67",
                "Hello World",
                "-100",
                "3.14159",
                "Another string"));

        config.setInputFiles(new ArrayList<>(List.of(inputFile.toString())));
        config.setFullStats(true);
        fileFilter = new FileFilter(config);

        fileFilter.process();

        assertTrue(Files.exists(tempDir.resolve("integers.txt")));
        assertTrue(Files.exists(tempDir.resolve("floats.txt")));
        assertTrue(Files.exists(tempDir.resolve("strings.txt")));

        List<String> integers = Files.readAllLines(tempDir.resolve("integers.txt"));
        assertEquals(2, integers.size());
        assertTrue(integers.contains("123"));
        assertTrue(integers.contains("-100"));

        List<String> floats = Files.readAllLines(tempDir.resolve("floats.txt"));
        assertEquals(2, floats.size());
        assertTrue(floats.contains("45.67"));
        assertTrue(floats.contains("3.14159"));

        List<String> strings = Files.readAllLines(tempDir.resolve("strings.txt"));
        assertEquals(2, strings.size());
        assertTrue(strings.contains("Hello World"));
        assertTrue(strings.contains("Another string"));
    }

    @Test
    void testProcessWithOnlyIntegers() throws IOException {
        Path inputFile = tempDir.resolve("integers_only.txt");
        Files.write(inputFile, List.of(
                "1",
                "2",
                "3",
                "-4",
                "0",
                "999999999999999999"));

        config.setInputFiles(new ArrayList<>(List.of(inputFile.toString())));
        fileFilter = new FileFilter(config);

        fileFilter.process();

        assertTrue(Files.exists(tempDir.resolve("integers.txt")));
        assertFalse(Files.exists(tempDir.resolve("floats.txt")));
        assertFalse(Files.exists(tempDir.resolve("strings.txt")));

        List<String> integers = Files.readAllLines(tempDir.resolve("integers.txt"));
        assertEquals(6, integers.size());
    }

    @Test
    void testProcessWithOnlyFloats() throws IOException {
        Path inputFile = tempDir.resolve("floats_only.txt");
        Files.write(inputFile, List.of(
                "1.5",
                "-2.75",
                "0.0",
                "3.141592653589793",
                "1.0E-10",
                "-9.99e+5"));

        config.setInputFiles(new ArrayList<>(List.of(inputFile.toString())));
        fileFilter = new FileFilter(config);

        fileFilter.process();

        assertFalse(Files.exists(tempDir.resolve("integers.txt")));
        assertTrue(Files.exists(tempDir.resolve("floats.txt")));
        assertFalse(Files.exists(tempDir.resolve("strings.txt")));

        List<String> floats = Files.readAllLines(tempDir.resolve("floats.txt"));
        assertEquals(6, floats.size());
    }

    @Test
    void testProcessWithOnlyStrings() throws IOException {
        Path inputFile = tempDir.resolve("strings_only.txt");
        Files.write(inputFile, List.of(
                "Lorem ipsum",
                "Привет мир",
                "123abc",
                "",
                "   ",
                "special!@#$%^&*()"));

        config.setInputFiles(new ArrayList<>(List.of(inputFile.toString())));
        fileFilter = new FileFilter(config);

        fileFilter.process();

        assertFalse(Files.exists(tempDir.resolve("integers.txt")));
        assertFalse(Files.exists(tempDir.resolve("floats.txt")));
        assertTrue(Files.exists(tempDir.resolve("strings.txt")));

        List<String> strings = Files.readAllLines(tempDir.resolve("strings.txt"));
        assertEquals(4, strings.size());
        assertTrue(strings.contains("Lorem ipsum"));
        assertTrue(strings.contains("Привет мир"));
        assertTrue(strings.contains("123abc"));
        assertTrue(strings.contains("special!@#$%^&*()"));
    }

    @Test
    void testProcessWithEmptyLines() throws IOException {
        Path inputFile = tempDir.resolve("empty_lines.txt");
        Files.write(inputFile, List.of(
                "",
                "   ",
                "\t",
                "\n",
                "valid",
                "",
                "123"));

        config.setInputFiles(new ArrayList<>(List.of(inputFile.toString())));
        fileFilter = new FileFilter(config);

        fileFilter.process();

        List<String> strings = Files.readAllLines(tempDir.resolve("strings.txt"));
        assertEquals(1, strings.size());
        assertEquals("valid", strings.get(0));

        List<String> integers = Files.readAllLines(tempDir.resolve("integers.txt"));
        assertEquals(1, integers.size());
        assertEquals("123", integers.get(0));
    }

    @Test
    void testProcessWithPrefix() throws IOException {
        Path inputFile = tempDir.resolve("test.txt");
        Files.write(inputFile, List.of("123", "45.67", "text"));

        config.setInputFiles(new ArrayList<>(List.of(inputFile.toString())));
        config.setPrefix("result_");
        fileFilter = new FileFilter(config);

        fileFilter.process();

        assertTrue(Files.exists(tempDir.resolve("result_integers.txt")));
        assertTrue(Files.exists(tempDir.resolve("result_floats.txt")));
        assertTrue(Files.exists(tempDir.resolve("result_strings.txt")));
    }

    @Test
    void testProcessWithCustomOutputPath() throws IOException {
        Path customOutput = tempDir.resolve("output");
        Path inputFile = tempDir.resolve("test.txt");
        Files.write(inputFile, List.of("123", "45.67", "text"));

        config.setInputFiles(new ArrayList<>(List.of(inputFile.toString())));
        config.setOutputPath(customOutput);
        fileFilter = new FileFilter(config);

        fileFilter.process();

        assertTrue(Files.exists(customOutput));
        assertTrue(Files.exists(customOutput.resolve("integers.txt")));
        assertTrue(Files.exists(customOutput.resolve("floats.txt")));
        assertTrue(Files.exists(customOutput.resolve("strings.txt")));
    }

    @Test
    void testProcessWithAppendMode() throws IOException {
        Path inputFile = tempDir.resolve("test.txt");
        Files.write(inputFile, List.of("123", "45.67"));

        config.setInputFiles(new ArrayList<>(List.of(inputFile.toString())));
        config.setAppendMode(true);
        fileFilter = new FileFilter(config);

        fileFilter.process();

        Files.write(inputFile, List.of("456", "78.90"));
        fileFilter = new FileFilter(config);

        fileFilter.process();

        List<String> integers = Files.readAllLines(tempDir.resolve("integers.txt"));
        assertEquals(2, integers.size());
        assertTrue(integers.contains("123"));
        assertTrue(integers.contains("456"));

        List<String> floats = Files.readAllLines(tempDir.resolve("floats.txt"));
        assertEquals(2, floats.size());
        assertTrue(floats.contains("45.67"));
        assertTrue(floats.contains("78.90"));
    }

    @Test
    void testProcessWithOverwriteMode() throws IOException {
        Path inputFile = tempDir.resolve("test.txt");
        Files.write(inputFile, List.of("123", "45.67"));

        config.setInputFiles(new ArrayList<>(List.of(inputFile.toString())));
        config.setAppendMode(false);
        fileFilter = new FileFilter(config);

        fileFilter.process();

        Files.write(inputFile, List.of("999"));
        fileFilter = new FileFilter(config);

        fileFilter.process();

        List<String> integers = Files.readAllLines(tempDir.resolve("integers.txt"));
        assertEquals(1, integers.size());
        assertEquals("999", integers.get(0));
    }

    @Test
    void testProcessMultipleFiles() throws IOException {
        Path file1 = tempDir.resolve("file1.txt");
        Path file2 = tempDir.resolve("file2.txt");

        Files.write(file1, List.of("123", "text1"));
        Files.write(file2, List.of("45.67", "text2"));

        config.setInputFiles(new ArrayList<>(List.of(file1.toString(), file2.toString())));
        fileFilter = new FileFilter(config);

        fileFilter.process();

        List<String> integers = Files.readAllLines(tempDir.resolve("integers.txt"));
        assertEquals(1, integers.size());
        assertEquals("123", integers.get(0));

        List<String> floats = Files.readAllLines(tempDir.resolve("floats.txt"));
        assertEquals(1, floats.size());
        assertEquals("45.67", floats.get(0));

        List<String> strings = Files.readAllLines(tempDir.resolve("strings.txt"));
        assertEquals(2, strings.size());
        assertTrue(strings.contains("text1"));
        assertTrue(strings.contains("text2"));
    }

    @Test
    void testProcessWithNonExistentFile() {
        config.setInputFiles(new ArrayList<>(List.of("nonexistent_file.txt")));
        fileFilter = new FileFilter(config);

        fileFilter.process();

        assertDoesNotThrow(() -> fileFilter.process());
    }

    @Test
    void testProcessWithShortStatistics() throws IOException {
        Path inputFile = tempDir.resolve("test.txt");
        Files.write(inputFile, List.of("123", "45.67", "text"));

        config.setInputFiles(new ArrayList<>(List.of(inputFile.toString())));
        config.setShortStats(true);
        fileFilter = new FileFilter(config);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        try {
            fileFilter.process();

            String output = outputStream.toString();
            assertTrue(output.contains("Statistics:"));
            assertTrue(output.contains("integer: 1"));
            assertTrue(output.contains("float: 1"));
            assertTrue(output.contains("string: 1"));
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    void testProcessWithFullStatistics() throws IOException {
        Path inputFile = tempDir.resolve("test.txt");
        Files.write(inputFile, List.of("10", "20", "1.5", "2.5", "short", "very long string"));

        config.setInputFiles(new ArrayList<>(List.of(inputFile.toString())));
        config.setFullStats(true);
        fileFilter = new FileFilter(config);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        try {
            fileFilter.process();

            String output = outputStream.toString();
            assertTrue(output.contains("Statistics:"));
            assertTrue(output.contains("integer: 2"));
            assertTrue(output.contains("min: 10"));
            assertTrue(output.contains("max: 20"));
            assertTrue(output.contains("sum: 30"));

            assertTrue(output.contains("float: 2"));
            assertTrue(output.contains("min: 1.5"));
            assertTrue(output.contains("max: 2.5"));

            assertTrue(output.contains("string: 2"));
            assertTrue(output.contains("min length: 5"));
            assertTrue(output.contains("max length: 16"));
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    void testProcessNoStatisticsWhenNotRequested() throws IOException {
        Path inputFile = tempDir.resolve("test.txt");
        Files.write(inputFile, List.of("123", "45.67", "text"));

        config.setInputFiles(new ArrayList<>(List.of(inputFile.toString())));
        config.setShortStats(false);
        config.setFullStats(false);
        fileFilter = new FileFilter(config);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        try {
            fileFilter.process();

            String output = outputStream.toString();
            assertFalse(output.contains("Statistics:"));
        } finally {
            System.setOut(originalOut);
        }
    }
}