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

package com.piece_framework.makegood.aspect.org.eclipse.php.debug.core.aspect;

import org.eclipse.debug.core.ILaunch;

public class ProcessCrashDetector implements Runnable {
    private ILaunch launch;
    private Process process;

    public ProcessCrashDetector(ILaunch launch, Process p) {
        this.launch = launch;
        this.process = p;
    }

    public ProcessCrashDetector(Process p) {
        this.process = p;
    }

    @Override
    public void run() {
    }
}
