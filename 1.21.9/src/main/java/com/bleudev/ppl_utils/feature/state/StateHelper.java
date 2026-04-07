package com.bleudev.ppl_utils.feature.state;

import com.bleudev.ppl_utils.config.PplUtilsConfig;
import net.minecraft.core.ClientAsset;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

import static com.bleudev.ppl_utils.util.ServerUtils.isClientOnPepeland;

public class StateHelper {
    protected static Optional<ClientAsset.ResourceTexture> getCustomCapeTexture(String playerName) {
        if (!(isClientOnPepeland() && PplUtilsConfig.render_states && PplUtilsConfig.render_state_capes)) return Optional.empty();
        for (States state : States.values()) {
            if (!state.shouldRender()) continue;
            if (state.getPlayerNames().stream().anyMatch(s -> s.equalsIgnoreCase(playerName))) {
                ResourceLocation location = state.getCapeTexture();
                return Optional.ofNullable(location).map(ClientAsset.ResourceTexture::new);
            }
        }
        return Optional.empty();
    }
    public static Optional<ResourceLocation> getTabIcon(String playerName) {
        if (!(isClientOnPepeland() && PplUtilsConfig.render_states && PplUtilsConfig.render_state_tab_icons)) return Optional.empty();
        for (States state : States.values()) {
            if (!state.shouldRender()) continue;
            if (state.getPlayerNames().stream().anyMatch(s -> s.equalsIgnoreCase(playerName))) {
                ResourceLocation location = state.getTabIcon();
                return Optional.ofNullable(location);
            }
        }
        return Optional.empty();
    }
}
