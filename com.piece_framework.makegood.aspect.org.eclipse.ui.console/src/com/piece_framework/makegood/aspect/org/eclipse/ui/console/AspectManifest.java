/**
 * Copyright (c) 2014 KUBO Atsuhiro <kubo@iteman.jp>,
 * All rights reserved.
 *
 * This file is part of MakeGood.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.piece_framework.makegood.aspect.org.eclipse.ui.console;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.Platform;

import com.piece_framework.makegood.aspect.Aspect;
import com.piece_framework.makegood.aspect.org.eclipse.ui.console.aspect.ConsoleViewAspect;

public class AspectManifest implements com.piece_framework.makegood.aspect.AspectManifest {
    private static final Aspect[] ASPECTS = {
        new ConsoleViewAspect(),
    };
    private static List<String> DEPENDENCIES = new ArrayList<String>(Arrays.asList(
        "org.eclipse.ui.console" //$NON-NLS-1$
    ));

    static {
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

    @Override
    public String pluginId() {
        return Fragment.PLUGIN_ID;
    }

    @Override
    public Aspect[] aspects() {
        return ASPECTS;
    }

    @Override
    public String[] dependencies() {
        return DEPENDENCIES.toArray(new String[ DEPENDENCIES.size() ]);
    }
}
