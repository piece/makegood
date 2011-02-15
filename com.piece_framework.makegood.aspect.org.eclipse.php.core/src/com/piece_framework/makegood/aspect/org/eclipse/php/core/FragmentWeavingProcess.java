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

package com.piece_framework.makegood.aspect.org.eclipse.php.core;

import org.eclipse.ui.IStartup;

import com.piece_framework.makegood.aspect.Aspect;
import com.piece_framework.makegood.aspect.PDTVersion;
import com.piece_framework.makegood.aspect.WeavingProcess;
import com.piece_framework.makegood.aspect.org.eclipse.php.core.aspect.MultibyteCharactersAspect;
import com.piece_framework.makegood.aspect.org.eclipse.php.core.aspect.SystemIncludePathAspect;

public class FragmentWeavingProcess extends WeavingProcess implements IStartup {
    private static final Object processLock = new Object();
    private static final String[] DEPENDENCIES = {
        "org.eclipse.php.core", //$NON-NLS-1$
        "org.eclipse.core.resources" //$NON-NLS-1$
    };

    @Override
    public void earlyStartup() {
        process();
    }

    @Override
    public void process() {
        synchronized (processLock) {
            if (MonitorTarget.endWeaving) return;
            super.process();
            MonitorTarget.endWeaving = true;
        }
    }

    @Override
    protected String pluginId() {
        return Fragment.PLUGIN_ID;
    }

    @Override
    protected Aspect[] aspects() {
        return PDTVersion.getInstance().compareTo("2.2.0") >= 0 ? //$NON-NLS-1$
                    new Aspect[] {
                        new SystemIncludePathAspect(),
                        new MultibyteCharactersAspect()
                    } :
                    new Aspect[] {
                        new SystemIncludePathAspect()
                    };
    }

    @Override
    protected String[] dependencies() {
        return DEPENDENCIES;
    }
}
