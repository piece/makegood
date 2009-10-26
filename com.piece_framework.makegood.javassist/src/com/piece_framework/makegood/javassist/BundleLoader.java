package com.piece_framework.makegood.javassist;

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
                if (bundleURL.getFile().startsWith("file:")) {
                    bundleLocation = bundleURL.getFile().substring("file:".length());
                } else {
                    bundleLocation = bundleURL.getFile();
                }

                if (new File(bundleLocation).isDirectory()) {
                    bundleLocation += "bin";
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
                buffer.append(buffer.length() > 0 ? ", " : "");
                buffer.append(notFoundBundle);
            }
            throw new NotFoundException("The following bundles were not found: " + buffer.toString());
        }
    }
}
