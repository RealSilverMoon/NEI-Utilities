package com.github.vfyjxf.neiutilities;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.vfyjxf.neiutilities.config.NeiUtilitiesConfig;
import com.github.vfyjxf.neiutilities.nei.AdvancedItemPanel;
import com.github.vfyjxf.neiutilities.nei.NEIConfig;

import codechicken.nei.ItemPanels;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@Mod(
    modid = NEIUtilities.MODID,
    version = Tags.VERSION,
    name = NEIUtilities.NAME,
    dependencies = NEIUtilities.DEPENDENCIES)
public class NEIUtilities {

    public static final String MODID = "neiutilities";
    public static final String NAME = "NEI Utilities";
    public static final String DEPENDENCIES = "required-after:NotEnoughItems";

    public static final Logger logger = LogManager.getLogger(NEIUtilities.NAME);

    @SideOnly(Side.CLIENT)
    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        NeiUtilitiesConfig.initConfig(
            new File(
                event.getModConfigurationDirectory()
                    .getPath(),
                "neiutilities.cfg"));
    }

    @SideOnly(Side.CLIENT)
    @EventHandler
    public void init(FMLInitializationEvent event) {
        if (!(ItemPanels.itemPanel instanceof AdvancedItemPanel)) {
            NEIConfig.setItemPanel();
        }
    }

}
