/**
 * Copyright (c) 2010 MATSUFUJI Hideharu <matsufuji2008@gmail.com>,
 *               2010-2012 KUBO Atsuhiro <kubo@iteman.jp>,
 * All rights reserved.
 *
 * This file is part of MakeGood.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.piece_framework.makegood.ui.launch;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;

import com.piece_framework.makegood.core.PHPResource;
import com.piece_framework.makegood.core.preference.MakeGoodProperty;
import com.piece_framework.makegood.launch.TestTargetRepository;

public class AllTestsLaunchShortcut extends MakeGoodLaunchShortcut {
    @Override
    public void launch(ISelection selection, String mode) {
        clearTestTargets();

        if (!(selection instanceof IStructuredSelection)) throw new NotLaunchedException();

        Object target = ((IStructuredSelection) selection).getFirstElement();
        IResource resource = null;
        if (target instanceof IModelElement) {
            resource = ((IModelElement) target).getResource();
        } else if (target instanceof IResource) {
            resource = (IResource) target;
        }
        if (resource == null) throw new NotLaunchedException();

        addTestFoldersAsTestTargets(resource);

        IResource mainScriptResource = TestTargetRepository.getInstance().getMainScriptResource();
        if (mainScriptResource == null) throw new NotLaunchedException();

        super.launch(new StructuredSelection(mainScriptResource), mode);
    }

    @Override
    public void launch(IEditorPart editor, String mode) {
        clearTestTargets();

        if (!(editor.getEditorInput() instanceof IFileEditorInput)) throw new NotLaunchedException();

        IFile target = ((IFileEditorInput) editor.getEditorInput()).getFile();
        if (!PHPResource.isPHPSource(target)) {
            ISelection selection = new StructuredSelection(target);
            launch(selection, mode);
            return;
        }

        addTestFoldersAsTestTargets(target);

        super.launch(editor, mode);
    }

    private void addTestFoldersAsTestTargets(IResource resource) {
        for (IFolder testFolder: new MakeGoodProperty(resource).getTestFolders()) {
            addTestTarget(testFolder);
        }
    }
}
