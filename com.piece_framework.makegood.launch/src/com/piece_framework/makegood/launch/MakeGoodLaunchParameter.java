package com.piece_framework.makegood.launch;

public class MakeGoodLaunchParameter {
    private static MakeGoodLaunchParameter parameter;
    private Object target;

    private MakeGoodLaunchParameter() {
    }

    public static MakeGoodLaunchParameter get() {
        if (parameter == null) {
            parameter = new MakeGoodLaunchParameter();
        }
        return parameter;
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    public Object getTarget() {
        return target;
    }
}
