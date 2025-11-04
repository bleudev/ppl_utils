package com.bleudev.ppl_utils.client.custom;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

import static com.bleudev.ppl_utils.PplUtilsConst.MOD_ID;

@Environment(EnvType.CLIENT)
public class Keys {
    private static class KeyHelper {
        protected enum Category {
            GENERAL("general");

            private final String name;

            Category(String name) {
                this.name = name;
            }

            protected KeyBinding.Category get() {
                return KeyBinding.Category.create(Identifier.of(MOD_ID, name));
            }
        }

        protected static KeyBinding create(String id, int default_key, Category category) {
            return KeyBindingHelper.registerKeyBinding(new KeyBinding("key." + MOD_ID + "." + id, InputUtil.Type.KEYSYM, default_key, category.get()));
        }
    }

    public static final KeyBinding LOBBY_KEY = KeyHelper.create("go_to_lobby", GLFW.GLFW_KEY_SEMICOLON, KeyHelper.Category.GENERAL);

    public static void initialize() {}
}
