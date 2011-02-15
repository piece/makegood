/**
 * Copyright (c) 2010-2011 KUBO Atsuhiro <kubo@iteman.jp>,
 * All rights reserved.
 *
 * This file is part of MakeGood.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.piece_framework.makegood.aspect.org.eclipse.php.debug.core;

import org.eclipse.ui.IStartup;

import com.piece_framework.makegood.aspect.Aspect;
import com.piece_framework.makegood.aspect.PDTVersion;
import com.piece_framework.makegood.aspect.WeavingProcess;
import com.piece_framework.makegood.aspect.org.eclipse.php.debug.core.aspect.CommandLineArgumentsFixAspect;
import com.piece_framework.makegood.aspect.org.eclipse.php.debug.core.aspect.LaunchWithMissingUserLibrariesFixAspect;
import com.piece_framework.makegood.aspect.org.eclipse.php.debug.core.aspect.SystemIncludePathAspect;
import com.piece_framework.makegood.aspect.org.eclipse.php.debug.core.aspect.XdebugConsoleFixAspect;
import com.piece_framework.makegood.aspect.org.eclipse.php.debug.core.aspect.XdebugLaunchAspect;

public class FragmentWeavingProcess extends WeavingProcess implements IStartup {
    private static final String PLUGIN_ID = "com.piece_framework.makegood.aspect.org.eclipse.php.debug.core"; //$NON-NLS-1$
    private static final String[] DEPENDENCIES = {
        Fragment.PLUGIN_ID,
        "org.eclipse.dltk.core", //$NON-NLS-1$
        "org.eclipse.equinox.common", //$NON-NLS-1$
        "org.eclipse.debug.core" //$NON-NLS-1$
    };

    @Override
    public void earlyStartup() {
        process();
        MonitorTarget.endWeaving = true;
    }

    @Override
    protected String pluginId() {
        return PLUGIN_ID;
    }

    @Override
    protected Aspect[] aspects() {
        return PDTVersion.getInstance().compareTo("2.2.0") >= 0 ? //$NON-NLS-1$
                    new Aspect[] {
                        new XdebugLaunchAspect(),
                        new XdebugConsoleFixAspect(),
                        new SystemIncludePathAspect(),
                        new LaunchWithMissingUserLibrariesFixAspect()
                    } :
                    new Aspect[] {
                        new XdebugLaunchAspect(),
                        new XdebugConsoleFixAspect(),
                        new SystemIncludePathAspect(),
                        new CommandLineArgumentsFixAspect(),
                        new LaunchWithMissingUserLibrariesFixAspect()
                    };
    }

    @Override
    protected String[] dependencies() {
        return DEPENDENCIES;
    }
}
