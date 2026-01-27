package com.bleudev.ppl_utils.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.DebugHud;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

import static com.bleudev.ppl_utils.util.LangUtils.round;

@Mixin(DebugHud.class)
public class DebugHudMixin {
    @Inject(method = "getLeftText", at = @At("RETURN"), cancellable = true)
    private void addWorldBorderDebugHudEntry(@NotNull CallbackInfoReturnable<List<String>> cir) {
        var l = new ArrayList<>(cir.getReturnValue());

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world != null) {
            StringBuilder border = new StringBuilder(Double.toString(round(client.world.getWorldBorder().getSize(), 3)));
            while (border.toString().split("\\.", 2)[1].length() < 3) border.append("0");
            l.add("World border: " + border);
        }

        cir.setReturnValue(l);
    }
}
