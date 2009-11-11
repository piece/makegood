package com.piece_farmework.makegood.aspect.include_path_search;

import com.piece_framework.makegood.javassist.monitor.IMonitorTarget;

public class MonitorTarget implements IMonitorTarget {
    static boolean endWeaving;

    @Override
    public boolean endWeaving() {
        return MonitorTarget.endWeaving;
    }
}
