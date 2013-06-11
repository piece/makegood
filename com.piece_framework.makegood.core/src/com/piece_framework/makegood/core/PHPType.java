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
@SuppressWarnings("restriction")
public class PHPType {
    private IType type;
    private TestingFramework testingFramework;
    private List<IType> testClassAncestors;

    public PHPType(IType type, TestingFramework testingFramework) {
        this.type = type;
        this.testingFramework = testingFramework;
    }

    public boolean isAbstract() throws CoreException {
        return Flags.isAbstract(type.getFlags());
    }

    public boolean isClass() throws CoreException {
        return PHPFlags.isClass(type.getFlags());
    }

    public boolean isNamespace() throws CoreException {
        return PHPFlags.isNamespace(type.getFlags());
    }

    public boolean isTest() throws CoreException {
        if (isClass()) {
            if (testClassAncestors == null) {
                testClassAncestors = collectTestClassAncestors();
            }

            return testClassAncestors.size() > 0;
        } else {
            return false;
        }
    }

    private List<IType> collectTestClassAncestors() throws ModelException {
        List<IType> testClassAncestors = new ArrayList<IType>();

        ITypeHierarchy hierarchy = type.newSupertypeHierarchy(new NullProgressMonitor());
        if (hierarchy == null) return testClassAncestors;
        IType[] superTypes = hierarchy.getAllSuperclasses(type);
        if (superTypes == null) return testClassAncestors;

        for (IType superType: superTypes) {
            for (String testClassSuperType: testingFramework.getTestClassSuperTypes()) {
                PHPClassType superTypeClassType = PHPClassType.fromIType(superType);
                if (superTypeClassType.getNamespace() != null) {
                    testClassSuperType = "\\" + testClassSuperType; //$NON-NLS-1$
                }

                if (superTypeClassType.getTypeName().equals(testClassSuperType)) {
                    testClassAncestors.add(superType);
                }
            }
        }

        return testClassAncestors;
    }
}
