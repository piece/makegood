package com.piece_framework.makegood.ui.views;

class TimeFormatter {
    static String format(long nanoTime,
                         String unitOfSecond,
                         String unitOfMillisecond
                         ) {
        double timeForFormat = 0.0d;
        String unit = null;
        if (nanoTime >= 1000000000) {
            timeForFormat = nanoTime / 1000000000d;
            unit = unitOfSecond;
        } else if (nanoTime < 1000000000 && nanoTime >= 1000){
            timeForFormat = nanoTime / 1000000d;
            unit = unitOfMillisecond;
        } else if (nanoTime > 0){
            return String.format("< 0.001 %s", unitOfMillisecond);
        } else {
            return String.format("0.000 %s", unitOfMillisecond);
        }
        return String.format("%.3f %s", timeForFormat, unit);
    }
}
