package com.piece_framework.makegood.javassist.monitor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

public class WeavingMonitor {
    private static final String EXTENSION_POINT_ID = "com.piece_framework.makegood.javassist.monitorTarget";
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
                if (configuration.getName().equals("monitorTarget")) {
                    try {
                        Object executable = configuration.createExecutableExtension("target");
                        if (executable instanceof IMonitorTarget) {
                            targets.add((IMonitorTarget) executable);
                        }
                    } catch (CoreException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        }
        return targets;
    }
}
