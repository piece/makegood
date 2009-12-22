package com.piece_framework.makegood.launch.elements;


public class Problem {
    ProblemType type;
    String typeClass;
    String content;

    Problem(ProblemType type) {
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
}
