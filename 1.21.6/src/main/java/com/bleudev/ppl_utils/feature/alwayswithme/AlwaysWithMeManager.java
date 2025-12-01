package com.bleudev.ppl_utils.feature.alwayswithme;

import com.bleudev.ppl_utils.PplUtilsConst;
import com.bleudev.ppl_utils.config.PplUtilsConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.CustomModelDataComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.bleudev.ppl_utils.util.ServerUtils.executeCommand;

/**
 * Manages the "Always With Me" feature that automatically applies model to items when held.
 * Format: [item_id;"model_name"], [item_id2;"model_name2"]
 * Example: [minecraft:stick;"hand"], [minecraft:shield;"stop_shield"]
 */
public class AlwaysWithMeManager {
    private static final AlwaysWithMeManager INSTANCE = new AlwaysWithMeManager();
    
    // Pattern to match [item_id;"model_name"]
    private static final Pattern MAPPING_PATTERN = Pattern.compile("\\[([^;]+);\"([^\"]+)\"\\]");
    
    private int lastSelectedHotbarSlot = -1;
    private ItemStack lastMainHandItem = null;
    private Map<String, List<String>> parsedMappings = new HashMap<>();
    private Set<String> ignoredModelNames = new HashSet<>();
    
    private AlwaysWithMeManager() {
    }
    
    public static AlwaysWithMeManager getInstance() {
        return INSTANCE;
    }
    
    /**
     * Parses the mappings string into a map.
     * Format: [item_id;"model_name"], [item_id2;"model_name2"]
     */
    private void parseMappings(@NotNull String mappingsString) {
        parsedMappings.clear();
        ignoredModelNames.clear();
        
        if (mappingsString == null || mappingsString.trim().isEmpty()) {
            return;
        }
        
        // Find all mappings
        Matcher matcher = MAPPING_PATTERN.matcher(mappingsString);
        Map<String, List<String>> tempMappings = new HashMap<>();
        List<String> allModelNames = new ArrayList<>();
        
        while (matcher.find()) {
            String itemId = matcher.group(1).trim();
            String modelName = matcher.group(2).trim();
            
            // Normalize item ID (remove minecraft: prefix if present, or keep full ID)
            if (itemId.startsWith("minecraft:")) {
                itemId = itemId.substring(10);
            }
            
            tempMappings.computeIfAbsent(itemId, k -> new ArrayList<>()).add(modelName);
            allModelNames.add(modelName);
        }
        
        // Find duplicate model names (case-insensitive)
        Map<String, Integer> modelNameCounts = new HashMap<>();
        for (String modelName : allModelNames) {
            String lower = modelName.toLowerCase();
            modelNameCounts.put(lower, modelNameCounts.getOrDefault(lower, 0) + 1);
        }
        
        // Mark duplicate model names as ignored
        for (Map.Entry<String, Integer> entry : modelNameCounts.entrySet()) {
            if (entry.getValue() > 1) {
                ignoredModelNames.add(entry.getKey());
            }
        }
        
        // Filter out ignored model names and keep only valid mappings
        for (Map.Entry<String, List<String>> entry : tempMappings.entrySet()) {
            List<String> validModels = new ArrayList<>();
            for (String modelName : entry.getValue()) {
                if (!ignoredModelNames.contains(modelName.toLowerCase())) {
                    validModels.add(modelName);
                }
            }
            if (!validModels.isEmpty()) {
                parsedMappings.put(entry.getKey(), validModels);
            }
        }
    }
    
    /**
     * Checks if item stack has custom model data.
     */
    private boolean hasCustomModelData(@NotNull ItemStack stack) {
        CustomModelDataComponent component = stack.get(DataComponentTypes.CUSTOM_MODEL_DATA);
        return component != null;
    }
    
    /**
     * Gets item ID from ItemStack.
     * Returns either "minecraft:item_id" or just "item_id" depending on namespace.
     */
    @NotNull
    private String getItemId(@NotNull ItemStack stack) {
        Identifier id = Registries.ITEM.getId(stack.getItem());
        if (id.getNamespace().equals("minecraft")) {
            return id.getPath(); // Return just "stick" for minecraft:stick
        }
        return id.toString(); // Return full "mod:item" for mod items
    }
    
    /**
     * Checks if two item stacks are the same item (ignoring count and NBT).
     */
    private boolean isSameItem(@Nullable ItemStack stack1, @Nullable ItemStack stack2) {
        if (stack1 == null && stack2 == null) return true;
        if (stack1 == null || stack2 == null) return false;
        return stack1.getItem() == stack2.getItem();
    }
    
    /**
     * Updates the manager. Should be called every tick.
     * Checks for hotbar slot changes or item changes in main hand.
     */
    public void tick(@NotNull MinecraftClient client) {
        if (!PplUtilsConfig.always_with_me_enabled) {
            lastMainHandItem = null;
            lastSelectedHotbarSlot = -1;
            return;
        }
        
        if (client.player == null) {
            lastMainHandItem = null;
            lastSelectedHotbarSlot = -1;
            return;
        }
        
        // Parse mappings if config changed
        String currentMappings = PplUtilsConfig.always_with_me_mappings;
        if (currentMappings == null || currentMappings.trim().isEmpty()) {
            parsedMappings.clear();
            lastMainHandItem = null;
            lastSelectedHotbarSlot = -1;
            return;
        }
        
        // Re-parse mappings (in case config was updated)
        parseMappings(currentMappings);
        
        int currentHotbarSlot = client.player.getInventory().getSelectedSlot();
        ItemStack currentMainHand = client.player.getMainHandStack();
        
        // Check if hotbar slot changed or item in main hand changed
        boolean hotbarSlotChanged = currentHotbarSlot != lastSelectedHotbarSlot;
        boolean itemChanged = !isSameItem(currentMainHand, lastMainHandItem);
        
        if (hotbarSlotChanged || itemChanged) {
            lastSelectedHotbarSlot = currentHotbarSlot;
            lastMainHandItem = currentMainHand.isEmpty() ? null : currentMainHand.copy();
            
            if (!currentMainHand.isEmpty()) {
                // Check if item has custom model data
                if (!hasCustomModelData(currentMainHand)) {
                    String itemId = getItemId(currentMainHand);
                    
                    // Find matching mappings for this item
                    List<String> modelNames = parsedMappings.get(itemId);
                    if (modelNames != null && !modelNames.isEmpty()) {
                        // If multiple models for same item, choose random
                        String selectedModel = modelNames.get(new Random().nextInt(modelNames.size()));
                        
                        // Send command (without leading /)
                        String command = "model " + selectedModel;
                        executeCommand(client, command);
                        PplUtilsConst.LOGGER.debug("Sent model command: /{} for item: {} (selected from {} options)", 
                            command, itemId, modelNames.size());
                    }
                }
            }
        }
    }
}
