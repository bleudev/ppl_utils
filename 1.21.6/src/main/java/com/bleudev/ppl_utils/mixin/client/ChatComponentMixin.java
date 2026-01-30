package com.bleudev.ppl_utils.mixin.client;

import net.minecraft.client.GuiMessage;
import net.minecraft.client.gui.components.ChatComponent;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.bleudev.ppl_utils.ClientCallbacks.tryStartWithMessage;
import static com.bleudev.ppl_utils.util.helper.ChatFilterHelper.shouldRenderChatMessage;

@Mixin(ChatComponent.class)
public class ChatComponentMixin {
    @Inject(method = "addMessageToDisplayQueue", at = @At("HEAD"), cancellable = true)
    private void cancelRenderingOfSomeMessages(GuiMessage message, CallbackInfo ci) {
        if (!shouldRenderChatMessage(message.content().getString())) ci.cancel();
    }

    @Inject(method = "addMessageToQueue(Lnet/minecraft/client/GuiMessage;)V", at = @At("HEAD"))
    private void injectTryStartWithMessage(@NotNull GuiMessage message, CallbackInfo ci) {
        tryStartWithMessage(message.content().getString());
    }
}
