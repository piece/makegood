/**
 * Copyright (c) 2009-2010 MATSUFUJI Hideharu <matsufuji2008@gmail.com>,
 *               2010-2011 KUBO Atsuhiro <kubo@iteman.jp>,
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
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

import com.piece_framework.makegood.aspect.AspectWeaver;
import com.piece_framework.makegood.core.PHPResource;
import com.piece_framework.makegood.ui.EditorParser;
import com.piece_framework.makegood.ui.launch.TestRunner;

public class RunTestFromEditorHandlerInContext extends AbstractHandler {
    private LastCheckedSource lastCheckedSource;

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        TestRunner.runTestsInContext(HandlerUtil.getActiveEditor(event));
        return null;
    }

    @Override
    public boolean isEnabled() {
        if (!AspectWeaver.isFinished()) return false;

        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (window == null) return false;
        IWorkbenchPage page = window.getActivePage();
        if (page == null) return false;
        IEditorPart editor = page.getActiveEditor();
        if (editor == null) return false;

        ISourceModule sourceModule = new EditorParser(editor).getSourceModule();
        if (sourceModule == null) {
            lastCheckedSource = null;
            return false;
        }

        if (lastCheckedSource == null || !lastCheckedSource.equals(sourceModule)) {
            lastCheckedSource = new LastCheckedSource(sourceModule, PHPResource.hasTests(sourceModule));
        }

        if (!lastCheckedSource.hasTests()) {
            return false;
        }

        return super.isEnabled();
    }

    /**
     * @since 1.2.0
     */
    private class LastCheckedSource {
        private ISourceModule sourceModule;
        private boolean hasTests;

        public LastCheckedSource(ISourceModule sourceModule, boolean hasTests) {
            this.sourceModule = sourceModule;
            this.hasTests = hasTests;
        }

        public boolean equals(ISourceModule sourceModule) {
            return this.sourceModule.equals(sourceModule);
        }

        public boolean hasTests() {
            return hasTests;
        }
    }
}
