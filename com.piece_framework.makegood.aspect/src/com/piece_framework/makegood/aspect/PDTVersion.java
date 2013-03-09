/**
 * Copyright (c) 2011, 2013 KUBO Atsuhiro <kubo@iteman.jp>,
 * All rights reserved.
 *
 * This file is part of MakeGood.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.piece_framework.makegood.aspect;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.Bundle;
import org.osgi.framework.Version;

/**
 * @since 1.2.0
 */
public class PDTVersion {
    /**
     * @since 1.6.0
     */
    private static final String REQUIRED_VERSION = "2.2.0"; //$NON-NLS-1$

    private static PDTVersion soleInstance;
    private Version version;

    /**
     * @since 1.6.0
     */
    private IStatus status;

    private PDTVersion() {
        Bundle bundle = Platform.getBundle("org.eclipse.php.core"); //$NON-NLS-1$
        if (bundle == null) {
            status = new Status(Status.ERROR, Activator.PLUGIN_ID, "No bundle is found for org.eclipse.php.core."); //$NON-NLS-1$
            Activator.getDefault().getLog().log(status);
            return;
        }

        Version version = bundle.getVersion();
        if (version.compareTo(Version.parseVersion(REQUIRED_VERSION)) < 0) {
            status = new Status(Status.ERROR, Activator.PLUGIN_ID, "The version of the bundle org.eclipse.php.core must be greater than or equal to " + REQUIRED_VERSION + "."); //$NON-NLS-1$ //$NON-NLS-2$
            Activator.getDefault().getLog().log(status);
            return;
        }

        this.version = version;
    }

    public static PDTVersion getInstance() {
        if (soleInstance == null) {
            soleInstance = new PDTVersion();
        }
        return soleInstance;
    }

    public int compareTo(String version) {
        return this.version.compareTo(Version.parseVersion(version));
    }

    /**
     * @since 1.6.0
     */
    public boolean hasError() {
        return status != null;
    }

    /**
     * @since 2.4.0
     */
    public Version getVersion() {
        return version;
    }
}
