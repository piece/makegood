/**
 * Copyright (c) 2012-2013 KUBO Atsuhiro <kubo@iteman.jp>,
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
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.IType;

/**
 * @since 2.2.0
 */
public class PHPSourceModule {
    private ISourceModule subject;
    private TestingFramework testingFramework;

    public PHPSourceModule(ISourceModule subject, TestingFramework testingFramework) {
        this.subject = subject;
        this.testingFramework = testingFramework;
    }

    public IResource getResource() {
        return subject.getResource();
    }

    public TestingFramework getTestingFramework() {
        return testingFramework;
    }

    public boolean hasRunnableTestTypes() throws CoreException {
        for (PHPType phpType: extractPHPTypes()) {
            if (phpType.isTest() && !phpType.isAbstract()) {
                return true;
            }
        }

        return false;
    }

    private List<PHPType> extractPHPTypes() throws CoreException {
        List<PHPType> phpTypes = new ArrayList<PHPType>();
        for (IType type: subject.getAllTypes()) {
            phpTypes.add(new PHPType(type, testingFramework));
        }

        return phpTypes;
    }
}
