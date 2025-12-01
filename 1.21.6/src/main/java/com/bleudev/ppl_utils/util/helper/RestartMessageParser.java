package com.bleudev.ppl_utils.util.helper;

import com.bleudev.ppl_utils.PplUtilsConst;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parser for restart messages from Pepeland server.
 * Supports formats:
 * - "[PPL10]: Рестарт случился"
 * - "[PPL10]: ► Рестарт через 1 минуту ◄"
 * - "[PPL10]: ►► Рестарт через 3 минуты ◄◄"
 * - "[PPL10]: ►►► Рестарт через 5 минут ◄◄◄"
 */
public class RestartMessageParser {
    // Pattern to match restart messages with time
    // Matches: "►►► Рестарт через 5 минут ◄◄◄" or "► Рестарт через 1 минуту ◄"
    private static final Pattern RESTART_TIME_PATTERN = Pattern.compile(
        "[►◄\\s]*Рестарт\\s+через\\s+(\\d+)\\s+(минут[а-я]*|секунд[а-я]*)[►◄\\s]*",
        Pattern.CASE_INSENSITIVE
    );
    
    // Pattern to match "Рестарт случился"
    private static final Pattern RESTART_HAPPENED_PATTERN = Pattern.compile(
        "Рестарт\\s+случился",
        Pattern.CASE_INSENSITIVE
    );
    
    /**
     * Parses restart message and extracts restart time in milliseconds.
     * @param message The chat message text
     * @return Restart time in milliseconds, or null if message doesn't contain restart info,
     *         or 0 if restart already happened
     */
    @Nullable
    public static Long parseRestartTime(@NotNull Text message) {
        String content = normalizeMessage(message.getString());
        
        // Check if restart already happened
        if (RESTART_HAPPENED_PATTERN.matcher(content).find()) {
            PplUtilsConst.LOGGER.info("Restart already happened, resetting indicator");
            return 0L; // Signal to reset/clear the restart bar
        }
        
        // Try to parse restart time
        Matcher matcher = RESTART_TIME_PATTERN.matcher(content);
        if (!matcher.find()) {
            return null; // Not a restart message
        }
        
        try {
            long timeValue = Long.parseLong(matcher.group(1));
            String timeUnit = matcher.group(2).toLowerCase();
            
            // Determine if it's minutes or seconds
            boolean isMinutes = timeUnit.startsWith("минут");
            
            long timeMs = isMinutes 
                ? timeValue * PplUtilsConst.MILLIS_PER_MINUTE
                : timeValue * PplUtilsConst.MILLIS_PER_SECOND;
            
            PplUtilsConst.LOGGER.info("Parsed restart message: {} {} = {} ms", 
                timeValue, isMinutes ? "minutes" : "seconds", timeMs);
            
            return timeMs;
        } catch (NumberFormatException e) {
            PplUtilsConst.LOGGER.error("Failed to parse restart time from message: {}", content, e);
            return null;
        }
    }
    
    /**
     * Normalizes message content by removing HTML tags and PPL prefixes.
     */
    @NotNull
    private static String normalizeMessage(@NotNull String message) {
        // Remove HTML tags like <color:...>
        String normalized = message.replaceAll("<[^< >]+> *", "");
        // Remove PPL prefixes like [PPL10]:
        normalized = normalized.replaceAll("\\[PPL[0-9]*]:\\s*", "");
        return normalized.trim();
    }
    
    /**
     * Checks if message is a restart-related message.
     */
    public static boolean isRestartMessage(@NotNull Text message) {
        String content = normalizeMessage(message.getString());
        return RESTART_HAPPENED_PATTERN.matcher(content).find() 
            || RESTART_TIME_PATTERN.matcher(content).find();
    }
}

