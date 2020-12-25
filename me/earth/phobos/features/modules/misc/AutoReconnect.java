/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiDisconnected
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.client.multiplayer.GuiConnecting
 *  net.minecraft.client.multiplayer.ServerData
 *  net.minecraftforge.client.event.GuiOpenEvent
 *  net.minecraftforge.event.world.WorldEvent$Unload
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package me.earth.phobos.features.modules.misc;

import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.modules.misc.AutoLog;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.MathUtil;
import me.earth.phobos.util.Timer;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AutoReconnect
extends Module {
    private static ServerData serverData;
    private static AutoReconnect INSTANCE;
    private final Setting<Integer> delay = this.register(new Setting<Integer>("Delay", 5));

    public AutoReconnect() {
        super("AutoReconnect", "Reconnects you if you disconnect.", Module.Category.MISC, true, false, false);
        this.setInstance();
    }

    private void setInstance() {
        INSTANCE = this;
    }

    public static AutoReconnect getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new AutoReconnect();
        }
        return INSTANCE;
    }

    @SubscribeEvent
    public void sendPacket(GuiOpenEvent event) {
        if (event.getGui() instanceof GuiDisconnected) {
            this.updateLastConnectedServer();
            if (AutoLog.getInstance().isOff()) {
                GuiDisconnected disconnected = (GuiDisconnected)event.getGui();
                event.setGui((GuiScreen)new GuiDisconnectedHook(disconnected));
            }
        }
    }

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event) {
        this.updateLastConnectedServer();
    }

    public void updateLastConnectedServer() {
        ServerData data = mc.func_147104_D();
        if (data != null) {
            serverData = data;
        }
    }

    static {
        INSTANCE = new AutoReconnect();
    }

    private class GuiDisconnectedHook
    extends GuiDisconnected {
        private final Timer timer;

        public GuiDisconnectedHook(GuiDisconnected disconnected) {
            super(disconnected.field_146307_h, disconnected.field_146306_a, disconnected.field_146304_f);
            this.timer = new Timer();
            this.timer.reset();
        }

        public void func_73876_c() {
            if (this.timer.passedS(((Integer)AutoReconnect.this.delay.getValue()).intValue())) {
                this.field_146297_k.func_147108_a((GuiScreen)new GuiConnecting(this.field_146307_h, this.field_146297_k, serverData == null ? this.field_146297_k.field_71422_O : serverData));
            }
        }

        public void func_73863_a(int mouseX, int mouseY, float partialTicks) {
            super.func_73863_a(mouseX, mouseY, partialTicks);
            String s = "Reconnecting in " + MathUtil.round((double)((long)((Integer)AutoReconnect.this.delay.getValue() * 1000) - this.timer.getPassedTimeMs()) / 1000.0, 1);
            AutoReconnect.this.renderer.drawString(s, this.field_146294_l / 2 - AutoReconnect.this.renderer.getStringWidth(s) / 2, this.field_146295_m - 16, 0xFFFFFF, true);
        }
    }
}

