package com.nbp.secure_gates.mixin;

import com.nbp.secure_gates.core.GateDecision;
import com.nbp.secure_gates.core.GateService;
import com.nbp.secure_gates.util.MessageUtil;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Slot.class)
public abstract class ResultSlotMixin {
    @Inject(method = "mayPickup", at = @At("HEAD"), cancellable = true)
    private void securegates$mayPickup(Player player, CallbackInfoReturnable<Boolean> cir) {
        if (!((Object) this instanceof ResultSlot) || !(player instanceof ServerPlayer serverPlayer)) {
            return;
        }

        ItemStack result = ((Slot) (Object) this).getItem();
        if (result.isEmpty()) {
            return;
        }

        GateDecision decision = GateService.checkCraft(serverPlayer, result, null);
        if (decision.denied()) {
            MessageUtil.send(serverPlayer, decision.message());
            cir.setReturnValue(false);
        }
    }
}
