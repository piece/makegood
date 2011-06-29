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

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;
import org.osgi.framework.Version;

/**
 * @since 1.2.0
 */
public class PDTVersion {
    private static final String REQUIRED_VERSION = "2.1.0"; //$NON-NLS-1$
    private static PDTVersion soleInstance;
    private Version version;

    private PDTVersion() {
        Bundle bundle = Platform.getBundle("org.eclipse.php.core"); //$NON-NLS-1$
        Assert.isNotNull(bundle, "No bundle is found for org.eclipse.php.core."); //$NON-NLS-1$
        Version version = bundle.getVersion();
        Assert.isTrue(
            version.compareTo(Version.parseVersion(REQUIRED_VERSION)) >= 0,
            "The version of the bundle org.eclipse.php.core must be greater than or equal to " + REQUIRED_VERSION + "." //$NON-NLS-1$ //$NON-NLS-2$
        );
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
}
