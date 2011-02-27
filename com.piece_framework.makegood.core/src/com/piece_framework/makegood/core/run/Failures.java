/**
 * Copyright (c) 2010-2011 KUBO Atsuhiro <kubo@iteman.jp>,
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
import com.piece_framework.makegood.core.result.TestCaseResult;
import com.piece_framework.makegood.core.result.TestSuiteResult;

public class Failures implements ResultReaderListener {
    public static final int FIND_PREVIOUS = 1;
    public static final int FIND_NEXT = 2;
    private List<Result> orderedResults = new ArrayList<Result>();
    private IdentityHashMap<Result, Integer> resultIndexes = new IdentityHashMap<Result, Integer>();
    private List<Integer> failureIndexes = new ArrayList<Integer>();

    private void addResult(Result result) {
        orderedResults.add(result);
        resultIndexes.put(result, orderedResults.size() - 1);
    }

    private void markCurrentResultAsFailure() {
        failureIndexes.add(orderedResults.size() - 1);
    }

    public TestCaseResult find(Result criterion, int direction) {
        Integer indexOfCriterion = resultIndexes.get(criterion);
        if (indexOfCriterion == null) return null;

        if (criterion instanceof TestSuiteResult) {
            indexOfCriterion += criterion.getSize();
        }

        if (direction == FIND_NEXT) {
            for (int i = failureIndexes.size() - 1; i >=0; --i) {
                Integer indexOfFailure = failureIndexes.get(i);
                if (indexOfFailure >= indexOfCriterion) continue;
                return (TestCaseResult) orderedResults.get(indexOfFailure);
            }
            if (failureIndexes.size() > 0) {
                return (TestCaseResult) orderedResults.get(failureIndexes.get(failureIndexes.size() - 1));
            }
        } else if (direction == FIND_PREVIOUS) {
            for (int i = 0; i < failureIndexes.size(); ++i) {
                Integer indexOfFailure = failureIndexes.get(i);
                if (indexOfFailure <= indexOfCriterion) continue;
                return (TestCaseResult) orderedResults.get(indexOfFailure);
            }
            if (failureIndexes.size() > 0) {
                return (TestCaseResult) orderedResults.get(failureIndexes.get(0));
            }
        }

        return null;
    }

    /**
     * @since 1.7.0
     */
    @Override
    public void onFirstTestSuite(TestSuiteResult testSuite) {
    }

    /**
     * @since 1.7.0
     */
    @Override
    public void startTestSuite(TestSuiteResult testSuite) {
        addResult(testSuite);
    }

    /**
     * @since 1.7.0
     */
    @Override
    public void endTestSuite(TestSuiteResult testSuite) {
    }

    /**
     * @since 1.7.0
     */
    @Override
    public void startTestCase(TestCaseResult testCase) {
        addResult(testCase);
    }

    /**
     * @since 1.7.0
     */
    @Override
    public void endTestCase(TestCaseResult testCase) {
    }

    /**
     * @since 1.7.0
     */
    @Override
    public void startFailure(TestCaseResult failure) {
        markCurrentResultAsFailure();
    }

    /**
     * @since 1.7.0
     */
    @Override
    public void endFailure(TestCaseResult failure) {
    }

    /**
     * @since 1.7.0
     */
    @Override
    public void startTest() {
    }

    /**
     * @since 1.7.0
     */
    @Override
    public void endTest() {
    }

    /**
     * @since 1.7.0
     */
    @Override
    public void startError(TestCaseResult error) {
        markCurrentResultAsFailure();
    }

    /**
     * @since 1.7.0
     */
    @Override
    public void endError(TestCaseResult error) {
    }
}
