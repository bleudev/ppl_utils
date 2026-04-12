package com.bleudev.ppl_utils.feature.rp;

import com.bleudev.ppl_utils.DataStorageHelper;
import com.bleudev.ppl_utils.util.helper.ApiHelper;
import com.google.gson.JsonObject;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackRepository;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class RpHelper {
    public static void checkUpdates() {
        try {
            JsonObject rpMeta = ApiHelper.getRpMetadata();
            String version = rpMeta.get("main").getAsJsonObject().get("version").getAsString();
            String url = rpMeta.get("main").getAsJsonObject().get("url").getAsString();

            if (!((getRpPack() != null) && version.equals(DataStorageHelper.getData().rpLatestVersion())) && deleteAndDownloadRp(version, url)) {
                DataStorageHelper.save(DataStorageHelper.getData().withRpLatestVersion(version));
            }
            Pack pack = getRpPack();
            if (pack != null) {
                PackRepository packRepository = Minecraft.getInstance().getResourcePackRepository();
                if (packRepository.addPack(pack.getId())) {
                    Minecraft.getInstance().options.updateResourcePacks(packRepository);
                }
            }
        } catch (IOException | InterruptedException | IllegalStateException e) {
            System.out.println("Error with checking updates");
        }
    }

    @Nullable
    private static Pack getRpPack() {
        PackRepository packRepository = Minecraft.getInstance().getResourcePackRepository();
        packRepository.reload();
        for (Pack pack : packRepository.getAvailablePacks()) {
            if (pack.getDescription().getString().contains("Pepeland Pack")) return pack;
        }
        return null;
    }

    private static boolean deleteAndDownloadRp(String version, String url) {
        Minecraft minecraft = Minecraft.getInstance();
        SystemToast.add(minecraft.getToastManager(), new SystemToast.SystemToastId(), Component.literal("Pack updater"), Component.literal("Found pack updater"));
        // Download
        URL url1;
        try {
            url1 = URL.of(URI.create(url), null);
        } catch (MalformedURLException e) {
            SystemToast.add(minecraft.getToastManager(), new SystemToast.SystemToastId(), Component.literal("Pack updater"), Component.literal("Update failed"));
            return false;
        }
        String path = "pepeland-pack-v." + version + ".zip";

        try (BufferedInputStream bin = new BufferedInputStream(url1.openStream());
             FileOutputStream fileOutputStream = new FileOutputStream(path)) {
            byte[] dataBuffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = bin.read(dataBuffer, 0, 1024)) != -1) {
                fileOutputStream.write(dataBuffer, 0, bytesRead);
            }

            InputStream in = url1.openStream();
            Files.copy(in, minecraft.getResourcePackDirectory().resolve(path), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            SystemToast.add(minecraft.getToastManager(), new SystemToast.SystemToastId(), Component.literal("Pack updater"), Component.literal("Update failed"));
            return false;
        }

        // Disable resource pack and remove
        Pack pack = getRpPack();
        if (pack != null) {
            PackRepository packRepository = minecraft.getResourcePackRepository();
            if (packRepository.removePack(pack.getId())) {
                minecraft.options.updateResourcePacks(packRepository);
            }
        }
        SystemToast.add(minecraft.getToastManager(), new SystemToast.SystemToastId(), Component.literal("Pack updater"), Component.literal("Update was completed").withStyle(ChatFormatting.GREEN));
        return true;
    }
}
