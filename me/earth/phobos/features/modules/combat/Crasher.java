/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.item.EntityEnderCrystal
 *  net.minecraft.init.Items
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.RayTraceResult
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 *  net.minecraftforge.fml.common.gameevent.TickEvent$ClientTickEvent
 *  net.minecraftforge.fml.common.gameevent.TickEvent$Phase
 */
package me.earth.phobos.features.modules.combat;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.BlockUtil;
import me.earth.phobos.util.InventoryUtil;
import me.earth.phobos.util.Timer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.Items;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class Crasher
extends Module {
    private final Setting<Boolean> oneDot15 = this.register(new Setting<Boolean>("1.15", false));
    private final Setting<Float> placeRange = this.register(new Setting<Float>("PlaceRange", Float.valueOf(6.0f), Float.valueOf(0.0f), Float.valueOf(10.0f)));
    private final Setting<Integer> crystals = this.register(new Setting<Integer>("Packets", 25, 0, 100));
    private final Setting<Integer> coolDown = this.register(new Setting<Integer>("CoolDown", 400, 0, 1000));
    private final Setting<InventoryUtil.Switch> switchMode = this.register(new Setting<InventoryUtil.Switch>("Switch", InventoryUtil.Switch.NORMAL));
    public Setting<Integer> sort = this.register(new Setting<Integer>("Sort", 0, 0, 2));
    private boolean offhand = false;
    private boolean mainhand = false;
    private final Timer timer = new Timer();
    private int lastHotbarSlot = -1;
    private boolean switchedItem = false;
    private boolean chinese = false;
    private final List<Integer> entityIDs = new ArrayList<Integer>();
    private int currentID = -1000;

    public Crasher() {
        super("CrystalCrash", "Attempts to crash chinese AutoCrystals", Module.Category.COMBAT, false, false, true);
    }

    @Override
    public void onEnable() {
        this.chinese = false;
        if (Crasher.fullNullCheck() || !this.timer.passedMs(this.coolDown.getValue().intValue())) {
            this.disable();
            return;
        }
        this.lastHotbarSlot = Crasher.mc.field_71439_g.field_71071_by.field_70461_c;
        this.placeCrystals();
        this.disable();
    }

    @Override
    public void onDisable() {
        if (!Crasher.fullNullCheck()) {
            for (int i : this.entityIDs) {
                Crasher.mc.field_71441_e.func_73028_b(i);
            }
        }
        this.entityIDs.clear();
        this.currentID = -1000;
        this.timer.reset();
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (Crasher.fullNullCheck() || event.phase == TickEvent.Phase.START || this.isOff() && this.timer.passedMs(10L)) {
            return;
        }
        this.switchItem(true);
    }

    private void placeCrystals() {
        this.offhand = Crasher.mc.field_71439_g.func_184592_cb().func_77973_b() == Items.field_185158_cP;
        this.mainhand = Crasher.mc.field_71439_g.func_184614_ca().func_77973_b() == Items.field_185158_cP;
        int crystalcount = 0;
        List<BlockPos> blocks = BlockUtil.possiblePlacePositions(this.placeRange.getValue().floatValue(), false, this.oneDot15.getValue());
        if (this.sort.getValue() == 1) {
            blocks.sort(Comparator.comparingDouble(hole -> Crasher.mc.field_71439_g.func_174818_b(hole)));
        } else if (this.sort.getValue() == 2) {
            blocks.sort(Comparator.comparingDouble(hole -> -Crasher.mc.field_71439_g.func_174818_b(hole)));
        }
        for (BlockPos pos : blocks) {
            if (this.isOff() || crystalcount >= this.crystals.getValue()) break;
            if (!BlockUtil.canPlaceCrystal(pos, false, this.oneDot15.getValue())) continue;
            this.placeCrystal(pos);
            ++crystalcount;
        }
    }

    private void placeCrystal(BlockPos pos) {
        if (!(this.chinese || this.mainhand || this.offhand || this.switchItem(false))) {
            this.disable();
            return;
        }
        RayTraceResult result = Crasher.mc.field_71441_e.func_72933_a(new Vec3d(Crasher.mc.field_71439_g.field_70165_t, Crasher.mc.field_71439_g.field_70163_u + (double)Crasher.mc.field_71439_g.func_70047_e(), Crasher.mc.field_71439_g.field_70161_v), new Vec3d((double)pos.func_177958_n() + 0.5, (double)pos.func_177956_o() - 0.5, (double)pos.func_177952_p() + 0.5));
        EnumFacing facing = result == null || result.field_178784_b == null ? EnumFacing.UP : result.field_178784_b;
        Crasher.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayerTryUseItemOnBlock(pos, facing, this.offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0.0f, 0.0f, 0.0f));
        Crasher.mc.field_71439_g.func_184609_a(EnumHand.MAIN_HAND);
        EntityEnderCrystal fakeCrystal = new EntityEnderCrystal((World)Crasher.mc.field_71441_e, (double)((float)pos.func_177958_n() + 0.5f), (double)(pos.func_177956_o() + 1), (double)((float)pos.func_177952_p() + 0.5f));
        int newID = this.currentID--;
        this.entityIDs.add(newID);
        Crasher.mc.field_71441_e.func_73027_a(newID, (Entity)fakeCrystal);
    }

    private boolean switchItem(boolean back) {
        this.chinese = true;
        if (this.offhand) {
            return true;
        }
        boolean[] value = InventoryUtil.switchItemToItem(back, this.lastHotbarSlot, this.switchedItem, this.switchMode.getValue(), Items.field_185158_cP);
        this.switchedItem = value[0];
        return value[1];
    }
}

