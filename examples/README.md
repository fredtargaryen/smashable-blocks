# Smashable Blocks example json files

Smashable Blocks doesn't do anything by default when installed on its own. Blocks are only affected once a json file like the ones in this folder is added to the game, via either a datapack or a mod.

A general example file, `examples`, is included in the mod, and can be viewed [here](https://github.com/fredtargaryen/smashable-blocks/blob/main/src/main/resources/data/smashableblocks/smashableblocks/blocks/examples). 
It introduces you to the general features of Smashable Blocks and provides a few examples that might spark your imagination.
It is inactive by default. If you are in single player or running a server, you can edit the mod jar file and rename the `examples` file to `examples.json` - or anything `.json`; the file's name isn't important.

The files in this folder aren't included in the mod jar, but are working files, grouped by theme, that provide more effects.
You can include `examples.json` or any number of the files here in a datapack or mod for them to take effect.
You are also welcome to submit contributions to these files via pull request!

## Example speed values

You can use `min_speed` and `max_speed` to limit which entities can smash a block: an entity with `min_speed <= speed < max_speed` can smash the block.
So if you set `min_speed` to `0.2` and `max_speed` to `0.275`, a walking player can smash a block but not a sprinting one.
Speed is measured in blocks per tick, so figuring out the right value isn't easy, but here are some sample values you can use:

| min_speed (blocks per tick) | Typical movement                              |
|-----------------------------|-----------------------------------------------|
| 0.1                         | Minimum speed of a falling block              |
| 0.2                         | A player walking                              |
| 0.25                        | A player walking with a potion of swiftness   |
| 0.275                       | A player sprinting                            |
| 0.33                        | A player sprinting with a potion of swiftness |
| 1.0                         | An arrow, fired with low power                |
| 3.0                         | An arrow, fired at full power                 |