/**
 * Copyright (c) 2012-2014 KUBO Atsuhiro <kubo@iteman.jp>,
 * All rights reserved.
 *
 * This file is part of MakeGood.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.piece_framework.makegood.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IFolder;

import com.piece_framework.makegood.core.continuoustesting.ContinuousTesting;
import com.piece_framework.makegood.core.continuoustesting.Scope;

/**
 * @since 2.0.0
 */
public class DefaultConfiguration {
    /**
     * @since 2.3.0
     */
    private ContinuousTesting continuousTesting;

    /**
     * @since 2.3.0
     */
    public DefaultConfiguration() {
        continuousTesting = new ContinuousTesting(true, Scope.ALL_TESTS);
    }

    /**
     * @since 2.3.0
     */
    public ContinuousTesting getContinuousTesting() {
        return continuousTesting;
    }

    public TestingFramework getTestingFramework() {
        return TestingFramework.PHPUnit;
    }

    public List<IFolder> getTestFolders() {
        return Collections.unmodifiableList(new ArrayList<IFolder>());
    }

    public String getPreloadScript() {
        return ""; //$NON-NLS-1$
    }

    public String getTestFilePattern() {
        return ""; //$NON-NLS-1$
    }

    public String getPHPUnitConfigFile() {
        return ""; //$NON-NLS-1$
    }
    
    public String getPHPUnitPharFile() {
    	return ""; //$NON-NLS-1"
    }

    /**
     * @since 2.5.0
     */
    public TestResultsLayout getTestResultsLayout() {
        return TestResultsLayout.TAB;
    }
}
