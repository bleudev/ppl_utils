package com.bleudev.ppl_utils.mixin.client;

import com.bleudev.ppl_utils.config.PplUtilsConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.SpriteIconButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.bleudev.ppl_utils.ClientCallbacks.executeLobby;
import static com.bleudev.ppl_utils.ClientCallbacks.shouldRenderLobbyButton;

@Mixin(PauseScreen.class)
public abstract class PauseScreenMixin extends Screen {
    protected PauseScreenMixin(Component title) {
        super(title);
    }

    // Rendering
    @Unique
    private ResourceLocation getLobbyButtonTexture() {
        return PplUtilsConfig.lobby_button_style.getSprite();
    }

    @Unique
    private void drawLobbyButton(@NotNull Minecraft client) {
        var btn = SpriteIconButton.builder(Component.translatable("text.ppl_utils.game_menu.lobby_button.tooltip"),
            button -> executeLobby(client), true)
            .sprite(getLobbyButtonTexture(), 13, 13)
            .size(20, 20)
            .build();
        btn.setPosition(this.width / 2 - 125, this.height / 4 + 32);
        if (PplUtilsConfig.lobby_button_tooltip_enabled) btn.setTooltip(Tooltip.create(btn.getMessage()));
        this.addRenderableWidget(btn);
    }

    @Inject(method = "createPauseMenu", at = @At("RETURN"))
    private void addLobbyButton(CallbackInfo ci) {
        if (minecraft != null) if (shouldRenderLobbyButton(minecraft)) drawLobbyButton(minecraft);
    }
}
