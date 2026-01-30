package com.bleudev.ppl_utils.util;

import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.jetbrains.annotations.NotNull;

import java.net.URI;

import static net.minecraft.ChatFormatting.UNDERLINE;

public class TextUtils {
    public static MutableComponent link(@NotNull MutableComponent text, String uri) {
        return text.setStyle(text.getStyle().withClickEvent(new ClickEvent.OpenUrl(URI.create(uri)))).withStyle(UNDERLINE);
    }
    public static MutableComponent link(String string, String uri) {
        return link(Component.literal(string), uri);
    }
    public static MutableComponent link(String uri) {
        return link(uri, uri);
    }
}
