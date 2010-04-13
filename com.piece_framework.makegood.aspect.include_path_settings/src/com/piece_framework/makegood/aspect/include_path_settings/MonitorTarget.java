/**
 * Copyright (c) 2009 MATSUFUJI Hideharu <matsufuji2008@gmail.com>,
 * All rights reserved.
 *
 * This file is part of MakeGood.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.piece_framework.makegood.aspect.include_path_settings;

import com.piece_framework.makegood.javassist.monitor.IMonitorTarget;

public class MonitorTarget implements IMonitorTarget {
    static boolean endWeaving;

    @Override
    public boolean endWeaving() {
        return MonitorTarget.endWeaving;
    }
}
