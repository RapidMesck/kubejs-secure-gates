package com.nbp.secure_gates.mixin;

import com.nbp.secure_gates.core.GateDecision;
import com.nbp.secure_gates.core.GateService;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.CrafterBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.CrafterBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(CrafterBlock.class)
public abstract class CrafterBlockMixin {
    @Inject(method = "dispenseFrom", at = @At("HEAD"), cancellable = true)
    private void securegates$dispenseFrom(BlockState state, ServerLevel level, BlockPos pos, CallbackInfo ci) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof CrafterBlockEntity crafter)) {
            return;
        }

        CraftingInput input = crafter.asCraftInput();
        Optional<RecipeHolder<CraftingRecipe>> optionalRecipe = CrafterBlock.getPotentialResults(level, input);
        if (optionalRecipe.isEmpty()) {
            return;
        }

        RecipeHolder<CraftingRecipe> recipe = optionalRecipe.get();
        ItemStack result = recipe.value().assemble(input, level.registryAccess());
        if (result.isEmpty()) {
            return;
        }

        GateDecision decision = GateService.checkAutoCraft(level, result, recipe.id(), pos);
        if (decision.denied()) {
            level.levelEvent(1050, pos, 0);
            ci.cancel();
        }
    }
}