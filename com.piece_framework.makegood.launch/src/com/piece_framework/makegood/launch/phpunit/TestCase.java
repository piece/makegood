package com.piece_framework.makegood.launch.phpunit;

import java.util.Map;

public class TestCase extends TestResult {
    String file;
    String className;
    int assertionCount;
    int line;
    double time;
    Failure failure;

    TestCase(Map<String, String> attributes) {
        this.name = attributes.get("name");
        if (attributes.containsKey("class")) {
            this.className = attributes.get("class");
        }
        if (attributes.containsKey("file")) {
            this.file = attributes.get("file");
        }
        if (attributes.containsKey("line")) {
            this.line = Integer.parseInt(attributes.get("line"));
        }
        if (attributes.containsKey("assertions")) {
            this.assertionCount = Integer.parseInt(attributes.get("assertions"));
        }
        if (attributes.containsKey("time")) {
            this.time = Double.parseDouble(attributes.get("time"));
        }
    }

    public String getFile() {
        return file;
    }

    public String getClassName() {
        return className;
    }

    public int getAssertionCount() {
        return assertionCount;
    }

    public int getLine() {
        return line;
    }

    public double getTime() {
        return time;
    }

    public Failure getFailure() {
        return failure;
    }
}
