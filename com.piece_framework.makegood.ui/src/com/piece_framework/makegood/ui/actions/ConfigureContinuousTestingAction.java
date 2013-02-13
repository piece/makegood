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

package com.piece_framework.makegood.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

import com.piece_framework.makegood.core.continuoustesting.Scope;
import com.piece_framework.makegood.launch.RuntimeConfiguration;
import com.piece_framework.makegood.ui.Activator;
import com.piece_framework.makegood.ui.Messages;

/**
 * @since 2.2.0
 */
public class ConfigureContinuousTestingAction implements IViewActionDelegate {
    public static final String ACTION_ID = "com.piece_framework.makegood.ui.viewActions.configureContinuousTestingAction"; //$NON-NLS-1$
    public static final ImageDescriptor IMAGE_DESCRIPTOR_ENABLED = Activator.getImageDescriptor("icons/configure_continuous_testing_enabled.gif"); //$NON-NLS-1$
    public static final ImageDescriptor IMAGE_DESCRIPTOR_DISABLED = Activator.getImageDescriptor("icons/configure_continuous_testing_disabled.gif"); //$NON-NLS-1$
    private IAction lastSelectedAction;

    @Override
    public void run(IAction action) {
        (RuntimeConfiguration.getInstance().getContinuousTesting().isEnabled() ? createDisableContinuousTestingAction() : createEnableContinuousTestingAction()).run();
    }

    @Override
    public void selectionChanged(IAction action, ISelection selection) {
        if (action != lastSelectedAction) {
            lastSelectedAction = action;
            lastSelectedAction.setMenuCreator(new ContinuousTestingConfigurationMenuCreator());
        }
    }

    @Override
    public void init(IViewPart view) {
    }

    private IAction createEnableContinuousTestingAction() {
        return new EnableContinuousTestingAction(Messages.MakeGoodView_ConfigureContinuousTestingAction_EnableContinuousTestingAction);
    }

    private IAction createDisableContinuousTestingAction() {
        return new DisableContinuousTestingAction(Messages.MakeGoodView_ConfigureContinuousTestingAction_DisableContinuousTestingAction);
    }

    private class ContinuousTestingConfigurationMenuCreator implements IMenuCreator {
        private Menu menu;

        @Override
        public void dispose() {
            if (menu != null) {
                menu.dispose();
                menu = null;
            }
        }

        @Override
        public Menu getMenu(Control parent) {
            if (menu != null) {
                menu.dispose();
                menu = null;
            }

            menu = new Menu(parent);
            addAction(createEnableContinuousTestingAction());
            addAction(createDisableContinuousTestingAction());
            addSeparator();
            addAction(new SetContinuousTestingScopeToAllTestsAction(Messages.MakeGoodView_ConfigureContinuousTestingAction_SetContinuousTestingScopeToAllTestsAction));
            addAction(new SetContinuousTestingScopeToLastTestAction(Messages.MakeGoodView_ConfigureContinuousTestingAction_SetContinuousTestingScopeToLastTestAction));
            addAction(new SetContinuousTestingScopeToFailedTestsAction(Messages.MakeGoodView_ConfigureContinuousTestingAction_SelectFailedTestsAsContinuousTestingScopeAction));

            return menu;
        }

        @Override
        public Menu getMenu(Menu parent) {
            return null;
        }

        private void addAction(IAction action) {
            new ActionContributionItem(action).fill(menu, -1);
        }

        private void addSeparator() {
            new Separator().fill(menu, -1);
        }
    }

    private class EnableContinuousTestingAction extends Action {
        public EnableContinuousTestingAction(String text) {
            super(text);
            setToolTipText(text);
            setChecked(RuntimeConfiguration.getInstance().getContinuousTesting().isEnabled());
        }

        @Override
        public void run() {
            RuntimeConfiguration.getInstance().getContinuousTesting().setEnabled(true);
            setChecked(true);
            lastSelectedAction.setImageDescriptor(IMAGE_DESCRIPTOR_ENABLED);
        }
    }

    private class DisableContinuousTestingAction extends Action {
        public DisableContinuousTestingAction(String text) {
            super(text);
            setToolTipText(text);
            setChecked(!RuntimeConfiguration.getInstance().getContinuousTesting().isEnabled());
        }

        @Override
        public void run() {
            RuntimeConfiguration.getInstance().getContinuousTesting().setEnabled(false);
            setChecked(true);
            lastSelectedAction.setImageDescriptor(IMAGE_DESCRIPTOR_DISABLED);
        }
    }

    private class SetContinuousTestingScopeToAllTestsAction extends Action {
        public SetContinuousTestingScopeToAllTestsAction(String text) {
            super(text);
            setToolTipText(text);
            setChecked(Scope.ALL_TESTS.equals(RuntimeConfiguration.getInstance().getContinuousTesting().getScope()));
        }

        @Override
        public void run() {
            RuntimeConfiguration.getInstance().getContinuousTesting().setScope(Scope.ALL_TESTS);
            setChecked(true);
        }
    }

    private class SetContinuousTestingScopeToLastTestAction extends Action {
        public SetContinuousTestingScopeToLastTestAction(String text) {
            super(text);
            setToolTipText(text);
            setChecked(Scope.LAST_TEST.equals(RuntimeConfiguration.getInstance().getContinuousTesting().getScope()));
        }

        @Override
        public void run() {
            RuntimeConfiguration.getInstance().getContinuousTesting().setScope(Scope.LAST_TEST);
            setChecked(true);
        }
    }

    private class SetContinuousTestingScopeToFailedTestsAction extends Action {
        public SetContinuousTestingScopeToFailedTestsAction(String text) {
            super(text);
            setToolTipText(text);
            setChecked(Scope.FAILED_TESTS.equals(RuntimeConfiguration.getInstance().getContinuousTesting().getScope()));
        }

        @Override
        public void run() {
            RuntimeConfiguration.getInstance().getContinuousTesting().setScope(Scope.FAILED_TESTS);
            setChecked(true);
        }
    }
}
