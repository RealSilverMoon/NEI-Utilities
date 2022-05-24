package com.github.vfyjxf.neiutilities.config;

import net.minecraftforge.common.config.Configuration;

import java.io.File;

public class NeiUtilitiesConfig {

    public static Configuration config;

    private static final String CATEGORY_HISTORY = "history";

    public static int historyColor = 0xee555555;
    public static int useRows = 2;
    private static SplittingMode splittingMode = SplittingMode.BACKGROUND;

    public static void initConfig(File configFile) {
        config = new Configuration(configFile);
        config.load();
        {
            historyColor = Integer.parseUnsignedInt(config.getString(
                    "historyColor",
                    CATEGORY_HISTORY,
                    "ee555555",
                    "Color of the history area display"), 16);
            useRows = config.getInt("useRows", "rows", 2, 1, 5, "Rows used in historical areas");
            try {
                splittingMode = SplittingMode.valueOf(
                        config.getString("splittingMode",
                                CATEGORY_HISTORY,
                                SplittingMode.LINE.name(),
                                "Splitting mode for the browsing history.\n" +
                                        "Mode : BACKGROUND,DOTTED_LINE,LINE"
                        )
                );
            } catch (IllegalArgumentException | NullPointerException e) {
                //set default mode
                splittingMode = SplittingMode.BACKGROUND;
            }
        }
        if (config.hasChanged()) {
            config.save();
        }
    }

}
