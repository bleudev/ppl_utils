package com.bleudev.ppl_utils.util;

/**
 * UI constants for rendering elements.
 * Centralizes all UI-related magic numbers.
 */
public class UIConstants {
    // Hotbar dimensions
    public static final int HOTBAR_WIDTH = 182;
    public static final int HOTBAR_HEIGHT = 22;
    public static final int HOTBAR_OFFSET_X = 91; // (width / 2) - 91
    public static final int HOTBAR_OFFSET_Y = 22; // height - 22
    
    // Icon and item sizes
    public static final int ICON_SIZE = 16;
    public static final int ITEM_SIZE = 16;
    
    // Padding and spacing
    public static final int PADDING_SMALL = 4;
    public static final int PADDING_MEDIUM = 8;
    public static final int PADDING_LARGE = 50; // For avoiding UI overlap (e.g., Axiom)
    
    // Text positioning
    public static final int TEXT_OFFSET_FROM_ICON = 4;
    public static final int ICON_VERTICAL_OFFSET = 3; // Align with hotbar items
    
    // Animation constants
    public static final float ANIMATION_STEP = 0.1f;
    public static final float ANIMATION_MAX = 1.0f;
    public static final float ANIMATION_MIN = 0.0f;
    
    // Colors
    public static final int COLOR_GLOBAL_CHAT_ENABLED = 0x69b3ff;
    public static final int COLOR_TEXT_WHITE = 0xFFFFFF;
    public static final int COLOR_TEXT_BEIGE = 0xFFDEB887;
    public static final int COLOR_TEXT_YELLOW = 0xFFFFFF00;
    public static final int COLOR_TEXT_RED = 0xFFFF0000;
    public static final int COLOR_BACKGROUND_SEMI_TRANSPARENT = 0x80000000;
    
    // Inventory slot thresholds for color coding
    public static final int SLOT_THRESHOLD_LOW = 17;  // 0-17: beige
    public static final int SLOT_THRESHOLD_MEDIUM = 23; // 18-23: white
    public static final int SLOT_THRESHOLD_HIGH = 34;   // 24-34: yellow
    // 35-36: red
    
    // Inventory constants
    public static final int HOTBAR_SLOT_COUNT = 9;
    public static final int INVENTORY_SLOT_COUNT = 27;
    public static final int TOTAL_INVENTORY_SLOTS = 36; // 9 hotbar + 27 inventory
    
    private UIConstants() {
        // Utility class - prevent instantiation
    }
}


