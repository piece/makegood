/**
 * Copyright (c) 2010 KUBO Atsuhiro <kubo@iteman.jp>,
 * All rights reserved.
 *
 * This file is part of MakeGood.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.piece_framework.makegood.stagehand_testrunner;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;

public class StagehandTestRunner {
    private static final String[] BUNDLE_INCLUDE_PATH = {
        "/resources/php/PEAR/src", //$NON-NLS-1$
        "/resources/php/PEAR" //$NON-NLS-1$
    };

    public static String[] getBundleIncludePath() {
        List<String> includePaths = new ArrayList<String>();
        for (String path: BUNDLE_INCLUDE_PATH) {
            URL url;

            try {
                url = FileLocator.resolve(
                          Platform.getBundle(Activator.PLUGIN_ID).getEntry(path)
                      );
            } catch (IOException e) {
                Activator.getDefault().getLog().log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e));
                break;
            }

            includePaths.add(new File(url.getPath()).getAbsolutePath());
        }

        return includePaths.toArray(new String[ includePaths.size() ]);
    }
}
