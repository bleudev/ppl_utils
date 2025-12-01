package com.bleudev.ppl_utils.mixin.client;

import com.bleudev.ppl_utils.util.helper.RestartMessageParser;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.bleudev.ppl_utils.ClientCallbacks.shouldRenderChatMessage;
import static com.bleudev.ppl_utils.util.ServerCache.isOnPepeland;

@Mixin(ChatHud.class)
public class ChatHudMixin {
    @Inject(method = "addVisibleMessage", at = @At("HEAD"), cancellable = true)
    private void cancelRenderingOfSomeMessages(ChatHudLine message, CallbackInfo ci) {
        if (!shouldRenderChatMessage(message)) ci.cancel();
    }
    
    @Inject(method = "addMessage(Lnet/minecraft/text/Text;)V", at = @At("HEAD"))
    private void processRestartMessages(Text message, CallbackInfo ci) {
        // Process restart messages from mixin (similar to chat filter approach)
        // This ensures restart messages are always processed, even if filtered
        MinecraftClient client = MinecraftClient.getInstance();
        if (client != null) {
            // Check server only if not ignoring server check
            boolean shouldProcess = com.bleudev.ppl_utils.config.PplUtilsConfig.restart_bar_ignore_server_check 
                || isOnPepeland(client);
            
            if (shouldProcess && RestartMessageParser.isRestartMessage(message)) {
                Long restartTime = RestartMessageParser.parseRestartTime(message);
                if (restartTime != null) {
                    com.bleudev.ppl_utils.util.helper.RestartHelper.runRestartBar(restartTime);
                }
            }
        }
    }
}
