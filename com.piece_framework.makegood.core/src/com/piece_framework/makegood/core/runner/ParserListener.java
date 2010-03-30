package com.piece_framework.makegood.core.runner;

public interface ParserListener {
    public void startTestSuite(TestSuite testSuite);

    public void endTestSuite();

    public void startTestCase(TestCase testCase);

    public void endTestCase();

    public void startProblem(Problem problem);

    public void endProblem();
}
