package ru.mokujin.filter;

import java.math.BigDecimal;
import java.math.RoundingMode;

public abstract class NumberStatistics implements Statistics {
    protected long count = 0;
    protected BigDecimal min = null;
    protected BigDecimal max = null;
    protected BigDecimal sum = BigDecimal.ZERO;
    protected BigDecimal avg = BigDecimal.ZERO;

    @Override
    public void addValue(String value) {
        BigDecimal number = new BigDecimal(value);
        count++;
        sum = sum.add(number);

        if (min == null || number.compareTo(min) < 0) {
            min = number;
        }
        if (max == null || number.compareTo(max) > 0) {
            max = number;
        }
    }

    protected void calculateAverage() {
        if (count > 0) {
            avg = sum.divide(BigDecimal.valueOf(count), 10, RoundingMode.HALF_UP);
        }
    }

    @Override
    public long getCount() {
        return count;
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

        calculateAverage();
        return String.format("%s: %d, min: %s, max: %s, sum: %s, average: %s",
                getType().name().toLowerCase(), count,
                min.toPlainString(),
                max.toPlainString(),
                sum.toPlainString(),
                avg.toPlainString());
    }
}