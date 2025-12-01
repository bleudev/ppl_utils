package com.bleudev.ppl_utils.util.helper;

import org.jetbrains.annotations.NotNull;

public class ErrorScreenHelper {
    public enum ErrorScreenReason {
        WORLD_JOIN(5, 10, .2f),
        MISC(5, 5, 10, .3f);

        private final int fadeIn;
        private final int hold;
        private final int fadeOut;
        private final float maxStrength;

        ErrorScreenReason(int fade_in, int hold, int fade_out, float max_strength) {
            this.fadeIn = fade_in;
            this.hold = hold;
            this.fadeOut = fade_out;
            maxStrength = max_strength;
        }
        ErrorScreenReason(int fade_in, int fade_out, float max_strength) {
            this(fade_in, 0, fade_out, max_strength);
        }

        public int getFadeIn() {
            return fadeIn;
        }
        public int getHold() {
            return hold;
        }
        public int getFadeOut() {
            return fadeOut;
        }
        public float getMaxStrength() {
            return maxStrength;
        }
    }

    private int tick = -1;
    private int ticks_fade_in = 0;
    private int ticks_hold = 0;
    private int ticks_fade_out = 0;
    private float max_strength = 0f;

    public void cause(@NotNull ErrorScreenReason reason) {
        tick = 0;
        ticks_fade_in = reason.getFadeIn();
        ticks_hold = reason.getHold();
        ticks_fade_out = reason.getFadeOut();
        max_strength = reason.getMaxStrength();
    }
    public void cause() {
        cause(ErrorScreenReason.MISC);
    }

    public void tick() {
        if (tick != -1) tick++;
        if (tick == ticks_fade_in + ticks_hold + ticks_fade_out) tick = -1;
    }

    public float getRedness() {
        if (tick == -1) return 0f;
        if (tick <= ticks_fade_in) return max_strength * tick / ticks_fade_in;
        if (tick <= ticks_fade_in + ticks_hold) return max_strength;
        return max_strength * (1f - (float) (tick - ticks_fade_in - ticks_hold) / ticks_fade_out);
    }
}
