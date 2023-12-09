package pers.gwyog.gtneioreplugin.config;

import com.falsepattern.lib.config.ConfigException;
import com.falsepattern.lib.config.SimpleGuiConfig;
import pers.gwyog.gtneioreplugin.Tags;

import net.minecraft.client.gui.GuiScreen;

public class ConfigGui extends SimpleGuiConfig {
    public ConfigGui(GuiScreen parent) throws ConfigException {
        super(parent, Tags.MODID, Tags.MODNAME, GUIConfig.class);
    }
}
