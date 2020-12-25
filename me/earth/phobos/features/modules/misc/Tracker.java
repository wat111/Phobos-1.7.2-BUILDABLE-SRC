/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.item.EntityEnderCrystal
 *  net.minecraft.entity.item.EntityExpBottle
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Items
 *  net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock
 *  net.minecraft.network.play.server.SPacketChat
 *  net.minecraft.util.math.BlockPos
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package me.earth.phobos.features.modules.misc;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import me.earth.phobos.event.events.ConnectionEvent;
import me.earth.phobos.event.events.DeathEvent;
import me.earth.phobos.event.events.PacketEvent;
import me.earth.phobos.event.events.UpdateWalkingPlayerEvent;
import me.earth.phobos.features.command.Command;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.modules.combat.AntiTrap;
import me.earth.phobos.features.modules.combat.AutoCrystal;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.EntityUtil;
import me.earth.phobos.util.TextUtil;
import me.earth.phobos.util.Timer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityExpBottle;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Tracker
extends Module {
    public Setting<TextUtil.Color> color = this.register(new Setting<TextUtil.Color>("Color", TextUtil.Color.RED));
    public Setting<Boolean> autoEnable = this.register(new Setting<Boolean>("AutoEnable", false));
    public Setting<Boolean> autoDisable = this.register(new Setting<Boolean>("AutoDisable", true));
    private EntityPlayer trackedPlayer;
    private static Tracker instance;
    private int usedExp = 0;
    private int usedStacks = 0;
    private int usedCrystals = 0;
    private int usedCStacks = 0;
    private boolean shouldEnable = false;
    private final Timer timer = new Timer();
    private final Set<BlockPos> manuallyPlaced = new HashSet<BlockPos>();

    public Tracker() {
        super("Tracker", "Tracks players in 1v1s. Only good in duels tho!", Module.Category.MISC, true, false, true);
        instance = this;
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {
        if (!Tracker.fullNullCheck() && (this.autoEnable.getValue().booleanValue() || this.autoDisable.getValue().booleanValue()) && event.getPacket() instanceof SPacketChat) {
            SPacketChat packet = (SPacketChat)event.getPacket();
            String message = packet.func_148915_c().func_150254_d();
            if (this.autoEnable.getValue().booleanValue() && (message.contains("has accepted your duel request") || message.contains("Accepted the duel request from")) && !message.contains("<")) {
                Command.sendMessage("Tracker will enable in 5 seconds.");
                this.timer.reset();
                this.shouldEnable = true;
            } else if (this.autoDisable.getValue().booleanValue() && message.contains("has defeated") && message.contains(Tracker.mc.field_71439_g.func_70005_c_()) && !message.contains("<")) {
                this.disable();
            }
        }
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        if (!Tracker.fullNullCheck() && this.isOn() && event.getPacket() instanceof CPacketPlayerTryUseItemOnBlock) {
            CPacketPlayerTryUseItemOnBlock packet = (CPacketPlayerTryUseItemOnBlock)event.getPacket();
            if (Tracker.mc.field_71439_g.func_184586_b(packet.field_187027_c).func_77973_b() == Items.field_185158_cP && !AntiTrap.placedPos.contains((Object)packet.field_179725_b) && !AutoCrystal.placedPos.contains((Object)packet.field_179725_b)) {
                this.manuallyPlaced.add(packet.field_179725_b);
            }
        }
    }

    @SubscribeEvent
    public void onUpdateWalkingPlayer(UpdateWalkingPlayerEvent event) {
        if (this.shouldEnable && this.timer.passedS(5.0) && this.isOff()) {
            this.enable();
        }
    }

    @Override
    public void onUpdate() {
        if (this.isOff()) {
            return;
        }
        if (this.trackedPlayer == null) {
            this.trackedPlayer = EntityUtil.getClosestEnemy(1000.0);
        } else {
            if (this.usedStacks != this.usedExp / 64) {
                this.usedStacks = this.usedExp / 64;
                Command.sendMessage(TextUtil.coloredString(this.trackedPlayer.func_70005_c_() + " used: " + this.usedStacks + " Stacks of EXP.", this.color.getValue()));
            }
            if (this.usedCStacks != this.usedCrystals / 64) {
                this.usedCStacks = this.usedCrystals / 64;
                Command.sendMessage(TextUtil.coloredString(this.trackedPlayer.func_70005_c_() + " used: " + this.usedCStacks + " Stacks of Crystals.", this.color.getValue()));
            }
        }
    }

    public void onSpawnEntity(Entity entity) {
        if (this.isOff()) {
            return;
        }
        if (entity instanceof EntityExpBottle && Objects.equals((Object)Tracker.mc.field_71441_e.func_72890_a(entity, 3.0), (Object)this.trackedPlayer)) {
            ++this.usedExp;
        }
        if (entity instanceof EntityEnderCrystal) {
            if (AntiTrap.placedPos.contains((Object)entity.func_180425_c().func_177977_b())) {
                AntiTrap.placedPos.remove((Object)entity.func_180425_c().func_177977_b());
            } else if (this.manuallyPlaced.contains((Object)entity.func_180425_c().func_177977_b())) {
                this.manuallyPlaced.remove((Object)entity.func_180425_c().func_177977_b());
            } else if (!AutoCrystal.placedPos.contains((Object)entity.func_180425_c().func_177977_b())) {
                ++this.usedCrystals;
            }
        }
    }

    @SubscribeEvent
    public void onConnection(ConnectionEvent event) {
        if (this.isOff() || event.getStage() != 1) {
            return;
        }
        String name = event.getName();
        if (this.trackedPlayer != null && name != null && name.equals(this.trackedPlayer.func_70005_c_()) && this.autoDisable.getValue().booleanValue()) {
            Command.sendMessage(name + " logged, Tracker disableing.");
            this.disable();
        }
    }

    @Override
    public void onToggle() {
        this.manuallyPlaced.clear();
        AntiTrap.placedPos.clear();
        this.shouldEnable = false;
        this.trackedPlayer = null;
        this.usedExp = 0;
        this.usedStacks = 0;
        this.usedCrystals = 0;
        this.usedCStacks = 0;
    }

    @Override
    public void onLogout() {
        if (this.autoDisable.getValue().booleanValue()) {
            this.disable();
        }
    }

    @SubscribeEvent
    public void onDeath(DeathEvent event) {
        if (this.isOn() && (event.player.equals((Object)this.trackedPlayer) || event.player.equals((Object)Tracker.mc.field_71439_g))) {
            this.usedExp = 0;
            this.usedStacks = 0;
            this.usedCrystals = 0;
            this.usedCStacks = 0;
            if (this.autoDisable.getValue().booleanValue()) {
                this.disable();
            }
        }
    }

    @Override
    public String getDisplayInfo() {
        if (this.trackedPlayer != null) {
            return this.trackedPlayer.func_70005_c_();
        }
        return null;
    }

    public static Tracker getInstance() {
        if (instance == null) {
            instance = new Tracker();
        }
        return instance;
    }
}

