// Copyright 2026 FredTargaryen
// See README.md for full copyright notice
package com.fredtargaryen.smashableblocks.util;

import com.fredtargaryen.smashableblocks.behaviour.BehaviourValidationException;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.*;

public class BlockStateUtil {
    public static final String CURRENT_STATE = "-";

    //REGEX CONSTANTS
    private static final String RES_LOC_REGEX = "[a-z]+:[a-z|_]+";
    private static final String VARIANT_REGEX = "[a-z]+=([0-9]+|[a-z|_]+)";
    private static final String VARIANTS_REGEX = "(" + VARIANT_REGEX + ",)*(" + VARIANT_REGEX + ")";
    private static final String BLOCK_STATES_REGEX = RES_LOC_REGEX + "\\[" + VARIANTS_REGEX + "\\]";
    private static final String TAGS_RES_LOC_REGEX = "#" + RES_LOC_REGEX;
    private static final String TAGS_BLOCK_STATES_REGEX = "#" + BLOCK_STATES_REGEX;
    private static final String OLD_WITHPROPS_REGEX = CURRENT_STATE + "\\[" + VARIANTS_REGEX + "\\]";

    ///////////////////////////////////
    //METHODS FOR PARSING BLOCKSTATES//
    ///////////////////////////////////

    /**
     * Transforms a blockStates String into a set of Blocks or BlockStates.
     * For example, "minecraft:snow" returns just the snow Block.
     * "#minecraft:wooden_doors[open=true]" returns all BlockStates representing an open door of any wood type.
     * If no BlockState properties are specified, return the Block rather than the BlockState,
     * so we don't add all possible states of a block to the map if we don't have to.
     *
     * @param states A BlockStates string as exemplified above.
     * @return The states string, parsed into Blocks and/or BlockStates.
     * @throws BehaviourValidationException If states can't be parsed successfully for some reason.
     */
    public static BlockStatesParseResult getAllBlocksOrStatesForString(String states) throws BehaviourValidationException {
        BlockStateSubsetDescription description = getDescriptionFromBlockStateStringGeneral(states);
        BlockStatesParseResult result = new BlockStatesParseResult();
        List<Block> blocks;

        //Get the properties specified in the config file. They may be needed later
        HashSet<String> propsToFilterFor = new HashSet<>();
        description.properties.ifPresent(s -> propsToFilterFor.addAll(List.of(s.split(","))));

        if (description.nameIsTag) {
            try {
                // Get all blocks under the tag named in description
                TagKey<Block> tag = BlockTags.create(ResourceLocation.of(description.name, ':'));
                blocks = BuiltInRegistries.BLOCK.getTag(tag).get().stream().map(Holder::value).toList();
            } catch (NoSuchElementException nsee) {
                throw new BehaviourValidationException(String.format("The tag string '%s' does not exist.", description.name));
            }
        } else {
            //Represents a single block
            blocks = List.of(getBlockFromString(description.name));
        }
        for (Block block : blocks) {
            if (block == Blocks.AIR) {
                throw new BehaviourValidationException(String.format("The %s string '%s' refers to at least one block that is invalid or not registered.",
                        description.nameIsTag ? "tag" : "blockstates",
                        description.name));
            }
            //Get all valid states of the block
            Collection<BlockState> possibleStates = block.getStateDefinition().getPossibleStates();
            description.properties.ifPresentOrElse(propsString -> {
                //Some properties were specified so add specific BlockStates
                //Add the states that have matching values for the properties in propsToFilterFor
                result.addBlockStates(
                        possibleStates.stream()
                                .filter(state -> arePropsSubsetOfBlockStateProps(propsToFilterFor, state))
                                .toList()
                );
            }, () -> {
                // No specified properties so just add the block
                result.addBlock(block);
            });
        }
        return result;
    }

    private static boolean arePropsSubsetOfBlockStateProps(HashSet<String> propsToFilterFor, BlockState state) {
        BlockStateSubsetDescription bssd = getDescriptionFromBlockStateStringGame(state.toString());
        if (bssd.properties.isEmpty()) return false;
        HashSet<String> stateProperties = new HashSet<>(List.of(bssd.properties.get().split(",")));
        return stateProperties.containsAll(propsToFilterFor);
    }

    public static Block getBlockFromString(String state) {
        return BuiltInRegistries.BLOCK.get(new ResourceLocation(state));
    }

    /**
     * Check the string matches any of the valid regexes for BlockState set descriptions.
     * If so, split it into the Block ResourceLocation, and properties if any are available
     *
     * @param string the raw string
     * @return A map of each part of the string
     */
    public static BlockStateSubsetDescription getDescriptionFromBlockStateStringGeneral(String string) throws BehaviourValidationException {
        BlockStateSubsetDescription bssd = new BlockStateSubsetDescription();
        if (string.equals(CURRENT_STATE)) {
            //Means "whatever the block was before"
            bssd.nameIsTag = false;
            bssd.name = CURRENT_STATE;
            bssd.properties = Optional.empty();
        } else if (string.matches(OLD_WITHPROPS_REGEX)) {
            //Means "whatever the block was before, but with these properties"
            bssd.nameIsTag = false;
            bssd.name = CURRENT_STATE;
            String[] splitString = string.split("\\[");
            bssd.properties = Optional.of(splitString[1].substring(0, splitString[1].length() - 1));
        } else if (string.matches(RES_LOC_REGEX)) {
            //Looks like "minecraft:acacia_button"
            bssd.nameIsTag = false;
            bssd.name = string;
            bssd.properties = Optional.empty();
        } else if (string.matches(TAGS_RES_LOC_REGEX)) {
            //Looks like "#minecraft:dirt_like"
            bssd.nameIsTag = true;
            bssd.name = string.substring(1);
            bssd.properties = Optional.empty();
        } else if (string.matches(BLOCK_STATES_REGEX)) {
            //Looks like "minecraft:acacia_button[face=wall]"
            bssd.nameIsTag = false;
            String[] splitString = string.split("\\[");
            bssd.name = splitString[0];
            bssd.properties = Optional.of(splitString[1].substring(0, splitString[1].length() - 1));
        } else if (string.matches(TAGS_BLOCK_STATES_REGEX)) {
            //Looks like "#minecraft:dirt_like[snowy=true]"
            bssd.nameIsTag = true;
            String[] splitString = string.split("\\[");
            bssd.name = splitString[0].substring(1);
            bssd.properties = Optional.of(splitString[1].substring(0, splitString[1].length() - 1));
        } else {
            throw new BehaviourValidationException(String.format("Could not parse blockStates string '%s'", string));
        }
        return bssd;
    }

    /**
     * Get description using the string form of a valid BlockState from the game, so we know the format it will have
     *
     * @param string The BlockState, as a String
     * @return A description of the BlockState
     */
    public static BlockStateSubsetDescription getDescriptionFromBlockStateStringGame(String string) {
        BlockStateSubsetDescription bssd = new BlockStateSubsetDescription();
        bssd.nameIsTag = false;
        String[] parts = string.split("\\[");
        if (parts.length == 1) {
            bssd.name = parts[0];
            bssd.properties = Optional.empty();
        } else { // 2
            // Remove surrounding "Block{" and "}"
            bssd.name = parts[0].substring(6, parts[0].length() - 1);
            // Remove final "]"
            bssd.properties = Optional.of(parts[1].substring(0, parts[1].length() - 1));
        }
        return bssd;
    }

    public static final class BlockStatesParseResult {
        public List<Block> blocks;
        public List<Block> blocksWithStateOverrides;
        public List<BlockState> blockStates;

        public BlockStatesParseResult() {
            this.blocks = new ArrayList<>();
            this.blocksWithStateOverrides = new ArrayList<>();
            this.blockStates = new ArrayList<>();
        }

        public void addBlock(Block block) {
            if (block != null) this.blocks.add(block);
        }

        public void addBlockStates(List<BlockState> states) {
            if (states != null && !states.isEmpty()) {
                Block block = states.getFirst().getBlock();
                if (!this.blocksWithStateOverrides.contains(block)) {
                    this.blocksWithStateOverrides.add(block);
                }
                this.blockStates.addAll(states);
            }
        }
    }
}
