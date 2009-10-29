package com.piece_framework.makegood.core;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentType;

public class PHPResource {
    public static String CONTENT_TYPE = "org.eclipse.php.core.phpsource";

    public static boolean isTrue(IResource target) {
        if (!(target instanceof IFile)) {
            return false;
        }

        IContentType contentType = Platform.getContentTypeManager().getContentType(CONTENT_TYPE);
        return contentType.isAssociatedWith(target.getName());
    }
}
