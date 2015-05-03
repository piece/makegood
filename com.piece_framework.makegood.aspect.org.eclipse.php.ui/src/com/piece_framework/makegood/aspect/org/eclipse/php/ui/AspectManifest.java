/**
 * Copyright (c) 2010-2011, 2014-2015 KUBO Atsuhiro <kubo@iteman.jp>,
 * All rights reserved.
 *
 * This file is part of MakeGood.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.piece_framework.makegood.aspect.org.eclipse.php.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.Platform;

import com.piece_framework.makegood.aspect.Aspect;
import com.piece_framework.makegood.aspect.PDTVersion;
import com.piece_framework.makegood.aspect.org.eclipse.php.ui.aspect.SystemIncludePathAspect;

public class AspectManifest implements com.piece_framework.makegood.aspect.AspectManifest {
    private static List<String> DEPENDENCIES = new ArrayList<String>();

    static {
        if (PDTVersion.getInstance().compareTo("3.5.0") < 0) { //$NON-NLS-1$
            DEPENDENCIES.addAll(Arrays.asList(
                "org.eclipse.php.ui", //$NON-NLS-1$
                "org.eclipse.dltk.ui", //$NON-NLS-1$
                "org.eclipse.jface", //$NON-NLS-1$
                Fragment.PLUGIN_ID, "org.eclipse.core.commands" //$NON-NLS-1$
            ));

            String os = Platform.getOS();
            String osArch = Platform.getOSArch();
            if (Platform.OS_LINUX.equals(os)) {
                if (Platform.ARCH_X86_64.equals(osArch)) {
                    DEPENDENCIES.add("org.eclipse.swt.gtk.linux.x86_64"); //$NON-NLS-1$
                } else if (Platform.ARCH_X86.equals(osArch)) {
                    DEPENDENCIES.add("org.eclipse.swt.gtk.linux.x86"); //$NON-NLS-1$
                }
            } else if (Platform.OS_MACOSX.equals(os)) {
                if (Platform.ARCH_X86_64.equals(osArch)) {
                    DEPENDENCIES.add("org.eclipse.swt.cocoa.macosx.x86_64"); //$NON-NLS-1$
                } else if (Platform.ARCH_X86.equals(osArch)) {
                    DEPENDENCIES.add("org.eclipse.swt.cocoa.macosx"); //$NON-NLS-1$
                }
            } else if (Platform.OS_WIN32.equals(os)) {
                if (Platform.ARCH_X86_64.equals(osArch)) {
                    DEPENDENCIES.add("org.eclipse.swt.win32.win32.x86_64"); //$NON-NLS-1$
                } else if (Platform.ARCH_X86.equals(osArch)) {
                    DEPENDENCIES.add("org.eclipse.swt.win32.win32.x86"); //$NON-NLS-1$
                }
            }
        }
    }

    @Override
    public String pluginId() {
        return Fragment.PLUGIN_ID;
    }

    @Override
    public Aspect[] aspects() {
        if (PDTVersion.getInstance().compareTo("3.5.0") >= 0) { //$NON-NLS-1$
            return new Aspect[] {
            };
        } else {
            return new Aspect[] {
                new SystemIncludePathAspect(),
            };
        }
    }

    @Override
    public String[] dependencies() {
        return DEPENDENCIES.toArray(new String[ DEPENDENCIES.size() ]);
    }
}
