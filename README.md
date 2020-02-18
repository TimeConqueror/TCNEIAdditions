# TCNEIAdditions
Mod that improves and fixes Thaumcraft NEI Plugin.
<br>
&#x1F534; <span>**Requires SpongeMixins as a dependency (https://github.com/TimeConqueror/SpongeMixins)**</span>

## Provided features:
* Rewrited Aspect-from-ItemStack Recipe. It is usable now, improved and doesn't cause lags. Can be accessed by pressing R on aspect in NEI.
* Rewrited Aspect Combination Recipe. Added new gui, divided all old info into Crafting (What aspects does provided contain) and Usage (In what aspects is provided contained) recipes. 
* Improved Wand Recipe handling. If some mod adds custom recipes for wands, it will display them (if the researh was completed). If ArcaneWandRecipe or ArcaneSceptreRecipe are deleted from ThaumcraftAPI#craftingRecipes, it will remove standard Wand Recipe Handlers and won't show them.
