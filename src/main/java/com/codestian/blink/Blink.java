package com.codestian.blink;

import com.codestian.blink.util.IEntityDataSaver;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Blink implements ModInitializer {
    public static final String MOD_ID = "blink";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final Identifier EYES_CLOSED_PACKET_ID = new Identifier("blink_eyes_closed");

    @Override
    public void onInitialize() {
        ServerPlayNetworking.registerGlobalReceiver(EYES_CLOSED_PACKET_ID, ((server, player, handler, buf, responseSender) ->  {
            IEntityDataSaver serverPlayer = (IEntityDataSaver) player;
            serverPlayer.getPersistentData().putBoolean("isEyesClosed", buf.readBoolean());
        }));
    }
}
