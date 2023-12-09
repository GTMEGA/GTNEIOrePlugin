package pers.gwyog.gtneioreplugin.config;

import com.falsepattern.lib.config.Config;
import com.falsepattern.lib.config.ConfigurationManager;
import pers.gwyog.gtneioreplugin.Tags;

@Config(modid = Tags.MODID,
        category = "gui")
public class PluginGuiConfiguration {
    static {
        ConfigurationManager.selfInit();
    }

    @Config.Comment("The value of the red color channel of the font color")
    @Config.LangKey("gtnop.config.gui.color.red")
    @Config.DefaultInt(0)
    @Config.RangeInt(min = 0, max = 255)
    public static int GUI_FONT_COLOR_RED_COMPONENT;

    @Config.Comment("The value of the green color channel of the font color")
    @Config.LangKey("gtnop.config.gui.color.green")
    @Config.DefaultInt(0)
    @Config.RangeInt(min = 0, max = 255)
    public static int GUI_FONT_COLOR_GREEN_COMPONENT;

    @Config.Comment("The value of the blue color channel of the font color")
    @Config.LangKey("gtnop.config.gui.color.blue")
    @Config.DefaultInt(0)
    @Config.RangeInt(min = 0, max = 255)
    public static int GUI_FONT_COLOR_BLUE_COMPONENT;

    public static void poke() {

    }
}
