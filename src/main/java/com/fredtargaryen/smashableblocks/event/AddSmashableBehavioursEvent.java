// Copyright 2026 FredTargaryen
// See README.md for full copyright notice
package com.fredtargaryen.smashableblocks.event;

import com.fredtargaryen.smashableblocks.importer.SmashableImporter;
import com.fredtargaryen.smashableblocks.behaviour.SmashableBehaviour;
import com.fredtargaryen.smashableblocks.behaviour.SmashableBehaviourInternal;
import net.neoforged.bus.api.Event;

import java.util.function.Function;

/**
 * This event is fired during BreakSystem setup, allowing mods to register their custom smash behaviours.
 * This event does not have a result.
 * This event is fired on the NeoForge.EVENT_BUS.
 */
public final class AddSmashableBehavioursEvent extends Event {
    private final SmashableImporter importer;

    public AddSmashableBehavioursEvent(SmashableImporter importer) {
        this.importer = importer;
    }

    public void addCustomBehaviour(String behaviourName, Function<SmashableBehaviour, SmashableBehaviourInternal> behaviourFactory) {
        this.importer.addBehaviourFactory(behaviourName, behaviourFactory);
    }
}
