/**
 * Copyright (c) 2012 KUBO Atsuhiro <kubo@iteman.jp>,
 * All rights reserved.
 *
 * This file is part of MakeGood.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.piece_framework.makegood.ui.launch;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IMethod;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.IType;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IEditorPart;

import com.piece_framework.makegood.core.result.TestCaseResult;
import com.piece_framework.makegood.launch.TestTargets;
import com.piece_framework.makegood.launch.ClassTestTarget;

/**
 * @since 2.1.0
 */
public class FailedTestsLaunchShortcut extends MakeGoodLaunchShortcut {
    private List<TestCaseResult> failures;
    private MakeGoodLaunchShortcut lastShortcut;

    public FailedTestsLaunchShortcut(List<TestCaseResult> failures, MakeGoodLaunchShortcut lastShortcut) {
        this.failures = failures;
        this.lastShortcut = lastShortcut;
    }

    @Override
    public void launch(ISelection selection, String mode) {
        clearTestTargets();
        addFailedTestsAsTestTargets();
        if (TestTargets.getInstance().getCount() > 0) {
            IResource mainScriptResource = TestTargets.getInstance().getMainScriptResource();
            if (mainScriptResource == null) throw new TestLaunchException();

            super.launch(new StructuredSelection(mainScriptResource), mode);
        } else {
            lastShortcut.launch(selection, mode);
        }
    }

    @Override
    public void launch(IEditorPart editor, String mode) {
        clearTestTargets();
        addFailedTestsAsTestTargets();
        if (TestTargets.getInstance().getCount() > 0) {
            super.launch(editor, mode);
        } else {
            lastShortcut.launch(editor, mode);
        }
    }

    public MakeGoodLaunchShortcut getLastShortcut() {
        return lastShortcut;
    }

    private void addFailedTestsAsTestTargets() {
        for (TestCaseResult failure: failures) {
            IFile file = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(new Path(failure.getParent().getFile()));
            if (file == null) continue;

            IModelElement sourceModule = DLTKCore.create(file);
            if (!(sourceModule instanceof ISourceModule)) continue;

            IType type = ((ISourceModule) sourceModule).getType(failure.getParent().getClassName());
            if (type == null) continue;

            if (failure.getClassName() == null) {
                addTestTarget(new ClassTestTarget(type));
                continue;
            }

            IMethod method = type.getMethod(failure.getMethodName());
            if (method == null) continue;

            addTestTarget(method);
        }
    }
}
