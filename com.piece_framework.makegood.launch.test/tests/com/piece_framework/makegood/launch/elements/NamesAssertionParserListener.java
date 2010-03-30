package com.piece_framework.makegood.launch.elements;

import static org.junit.Assert.assertEquals;
import java.util.Arrays;
import java.util.Iterator;

import com.piece_framework.makegood.core.runner.Problem;
import com.piece_framework.makegood.core.runner.ProblemType;
import com.piece_framework.makegood.core.runner.TestCase;
import com.piece_framework.makegood.core.runner.TestSuite;
import com.piece_framework.makegood.core.runner.junitxmlreader.ParserListener;

public class NamesAssertionParserListener implements ParserListener {
    private Iterator<String> suiteNamesIterator;
    private Iterator<String> caseNamesIterator;
    private Iterator<ProblemType> problemTypesIterator;

    public NamesAssertionParserListener(String[] suiteNames,
                               String[] caseNames,
                               ProblemType[] problemTypes
                               ) {
        suiteNamesIterator = Arrays.asList(suiteNames).iterator();
        caseNamesIterator = Arrays.asList(caseNames).iterator();
        problemTypesIterator = Arrays.asList(problemTypes).iterator();
    }

    @Override
    public void startTestSuite(TestSuite testSuite) {
        assertEquals(suiteNamesIterator.next(), testSuite.getName());
    }

    @Override
    public void startTestCase(TestCase testCase) {
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
