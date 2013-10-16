/**
 * Copyright (c) 2013 KUBO Atsuhiro <kubo@iteman.jp>,
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
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

import com.piece_framework.makegood.core.TestResultsLayout;
import com.piece_framework.makegood.ui.MakeGoodContext;
import com.piece_framework.makegood.ui.Messages;

/**
 * @since 2.5.0
 */
public class SelectTestResultsLayoutAction implements IViewActionDelegate {
    public static final String ACTION_ID = "com.piece_framework.makegood.ui.viewActions.selectTestResultsLayoutAction"; //$NON-NLS-1$
    private IAction lastSelectedAction;

    @Override
    public void run(IAction action) {
    }

    @Override
    public void selectionChanged(IAction action, ISelection selection) {
        if (action != lastSelectedAction) {
            lastSelectedAction = action;
            lastSelectedAction.setMenuCreator(new TestResultsLayoutSelectionMenuCreator());
        }
    }

    @Override
    public void init(IViewPart view) {
    }

    private class TestResultsLayoutSelectionMenuCreator implements IMenuCreator {
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
            addAction(new SetTestResultsLayoutToTabAction(Messages.MakeGoodView_SelectTestResultsLayoutAction_SetTestResultsLayoutToTabAction));
            addAction(new SetTestResultsLayoutToHorizontalAction(Messages.MakeGoodView_SelectTestResultsLayoutAction_SetTestResultsLayoutToHorizontalAction));

            return menu;
        }

        @Override
        public Menu getMenu(Menu parent) {
            return null;
        }

        private void addAction(IAction action) {
            new ActionContributionItem(action).fill(menu, -1);
        }
    }

    private class SetTestResultsLayoutToTabAction extends Action {
        public SetTestResultsLayoutToTabAction(String text) {
            super(text);
            setToolTipText(text);
            setChecked(TestResultsLayout.TAB.equals(MakeGoodContext.getInstance().getTestResultsLayout()));
        }

        @Override
        public void run() {
            MakeGoodContext.getInstance().setTestResultsLayout(TestResultsLayout.TAB);
            setChecked(true);
        }
    }

    private class SetTestResultsLayoutToHorizontalAction extends Action {
        public SetTestResultsLayoutToHorizontalAction(String text) {
            super(text);
            setToolTipText(text);
            setChecked(TestResultsLayout.HORIZONTAL.equals(MakeGoodContext.getInstance().getTestResultsLayout()));
        }

        @Override
        public void run() {
            MakeGoodContext.getInstance().setTestResultsLayout(TestResultsLayout.HORIZONTAL);
            setChecked(true);
        }
    }
}
