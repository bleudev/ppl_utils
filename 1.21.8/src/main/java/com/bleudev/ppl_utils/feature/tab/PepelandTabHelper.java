package com.bleudev.ppl_utils.feature.tab;

import com.bleudev.ppl_utils.config.PplUtilsConfig;
import com.bleudev.ppl_utils.feature.state.StateHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class PepelandTabHelper {
    private static final String STAR_STRING = "孡";

    public static Component getNewTabListDisplayName(String nickname, MutableComponent original) {
        Optional<String> icon = StateHelper.getTabIcon(nickname);
        if (icon.isPresent()) original = original.append(" " + icon.get());
        if (shouldHighlight(nickname)) original = highlight(original);
        return original;
    }

    private static boolean shouldHighlight(String nickname) {
        return PplUtilsConfig.do_tab_highlight && PplUtilsConfig.highlight_in_tab.stream().anyMatch(n -> n.equalsIgnoreCase(nickname));
    }

    private static @NotNull MutableComponent highlight(MutableComponent current) {
        return current.append(" " + STAR_STRING);
    }
}
