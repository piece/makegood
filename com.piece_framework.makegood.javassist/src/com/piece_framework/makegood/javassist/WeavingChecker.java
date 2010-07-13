/**
 * Copyright (c) 2010 MATSUFUJI Hideharu <matsufuji2008@gmail.com>,
 * All rights reserved.
 *
 * This file is part of MakeGood.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.piece_framework.makegood.javassist;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class WeavingChecker {
    private Map<String, Boolean> points = Collections.synchronizedMap(new HashMap<String, Boolean>());

    public WeavingChecker(String[] pointNames) {
        if (pointNames == null) throw new IllegalArgumentException();

        for (String pointName: pointNames) {
            points.put(pointName, Boolean.FALSE);
        }
    }

    public void pass(String pointName) {
        if (pointName == null
                || !points.containsKey(pointName)) throw new IllegalArgumentException();

        points.put(pointName, Boolean.TRUE);
    }

    public void checkAll() throws CannotWeaveException {
        StringBuilder failures = new StringBuilder();

        for (String pointName: points.keySet()) {
            if (!points.get(pointName)) {
                if (failures.length() > 0) failures.append(", ");   //$NON-NLS-1$
                failures.append(pointName);
            }
        }
        if (failures.length() > 0) {
            throw new CannotWeaveException("Cannot weave the following points. ["   //$NON-NLS-1$
                                           + failures
                                           + "]"    //$NON-NLS-1$
                                           );
        }
    }
}
