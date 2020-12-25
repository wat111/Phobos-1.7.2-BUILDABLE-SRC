/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.CPacketPlayer$Position
 *  net.minecraft.util.math.RayTraceResult
 *  net.minecraft.util.math.RayTraceResult$Type
 *  net.minecraft.util.math.Vec3d
 */
package me.earth.phobos.features.modules.movement;

import me.earth.phobos.Phobos;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.modules.movement.Flight;
import me.earth.phobos.features.modules.movement.Phase;
import me.earth.phobos.features.setting.Setting;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

public class Static
extends Module {
    private final Setting<Mode> mode = this.register(new Setting<Mode>("Mode", Mode.ROOF));
    private final Setting<Boolean> disabler = this.register(new Setting<Object>("Disable", Boolean.valueOf(true), v -> this.mode.getValue() == Mode.ROOF));
    private final Setting<Boolean> ySpeed = this.register(new Setting<Object>("YSpeed", Boolean.valueOf(false), v -> this.mode.getValue() == Mode.STATIC));
    private final Setting<Float> speed = this.register(new Setting<Object>("Speed", Float.valueOf(0.1f), Float.valueOf(0.0f), Float.valueOf(10.0f), v -> this.ySpeed.getValue() != false && this.mode.getValue() == Mode.STATIC));
    private final Setting<Float> height = this.register(new Setting<Object>("Height", Float.valueOf(3.0f), Float.valueOf(0.0f), Float.valueOf(256.0f), v -> this.mode.getValue() == Mode.NOVOID));

    public Static() {
        super("Static", "Stops any movement. Glitches you up.", Module.Category.MOVEMENT, false, false, false);
    }

    @Override
    public void onUpdate() {
        if (Static.fullNullCheck()) {
            return;
        }
        switch (this.mode.getValue()) {
            case STATIC: {
                Static.mc.field_71439_g.field_71075_bZ.field_75100_b = false;
                Static.mc.field_71439_g.field_70159_w = 0.0;
                Static.mc.field_71439_g.field_70181_x = 0.0;
                Static.mc.field_71439_g.field_70179_y = 0.0;
                if (!this.ySpeed.getValue().booleanValue()) break;
                Static.mc.field_71439_g.field_70747_aH = this.speed.getValue().floatValue();
                if (Static.mc.field_71474_y.field_74314_A.func_151470_d()) {
                    Static.mc.field_71439_g.field_70181_x += (double)this.speed.getValue().floatValue();
                }
                if (!Static.mc.field_71474_y.field_74311_E.func_151470_d()) break;
                Static.mc.field_71439_g.field_70181_x -= (double)this.speed.getValue().floatValue();
                break;
            }
            case ROOF: {
                Static.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayer.Position(Static.mc.field_71439_g.field_70165_t, 10000.0, Static.mc.field_71439_g.field_70161_v, Static.mc.field_71439_g.field_70122_E));
                if (!this.disabler.getValue().booleanValue()) break;
                this.disable();
                break;
            }
            case NOVOID: {
                if (Static.mc.field_71439_g.field_70145_X || !(Static.mc.field_71439_g.field_70163_u <= (double)this.height.getValue().floatValue())) break;
                RayTraceResult trace = Static.mc.field_71441_e.func_147447_a(Static.mc.field_71439_g.func_174791_d(), new Vec3d(Static.mc.field_71439_g.field_70165_t, 0.0, Static.mc.field_71439_g.field_70161_v), false, false, false);
                if (trace != null && trace.field_72313_a == RayTraceResult.Type.BLOCK) {
                    return;
                }
                if (Phobos.moduleManager.isModuleEnabled(Phase.class) || Phobos.moduleManager.isModuleEnabled(Flight.class)) {
                    return;
                }
                Static.mc.field_71439_g.func_70016_h(0.0, 0.0, 0.0);
                if (Static.mc.field_71439_g.func_184187_bx() == null) break;
                Static.mc.field_71439_g.func_184187_bx().func_70016_h(0.0, 0.0, 0.0);
            }
        }
    }

    @Override
    public String getDisplayInfo() {
        if (this.mode.getValue() == Mode.ROOF) {
            return "Roof";
        }
        if (this.mode.getValue() == Mode.NOVOID) {
            return "NoVoid";
        }
        return null;
    }

    public static enum Mode {
        STATIC,
        ROOF,
        NOVOID;

    }
}

