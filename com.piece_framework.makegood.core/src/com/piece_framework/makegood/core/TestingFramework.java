/**
 * Copyright (c) 2012-2013 MATSUFUJI Hideharu <matsufuji2008@gmail.com>,
 *               2011-2012, 2014 KUBO Atsuhiro <kubo@iteman.jp>,
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.dltk.ast.Modifiers;
import org.eclipse.dltk.core.IMethod;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.core.ITypeHierarchy;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.php.internal.core.typeinference.PHPClassType;

public enum TestingFramework {
    PHPUnit {
        /**
         * @since 1.6.0
         */
        @Override
        public String[] getTestClassSuperTypes() {
            return new String[] {
                "PHPUnit_Framework_TestCase", //$NON-NLS-1$
            };
        }

        /**
         * @since 1.7.0
         */
        @Override
        public String[] getRequiredSuperTypes() {
            return getTestClassSuperTypes();
        }

        /**
         * @since 2.0.0
         */
        @Override
        public String getTestFilePattern() {
            return "Test(?:Case)?\\.php$"; //$NON-NLS-1$
        }

        /**
         * @since 2.3.0
         */
        @Override
        public boolean isTestMethod(IMethod method) throws ModelException {
            if (super.isTestMethod(method)) return true;

            IType type = (IType) method.getParent();
            IMethod beforeMethod = null;
            for (IModelElement element: type.getChildren()) {
                if (!(element instanceof IMethod)) continue;
                if (!method.getElementName().equals(element.getElementName())) {
                    beforeMethod = (IMethod) element;
                    continue;
                }

                int startIndex = beforeMethod != null ?
                    beforeMethod.getSourceRange().getOffset() + beforeMethod.getSourceRange().getLength() :
                    type.getSourceRange().getOffset();
                String target = method.getSourceModule().getSource().substring(
                    startIndex, method.getSourceRange().getOffset());
                Pattern pattern = Pattern.compile("/\\*\\*[ |\t|\n].*@test.*\\*/", Pattern.MULTILINE + Pattern.DOTALL);
                Matcher matcher = pattern.matcher(target);
                return matcher.find();
            }
            return false;
        }
    },
    PHPSpec {
        /**
         * @since 2.0.0
         */
        @Override
        public String[] getTestClassSuperTypes() {
            return new String[] {
                "PHPSpec\\Context", //$NON-NLS-1$
            };
        }

        /**
         * @since 2.0.0
         */
        @Override
        public String[] getRequiredSuperTypes() {
            return getTestClassSuperTypes();
        }

        /**
         * @since 2.0.0
         */
        @Override
        public String getTestFilePattern() {
            return "Spec\\.php$"; //$NON-NLS-1$
        }
    };

    private static List<String> superTypesOfAllTestingFrameworks;

    /**
     * @since 1.6.0
     */
    public abstract String[] getTestClassSuperTypes();

    /**
     * @since 1.7.0
     */
    public abstract String[] getRequiredSuperTypes();

    /**
     * @since 2.0.0
     */
    public abstract String getTestFilePattern();

    /**
     * @since 2.3.0
     */
    public static boolean isTestClassSuperType(IType type) {
        if (superTypesOfAllTestingFrameworks == null) {
            superTypesOfAllTestingFrameworks = new ArrayList<String>();
            for (TestingFramework testingFramework: values()) {
                for (String superType: testingFramework.getTestClassSuperTypes()) {
                    superTypesOfAllTestingFrameworks.add(superType);
                }
            }
        }
        if (type == null) return false;
        for (String superType: superTypesOfAllTestingFrameworks) {
            if (type.getElementName().equals(superType)) return true;
        }
        return false;
    }

    /**
     * @since 2.3.0
     */
    public boolean isTestClass(IType type) {
        if (type== null) return false;
        if (!type.getScriptProject().isOpen()) return false;

        String[] testClassSuperTypes = getTestClassSuperTypes();

        try {
            // The PHPFlags class is not used because it fail the weaving.
            int flag = type.getFlags();
            boolean isNotClass = (flag & Modifiers.AccNameSpace) != 0
                                 || (flag & Modifiers.AccInterface) != 0;
            if (isNotClass) return false;
            for (String testClassSuperType: testClassSuperTypes) {
                if (hasTests(type, testClassSuperType)) {
                    return true;
                }
            }
        } catch (ModelException e) {
            Activator.getDefault().getLog().log(new Status(Status.WARNING, Activator.PLUGIN_ID, e.getMessage(), e));
        }

        return false;
    }

    /**
     * @since 2.3.0
     */
    public boolean isTestMethod(IMethod method) throws ModelException {
        if (method == null) return false;
        if (!(method.getParent() instanceof IType)
            || !isTestClass((IType) method.getParent())) return false;

        int flags = method.getFlags();
        if ((flags & Modifiers.AccPublic) == 0) return false;
        if ((flags & Modifiers.AccStatic) != 0) return false;

        if (method.getElementName().startsWith("test")) return true;
        return false;
    }

    private boolean hasTests(IType type, String testClassSuperType) throws ModelException {
        // TODO Type Hierarchy by PDT 2.1 does not work with namespaces.
        ITypeHierarchy hierarchy = type.newSupertypeHierarchy(new NullProgressMonitor());
        if (hierarchy == null) return false;
        IType[] supertypes = hierarchy.getAllSuperclasses(type);
        if (supertypes == null) return false;
        for (IType supertype: supertypes) {
            if (PHPClassType.fromIType(supertype).getTypeName().equals(testClassSuperType)) {
                return true;
            }
        }
        return false;
    }
}
