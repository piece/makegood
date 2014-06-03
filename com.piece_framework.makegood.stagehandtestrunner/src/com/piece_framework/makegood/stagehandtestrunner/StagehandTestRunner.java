/**
 * Copyright (c) 2010-2012, 2014 KUBO Atsuhiro <kubo@iteman.jp>,
 * All rights reserved.
 *
 * This file is part of MakeGood.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.piece_framework.makegood.stagehandtestrunner;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.Bundle;

public class StagehandTestRunner {
    private static final String PEAR_PATH = "/resources/php"; //$NON-NLS-1$
    private static final String LAUNCHER_SCRIPT = PEAR_PATH + "/bin/testrunner.php"; //$NON-NLS-1$

    public static String getCommandPath() throws CoreException {
        Bundle bundle = Platform.getBundle(Activator.PLUGIN_ID);
        if (bundle == null) {
            throw new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Bundle [ " +  Activator.PLUGIN_ID + " ] is not found")); //$NON-NLS-1$
        }

        URL commandURL = bundle.getEntry(LAUNCHER_SCRIPT);
        if (commandURL == null) {
            throw new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Command [ " +  LAUNCHER_SCRIPT + " ] is not found")); //$NON-NLS-1$
        }

        URL absoluteCommandURL;
        try {
            absoluteCommandURL = FileLocator.resolve(commandURL);
        } catch (IOException e) {
            throw new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e));
        }

        return new File(absoluteCommandURL.getPath()).getAbsolutePath();
    }
}
