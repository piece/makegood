/**
 * Copyright (c) 2009-2010 MATSUFUJI Hideharu <matsufuji2008@gmail.com>,
 *               2010-2013 KUBO Atsuhiro <kubo@iteman.jp>,
 * All rights reserved.
 *
 * This file is part of MakeGood.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.piece_framework.makegood.ui.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Status;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

import com.piece_framework.makegood.core.PHPSourceModule;
import com.piece_framework.makegood.core.preference.MakeGoodProperties;
import com.piece_framework.makegood.ui.Activator;
import com.piece_framework.makegood.ui.EditorParser;

public class RunTestFromEditorHandlerInContext extends RunHandler {
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        getTestRunner().runTestsInContext(HandlerUtil.getActiveEditor(event));
        return null;
    }

    @Override
    public boolean isEnabled() {
        if (!super.isEnabled()) return false;

        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (window == null) return false;
        IWorkbenchPage page = window.getActivePage();
        if (page == null) return false;
        IEditorPart editor = page.getActiveEditor();
        if (editor == null) return false;
        ISourceModule sourceModule = new EditorParser(editor).getSourceModule();
        if (sourceModule == null) return false;
        IResource resource = sourceModule.getResource();
        if (resource == null) return false;

        try {
            return new PHPSourceModule(sourceModule, new MakeGoodProperties(resource).getTestingFramework()).hasRunnableTestTypes();
        } catch (CoreException e) {
            Activator.getDefault().getLog().log(new Status(Status.ERROR, Activator.PLUGIN_ID, e.getMessage(), e));
            return false;
        }
    }
}
