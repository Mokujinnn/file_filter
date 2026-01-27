package ru.mokujin.filter;

import java.math.BigDecimal;

public class FloatStatistics extends NumberStatistics {
    @Override
    public DataType getType() {
        return DataType.FLOAT;
    }

    @Override
    public String getFullStats() {
        if (count == 0) {
            return getShortStats();
        }

        calculateAverage();
        return String.format("%s: %d, min: %s, max: %s, sum: %s, average: %s",
                getType().name().toLowerCase(), count,
                formatDecimal(min),
                formatDecimal(max),
                formatDecimal(sum),
                formatDecimal(avg));
    }

    private String formatDecimal(BigDecimal number) {
        String str = number.stripTrailingZeros().toPlainString();

        if (str.indexOf('.') == -1) {
            return str + ".0";
        }

        return str;
    }
}
