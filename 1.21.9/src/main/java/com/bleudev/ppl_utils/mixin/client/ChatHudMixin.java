package com.bleudev.ppl_utils.mixin.client;

import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.bleudev.ppl_utils.ClientCallbacks.shouldRenderChatMessage;
import static com.bleudev.ppl_utils.ClientCallbacks.tryStartWithMessage;

@Mixin(ChatHud.class)
public class ChatHudMixin {
    @Inject(method = "addVisibleMessage", at = @At("HEAD"), cancellable = true)
    private void cancelRenderingOfSomeMessages(ChatHudLine message, CallbackInfo ci) {
        if (!shouldRenderChatMessage(message)) ci.cancel();
    }

    @Inject(method = "addMessage(Lnet/minecraft/client/gui/hud/ChatHudLine;)V", at = @At("HEAD"))
    private void injectTryStartWithMessage(@NotNull ChatHudLine message, CallbackInfo ci) {
        tryStartWithMessage(message.content().getString());
    }
}
