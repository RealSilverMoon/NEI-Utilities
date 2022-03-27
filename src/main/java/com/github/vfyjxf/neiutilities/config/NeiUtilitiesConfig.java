package com.github.vfyjxf.neiutilities.config;

import net.minecraftforge.common.config.Configuration;

import java.io.File;

public class NeiUtilitiesConfig {

    public static Configuration config;

    public static int historyColor = 0xee555555;

    public static void initConfig(File configFile) {
        config = new Configuration(configFile);
        config.load();
        historyColor = Integer.parseUnsignedInt(config.getString(
                "historyColor",
                "colour",
                "ee555555",
                "Color of the history area display"), 16);
        if (config.hasChanged()) {
            config.save();
        }
    }

}
