package pers.gwyog.gtneioreplugin.plugin.gregtech5;

import codechicken.lib.gui.GuiDraw;
import codechicken.nei.PositionedStack;
import cpw.mods.fml.common.Loader;
import gregtech.api.GregTech_API;
import gregtech.api.enums.Materials;
import gregtech.common.blocks.GT_Block_Ore;
import gregtech.common.blocks.GT_Block_Ore_Abstract;
import lombok.val;
import net.minecraft.block.material.Material;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import pers.gwyog.gtneioreplugin.util.GT5OreLayerHelper;
import pers.gwyog.gtneioreplugin.util.GT5OreLayerHelper.OreLayerWrapper;
import scala.actors.threadpool.Arrays;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static gregtech.common.blocks.GT_Block_Ore_Abstract.OreSize;

public class PluginGT5VeinStat extends PluginGT5Base {

    public static String[] getLocalizedVeinName(OreLayerWrapper oreLayer) {
        String unlocalizedName = oreLayer.veinName;
        if (unlocalizedName.startsWith("ore.mix.custom."))
            return get_Cnames(oreLayer);//I18n.format("gtnop.ore.custom.name") + I18n.format("gtnop.ore.vein.name") + unlocalizedName.substring(15);
        else
            return new String[]{I18n.format("gtnop." + unlocalizedName) + I18n.format("gtnop.ore.vein.name")};
    }

    public static String coustomVeinRenamer(OreLayerWrapper oreLayer) {
        Set<String> s = new HashSet<String>();
        for (int i = 0; i < 4; i++)
            s.add(oreLayer.materials[i].mLocalizedName.replaceAll(" ", ""));
        return s.toString()
                .replace("[".charAt(0), ",".charAt(0))
                .replace("]".charAt(0), ",".charAt(0))
                .replaceAll(" Ore", ",")
                .replaceAll("Ore", ",")
                .replaceAll(" Sand", ",")
                .replaceAll("Sand", ",")
                .replaceAll("Stone", ",")
                .replaceAll(" Stone", ",")
                .replaceAll("Earth", ",")
                .replaceAll(" Earth", ",")
                .replaceAll("Infused", ",")
                .replaceAll(" Infused", ",")
                .replaceAll(",", "")
                .trim();
    }
    
    /*public String getWeightedChance(OreLayerWrapper oreLayer) {
        String weightedChance = "";
        for (int i=0; i < oreLayer.alloweddims.size(); i++) {
        if (oreLayer.alloweddims.get(i) && (oreLayer.Weight.get(i) != 0)) {
            if (!weightedChance.isEmpty())
                weightedChance += ", ";
            weightedChance += String.format("%.2f%%", (100.0f*oreLayer.Weight.get(i))/GT5OreLayerHelper.weightPerWorld[i]);
        }
        }
        return weightedChance;
    }*/

    public static String[] get_Cnames(OreLayerWrapper oreLayer) {

        String[] splt = coustomVeinRenamer(oreLayer).split("\\s");
    	/*HashSet<String> h = new HashSet<String>();
    	for (int i=0; i < splt.length;i++) {
    		h.add(splt[i]);
    	}
    	h.toArray(splt);*/

        String[] ret = {oreLayer.veinName.replace("ore.mix.custom.", "") + " ", " ", " "};
        for (int i = 0; i < ret.length; i++) {
            ret[i] = ret[i].trim();
        }
        for (int i = 0; i < splt.length; i++) {
            //FMLLog.info("Split:"+splt[i]);
            //FMLLog.info("I:"+Integer.toString(i));
            if (ret[0].length() + splt[i].length() <= 20)
                ret[0] = ret[0] + splt[i] + " ";
            if ((ret[0].length() + splt[i].length() > 20) && ret[1].length() + splt[i].length() <= 70 && !ret[0].contains(splt[i]))
                ret[1] = ret[1] + splt[i] + " ";
            if ((ret[0].length() + splt[i].length() > 20) && (ret[1].length() + splt[i].length() > 70) && ret[2].length() + splt[i].length() <= 70 && !ret[1].contains(splt[i]))
                ret[2] = ret[2] + splt[i] + " ";
        }
        for (int i = 0; i < ret.length; i++) {
            ret[i] = ret[i].trim();
        }

        if (ret[2].isEmpty() && !ret[1].isEmpty())
            if (ret[1].length() <= 65)
                ret[1] = ret[1] + " Vein";
            else
                ret[2] = ret[2] + "Vein";
        else if (ret[1].isEmpty() && ret[2].isEmpty() && !ret[0].isEmpty())
            if (ret[0].length() <= 15)
                ret[0] = ret[0] + " Vein";
            else
                ret[1] = ret[1] + "Vein";
        else if (!(ret[1].isEmpty() && ret[2].isEmpty()))
            ret[2] = ret[2] + "Vein";
        String[] ret2 = new String[2];
        if (ret[2].isEmpty() && !ret[1].isEmpty()) {
            ret2[0] = ret[0];
            ret2[1] = ret[1];
            return ret2;
        }
        String[] ret1 = new String[1];
        if (ret[1].isEmpty() && ret[2].isEmpty() && !ret[0].isEmpty()) {
            ret1[0] = ret[0];
            return ret1;
        } else
            return ret;
    }

    @Override
    public void loadCraftingRecipes(String outputId, Object... results) {
        if (outputId.equals(getOutputId())) {
            for (String veinName : GT5OreLayerHelper.mapOreLayerWrapper.keySet()) {
                OreLayerWrapper oreLayer = GT5OreLayerHelper.mapOreLayerWrapper.get(veinName);
                this.addRecipesForOreLayer(oreLayer);
            }
        } else
            super.loadCraftingRecipes(outputId, results);
    }

    @Override
    public void loadCraftingRecipes(ItemStack stack) {
        val item = stack.getItem();

        if (item instanceof ItemBlock) {
            val itemBlock = ((ItemBlock) item);

            if (itemBlock.field_150939_a instanceof GT_Block_Ore) {
                val oreBlock = (GT_Block_Ore) itemBlock.field_150939_a;
                val oreMaterial = oreBlock.getOreType();

                for (OreLayerWrapper oreLayer : GT5OreLayerHelper.mapOreLayerWrapper.values()) {
                    val condition = oreLayer.materials[0] == oreMaterial ||
                                    oreLayer.materials[1] == oreMaterial ||
                                    oreLayer.materials[2] == oreMaterial ||
                                    oreLayer.materials[3] == oreMaterial;

                    if (condition) {
                        this.addRecipesForOreLayer(oreLayer);
                    }
                }
            }
        } else {
            super.loadCraftingRecipes(stack);
        }
    }

    protected void addRecipesForOreLayer(OreLayerWrapper oreLayer) {
        val primaryOre = GT_Block_Ore_Abstract.getOre(oreLayer.materials[0], OreSize.Normal);
        val primaryOreList = new ArrayList<ItemStack>();

        if (primaryOre != null) {
            primaryOreList.add(new ItemStack(primaryOre));
        }

        val secondaryOre = GT_Block_Ore_Abstract.getOre(oreLayer.materials[1], OreSize.Normal);
        val secondaryOreList = new ArrayList<ItemStack>();

        if (secondaryOre != null) {
            secondaryOreList.add(new ItemStack(secondaryOre));
        }

        val betweenOre = GT_Block_Ore_Abstract.getOre(oreLayer.materials[2], OreSize.Normal);
        val betweenOreList = new ArrayList<ItemStack>();

        if (betweenOre != null) {
            betweenOreList.add(new ItemStack(betweenOre));
        }

        val sporadicOre = GT_Block_Ore_Abstract.getOre(oreLayer.materials[3], OreSize.Normal);
        val sporadicOreList = new ArrayList<ItemStack>();

        if (sporadicOre != null) {
            sporadicOreList.add(new ItemStack(sporadicOre));
        }

        this.arecipes.add(new CachedVeinStatRecipe(oreLayer.veinName,
                                                   primaryOreList,
                                                   secondaryOreList,
                                                   betweenOreList,
                                                   sporadicOreList));
    }

    @Override
    public void drawExtras(int recipe) {
        CachedVeinStatRecipe crecipe = (CachedVeinStatRecipe) this.arecipes.get(recipe);
        OreLayerWrapper oreLayer = GT5OreLayerHelper.mapOreLayerWrapper.get(crecipe.veinName);

        String sDimNames = GT5OreLayerHelper.bufferedDims.get(oreLayer);

        /*if (getLocalizedVeinName(oreLayer).length>1) {
        GuiDraw.drawString(I18n.format("gtnop.gui.nei.veinName") + ": " + getLocalizedVeinName(oreLayer)[0], 2, 20, 0x404040, false);
        if (getLocalizedVeinName(oreLayer).length>2) {
        	GuiDraw.drawString(I18n.format(getLocalizedVeinName(oreLayer)[1]), 2, 30, 0x404040, false);
        	GuiDraw.drawString(I18n.format(getLocalizedVeinName(oreLayer)[2]), 2, 40, 0x404040, false);
        }
        else
        GuiDraw.drawString(I18n.format(getLocalizedVeinName(oreLayer)[1]), 2, 30, 0x404040, false);
        }
        else*/
        val textColor = 0x404040;

        if(Loader.isModLoaded("visualprospecting")) {
            GuiDraw.drawString(I18n.format("gtnop.gui.nei.veinName") + ": " + I18n.format(oreLayer.veinName) + "" + I18n.format("gtnop.gui.nei.vein"), 2, 20, textColor, false);
        }
        else {
            val localizedName = oreLayer.materials[0] != null ?  oreLayer.materials[0].mLocalizedName : "placeholder name";

            if (localizedName.contains("Ore"))
                GuiDraw.drawString(I18n.format("gtnop.gui.nei.veinName") + ": " + localizedName.split("Ore")[0] + "" + I18n.format("gtnop.gui.nei.vein"), 2, 20, textColor, false);
            else if (localizedName.contains("Sand"))
                GuiDraw.drawString(I18n.format("gtnop.gui.nei.veinName") + ": " + localizedName.split("Sand")[0] + "" + I18n.format("gtnop.gui.nei.vein"), 2, 20, textColor, false);
            else
                GuiDraw.drawString(I18n.format("gtnop.gui.nei.veinName") + ": " + localizedName + " " + I18n.format("gtnop.gui.nei.vein"), 2, 20, textColor, false);
        }
        
        drawToolTip(sDimNames);
        if (!ttDisplayed) {
            val primaryOreName = oreLayer.materials[0] != null ? oreLayer.materials[0].mLocalizedName : "";
            GuiDraw.drawString(I18n.format("gtnop.gui.nei.primaryOre") + ": " + primaryOreName, 2, 50, textColor, false);

            val secondaryOreName = oreLayer.materials[1] != null ? oreLayer.materials[1].mLocalizedName : "";
            GuiDraw.drawString(I18n.format("gtnop.gui.nei.secondaryOre") + ": " + secondaryOreName, 2, 60, textColor, false);

            val betweenOreName = oreLayer.materials[2] != null ? oreLayer.materials[2].mLocalizedName : "";
            GuiDraw.drawString(I18n.format("gtnop.gui.nei.betweenOre") + ": " + betweenOreName, 2, 70, textColor, false);

            val sporadicOreName = oreLayer.materials[3] != null ? oreLayer.materials[3].mLocalizedName : "";
            GuiDraw.drawString(I18n.format("gtnop.gui.nei.sporadicOre") + ": " + sporadicOreName, 2, 80, textColor, false);

            GuiDraw.drawString(I18n.format("gtnop.gui.nei.genHeight") + ": " + oreLayer.worldGenHeightRange, 2, 90, textColor, false);

            GuiDraw.drawString(I18n.format("gtnop.gui.nei.weightedChance") + ": " + Integer.toString(oreLayer.randomWeight), 100, 90, textColor, false);

            GuiDraw.drawString(I18n.format("gtnop.gui.nei.worldNames") + ": ", 2, 100, textColor, false);
            if (sDimNames.length() > 36) {
                GuiDraw.drawString(I18n.format("") + sDimNames.substring(0, 36), 2, 110, textColor, false);
                if (sDimNames.length() > 70) {
                    GuiDraw.drawString(I18n.format("") + sDimNames.substring(36, 70), 2, 120, textColor, false);
                    GuiDraw.drawString(I18n.format("") + sDimNames.substring(70, sDimNames.length() - 1), 2, 130, textColor, false);
                } else
                    GuiDraw.drawString(I18n.format("") + sDimNames.substring(36, sDimNames.length() - 1), 2, 120, textColor, false);
            } else
                GuiDraw.drawString(I18n.format("") + sDimNames.substring(0, sDimNames.length() - 1), 2, 110, textColor, false);
        }
        //if (GT5OreLayerHelper.restrictBiomeSupport) GuiDraw.drawString(I18n.format("gtnop.gui.nei.restrictBiome") + ": " + getBiomeTranslated(oreLayer.restrictBiome), 2, 122, 0x404040, false);
        GuiDraw.drawStringR(EnumChatFormatting.BOLD + I18n.format("gtnop.gui.nei.seeAll"), getGuiWidth() - 3, 5, textColor, false);
    }

    @Override
    public String getOutputId() {
        return "GTOrePluginVein";
    }

    @Override
    public String getRecipeName() {
        return I18n.format("gtnop.gui.veinStat.name");
    }

    public class CachedVeinStatRecipe extends CachedRecipe {
        public String veinName;
        public PositionedStack positionedStackPrimary;
        public PositionedStack positionedStackSecondary;
        public PositionedStack positionedStackBetween;
        public PositionedStack positionedStackSporadic;

        public CachedVeinStatRecipe(String veinName,
                                    List<ItemStack> stackListPrimary,
                                    List<ItemStack> stackListSecondary,
                                    List<ItemStack> stackListBetween,
                                    List<ItemStack> stackListSporadic) {
            this.veinName = veinName;
            if (!stackListPrimary.isEmpty()) {
                this.positionedStackPrimary = new PositionedStack(stackListPrimary, 2, 0);
            }

            if (!stackListSecondary.isEmpty()) {
                this.positionedStackSecondary = new PositionedStack(stackListSecondary, 22, 0);
            }

            if (!stackListBetween.isEmpty()) {
                this.positionedStackBetween = new PositionedStack(stackListBetween, 42, 0);
            }

            if (!stackListSporadic.isEmpty()) {
                this.positionedStackSporadic = new PositionedStack(stackListSporadic, 62, 0);
            }
        }

        @Override
        public List<PositionedStack> getIngredients() {
            List<PositionedStack> ingredientsList = new ArrayList<>();
            if (this.positionedStackPrimary != null) {
                ingredientsList.add(this.positionedStackPrimary);
            }

            if (this.positionedStackSecondary != null) {
                ingredientsList.add(this.positionedStackSecondary);
            }

            if (this.positionedStackBetween != null) {
                ingredientsList.add(this.positionedStackBetween);
            }

            if (this.positionedStackSporadic != null) {
                ingredientsList.add(this.positionedStackSporadic);
            }

            return ingredientsList;
        }

        @Override
        public PositionedStack getResult() {
            return null;
        }
    }
}
