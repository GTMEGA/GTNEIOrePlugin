package pers.gwyog.gtneioreplugin.config;

import com.falsepattern.lib.config.Config;
import com.falsepattern.lib.config.ConfigurationManager;
import pers.gwyog.gtneioreplugin.Tags;

@Config(modid = Tags.MODID,
        category = "gui")
public class GUIConfig {
    static {
        ConfigurationManager.selfInit();
    }

    @Config.Comment("The color to use when rendering text")
    @Config.LangKey("gtnop.config.gui.color")
    @Config.DefaultInt(0)
    public static int GUI_FONT_COLOR;

    public static void poke() {

    }
}
