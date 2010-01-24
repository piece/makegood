package com.piece_framework.makegood.launch.elements;

public interface ParserListener {
    public void startTestSuite(TestSuite testSuite);

    public void endTestSuite();

    public void startTestCase(TestCase testCase);

    public void endTestCase();

    public void startProblem(Problem problem);

    public void endProblem();
}
