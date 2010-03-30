package com.piece_framework.makegood.core.runner.junitxmlreader;

import com.piece_framework.makegood.core.runner.Problem;
import com.piece_framework.makegood.core.runner.TestCase;
import com.piece_framework.makegood.core.runner.TestSuite;

public interface JUnitXMLReaderListener {
    public void startTestSuite(TestSuite testSuite);

    public void endTestSuite();

    public void startTestCase(TestCase testCase);

    public void endTestCase();

    public void startProblem(Problem problem);

    public void endProblem();
}
