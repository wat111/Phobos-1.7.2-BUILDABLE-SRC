/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.common.util.concurrent.AtomicDouble
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.init.Items
 *  net.minecraft.item.ItemStack
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.CPacketEntityAction
 *  net.minecraft.network.play.client.CPacketEntityAction$Action
 *  net.minecraft.network.play.client.CPacketPlayer
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.tileentity.TileEntityBed
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.util.math.Vec3i
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package me.earth.phobos.features.modules.combat;

import com.google.common.util.concurrent.AtomicDouble;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import me.earth.phobos.event.events.PacketEvent;
import me.earth.phobos.event.events.UpdateWalkingPlayerEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.BlockUtil;
import me.earth.phobos.util.DamageUtil;
import me.earth.phobos.util.EntityUtil;
import me.earth.phobos.util.InventoryUtil;
import me.earth.phobos.util.MathUtil;
import me.earth.phobos.util.RotationUtil;
import me.earth.phobos.util.Timer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBed;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class BedBomb
extends Module {
    private final Setting<Boolean> place = this.register(new Setting<Boolean>("Place", false));
    private final Setting<Integer> placeDelay = this.register(new Setting<Object>("Placedelay", Integer.valueOf(50), Integer.valueOf(0), Integer.valueOf(500), v -> this.place.getValue()));
    private final Setting<Float> placeRange = this.register(new Setting<Object>("PlaceRange", Float.valueOf(6.0f), Float.valueOf(1.0f), Float.valueOf(10.0f), v -> this.place.getValue()));
    private final Setting<Boolean> extraPacket = this.register(new Setting<Object>("InsanePacket", Boolean.valueOf(false), v -> this.place.getValue()));
    private final Setting<Boolean> packet = this.register(new Setting<Object>("Packet", Boolean.valueOf(false), v -> this.place.getValue()));
    private final Setting<Boolean> explode = this.register(new Setting<Boolean>("Break", true));
    private final Setting<Integer> breakDelay = this.register(new Setting<Object>("Breakdelay", Integer.valueOf(50), Integer.valueOf(0), Integer.valueOf(500), v -> this.explode.getValue()));
    private final Setting<Float> breakRange = this.register(new Setting<Object>("BreakRange", Float.valueOf(6.0f), Float.valueOf(1.0f), Float.valueOf(10.0f), v -> this.explode.getValue()));
    private final Setting<Float> minDamage = this.register(new Setting<Object>("MinDamage", Float.valueOf(5.0f), Float.valueOf(1.0f), Float.valueOf(36.0f), v -> this.explode.getValue()));
    private final Setting<Float> range = this.register(new Setting<Object>("Range", Float.valueOf(10.0f), Float.valueOf(1.0f), Float.valueOf(12.0f), v -> this.explode.getValue()));
    private final Setting<Boolean> suicide = this.register(new Setting<Object>("Suicide", Boolean.valueOf(false), v -> this.explode.getValue()));
    private final Setting<Boolean> removeTiles = this.register(new Setting<Boolean>("RemoveTiles", false));
    private final Setting<Boolean> rotate = this.register(new Setting<Boolean>("Rotate", false));
    private final Setting<Logic> logic = this.register(new Setting<Object>("Logic", (Object)Logic.BREAKPLACE, v -> this.place.getValue() != false && this.explode.getValue() != false));
    private final Timer breakTimer = new Timer();
    private final Timer placeTimer = new Timer();
    private EntityPlayer target = null;
    private boolean sendRotationPacket = false;
    private final AtomicDouble yaw = new AtomicDouble(-1.0);
    private final AtomicDouble pitch = new AtomicDouble(-1.0);
    private final AtomicBoolean shouldRotate = new AtomicBoolean(false);
    private BlockPos maxPos = null;
    private int lastHotbarSlot = -1;
    private int bedSlot = -1;

    public BedBomb() {
        super("BedBomb", "AutoPlace and Break for beds", Module.Category.COMBAT, true, false, false);
    }

    @SubscribeEvent
    public void onPacket(PacketEvent.Send event) {
        if (this.shouldRotate.get() && event.getPacket() instanceof CPacketPlayer) {
            CPacketPlayer packet = (CPacketPlayer)event.getPacket();
            packet.field_149476_e = (float)this.yaw.get();
            packet.field_149473_f = (float)this.pitch.get();
            this.shouldRotate.set(false);
        }
    }

    @SubscribeEvent
    public void onUpdateWalkingPlayer(UpdateWalkingPlayerEvent event) {
        if (event.getStage() != 0 || BedBomb.fullNullCheck() || BedBomb.mc.field_71439_g.field_71093_bK != -1 && BedBomb.mc.field_71439_g.field_71093_bK != 1) {
            return;
        }
        this.doBedBomb();
    }

    private void doBedBomb() {
        switch (this.logic.getValue()) {
            case BREAKPLACE: {
                this.mapBeds();
                this.breakBeds();
                this.placeBeds();
                break;
            }
            case PLACEBREAK: {
                this.mapBeds();
                this.placeBeds();
                this.breakBeds();
            }
        }
    }

    private void breakBeds() {
        if (this.explode.getValue().booleanValue() && this.breakTimer.passedMs(this.breakDelay.getValue().intValue()) && this.maxPos != null) {
            BedBomb.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketEntityAction((Entity)BedBomb.mc.field_71439_g, CPacketEntityAction.Action.STOP_SNEAKING));
            BlockUtil.rightClickBlockLegit(this.maxPos, this.range.getValue().floatValue(), this.rotate.getValue() != false && this.place.getValue() == false, EnumHand.MAIN_HAND, this.yaw, this.pitch, this.shouldRotate, true);
            if (BedBomb.mc.field_71439_g.func_70093_af()) {
                BedBomb.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketEntityAction((Entity)BedBomb.mc.field_71439_g, CPacketEntityAction.Action.START_SNEAKING));
            }
            this.breakTimer.reset();
        }
    }

    private void mapBeds() {
        this.maxPos = null;
        float maxDamage = 0.5f;
        if (this.removeTiles.getValue().booleanValue()) {
            ArrayList<BedData> removedBlocks = new ArrayList<BedData>();
            for (TileEntity tile : BedBomb.mc.field_71441_e.field_147482_g) {
                if (!(tile instanceof TileEntityBed)) continue;
                TileEntityBed bed = (TileEntityBed)tile;
                BedData data = new BedData(tile.func_174877_v(), BedBomb.mc.field_71441_e.func_180495_p(tile.func_174877_v()), bed, bed.func_193050_e());
                removedBlocks.add(data);
            }
            for (BedData data : removedBlocks) {
                BedBomb.mc.field_71441_e.func_175698_g(data.getPos());
            }
            for (BedData data : removedBlocks) {
                float selfDamage;
                BlockPos pos;
                if (!data.isHeadPiece() || !(BedBomb.mc.field_71439_g.func_174818_b(pos = data.getPos()) <= MathUtil.square(this.breakRange.getValue().floatValue())) || !((double)(selfDamage = DamageUtil.calculateDamage(pos, (Entity)BedBomb.mc.field_71439_g)) + 1.0 < (double)EntityUtil.getHealth((Entity)BedBomb.mc.field_71439_g)) && DamageUtil.canTakeDamage(this.suicide.getValue())) continue;
                for (EntityPlayer player : BedBomb.mc.field_71441_e.field_73010_i) {
                    float damage;
                    if (!(player.func_174818_b(pos) < MathUtil.square(this.range.getValue().floatValue())) || !EntityUtil.isValid((Entity)player, this.range.getValue().floatValue() + this.breakRange.getValue().floatValue()) || !((damage = DamageUtil.calculateDamage(pos, (Entity)player)) > selfDamage || damage > this.minDamage.getValue().floatValue() && !DamageUtil.canTakeDamage(this.suicide.getValue())) && !(damage > EntityUtil.getHealth((Entity)player)) || !(damage > maxDamage)) continue;
                    maxDamage = damage;
                    this.maxPos = pos;
                }
            }
            for (BedData data : removedBlocks) {
                BedBomb.mc.field_71441_e.func_175656_a(data.getPos(), data.getState());
            }
        } else {
            for (TileEntity tile : BedBomb.mc.field_71441_e.field_147482_g) {
                float selfDamage;
                BlockPos pos;
                TileEntityBed bed;
                if (!(tile instanceof TileEntityBed) || !(bed = (TileEntityBed)tile).func_193050_e() || !(BedBomb.mc.field_71439_g.func_174818_b(pos = bed.func_174877_v()) <= MathUtil.square(this.breakRange.getValue().floatValue())) || !((double)(selfDamage = DamageUtil.calculateDamage(pos, (Entity)BedBomb.mc.field_71439_g)) + 1.0 < (double)EntityUtil.getHealth((Entity)BedBomb.mc.field_71439_g)) && DamageUtil.canTakeDamage(this.suicide.getValue())) continue;
                for (EntityPlayer player : BedBomb.mc.field_71441_e.field_73010_i) {
                    float damage;
                    if (!(player.func_174818_b(pos) < MathUtil.square(this.range.getValue().floatValue())) || !EntityUtil.isValid((Entity)player, this.range.getValue().floatValue() + this.breakRange.getValue().floatValue()) || !((damage = DamageUtil.calculateDamage(pos, (Entity)player)) > selfDamage || damage > this.minDamage.getValue().floatValue() && !DamageUtil.canTakeDamage(this.suicide.getValue())) && !(damage > EntityUtil.getHealth((Entity)player)) || !(damage > maxDamage)) continue;
                    maxDamage = damage;
                    this.maxPos = pos;
                }
            }
        }
    }

    private void placeBeds() {
        if (this.place.getValue().booleanValue() && this.placeTimer.passedMs(this.placeDelay.getValue().intValue()) && this.maxPos == null) {
            this.bedSlot = this.findBedSlot();
            if (this.bedSlot == -1) {
                if (BedBomb.mc.field_71439_g.func_184592_cb().func_77973_b() == Items.field_151104_aV) {
                    this.bedSlot = -2;
                } else {
                    return;
                }
            }
            this.lastHotbarSlot = BedBomb.mc.field_71439_g.field_71071_by.field_70461_c;
            this.target = EntityUtil.getClosestEnemy(this.placeRange.getValue().floatValue());
            if (this.target != null) {
                BlockPos targetPos = new BlockPos(this.target.func_174791_d());
                this.placeBed(targetPos, true);
            }
        }
    }

    private void placeBed(BlockPos pos, boolean firstCheck) {
        if (BedBomb.mc.field_71441_e.func_180495_p(pos).func_177230_c() == Blocks.field_150324_C) {
            return;
        }
        float damage = DamageUtil.calculateDamage(pos, (Entity)BedBomb.mc.field_71439_g);
        if ((double)damage > (double)EntityUtil.getHealth((Entity)BedBomb.mc.field_71439_g) + 0.5) {
            if (firstCheck) {
                this.placeBed(pos.func_177984_a(), false);
            }
            return;
        }
        if (!BedBomb.mc.field_71441_e.func_180495_p(pos).func_185904_a().func_76222_j()) {
            if (firstCheck) {
                this.placeBed(pos.func_177984_a(), false);
            }
            return;
        }
        ArrayList<BlockPos> positions = new ArrayList<BlockPos>();
        HashMap<BlockPos, EnumFacing> facings = new HashMap<BlockPos, EnumFacing>();
        for (EnumFacing facing : EnumFacing.values()) {
            BlockPos position;
            if (facing == EnumFacing.DOWN || facing == EnumFacing.UP || !(BedBomb.mc.field_71439_g.func_174818_b(position = pos.func_177972_a(facing)) <= MathUtil.square(this.placeRange.getValue().floatValue())) || !BedBomb.mc.field_71441_e.func_180495_p(position).func_185904_a().func_76222_j() || BedBomb.mc.field_71441_e.func_180495_p(position.func_177977_b()).func_185904_a().func_76222_j()) continue;
            positions.add(position);
            facings.put(position, facing.func_176734_d());
        }
        if (positions.isEmpty()) {
            if (firstCheck) {
                this.placeBed(pos.func_177984_a(), false);
            }
            return;
        }
        positions.sort(Comparator.comparingDouble(pos2 -> BedBomb.mc.field_71439_g.func_174818_b(pos2)));
        BlockPos finalPos = (BlockPos)positions.get(0);
        EnumFacing finalFacing = (EnumFacing)facings.get((Object)finalPos);
        float[] rotation = RotationUtil.simpleFacing(finalFacing);
        if (!this.sendRotationPacket && this.extraPacket.getValue().booleanValue()) {
            RotationUtil.faceYawAndPitch(rotation[0], rotation[1]);
            this.sendRotationPacket = true;
        }
        this.yaw.set((double)rotation[0]);
        this.pitch.set((double)rotation[1]);
        this.shouldRotate.set(true);
        Vec3d hitVec = new Vec3d((Vec3i)finalPos.func_177977_b()).func_72441_c(0.5, 0.5, 0.5).func_178787_e(new Vec3d(finalFacing.func_176734_d().func_176730_m()).func_186678_a(0.5));
        BedBomb.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketEntityAction((Entity)BedBomb.mc.field_71439_g, CPacketEntityAction.Action.START_SNEAKING));
        InventoryUtil.switchToHotbarSlot(this.bedSlot, false);
        BlockUtil.rightClickBlock(finalPos.func_177977_b(), hitVec, this.bedSlot == -2 ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, EnumFacing.UP, this.packet.getValue());
        BedBomb.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketEntityAction((Entity)BedBomb.mc.field_71439_g, CPacketEntityAction.Action.STOP_SNEAKING));
        this.placeTimer.reset();
    }

    @Override
    public String getDisplayInfo() {
        if (this.target != null) {
            return this.target.func_70005_c_();
        }
        return null;
    }

    @Override
    public void onToggle() {
        this.lastHotbarSlot = -1;
        this.bedSlot = -1;
        this.sendRotationPacket = false;
        this.target = null;
        this.yaw.set(-1.0);
        this.pitch.set(-1.0);
        this.shouldRotate.set(false);
    }

    private int findBedSlot() {
        for (int i = 0; i < 9; ++i) {
            ItemStack stack = BedBomb.mc.field_71439_g.field_71071_by.func_70301_a(i);
            if (stack == ItemStack.field_190927_a || stack.func_77973_b() != Items.field_151104_aV) continue;
            return i;
        }
        return -1;
    }

    public static enum Logic {
        BREAKPLACE,
        PLACEBREAK;

    }

    public static class BedData {
        private final BlockPos pos;
        private final IBlockState state;
        private final boolean isHeadPiece;
        private final TileEntityBed entity;

        public BedData(BlockPos pos, IBlockState state, TileEntityBed bed, boolean isHeadPiece) {
            this.pos = pos;
            this.state = state;
            this.entity = bed;
            this.isHeadPiece = isHeadPiece;
        }

        public BlockPos getPos() {
            return this.pos;
        }

        public IBlockState getState() {
            return this.state;
        }

        public boolean isHeadPiece() {
            return this.isHeadPiece;
        }

        public TileEntityBed getEntity() {
            return this.entity;
        }
    }
}

