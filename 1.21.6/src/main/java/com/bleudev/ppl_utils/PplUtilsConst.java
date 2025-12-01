package com.bleudev.ppl_utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.bleudev.ppl_utils.util.LangUtils.unmodifiableUnion;
import static com.bleudev.ppl_utils.util.helper.PlatformHelper.getModVersion;

public class PplUtilsConst {
    public static final String MOD_ID = "ppl_utils";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final String VERSION = getModVersion(MOD_ID, "+");
    public static final boolean BETA_MODE_ENABLED = VERSION.endsWith("_beta");

    public static final String ISSUES_PAGE = "https://github.com/bleudev/ppl_utils/issues";

    public static final List<String> PEPELAND_IPS = List.of(
        "play.pepeland.net",
        "issues.pepeland.net",
        "issues2.pepeland.net",
        "neo.play.pepeland.net"
    );

    public static final List<String> SUPPORTS_LOBBY_COMMAND_IPS = unmodifiableUnion(
        PEPELAND_IPS, List.of(
            // Maybe in future there will be more servers
        )
    );
    public static final List<String> SUPPORTS_GLOBAL_CHAT_IPS = unmodifiableUnion(
        PEPELAND_IPS, List.of(
            // Maybe in future there will be more servers
        )
    );

    public static final String GLOBAL_CHAT_COMMAND = "g";
    
    // Animation constants
    public static final float ANIMATION_STEP = 0.1f;
    public static final int BETA_MESSAGE_COOLDOWN_TICKS = 10; // 10 minutes in ticks
    
    // Time conversion constants
    public static final long MILLIS_PER_SECOND = 1_000L;
    public static final long MILLIS_PER_MINUTE = 60_000L;
}
