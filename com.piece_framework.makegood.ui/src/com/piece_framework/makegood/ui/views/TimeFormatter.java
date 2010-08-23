/**
 * Copyright (c) 2010 MATSUFUJI Hideharu <matsufuji2008@gmail.com>,
 *               2010 KUBO Atsuhiro <kubo@iteman.jp>,
 * All rights reserved.
 *
 * This file is part of MakeGood.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.piece_framework.makegood.ui.views;

class TimeFormatter {
    static String format(long nanoTime) {
        double timeForFormat = 0.0d;
        String unit = null;
        if (nanoTime >= 1000000000) {
            timeForFormat = nanoTime / 1000000000d;
            unit = "s"; //$NON-NLS-1$
        } else if (nanoTime < 1000000000 && nanoTime >= 1000){
            timeForFormat = nanoTime / 1000000d;
            unit = "ms";  //$NON-NLS-1$
        } else if (nanoTime > 0){
            return "< 0.001ms"; //$NON-NLS-1$
        } else {
            return "0.000ms"; //$NON-NLS-1$
        }
        return String.format("%.3f%s", timeForFormat, unit); //$NON-NLS-1$
    }
}
