/**
 * Copyright (c) 2009-2010 MATSUFUJI Hideharu <matsufuji2008@gmail.com>,
 *               2010 KUBO Atsuhiro <kubo@iteman.jp>,
 * All rights reserved.
 *
 * This file is part of MakeGood.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.piece_framework.makegood.ui.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

import com.piece_framework.makegood.core.PHPResource;
import com.piece_framework.makegood.javassist.monitor.WeavingMonitor;
import com.piece_framework.makegood.ui.launch.EditorParser;
import com.piece_framework.makegood.ui.launch.TestRunner;

public class RunTestFromEditor extends AbstractHandler {
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        TestRunner.runTestsInContext(HandlerUtil.getActiveEditor(event));
        return null;
    }

    @Override
    public boolean isEnabled() {
        if (!WeavingMonitor.endAll()) return false;

        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (window == null) return false;
        IWorkbenchPage page = window.getActivePage();
        if (page == null) return false;
        IEditorPart editor = page.getActiveEditor();
        if (editor == null) return false;
        if (!PHPResource.includesTests(new EditorParser(editor).getSourceModule())) return false;

        return super.isEnabled();
    }
}
