package com.github.vfyjxf.neiutilities.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import codechicken.nei.ItemPanel;

@Mixin(value = codechicken.nei.ItemPanels.class)
public interface ItemPanels {

    @Accessor(remap = false)
    public static void setItemPanel(ItemPanel itemPanel) {}
}
