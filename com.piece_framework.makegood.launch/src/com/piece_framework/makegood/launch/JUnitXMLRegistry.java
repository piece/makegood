/**
 * Copyright (c) 2009 MATSUFUJI Hideharu <matsufuji2008@gmail.com>,
 *               2010 KUBO Atsuhiro <kubo@iteman.jp>,
 * All rights reserved.
 *
 * This file is part of MakeGood.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.piece_framework.makegood.launch;

import java.io.File;

public class JUnitXMLRegistry {
    private static File registry =
        new File(System.getProperty("java.io.tmpdir"), Activator.PLUGIN_ID); //$NON-NLS-1$

    public static File getRegistry() {
        return registry;
    }

    public static void create() {
        if (registry.exists()) {
            registry.delete();
        }

        registry.mkdirs();
    }

    public static void destroy() {
        for (File file: registry.listFiles()) {
            file.delete();
        }
        registry.delete();
    }
}
