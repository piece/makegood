/**
 * Copyright (c) 2010-2012 KUBO Atsuhiro <kubo@iteman.jp>,
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
import org.eclipse.debug.core.IStreamListener;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.ISourceLocator;
import org.eclipse.debug.core.model.IStreamMonitor;
import org.eclipse.debug.core.model.IStreamsProxy;
import org.eclipse.php.internal.debug.core.launching.PHPLaunch;
import org.eclipse.php.internal.debug.core.model.IPHPDebugTarget;

public class MakeGoodLaunch extends PHPLaunch {
    private static List<ILaunchConfiguration> launchConfigurations = new ArrayList<ILaunchConfiguration>();

    /**
     * @since 1.10.0
     */
    private OutputStreamListener outputStreamListener = new OutputStreamListener();

    public MakeGoodLaunch(ILaunchConfiguration launchConfiguration, String mode, ISourceLocator locator) {
        super(launchConfiguration, mode, locator);
    }

    /**
     * @since 1.10.0
     */
    @Override
    public void addProcess(IProcess process) {
        outputStreamListener.addStreamListener(process);
        super.addProcess(process);
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

    /**
     * @since 1.10.0
     */
    public String getStreamOutput() {
        IDebugTarget debugTarget = getDebugTarget();
        if (debugTarget != null && debugTarget instanceof IPHPDebugTarget) {
            return ((IPHPDebugTarget) debugTarget).getOutputBuffer().toString();
        } else {
            return outputStreamListener.getOutput();
        }
    }

    /**
     * @since 1.10.0
     */
    public void removeStreamListener()
    {
        outputStreamListener.removeStreamListener();
    }

    static void clearLaunchConfigurations() throws CoreException {
        for (int i = 0; i < launchConfigurations.size(); ++i) {
            launchConfigurations.get(i).delete();
        }
    }

    public static boolean hasActiveLaunch() {
        for (ILaunch launch: DebugPlugin.getDefault().getLaunchManager().getLaunches()) {
            if ((launch instanceof MakeGoodLaunch) && !launch.isTerminated()) {
                return true;
            }
        }

        return false;
    }

    /**
     * @since 1.10.0
     */
    private class OutputStreamListener implements IStreamListener {
        private StringBuilder output = new StringBuilder();
        private IStreamMonitor outputStreamMonitor;

        @Override
        public void streamAppended(String text, IStreamMonitor monitor) {
            output.append(text);
        }

        public void addStreamListener(IProcess process) {
            if (process != null) {
                if (!process.isTerminated()) {
                    IStreamsProxy streamsProxy = process.getStreamsProxy();
                    if (streamsProxy != null) {
                        outputStreamMonitor = streamsProxy.getOutputStreamMonitor();
                        if (outputStreamMonitor != null) {
                            outputStreamMonitor.addListener(this);
                        }
                    }
                }
            }
        }

        public void removeStreamListener()
        {
            if (outputStreamMonitor != null) {
                outputStreamMonitor.removeListener(outputStreamListener);
            }
        }

        public String getOutput() {
            return output.toString();
        }
    }
}
