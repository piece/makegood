/**
 * Copyright (c) 2010 MATSUFUJI Hideharu <matsufuji2008@gmail.com>,
 *               2010 KUBO Atsuhiro <kubo@iteman.jp>,
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
import org.eclipse.dltk.core.search.IDLTKSearchConstants;
import org.eclipse.dltk.core.search.IDLTKSearchScope;
import org.eclipse.dltk.core.search.SearchEngine;
import org.eclipse.dltk.core.search.SearchMatch;
import org.eclipse.dltk.core.search.SearchParticipant;
import org.eclipse.dltk.core.search.SearchPattern;
import org.eclipse.dltk.core.search.SearchRequestor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.framework.debug.Debug;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.texteditor.ITextEditor;

import com.piece_framework.makegood.core.PHPResource;
import com.piece_framework.makegood.launch.CommandLineGenerator;
import com.piece_framework.makegood.ui.Activator;
import com.piece_framework.makegood.ui.Messages;

public class RelatedTestsLaunchShortcut extends MakeGoodLaunchShortcut {
    @Override
    public void launch(IEditorPart editor, String mode) {
        if (!(editor instanceof ITextEditor)) {
            return;
        }

        CommandLineGenerator parameter = CommandLineGenerator.getInstance();
        parameter.clearTargets();

        launchTestsForProductCode(editor, mode);
    }

    private void launchTestsForProductCode(final IEditorPart editor,
                                           final String mode
                                           ) {
        SearchRequestor requestor = new SearchRequestor() {
            Set<IResource> tests = new HashSet<IResource>();

            @Override
            public void acceptSearchMatch(SearchMatch match) throws CoreException {
                IModelElement element = DLTKCore.create(match.getResource());
                if (!(element instanceof ISourceModule)) {
                    return;
                }
                if (!PHPResource.includeTestClass((ISourceModule) element)) {
                    return;
                }

                tests.add(match.getResource());
            }

            @Override
            public void endReporting() {
                EditorParser parser = new EditorParser(editor);
                ISourceModule source = parser.getSourceModule();
                if (source != null && PHPResource.includeTestClass(source)) {
                    tests.add(source.getResource());
                }

                if (tests.size() == 0) {
                    MessageDialog.openInformation(editor.getEditorSite().getShell(),
                                                  Messages.MakeGoodLaunchShortcut_messageTitle,
                                                  Messages.MakeGoodLaunchShortcut_notFoundTestsMessage
                                                  );
                    return;
                }

                CommandLineGenerator parameter = CommandLineGenerator.getInstance();
                parameter.clearTargets();
                for (IResource test: tests) {
                    Debug.println(test);
                    parameter.addTarget(test);
                }
                RelatedTestsLaunchShortcut.super.launch(editor, mode);
            }
        };

        List<IType> types = new EditorParser(editor).getTypes();
        if (types == null || types.size() == 0) {
            return;
        }

        IDLTKLanguageToolkit toolkit = DLTKLanguageManager.getLanguageToolkit(types.get(0));
        if (toolkit == null) {
            return;
        }
        StringBuilder patternString = new StringBuilder();
        for (IType type: types) {
            Debug.println(type.getElementName());
            patternString.append(patternString.length() > 0 ? "|" : ""); //$NON-NLS-1$ //$NON-NLS-2$
            patternString.append(type.getElementName());
        }
        SearchPattern pattern = SearchPattern.createPattern(patternString.toString(),
                                                            IDLTKSearchConstants.TYPE,
                                                            IDLTKSearchConstants.REFERENCES,
                                                            SearchPattern.R_REGEXP_MATCH,
                                                            toolkit
                                                            );
        IDLTKSearchScope scope = SearchEngine.createSearchScope(types.get(0).getScriptProject());
        SearchEngine engine = new SearchEngine();
        try {
            engine.search(pattern,
                          new SearchParticipant[] {SearchEngine.getDefaultSearchParticipant()},
                          scope,
                          requestor,
                          null
                          );
        } catch (CoreException e) {
            Activator.getDefault().getLog().log(
                new Status(
                    Status.ERROR,
                    Activator.PLUGIN_ID,
                    e.getMessage(),
                    e
                )
            );
        }
    }
}
