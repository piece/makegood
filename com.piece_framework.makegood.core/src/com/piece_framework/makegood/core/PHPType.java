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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.dltk.core.Flags;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.core.ITypeHierarchy;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.php.core.compiler.PHPFlags;
import org.eclipse.php.internal.core.typeinference.PHPClassType;

/**
 * @since 2.2.0
 */
public class PHPType {
    private IType subject;
    private TestingFramework testingFramework;
    private List<IType> testClassAncestors;

    public PHPType(IType subject, TestingFramework testingFramework) {
        this.subject = subject;
        this.testingFramework = testingFramework;
    }

    public boolean isAbstract() throws CoreException {
        return Flags.isAbstract(subject.getFlags());
    }

    public boolean isClass() throws CoreException {
        return PHPFlags.isClass(subject.getFlags());
    }

    public boolean isNamespace() throws CoreException {
        return PHPFlags.isNamespace(subject.getFlags());
    }

    public boolean isTest() throws CoreException {
        if (isClass() == false) return false;
        if (isAbstract() == true) return false;

        if (testClassAncestors == null) {
            testClassAncestors = collectTestClassAncestors();
        }

        return testClassAncestors.size() > 0;
    }

    private List<IType> collectTestClassAncestors() throws ModelException {
        List<IType> testClassAncestors = new ArrayList<IType>();

        ITypeHierarchy hierarchy = subject.newSupertypeHierarchy(new NullProgressMonitor());
        if (hierarchy == null) return testClassAncestors;
        IType[] superTypes = hierarchy.getAllSuperclasses(subject);
        if (superTypes == null) return testClassAncestors;

        for (IType superType: superTypes) {
            for (String testClassSuperType: testingFramework.getTestClassSuperTypes()) {
                if (PHPClassType.fromIType(superType).getNamespace() != null) {
                    testClassSuperType = "\\" + testClassSuperType; //$NON-NLS-1$
                }

                if (PHPClassType.fromIType(superType).getTypeName().equals(testClassSuperType)) {
                    testClassAncestors.add(superType);
                }
            }
        }

        return testClassAncestors;
    }
}
