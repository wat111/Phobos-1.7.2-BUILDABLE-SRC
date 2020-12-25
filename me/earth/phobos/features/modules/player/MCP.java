/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Items
 *  net.minecraft.item.ItemEnderPearl
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.math.RayTraceResult
 *  net.minecraft.util.math.RayTraceResult$Type
 *  net.minecraft.world.World
 *  org.lwjgl.input.Mouse
 */
package me.earth.phobos.features.modules.player;

import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.InventoryUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemEnderPearl;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import org.lwjgl.input.Mouse;

public class MCP
extends Module {
    private Setting<Mode> mode = this.register(new Setting<Mode>("Mode", Mode.MIDDLECLICK));
    private Setting<Boolean> stopRotation = this.register(new Setting<Boolean>("Rotation", true));
    private Setting<Boolean> antiFriend = this.register(new Setting<Boolean>("AntiFriend", true));
    private Setting<Integer> rotation = this.register(new Setting<Object>("Delay", Integer.valueOf(10), Integer.valueOf(0), Integer.valueOf(100), v -> this.stopRotation.getValue()));
    private boolean clicked = false;

    public MCP() {
        super("MCP", "Throws a pearl", Module.Category.PLAYER, false, false, false);
    }

    @Override
    public void onEnable() {
        if (!MCP.fullNullCheck() && this.mode.getValue() == Mode.TOGGLE) {
            this.throwPearl();
            this.disable();
        }
    }

    @Override
    public void onTick() {
        if (this.mode.getValue() == Mode.MIDDLECLICK) {
            if (Mouse.isButtonDown((int)2)) {
                if (!this.clicked) {
                    this.throwPearl();
                }
                this.clicked = true;
            } else {
                this.clicked = false;
            }
        }
    }

    private void throwPearl() {
        boolean offhand;
        Entity entity;
        RayTraceResult result;
        if (this.antiFriend.getValue().booleanValue() && (result = MCP.mc.field_71476_x) != null && result.field_72313_a == RayTraceResult.Type.ENTITY && (entity = result.field_72308_g) instanceof EntityPlayer) {
            return;
        }
        int pearlSlot = InventoryUtil.findHotbarBlock(ItemEnderPearl.class);
        boolean bl = offhand = MCP.mc.field_71439_g.func_184592_cb().func_77973_b() == Items.field_151079_bi;
        if (pearlSlot != -1 || offhand) {
            int oldslot = MCP.mc.field_71439_g.field_71071_by.field_70461_c;
            if (!offhand) {
                InventoryUtil.switchToHotbarSlot(pearlSlot, false);
            }
            MCP.mc.field_71442_b.func_187101_a((EntityPlayer)MCP.mc.field_71439_g, (World)MCP.mc.field_71441_e, offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND);
            if (!offhand) {
                InventoryUtil.switchToHotbarSlot(oldslot, false);
            }
        }
    }

    public static enum Mode {
        TOGGLE,
        MIDDLECLICK;

    }
}

