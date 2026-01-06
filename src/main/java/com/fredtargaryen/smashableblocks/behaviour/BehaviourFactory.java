// Copyright 2026 FredTargaryen
// See README.md for full copyright notice
package com.fredtargaryen.smashableblocks.behaviour;

/**
 * Workaround to be able to call a Function&lt;SmashableBehaviour, SmashableBehaviourInternal&gt; that can throw an exception
 */
@FunctionalInterface
public interface BehaviourFactory {
    SmashableBehaviourInternal apply(SmashableBehaviour sb) throws BehaviourValidationException;
}
