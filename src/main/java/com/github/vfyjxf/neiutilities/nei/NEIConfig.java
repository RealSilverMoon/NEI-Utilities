package com.github.vfyjxf.neiutilities.nei;

import codechicken.nei.ItemPanels;
import codechicken.nei.LayoutManager;
import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;
import codechicken.nei.guihook.GuiContainerManager;
import com.github.vfyjxf.neiutilities.NEIUtilities;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class NEIConfig implements IConfigureNEI {

    @Override
    public void loadConfig() {
        API.registerUsageHandler(AdvancedItemPanel.INSTANCE);
        API.registerRecipeHandler(AdvancedItemPanel.INSTANCE);
        GuiContainerManager.addInputHandler(AdvancedItemPanel.INSTANCE);
    }

    public static void setItemPanel() {
        try {
            Field itemPanel = ItemPanels.class.getDeclaredField("itemPanel");
            Field modifiers = itemPanel.getClass().getDeclaredField("modifiers");
            itemPanel.setAccessible(true);
            modifiers.setAccessible(true);
            modifiers.setInt(itemPanel, itemPanel.getModifiers() & ~Modifier.FINAL);
            itemPanel.set(null, AdvancedItemPanel.INSTANCE);
            modifiers.setInt(itemPanel, itemPanel.getModifiers() | Modifier.FINAL);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
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
