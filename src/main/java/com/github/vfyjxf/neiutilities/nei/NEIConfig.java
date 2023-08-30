package com.github.vfyjxf.neiutilities.nei;

import java.lang.reflect.Field;

import org.lwjgl.input.Keyboard;

import com.github.vfyjxf.neiutilities.NEIUtilities;
import com.github.vfyjxf.neiutilities.gui.ItemInfoHelper;

import codechicken.nei.ItemPanel;
import codechicken.nei.ItemPanels;
import codechicken.nei.PanelWidget;
import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;
import codechicken.nei.guihook.GuiContainerManager;

public class NEIConfig implements IConfigureNEI {

    public static final String COPY_NAME_KEY = "neiutilities.utils.copy_name";
    public static final String COPY_OREDICT_KEY = "neiutilities.utils.copy_oredict";

    @Override
    public void loadConfig() {
        {
            API.registerUsageHandler(AdvancedItemPanel.GRID_INSTANCE);
            API.registerRecipeHandler(AdvancedItemPanel.GRID_INSTANCE);
            GuiContainerManager.addInputHandler(AdvancedItemPanel.GRID_INSTANCE);
        }
        {
            GuiContainerManager.addInputHandler(new ItemInfoHelper());
            API.addKeyBind(COPY_NAME_KEY, Keyboard.KEY_C);
            API.addKeyBind(COPY_OREDICT_KEY, Keyboard.KEY_D);
        }
    }

    public static void setItemPanel() {
        ItemPanel panel = ItemPanels.itemPanel;
        try {
            Field grid = PanelWidget.class.getDeclaredField("grid");
            grid.setAccessible(true);
            grid.set(panel, AdvancedItemPanel.GRID_INSTANCE);
            grid.setAccessible(false);
        } catch (NoSuchFieldException | IllegalAccessException e2) {
            e2.printStackTrace();
        }
    }

    @Override
    public String getName() {
        return NEIUtilities.NAME;
    }

    @Override
    public String getVersion() {
        return NEIUtilities.VERSION;
    }
}
