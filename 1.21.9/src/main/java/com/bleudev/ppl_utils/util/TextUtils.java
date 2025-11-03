package com.bleudev.ppl_utils.util;

import net.minecraft.text.ClickEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.net.URI;

import static net.minecraft.util.Formatting.UNDERLINE;

public class TextUtils {
    public static MutableText link(@NotNull MutableText text, String uri) {
        return text.setStyle(text.getStyle().withClickEvent(new ClickEvent.OpenUrl(URI.create(uri)))).formatted(UNDERLINE);
    }
    public static MutableText link(String string, String uri) {
        return link(Text.literal(string), uri);
    }
    public static MutableText link(String uri) {
        return link(uri, uri);
    }
}
