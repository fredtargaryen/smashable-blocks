# Smashable Blocks
Smashable Blocks is a Minecraft NeoForge utility mod which, via a json file interface, allows customisation of the behaviour of any block when smashed (here defined as collided with at speed).
The classic use case is to make glass blocks fragile, so that a player could fall or fire an arrow through them, but there are countless other possibilities.

### Customising Smashable Blocks
You don't need to make a mod to customise which blocks can be smashed; the minimum you need is a datapack.
Examples of how to customise blocks' behaviour in a json file can be found [here](https://github.com/fredtargaryen/smashable-blocks/blob/master/src/main/resources/data/smashableblocks/smashableblocks/blocks/examples).
Just create a `<your namespace>/smashableblocks/blocks/<your filename>.json` file in your `data` folder and add the blocks; Smashable Blocks will identify them.
You can have as many such files as you want and their names are up to you. If you're single-player you can just rename the `examples` file in the mod jar to `<your filename>.json` and edit the `blocks` folder to your liking.

### Customising Which Entities Can Smash Blocks
Two entity type tags define which entities can smash blocks:
- Entities in the `smashableblocks:smashers_light` tag are considered lightweight, such as arrows and fireworks.
- Entities in the `smashableblocks:smashers_heavy` tag are considered heavyweight, such as players, horses, minecarts and creepers.

You can customise which entities are in which tags in your own mod or datapack.

When a new entity joins the level, Smashable Blocks checks for its presence in either tag and assigns it the corresponding weight, which may affect which blocks it can smash. An example of how weight can be used is given in the above link.

If the entity:
- is in neither tag
- is a so-called 'living entity' such as a pig or zombie
- is not in the `AMBIENT`, `MISC` or `WATER_AMBIENT` entity categories

It will be assigned a heavy weight. This is because that would be accurate for most entities matching that description, and avoids the need for very long tag files to contain every living entity, as well as the need for mods to add their entities to the tag to work with Smashable Blocks.

### Issues
Please report any issues on [the Issues page](https://github.com/fredtargaryen/smashable-blocks/issues).

## Information for developers

### Customising block behaviour

You can use your mod's `data` folder as a datapack for customising smashable blocks, as described above.
You normally **do not** need to add Smashable Blocks as a dependency.

### Adding new behaviour types

(Note: You may want to check with me before starting work on a new behaviour type, as it could potentially be integrated into Smashable Blocks for others to use easily.)

If you want to add custom behaviour types in your mod, you **will** need to add Smashable Blocks as a dependency.
Follow the instructions [here](https://docs.neoforged.net/toolchain/docs/dependencies/) to do so.

Your custom behaviour class must extend `SmashableBehaviourInternal`. Make a `Function<SmashableBehaviour, SmashableBehaviourInternal>` - which can just be your behaviour's constructor, taking a `SmashableBehaviour` as a parameter.
Then subscribe to `AddSmashableBehavioursEvent` on `NeoForge.EVENT_BUS`, passing in that function; for example:
```
@SubscribeEvent
public static void addCustomBehaviours(AddSmashableBehavioursEvent event) {
    event.addCustomBehaviour("mycustombehaviourname", MyCustomBehaviour::new);
}
```
If you choose a behaviour name that was already added to `SmashableImporter` you will overwrite it, so unless you really want to, choose a unique name for your custom behaviour. You could call it `"mymod:mybehaviourname"`, `ResourceLocation`-style.

### Releases
You can find the latest Smashable Blocks release files at:
- [CurseForge]()
- [Modrinth](https://modrinth.com/project/smashable-blocks/versions)
- [My github.io page]()

### Pull Requests
Any pull requests are very welcome. There are currently no standards for pull requests but clean code which
follows the existing patterns is appreciated. If you are making a new feature, message me first to see
if I will accept it!

### Copyright Notice
Unless otherwise stated the following notice applies to all code within this project.
```
Copyright 2026 FredTargaryen
Smashable Blocks is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
Smashable Blocks is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
You should have received a copy of the GNU Lesser General Public License along with Smashable Blocks. If not, see <https://www.gnu.org/licenses/>.
```
