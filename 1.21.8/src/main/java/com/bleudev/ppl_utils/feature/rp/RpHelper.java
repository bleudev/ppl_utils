package com.bleudev.ppl_utils.feature.rp;

import com.bleudev.ppl_utils.DataStorageHelper;
import com.bleudev.ppl_utils.util.helper.ApiHelper;
import com.google.gson.JsonObject;
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
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static net.minecraft.ChatFormatting.GREEN;
import static net.minecraft.ChatFormatting.RED;


public class RpHelper {
    public static void asyncCheckUpdates() {
        new Thread(RpHelper::checkUpdates).start();
    }

    private static void checkUpdates() {
        try {
            JsonObject rpMeta = ApiHelper.getRpMetadata();
            String version = rpMeta.get("main").getAsJsonObject().get("version").getAsString();
            String url = rpMeta.get("main").getAsJsonObject().get("url").getAsString();

            if (!((getRpPack() != null) && version.equals(DataStorageHelper.getData().rpLatestVersion())) && deleteAndDownloadRp(version, url)) {
                DataStorageHelper.save(DataStorageHelper.getData().withRpLatestVersion(version));
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
            if (isPepelandResourcePack(pack, false)) return pack;
        }
        return null;
    }

    private static boolean isPepelandResourcePack(Pack pack, boolean latest) {
        String description = pack.getDescription().getString();
        return description.contains("Pepeland Pack") && (
            !latest || description.contains("v" + DataStorageHelper.getData().rpLatestVersion())
        );
    }

    private static boolean deleteAndDownloadRp(String version, String url) {
        Component toastTitle = Component.translatable("ppl_utils.text.toast.rp.title");
        Component startToast = Component.translatable("ppl_utils.text.toast.rp.start");
        Component failureToast = Component.translatable("ppl_utils.text.toast.rp.failure").withStyle(RED);
        Component successToast = Component.translatable("ppl_utils.text.toast.rp.success").withStyle(GREEN);
        Minecraft minecraft = Minecraft.getInstance();
        Consumer<Component> toast = (c) -> SystemToast
            .add(minecraft.getToastManager(), new SystemToast.SystemToastId(), toastTitle, c);

        toast.accept(startToast);
        // Download
        URL url1;
        try {
            url1 = URL.of(URI.create(url), null);
        } catch (MalformedURLException e) {
            toast.accept(failureToast);
            return false;
        }
        String path = "pepeland-pack-v." + version + ".zip";
        Path rpDir = minecraft.getResourcePackDirectory();

        try (BufferedInputStream bin = new BufferedInputStream(url1.openStream());
             FileOutputStream fileOutputStream = new FileOutputStream(path)) {
            byte[] dataBuffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = bin.read(dataBuffer, 0, 1024)) != -1) {
                fileOutputStream.write(dataBuffer, 0, bytesRead);
            }

            InputStream in = url1.openStream();
            Files.copy(in, rpDir.resolve(path), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            toast.accept(failureToast);
            return false;
        }

        // Update resource packs
        PackRepository packRepository = Minecraft.getInstance().getResourcePackRepository();
        packRepository.reload();
        boolean bl = false;
        for (Pack pack : packRepository.getAvailablePacks()) {
            if (isPepelandResourcePack(pack, false)) {
                if (packRepository.removePack(pack.getId())) bl = true;
            }
            if (isPepelandResourcePack(pack, true)) {
                if (packRepository.addPack(pack.getId())) bl = true;
            }
        }
        if (bl) {
            minecraft.options.updateResourcePacks(packRepository);
        }

        // Delete unused resource packs
        try (Stream<Path> ws = Files.walk(rpDir)) {
            for (Path p : ws.filter(Files::isRegularFile).toList()) {
                String name = String.valueOf(p.getFileName());
                if (name.contains("pepeland") && !name.contains(version)) {
                    Files.deleteIfExists(p);
                }
            }
        } catch (IOException e) {
            return true;
        }
        try (Stream<Path> ws = Files.walk(rpDir.getParent(), 1)) {
            for (Path p : ws.filter(Files::isRegularFile).toList()) {
                String name = String.valueOf(p.getFileName());
                if (name.contains("pepeland") && name.contains("zip")) {
                    Files.deleteIfExists(p);
                }
            }
        } catch (IOException e) {
            return true;
        }

        toast.accept(successToast);
        return true;
    }
}
