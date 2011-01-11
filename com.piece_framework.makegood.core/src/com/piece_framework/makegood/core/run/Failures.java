/**
 * Copyright (c) 2010 KUBO Atsuhiro <kubo@iteman.jp>,
 * All rights reserved.
 *
 * This file is part of MakeGood.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.piece_framework.makegood.core.run;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;

import com.piece_framework.makegood.core.result.Result;
import com.piece_framework.makegood.core.result.TestSuiteResult;

public class Failures {
    public static final int FIND_PREVIOUS = 1;
    public static final int FIND_NEXT = 2;
    private List<Result> orderedResults = new ArrayList<Result>();
    private IdentityHashMap<Result, Integer> resultIndexes = new IdentityHashMap<Result, Integer>();
    private List<Integer> failureIndexes = new ArrayList<Integer>();

    public void addResult(Result result) {
        orderedResults.add(result);
        resultIndexes.put(result, orderedResults.size() - 1);
    }

    public void markCurrentResultAsFailure() {
        failureIndexes.add(orderedResults.size() - 1);
    }

    public Result find(Result criterion, int direction) {
        Integer indexOfCriterion = resultIndexes.get(criterion);
        if (indexOfCriterion == null) return null;

        if (criterion instanceof TestSuiteResult) {
            indexOfCriterion += criterion.getSize();
        }

        if (direction == FIND_NEXT) {
            for (int i = failureIndexes.size() - 1; i >=0; --i) {
                Integer indexOfFailure = failureIndexes.get(i);
                if (indexOfFailure >= indexOfCriterion) continue;
                return orderedResults.get(indexOfFailure);
            }
        } else if (direction == FIND_PREVIOUS) {
            for (int i = 0; i < failureIndexes.size(); ++i) {
                Integer indexOfFailure = failureIndexes.get(i);
                if (indexOfFailure <= indexOfCriterion) continue;
                return orderedResults.get(indexOfFailure);
            }
        }

        return null;
    }
}
