/**
 * Copyright (c) 2012 MATSUFUJI Hideharu <matsufuji2008@gmail.com>,
 *               2011-2012 KUBO Atsuhiro <kubo@iteman.jp>,
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
         * @since 1.x.0
         */
        @Override
        protected boolean checkTestMethod(IMethod method) throws ModelException {
            if (super.checkTestMethod(method)) return true;

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
    SimpleTest {
        /**
         * @since 1.6.0
         */
        @Override
        public String[] getTestClassSuperTypes() {
            return new String[] {
                "SimpleTestCase", //$NON-NLS-1$
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
         * @since 1.x.0
         */
        @Override
        public boolean isTestMethod(IMethod method) throws ModelException {
            if (!super.isTestMethod(method)) return false;

            if (method.getElementName().startsWith("test")) return true;
            return false;
        }
    },
    CakePHP {
        /**
         * @since 1.6.0
         */
        @Override
        public String[] getTestClassSuperTypes() {
            return new String[] {
                "CakeTestCase", //$NON-NLS-1$
                "CakeWebTestCase", //$NON-NLS-1$
            };
        }

        /**
         * @since 1.7.0
         */
        @Override
        public String[] getRequiredSuperTypes() {
            return new String[] {
                "SimpleTestCase", //$NON-NLS-1$
                "CakeTestCase", //$NON-NLS-1$
                "CakeWebTestCase", //$NON-NLS-1$
            };
        }

        /**
         * @since 2.0.0
         */
        @Override
        public String getTestFilePattern() {
            return "\\.test\\.php$"; //$NON-NLS-1$
        }
    },
    CIUnit {
        /**
         * @since 1.6.0
         */
        @Override
        public String[] getTestClassSuperTypes() {
            return new String[] {
                "CIUnit_TestCase", //$NON-NLS-1$
                "CIUnit_TestCase_Selenium", //$NON-NLS-1$
            };
        }

        /**
         * @since 1.7.0
         */
        @Override
        public String[] getRequiredSuperTypes() {
            return new String[] {
                "PHPUnit_Framework_TestCase", //$NON-NLS-1$
                "CIUnit_TestCase", //$NON-NLS-1$
                "CIUnit_TestCase_Selenium", //$NON-NLS-1$
            };
        }

        /**
         * @since 2.0.0
         */
        @Override
        public String getTestFilePattern() {
            return "^test.+\\.php$"; //$NON-NLS-1$
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
     * @since 1.x.0
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
     * @since 1.x.0
     */
    public boolean isTestClass(IType type) {
        if (type== null) return false;
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
     * @since 1.x.0
     */
    public boolean isTestMethod(IMethod method) throws ModelException {
        if (method == null) return false;
        if (!(method.getParent() instanceof IType)
            || !isTestClass((IType) method.getParent())) return false;

        int flags = method.getFlags();
        if ((flags & Modifiers.AccPublic) == 0) return false;
        if ((flags & Modifiers.AccStatic) != 0) return false;

        if (checkTestMethod(method)) return true;
        return false;
    }

    /**
     * @since 1.x.0
     */
    protected boolean checkTestMethod(IMethod method) throws ModelException {
        return method.getElementName().startsWith("test");
    };

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
