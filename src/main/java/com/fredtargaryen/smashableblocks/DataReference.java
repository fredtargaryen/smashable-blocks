// Copyright 2026 FredTargaryen
// See README.md for full copyright notice
package com.fredtargaryen.smashableblocks;

import net.minecraft.resources.Identifier;

// Change version number in: build.gradle; mods.toml
public class DataReference {
    public static final String MODID = "smashableblocks";

    public static Identifier getIdentifier(String path) {
        return Identifier.fromNamespaceAndPath(MODID, path);
    }

    //The minimum speed a permitted entity must be travelling to break a fragile glass block.
    //This should be sprinting, which is over 5.5 m/s.
    //Divided by 20: 0.275 blocks per tick.
    public static final float MINIMUM_ENTITY_SPEED = 0.275f;
    public static final float MINIMUM_ENTITY_SPEED_SQUARED = MINIMUM_ENTITY_SPEED * MINIMUM_ENTITY_SPEED;

    /**
     * On the client, for some reason walk speed is recorded as roughly 0.136.
     */
    public static final float PLAYER_WALK_SPEED_SQUARED = 0.135f * 0.135f;

    /**
     * On the client, for some reason sprint speed is recorded as roughly 0.1655.
     */
    public static final float PLAYER_SPRINT_SPEED_SQUARED = 0.165f * 0.165f;

    //Arbitrary high speed.
    //A potion of Speed "increases walking speed by 20% × level" (Minecraft Wiki)
    //At potion levels above 100 the player is moving faster than chunks can load so no point allowing higher speeds
    //Maximum speed = 5.612 m/s [sprint speed average] + (5.612 * 0.2 [20%] * 100 [potion level]) = 117.852 m/s
    //117.852 / 20 = 5.8926 blocks per tick.
    //Squared, to avoid a Math.sqrt() every tick: 34.7227348
    public static final float MAXIMUM_ENTITY_SPEED = 5.8926f;
    public static final float MAXIMUM_ENTITY_SPEED_SQUARED = MAXIMUM_ENTITY_SPEED * MAXIMUM_ENTITY_SPEED;

    public static final byte SMASHER_WEIGHT_LIGHT = 0;
    public static final byte SMASHER_WEIGHT_HEAVY = 1;
    public static final byte SMASHER_WEIGHT_ANY = 2;
}
