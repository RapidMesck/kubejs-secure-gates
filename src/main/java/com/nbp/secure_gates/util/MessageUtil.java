package com.nbp.secure_gates.util;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public final class MessageUtil {
    private static final String DEFAULT_MESSAGE = "Voce ainda nao desbloqueou esta acao.";

    private MessageUtil() {
    }

    public static void send(ServerPlayer player, String message) {
        String text = message == null || message.isBlank() ? DEFAULT_MESSAGE : message;
        player.displayClientMessage(Component.literal(text), true);
    }
}
