/**
 * Copyright (c) 2009-2010 MATSUFUJI Hideharu <matsufuji2008@gmail.com>,
 *               2010-2012 KUBO Atsuhiro <kubo@iteman.jp>,
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
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentType;

public class Resource {
    /**
     * @since 2.2.0
     */
    private IResource subject;

    /**
     * @since 2.2.0
     */
    private IContentType phpContentType;

    /**
     * @since 2.2.0
     */
    public Resource(IResource subject) {
        this.subject = subject;
        phpContentType = Platform.getContentTypeManager().getContentType(phpContentTypeID());
    }

    public boolean isPHPSource() {
        if ((subject instanceof IFile) == false) return false;
        return phpContentType.isAssociatedWith(subject.getName());
    }

    /**
     * @since 2.2.0
     */
    private String phpContentTypeID() {
        return "org.eclipse.php.core.phpsource"; //$NON-NLS-1$
    }
}
