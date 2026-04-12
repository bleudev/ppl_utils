package com.bleudev.ppl_utils;

import com.google.gson.*;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonWriter;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.Optionull;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import static com.bleudev.ppl_utils.PplUtilsConst.LOGGER;

public class DataStorageHelper {
    public record StorageData(long startRestartTime, long restartTime, int cachedEnderChestCount, String rpLatestVersion) {
        @Nullable
        private String toJsonString() {
            JsonObject object = new JsonObject();
            object.addProperty("startRestartTime", this.startRestartTime);
            object.addProperty("restartTime", this.restartTime);
            object.addProperty("cachedEnderChestCount", this.cachedEnderChestCount);
            object.addProperty("rpLatestVersion", this.rpLatestVersion);
            try {
                StringWriter stringWriter = new StringWriter();
                JsonWriter jsonWriter = new JsonWriter(stringWriter);
                jsonWriter.setFormattingStyle(FormattingStyle.PRETTY);
                jsonWriter.setStrictness(Strictness.LENIENT);
                Streams.write(object, jsonWriter);
                return stringWriter.toString();
            } catch (IOException ignored) {
                return null;
            }
        }
        @NotNull
        private static StorageData fromJsonString(String jsonString) {
            JsonObject object = JsonParser.parseString(jsonString).getAsJsonObject();
            return new StorageData(
                Optionull.mapOrDefault(object.get("startRestartTime"), JsonElement::getAsLong, 0L),
                Optionull.mapOrDefault(object.get("restartTime"), JsonElement::getAsLong, 0L),
                Optionull.mapOrDefault(object.get("cachedEnderChestCount"), JsonElement::getAsInt, -1),
                Optionull.mapOrDefault(object.get("rpLatestVersion"), JsonElement::getAsString, "")
            );
        }

        @Contract("_ -> new")
        public @NotNull StorageData withStartRestartTime(long newTime) {
            return new StorageData(newTime, restartTime(), cachedEnderChestCount(), rpLatestVersion());
        }
        @Contract("_ -> new")
        public @NotNull StorageData withRestartTime(long newTime) {
            return new StorageData(startRestartTime(), newTime, cachedEnderChestCount(), rpLatestVersion());
        }
        @Contract("_ -> new")
        public @NotNull StorageData withCachedEnderChestCount(int newCount) {
            return new StorageData(startRestartTime(), restartTime(), newCount, rpLatestVersion());
        }
        @Contract("_ -> new")
        public @NotNull StorageData withRpLatestVersion(String newVersion) {
            return new StorageData(startRestartTime(), restartTime(), cachedEnderChestCount(), newVersion);
        }
    }

    private static final String PATH = "ppl_utils_data.json";

    private static @NotNull Path getFullpath() {
        return FabricLoader.getInstance().getConfigDir().resolve(PATH);
    }

    public static void save() {
        try {
            Files.writeString(getFullpath(), Objects.requireNonNull(data.toJsonString()));
        } catch (IOException e) {
            LOGGER.error(e.getLocalizedMessage());
        }
    }

    public static void save(StorageData data) {
        DataStorageHelper.data = data;
        save();
    }

    public static void load() {
        if (Files.exists(getFullpath())) { try {
            DataStorageHelper.data = StorageData.fromJsonString(Files.readString(getFullpath()));
        } catch (IOException e) {
            LOGGER.error(e.getLocalizedMessage());
        } }
    }

    private static StorageData data = new StorageData(0, 0, -1, "");
    public static StorageData getData() {
        return data;
    }
}
