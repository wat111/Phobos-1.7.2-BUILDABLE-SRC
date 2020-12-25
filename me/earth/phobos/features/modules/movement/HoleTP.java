/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.material.Material
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.CPacketPlayer$Position
 *  net.minecraft.util.math.BlockPos
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package me.earth.phobos.features.modules.movement;

import me.earth.phobos.event.events.UpdateWalkingPlayerEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.modules.movement.LagBlock;
import me.earth.phobos.features.modules.movement.Speed;
import me.earth.phobos.features.modules.movement.Strafe;
import me.earth.phobos.util.BlockUtil;
import me.earth.phobos.util.EntityUtil;
import net.minecraft.block.material.Material;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class HoleTP
extends Module {
    private static HoleTP INSTANCE = new HoleTP();
    private final double[] oneblockPositions = new double[]{0.42, 0.75};
    private int packets;
    private boolean jumped = false;

    public HoleTP() {
        super("HoleTP", "Teleports you in a hole.", Module.Category.MOVEMENT, true, false, false);
        this.setInstance();
    }

    private void setInstance() {
        INSTANCE = this;
    }

    public static HoleTP getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new HoleTP();
        }
        return INSTANCE;
    }

    @SubscribeEvent
    public void onUpdateWalkingPlayer(UpdateWalkingPlayerEvent event) {
        if (event.getStage() == 1 && (Speed.getInstance().isOff() || Speed.getInstance().mode.getValue() == Speed.Mode.INSTANT) && Strafe.getInstance().isOff() && LagBlock.getInstance().isOff()) {
            if (!HoleTP.mc.field_71439_g.field_70122_E) {
                if (HoleTP.mc.field_71474_y.field_74314_A.func_151470_d()) {
                    this.jumped = true;
                }
            } else {
                this.jumped = false;
            }
            if (!this.jumped && (double)HoleTP.mc.field_71439_g.field_70143_R < 0.5 && BlockUtil.isInHole() && HoleTP.mc.field_71439_g.field_70163_u - BlockUtil.getNearestBlockBelow() <= 1.125 && HoleTP.mc.field_71439_g.field_70163_u - BlockUtil.getNearestBlockBelow() <= 0.95 && !EntityUtil.isOnLiquid() && !EntityUtil.isInLiquid()) {
                if (!HoleTP.mc.field_71439_g.field_70122_E) {
                    ++this.packets;
                }
                if (!(HoleTP.mc.field_71439_g.field_70122_E || HoleTP.mc.field_71439_g.func_70055_a(Material.field_151586_h) || HoleTP.mc.field_71439_g.func_70055_a(Material.field_151587_i) || HoleTP.mc.field_71474_y.field_74314_A.func_151470_d() || HoleTP.mc.field_71439_g.func_70617_f_() || this.packets <= 0)) {
                    BlockPos blockPos = new BlockPos(HoleTP.mc.field_71439_g.field_70165_t, HoleTP.mc.field_71439_g.field_70163_u, HoleTP.mc.field_71439_g.field_70161_v);
                    for (double position : this.oneblockPositions) {
                        HoleTP.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayer.Position((double)((float)blockPos.func_177958_n() + 0.5f), HoleTP.mc.field_71439_g.field_70163_u - position, (double)((float)blockPos.func_177952_p() + 0.5f), true));
                    }
                    HoleTP.mc.field_71439_g.func_70107_b((double)((float)blockPos.func_177958_n() + 0.5f), BlockUtil.getNearestBlockBelow() + 0.1, (double)((float)blockPos.func_177952_p() + 0.5f));
                    this.packets = 0;
                }
            }
        }
    }
}

