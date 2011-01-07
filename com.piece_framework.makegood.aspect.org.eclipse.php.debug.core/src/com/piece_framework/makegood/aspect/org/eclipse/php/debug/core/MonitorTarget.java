/**
 * Copyright (c) 2010 KUBO Atsuhiro <kubo@iteman.jp>,
 * All rights reserved.
 *
 * This file is part of MakeGood.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.piece_framework.makegood.aspect.org.eclipse.php.debug.core;

import com.piece_framework.makegood.aspect.monitor.IMonitorTarget;

public class MonitorTarget implements IMonitorTarget {
    static boolean endWeaving = false;

    @Override
    public boolean endWeaving() {
        return MonitorTarget.endWeaving;
    }
}
