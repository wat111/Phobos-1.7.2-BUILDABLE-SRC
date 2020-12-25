/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockLiquid
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.init.Blocks
 *  net.minecraft.item.ItemPickaxe
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package me.earth.phobos.features.modules.misc;

import java.util.ArrayList;
import java.util.List;
import me.earth.phobos.Phobos;
import me.earth.phobos.event.events.BlockEvent;
import me.earth.phobos.event.events.UpdateWalkingPlayerEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.BlockUtil;
import me.earth.phobos.util.MathUtil;
import me.earth.phobos.util.Timer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Nuker
extends Module {
    public Setting<Boolean> rotate = this.register(new Setting<Boolean>("Rotate", false));
    public Setting<Float> distance = this.register(new Setting<Float>("Range", Float.valueOf(6.0f), Float.valueOf(0.1f), Float.valueOf(10.0f)));
    public Setting<Integer> blockPerTick = this.register(new Setting<Integer>("Blocks/Attack", 50, 1, 100));
    public Setting<Integer> delay = this.register(new Setting<Integer>("Delay/Attack", 50, 1, 1000));
    public Setting<Boolean> nuke = this.register(new Setting<Boolean>("Nuke", false));
    public Setting<Mode> mode = this.register(new Setting<Object>("Mode", (Object)Mode.NUKE, v -> this.nuke.getValue()));
    public Setting<Boolean> antiRegear = this.register(new Setting<Boolean>("AntiRegear", false));
    public Setting<Boolean> hopperNuker = this.register(new Setting<Boolean>("HopperAura", false));
    private Setting<Boolean> autoSwitch = this.register(new Setting<Boolean>("AutoSwitch", false));
    private int oldSlot = -1;
    private boolean isMining = false;
    private final Timer timer = new Timer();
    private Block selected;

    public Nuker() {
        super("Nuker", "Mines many blocks", Module.Category.MISC, true, false, false);
    }

    @Override
    public void onToggle() {
        this.selected = null;
    }

    @SubscribeEvent
    public void onClickBlock(BlockEvent event) {
        Block block;
        if (event.getStage() == 3 && (this.mode.getValue() == Mode.SELECTION || this.mode.getValue() == Mode.NUKE) && (block = Nuker.mc.field_71441_e.func_180495_p(event.pos).func_177230_c()) != null && block != this.selected) {
            this.selected = block;
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onUpdateWalkingPlayerPre(UpdateWalkingPlayerEvent event) {
        if (event.getStage() == 0) {
            if (this.nuke.getValue().booleanValue()) {
                BlockPos pos = null;
                switch (this.mode.getValue()) {
                    case SELECTION: 
                    case NUKE: {
                        pos = this.getClosestBlockSelection();
                        break;
                    }
                    case ALL: {
                        pos = this.getClosestBlockAll();
                        break;
                    }
                }
                if (pos != null) {
                    if (this.mode.getValue() == Mode.SELECTION || this.mode.getValue() == Mode.ALL) {
                        if (this.rotate.getValue().booleanValue()) {
                            float[] angle = MathUtil.calcAngle(Nuker.mc.field_71439_g.func_174824_e(mc.func_184121_ak()), new Vec3d((double)((float)pos.func_177958_n() + 0.5f), (double)((float)pos.func_177956_o() + 0.5f), (double)((float)pos.func_177952_p() + 0.5f)));
                            Phobos.rotationManager.setPlayerRotations(angle[0], angle[1]);
                        }
                        if (this.canBreak(pos)) {
                            Nuker.mc.field_71442_b.func_180512_c(pos, Nuker.mc.field_71439_g.func_174811_aO());
                            Nuker.mc.field_71439_g.func_184609_a(EnumHand.MAIN_HAND);
                        }
                    } else {
                        for (int i = 0; i < this.blockPerTick.getValue(); ++i) {
                            pos = this.getClosestBlockSelection();
                            if (pos == null) continue;
                            if (this.rotate.getValue().booleanValue()) {
                                float[] angle = MathUtil.calcAngle(Nuker.mc.field_71439_g.func_174824_e(mc.func_184121_ak()), new Vec3d((double)((float)pos.func_177958_n() + 0.5f), (double)((float)pos.func_177956_o() + 0.5f), (double)((float)pos.func_177952_p() + 0.5f)));
                                Phobos.rotationManager.setPlayerRotations(angle[0], angle[1]);
                            }
                            if (!this.timer.passedMs(this.delay.getValue().intValue())) continue;
                            Nuker.mc.field_71442_b.func_180512_c(pos, Nuker.mc.field_71439_g.func_174811_aO());
                            Nuker.mc.field_71439_g.func_184609_a(EnumHand.MAIN_HAND);
                            this.timer.reset();
                        }
                    }
                }
            }
            if (this.antiRegear.getValue().booleanValue()) {
                this.breakBlocks(BlockUtil.shulkerList);
            }
            if (this.hopperNuker.getValue().booleanValue()) {
                ArrayList<Block> blocklist = new ArrayList<Block>();
                blocklist.add((Block)Blocks.field_150438_bZ);
                this.breakBlocks(blocklist);
            }
        }
    }

    public void breakBlocks(List<Block> blocks) {
        BlockPos pos = this.getNearestBlock(blocks);
        if (pos != null) {
            if (!this.isMining) {
                this.oldSlot = Nuker.mc.field_71439_g.field_71071_by.field_70461_c;
                this.isMining = true;
            }
            if (this.rotate.getValue().booleanValue()) {
                float[] angle = MathUtil.calcAngle(Nuker.mc.field_71439_g.func_174824_e(mc.func_184121_ak()), new Vec3d((double)((float)pos.func_177958_n() + 0.5f), (double)((float)pos.func_177956_o() + 0.5f), (double)((float)pos.func_177952_p() + 0.5f)));
                Phobos.rotationManager.setPlayerRotations(angle[0], angle[1]);
            }
            if (this.canBreak(pos)) {
                if (this.autoSwitch.getValue().booleanValue()) {
                    int newSlot = -1;
                    for (int i = 0; i < 9; ++i) {
                        ItemStack stack = Nuker.mc.field_71439_g.field_71071_by.func_70301_a(i);
                        if (stack == ItemStack.field_190927_a || !(stack.func_77973_b() instanceof ItemPickaxe)) continue;
                        newSlot = i;
                        break;
                    }
                    if (newSlot != -1) {
                        Nuker.mc.field_71439_g.field_71071_by.field_70461_c = newSlot;
                    }
                }
                Nuker.mc.field_71442_b.func_180512_c(pos, Nuker.mc.field_71439_g.func_174811_aO());
                Nuker.mc.field_71439_g.func_184609_a(EnumHand.MAIN_HAND);
            }
        } else if (this.autoSwitch.getValue().booleanValue() && this.oldSlot != -1) {
            Nuker.mc.field_71439_g.field_71071_by.field_70461_c = this.oldSlot;
            this.oldSlot = -1;
            this.isMining = false;
        }
    }

    private boolean canBreak(BlockPos pos) {
        IBlockState blockState = Nuker.mc.field_71441_e.func_180495_p(pos);
        Block block = blockState.func_177230_c();
        return block.func_176195_g(blockState, (World)Nuker.mc.field_71441_e, pos) != -1.0f;
    }

    private BlockPos getNearestBlock(List<Block> blocks) {
        double maxDist = MathUtil.square(this.distance.getValue().floatValue());
        BlockPos ret = null;
        for (double x = maxDist; x >= -maxDist; x -= 1.0) {
            for (double y = maxDist; y >= -maxDist; y -= 1.0) {
                for (double z = maxDist; z >= -maxDist; z -= 1.0) {
                    BlockPos pos = new BlockPos(Nuker.mc.field_71439_g.field_70165_t + x, Nuker.mc.field_71439_g.field_70163_u + y, Nuker.mc.field_71439_g.field_70161_v + z);
                    double dist = Nuker.mc.field_71439_g.func_70092_e((double)pos.func_177958_n(), (double)pos.func_177956_o(), (double)pos.func_177952_p());
                    if (!(dist <= maxDist) || !blocks.contains((Object)Nuker.mc.field_71441_e.func_180495_p(pos).func_177230_c()) || !this.canBreak(pos)) continue;
                    maxDist = dist;
                    ret = pos;
                }
            }
        }
        return ret;
    }

    private BlockPos getClosestBlockAll() {
        float maxDist = this.distance.getValue().floatValue();
        BlockPos ret = null;
        for (float x = maxDist; x >= -maxDist; x -= 1.0f) {
            for (float y = maxDist; y >= -maxDist; y -= 1.0f) {
                for (float z = maxDist; z >= -maxDist; z -= 1.0f) {
                    BlockPos pos = new BlockPos(Nuker.mc.field_71439_g.field_70165_t + (double)x, Nuker.mc.field_71439_g.field_70163_u + (double)y, Nuker.mc.field_71439_g.field_70161_v + (double)z);
                    double dist = Nuker.mc.field_71439_g.func_70011_f((double)pos.func_177958_n(), (double)pos.func_177956_o(), (double)pos.func_177952_p());
                    if (!(dist <= (double)maxDist) || Nuker.mc.field_71441_e.func_180495_p(pos).func_177230_c() == Blocks.field_150350_a || Nuker.mc.field_71441_e.func_180495_p(pos).func_177230_c() instanceof BlockLiquid || !this.canBreak(pos) || !((double)pos.func_177956_o() >= Nuker.mc.field_71439_g.field_70163_u)) continue;
                    maxDist = (float)dist;
                    ret = pos;
                }
            }
        }
        return ret;
    }

    private BlockPos getClosestBlockSelection() {
        float maxDist = this.distance.getValue().floatValue();
        BlockPos ret = null;
        for (float x = maxDist; x >= -maxDist; x -= 1.0f) {
            for (float y = maxDist; y >= -maxDist; y -= 1.0f) {
                for (float z = maxDist; z >= -maxDist; z -= 1.0f) {
                    BlockPos pos = new BlockPos(Nuker.mc.field_71439_g.field_70165_t + (double)x, Nuker.mc.field_71439_g.field_70163_u + (double)y, Nuker.mc.field_71439_g.field_70161_v + (double)z);
                    double dist = Nuker.mc.field_71439_g.func_70011_f((double)pos.func_177958_n(), (double)pos.func_177956_o(), (double)pos.func_177952_p());
                    if (!(dist <= (double)maxDist) || Nuker.mc.field_71441_e.func_180495_p(pos).func_177230_c() == Blocks.field_150350_a || Nuker.mc.field_71441_e.func_180495_p(pos).func_177230_c() instanceof BlockLiquid || Nuker.mc.field_71441_e.func_180495_p(pos).func_177230_c() != this.selected || !this.canBreak(pos) || !((double)pos.func_177956_o() >= Nuker.mc.field_71439_g.field_70163_u)) continue;
                    maxDist = (float)dist;
                    ret = pos;
                }
            }
        }
        return ret;
    }

    public static enum Mode {
        SELECTION,
        ALL,
        NUKE;

    }
}

