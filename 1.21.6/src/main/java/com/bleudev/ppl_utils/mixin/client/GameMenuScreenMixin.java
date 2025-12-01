package com.bleudev.ppl_utils.mixin.client;

import com.bleudev.ppl_utils.config.PplUtilsConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.TextIconButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.bleudev.ppl_utils.ClientCallbacks.executeLobby;
import static com.bleudev.ppl_utils.ClientCallbacks.shouldRenderLobbyButton;

@Mixin(GameMenuScreen.class)
public abstract class GameMenuScreenMixin extends Screen {
    protected GameMenuScreenMixin(Text title) {
        super(title);
    }

    // Rendering
    @Unique
    private Identifier getLobbyButtonTexture() {
        return PplUtilsConfig.lobby_button_style.getSprite();
    }
    
    @Unique
    private int getLobbyButtonX() {
        int centerX = this.width / 2;
        return switch (PplUtilsConfig.lobby_button_position) {
            case FIRST -> centerX - 125; // Current position (left of "Return to game")
            case SECOND -> centerX + 105; // Right of "Статистика" (Statistics) - red square position
            case THIRD -> centerX + 105; // Right of "Открыть для сети" - purple square position, shifted right
        };
    }
    
    @Unique
    private int getLobbyButtonY() {
        int baseY = this.height / 4 + 32;
        return switch (PplUtilsConfig.lobby_button_position) {
            case FIRST -> baseY;
            case SECOND -> baseY + 24; // Same row as "Статистика"
            case THIRD -> baseY + 72; // Same row as "Открыть для сети" (Open to LAN)
        };
    }

    @Unique
    private void drawLobbyButton(@NotNull MinecraftClient client) {
        Text buttonText = Text.translatable("text.ppl_utils.game_menu.lobby_button.tooltip");
        
        // Use TextIconButtonWidget with texture
        var btn = TextIconButtonWidget.builder(buttonText, button -> executeLobby(client), true)
            .texture(getLobbyButtonTexture(), 13, 13)
            .dimension(20, 20)
            .build();
        btn.setPosition(getLobbyButtonX(), getLobbyButtonY());
        if (PplUtilsConfig.lobby_button_tooltip_enabled) btn.setTooltip(Tooltip.of(btn.getMessage()));
        this.addDrawableChild(btn);
    }

    @Inject(method = "initWidgets", at = @At("RETURN"))
    private void addLobbyButton(CallbackInfo ci) {
        if (client != null) if (shouldRenderLobbyButton(client)) drawLobbyButton(client);
    }
}
