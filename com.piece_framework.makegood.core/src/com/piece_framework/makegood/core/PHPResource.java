package com.piece_framework.makegood.core;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.IType;
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
        boolean isTestClass = false;
        try {
            for (IType type : source.getAllTypes()) {
                for (String superClass : type.getSuperClasses()) {
                    if (superClass.equals("PHPUnit_Framework_TestCase")) {
                        isTestClass = true;
                        break;
                    }
                }
                if (isTestClass) {
                    break;
                }
            }
        } catch (ModelException e) {
        }
        return isTestClass;
    }
}
