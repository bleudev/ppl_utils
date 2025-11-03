package com.bleudev.ppl_utils.client.impl;

import com.mojang.serialization.MapCodec;
import net.minecraft.text.StyleSpriteSource;
import net.minecraft.text.object.TextObjectContents;
import net.minecraft.util.Atlases;
import net.minecraft.util.Identifier;

import static com.bleudev.ppl_utils.PplUtilsConst.MOD_ID;

public class LobbyButtonText implements TextObjectContents {
    public static final TextObjectContents INSTANCE = new LobbyButtonText();

    @Override
    public StyleSpriteSource spriteSource() {
        return new StyleSpriteSource.Sprite(Atlases.GUI, Identifier.of(MOD_ID, "pepe_mono"));
    }

    @Override
    public String asText() {
        return "Lobby icon";
    }

    @Override
    public MapCodec<? extends TextObjectContents> getCodec() {
        return MapCodec.unit(LobbyButtonText::new);
    }
}
