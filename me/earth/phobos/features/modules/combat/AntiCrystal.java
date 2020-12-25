/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.item.EntityEnderCrystal
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Items
 *  net.minecraft.network.play.client.CPacketPlayer
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Vec3d
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package me.earth.phobos.features.modules.combat;

import java.util.ArrayList;
import java.util.List;
import me.earth.phobos.event.events.PacketEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.BlockUtil;
import me.earth.phobos.util.DamageUtil;
import me.earth.phobos.util.EntityUtil;
import me.earth.phobos.util.MathUtil;
import me.earth.phobos.util.Timer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AntiCrystal
extends Module {
    public Setting<Float> range = this.register(new Setting<Float>("Range", Float.valueOf(6.0f), Float.valueOf(0.0f), Float.valueOf(10.0f)));
    public Setting<Float> wallsRange = this.register(new Setting<Float>("WallsRange", Float.valueOf(3.5f), Float.valueOf(0.0f), Float.valueOf(10.0f)));
    public Setting<Float> minDmg = this.register(new Setting<Float>("MinDmg", Float.valueOf(6.0f), Float.valueOf(0.0f), Float.valueOf(100.0f)));
    public Setting<Float> selfDmg = this.register(new Setting<Float>("SelfDmg", Float.valueOf(2.0f), Float.valueOf(0.0f), Float.valueOf(10.0f)));
    public Setting<Integer> placeDelay = this.register(new Setting<Integer>("PlaceDelay", 0, 0, 500));
    public Setting<Integer> breakDelay = this.register(new Setting<Integer>("BreakDelay", 0, 0, 500));
    public Setting<Integer> checkDelay = this.register(new Setting<Integer>("CheckDelay", 0, 0, 500));
    public Setting<Integer> wasteAmount = this.register(new Setting<Integer>("WasteAmount", 1, 1, 5));
    public Setting<Boolean> switcher = this.register(new Setting<Boolean>("Switch", true));
    public Setting<Boolean> rotate = this.register(new Setting<Boolean>("Rotate", true));
    public Setting<Boolean> packet = this.register(new Setting<Boolean>("Packet", true));
    public Setting<Integer> rotations = this.register(new Setting<Integer>("Spoofs", 1, 1, 20));
    private float yaw = 0.0f;
    private float pitch = 0.0f;
    private boolean rotating = false;
    private int rotationPacketsSpoofed = 0;
    private final List<BlockPos> targets = new ArrayList<BlockPos>();
    private Entity breakTarget;
    private final Timer timer = new Timer();
    private final Timer breakTimer = new Timer();
    private final Timer checkTimer = new Timer();

    public AntiCrystal() {
        super("AntiCrystal", "Hacker shit", Module.Category.COMBAT, true, false, false);
    }

    @Override
    public void onToggle() {
        this.rotating = false;
    }

    private Entity getDeadlyCrystal() {
        Entity bestcrystal = null;
        float highestDamage = 0.0f;
        for (Entity crystal : AntiCrystal.mc.field_71441_e.field_72996_f) {
            float damage;
            if (!(crystal instanceof EntityEnderCrystal) || AntiCrystal.mc.field_71439_g.func_70068_e(crystal) > 169.0 || (damage = DamageUtil.calculateDamage(crystal, (Entity)AntiCrystal.mc.field_71439_g)) < this.minDmg.getValue().floatValue()) continue;
            if (bestcrystal == null) {
                bestcrystal = crystal;
                highestDamage = damage;
                continue;
            }
            if (!(damage > highestDamage)) continue;
            bestcrystal = crystal;
            highestDamage = damage;
        }
        return bestcrystal;
    }

    private int getSafetyCrystals(Entity deadlyCrystal) {
        int count = 0;
        for (Entity entity : AntiCrystal.mc.field_71441_e.field_72996_f) {
            float damage;
            if (entity instanceof EntityEnderCrystal || (damage = DamageUtil.calculateDamage(entity, (Entity)AntiCrystal.mc.field_71439_g)) > 2.0f || deadlyCrystal.func_70068_e(entity) > 144.0) continue;
            ++count;
        }
        return count;
    }

    private BlockPos getPlaceTarget(Entity deadlyCrystal) {
        BlockPos closestPos = null;
        float smallestDamage = 10.0f;
        for (BlockPos pos : BlockUtil.possiblePlacePositions(this.range.getValue().floatValue())) {
            float damage = DamageUtil.calculateDamage(pos, (Entity)AntiCrystal.mc.field_71439_g);
            if (damage > 2.0f || deadlyCrystal.func_174818_b(pos) > 144.0 || AntiCrystal.mc.field_71439_g.func_174818_b(pos) >= MathUtil.square(this.wallsRange.getValue().floatValue()) && BlockUtil.rayTracePlaceCheck(pos, true, 1.0f)) continue;
            if (closestPos == null) {
                smallestDamage = damage;
                closestPos = pos;
                continue;
            }
            if (!(damage < smallestDamage) && (damage != smallestDamage || !(AntiCrystal.mc.field_71439_g.func_174818_b(pos) < AntiCrystal.mc.field_71439_g.func_174818_b(closestPos)))) continue;
            smallestDamage = damage;
            closestPos = pos;
        }
        return closestPos;
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        if (event.getStage() == 0 && this.rotate.getValue().booleanValue() && this.rotating) {
            if (event.getPacket() instanceof CPacketPlayer) {
                CPacketPlayer packet = (CPacketPlayer)event.getPacket();
                packet.field_149476_e = this.yaw;
                packet.field_149473_f = this.pitch;
            }
            ++this.rotationPacketsSpoofed;
            if (this.rotationPacketsSpoofed >= this.rotations.getValue()) {
                this.rotating = false;
                this.rotationPacketsSpoofed = 0;
            }
        }
    }

    @Override
    public void onTick() {
        if (!AntiCrystal.fullNullCheck() && this.checkTimer.passedMs(this.checkDelay.getValue().intValue())) {
            Entity deadlyCrystal = this.getDeadlyCrystal();
            if (deadlyCrystal != null) {
                BlockPos placeTarget = this.getPlaceTarget(deadlyCrystal);
                if (placeTarget != null) {
                    this.targets.add(placeTarget);
                }
                this.placeCrystal(deadlyCrystal);
                this.breakTarget = this.getBreakTarget(deadlyCrystal);
                this.breakCrystal();
            }
            this.checkTimer.reset();
        }
    }

    public Entity getBreakTarget(Entity deadlyCrystal) {
        Entity smallestCrystal = null;
        float smallestDamage = 10.0f;
        for (Entity entity : AntiCrystal.mc.field_71441_e.field_72996_f) {
            float damage;
            if (!(entity instanceof EntityEnderCrystal) || (damage = DamageUtil.calculateDamage(entity, (Entity)AntiCrystal.mc.field_71439_g)) > this.selfDmg.getValue().floatValue() || entity.func_70068_e(deadlyCrystal) > 144.0 || AntiCrystal.mc.field_71439_g.func_70068_e(entity) > MathUtil.square(this.wallsRange.getValue().floatValue()) && EntityUtil.rayTraceHitCheck(entity, true)) continue;
            if (smallestCrystal == null) {
                smallestCrystal = entity;
                smallestDamage = damage;
                continue;
            }
            if (!(damage < smallestDamage) && (smallestDamage != damage || !(AntiCrystal.mc.field_71439_g.func_70068_e(entity) < AntiCrystal.mc.field_71439_g.func_70068_e(smallestCrystal)))) continue;
            smallestCrystal = entity;
            smallestDamage = damage;
        }
        return smallestCrystal;
    }

    private void placeCrystal(Entity deadlyCrystal) {
        boolean offhand;
        boolean bl = offhand = AntiCrystal.mc.field_71439_g.func_184592_cb().func_77973_b() == Items.field_185158_cP;
        if (this.timer.passedMs(this.placeDelay.getValue().intValue()) && (this.switcher.getValue().booleanValue() || AntiCrystal.mc.field_71439_g.func_184614_ca().func_77973_b() == Items.field_185158_cP || offhand) && !this.targets.isEmpty() && this.getSafetyCrystals(deadlyCrystal) <= this.wasteAmount.getValue()) {
            if (this.switcher.getValue().booleanValue() && AntiCrystal.mc.field_71439_g.func_184614_ca().func_77973_b() != Items.field_185158_cP && !offhand) {
                this.doSwitch();
            }
            this.rotateToPos(this.targets.get(this.targets.size() - 1));
            BlockUtil.placeCrystalOnBlock(this.targets.get(this.targets.size() - 1), offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, true, true);
            this.timer.reset();
        }
    }

    private void doSwitch() {
        int crystalSlot;
        int n = crystalSlot = AntiCrystal.mc.field_71439_g.func_184614_ca().func_77973_b() == Items.field_185158_cP ? AntiCrystal.mc.field_71439_g.field_71071_by.field_70461_c : -1;
        if (crystalSlot == -1) {
            for (int l = 0; l < 9; ++l) {
                if (AntiCrystal.mc.field_71439_g.field_71071_by.func_70301_a(l).func_77973_b() != Items.field_185158_cP) continue;
                crystalSlot = l;
                break;
            }
        }
        if (crystalSlot != -1) {
            AntiCrystal.mc.field_71439_g.field_71071_by.field_70461_c = crystalSlot;
        }
    }

    private void breakCrystal() {
        if (this.breakTimer.passedMs(this.breakDelay.getValue().intValue()) && this.breakTarget != null && DamageUtil.canBreakWeakness((EntityPlayer)AntiCrystal.mc.field_71439_g)) {
            this.rotateTo(this.breakTarget);
            EntityUtil.attackEntity(this.breakTarget, this.packet.getValue(), true);
            this.breakTimer.reset();
            this.targets.clear();
        }
    }

    private void rotateTo(Entity entity) {
        if (this.rotate.getValue().booleanValue()) {
            float[] angle = MathUtil.calcAngle(AntiCrystal.mc.field_71439_g.func_174824_e(mc.func_184121_ak()), entity.func_174791_d());
            this.yaw = angle[0];
            this.pitch = angle[1];
            this.rotating = true;
        }
    }

    private void rotateToPos(BlockPos pos) {
        if (this.rotate.getValue().booleanValue()) {
            float[] angle = MathUtil.calcAngle(AntiCrystal.mc.field_71439_g.func_174824_e(mc.func_184121_ak()), new Vec3d((double)((float)pos.func_177958_n() + 0.5f), (double)((float)pos.func_177956_o() - 0.5f), (double)((float)pos.func_177952_p() + 0.5f)));
            this.yaw = angle[0];
            this.pitch = angle[1];
            this.rotating = true;
        }
    }
}

