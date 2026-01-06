# Smashable Blocks
Smashable Blocks is a Minecraft NeoForge utility mod which, via a json file interface, allows customisation of the behaviour of any block when collided with at speed.
A common use case would be to make glass blocks fragile, so that a player could fall or fire an arrow through them, but there are countless other possibilities.

### Customising Smashable Blocks
Full information on how to customise blocks' behaviour on collision can be found in these places:
* At the bottom of [this file](https://github.com/fredtargaryen/Fragile-Glass/blob/master/src/main/java/com/fredtargaryen/fragileglass/config/behaviour/datamanager/BlockDataManager.java).
* In your game folder, under `config/fragileglassft_blocks.cfg`.
### Customising Which Entities Can Smash Blocks
Any entity in the `smashableblocks:entity_types/smashers` tag, which mods and datapacks can modify, will be able to activate smashable blocks. 
Regardless of tags the mod is hardcoded to allow all living entities - e.g. pigs, ghasts, zombies - to smash smashable blocks. This is because otherwise it would lead to some large tags that would need updating every version. The exceptions to this are entities in the following categories:

- `AMBIENT`
- `MISC`
- `WATER_AMBIENT`

Though this can be changed by adding them to the tag. 

### Issues
Please report any issues on [the Issues page](https://github.com/fredtargaryen/smashable-blocks/issues).

## Information for developers

### Customising vanilla or mod block behaviour
Customising block behaviour using the existing behaviour options is as simple as modifying the smashableblocks.json file.
See above for information on how to specify new behaviours.

If you are making a mod or datapack that just makes some blocks smashable, you **do not** need to add Smashable Blocks as a dependency. Just create a `<your namespace>/smashableblocks/blocks/<your filename>.json` file in your `data` folder and add the blocks; Smashable Blocks will identify them.

### Adding new behaviour types

You may want to check with me before starting work on a new behaviour type, as it could potentially be integrated into Smashable Blocks for others to use.

If you want to add new behaviour types in your mod, you **will** need to add Smashable Blocks as a dependency.
Follow the instructions [here](https://github.com/MinecraftForge/ForgeGradle/wiki/Dependencies) to do so.

If you want the dependency to be optional, you can check if Smashable Blocks was loaded with `ModList.get().isLoaded("smashableblocks")`.


Your custom behaviour class must extend `SmashableBehaviourInternal`. Make a `Function<SmashableBehaviour, SmashableBehaviourInternal>` - which can just be your behaviour's constructor.
Then subscribe to the custom behaviours event, for example:
```
@SubscribeEvent
public static void addCustomBehaviours(AddSmashableBehavioursEvent event) {
    event.addCustomBehaviour("mycustombehaviourname", MyCustomBehaviour::new);
}
```
If you choose a behaviour name that was already added to `SmashableImporter` you will overwrite it, so unless you really want to, choose a unique name for your custom behaviour.

You can find the latest Smashable Blocks release files [here](https://minecraft.curseforge.com/projects/fragile-glass-and-thin-ice/files).



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
