/**
 * Copyright (c) 2009 MATSUFUJI Hideharu <matsufuji2008@gmail.com>,
 *               2010 KUBO Atsuhiro <kubo@iteman.jp>,
 * All rights reserved.
 *
 * This file is part of MakeGood.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.piece_framework.makegood.ui.views;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.piece_framework.makegood.core.result.Result;
import com.piece_framework.makegood.core.result.TestSuiteResult;

public class ResultTreeContentProvider implements ITreeContentProvider {
    @Override
    public Object[] getChildren(Object parentElement) {
        List<Result> children = new ArrayList<Result>(((Result) parentElement).getChildren());
        Collections.reverse(children);
        return children.toArray(new Result[ children.size() ]);
    }

    @Override
    public Object getParent(Object element) {
        return null;
    }

    @Override
    public boolean hasChildren(Object element) {
        if (!(element instanceof TestSuiteResult)) return false;
        return !((TestSuiteResult) element).getChildren().isEmpty();
    }

    @Override
    public Object[] getElements(Object inputElement) {
        return getChildren(inputElement);
    }

    @Override
    public void dispose() {
    }

    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
    }
}
