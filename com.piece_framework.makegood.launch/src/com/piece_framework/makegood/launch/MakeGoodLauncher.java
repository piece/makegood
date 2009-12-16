package com.piece_framework.makegood.launch;

import com.piece_framework.makegood.core.TestingFramework;

public class MakeGoodLauncher {
    private TestingFramework testingFramework;
    private String script;

    public MakeGoodLauncher(TestingFramework testingFramework,
                     String script
                     ) {
        this.testingFramework = testingFramework;
        this.script = script;
    }

    public TestingFramework getTestingFramework() {
        return testingFramework;
    }

    public String getScript() {
        return script;
    }
}
