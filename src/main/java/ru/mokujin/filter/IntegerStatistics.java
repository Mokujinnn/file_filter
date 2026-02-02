package ru.mokujin.filter;

public class IntegerStatistics extends NumberStatistics {
    @Override
    public DataType getType() {
        return DataType.INTEGER;
    }
}