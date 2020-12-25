/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.entity.Entity
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemBlock
 *  net.minecraft.item.ItemStack
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.CPacketEntityAction
 *  net.minecraft.network.play.client.CPacketEntityAction$Action
 *  net.minecraft.network.play.client.CPacketHeldItemChange
 *  net.minecraft.network.play.client.CPacketPlayer$Rotation
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Vec3d
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package me.earth.phobos.features.modules.player;

import me.earth.phobos.event.events.UpdateWalkingPlayerEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.BlockUtil;
import me.earth.phobos.util.EntityUtil;
import me.earth.phobos.util.InventoryUtil;
import me.earth.phobos.util.MathUtil;
import me.earth.phobos.util.Timer;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Scaffold
extends Module {
    public Setting<Boolean> rotation = this.register(new Setting<Boolean>("Rotate", false));
    private final Timer timer = new Timer();

    public Scaffold() {
        super("Scaffold", "Places Blocks underneath you.", Module.Category.PLAYER, true, false, false);
    }

    @Override
    public void onEnable() {
        this.timer.reset();
    }

    @SubscribeEvent
    public void onUpdateWalkingPlayerPost(UpdateWalkingPlayerEvent event) {
        BlockPos playerBlock;
        if (this.isOff() || Scaffold.fullNullCheck() || event.getStage() == 0) {
            return;
        }
        if (!Scaffold.mc.field_71474_y.field_74314_A.func_151470_d()) {
            this.timer.reset();
        }
        if (BlockUtil.isScaffoldPos((playerBlock = EntityUtil.getPlayerPosWithEntity()).func_177982_a(0, -1, 0))) {
            if (BlockUtil.isValidBlock(playerBlock.func_177982_a(0, -2, 0))) {
                this.place(playerBlock.func_177982_a(0, -1, 0), EnumFacing.UP);
            } else if (BlockUtil.isValidBlock(playerBlock.func_177982_a(-1, -1, 0))) {
                this.place(playerBlock.func_177982_a(0, -1, 0), EnumFacing.EAST);
            } else if (BlockUtil.isValidBlock(playerBlock.func_177982_a(1, -1, 0))) {
                this.place(playerBlock.func_177982_a(0, -1, 0), EnumFacing.WEST);
            } else if (BlockUtil.isValidBlock(playerBlock.func_177982_a(0, -1, -1))) {
                this.place(playerBlock.func_177982_a(0, -1, 0), EnumFacing.SOUTH);
            } else if (BlockUtil.isValidBlock(playerBlock.func_177982_a(0, -1, 1))) {
                this.place(playerBlock.func_177982_a(0, -1, 0), EnumFacing.NORTH);
            } else if (BlockUtil.isValidBlock(playerBlock.func_177982_a(1, -1, 1))) {
                if (BlockUtil.isValidBlock(playerBlock.func_177982_a(0, -1, 1))) {
                    this.place(playerBlock.func_177982_a(0, -1, 1), EnumFacing.NORTH);
                }
                this.place(playerBlock.func_177982_a(1, -1, 1), EnumFacing.EAST);
            } else if (BlockUtil.isValidBlock(playerBlock.func_177982_a(-1, -1, 1))) {
                if (BlockUtil.isValidBlock(playerBlock.func_177982_a(-1, -1, 0))) {
                    this.place(playerBlock.func_177982_a(0, -1, 1), EnumFacing.WEST);
                }
                this.place(playerBlock.func_177982_a(-1, -1, 1), EnumFacing.SOUTH);
            } else if (BlockUtil.isValidBlock(playerBlock.func_177982_a(1, -1, 1))) {
                if (BlockUtil.isValidBlock(playerBlock.func_177982_a(0, -1, 1))) {
                    this.place(playerBlock.func_177982_a(0, -1, 1), EnumFacing.SOUTH);
                }
                this.place(playerBlock.func_177982_a(1, -1, 1), EnumFacing.WEST);
            } else if (BlockUtil.isValidBlock(playerBlock.func_177982_a(1, -1, 1))) {
                if (BlockUtil.isValidBlock(playerBlock.func_177982_a(0, -1, 1))) {
                    this.place(playerBlock.func_177982_a(0, -1, 1), EnumFacing.EAST);
                }
                this.place(playerBlock.func_177982_a(1, -1, 1), EnumFacing.NORTH);
            }
        }
    }

    public void place(BlockPos posI, EnumFacing face) {
        Block block;
        BlockPos pos = posI;
        if (face == EnumFacing.UP) {
            pos = pos.func_177982_a(0, -1, 0);
        } else if (face == EnumFacing.NORTH) {
            pos = pos.func_177982_a(0, 0, 1);
        } else if (face == EnumFacing.SOUTH) {
            pos = pos.func_177982_a(0, 0, -1);
        } else if (face == EnumFacing.EAST) {
            pos = pos.func_177982_a(-1, 0, 0);
        } else if (face == EnumFacing.WEST) {
            pos = pos.func_177982_a(1, 0, 0);
        }
        int oldSlot = Scaffold.mc.field_71439_g.field_71071_by.field_70461_c;
        int newSlot = -1;
        for (int i = 0; i < 9; ++i) {
            ItemStack stack = Scaffold.mc.field_71439_g.field_71071_by.func_70301_a(i);
            if (InventoryUtil.isNull(stack) || !(stack.func_77973_b() instanceof ItemBlock) || !Block.func_149634_a((Item)stack.func_77973_b()).func_176223_P().func_185913_b()) continue;
            newSlot = i;
            break;
        }
        if (newSlot == -1) {
            return;
        }
        boolean crouched = false;
        if (!Scaffold.mc.field_71439_g.func_70093_af() && BlockUtil.blackList.contains((Object)(block = Scaffold.mc.field_71441_e.func_180495_p(pos).func_177230_c()))) {
            Scaffold.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketEntityAction((Entity)Scaffold.mc.field_71439_g, CPacketEntityAction.Action.START_SNEAKING));
            crouched = true;
        }
        if (!(Scaffold.mc.field_71439_g.func_184614_ca().func_77973_b() instanceof ItemBlock)) {
            Scaffold.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketHeldItemChange(newSlot));
            Scaffold.mc.field_71439_g.field_71071_by.field_70461_c = newSlot;
            Scaffold.mc.field_71442_b.func_78765_e();
        }
        if (Scaffold.mc.field_71474_y.field_74314_A.func_151470_d()) {
            Scaffold.mc.field_71439_g.field_70159_w *= 0.3;
            Scaffold.mc.field_71439_g.field_70179_y *= 0.3;
            Scaffold.mc.field_71439_g.func_70664_aZ();
            if (this.timer.passedMs(1500L)) {
                Scaffold.mc.field_71439_g.field_70181_x = -0.28;
                this.timer.reset();
            }
        }
        if (this.rotation.getValue().booleanValue()) {
            float[] angle = MathUtil.calcAngle(Scaffold.mc.field_71439_g.func_174824_e(mc.func_184121_ak()), new Vec3d((double)((float)pos.func_177958_n() + 0.5f), (double)((float)pos.func_177956_o() - 0.5f), (double)((float)pos.func_177952_p() + 0.5f)));
            Scaffold.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayer.Rotation(angle[0], (float)MathHelper.func_180184_b((int)((int)angle[1]), (int)360), Scaffold.mc.field_71439_g.field_70122_E));
        }
        Scaffold.mc.field_71442_b.func_187099_a(Scaffold.mc.field_71439_g, Scaffold.mc.field_71441_e, pos, face, new Vec3d(0.5, 0.5, 0.5), EnumHand.MAIN_HAND);
        Scaffold.mc.field_71439_g.func_184609_a(EnumHand.MAIN_HAND);
        Scaffold.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketHeldItemChange(oldSlot));
        Scaffold.mc.field_71439_g.field_71071_by.field_70461_c = oldSlot;
        Scaffold.mc.field_71442_b.func_78765_e();
        if (crouched) {
            Scaffold.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketEntityAction((Entity)Scaffold.mc.field_71439_g, CPacketEntityAction.Action.STOP_SNEAKING));
        }
    }
}

