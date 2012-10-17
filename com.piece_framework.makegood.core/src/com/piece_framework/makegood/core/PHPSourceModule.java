/**
 * Copyright (c) 2012 KUBO Atsuhiro <kubo@iteman.jp>,
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

import com.piece_framework.makegood.core.preference.MakeGoodProperty;

/**
 * @since 2.2.0
 */
public class PHPSourceModule {
    private ISourceModule subject;
    private TestingFramework testingFramework;

    public PHPSourceModule(ISourceModule subject) {
        this.subject = subject;
        IResource resource = subject.getResource();
        if (resource != null) {
            testingFramework = new MakeGoodProperty(resource).getTestingFramework();
        }
    }

    public IResource getResource() {
        return subject.getResource();
    }

    public TestingFramework getTestingFramework() {
        return testingFramework;
    }

    public boolean isTest() throws CoreException {
        for (PHPType phpType: extractPHPTypes()) {
            if (phpType.isTest()) {
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
