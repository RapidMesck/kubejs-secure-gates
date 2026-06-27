package com.nbp.secure_gates.kubejs;

import com.nbp.secure_gates.core.GateContext;
import com.nbp.secure_gates.util.IdUtil;
import dev.latvian.mods.kubejs.event.KubeEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class GateKubeEvent implements KubeEvent {
    private final GateContext context;
    private boolean denied;
    private String message;

    public GateKubeEvent(GateContext context) {
        this.context = context;
    }

    public void deny(String message) {
        this.denied = true;
        this.message = message;
    }

    public void cancel() {
        deny(null);
    }

    public boolean isCancelled() {
        return denied;
    }

    public boolean isDenied() {
        return denied;
    }

    public String getMessage() {
        return message;
    }

    public GateContext getContext() {
        return context;
    }

    public ServerPlayer getPlayer() {
        return context.player();
    }

    public Level getLevel() {
        return context.level();
    }

    public ItemStack getItem() {
        return context.itemStack();
    }

    public ItemStack getResult() {
        return context.resultStack();
    }

    public String getItemId() {
        return IdUtil.itemId(context.itemStack());
    }

    public String getResultId() {
        return IdUtil.itemId(context.resultStack());
    }

    public BlockState getBlock() {
        return context.blockState();
    }

    public String getBlockId() {
        if (context.blockId() != null) {
            return context.blockId().toString();
        }

        return IdUtil.blockId(context.blockState());
    }

    public String getRecipeId() {
        return context.recipeId() == null ? null : context.recipeId().toString();
    }

    public BlockPos getPos() {
        return context.blockPos();
    }

    public String getHand() {
        InteractionHand hand = context.hand();
        return hand == null ? null : hand.name();
    }

    public String getAction() {
        return context.action().name();
    }
}
