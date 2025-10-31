package com.bleudev.ppl_utils.client

import com.bleudev.ppl_utils.client.compat.modmenu.PplUtilsConfig
import net.fabricmc.api.ClientModInitializer

class Ppl_utilsClient : ClientModInitializer {
    override fun onInitializeClient() {
        PplUtilsConfig.initialize()
    }
}
