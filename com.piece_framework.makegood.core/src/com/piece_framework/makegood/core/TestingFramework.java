/**
 * Copyright (c) 2009 MATSUFUJI Hideharu <matsufuji2008@gmail.com>,
 *               2011 KUBO Atsuhiro <kubo@iteman.jp>,
 * All rights reserved.
 *
 * This file is part of MakeGood.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.piece_framework.makegood.core;

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
    };

    /**
     * @since 1.6.0
     */
    public abstract String[] getTestClassSuperTypes();
}
