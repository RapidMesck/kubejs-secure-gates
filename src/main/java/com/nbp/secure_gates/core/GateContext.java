package com.nbp.secure_gates.core;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public record GateContext(
    GateAction action,
    ServerPlayer player,
    Level level,
    ItemStack itemStack,
    ItemStack resultStack,
    ResourceLocation recipeId,
    BlockPos blockPos,
    BlockState blockState,
    InteractionHand hand,
    ResourceLocation blockId
) {
}
