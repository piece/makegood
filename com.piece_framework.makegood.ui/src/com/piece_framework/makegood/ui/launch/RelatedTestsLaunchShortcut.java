/**
 * Copyright (c) 2010 MATSUFUJI Hideharu <matsufuji2008@gmail.com>,
 *               2010-2011 KUBO Atsuhiro <kubo@iteman.jp>,
 * All rights reserved.
 *
 * This file is part of MakeGood.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.piece_framework.makegood.ui.launch;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Status;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.DLTKLanguageManager;
import org.eclipse.dltk.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.core.search.IDLTKSearchConstants;
import org.eclipse.dltk.core.search.SearchEngine;
import org.eclipse.dltk.core.search.SearchMatch;
import org.eclipse.dltk.core.search.SearchParticipant;
import org.eclipse.dltk.core.search.SearchPattern;
import org.eclipse.dltk.core.search.SearchRequestor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.php.internal.core.typeinference.PHPClassType;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.texteditor.ITextEditor;

import com.piece_framework.makegood.core.PHPFlags;
import com.piece_framework.makegood.core.PHPResource;
import com.piece_framework.makegood.launch.TestingTargets;
import com.piece_framework.makegood.launch.TestLifecycle;
import com.piece_framework.makegood.ui.Activator;
import com.piece_framework.makegood.ui.Messages;
import com.piece_framework.makegood.ui.views.EditorParser;

public class RelatedTestsLaunchShortcut extends MakeGoodLaunchShortcut {
    @Override
    public void launch(IEditorPart editor, String mode) {
        if (!(editor instanceof ITextEditor)) throw new NotLaunchedException();
        launchTestsRelatedTo(editor, mode);
    }

    private void launchTestsRelatedTo(final IEditorPart editor, final String mode) {
        SearchRequestor requestor = new SearchRequestor() {
            Set<IResource> tests = new HashSet<IResource>();
            Set<IResource> searchMatches = new HashSet<IResource>();

            @Override
            public void acceptSearchMatch(SearchMatch match) throws CoreException {
                IResource resource = match.getResource();
                if (searchMatches.contains(resource)) return;
                searchMatches.add(resource);

                IModelElement element = DLTKCore.create(resource);
                if (!(element instanceof ISourceModule)) return;
                if (!PHPResource.hasTests((ISourceModule) element)) return;
                tests.add(resource);
            }

            @Override
            public void endReporting() {
                ISourceModule source = new EditorParser(editor).getSourceModule();
                if (source != null && PHPResource.hasTests(source)) {
                    tests.add(source.getResource());
                }

                if (tests.size() == 0) {
                    MessageDialog.openInformation(
                        editor.getEditorSite().getShell(),
                        Messages.MakeGoodLaunchShortcut_messageTitle,
                        Messages.MakeGoodLaunchShortcut_notFoundTestsMessage
                    );
                    TestLifecycle.destroy();
                    return;
                }

                for (IResource test: tests) {
                    TestLifecycle.getInstance().getTestingTargets().add(test);
                }
                RelatedTestsLaunchShortcut.super.launch(editor, mode);
            }
        };

        List<IType> types = new EditorParser(editor).getTypes();
        if (types == null || types.size() == 0) throw new NotLaunchedException();

        IDLTKLanguageToolkit toolkit = DLTKLanguageManager.getLanguageToolkit(types.get(0));
        if (toolkit == null) throw new NotLaunchedException();

        SearchPattern pattern = null;
        for (IType type: types) {
            int flags;
            try {
                flags = type.getFlags();
            } catch (ModelException e) {
                Activator.getDefault().getLog().log(new Status(Status.WARNING, Activator.PLUGIN_ID, e.getMessage(), e));
                continue;
            }

            if (!PHPFlags.isClass(flags)) continue;

            SearchPattern patternForType =
                SearchPattern.createPattern(
                    PHPClassType.fromIType(type).getTypeName(),
                    IDLTKSearchConstants.TYPE,
                    IDLTKSearchConstants.REFERENCES,
                    SearchPattern.R_FULL_MATCH,
                    toolkit
                );
            if (pattern == null) {
                pattern = patternForType;
            } else {
                pattern = SearchPattern.createOrPattern(pattern, patternForType);
            }
        }

        try {
            new SearchEngine().search(
                pattern,
                new SearchParticipant[] { SearchEngine.getDefaultSearchParticipant() },
                SearchEngine.createSearchScope(types.get(0).getScriptProject()),
                requestor,
                null
            );
        } catch (CoreException e) {
            Activator.getDefault().getLog().log(new Status(Status.ERROR, Activator.PLUGIN_ID, e.getMessage(), e));
            throw new NotLaunchedException();
        }
    }
}
