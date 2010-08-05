/**
 * Copyright (c) 2010 MATSUFUJI Hideharu <matsufuji2008@gmail.com>,
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
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.ui.IViewPart;

import com.piece_framework.makegood.aspect.monitor.WeavingMonitor;
import com.piece_framework.makegood.ui.actions.StopTestAction;
import com.piece_framework.makegood.ui.views.ResultView;
import com.piece_framework.makegood.ui.views.ViewShow;

public class StopTest extends AbstractHandler {
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        IAction action = getStopAction();
        if (action != null && action.isEnabled()) action.run();

        return null;
    }

    @Override
    public boolean isEnabled() {
        if (!WeavingMonitor.endAll()) return false;

        IAction action = getStopAction();
        return action != null && action.isEnabled();
    }

    private IAction getStopAction() {
        IViewPart view = ViewShow.find(ResultView.ID);
        if (view == null) return null;
        IToolBarManager manager = view.getViewSite().getActionBars().getToolBarManager();
        if (manager == null) return null;
        ActionContributionItem item = (ActionContributionItem) manager.find(StopTestAction.ID);
        if (item == null) return null;
        return item.getAction();
    }
}
