/**
 * Copyright (c) 2010 MATSUFUJI Hideharu <matsufuji2008@gmail.com>,
 *               2010 KUBO Atsuhiro <kubo@iteman.jp>,
 * All rights reserved.
 *
 * This file is part of MakeGood.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.piece_framework.makegood.core.run;

import static org.junit.Assert.assertEquals;
import java.util.Arrays;
import java.util.Iterator;

import com.piece_framework.makegood.core.result.ResultType;
import com.piece_framework.makegood.core.result.TestCaseResult;
import com.piece_framework.makegood.core.result.TestSuiteResult;
import com.piece_framework.makegood.core.run.JUnitXMLReaderListener;

public class NamesAssertionJUnitXMLReaderListener implements JUnitXMLReaderListener {
    private Iterator<String> suiteNamesIterator;
    private Iterator<String> caseNamesIterator;
    private Iterator<ResultType> problemTypesIterator;

    public NamesAssertionJUnitXMLReaderListener(
        String[] suiteNames,
        String[] caseNames,
        ResultType[] problemTypes) {
        suiteNamesIterator = Arrays.asList(suiteNames).iterator();
        caseNamesIterator = Arrays.asList(caseNames).iterator();
        problemTypesIterator = Arrays.asList(problemTypes).iterator();
    }

    @Override
    public void startTestSuite(TestSuiteResult testSuite) {
        assertEquals(suiteNamesIterator.next(), testSuite.getName());
    }

    @Override
    public void startTestCase(TestCaseResult testCase) {
        assertEquals(caseNamesIterator.next(), testCase.getName());
    }

    @Override
    public void startFailure(TestCaseResult problem) {
        assertEquals(problemTypesIterator.next(), problem.getResultType());
    }

    @Override
    public void endTestSuite(TestSuiteResult testSuite) {
    }

    @Override
    public void endTestCase(TestCaseResult testCase) {
    }

    @Override
    public void endFailure(TestCaseResult failure) {
    }

    @Override
    public void endTest() {
    }

    public boolean finished() {
        return !suiteNamesIterator.hasNext()
               && !caseNamesIterator.hasNext()
               && !problemTypesIterator.hasNext();
    }
}
