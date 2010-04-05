package com.piece_framework.makegood.core.runner.junitxmlreader;

import com.piece_framework.makegood.core.runner.TestCaseResult;
import com.piece_framework.makegood.core.runner.TestSuiteResult;

public interface JUnitXMLReaderListener {
    public void startTestSuite(TestSuiteResult testSuite);

    public void endTestSuite();

    public void startTestCase(TestCaseResult testCase);

    public void endTestCase();

    public void startFailure(TestCaseResult failure);

    public void endFailure();
}
