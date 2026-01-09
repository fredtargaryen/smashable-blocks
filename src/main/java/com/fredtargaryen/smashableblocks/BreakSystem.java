// Copyright 2026 FredTargaryen
// See README.md for full copyright notice
package com.fredtargaryen.smashableblocks;

import com.fredtargaryen.smashableblocks.attachment.DefaultSmasherImpl;
import com.fredtargaryen.smashableblocks.attachment.Smasher;
import com.fredtargaryen.smashableblocks.behaviour.SmashableBehaviourParentInternal;
import com.fredtargaryen.smashableblocks.event.AddSmashableBehavioursEvent;
import com.fredtargaryen.smashableblocks.importer.SmashableImporter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import static com.fredtargaryen.smashableblocks.Tags.*;

public final class BreakSystem {
    public static final BreakSystem INSTANCE;

    private final HashMap<Block, SmashableBehaviourParentInternal> blockBehaviourMap;
    private final HashSet<Block> blocksWithStateOverrides;
    private final HashMap<BlockState, SmashableBehaviourParentInternal> stateBehaviourMap;

    private final SmashableImporter smashableImporter;

    static {
        INSTANCE = new BreakSystem();
    }

    public BreakSystem() {
        this.blockBehaviourMap = new HashMap<>();
        this.stateBehaviourMap = new HashMap<>();
        this.blocksWithStateOverrides = new HashSet<>();
        this.smashableImporter = new SmashableImporter();
        NeoForge.EVENT_BUS.register(this);
    }

    public void setup(MinecraftServer server) {
        this.smashableImporter.resetBehaviours();
        this.smashableImporter.addDefaultBehaviourFactories();
        NeoForge.EVENT_BUS.post(new AddSmashableBehavioursEvent(this.smashableImporter));
        this.smashableImporter.collectAndImportBehaviourFiles(server, this.blockBehaviourMap, this.blocksWithStateOverrides, this.stateBehaviourMap);
    }

    /**
     * Identifies all Smashers in the world and attempts to smash any blocks obstructing them
     *
     * @param event
     */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void breakCheck(EntityTickEvent.Pre event) {
        Entity e = event.getEntity();
        if (e.hasData(AttachmentTypes.SMASHER)) {
            Smasher s = e.getData(AttachmentTypes.SMASHER);
            //Update the attachment before determining speed
            s.update();
            if (s.isAbleToBreak()) {
                this.attemptToSmashBlocksInWay2(e, s.getMovementVector(), s.getBreakRangeMultiplier());
            }
        }
    }

    @SubscribeEvent
    public void onEntityJoin(EntityJoinLevelEvent event) {
        Entity e = event.getEntity();
        if (!event.getLevel().isClientSide()) {
            Smasher s = new DefaultSmasherImpl(e);
            EntityType<?> et = e.getType();
            if (et.is(EXCLUDED)) return;
            if (et.is(SMASHERS_LIGHT)) {
                s.setWeight(DataReference.SMASHER_WEIGHT_LIGHT);
                e.setData(AttachmentTypes.SMASHER, s);
            }
            else if(et.is(SMASHERS_HEAVY)) {
                s.setWeight(DataReference.SMASHER_WEIGHT_HEAVY);
                e.setData(AttachmentTypes.SMASHER, s);
            }
            // Add most appropriate entities by default because it's easier than trying to add them all to the tags.
            // They would usually count as heavy but if an entity should be light it can be added to the SMASHERS_LIGHT tag
            else if (e instanceof LivingEntity && !this.isMobCategoryExempt(et.getCategory())) {
                s.setWeight(DataReference.SMASHER_WEIGHT_HEAVY);
                e.setData(AttachmentTypes.SMASHER, s);
            }
        }
    }

    private boolean isMobCategoryExempt(MobCategory mc) {
        return mc == MobCategory.AMBIENT
                || mc == MobCategory.MISC
                || mc == MobCategory.WATER_AMBIENT;
    }

    /**
     * Smash blocks ahead of the entity, so that the entity won't collide with the blocks before they break and lose all
     * its speed. Smaller resolution is more likely to break all the boxes in the way, but calculation gets slower
     *
     * @param e                    The entity that is moving
     * @param movement             The vector of movement which e intends to do this tick
     * @param breakRangeMultiplier Artificially increase the amount of blocks broken ahead of the entity; a kind of lag
     *                             compensation. The calculated entity speed remains the same
     */
    private void attemptToSmashBlocksInWay(Entity e, Vec3 movement, byte breakRangeMultiplier) {
        float speedSq = (float) movement.lengthSqr();
        final double resolution = 1.0;
        AABB originalAABB = e.getBoundingBox();
        AABB aabb = originalAABB;
        Vec3 increment = movement.normalize().scale(resolution);
        //"Move" e's bounding box along the movement vector in increments of 0.5m,
        //collecting the positions of any blocks that intersect the bounding box along the way
        double remainingDistance = movement.length() * breakRangeMultiplier - resolution;
        while (remainingDistance > resolution) {
            aabb = aabb.move(increment);
            this.smashSurroundingBlocks(e, aabb, speedSq);
            remainingDistance -= resolution;
        }
        //aabb is less than resolution blocks away from the end of movement.
        //Offset aabb right to the end of the break range, and collect any positions it intersects with
        aabb = originalAABB.move(movement.scale(breakRangeMultiplier));
        this.smashSurroundingBlocks(e, aabb, speedSq);
    }

    private void smashSurroundingBlocks(Entity e, AABB entityAabb, float speedSq) {
        // Get all blocks intersecting the bounding box
        int minX = (int) Math.floor(entityAabb.minX);
        int maxX = (int) Math.floor(entityAabb.maxX);
        int minY = (int) Math.floor(entityAabb.minY);
        int maxY = (int) Math.floor(entityAabb.maxY);
        int minZ = (int) Math.floor(entityAabb.minZ);
        int maxZ = (int) Math.floor(entityAabb.maxZ);
        BlockPos.MutableBlockPos currentPos = new BlockPos.MutableBlockPos();
        Level level = e.level();
        for (int x = minX; x <= maxX; ++x) {
            currentPos.setX(x);
            for (int y = minY; y <= maxY; ++y) {
                currentPos.setY(y);
                for (int z = minZ; z <= maxZ; ++z) {
                    currentPos.setZ(z);
                    // Try and smash the block
                    BlockState state = level.getBlockState(currentPos);
                    if (state.isAir()) continue;
                    Block block = state.getBlock();
                    SmashableBehaviourParentInternal behaviours = null;
                    if (blocksWithStateOverrides.contains(block)) {
                        if (stateBehaviourMap.containsKey(state)) {
                            behaviours = stateBehaviourMap.get(state);
                        } else if (blockBehaviourMap.containsKey(block)) {
                            behaviours = blockBehaviourMap.get(block);
                        }
                    } else {
                        if (blockBehaviourMap.containsKey(block)) {
                            behaviours = blockBehaviourMap.get(block);
                        }
                    }
                    if (behaviours != null) {
                        behaviours.onSmash(level, e, currentPos, state, speedSq, level.getBlockEntity(currentPos));
                    }
                }
            }
        }
    }

    /**
     * Simulate a point travelling along the movement vector, but from the zero BlockPos.
     * Collect all the positions travelled through, then apply them as offsets to each BlockPos currently intersected
     * by the entity bounding box. This builds a set of all BlockPoss to be passed through by the entity.
     * Should break all blocks that should be broken, but the implementation might be less performant.
     * Implements the voxel traversal algorithm here: http://www.cse.yorku.ca/~amana/research/grid.pdf
     *
     * @param e                    The moving entity
     * @param movement             The vector along which it is predicted to move this tick
     * @param breakRangeMultiplier Scaling of the movement vector to compensate for lag
     */
    private void attemptToSmashBlocksInWay2(Entity e, Vec3 movement, byte breakRangeMultiplier) {
        Level level = e.level();
        // Get starting point
        Vec3 entityPos = e.position();
        Vec3 startPos = new Vec3(
                entityPos.x - Math.floor(entityPos.x),
                entityPos.y - Math.floor(entityPos.y),
                entityPos.z - Math.floor(entityPos.z));
        Vec3 endPos = startPos.add(movement.scale(breakRangeMultiplier));
        Vec3i endBlockPos = new Vec3i(
                (int) Math.floor(endPos.x),
                (int) Math.floor(endPos.y),
                (int) Math.floor(endPos.z));
        // Current BlockPos
        int X = 0, Y = 0, Z = 0;
        // BlockPos changes by this amount when the line moves forward
        int stepX = movement.x < 0 ? -1 : 1;
        int stepY = movement.y < 0 ? -1 : 1;
        int stepZ = movement.z < 0 ? -1 : 1;
        // The value of t (distance along line) at which the coordinate of the corresponding axis changes
        double tMaxX = getTMax(startPos.x, stepX, movement.x);
        double tMaxY = getTMax(startPos.y, stepY, movement.y);
        double tMaxZ = getTMax(startPos.z, stepZ, movement.z);
        double tDeltaX = getTDelta(movement.x);
        double tDeltaY = getTDelta(movement.y);
        double tDeltaZ = getTDelta(movement.z);
        List<Vec3i> hitBlocks = new ArrayList<>();
        Vec3i currentPos = Vec3i.ZERO;
        while (!currentPos.equals(endBlockPos)) {
            if (Math.abs(tMaxX) < Math.abs(tMaxY)) {
                if (Math.abs(tMaxX) < Math.abs(tMaxZ)) {
                    X += stepX;
                    if (Math.abs(X) > Level.MAX_LEVEL_SIZE) // We've gone outside the level bounds. No point continuing the traversal
                        break;
                    tMaxX = addTDelta(tMaxX, tDeltaX);
                } else {
                    Z += stepZ;
                    if (Math.abs(Z) > Level.MAX_LEVEL_SIZE)
                        break;
                    tMaxZ = addTDelta(tMaxZ, tDeltaZ);
                }
            } else {
                if (Math.abs(tMaxY) < Math.abs(tMaxZ)) {
                    Y += stepY;
                    if (level.isOutsideBuildHeight(Y))
                        break;
                    tMaxY = addTDelta(tMaxY, tDeltaY);
                } else {
                    Z += stepZ;
                    if (Math.abs(Z) > Level.MAX_LEVEL_SIZE)
                        break;
                    tMaxZ = addTDelta(tMaxZ, tDeltaZ);
                }
            }
            currentPos = new Vec3i(X, Y, Z);
            hitBlocks.add(currentPos);
        }

        if (hitBlocks.isEmpty()) return;

        HashSet<BlockPos> initialBlockPositions = this.getIntersectingBlocks(e.getBoundingBox());
        HashSet<BlockPos> hitBlockPositions = new HashSet<>();
        for (BlockPos pos : initialBlockPositions) {
            for (Vec3i voxel : hitBlocks) {
                hitBlockPositions.add(pos.offset(voxel));
            }
        }

        this.smashCollectedBlockPositions(e, (float) movement.lengthSqr(), hitBlockPositions);
    }

    private double getTMax(double startOnAxis, int step, double axisLength) {
        if (axisLength == 0.0) return Double.MAX_VALUE;

        if (step > 0) {
            return (1.0 - startOnAxis) / axisLength;
        }

        return startOnAxis / axisLength;
    }

    private double getTDelta(double axisLength) {
        if (axisLength == 0.0) return Double.MAX_VALUE;

        return 1.0 / axisLength;
    }

    private double addTDelta(double max, double delta) {
        if (delta == 0) return max;

        if (delta < 0) {
            if (delta == Double.MIN_VALUE) return Double.MIN_VALUE;
        }

        if (delta == Double.MAX_VALUE) return Double.MAX_VALUE;

        return max + delta;
    }

    /**
     * Get all BlockPos that intersect with a given bounding box,
     * and add to a HashSet so that we don't try to smash the same block twice
     *
     * @param aabb The bounding box to get blocks around
     */
    private HashSet<BlockPos> getIntersectingBlocks(AABB aabb) {
        HashSet<BlockPos> hitBlockPositions = new HashSet<>();
        int minX = (int) Math.floor(aabb.minX);
        int maxX = (int) Math.floor(aabb.maxX);
        int minY = (int) Math.floor(aabb.minY);
        int maxY = (int) Math.floor(aabb.maxY);
        int minZ = (int) Math.floor(aabb.minZ);
        int maxZ = (int) Math.floor(aabb.maxZ);
        for (int x = minX; x <= maxX; ++x) {
            for (int y = minY; y <= maxY; ++y) {
                for (int z = minZ; z <= maxZ; ++z) {
                    hitBlockPositions.add(new BlockPos(x, y, z));
                }
            }
        }
        return hitBlockPositions;
    }

    private void smashCollectedBlockPositions(Entity e, float speedSq, HashSet<BlockPos> hitBlockPositions) {
        Level level = e.level();
        for (BlockPos pos : hitBlockPositions) {
            BlockState state = level.getBlockState(pos);
            if (state.isAir()) continue;
            Block block = state.getBlock();
            SmashableBehaviourParentInternal behaviours = null;
            if (blocksWithStateOverrides.contains(block)) {
                if (stateBehaviourMap.containsKey(state)) {
                    behaviours = stateBehaviourMap.get(state);
                } else if (blockBehaviourMap.containsKey(block)) {
                    behaviours = blockBehaviourMap.get(block);
                }
            } else {
                if (blockBehaviourMap.containsKey(block)) {
                    behaviours = blockBehaviourMap.get(block);
                }
            }
            if (behaviours != null) {
                behaviours.onSmash(level, e, pos, state, speedSq, level.getBlockEntity(pos));
            }
        }
    }
}
