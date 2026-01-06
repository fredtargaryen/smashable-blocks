// Copyright 2026 FredTargaryen
// See README.md for full copyright notice
package com.fredtargaryen.smashableblocks.behaviour;

public final class BehaviourValidationException extends RuntimeException {
    public BehaviourValidationException(String message, Object... formatParams) {
        super(String.format(message, formatParams));
    }

    public static BehaviourValidationException missingParameter(String paramName) {
        return new BehaviourValidationException("No value specified for parameter '%s'", paramName);
    }
}
