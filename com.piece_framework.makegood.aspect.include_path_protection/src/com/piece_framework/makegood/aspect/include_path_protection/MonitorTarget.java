package com.piece_framework.makegood.aspect.include_path_protection;

import com.piece_framework.makegood.javassist.monitor.IMonitorTarget;

public class MonitorTarget implements IMonitorTarget {
    static boolean endWeaving;

    @Override
    public boolean endWeaving() {
        return MonitorTarget.endWeaving;
    }
}
