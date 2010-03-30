package com.piece_framework.makegood.core.runner;


public class Problem {
    ProblemType type;
    private String typeClass;
    private String content;

    public Problem(ProblemType type) {
        this.type = type;
    }

    public ProblemType getType() {
        return type;
    }

    public String getTypeClass() {
        return typeClass;
    }

    public String getContent() {
        return content;
    }

    public void setTypeClass(String typeClass) {
        this.typeClass = typeClass;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
