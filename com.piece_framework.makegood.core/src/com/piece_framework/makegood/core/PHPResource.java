package com.piece_framework.makegood.core;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.core.ITypeHierarchy;
import org.eclipse.dltk.core.ModelException;

public class PHPResource {
    public static String CONTENT_TYPE = "org.eclipse.php.core.phpsource";

    public static boolean isTrue(IResource target) {
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
        }
        return false;
    }

    private static boolean isTestClass(IType type) throws ModelException {
        for (String superClass: type.getSuperClasses()) {
            if (superClass.equals("PHPUnit_Framework_TestCase")) {
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
}
