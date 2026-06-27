package com.nbp.secure_gates.kubejs;

import com.nbp.secure_gates.SecureGates;
import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;

public final class SecureGateKubeJSEvents {
    public static final EventGroup GROUP = EventGroup.of(SecureGates.EVENT_GROUP);

    public static final EventHandler CRAFT = GROUP.server("craft", () -> CraftGateKubeEvent.class);
    public static final EventHandler USE_ITEM = GROUP.server("useItem", () -> UseItemGateKubeEvent.class);
    public static final EventHandler PLACE_BLOCK = GROUP.server("placeBlock", () -> PlaceBlockGateKubeEvent.class);
    public static final EventHandler USE_BLOCK = GROUP.server("useBlock", () -> UseBlockGateKubeEvent.class);
    public static final EventHandler BREAK_BLOCK = GROUP.server("breakBlock", () -> BreakBlockGateKubeEvent.class);
    public static final EventHandler AUTO_CRAFT = GROUP.server("autoCraft", () -> AutoCraftGateKubeEvent.class);

    private SecureGateKubeJSEvents() {
    }
}