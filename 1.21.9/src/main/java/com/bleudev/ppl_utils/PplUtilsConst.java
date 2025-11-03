package com.bleudev.ppl_utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.bleudev.ppl_utils.PlatformHelper.getModVersion;

public class PplUtilsConst {
    public static final String MOD_ID = "ppl_utils";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final String VERSION = getModVersion(MOD_ID, "+");
    public static final boolean BETA_MODE_ENABLED = VERSION.endsWith("_beta");

    public static final String ISSUES_PAGE = "https://github.com/bleudev/ppl_utils/issues";
}
