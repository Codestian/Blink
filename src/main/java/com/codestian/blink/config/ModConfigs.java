package com.codestian.blink.config;

import com.codestian.blink.Blink;
import com.mojang.datafixers.util.Pair;

public class ModConfigs {
    private static SimpleConfig CONFIG;
    private static ModConfigProvider configs;

    public static int SPEED;
    public static int DURATION_RANGE_START;
    public static int DURATION_RANGE_END;

    private static final int defaultSpeed = 8;
    private static final int defaultDurationRangeStart = 200;
    private static final int defaultDurationRangeEnd = 300;

    public static void registerConfigs() {
        configs = new ModConfigProvider();
        createConfigs();
        CONFIG = SimpleConfig.of(Blink.MOD_ID + "config").provider(configs).request();
        assignConfigs();
    }

    private static void createConfigs() {
        configs.addKeyValuePair(new Pair<>("speed", defaultSpeed), "Affects how fast or slow the blink will be");
        configs.addKeyValuePair(new Pair<>("durationRangeStart", defaultDurationRangeStart), "Start of the duration range blink starts");
        configs.addKeyValuePair(new Pair<>("durationRangeEnd", defaultDurationRangeEnd), "Start of the duration range blink ends");
    }

    private static void assignConfigs() {
        SPEED = CONFIG.getOrDefault("speed", defaultSpeed);
        DURATION_RANGE_START = CONFIG.getOrDefault("durationRangeStart", defaultDurationRangeStart);
        DURATION_RANGE_END = CONFIG.getOrDefault("durationRangeEnd", defaultDurationRangeEnd);

        Blink.LOGGER.info("All " + configs.getConfigsList().size() + " keys have been set properly!");
    }
}