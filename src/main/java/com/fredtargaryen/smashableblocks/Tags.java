// Copyright 2026 FredTargaryen
// See README.md for full copyright notice
package com.fredtargaryen.smashableblocks;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

public class Tags {
    public static final TagKey<EntityType<?>> SMASHERS = TagKey.create(Registries.ENTITY_TYPE, DataReference.getResourceLocation("smashers"));
}
