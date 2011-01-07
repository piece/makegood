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

package com.piece_framework.makegood.aspect;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javassist.ClassPool;
import javassist.NotFoundException;

import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

public class BundleLoader {
    private String[] bundles;

    public BundleLoader(String[] bundles) {
        this.bundles = bundles;
    }

    public void load() throws NotFoundException {
        if (bundles == null) {
            return;
        }

        List<String> notFoundBundles = new ArrayList<String>();
        for (String bundle: this.bundles) {
            try {
                Bundle realBundle = Platform.getBundle(bundle);
                if (realBundle == null) {
                    throw new NotFoundException(null);
                }
                URL bundleURL = new URL(realBundle.getLocation());
                String bundleLocation = null;
                if (bundleURL.getFile().startsWith("file:")) { //$NON-NLS-1$
                    bundleLocation = bundleURL.getFile().substring("file:".length()); //$NON-NLS-1$
                } else {
                    bundleLocation = bundleURL.getFile();
                }
                File bundleFile = new File(bundleLocation);
                if (!bundleFile.isAbsolute()) {
                    bundleLocation = Platform.getInstallLocation().getURL().getPath() +
                                     bundleLocation;
                }
                if (new File(bundleLocation).isDirectory()) {
                    if (new File(bundleLocation + "bin").exists()) { //$NON-NLS-1$
                        bundleLocation += "bin"; //$NON-NLS-1$
                    }
                }

                ClassPool.getDefault().appendClassPath(bundleLocation);
            } catch (MalformedURLException e) {
                notFoundBundles.add(bundle);
            } catch (NotFoundException e) {
                notFoundBundles.add(bundle);
            }
        }

        if (notFoundBundles.size() > 0) {
            StringBuffer buffer = new StringBuffer();
            for (String notFoundBundle: notFoundBundles) {
                buffer.append(buffer.length() > 0 ? ", " : ""); //$NON-NLS-1$ //$NON-NLS-2$
                buffer.append(notFoundBundle);
            }
            throw new NotFoundException(Messages.BundleLoader_notFoundMessage + buffer.toString());
        }
    }
}
