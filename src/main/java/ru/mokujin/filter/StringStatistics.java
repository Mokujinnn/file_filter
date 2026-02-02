package ru.mokujin.filter;

public class StringStatistics implements Statistics {
    private long count = 0;
    private int minLength = Integer.MAX_VALUE;
    private int maxLength = 0;

    @Override
    public void addValue(String value) {
        count++;

        int length = value.length();
        if (length < minLength) {
            minLength = length;
        }
        if (length > maxLength) {
            maxLength = length;
        }
    }

    @Override
    public long getCount() {
        return count;
    }

    @Override
    public DataType getType() {
        return DataType.STRING;
    }

    @Override
    public String getShortStats() {
        return String.format("%s: %d", getType().name().toLowerCase(), count);
    }

    @Override
    public String getFullStats() {
        if (count == 0) {
            return getShortStats();
        }

        return String.format("%s: %d, min length: %d, max length: %d",
                getType().name().toLowerCase(), count, minLength, maxLength);
    }
}
