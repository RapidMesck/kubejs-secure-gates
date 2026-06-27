package com.nbp.secure_gates.core;

import com.nbp.secure_gates.SecureGates;
import com.nbp.secure_gates.kubejs.AutoCraftGateKubeEvent;
import com.nbp.secure_gates.kubejs.BreakBlockGateKubeEvent;
import com.nbp.secure_gates.kubejs.CraftGateKubeEvent;
import com.nbp.secure_gates.kubejs.GateKubeEvent;
import com.nbp.secure_gates.kubejs.PlaceBlockGateKubeEvent;
import com.nbp.secure_gates.kubejs.SecureGateKubeJSEvents;
import com.nbp.secure_gates.kubejs.UseBlockGateKubeEvent;
import com.nbp.secure_gates.kubejs.UseItemGateKubeEvent;
import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.script.ScriptType;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public final class GateService {
    private GateService() {
    }

    public static GateDecision checkCraft(ServerPlayer player, ItemStack result, ResourceLocation recipeId) {
        GateContext context = new GateContext(
            GateAction.CRAFT_PICKUP,
            player,
            player.level(),
            null,
            result.copy(),
            recipeId,
            null,
            null,
            null,
            null
        );

        return post(SecureGateKubeJSEvents.CRAFT, new CraftGateKubeEvent(context));
    }

    public static GateDecision checkCraftPreview(ServerPlayer player, ItemStack result, ResourceLocation recipeId) {
        GateContext context = new GateContext(
            GateAction.CRAFT_PREVIEW,
            player,
            player.level(),
            null,
            result.copy(),
            recipeId,
            null,
            null,
            null,
            null
        );

        return post(SecureGateKubeJSEvents.CRAFT, new CraftGateKubeEvent(context));
    }
    public static GateDecision checkUseItem(ServerPlayer player, Level level, ItemStack stack, InteractionHand hand) {
        GateContext context = new GateContext(
            GateAction.USE_ITEM,
            player,
            level,
            stack.copy(),
            null,
            null,
            null,
            null,
            hand,
            null
        );

        return post(SecureGateKubeJSEvents.USE_ITEM, new UseItemGateKubeEvent(context));
    }

    public static GateDecision checkPlaceBlock(
        ServerPlayer player,
        Level level,
        ItemStack stack,
        ResourceLocation blockId,
        BlockPos pos,
        InteractionHand hand
    ) {
        GateContext context = new GateContext(
            GateAction.PLACE_BLOCK,
            player,
            level,
            stack.copy(),
            null,
            null,
            pos.immutable(),
            null,
            hand,
            blockId
        );

        return post(SecureGateKubeJSEvents.PLACE_BLOCK, new PlaceBlockGateKubeEvent(context));
    }

    public static GateDecision checkUseBlock(
        ServerPlayer player,
        Level level,
        ItemStack stack,
        BlockState state,
        BlockPos pos,
        InteractionHand hand
    ) {
        GateContext context = new GateContext(
            GateAction.USE_BLOCK,
            player,
            level,
            stack.copy(),
            null,
            null,
            pos.immutable(),
            state,
            hand,
            null
        );

        return post(SecureGateKubeJSEvents.USE_BLOCK, new UseBlockGateKubeEvent(context));
    }

    public static GateDecision checkBreakBlock(
        ServerPlayer player,
        Level level,
        ItemStack stack,
        BlockState state,
        BlockPos pos,
        InteractionHand hand
    ) {
        GateContext context = new GateContext(
            GateAction.BREAK_BLOCK,
            player,
            level,
            stack.copy(),
            null,
            null,
            pos.immutable(),
            state,
            hand,
            null
        );

        return post(SecureGateKubeJSEvents.BREAK_BLOCK, new BreakBlockGateKubeEvent(context));
    }

    public static GateDecision checkAutoCraft(
        Level level,
        ItemStack result,
        ResourceLocation recipeId,
        BlockPos pos
    ) {
        GateContext context = new GateContext(
            GateAction.AUTO_CRAFTER,
            null,
            level,
            null,
            result.copy(),
            recipeId,
            pos.immutable(),
            null,
            null,
            null
        );

        return post(SecureGateKubeJSEvents.AUTO_CRAFT, new AutoCraftGateKubeEvent(context));
    }

    private static GateDecision post(EventHandler handler, GateKubeEvent event) {
        try {
            handler.post(ScriptType.SERVER, event);
        } catch (Throwable throwable) {
            SecureGates.LOGGER.error("Secure gate event failed for action {}", event.getAction(), throwable);
            return GateDecision.allow();
        }

        if (event.isDenied()) {
            return GateDecision.deny(event.getMessage());
        }

        return GateDecision.allow();
    }
}