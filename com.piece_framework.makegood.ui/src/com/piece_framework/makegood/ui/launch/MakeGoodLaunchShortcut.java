/**
 * Copyright (c) 2009-2010 MATSUFUJI Hideharu <matsufuji2008@gmail.com>,
 *               2011 KUBO Atsuhiro <kubo@iteman.jp>,
 * All rights reserved.
 *
 * This file is part of MakeGood.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.piece_framework.makegood.ui.launch;

import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.php.internal.debug.ui.launching.PHPExeLaunchShortcut;
import org.eclipse.ui.IEditorPart;

import com.piece_framework.makegood.launch.Activator;
import com.piece_framework.makegood.launch.ProjectNotFoundException;
import com.piece_framework.makegood.launch.TestTargets;
import com.piece_framework.makegood.launch.ResourceNotFoundException;

public class MakeGoodLaunchShortcut extends PHPExeLaunchShortcut {
    @Override
    public void launch(final ISelection selection, final String mode) {
        super.launch(selection, mode);
    }

    @Override
    public void launch(IEditorPart editor, String mode) {
        super.launch(editor, mode);
    }

    @Override
    protected ILaunchConfigurationType getPHPExeLaunchConfigType() {
        return DebugPlugin.getDefault()
                          .getLaunchManager()
                          .getLaunchConfigurationType("com.piece_framework.makegood.launch.makegoodLaunchConfigurationType"); //$NON-NLS-1$
    }

    /**
     * @since 1.3.0
     */
    protected void addTestTarget(Object testTarget) {
        try {
            TestTargets.getInstance().add(testTarget);
        } catch (ResourceNotFoundException e) {
            Activator.getDefault().getLog().log(new Status(Status.ERROR, Activator.PLUGIN_ID, e.getMessage(), e));
            throw new NotLaunchedException();
        } catch (ProjectNotFoundException e) {
            Activator.getDefault().getLog().log(new Status(Status.ERROR, Activator.PLUGIN_ID, e.getMessage(), e));
            throw new NotLaunchedException();
        }

        if (testTarget instanceof ISourceModule) {
            IType[] types = null;
            try {
                types = ((ISourceModule) testTarget).getTypes();
            } catch (ModelException e) {
                Activator.getDefault().getLog().log(new Status(Status.WARNING, Activator.PLUGIN_ID, e.getMessage(), e));
                throw new NotLaunchedException();
            }

            for (IType type: types) {
                try {
                    TestTargets.getInstance().add(type);
                } catch (ResourceNotFoundException e) {
                    Activator.getDefault().getLog().log(new Status(Status.ERROR, Activator.PLUGIN_ID, e.getMessage(), e));
                    throw new NotLaunchedException();
                } catch (ProjectNotFoundException e) {
                    Activator.getDefault().getLog().log(new Status(Status.ERROR, Activator.PLUGIN_ID, e.getMessage(), e));
                    throw new NotLaunchedException();
                }
            }
        }
    }

    /**
     * @since 1.3.0
     */
    protected void clearTestTargets() {
        TestTargets.getInstance().clear();
    }
}
