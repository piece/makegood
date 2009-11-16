package com.piece_framework.makegood.launch.phpunit;

import java.util.Map;

public class TestSuite extends TestResult {
    String file;
    String fullPackage;
    String packageName;
    int testCount;
    int assertionCount;
    int errorCount;
    int failureCount;

    TestSuite(Map<String, String> attributes) {
        this.name = attributes.get("name"); //$NON-NLS-1$
        if (attributes.containsKey("file")) { //$NON-NLS-1$
            this.file = attributes.get("file"); //$NON-NLS-1$
        }
        if (attributes.containsKey("fullPackage")) { //$NON-NLS-1$
            this.fullPackage = attributes.get("fullPackage"); //$NON-NLS-1$
        }
        if (attributes.containsKey("package")) { //$NON-NLS-1$
            this.packageName = attributes.get("package"); //$NON-NLS-1$
        }
        if (attributes.containsKey("tests")) { //$NON-NLS-1$
            this.testCount = Integer.parseInt(attributes.get("tests")); //$NON-NLS-1$
        }
        if (attributes.containsKey("assertions")) { //$NON-NLS-1$
            this.assertionCount = Integer.parseInt(attributes.get("assertions")); //$NON-NLS-1$
        }
        if (attributes.containsKey("errors")) { //$NON-NLS-1$
            this.errorCount = Integer.parseInt(attributes.get("errors")); //$NON-NLS-1$
        }
        if (attributes.containsKey("failures")) { //$NON-NLS-1$
            this.failureCount = Integer.parseInt(attributes.get("failures")); //$NON-NLS-1$
        }
        if (attributes.containsKey("time")) { //$NON-NLS-1$
            this.time = Double.parseDouble(attributes.get("time")); //$NON-NLS-1$
        }
    }

    public String getFile() {
        return file;
    }

    public String getFullPackage() {
        return fullPackage;
    }

    public String getPackageName() {
        return packageName;
    }

    public int getTestCount() {
        return testCount;
    }

    public int getAssertionCount() {
        return assertionCount;
    }

    public int getErrorCount() {
        return errorCount;
    }

    public int getFailureCount() {
        return failureCount;
    }

    @Override
    public boolean hasError() {
        boolean result = super.hasError();
        if (!result) {
            result = errorCount > 0;
        }
        return result;
    }

    @Override
    public boolean hasFailure() {
        boolean result = super.hasFailure();
        if (!result) {
            result = failureCount > 0;
        }
        return result;
    }
}
