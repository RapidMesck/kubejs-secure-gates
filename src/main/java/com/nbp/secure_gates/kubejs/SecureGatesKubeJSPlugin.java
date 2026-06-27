package com.nbp.secure_gates.kubejs;

import com.nbp.secure_gates.SecureGates;
import dev.latvian.mods.kubejs.event.EventGroupRegistry;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;

public final class SecureGatesKubeJSPlugin implements KubeJSPlugin {
    @Override
    public void init() {
        SecureGates.LOGGER.info("Loading KubeJS plugin for {}", SecureGates.EVENT_GROUP);
    }

    @Override
    public void registerEvents(EventGroupRegistry registry) {
        registry.register(SecureGateKubeJSEvents.GROUP);
    }
}
