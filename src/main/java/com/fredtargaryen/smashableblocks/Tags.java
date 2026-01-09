// Copyright 2026 FredTargaryen
// See README.md for full copyright notice
package com.fredtargaryen.smashableblocks;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

public class Tags {
    public static final TagKey<EntityType<?>> EXCLUDED = TagKey.create(Registries.ENTITY_TYPE, DataReference.getResourceLocation("excluded"));
    public static final TagKey<EntityType<?>> SMASHERS_LIGHT = TagKey.create(Registries.ENTITY_TYPE, DataReference.getResourceLocation("smashers_light"));
    public static final TagKey<EntityType<?>> SMASHERS_HEAVY = TagKey.create(Registries.ENTITY_TYPE, DataReference.getResourceLocation("smashers_heavy"));
}
