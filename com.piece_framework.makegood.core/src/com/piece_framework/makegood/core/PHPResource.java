/**
 * Copyright (c) 2009-2010 MATSUFUJI Hideharu <matsufuji2008@gmail.com>,
 * All rights reserved.
 *
 * This file is part of MakeGood.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.piece_framework.makegood.core;

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

public class PHPResource {
    public static String CONTENT_TYPE = "org.eclipse.php.core.phpsource"; //$NON-NLS-1$

    public static boolean isPHPSource(IResource target) {
        if (!(target instanceof IFile)) {
            return false;
        }

        IContentType contentType = Platform.getContentTypeManager().getContentType(CONTENT_TYPE);
        return contentType.isAssociatedWith(target.getName());
    }

    public static boolean includeTestClass(ISourceModule source) {
        if (source == null) {
            return false;
        }

        try {
            for (IType type : source.getAllTypes()) {
                if (isTestClass(type)) {
                    return true;
                }
            }
        } catch (ModelException e) {
            Activator.getDefault().getLog().log(
                new Status(
                    Status.WARNING,
                    Activator.PLUGIN_ID,
                    e.getMessage(),
                    e
                )
            );
        }
        return false;
    }

    private static boolean isTestClass(IType type) throws ModelException {
        if (type == null) {
            return false;
        }

        String testClass = getTestClass(type);
        for (String superClass: type.getSuperClasses()) {
            if (superClass.equals(testClass)) { //$NON-NLS-1$
                return true;
            }
        }
        ITypeHierarchy hierarchy = type.newTypeHierarchy(new NullProgressMonitor());
        if (hierarchy != null) {
            for (IType superClass : hierarchy.getAllSupertypes(type)) {
                if (isTestClass(superClass)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static String getTestClass(IType type) {
        MakeGoodProperty property = new MakeGoodProperty(type.getResource());
        if (property.usePHPUnit()) {
            return "PHPUnit_Framework_TestCase"; //$NON-NLS-1$
        } else if (property.useSimpleTest()) {
            return "UnitTestCase"; //$NON-NLS-1$
        } else {
            return "PHPUnit_Framework_TestCase"; //$NON-NLS-1$
        }
    }
}
