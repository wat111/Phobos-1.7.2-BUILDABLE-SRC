/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.entity.Entity
 *  net.minecraft.network.play.client.CPacketPlayer
 *  net.minecraft.network.play.server.SPacketMoveVehicle
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package me.earth.phobos.features.modules.player;

import me.earth.phobos.event.events.JesusEvent;
import me.earth.phobos.event.events.PacketEvent;
import me.earth.phobos.event.events.UpdateWalkingPlayerEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.modules.player.Freecam;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.EntityUtil;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketMoveVehicle;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Jesus
extends Module {
    public Setting<Mode> mode = this.register(new Setting<Mode>("Mode", Mode.NORMAL));
    public Setting<Boolean> cancelVehicle = this.register(new Setting<Boolean>("NoVehicle", false));
    public Setting<EventMode> eventMode = this.register(new Setting<Object>("Jump", (Object)EventMode.PRE, v -> this.mode.getValue() == Mode.TRAMPOLINE));
    public Setting<Boolean> fall = this.register(new Setting<Object>("NoFall", Boolean.valueOf(false), v -> this.mode.getValue() == Mode.TRAMPOLINE));
    public static AxisAlignedBB offset = new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.9999, 1.0);
    private static Jesus INSTANCE = new Jesus();
    private boolean grounded = false;

    public Jesus() {
        super("Jesus", "Allows you to walk on water", Module.Category.PLAYER, true, false, false);
        INSTANCE = this;
    }

    public static Jesus getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Jesus();
        }
        return INSTANCE;
    }

    @SubscribeEvent
    public void updateWalkingPlayer(UpdateWalkingPlayerEvent event) {
        if (Jesus.fullNullCheck() || Freecam.getInstance().isOn()) {
            return;
        }
        if (!(event.getStage() != 0 || this.mode.getValue() != Mode.BOUNCE && this.mode.getValue() != Mode.VANILLA && this.mode.getValue() != Mode.NORMAL || Jesus.mc.field_71439_g.func_70093_af() || Jesus.mc.field_71439_g.field_70145_X || Jesus.mc.field_71474_y.field_74314_A.func_151470_d() || !EntityUtil.isInLiquid())) {
            Jesus.mc.field_71439_g.field_70181_x = 0.1f;
        }
        if (event.getStage() == 0 && this.mode.getValue() == Mode.TRAMPOLINE && (this.eventMode.getValue() == EventMode.ALL || this.eventMode.getValue() == EventMode.PRE)) {
            this.doTrampoline();
        } else if (event.getStage() == 1 && this.mode.getValue() == Mode.TRAMPOLINE && (this.eventMode.getValue() == EventMode.ALL || this.eventMode.getValue() == EventMode.POST)) {
            this.doTrampoline();
        }
    }

    @SubscribeEvent
    public void sendPacket(PacketEvent.Send event) {
        if (event.getPacket() instanceof CPacketPlayer && Freecam.getInstance().isOff() && (this.mode.getValue() == Mode.BOUNCE || this.mode.getValue() == Mode.NORMAL) && Jesus.mc.field_71439_g.func_184187_bx() == null && !Jesus.mc.field_71474_y.field_74314_A.func_151470_d()) {
            CPacketPlayer packet = (CPacketPlayer)event.getPacket();
            if (!EntityUtil.isInLiquid() && EntityUtil.isOnLiquid(0.05f) && EntityUtil.checkCollide() && Jesus.mc.field_71439_g.field_70173_aa % 3 == 0) {
                packet.field_149477_b -= (double)0.05f;
            }
        }
    }

    @SubscribeEvent
    public void onLiquidCollision(JesusEvent event) {
        if (Jesus.fullNullCheck() || Freecam.getInstance().isOn()) {
            return;
        }
        if (event.getStage() == 0 && (this.mode.getValue() == Mode.BOUNCE || this.mode.getValue() == Mode.VANILLA || this.mode.getValue() == Mode.NORMAL) && Jesus.mc.field_71441_e != null && Jesus.mc.field_71439_g != null && EntityUtil.checkCollide() && !(Jesus.mc.field_71439_g.field_70181_x >= (double)0.1f) && (double)event.getPos().func_177956_o() < Jesus.mc.field_71439_g.field_70163_u - (double)0.05f) {
            if (Jesus.mc.field_71439_g.func_184187_bx() != null) {
                event.setBoundingBox(new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, (double)0.95f, 1.0));
            } else {
                event.setBoundingBox(Block.field_185505_j);
            }
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onPacketReceived(PacketEvent.Receive event) {
        if (this.cancelVehicle.getValue().booleanValue() && event.getPacket() instanceof SPacketMoveVehicle) {
            event.setCanceled(true);
        }
    }

    @Override
    public String getDisplayInfo() {
        if (this.mode.getValue() == Mode.NORMAL) {
            return null;
        }
        return this.mode.currentEnumName();
    }

    private void doTrampoline() {
        if (Jesus.mc.field_71439_g.func_70093_af()) {
            return;
        }
        if (EntityUtil.isAboveLiquid((Entity)Jesus.mc.field_71439_g) && !Jesus.mc.field_71439_g.func_70093_af() && !Jesus.mc.field_71474_y.field_74314_A.field_74513_e) {
            Jesus.mc.field_71439_g.field_70181_x = 0.1;
            return;
        }
        if (Jesus.mc.field_71439_g.field_70122_E || Jesus.mc.field_71439_g.func_70617_f_()) {
            this.grounded = false;
        }
        if (Jesus.mc.field_71439_g.field_70181_x > 0.0) {
            if (Jesus.mc.field_71439_g.field_70181_x < 0.03 && this.grounded) {
                Jesus.mc.field_71439_g.field_70181_x += 0.06713;
            } else if (Jesus.mc.field_71439_g.field_70181_x <= 0.05 && this.grounded) {
                Jesus.mc.field_71439_g.field_70181_x *= 1.20000000999;
                Jesus.mc.field_71439_g.field_70181_x += 0.06;
            } else if (Jesus.mc.field_71439_g.field_70181_x <= 0.08 && this.grounded) {
                Jesus.mc.field_71439_g.field_70181_x *= 1.20000003;
                Jesus.mc.field_71439_g.field_70181_x += 0.055;
            } else if (Jesus.mc.field_71439_g.field_70181_x <= 0.112 && this.grounded) {
                Jesus.mc.field_71439_g.field_70181_x += 0.0535;
            } else if (this.grounded) {
                Jesus.mc.field_71439_g.field_70181_x *= 1.000000000002;
                Jesus.mc.field_71439_g.field_70181_x += 0.0517;
            }
        }
        if (this.grounded && Jesus.mc.field_71439_g.field_70181_x < 0.0 && Jesus.mc.field_71439_g.field_70181_x > -0.3) {
            Jesus.mc.field_71439_g.field_70181_x += 0.045835;
        }
        if (!this.fall.getValue().booleanValue()) {
            Jesus.mc.field_71439_g.field_70143_R = 0.0f;
        }
        if (!EntityUtil.checkForLiquid((Entity)Jesus.mc.field_71439_g, true)) {
            return;
        }
        if (EntityUtil.checkForLiquid((Entity)Jesus.mc.field_71439_g, true)) {
            Jesus.mc.field_71439_g.field_70181_x = 0.5;
        }
        this.grounded = true;
    }

    public static enum Mode {
        TRAMPOLINE,
        BOUNCE,
        VANILLA,
        NORMAL;

    }

    public static enum EventMode {
        PRE,
        POST,
        ALL;

    }
}

