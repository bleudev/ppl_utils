package com.bleudev.ppl_utils.mixin.client;

import com.bleudev.ppl_utils.client.compat.modmenu.PplUtilsConfig;
import com.bleudev.ppl_utils.client.impl.LobbyButtonText;
import com.bleudev.ppl_utils.util.ServerUtils;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameMenuScreen.class)
public abstract class GameMenuScreenMixin extends Screen {
    protected GameMenuScreenMixin(Text title) {
        super(title);
    }

    // Rendering
    @Unique
    private void drawLobbyButton() {
        var b = ButtonWidget
            .builder(Text.object(LobbyButtonText.INSTANCE), button ->
                ServerUtils.executeCommand("lobby"))
            .dimensions(this.width / 2 - 125, this.height / 4 + 32, 20, 20);
        if (PplUtilsConfig.lobby_button_tooltip_enabled) b = b
            .tooltip(Tooltip.of(Text.translatable("text.ppl_utils.game_menu.lobby_button.tooltip")));

        this.addDrawableChild(b.build());
    }

    @Inject(method = "initWidgets", at = @At("RETURN"))
    private void addLobbyButton(CallbackInfo ci) {
        if (PplUtilsConfig.lobby_button_enabled && ServerUtils.isClientOnServerSupportsLobbyCommand()) drawLobbyButton();
    }
}
