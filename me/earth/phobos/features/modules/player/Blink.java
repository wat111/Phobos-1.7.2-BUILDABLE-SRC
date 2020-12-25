/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.entity.EntityOtherPlayerMP
 *  net.minecraft.entity.Entity
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.CPacketChatMessage
 *  net.minecraft.network.play.client.CPacketClientStatus
 *  net.minecraft.network.play.client.CPacketConfirmTeleport
 *  net.minecraft.network.play.client.CPacketKeepAlive
 *  net.minecraft.network.play.client.CPacketPlayer
 *  net.minecraft.network.play.client.CPacketTabComplete
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package me.earth.phobos.features.modules.player;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import me.earth.phobos.event.events.PacketEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.MathUtil;
import me.earth.phobos.util.Timer;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.network.play.client.CPacketClientStatus;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.play.client.CPacketKeepAlive;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketTabComplete;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Blink
extends Module {
    public Setting<Boolean> cPacketPlayer = this.register(new Setting<Boolean>("CPacketPlayer", true));
    public Setting<Mode> autoOff = this.register(new Setting<Mode>("AutoOff", Mode.MANUAL));
    public Setting<Integer> timeLimit = this.register(new Setting<Object>("Time", Integer.valueOf(20), Integer.valueOf(1), Integer.valueOf(500), v -> this.autoOff.getValue() == Mode.TIME));
    public Setting<Integer> packetLimit = this.register(new Setting<Object>("Packets", Integer.valueOf(20), Integer.valueOf(1), Integer.valueOf(500), v -> this.autoOff.getValue() == Mode.PACKETS));
    public Setting<Float> distance = this.register(new Setting<Object>("Distance", Float.valueOf(10.0f), Float.valueOf(1.0f), Float.valueOf(100.0f), v -> this.autoOff.getValue() == Mode.DISTANCE));
    private Timer timer = new Timer();
    private Queue<Packet<?>> packets = new ConcurrentLinkedQueue();
    private EntityOtherPlayerMP entity;
    private int packetsCanceled = 0;
    private BlockPos startPos = null;
    private static Blink INSTANCE = new Blink();

    public Blink() {
        super("Blink", "Fakelag.", Module.Category.PLAYER, true, false, false);
        this.setInstance();
    }

    private void setInstance() {
        INSTANCE = this;
    }

    public static Blink getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Blink();
        }
        return INSTANCE;
    }

    @Override
    public void onEnable() {
        if (!Blink.fullNullCheck()) {
            this.entity = new EntityOtherPlayerMP((World)Blink.mc.field_71441_e, Blink.mc.field_71449_j.func_148256_e());
            this.entity.func_82149_j((Entity)Blink.mc.field_71439_g);
            this.entity.field_70177_z = Blink.mc.field_71439_g.field_70177_z;
            this.entity.field_70759_as = Blink.mc.field_71439_g.field_70759_as;
            this.entity.field_71071_by.func_70455_b(Blink.mc.field_71439_g.field_71071_by);
            Blink.mc.field_71441_e.func_73027_a(6942069, (Entity)this.entity);
            this.startPos = Blink.mc.field_71439_g.func_180425_c();
        } else {
            this.disable();
        }
        this.packetsCanceled = 0;
        this.timer.reset();
    }

    @Override
    public void onUpdate() {
        if (Blink.nullCheck() || this.autoOff.getValue() == Mode.TIME && this.timer.passedS(this.timeLimit.getValue().intValue()) || this.autoOff.getValue() == Mode.DISTANCE && this.startPos != null && Blink.mc.field_71439_g.func_174818_b(this.startPos) >= MathUtil.square(this.distance.getValue().floatValue()) || this.autoOff.getValue() == Mode.PACKETS && this.packetsCanceled >= this.packetLimit.getValue()) {
            this.disable();
        }
    }

    @Override
    public void onLogout() {
        if (this.isOn()) {
            this.disable();
        }
    }

    @SubscribeEvent
    public void onSendPacket(PacketEvent.Send event) {
        if (event.getStage() == 0 && Blink.mc.field_71441_e != null && !mc.func_71356_B()) {
            Object packet = event.getPacket();
            if (this.cPacketPlayer.getValue().booleanValue() && packet instanceof CPacketPlayer) {
                event.setCanceled(true);
                this.packets.add((Packet<?>)packet);
                ++this.packetsCanceled;
            }
            if (!this.cPacketPlayer.getValue().booleanValue()) {
                if (packet instanceof CPacketChatMessage || packet instanceof CPacketConfirmTeleport || packet instanceof CPacketKeepAlive || packet instanceof CPacketTabComplete || packet instanceof CPacketClientStatus) {
                    return;
                }
                this.packets.add((Packet<?>)packet);
                event.setCanceled(true);
                ++this.packetsCanceled;
            }
        }
    }

    @Override
    public void onDisable() {
        if (!Blink.fullNullCheck()) {
            Blink.mc.field_71441_e.func_72900_e((Entity)this.entity);
            while (!this.packets.isEmpty()) {
                Blink.mc.field_71439_g.field_71174_a.func_147297_a(this.packets.poll());
            }
        }
        this.startPos = null;
    }

    public static enum Mode {
        MANUAL,
        TIME,
        DISTANCE,
        PACKETS;

    }
}

