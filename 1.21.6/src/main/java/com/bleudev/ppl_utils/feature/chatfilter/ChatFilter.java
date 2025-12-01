package com.bleudev.ppl_utils.feature.chatfilter;

import com.bleudev.ppl_utils.PplUtilsConst;
import com.bleudev.ppl_utils.config.PplUtilsConfig;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Manages chat message filtering based on whitelist/blacklist mode.
 */
public class ChatFilter {
    public enum FilterMode {
        WHITELIST,
        BLACKLIST
    }
    
    // Cache for lowercase filter words
    private static List<String> cachedLowercaseWords = null;
    private static List<String> lastFilterWords = null;
    
    /**
     * Gets filter words in lowercase, using cache if available.
     */
    @NotNull
    private static List<String> getLowercaseWords() {
        List<String> currentWords = PplUtilsConfig.chat_filter_words;
        
        if (currentWords == null) {
            return new ArrayList<>();
        }
        
        // Rebuild cache only if list changed
        if (cachedLowercaseWords == null || !currentWords.equals(lastFilterWords)) {
            cachedLowercaseWords = new ArrayList<>(currentWords.size());
            for (String word : currentWords) {
                cachedLowercaseWords.add(word.toLowerCase(Locale.ROOT));
            }
            lastFilterWords = new ArrayList<>(currentWords);
        }
        
        return cachedLowercaseWords;
    }
    
    /**
     * Invalidates the filter words cache. Should be called when filter words are modified.
     */
    public static void invalidateCache() {
        cachedLowercaseWords = null;
        lastFilterWords = null;
    }
    
    /**
     * Checks if a message should be displayed based on current filter settings.
     * @param message The chat message text
     * @return true if message should be displayed, false if it should be hidden
     */
    public static boolean shouldDisplayMessage(@NotNull Text message) {
        // If filter is disabled, show all messages
        if (!PplUtilsConfig.chat_filter_enabled) {
            return true;
        }
        
        String messageText = message.getString().toLowerCase(Locale.ROOT);
        List<String> filterWords = getLowercaseWords();
        
        if (filterWords.isEmpty()) {
            // If no words in filter, whitelist mode shows nothing, blacklist shows everything
            return PplUtilsConfig.chat_filter_mode == PplUtilsConfig.ChatFilterMode.BLACKLIST;
        }
        
        // Use regular loop instead of stream for better performance
        // Early exit for blacklist mode (hide on first match)
        if (PplUtilsConfig.chat_filter_mode == PplUtilsConfig.ChatFilterMode.BLACKLIST) {
            for (String word : filterWords) {
                if (messageText.contains(word)) {
                    return false; // Early exit: hide message
                }
            }
            return true; // No matches found, show message
        } else {
            // Whitelist mode: show only if contains any word
            for (String word : filterWords) {
                if (messageText.contains(word)) {
                    return true; // Early exit: show message
                }
            }
            return false; // No matches found, hide message
        }
    }
    
    /**
     * Gets the current filter mode.
     */
    @NotNull
    public static FilterMode getMode() {
        if (PplUtilsConfig.chat_filter_mode == null) {
            return FilterMode.BLACKLIST;
        }
        return PplUtilsConfig.chat_filter_mode == PplUtilsConfig.ChatFilterMode.WHITELIST 
            ? FilterMode.WHITELIST 
            : FilterMode.BLACKLIST;
    }
    
    /**
     * Sets the filter mode.
     */
    public static void setMode(@NotNull FilterMode mode) {
        PplUtilsConfig.chat_filter_mode = mode == FilterMode.WHITELIST 
            ? PplUtilsConfig.ChatFilterMode.WHITELIST 
            : PplUtilsConfig.ChatFilterMode.BLACKLIST;
        PplUtilsConfig.saveConfig();
    }
    
    /**
     * Checks if filter is enabled.
     */
    public static boolean isEnabled() {
        return PplUtilsConfig.chat_filter_enabled;
    }
    
    /**
     * Enables or disables the filter.
     */
    public static void setEnabled(boolean enabled) {
        PplUtilsConfig.chat_filter_enabled = enabled;
        PplUtilsConfig.saveConfig();
    }
    
    /**
     * Adds a word to the filter list.
     * Automatically enables the filter if it was disabled.
     */
    public static boolean addWord(@NotNull String word) {
        if (PplUtilsConfig.chat_filter_words == null) {
            PplUtilsConfig.chat_filter_words = new java.util.ArrayList<>();
        }
        
        String lowerWord = word.toLowerCase(Locale.ROOT).trim();
        if (lowerWord.isEmpty()) {
            return false;
        }
        
        if (!PplUtilsConfig.chat_filter_words.contains(lowerWord)) {
            PplUtilsConfig.chat_filter_words.add(lowerWord);
            invalidateCache(); // Invalidate cache when words change
            // Automatically enable filter when adding first word
            if (!PplUtilsConfig.chat_filter_enabled) {
                PplUtilsConfig.chat_filter_enabled = true;
                PplUtilsConst.LOGGER.info("Chat filter automatically enabled after adding word: {}", lowerWord);
            }
            PplUtilsConfig.saveConfig();
            return true;
        }
        return false;
    }
    
    /**
     * Removes a word from the filter list.
     */
    public static boolean removeWord(@NotNull String word) {
        if (PplUtilsConfig.chat_filter_words == null) {
            return false;
        }
        
        String lowerWord = word.toLowerCase(Locale.ROOT).trim();
        boolean removed = PplUtilsConfig.chat_filter_words.remove(lowerWord);
        if (removed) {
            invalidateCache(); // Invalidate cache when words change
            PplUtilsConfig.saveConfig();
        }
        return removed;
    }
    
    /**
     * Gets the list of filter words.
     */
    @NotNull
    public static List<String> getWords() {
        if (PplUtilsConfig.chat_filter_words == null) {
            return new java.util.ArrayList<>();
        }
        return new java.util.ArrayList<>(PplUtilsConfig.chat_filter_words);
    }
}

