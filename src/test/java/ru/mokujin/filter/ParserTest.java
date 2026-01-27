package ru.mokujin.filter;

import com.beust.jcommander.JCommander;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;

import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ParserTest {
    private Config config;
    private JCommander jCommander;

    @BeforeEach
    void setUp() {
        config = new Config();
        jCommander = JCommander.newBuilder()
                .addObject(config)
                .build();
    }

    @Test
    @DisplayName("Parse args")
    void parseArgsTest() {
        String[] args = { "-o", "output", "-p", "prefix", "-a", "-s", "-f", "input1", "input2" };
        jCommander.parse(args);
        assertThat(config.getOutputPath()).isEqualTo(Paths.get("output"));
        assertThat(config.getPrefix()).isEqualTo("prefix");
        assertThat(config.isAppendMode()).isTrue();
        assertThat(config.isShortStats()).isTrue();
        assertThat(config.isFullStats()).isTrue();
        assertThat(config.getInputFiles()).containsExactly("input1", "input2");
    }

    @Test
    @DisplayName("Parse empty args")
    void parseEmptyArgsTest() {
        String[] args = {};
        jCommander.parse(args);
        assertThat(config.getOutputPath()).isEqualTo(Paths.get("."));
        assertThat(config.getPrefix()).isEqualTo("");
        assertThat(config.isAppendMode()).isFalse();
        assertThat(config.isShortStats()).isFalse();
        assertThat(config.isFullStats()).isFalse();
        assertThat(config.getInputFiles()).isEmpty();
    }

    @Test
    @DisplayName("If no input files specified")
    void parseNoInputFilesTest() {
        String[] args = { "-o", "output" };

        assertThrows(IllegalArgumentException.class,
                () -> Main.parseArgs(args));
    }
}