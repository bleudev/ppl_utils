package com.bleudev.ppl_utils.custom;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import static com.bleudev.ppl_utils.PplUtilsConst.MOD_ID;

@Environment(EnvType.CLIENT)
public class Keys {
    private static KeyBinding createKey(String id, int default_key, String category) {
        return KeyBindingHelper.registerKeyBinding(new KeyBinding("key." + MOD_ID + "." + id, InputUtil.Type.KEYSYM, default_key, category));
    }
    private static KeyBinding createKey(String id, String category) {
        return createKey(id, -1, category);
    }

    private static @NotNull String createCategory(String name) {
        return "key.category." + MOD_ID + "." + name;
    }

    public static final String CATEGORY_GENERAL = createCategory("general");

    public static final KeyBinding LOBBY_KEY = createKey("go_to_lobby", GLFW.GLFW_KEY_SEMICOLON, CATEGORY_GENERAL);
    public static final KeyBinding SEND_TO_GLOBAL_CHAT_KEY = createKey("send_to_global_chat", "key.categories.multiplayer");
    public static final KeyBinding TOGGLE_GLOBAL_CHAT_KEY = createKey("toggle_global_chat", CATEGORY_GENERAL);

    public static void initialize() {}
}
