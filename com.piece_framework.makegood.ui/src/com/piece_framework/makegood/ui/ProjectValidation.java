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
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.core.index2.search.ISearchEngine.MatchRule;
import org.eclipse.dltk.core.index2.search.ModelAccess;
import org.eclipse.dltk.core.search.SearchEngine;
import org.eclipse.php.internal.debug.core.preferences.PHPexeItem;

import com.piece_framework.makegood.core.MakeGoodProperty;
import com.piece_framework.makegood.launch.PHPexeItemRepository;

/**
 * @since 1.6.0
 */
public class ProjectValidation {
    private int validationCount = 0;

    synchronized public boolean validate(IProject project) {
        ++validationCount;

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
            for (String testClassSuperType: property.getTestingFramework().getTestClassSuperTypes()) {
                IType[] types = new ModelAccess().findTypes(
                    testClassSuperType,
                    MatchRule.EXACT,
                    0,
                    0,
                    SearchEngine.createSearchScope(DLTKCore.create(project)),
                    null
                );

                if (types == null || types.length == 0) {
                    MakeGoodContext.getInstance().updateStatus(MakeGoodStatus.TestingFrameworkNotAvailable, project);
                    return false;
                }

                break;
            }
        }

        return true;
    }
}
