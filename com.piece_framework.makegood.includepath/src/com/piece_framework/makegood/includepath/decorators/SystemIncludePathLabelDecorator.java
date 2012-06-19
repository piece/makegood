/**
 * Copyright (c) 2010 MATSUFUJI Hideharu <matsufuji2008@gmail.com>,
 * All rights reserved.
 *
 * This file is part of MakeGood.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.piece_framework.makegood.includepath.decorators;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.php.internal.core.includepath.IncludePath;
import org.eclipse.swt.graphics.Image;

import com.piece_framework.makegood.includepath.ConfigurationIncludePath;

public class SystemIncludePathLabelDecorator implements ILabelDecorator {
    @Override
    public String decorateText(String text, Object element) {
        if (!(element instanceof IncludePath)) return text;
        IncludePath includePath = (IncludePath) element;
        if (!(includePath.getEntry() instanceof IResource)) return text;

        ConfigurationIncludePath configuration =
            new ConfigurationIncludePath(includePath.getProject());
        if (configuration.equalsDummyResource((IResource) includePath.getEntry())) {
            return ConfigurationIncludePath.text;
        }
        return text;
    }

    @Override
    public void addListener(ILabelProviderListener listener) {}

    @Override
    public void dispose() {}

    @Override
    public boolean isLabelProperty(Object element, String property) {
        return false;
    }

    @Override
    public void removeListener(ILabelProviderListener listener) {}

    @Override
    public Image decorateImage(Image image, Object element) {
        return null;
    }
}
