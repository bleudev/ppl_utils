package com.bleudev.ppl_utils.mixin.client;

import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.bleudev.ppl_utils.ClientCallbacks.shouldSendMessagesToGlobalChat;
import static com.bleudev.ppl_utils.PplUtilsConst.GLOBAL_CHAT_COMMAND;

@Mixin(ChatScreen.class)
public abstract class ChatScreenMixin extends Screen {
    protected ChatScreenMixin(Text title) {
        super(title);
    }

    @Shadow
    public abstract String normalize(String chatText);

    @Inject(method = "sendMessage", at = @At("HEAD"), cancellable = true)
    private void sendToGlobalChat(String chatText, boolean addToHistory, CallbackInfo ci) {
        chatText = this.normalize(chatText);
        if (    !chatText.startsWith("/") &&
                this.client != null &&
                shouldSendMessagesToGlobalChat(this.client) &&
                this.client.player != null) {
            this.client.player.networkHandler.sendChatCommand(
                GLOBAL_CHAT_COMMAND + " " +  chatText);
            ci.cancel();
        }
    }
}
