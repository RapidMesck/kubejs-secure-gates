package com.nbp.secure_gates.core;

public record GateDecision(boolean denied, String message) {
    public static GateDecision allow() {
        return new GateDecision(false, null);
    }

    public static GateDecision deny(String message) {
        return new GateDecision(true, message);
    }
}
