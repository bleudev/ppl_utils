package com.bleudev.ppl_utils.util;

import net.minecraft.client.MinecraftClient;
import org.jetbrains.annotations.NotNull;

/**
 * Caches server-related information to avoid repeated computations.
 */
public class ServerCache {
    private static String cachedServerAddress = null;
    private static Boolean cachedIsOnPepeland = null;
    private static Boolean cachedIsLobbyCommandWorking = null;
    private static Boolean cachedIsGlobalChatWorking = null;
    
    /**
     * Invalidates the cache. Should be called when server connection changes.
     */
    public static void invalidate() {
        cachedServerAddress = null;
        cachedIsOnPepeland = null;
        cachedIsLobbyCommandWorking = null;
        cachedIsGlobalChatWorking = null;
    }
    
    /**
     * Gets the current server address, using cache if available.
     * 
     * @param client The Minecraft client
     * @return Server address or null if not connected
     */
    public static String getServerAddress(@NotNull MinecraftClient client) {
        var server = client.getCurrentServerEntry();
        if (server == null) {
            invalidate();
            return null;
        }
        
        String address = server.address;
        if (cachedServerAddress == null || !cachedServerAddress.equals(address)) {
            cachedServerAddress = address;
            // Invalidate dependent caches
            cachedIsOnPepeland = null;
            cachedIsLobbyCommandWorking = null;
            cachedIsGlobalChatWorking = null;
        }
        
        return cachedServerAddress;
    }
    
    /**
     * Checks if client is on Pepeland, using cache if available.
     * 
     * @param client The Minecraft client
     * @return True if on Pepeland server
     */
    public static boolean isOnPepeland(@NotNull MinecraftClient client) {
        if (cachedIsOnPepeland == null) {
            cachedIsOnPepeland = ServerUtils.isClientOnPepeland(client);
        }
        return cachedIsOnPepeland;
    }
    
    /**
     * Checks if lobby command works, using cache if available.
     * 
     * @param client The Minecraft client
     * @return True if lobby command is available
     */
    public static boolean isLobbyCommandWorking(@NotNull MinecraftClient client) {
        if (cachedIsLobbyCommandWorking == null) {
            cachedIsLobbyCommandWorking = ServerUtils.isLobbyCommandWorking(client);
        }
        return cachedIsLobbyCommandWorking;
    }
    
    /**
     * Checks if global chat works, using cache if available.
     * 
     * @param client The Minecraft client
     * @return True if global chat is available
     */
    public static boolean isGlobalChatWorking(@NotNull MinecraftClient client) {
        if (cachedIsGlobalChatWorking == null) {
            cachedIsGlobalChatWorking = ServerUtils.isGlobalChatWorking(client);
        }
        return cachedIsGlobalChatWorking;
    }
}


