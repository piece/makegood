/**
 * Copyright (c) 2011 KUBO Atsuhiro <kubo@iteman.jp>,
 * All rights reserved.
 *
 * This file is part of MakeGood.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.piece_framework.makegood.ui;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.core.index2.search.ISearchEngine.MatchRule;
import org.eclipse.dltk.core.index2.search.ModelAccess;
import org.eclipse.dltk.core.search.SearchEngine;
import org.eclipse.php.internal.debug.core.preferences.PHPexeItem;

import com.piece_framework.makegood.core.preference.MakeGoodProperty;
import com.piece_framework.makegood.launch.PHPexeItemRepository;

/**
 * @since 1.6.0
 */
public class ProjectValidation {
    private static final String NATURE_ID_PHPNATURE = "org.eclipse.php.core.PHPNature"; //$NON-NLS-1$

    private int validationCount = 0;

    public boolean validate(IProject project) throws CoreException {
        ++validationCount;

        if (project == null) {
            MakeGoodContext.getInstance().updateStatus(MakeGoodStatus.NoProjectSelected);
            return false;
        }

        if (!project.exists()) {
            MakeGoodContext.getInstance().updateStatus(MakeGoodStatus.ProjectNotFound, project);
            return false;
        }

        if (!project.hasNature(NATURE_ID_PHPNATURE)) {
            MakeGoodContext.getInstance().updateStatus(MakeGoodStatus.NoTestableProjectSelected, project);
            return false;
        }

        PHPexeItem phpexeItem = new PHPexeItemRepository().findByProject(project);
        if (phpexeItem == null) {
            MakeGoodContext.getInstance().updateStatus(MakeGoodStatus.NoPHPExecutablesDefined, project);
            return false;
        }

        if (!PHPexeItem.SAPI_CLI.equals(phpexeItem.getSapiType())) {
            MakeGoodContext.getInstance().updateStatus(MakeGoodStatus.SAPINotCLI, project);
            return false;
        }

        MakeGoodProperty property = new MakeGoodProperty(project);
        if (!property.exists()) {
            MakeGoodContext.getInstance().updateStatus(MakeGoodStatus.MakeGoodNotConfigured, project);
            return false;
        }

        // TODO The ModelAccess().findTypes() method sometimes returns an empty array when starting up Eclipse.
        if (validationCount > 1) {
            for (String requiredSuperType: property.getTestingFramework().getRequiredSuperTypes()) {
                IType[] types = new ModelAccess().findTypes(
                    requiredSuperType,
                    MatchRule.EXACT,
                    0,
                    0,
                    SearchEngine.createSearchScope(DLTKCore.create(project)),
                    null
                );

                if (types == null || types.length == 0) {
                    MakeGoodStatus.TestingFrameworkNotAvailable.setReason(requiredSuperType);
                    MakeGoodContext.getInstance().updateStatus(MakeGoodStatus.TestingFrameworkNotAvailable, project);
                    return false;
                }
            }
        }

        return true;
    }
}
