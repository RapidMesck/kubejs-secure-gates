package com.nbp.secure_gates.util;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public final class IdUtil {
    private IdUtil() {
    }

    public static String itemId(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return null;
        }

        return BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
    }

    public static String blockId(BlockState state) {
        if (state == null) {
            return null;
        }

        return BuiltInRegistries.BLOCK.getKey(state.getBlock()).toString();
    }

    public static net.minecraft.resources.ResourceLocation blockItemId(ItemStack stack) {
        if (stack == null || stack.isEmpty() || !(stack.getItem() instanceof BlockItem blockItem)) {
            return null;
        }

        Block block = blockItem.getBlock();
        return BuiltInRegistries.BLOCK.getKey(block);
    }
}
