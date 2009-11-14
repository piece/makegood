package com.piece_framework.makegood.core.launch;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.ui.IStartup;

import com.piece_framework.makegood.core.Activator;

public class Startup implements IStartup {
    private static final String EXTENSION_POINT_ID = Activator.PLUGIN_ID + ".eventListeners";

    @Override
    public void earlyStartup() {
        List<IMakeGoodEventListener> eventListeners = collectEventListeners();
        DebugPlugin.getDefault().addDebugEventListener(
                new MakeGoodDebugEventSetListener(eventListeners)
                );
    }

    private List<IMakeGoodEventListener> collectEventListeners() {
        List<IMakeGoodEventListener> eventListeners = new ArrayList<IMakeGoodEventListener>();

        IExtensionRegistry registry = Platform.getExtensionRegistry();
        IExtensionPoint point = registry.getExtensionPoint(EXTENSION_POINT_ID);
        IExtension[] extensions = point.getExtensions();
        for (IExtension extension: extensions) {
            for (IConfigurationElement configuration: extension.getConfigurationElements()) {
                if (configuration.getName() == null) {
                    continue;
                }
                if (configuration.getName().equals("eventListener")) {
                    try {
                        Object executable = configuration.createExecutableExtension("listener");
                        if (executable instanceof IMakeGoodEventListener) {
                            eventListeners.add((IMakeGoodEventListener) executable);
                        }
                    } catch (CoreException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        }
        return eventListeners;
    }
}
