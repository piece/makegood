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

package com.piece_framework.makegood.ui;

import org.eclipse.core.resources.IProject;

/**
 * @since 1.6.0
 */
public enum MakeGoodStatus {
    NoTestableProjectSelected,
    NoPHPExecutablesDefined,
    MakeGoodNotConfigured,
    SAPINotCLI,
    TestingFrameworkNotAvailable,
    RunningTest,
    WaitingForTestRun,
    ;

    private IProject project;

    public void setProject(IProject project) {
        this.project = project;
    }

    public IProject getProject() {
        return project;
    }
}
