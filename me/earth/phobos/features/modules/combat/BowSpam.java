/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Items
 *  net.minecraft.item.ItemBow
 *  net.minecraft.item.ItemEndCrystal
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.CPacketPlayer$Position
 *  net.minecraft.network.play.client.CPacketPlayer$PositionRotation
 *  net.minecraft.network.play.client.CPacketPlayerDigging
 *  net.minecraft.network.play.client.CPacketPlayerDigging$Action
 *  net.minecraft.network.play.client.CPacketPlayerTryUseItem
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Vec3d
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 *  org.lwjgl.input.Mouse
 */
package me.earth.phobos.features.modules.combat;

import me.earth.phobos.Phobos;
import me.earth.phobos.event.events.PacketEvent;
import me.earth.phobos.event.events.UpdateWalkingPlayerEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.modules.combat.AutoCrystal;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.EntityUtil;
import me.earth.phobos.util.InventoryUtil;
import me.earth.phobos.util.MathUtil;
import me.earth.phobos.util.Timer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemEndCrystal;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Mouse;

public class BowSpam
extends Module {
    public Setting<Mode> mode = this.register(new Setting<Mode>("Mode", Mode.FAST));
    public Setting<Boolean> bowbomb = this.register(new Setting<Object>("BowBomb", Boolean.valueOf(false), v -> this.mode.getValue() != Mode.BOWBOMB));
    public Setting<Boolean> allowOffhand = this.register(new Setting<Object>("Offhand", Boolean.valueOf(true), v -> this.mode.getValue() != Mode.AUTORELEASE));
    public Setting<Integer> ticks = this.register(new Setting<Object>("Ticks", 3, 0, 20, v -> this.mode.getValue() == Mode.BOWBOMB || this.mode.getValue() == Mode.FAST, "Speed"));
    public Setting<Integer> delay = this.register(new Setting<Object>("Delay", 50, 0, 500, v -> this.mode.getValue() == Mode.AUTORELEASE, "Speed"));
    public Setting<Boolean> tpsSync = this.register(new Setting<Boolean>("TpsSync", true));
    public Setting<Boolean> autoSwitch = this.register(new Setting<Boolean>("AutoSwitch", false));
    public Setting<Boolean> onlyWhenSave = this.register(new Setting<Object>("OnlyWhenSave", Boolean.valueOf(true), v -> this.autoSwitch.getValue()));
    public Setting<Target> targetMode = this.register(new Setting<Object>("Target", (Object)Target.LOWEST, v -> this.autoSwitch.getValue()));
    public Setting<Float> range = this.register(new Setting<Object>("Range", Float.valueOf(3.0f), Float.valueOf(0.0f), Float.valueOf(6.0f), v -> this.autoSwitch.getValue(), "Range of the target"));
    public Setting<Float> health = this.register(new Setting<Object>("Lethal", Float.valueOf(6.0f), Float.valueOf(0.1f), Float.valueOf(36.0f), v -> this.autoSwitch.getValue(), "When should it switch?"));
    public Setting<Float> ownHealth = this.register(new Setting<Object>("OwnHealth", Float.valueOf(20.0f), Float.valueOf(0.1f), Float.valueOf(36.0f), v -> this.autoSwitch.getValue(), "Own Health."));
    private final Timer timer = new Timer();
    private boolean offhand = false;
    private boolean switched = false;
    private int lastHotbarSlot = -1;

    public BowSpam() {
        super("BowSpam", "Spams your bow", Module.Category.COMBAT, true, false, false);
    }

    @Override
    public void onEnable() {
        this.lastHotbarSlot = BowSpam.mc.field_71439_g.field_71071_by.field_70461_c;
    }

    @SubscribeEvent
    public void onUpdateWalkingPlayer(UpdateWalkingPlayerEvent event) {
        if (event.getStage() != 0) {
            return;
        }
        if (this.autoSwitch.getValue().booleanValue() && InventoryUtil.findHotbarBlock(ItemBow.class) != -1 && this.ownHealth.getValue().floatValue() <= EntityUtil.getHealth((Entity)BowSpam.mc.field_71439_g) && (!this.onlyWhenSave.getValue().booleanValue() || EntityUtil.isSafe((Entity)BowSpam.mc.field_71439_g))) {
            AutoCrystal crystal;
            EntityPlayer target = this.getTarget();
            if (!(target == null || (crystal = Phobos.moduleManager.getModuleByClass(AutoCrystal.class)).isOn() && InventoryUtil.holdingItem(ItemEndCrystal.class))) {
                Vec3d pos = target.func_174791_d();
                double xPos = pos.field_72450_a;
                double yPos = pos.field_72448_b;
                double zPos = pos.field_72449_c;
                if (BowSpam.mc.field_71439_g.func_70685_l((Entity)target)) {
                    yPos += (double)target.eyeHeight;
                } else if (EntityUtil.canEntityFeetBeSeen((Entity)target)) {
                    yPos += 0.1;
                } else {
                    return;
                }
                if (!(BowSpam.mc.field_71439_g.func_184614_ca().func_77973_b() instanceof ItemBow)) {
                    this.lastHotbarSlot = BowSpam.mc.field_71439_g.field_71071_by.field_70461_c;
                    InventoryUtil.switchToHotbarSlot(ItemBow.class, false);
                    BowSpam.mc.field_71474_y.field_74313_G.field_74513_e = true;
                    this.switched = true;
                }
                Phobos.rotationManager.lookAtVec3d(xPos, yPos, zPos);
                if (BowSpam.mc.field_71439_g.func_184614_ca().func_77973_b() instanceof ItemBow) {
                    this.switched = true;
                }
            }
        } else if (event.getStage() == 0 && this.switched && this.lastHotbarSlot != -1) {
            InventoryUtil.switchToHotbarSlot(this.lastHotbarSlot, false);
            BowSpam.mc.field_71474_y.field_74313_G.field_74513_e = Mouse.isButtonDown((int)1);
            this.switched = false;
        } else {
            BowSpam.mc.field_71474_y.field_74313_G.field_74513_e = Mouse.isButtonDown((int)1);
        }
        if (this.mode.getValue() == Mode.FAST && (this.offhand || BowSpam.mc.field_71439_g.field_71071_by.func_70448_g().func_77973_b() instanceof ItemBow) && BowSpam.mc.field_71439_g.func_184587_cr()) {
            float f = BowSpam.mc.field_71439_g.func_184612_cw();
            float f2 = this.ticks.getValue().intValue();
            float f3 = this.tpsSync.getValue() != false ? Phobos.serverManager.getTpsFactor() : 1.0f;
            if (f >= f2 * f3) {
                BowSpam.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.field_177992_a, BowSpam.mc.field_71439_g.func_174811_aO()));
                BowSpam.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayerTryUseItem(this.offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND));
                BowSpam.mc.field_71439_g.func_184597_cx();
            }
        }
    }

    @Override
    public void onUpdate() {
        this.offhand = BowSpam.mc.field_71439_g.func_184592_cb().func_77973_b() == Items.field_151031_f && this.allowOffhand.getValue() != false;
        switch (this.mode.getValue()) {
            case AUTORELEASE: {
                if (!this.offhand && !(BowSpam.mc.field_71439_g.field_71071_by.func_70448_g().func_77973_b() instanceof ItemBow) || !this.timer.passedMs((int)((float)this.delay.getValue().intValue() * (this.tpsSync.getValue() != false ? Phobos.serverManager.getTpsFactor() : 1.0f)))) break;
                BowSpam.mc.field_71442_b.func_78766_c((EntityPlayer)BowSpam.mc.field_71439_g);
                this.timer.reset();
                break;
            }
            case BOWBOMB: {
                if (!this.offhand && !(BowSpam.mc.field_71439_g.field_71071_by.func_70448_g().func_77973_b() instanceof ItemBow) || !BowSpam.mc.field_71439_g.func_184587_cr()) break;
                float f = BowSpam.mc.field_71439_g.func_184612_cw();
                float f2 = this.ticks.getValue().intValue();
                float f3 = this.tpsSync.getValue() != false ? Phobos.serverManager.getTpsFactor() : 1.0f;
                if (!(f >= f2 * f3)) break;
                BowSpam.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.field_177992_a, BowSpam.mc.field_71439_g.func_174811_aO()));
                BowSpam.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayer.PositionRotation(BowSpam.mc.field_71439_g.field_70165_t, BowSpam.mc.field_71439_g.field_70163_u - 0.0624, BowSpam.mc.field_71439_g.field_70161_v, BowSpam.mc.field_71439_g.field_70177_z, BowSpam.mc.field_71439_g.field_70125_A, false));
                BowSpam.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayer.PositionRotation(BowSpam.mc.field_71439_g.field_70165_t, BowSpam.mc.field_71439_g.field_70163_u - 999.0, BowSpam.mc.field_71439_g.field_70161_v, BowSpam.mc.field_71439_g.field_70177_z, BowSpam.mc.field_71439_g.field_70125_A, true));
                BowSpam.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayerTryUseItem(this.offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND));
                BowSpam.mc.field_71439_g.func_184597_cx();
            }
        }
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        CPacketPlayerDigging packet;
        if (event.getStage() == 0 && this.bowbomb.getValue().booleanValue() && this.mode.getValue() != Mode.BOWBOMB && event.getPacket() instanceof CPacketPlayerDigging && (packet = (CPacketPlayerDigging)event.getPacket()).func_180762_c() == CPacketPlayerDigging.Action.RELEASE_USE_ITEM && (this.offhand || BowSpam.mc.field_71439_g.field_71071_by.func_70448_g().func_77973_b() instanceof ItemBow) && BowSpam.mc.field_71439_g.func_184612_cw() >= 20 && !BowSpam.mc.field_71439_g.field_70122_E) {
            BowSpam.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayer.Position(BowSpam.mc.field_71439_g.field_70165_t, BowSpam.mc.field_71439_g.field_70163_u - (double)0.1f, BowSpam.mc.field_71439_g.field_70161_v, false));
            BowSpam.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayer.Position(BowSpam.mc.field_71439_g.field_70165_t, BowSpam.mc.field_71439_g.field_70163_u - 10000.0, BowSpam.mc.field_71439_g.field_70161_v, true));
        }
    }

    private EntityPlayer getTarget() {
        double maxHealth = 36.0;
        EntityPlayer target = null;
        for (EntityPlayer player : BowSpam.mc.field_71441_e.field_73010_i) {
            if (player == null || EntityUtil.isDead((Entity)player) || EntityUtil.getHealth((Entity)player) > this.health.getValue().floatValue() || player.equals((Object)BowSpam.mc.field_71439_g) || Phobos.friendManager.isFriend(player) || BowSpam.mc.field_71439_g.func_70068_e((Entity)player) > MathUtil.square(this.range.getValue().floatValue()) || !BowSpam.mc.field_71439_g.func_70685_l((Entity)player) && !EntityUtil.canEntityFeetBeSeen((Entity)player)) continue;
            if (target == null) {
                target = player;
                maxHealth = EntityUtil.getHealth((Entity)player);
            }
            if (this.targetMode.getValue() == Target.CLOSEST && BowSpam.mc.field_71439_g.func_70068_e((Entity)player) < BowSpam.mc.field_71439_g.func_70068_e((Entity)target)) {
                target = player;
                maxHealth = EntityUtil.getHealth((Entity)player);
            }
            if (this.targetMode.getValue() != Target.LOWEST || !((double)EntityUtil.getHealth((Entity)player) < maxHealth)) continue;
            target = player;
            maxHealth = EntityUtil.getHealth((Entity)player);
        }
        return target;
    }

    public static enum Mode {
        FAST,
        AUTORELEASE,
        BOWBOMB;

    }

    public static enum Target {
        CLOSEST,
        LOWEST;

    }
}

