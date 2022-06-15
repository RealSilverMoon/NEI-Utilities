package com.github.vfyjxf.neiutilities.gui;

import codechicken.nei.guihook.GuiContainerManager;
import codechicken.nei.guihook.IContainerInputHandler;
import com.github.vfyjxf.neiutilities.nei.NEIConfig;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.awt.*;
import java.awt.datatransfer.StringSelection;

import static codechicken.nei.NEIClientConfig.getKeyBinding;

public class ItemInfoHelper implements IContainerInputHandler {

    @Override
    public boolean lastKeyTyped(GuiContainer gui, char keyChar, int keyId) {
        if (keyId == getKeyBinding(NEIConfig.COPY_NAME_KEY) || keyId == getKeyBinding(NEIConfig.COPY_OREDICT_KEY)) {
            if (GuiContainer.isCtrlKeyDown()) {
                ItemStack stackUnderMouse = GuiContainerManager.getStackMouseOver(gui);
                if (stackUnderMouse != null) {
                    if (keyId == getKeyBinding(NEIConfig.COPY_NAME_KEY)) {
                        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(stackUnderMouse.getDisplayName()), null);
                    } else {
                        StringBuilder builder = new StringBuilder();
                        for (int id : OreDictionary.getOreIDs(stackUnderMouse)) {
                            String oreDictionaryName = OreDictionary.getOreName(id);
                            if (!"Unknown".equals(oreDictionaryName)) {
                                builder.append(oreDictionaryName).append(",");
                            }
                        }
                        if (builder.length() > 0) {
                            builder.deleteCharAt(builder.length() - 1);
                        }
                        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(builder.toString()), null);
                    }
                    return true;
                }
            }
        }
        return false;
    }


    @Override
    public boolean keyTyped(GuiContainer gui, char keyChar, int keyCode) {
        return false;
    }

    @Override
    public void onKeyTyped(GuiContainer gui, char keyChar, int keyID) {

    }

    @Override
    public boolean mouseClicked(GuiContainer gui, int mouseX, int mouseY, int button) {
        return false;
    }

    @Override
    public void onMouseClicked(GuiContainer gui, int mouseX, int mouseY, int button) {

    }

    @Override
    public void onMouseUp(GuiContainer gui, int mouseX, int mouseY, int button) {

    }

    @Override
    public boolean mouseScrolled(GuiContainer gui, int mouseX, int mouseY, int scrolled) {
        return false;
    }

    @Override
    public void onMouseScrolled(GuiContainer gui, int mouseX, int mouseY, int scrolled) {

    }

    @Override
    public void onMouseDragged(GuiContainer gui, int mouseX, int mouseY, int button, long heldTime) {

    }
}
