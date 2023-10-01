package com.github.vfyjxf.neiutilities.nei;

import org.lwjgl.input.Keyboard;

import com.github.vfyjxf.neiutilities.NEIUtilities;
import com.github.vfyjxf.neiutilities.Tags;
import com.github.vfyjxf.neiutilities.gui.ItemInfoHelper;
import com.github.vfyjxf.neiutilities.mixins.ItemPanels;

import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;
import codechicken.nei.guihook.GuiContainerManager;

public class NEIConfig implements IConfigureNEI {

    public static final String COPY_NAME_KEY = "neiutilities.utils.copy_name";
    public static final String COPY_OREDICT_KEY = "neiutilities.utils.copy_oredict";

    @Override
    public void loadConfig() {
        {
            API.registerUsageHandler(AdvancedItemPanel.INSTANCE);
            API.registerRecipeHandler(AdvancedItemPanel.INSTANCE);
            GuiContainerManager.addInputHandler(AdvancedItemPanel.INSTANCE);
        }
        {
            GuiContainerManager.addInputHandler(new ItemInfoHelper());
            API.addKeyBind(COPY_NAME_KEY, Keyboard.KEY_C);
            API.addKeyBind(COPY_OREDICT_KEY, Keyboard.KEY_D);
        }
    }

    public static void setItemPanel() {
        try {
            ItemPanels.setItemPanel(AdvancedItemPanel.INSTANCE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getName() {
        return NEIUtilities.NAME;
    }

    @Override
    public String getVersion() {
        return Tags.VERSION;
    }
}
