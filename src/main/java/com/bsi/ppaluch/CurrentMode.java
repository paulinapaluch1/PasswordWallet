package com.bsi.ppaluch;

public class CurrentMode {
    private static Mode mode;
    public static Mode getMode() {
        return mode;
    }

    public static void setMode(Mode mode) {
        CurrentMode.mode = mode;
    }
}
