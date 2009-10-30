package com.piece_framework.makegood.launch;

public class MakeGoodViewRegistry {
    private static String viewId;

    public static void register(String viewId) {
        MakeGoodViewRegistry.viewId = viewId;
    }

    public static String getViewId() {
        return viewId;
    }
}
