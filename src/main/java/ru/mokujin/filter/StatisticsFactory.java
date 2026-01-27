package ru.mokujin.filter;

public class StatisticsFactory {
    public static Statistics creStatistics(DataType type) {
        return switch (type) {
            case INTEGER -> new IntegerStatistics();
            case FLOAT -> new FloatStatistics();
            case STRING -> new StringStatistics();
        };
    }
}
