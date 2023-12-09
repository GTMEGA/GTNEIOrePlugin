package pers.gwyog.gtneioreplugin;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLLoadCompleteEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pers.gwyog.gtneioreplugin.config.PluginGuiConfiguration;
import pers.gwyog.gtneioreplugin.util.GT5OreLayerHelper;
import pers.gwyog.gtneioreplugin.util.GT5OreSmallHelper;

import java.util.HashSet;

@Mod(modid = Tags.MODID,
     name = Tags.MODNAME,
     version = Tags.VERSION,
     dependencies = "required-after:gregtech;" +
                    "required-after:NotEnoughItems",
     guiFactory = Tags.GROUPNAME + ".config.GTNOPGuiFactory")
public class GTNEIOrePlugin {
    public static final String MODID = Tags.MODID;
    public static final String NAME = Tags.MODNAME;
    public static final String VERSION = Tags.VERSION;
    public static final Logger LOG = LogManager.getLogger(NAME);
    public static boolean csv = false;
    public static String CSVname;
    public static String CSVnameSmall;
    public static HashSet OreV = new HashSet();
    public static boolean hideBackground = true;
    public static boolean toolTips = true;
    @Mod.Instance(MODID)
    public static GTNEIOrePlugin instance;

    @EventHandler
    public void preinit(FMLPreInitializationEvent event) {
        PluginGuiConfiguration.poke();
        Config config = new Config(event, Tags.MODID + ".cfg");

        csv = config.tConfig.getBoolean("print csv",
                                        "ALL",
                                        false,
                                        "print csv, you need apache commons collections to be injected in the minecraft jar.");

        CSVname = config.tConfig.getString("CSV_name",
                                           "ALL",
                                           event.getModConfigurationDirectory() + "/GTNH-Oresheet.csv",
                                           "rename the oresheet here, it will appear in /config");

        CSVnameSmall= config.tConfig.getString("CSV_name_for_Small_Ore_Sheet",
                                               "ALL",
                                               event.getModConfigurationDirectory() + "/GTNH-Small-Ores-Sheet.csv",
                                               "rename the oresheet here, it will appear in /config");

        hideBackground = config.tConfig.getBoolean("Hide Background",
                                                   "ALL",
                                                   true,
                                                   "Hides the Background when the tooltip for the Dimensions is rendered");

        toolTips = config.tConfig.getBoolean("DimTooltip",
                                             "ALL",
                                             true,
                                             "Activates Dimensison Tooltips");

        config.save();
    }

    @EventHandler
    public void onLoadComplete(FMLLoadCompleteEvent event) {
        if (event.getSide() == Side.CLIENT) {
            new GT5OreLayerHelper();
            new GT5OreSmallHelper();
            if (csv) {
                new pers.gwyog.gtneioreplugin.util.CSVMaker().run();
            }
        }
    }

}
