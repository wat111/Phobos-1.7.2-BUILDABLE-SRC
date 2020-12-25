/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.CPacketPlayer$Position
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Vec3d
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package me.earth.phobos.features.modules.movement;

import java.util.Objects;
import me.earth.phobos.event.events.UpdateWalkingPlayerEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.MathUtil;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class TPSpeed
extends Module {
    private Setting<Mode> mode = this.register(new Setting<Mode>("Mode", Mode.NORMAL));
    private Setting<Double> speed = this.register(new Setting<Double>("Speed", 0.25, 0.1, 10.0));
    private Setting<Double> fallSpeed = this.register(new Setting<Object>("FallSpeed", Double.valueOf(0.25), Double.valueOf(0.1), Double.valueOf(10.0), v -> this.mode.getValue() == Mode.STEP));
    private Setting<Boolean> turnOff = this.register(new Setting<Boolean>("Off", false));
    private Setting<Integer> tpLimit = this.register(new Setting<Object>("Limit", 2, 1, 10, v -> this.turnOff.getValue(), "Turn it off."));
    private int tps = 0;
    private double[] selectedPositions = new double[]{0.42, 0.75, 1.0};

    public TPSpeed() {
        super("TpSpeed", "Teleports you.", Module.Category.MOVEMENT, true, false, false);
    }

    @Override
    public void onEnable() {
        this.tps = 0;
    }

    @SubscribeEvent
    public void onUpdatePlayerWalking(UpdateWalkingPlayerEvent event) {
        if (event.getStage() != 0) {
            return;
        }
        if (this.mode.getValue() == Mode.NORMAL) {
            if (this.turnOff.getValue().booleanValue() && this.tps >= this.tpLimit.getValue()) {
                this.disable();
                return;
            }
            if (TPSpeed.mc.field_71439_g.field_191988_bg != 0.0f || TPSpeed.mc.field_71439_g.field_70702_br != 0.0f && TPSpeed.mc.field_71439_g.field_70122_E) {
                for (double x = 0.0625; x < this.speed.getValue(); x += 0.262) {
                    double[] dir = MathUtil.directionSpeed(x);
                    TPSpeed.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayer.Position(TPSpeed.mc.field_71439_g.field_70165_t + dir[0], TPSpeed.mc.field_71439_g.field_70163_u, TPSpeed.mc.field_71439_g.field_70161_v + dir[1], TPSpeed.mc.field_71439_g.field_70122_E));
                }
                TPSpeed.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayer.Position(TPSpeed.mc.field_71439_g.field_70165_t + TPSpeed.mc.field_71439_g.field_70159_w, 0.0, TPSpeed.mc.field_71439_g.field_70161_v + TPSpeed.mc.field_71439_g.field_70179_y, TPSpeed.mc.field_71439_g.field_70122_E));
                ++this.tps;
            }
        } else if ((TPSpeed.mc.field_71439_g.field_191988_bg != 0.0f || TPSpeed.mc.field_71439_g.field_70702_br != 0.0f) && TPSpeed.mc.field_71439_g.field_70122_E) {
            double pawnY = 0.0;
            double[] lastStep = MathUtil.directionSpeed(0.262);
            for (double x = 0.0625; x < this.speed.getValue(); x += 0.262) {
                double[] dir = MathUtil.directionSpeed(x);
                AxisAlignedBB bb = Objects.requireNonNull(TPSpeed.mc.field_71439_g.func_174813_aQ()).func_72317_d(dir[0], pawnY, dir[1]);
                while (TPSpeed.collidesHorizontally(bb)) {
                    for (double position : this.selectedPositions) {
                        TPSpeed.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayer.Position(TPSpeed.mc.field_71439_g.field_70165_t + dir[0] - lastStep[0], TPSpeed.mc.field_71439_g.field_70163_u + pawnY + position, TPSpeed.mc.field_71439_g.field_70161_v + dir[1] - lastStep[1], true));
                    }
                    bb = Objects.requireNonNull(TPSpeed.mc.field_71439_g.func_174813_aQ()).func_72317_d(dir[0], pawnY += 1.0, dir[1]);
                }
                if (!TPSpeed.mc.field_71441_e.func_72829_c(bb.func_72314_b(0.0125, 0.0, 0.0125).func_72317_d(0.0, -1.0, 0.0))) {
                    for (double i = 0.0; i <= 1.0; i += this.fallSpeed.getValue().doubleValue()) {
                        TPSpeed.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayer.Position(TPSpeed.mc.field_71439_g.field_70165_t + dir[0], TPSpeed.mc.field_71439_g.field_70163_u + pawnY - i, TPSpeed.mc.field_71439_g.field_70161_v + dir[1], true));
                    }
                    pawnY -= 1.0;
                }
                TPSpeed.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayer.Position(TPSpeed.mc.field_71439_g.field_70165_t + dir[0], TPSpeed.mc.field_71439_g.field_70163_u + pawnY, TPSpeed.mc.field_71439_g.field_70161_v + dir[1], TPSpeed.mc.field_71439_g.field_70122_E));
            }
            TPSpeed.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayer.Position(TPSpeed.mc.field_71439_g.field_70165_t + TPSpeed.mc.field_71439_g.field_70159_w, 0.0, TPSpeed.mc.field_71439_g.field_70161_v + TPSpeed.mc.field_71439_g.field_70179_y, TPSpeed.mc.field_71439_g.field_70122_E));
        }
    }

    private static boolean collidesHorizontally(AxisAlignedBB bb) {
        if (TPSpeed.mc.field_71441_e.func_184143_b(bb)) {
            Vec3d center = bb.func_189972_c();
            BlockPos blockpos = new BlockPos(center.field_72450_a, bb.field_72338_b, center.field_72449_c);
            return TPSpeed.mc.field_71441_e.func_175665_u(blockpos.func_177976_e()) || TPSpeed.mc.field_71441_e.func_175665_u(blockpos.func_177974_f()) || TPSpeed.mc.field_71441_e.func_175665_u(blockpos.func_177978_c()) || TPSpeed.mc.field_71441_e.func_175665_u(blockpos.func_177968_d()) || TPSpeed.mc.field_71441_e.func_175665_u(blockpos);
        }
        return false;
    }

    public static enum Mode {
        NORMAL,
        STEP;

    }
}

