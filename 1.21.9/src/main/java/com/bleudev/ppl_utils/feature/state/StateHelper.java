package com.bleudev.ppl_utils.feature.state;

import com.bleudev.ppl_utils.config.PplUtilsConfig;
import net.minecraft.core.ClientAsset;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

public class StateHelper {
    protected static Optional<ClientAsset.ResourceTexture> getCustomCapeTexture(String playerName) {
        if (!(PplUtilsConfig.render_states && PplUtilsConfig.render_state_capes)) return Optional.empty();
        for (States state : States.values()) {
            if (!state.shouldRender()) continue;
            if (state.getPlayerNames().stream().anyMatch(s -> s.equalsIgnoreCase(playerName))) {
                ResourceLocation location = state.getCapeTexture();
                if (location == null) return Optional.empty();
                return Optional.of(new ClientAsset.ResourceTexture(location));
            }
        }
        return Optional.empty();
    }
}
