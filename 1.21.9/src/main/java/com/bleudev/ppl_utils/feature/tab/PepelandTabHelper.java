package com.bleudev.ppl_utils.feature.tab;

import com.bleudev.ppl_utils.config.PplUtilsConfig;
import com.bleudev.ppl_utils.feature.state.StateHelper;
import net.minecraft.data.AtlasIds;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.objects.AtlasSprite;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

import static com.bleudev.ppl_utils.util.RegistryUtils.getIdentifier;

public class PepelandTabHelper {
    public static final ResourceLocation STAR_TEXTURE = getIdentifier("tab/star");
    private static final Component STAR_COMPONENT = Component.object(new AtlasSprite(AtlasIds.GUI, STAR_TEXTURE));

    public static Component getNewTabListDisplayName(String nickname, MutableComponent original) {
        Optional<ResourceLocation> icon = StateHelper.getTabIcon(nickname);
        if (icon.isPresent()) original = original.append(" ").append(Component.object(new AtlasSprite(AtlasIds.GUI, icon.get())));
        if (shouldHighlight(nickname)) original = highlight(original);
        return original;
    }

    private static boolean shouldHighlight(String nickname) {
        return PplUtilsConfig.do_tab_highlight && PplUtilsConfig.highlight_in_tab.stream().anyMatch(n -> n.equalsIgnoreCase(nickname));
    }

    private static @NotNull MutableComponent highlight(MutableComponent current) {
        return current.append(" ").append(STAR_COMPONENT);
    }
}
