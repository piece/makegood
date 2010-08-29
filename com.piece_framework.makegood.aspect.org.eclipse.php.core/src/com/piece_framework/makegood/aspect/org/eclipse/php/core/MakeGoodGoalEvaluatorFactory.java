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

package com.piece_framework.makegood.aspect.org.eclipse.php.core;

import org.eclipse.dltk.ti.IGoalEvaluatorFactory;
import org.eclipse.dltk.ti.goals.GoalEvaluator;
import org.eclipse.dltk.ti.goals.IGoal;

/**
 * An implementation of the IGoalEvaluatorFactory interface to avoid raising internal
 * errors during "Processing Dirty Regions" and "Semantic Highlighting Job" in an PHP
 * Editor view.
 */
public class MakeGoodGoalEvaluatorFactory implements IGoalEvaluatorFactory {
    @Override
    public GoalEvaluator createEvaluator(IGoal goal) {
        new FragmentWeavingProcess().earlyStartup();
        return null;
    }
}
