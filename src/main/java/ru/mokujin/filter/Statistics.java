package ru.mokujin.filter;

public interface Statistics {
    void addValue(String value);

    String getShortStats();

    String getFullStats();

    long getCount();

    DataType getType();
}