package com.piece_framework.makegood.core.runner.junitxmlreader;

import static org.junit.Assert.assertEquals;
import java.util.Arrays;
import java.util.Iterator;

import com.piece_framework.makegood.core.runner.Problem;
import com.piece_framework.makegood.core.runner.ProblemType;
import com.piece_framework.makegood.core.runner.TestCaseResult;
import com.piece_framework.makegood.core.runner.TestSuiteResult;
import com.piece_framework.makegood.core.runner.junitxmlreader.JUnitXMLReaderListener;

public class NamesAssertionJUnitXMLReaderListener implements JUnitXMLReaderListener {
    private Iterator<String> suiteNamesIterator;
    private Iterator<String> caseNamesIterator;
    private Iterator<ProblemType> problemTypesIterator;

    public NamesAssertionJUnitXMLReaderListener(String[] suiteNames,
                               String[] caseNames,
                               ProblemType[] problemTypes
                               ) {
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
    public void startProblem(Problem problem) {
        assertEquals(problemTypesIterator.next(), problem.getType());
    }

    @Override
    public void endTestSuite() {
    }

    @Override
    public void endTestCase() {
    }

    @Override
    public void endProblem() {
    }

    public boolean clearAll() {
        return !suiteNamesIterator.hasNext()
               && !caseNamesIterator.hasNext()
               && !problemTypesIterator.hasNext();
    }
}
