/**
 * Copyright (c) 2011 KUBO Atsuhiro <kubo@iteman.jp>,
 * All rights reserved.
 *
 * This file is part of MakeGood.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.piece_framework.makegood.aspect;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.NotFoundException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.IStartup;

/**
 * @since 1.6.0
 */
public class AspectWeaver implements IStartup {
    private static final String EXTENSION_POINT_ID = "com.piece_framework.makegood.aspect.manifests"; //$NON-NLS-1$
    private static final Object processLock = new Object();
    private static boolean isFinished = false;

    /* (non-Javadoc)
     * @see org.eclipse.ui.IStartup#earlyStartup()
     */
    @Override
    public void earlyStartup() {
        weave();
    }

    void weave() {
        synchronized (processLock) {
            if (isFinished) return;
            for (AspectManifest manifest: getManifests()) {
                boolean result = loadDependencies(manifest);
                if (!result) continue;
                weaveAspects(manifest);
            }
            isFinished = true;
        }
    }

    public static boolean isFinished() {
        return isFinished;
    }

    private static List<AspectManifest> getManifests() {
        List<AspectManifest> manifests = new ArrayList<AspectManifest>();

        for (IExtension extension: Platform.getExtensionRegistry().getExtensionPoint(EXTENSION_POINT_ID).getExtensions()) {
            for (IConfigurationElement configurationElement: extension.getConfigurationElements()) {
                if ("manifest".equals(configurationElement.getName())) { //$NON-NLS-1$
                    try {
                        Object executableExtension = configurationElement.createExecutableExtension("class"); //$NON-NLS-1$
                        if (executableExtension instanceof AspectManifest) {
                            manifests.add((AspectManifest) executableExtension);
                        }
                    } catch (CoreException e) {
                        Activator.getDefault().getLog().log(new Status(Status.ERROR, Activator.PLUGIN_ID, e.getMessage(), e));
                    }
                }
            }
        }

        return manifests;
    }

    private boolean loadDependencies(AspectManifest manifest) {
        try {
            new BundleLoader(manifest.dependencies()).load();
        } catch (NotFoundException e) {
            log(e, manifest);
            return false;
        }

        return true;
    }

    private void weaveAspects(AspectManifest manifest) {
        Map<CtClass, CtClass> wovenClasses = new IdentityHashMap<CtClass, CtClass>();

        for (Aspect aspect: manifest.aspects()) {
            try {
                aspect.weave();
            } catch (NotFoundException e) {
                log(e, manifest);
                continue;
            } catch (CannotCompileException e) {
                log(e, manifest);
                continue;
            } catch (CannotWeaveException e) {
                log(e, manifest);
                continue;
            }

            for (CtClass wovenClass: aspect.getWovenClasses()) {
                wovenClasses.put(wovenClass, wovenClass);
            }
        }

        for (CtClass wovenClass: wovenClasses.values()) {
            try {
                wovenClass.toClass(manifest.getClass().getClassLoader(), null);
            } catch (CannotCompileException e) {
                log(e, manifest);
                continue;
            }
        }
    }

    private void log(Exception e, AspectManifest manifest) {
        Platform.getLog(Platform.getBundle(manifest.pluginId()))
                .log(new Status(IStatus.ERROR, manifest.pluginId(), 0, e.getMessage(), e));
    }
}
