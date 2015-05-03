/**
 * Copyright (c) 2010-2011, 2015 KUBO Atsuhiro <kubo@iteman.jp>,
 * All rights reserved.
 *
 * This file is part of MakeGood.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.piece_framework.makegood.aspect.org.eclipse.php.core;

import com.piece_framework.makegood.aspect.Aspect;
import com.piece_framework.makegood.aspect.PDTVersion;
import com.piece_framework.makegood.aspect.org.eclipse.php.core.aspect.MultibyteCharactersAspect;
import com.piece_framework.makegood.aspect.org.eclipse.php.core.aspect.SystemIncludePathAspect;

public class AspectManifest implements com.piece_framework.makegood.aspect.AspectManifest {
    @Override
    public String pluginId() {
        return Fragment.PLUGIN_ID;
    }

    @Override
    public Aspect[] aspects() {
        if (PDTVersion.getInstance().compareTo("3.5.0") >= 0) { //$NON-NLS-1$
            return new Aspect[] {
                new MultibyteCharactersAspect(),
            };
        } else {
            return new Aspect[] {
                new SystemIncludePathAspect(),
                new MultibyteCharactersAspect(),
            };
        }
    }

    @Override
    public String[] dependencies() {
        if (PDTVersion.getInstance().compareTo("3.5.0") >= 0) { //$NON-NLS-1$
            return new String[] {
                "org.eclipse.php.core", //$NON-NLS-1$
            };
        } else {
            return new String[] {
                "org.eclipse.php.core", //$NON-NLS-1$
                "org.eclipse.core.resources", //$NON-NLS-1$
                "org.eclipse.dltk.core", //$NON-NLS-1$
                "com.piece_framework.makegood.includepath", //$NON-NLS-1$
            };
        }
    }
}
