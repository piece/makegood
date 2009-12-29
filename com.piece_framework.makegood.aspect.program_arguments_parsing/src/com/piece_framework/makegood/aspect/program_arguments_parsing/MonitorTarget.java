package com.piece_framework.makegood.aspect.program_arguments_parsing;

import com.piece_framework.makegood.javassist.monitor.IMonitorTarget;

public class MonitorTarget implements IMonitorTarget {
    static boolean endWeaving;

    @Override
    public boolean endWeaving() {
        return MonitorTarget.endWeaving;
    }
}
