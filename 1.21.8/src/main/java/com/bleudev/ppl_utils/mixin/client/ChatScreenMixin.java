package com.bleudev.ppl_utils.mixin.client;

import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.bleudev.ppl_utils.ClientCallbacks.shouldSendMessagesToGlobalChat;
import static com.bleudev.ppl_utils.PplUtilsConst.GLOBAL_CHAT_COMMAND;

@Mixin(ChatScreen.class)
public abstract class ChatScreenMixin extends Screen {
    protected ChatScreenMixin(Component title) {
        super(title);
    }

    @Shadow
    public abstract String normalizeChatMessage(String chatText);

    @Inject(method = "handleChatInput", at = @At("HEAD"), cancellable = true)
    private void sendToGlobalChat(String chatText, boolean addToHistory, CallbackInfo ci) {
        chatText = this.normalizeChatMessage(chatText);
        if (    !chatText.startsWith("/") &&
                this.minecraft != null &&
                shouldSendMessagesToGlobalChat(this.minecraft) &&
                this.minecraft.player != null) {
            this.minecraft.player.connection.sendCommand(
                GLOBAL_CHAT_COMMAND + " " +  chatText);
            ci.cancel();
        }
    }
}
