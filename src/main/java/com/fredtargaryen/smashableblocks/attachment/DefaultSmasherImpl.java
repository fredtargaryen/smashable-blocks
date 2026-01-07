// Copyright 2026 FredTargaryen
// See README.md for full copyright notice
package com.fredtargaryen.smashableblocks.attachment;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public final class DefaultSmasherImpl implements Smasher {
    private Entity attachedEntity;
    private Vec3 prevPosition = null;
    private Vec3 position = null;
    private Vec3 movement = null;
    private byte weight;

    public DefaultSmasherImpl(Entity e) {
        this.attachedEntity = e;
        this.prevPosition = this.position = e.position();
    }

    @Override
    public void update() {
        this.prevPosition = this.position;
        this.position = this.attachedEntity.position();
        this.movement = this.position.subtract(this.prevPosition);
    }

    @Override
    public Vec3 getMovementVector() {
        return movement;
    }

    @Override
    public boolean isAbleToBreak() {
        return this.movement.lengthSqr() >= 0.005;
    }

    @Override
    public byte getBreakRangeMultiplier() {
        return 2;
    }

    @Override
    public void setWeight(byte weight) {
        this.weight = weight;
    }

    @Override
    public byte getWeight() {
        return this.weight;
    }
}
