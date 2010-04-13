/**
 * Copyright (c) 2009-2010 MATSUFUJI Hideharu <matsufuji2008@gmail.com>,
 * All rights reserved.
 *
 * This file is part of MakeGood.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.piece_framework.makegood.javassist.monitor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;

public class WeavingMonitor {
    private static final String PLUGIN_ID = "com.piece_framework.makegood.javassist";    //$NON-NLS-1$
    private static final String EXTENSION_POINT_ID = PLUGIN_ID + ".monitorTargets";      //$NON-NLS-1$
    private static List<IMonitorTarget> targets;

    public static boolean endAll() {
        if (targets == null) {
            targets = getMonitorTargets();
        }

        for (IMonitorTarget target: targets) {
            if (!target.endWeaving()) {
                return false;
            }
        }
        return true;
    }

    private static List<IMonitorTarget> getMonitorTargets() {
        List<IMonitorTarget> targets = new ArrayList<IMonitorTarget>();

        IExtensionRegistry registry = Platform.getExtensionRegistry();
        IExtensionPoint point = registry
                .getExtensionPoint(EXTENSION_POINT_ID);
        IExtension[] extensions = point.getExtensions();
        for (IExtension extension: extensions) {
            for (IConfigurationElement configuration: extension.getConfigurationElements()) {
                if (configuration.getName() == null) {
                    continue;
                }
                if (configuration.getName().equals("monitorTarget")) { //$NON-NLS-1$
                    try {
                        Object executable = configuration.createExecutableExtension("target"); //$NON-NLS-1$
                        if (executable instanceof IMonitorTarget) {
                            targets.add((IMonitorTarget) executable);
                        }
                    } catch (CoreException e) {
                        Platform.getLog(Platform.getBundle(PLUGIN_ID)).log(
                            new Status(
                                Status.WARNING,
                                PLUGIN_ID,
                                e.getMessage(),
                                e
                            )
                        );
                    }
                }
            }
        }
        return targets;
    }
}
