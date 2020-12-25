/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.init.Items
 *  net.minecraft.inventory.ClickType
 *  net.minecraft.inventory.EntityEquipmentSlot
 *  net.minecraft.item.ItemStack
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.CPacketClickWindow
 *  net.minecraft.network.play.client.CPacketEntityAction
 *  net.minecraft.network.play.client.CPacketEntityAction$Action
 *  net.minecraft.network.play.client.CPacketPlayer
 *  net.minecraft.network.play.server.SPacketSetSlot
 *  net.minecraft.network.play.server.SPacketWindowItems
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.math.RayTraceResult
 *  net.minecraft.util.math.RayTraceResult$Type
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package me.earth.phobos.features.modules.movement;

import me.earth.phobos.event.events.PacketEvent;
import me.earth.phobos.features.command.Command;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.EntityUtil;
import me.earth.phobos.util.InventoryUtil;
import me.earth.phobos.util.Timer;
import me.earth.phobos.util.Util;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketClickWindow;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketSetSlot;
import net.minecraft.network.play.server.SPacketWindowItems;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class NoFall
extends Module {
    private Setting<Mode> mode = this.register(new Setting<Mode>("Mode", Mode.PACKET));
    private Setting<Integer> distance = this.register(new Setting<Object>("Distance", Integer.valueOf(15), Integer.valueOf(0), Integer.valueOf(50), v -> this.mode.getValue() == Mode.BUCKET));
    private Setting<Boolean> glide = this.register(new Setting<Object>("Glide", Boolean.valueOf(false), v -> this.mode.getValue() == Mode.ELYTRA));
    private Setting<Boolean> silent = this.register(new Setting<Object>("Silent", Boolean.valueOf(true), v -> this.mode.getValue() == Mode.ELYTRA));
    private Setting<Boolean> bypass = this.register(new Setting<Object>("Bypass", Boolean.valueOf(false), v -> this.mode.getValue() == Mode.ELYTRA));
    private Timer timer = new Timer();
    private boolean equipped = false;
    private boolean gotElytra = false;
    private State currentState = State.FALL_CHECK;
    private static Timer bypassTimer = new Timer();
    private static int ogslot = -1;

    public NoFall() {
        super("NoFall", "Prevents fall damage.", Module.Category.MOVEMENT, true, false, false);
    }

    @Override
    public void onEnable() {
        ogslot = -1;
        this.currentState = State.FALL_CHECK;
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        if (NoFall.fullNullCheck()) {
            return;
        }
        if (this.mode.getValue() == Mode.ELYTRA) {
            if (this.bypass.getValue().booleanValue()) {
                this.currentState = this.currentState.onSend(event);
            } else if (!this.equipped && event.getPacket() instanceof CPacketPlayer && NoFall.mc.field_71439_g.field_70143_R >= 3.0f) {
                RayTraceResult result = null;
                if (!this.glide.getValue().booleanValue()) {
                    result = NoFall.mc.field_71441_e.func_147447_a(NoFall.mc.field_71439_g.func_174791_d(), NoFall.mc.field_71439_g.func_174791_d().func_72441_c(0.0, -3.0, 0.0), true, true, false);
                }
                if (this.glide.getValue().booleanValue() || result != null && result.field_72313_a == RayTraceResult.Type.BLOCK) {
                    if (NoFall.mc.field_71439_g.func_184582_a(EntityEquipmentSlot.CHEST).func_77973_b().equals((Object)Items.field_185160_cR)) {
                        NoFall.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketEntityAction((Entity)NoFall.mc.field_71439_g, CPacketEntityAction.Action.START_FALL_FLYING));
                    } else if (this.silent.getValue().booleanValue()) {
                        int slot = InventoryUtil.getItemHotbar(Items.field_185160_cR);
                        if (slot != -1) {
                            NoFall.mc.field_71442_b.func_187098_a(NoFall.mc.field_71439_g.field_71069_bz.field_75152_c, 6, slot, ClickType.SWAP, (EntityPlayer)NoFall.mc.field_71439_g);
                            NoFall.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketEntityAction((Entity)NoFall.mc.field_71439_g, CPacketEntityAction.Action.START_FALL_FLYING));
                        }
                        ogslot = slot;
                        this.equipped = true;
                    }
                }
            }
        }
        if (this.mode.getValue() == Mode.PACKET && event.getPacket() instanceof CPacketPlayer) {
            CPacketPlayer packet = (CPacketPlayer)event.getPacket();
            packet.field_149474_g = true;
        }
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {
        if (NoFall.fullNullCheck()) {
            return;
        }
        if ((this.equipped || this.bypass.getValue().booleanValue()) && this.mode.getValue() == Mode.ELYTRA && (event.getPacket() instanceof SPacketWindowItems || event.getPacket() instanceof SPacketSetSlot)) {
            if (this.bypass.getValue().booleanValue()) {
                this.currentState = this.currentState.onReceive(event);
            } else {
                this.gotElytra = true;
            }
        }
    }

    @Override
    public void onUpdate() {
        if (NoFall.fullNullCheck()) {
            return;
        }
        if (this.mode.getValue() == Mode.ELYTRA) {
            int slot;
            if (this.bypass.getValue().booleanValue()) {
                this.currentState = this.currentState.onUpdate();
            } else if (this.silent.getValue().booleanValue() && this.equipped && this.gotElytra) {
                NoFall.mc.field_71442_b.func_187098_a(NoFall.mc.field_71439_g.field_71069_bz.field_75152_c, 6, ogslot, ClickType.SWAP, (EntityPlayer)NoFall.mc.field_71439_g);
                NoFall.mc.field_71442_b.func_78765_e();
                this.equipped = false;
                this.gotElytra = false;
            } else if (this.silent.getValue().booleanValue() && InventoryUtil.getItemHotbar(Items.field_185160_cR) == -1 && (slot = InventoryUtil.findStackInventory(Items.field_185160_cR)) != -1 && ogslot != -1) {
                System.out.println(String.format("Moving %d to hotbar %d", slot, ogslot));
                NoFall.mc.field_71442_b.func_187098_a(NoFall.mc.field_71439_g.field_71069_bz.field_75152_c, slot, ogslot, ClickType.SWAP, (EntityPlayer)NoFall.mc.field_71439_g);
                NoFall.mc.field_71442_b.func_78765_e();
            }
        }
    }

    @Override
    public void onTick() {
        Vec3d posVec;
        RayTraceResult result;
        if (NoFall.fullNullCheck()) {
            return;
        }
        if (this.mode.getValue() == Mode.BUCKET && NoFall.mc.field_71439_g.field_70143_R >= (float)this.distance.getValue().intValue() && !EntityUtil.isAboveWater((Entity)NoFall.mc.field_71439_g) && this.timer.passedMs(100L) && (result = NoFall.mc.field_71441_e.func_147447_a(posVec = NoFall.mc.field_71439_g.func_174791_d(), posVec.func_72441_c(0.0, (double)-5.33f, 0.0), true, true, false)) != null && result.field_72313_a == RayTraceResult.Type.BLOCK) {
            EnumHand hand = EnumHand.MAIN_HAND;
            if (NoFall.mc.field_71439_g.func_184592_cb().func_77973_b() == Items.field_151131_as) {
                hand = EnumHand.OFF_HAND;
            } else if (NoFall.mc.field_71439_g.func_184614_ca().func_77973_b() != Items.field_151131_as) {
                for (int i = 0; i < 9; ++i) {
                    if (NoFall.mc.field_71439_g.field_71071_by.func_70301_a(i).func_77973_b() != Items.field_151131_as) continue;
                    NoFall.mc.field_71439_g.field_71071_by.field_70461_c = i;
                    NoFall.mc.field_71439_g.field_70125_A = 90.0f;
                    this.timer.reset();
                    return;
                }
                return;
            }
            NoFall.mc.field_71439_g.field_70125_A = 90.0f;
            NoFall.mc.field_71442_b.func_187101_a((EntityPlayer)NoFall.mc.field_71439_g, (World)NoFall.mc.field_71441_e, hand);
            this.timer.reset();
        }
    }

    @Override
    public String getDisplayInfo() {
        return this.mode.currentEnumName();
    }

    public static enum State {
        FALL_CHECK{

            @Override
            public State onSend(PacketEvent.Send event) {
                RayTraceResult result = Util.mc.field_71441_e.func_147447_a(Util.mc.field_71439_g.func_174791_d(), Util.mc.field_71439_g.func_174791_d().func_72441_c(0.0, -3.0, 0.0), true, true, false);
                if (event.getPacket() instanceof CPacketPlayer && Util.mc.field_71439_g.field_70143_R >= 3.0f && result != null && result.field_72313_a == RayTraceResult.Type.BLOCK) {
                    int slot = InventoryUtil.getItemHotbar(Items.field_185160_cR);
                    if (slot != -1) {
                        Util.mc.field_71442_b.func_187098_a(Util.mc.field_71439_g.field_71069_bz.field_75152_c, 6, slot, ClickType.SWAP, (EntityPlayer)Util.mc.field_71439_g);
                        ogslot = slot;
                        Util.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketEntityAction((Entity)Util.mc.field_71439_g, CPacketEntityAction.Action.START_FALL_FLYING));
                        return WAIT_FOR_ELYTRA_DEQUIP;
                    }
                    return this;
                }
                return this;
            }
        }
        ,
        WAIT_FOR_ELYTRA_DEQUIP{

            @Override
            public State onReceive(PacketEvent.Receive event) {
                if (event.getPacket() instanceof SPacketWindowItems || event.getPacket() instanceof SPacketSetSlot) {
                    return REEQUIP_ELYTRA;
                }
                return this;
            }
        }
        ,
        REEQUIP_ELYTRA{

            @Override
            public State onUpdate() {
                Util.mc.field_71442_b.func_187098_a(Util.mc.field_71439_g.field_71069_bz.field_75152_c, 6, ogslot, ClickType.SWAP, (EntityPlayer)Util.mc.field_71439_g);
                Util.mc.field_71442_b.func_78765_e();
                int slot = InventoryUtil.findStackInventory(Items.field_185160_cR, true);
                if (slot == -1) {
                    Command.sendMessage("\u00a7cElytra not found after regain?");
                    return WAIT_FOR_NEXT_REQUIP;
                }
                Util.mc.field_71442_b.func_187098_a(Util.mc.field_71439_g.field_71069_bz.field_75152_c, slot, ogslot, ClickType.SWAP, (EntityPlayer)Util.mc.field_71439_g);
                Util.mc.field_71442_b.func_78765_e();
                bypassTimer.reset();
                return RESET_TIME;
            }
        }
        ,
        WAIT_FOR_NEXT_REQUIP{

            @Override
            public State onUpdate() {
                if (bypassTimer.passedMs(250L)) {
                    return REEQUIP_ELYTRA;
                }
                return this;
            }
        }
        ,
        RESET_TIME{

            @Override
            public State onUpdate() {
                if (Util.mc.field_71439_g.field_70122_E || bypassTimer.passedMs(250L)) {
                    Util.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketClickWindow(0, 0, 0, ClickType.PICKUP, new ItemStack(Blocks.field_150357_h), 1337));
                    return FALL_CHECK;
                }
                return this;
            }
        };


        public State onSend(PacketEvent.Send e) {
            return this;
        }

        public State onReceive(PacketEvent.Receive e) {
            return this;
        }

        public State onUpdate() {
            return this;
        }
    }

    public static enum Mode {
        PACKET,
        BUCKET,
        ELYTRA;

    }
}

