package com.bleudev.ppl_utils.feature.executablequeue;

import com.bleudev.ppl_utils.PplUtilsConst;
import com.bleudev.ppl_utils.config.PplUtilsConfig;
import net.minecraft.client.MinecraftClient;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.bleudev.ppl_utils.util.ServerUtils.executeCommand;

/**
 * Manages the "Executable Queue" feature that executes commands in sequence with delays.
 * Format: [{/command argument}; ms delay], [{/command argument}; ms delay]
 * Example: [{/ie lore add text}; 100ms], [{/ie lore add text2}; 100ms]
 */
public class ExecutableQueueManager {
    private static final ExecutableQueueManager INSTANCE = new ExecutableQueueManager();
    
    // Pattern to match [{/command}; ms delay]
    private static final Pattern QUEUE_ITEM_PATTERN = Pattern.compile("\\[\\{([^}]+)\\};\\s*(\\d+)ms\\]");
    
    private boolean isExecuting = false;
    private List<QueueItem> executionQueue = new ArrayList<>();
    private int currentCommandIndex = 0;
    private long nextCommandTime = 0;
    
    private ExecutableQueueManager() {
    }
    
    public static ExecutableQueueManager getInstance() {
        return INSTANCE;
    }
    
    /**
     * Represents a single command in the queue with its delay.
     */
    private static class QueueItem {
        final String command;
        final int delayMs;
        
        QueueItem(String command, int delayMs) {
            this.command = command;
            this.delayMs = delayMs;
        }
    }
    
    /**
     * Parses the queue string into a list of QueueItems.
     * Format: [{/command argument}; ms delay], [{/command argument}; ms delay]
     */
    private List<QueueItem> parseQueue(@NotNull String queueString) {
        List<QueueItem> items = new ArrayList<>();
        
        if (queueString == null || queueString.trim().isEmpty()) {
            return items;
        }
        
        Matcher matcher = QUEUE_ITEM_PATTERN.matcher(queueString);
        while (matcher.find()) {
            String command = matcher.group(1).trim();
            int delayMs;
            try {
                delayMs = Integer.parseInt(matcher.group(2));
            } catch (NumberFormatException e) {
                PplUtilsConst.LOGGER.warn("Invalid delay in queue item: {}", matcher.group(2));
                continue;
            }
            
            items.add(new QueueItem(command, delayMs));
        }
        
        PplUtilsConst.LOGGER.debug("Parsed {} commands from queue", items.size());
        return items;
    }
    
    /**
     * Gets clipboard content from GLFW.
     */
    @NotNull
    private String getClipboardContent(@NotNull MinecraftClient client) {
        long window = client.getWindow().getHandle();
        String clipboard = GLFW.glfwGetClipboardString(window);
        return clipboard != null ? clipboard : "";
    }
    
    /**
     * Starts executing the queue of commands with delays from config.
     * Commands are executed sequentially, waiting for the specified delay between each.
     */
    public void executeQueue(@NotNull MinecraftClient client) {
        if (isExecuting) {
            PplUtilsConst.LOGGER.debug("Queue is already executing, ignoring request");
            return;
        }
        
        String queueString = PplUtilsConfig.executable_queue_commands;
        if (queueString == null || queueString.trim().isEmpty()) {
            PplUtilsConst.LOGGER.debug("Queue string is empty, nothing to execute");
            return;
        }
        
        startExecution(client, queueString);
    }
    
    /**
     * Starts executing the queue of commands with delays from clipboard.
     * Commands are executed sequentially, waiting for the specified delay between each.
     */
    public void executeQueueFromClipboard(@NotNull MinecraftClient client) {
        if (isExecuting) {
            PplUtilsConst.LOGGER.debug("Queue is already executing, ignoring request");
            return;
        }
        
        String clipboardContent = getClipboardContent(client);
        if (clipboardContent == null || clipboardContent.trim().isEmpty()) {
            PplUtilsConst.LOGGER.debug("Clipboard is empty, nothing to execute");
            return;
        }
        
        PplUtilsConst.LOGGER.debug("Executing queue from clipboard: {}", clipboardContent);
        startExecution(client, clipboardContent);
    }
    
    /**
     * Starts execution of the queue with the given string.
     */
    private void startExecution(@NotNull MinecraftClient client, @NotNull String queueString) {
        // Parse the queue
        executionQueue = parseQueue(queueString);
        
        if (executionQueue.isEmpty()) {
            PplUtilsConst.LOGGER.debug("No valid commands found in queue");
            return;
        }
        
        isExecuting = true;
        currentCommandIndex = 0;
        nextCommandTime = System.currentTimeMillis();
        PplUtilsConst.LOGGER.debug("Starting queue execution with {} commands", executionQueue.size());
        
        // Execute first command immediately
        executeNextCommand(client);
    }
    
    /**
     * Executes the next command in the queue.
     * Should be called from the client tick event.
     */
    public void tick(@NotNull MinecraftClient client) {
        if (!isExecuting || executionQueue.isEmpty()) {
            return;
        }
        
        long currentTime = System.currentTimeMillis();
        if (currentTime >= nextCommandTime && currentCommandIndex < executionQueue.size()) {
            executeNextCommand(client);
        }
    }
    
    /**
     * Executes the next command and schedules the next one.
     */
    private void executeNextCommand(@NotNull MinecraftClient client) {
        if (currentCommandIndex >= executionQueue.size()) {
            // Queue completed
            isExecuting = false;
            executionQueue.clear();
            currentCommandIndex = 0;
            PplUtilsConst.LOGGER.debug("Queue execution completed");
            return;
        }
        
        QueueItem item = executionQueue.get(currentCommandIndex);
        executeCommand(client, item.command);
        PplUtilsConst.LOGGER.debug("Executed command: /{}", item.command);
        
        currentCommandIndex++;
        
        // Schedule next command if there is one
        if (currentCommandIndex < executionQueue.size()) {
            nextCommandTime = System.currentTimeMillis() + item.delayMs;
        } else {
            // Last command executed, mark as complete
            isExecuting = false;
            executionQueue.clear();
            currentCommandIndex = 0;
            PplUtilsConst.LOGGER.debug("Queue execution completed");
        }
    }
    
    /**
     * Checks if the queue is currently executing.
     */
    public boolean isExecuting() {
        return isExecuting;
    }
}

