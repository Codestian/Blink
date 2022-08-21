package com.codestian.blink;

import com.codestian.blink.config.ModConfigs;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.PacketByteBuf;
import org.lwjgl.glfw.GLFW;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static com.codestian.blink.Blink.EYES_CLOSED_PACKET_ID;
import static com.codestian.blink.mixin.drawableHelperInvoker.fillGradient;

public class BlinkClient implements ClientModInitializer {

    public static final AtomicBoolean enableBlinking = new AtomicBoolean(false);
    public static final AtomicBoolean enableBlinkingTimer = new AtomicBoolean(true);
    private static final AtomicInteger value = new AtomicInteger(0);
    private static final AtomicBoolean isClosing = new AtomicBoolean(true);
    private static final int color = 0xff000000;

    private static KeyBinding keyBinding;
    private static MatrixStack matrixStack;
    private static int width = 0;
    private static int height = 0;

    @Override
    public void onInitializeClient() {

        //  Setup configuration.
        ModConfigs.registerConfigs();

        //  Setup keybinding setting.
        keyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "Open/close eyes",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_R,
                "Blink"
        ));

        //  Renders the blinking animation.
        HudRenderCallback.EVENT.register((ms, tickDelta) -> {
            matrixStack = ms;
            width = MinecraftClient.getInstance().getWindow().getScaledWidth();
            height = MinecraftClient.getInstance().getWindow().getScaledHeight();

            if (enableBlinking.get()) {
                //  Auto blinking is executed based on the timer in BlinkTimerMixin.
                autoBlink();
            } else {
                //  Manual blinking is executed based on whether the keybinding is being held.
                manualBlink();
            }
        });
    }

    private static void manualBlink() {
        if (keyBinding.isPressed()) {
            enableBlinkingTimer.set(false);
            closeEye();
        } else {
            enableBlinkingTimer.set(true);
            openEye();
        }
        setIsEyesClosed();
    }

    private static void autoBlink() {
        if (isClosing.get()) {
            if (closeEye()) {
                isClosing.set(false);
            }
        } else {
            if (openEye()) {
                isClosing.set(true);
                enableBlinking.set(false);
            }
        }
        setIsEyesClosed();
    }

    private static boolean closeEye() {
        blinkAnimation();
        if (value.get() < ModConfigs.SPEED) {
            value.getAndIncrement();
            return false;
        } else {
            return true;
        }
    }

    private static boolean openEye() {
        blinkAnimation();
        if (value.get() > 0) {
            value.getAndDecrement();
            return false;
        } else {
            return true;
        }
    }

    private static void setIsEyesClosed() {
        if (value.get() * height > ModConfigs.SPEED / 2) {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeBoolean(true);
            ClientPlayNetworking.send(EYES_CLOSED_PACKET_ID, buf);
        } else {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeBoolean(false);
            ClientPlayNetworking.send(EYES_CLOSED_PACKET_ID, buf);
        }
    }

    private static void blinkAnimation() {
        DrawableHelper.fill(matrixStack, 0, 0, width, (value.get() * height) / ModConfigs.SPEED, color);
        fillGradient(matrixStack, 0, (value.get() * height) / ModConfigs.SPEED, width, ((value.get() * height) / ModConfigs.SPEED) * 2, color, 0x00000000, 0);
    }
}
