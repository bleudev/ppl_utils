package com.bleudev.ppl_utils.mixin.client;

import net.minecraft.client.gui.components.BossHealthOverlay;
import net.minecraft.client.gui.components.LerpingBossEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;
import java.util.UUID;

@Mixin(BossHealthOverlay.class)
public interface BossBarHudAccessor {
    @Accessor("events")
    Map<UUID, LerpingBossEvent> ppl_utils$bossBars();
}
