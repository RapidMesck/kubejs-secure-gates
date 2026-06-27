package com.nbp.secure_gates.mixin;

import com.nbp.secure_gates.core.GateDecision;
import com.nbp.secure_gates.core.GateService;
import com.nbp.secure_gates.util.IdUtil;
import com.nbp.secure_gates.util.MessageUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerGameMode.class)
public abstract class ServerPlayerGameModeMixin {
    @Shadow
    protected ServerLevel level;

    @Shadow
    @Final
    protected ServerPlayer player;

    @Inject(method = "useItem", at = @At("HEAD"), cancellable = true)
    private void securegates$useItem(
        ServerPlayer player,
        Level level,
        ItemStack stack,
        InteractionHand hand,
        CallbackInfoReturnable<InteractionResult> cir
    ) {
        if (stack.isEmpty()) {
            return;
        }

        GateDecision decision = GateService.checkUseItem(player, level, stack, hand);
        if (decision.denied()) {
            MessageUtil.send(player, decision.message());
            cir.setReturnValue(InteractionResult.FAIL);
        }
    }

    @Inject(method = "useItemOn", at = @At("HEAD"), cancellable = true)
    private void securegates$useItemOn(
        ServerPlayer player,
        Level level,
        ItemStack stack,
        InteractionHand hand,
        BlockHitResult hitResult,
        CallbackInfoReturnable<InteractionResult> cir
    ) {
        if (!stack.isEmpty()) {
            GateDecision itemDecision = GateService.checkUseItem(player, level, stack, hand);
            if (itemDecision.denied()) {
                MessageUtil.send(player, itemDecision.message());
                cir.setReturnValue(InteractionResult.FAIL);
                return;
            }
        }

        ResourceLocation placingBlockId = IdUtil.blockItemId(stack);
        if (placingBlockId != null) {
            BlockPos placePos = hitResult.getBlockPos().relative(hitResult.getDirection());
            GateDecision placeDecision = GateService.checkPlaceBlock(
                player,
                level,
                stack,
                placingBlockId,
                placePos,
                hand
            );

            if (placeDecision.denied()) {
                MessageUtil.send(player, placeDecision.message());
                cir.setReturnValue(InteractionResult.FAIL);
                return;
            }
        }

        BlockPos clickedPos = hitResult.getBlockPos();
        BlockState clickedState = level.getBlockState(clickedPos);
        GateDecision useBlockDecision = GateService.checkUseBlock(
            player,
            level,
            stack,
            clickedState,
            clickedPos,
            hand
        );

        if (useBlockDecision.denied()) {
            MessageUtil.send(player, useBlockDecision.message());
            cir.setReturnValue(InteractionResult.FAIL);
        }
    }

    @Inject(method = "destroyBlock", at = @At("HEAD"), cancellable = true)
    private void securegates$destroyBlock(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        ItemStack stack = this.player.getMainHandItem();

        if (!stack.isEmpty()) {
            GateDecision itemDecision = GateService.checkUseItem(
                this.player,
                this.level,
                stack,
                InteractionHand.MAIN_HAND
            );

            if (itemDecision.denied()) {
                MessageUtil.send(this.player, itemDecision.message());
                cir.setReturnValue(false);
                return;
            }
        }

        BlockState state = this.level.getBlockState(pos);
        GateDecision breakDecision = GateService.checkBreakBlock(
            this.player,
            this.level,
            stack,
            state,
            pos,
            InteractionHand.MAIN_HAND
        );

        if (breakDecision.denied()) {
            MessageUtil.send(this.player, breakDecision.message());
            cir.setReturnValue(false);
        }
    }
}