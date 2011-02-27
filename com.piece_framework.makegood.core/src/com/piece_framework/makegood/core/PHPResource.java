/**
 * Copyright (c) 2009-2010 MATSUFUJI Hideharu <matsufuji2008@gmail.com>,
 *               2010-2011 KUBO Atsuhiro <kubo@iteman.jp>,
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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.core.ITypeHierarchy;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.php.internal.core.typeinference.PHPClassType;

public class PHPResource {
    public static String CONTENT_TYPE = "org.eclipse.php.core.phpsource"; //$NON-NLS-1$

    public static boolean isPHPSource(IResource target) {
        if (!(target instanceof IFile)) return false;

        IContentType contentType = Platform.getContentTypeManager().getContentType(CONTENT_TYPE);
        return contentType.isAssociatedWith(target.getName());
    }

    public static boolean hasTests(ISourceModule source) {
        if (source == null) return false;
        IResource resource = source.getResource();
        if (resource == null) return false;
        List<String> testClassSuperTypes = getTestClassSuperType(resource);

        try {
            for (IType type: source.getAllTypes()) {
                if (!PHPFlags.isClass(type.getFlags())) continue;
                if (PHPFlags.isAbstract(type.getFlags())) continue;
                for (String testClassSuperType: testClassSuperTypes) {
                    if (hasTests(type, testClassSuperType)) {
                        return true;
                    }
                }
            }
        } catch (ModelException e) {
            MakeGoodCorePlugin.getDefault().getLog().log(new Status(Status.WARNING, MakeGoodCorePlugin.PLUGIN_ID, e.getMessage(), e));
        }

        return false;
    }

    /**
     * @since 1.2.0
     */
    private static boolean hasTests(IType type, String testClassSuperType) throws ModelException {
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

    private static List<String> getTestClassSuperType(IResource resource) {
        List<String> testClassSuperTypes = new ArrayList<String>();
        MakeGoodProperty property = new MakeGoodProperty(resource);
        switch (property.getTestingFramework()) {
        case PHPUnit:
            testClassSuperTypes.add("PHPUnit_Framework_TestCase"); //$NON-NLS-1$
            break;
        case SimpleTest:
            testClassSuperTypes.add("SimpleTestCase"); //$NON-NLS-1$
            break;
        case CakePHP:
            testClassSuperTypes.add("CakeTestCase"); //$NON-NLS-1$
            testClassSuperTypes.add("CakeWebTestCase"); //$NON-NLS-1$
            break;
        case CIUnit:
            testClassSuperTypes.add("CIUnit_TestCase"); //$NON-NLS-1$
            testClassSuperTypes.add("CIUnit_TestCase_Selenium"); //$NON-NLS-1$
            break;
        default:
            break;
        }

        return testClassSuperTypes;
    }
}
