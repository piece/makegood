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

package com.piece_framework.makegood.launch;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.ISourceLocator;
import org.eclipse.php.internal.debug.core.launching.PHPLaunch;

public class MakeGoodLaunch extends PHPLaunch {
    private static List<ILaunchConfiguration> launchConfigurations = new ArrayList<ILaunchConfiguration>();

    /**
     * @since 1.2.0
     */
    private boolean isActive = false;

    public MakeGoodLaunch(ILaunchConfiguration launchConfiguration, String mode, ISourceLocator locator) {
        super(launchConfiguration, mode, locator);
    }

    @Override
    public void launchAdded(ILaunch launch) {
        if (this.equals(launch)) {
            ILaunchConfiguration launchConfiguration = getLaunchConfiguration();
            if (launchConfiguration != null) {
                launchConfigurations.add(launchConfiguration);
            }
        }

        super.launchAdded(launch);
    }

    static void clearLaunchConfigurations() throws CoreException {
        for (int i = 0; i < launchConfigurations.size(); ++i) {
            launchConfigurations.get(i).delete();
        }
    }

    public static boolean hasActiveLaunch() {
        for (ILaunch launch: DebugPlugin.getDefault().getLaunchManager().getLaunches()) {
            if (!(launch instanceof MakeGoodLaunch)) return false;
            if (((MakeGoodLaunch) launch).isActive()) {
                return true;
            }
        }

        return false;
    }

    /**
     * @since 1.2.0
     */
    private boolean isActive() {
        return isActive;
    }

    /**
     * @since 1.2.0
     */
    public void activate() {
        isActive = true;
    }

    /**
     * @since 1.2.0
     */
    public void deactivate() {
        isActive = false;
    }
}
