package com.bleudev.ppl_utils.mixin.client;

import com.bleudev.ppl_utils.client.compat.modmenu.PplUtilsConfig;
import com.bleudev.ppl_utils.util.ServerUtils;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextIconButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.bleudev.ppl_utils.client.ClientCallbacks.executeLobby;

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
    private void drawLobbyButton() {
        var b = TextIconButtonWidget.builder(Text.translatable("text.ppl_utils.game_menu.lobby_button.tooltip"),
            button -> executeLobby(), true)
            .texture(getLobbyButtonTexture(), 13, 13)
            .dimension(20, 20);
        if (PplUtilsConfig.lobby_button_tooltip_enabled) b = b.useTextAsTooltip();
        var btn = b.build();
        btn.setPosition(this.width / 2 - 125, this.height / 4 + 32);
        this.addDrawableChild(btn);
    }

    @Inject(method = "initWidgets", at = @At("RETURN"))
    private void addLobbyButton(CallbackInfo ci) {
        if (PplUtilsConfig.lobby_button_enabled && ServerUtils.isClientOnServerSupportsLobbyCommand()) drawLobbyButton();
    }
}
