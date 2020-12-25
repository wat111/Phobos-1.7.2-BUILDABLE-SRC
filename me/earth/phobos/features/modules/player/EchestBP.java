/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.client.gui.inventory.GuiContainer
 *  net.minecraft.inventory.Container
 *  net.minecraft.inventory.ContainerChest
 *  net.minecraft.inventory.InventoryBasic
 */
package me.earth.phobos.features.modules.player;

import me.earth.phobos.features.modules.Module;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.InventoryBasic;

public class EchestBP
extends Module {
    private GuiScreen echestScreen = null;

    public EchestBP() {
        super("EchestBP", "Allows to open your echest later.", Module.Category.PLAYER, false, false, false);
    }

    @Override
    public void onUpdate() {
        InventoryBasic basic;
        Container container;
        if (EchestBP.mc.field_71462_r instanceof GuiContainer && (container = ((GuiContainer)EchestBP.mc.field_71462_r).field_147002_h) instanceof ContainerChest && ((ContainerChest)container).func_85151_d() instanceof InventoryBasic && (basic = (InventoryBasic)((ContainerChest)container).func_85151_d()).func_70005_c_().equalsIgnoreCase("Ender Chest")) {
            this.echestScreen = EchestBP.mc.field_71462_r;
            EchestBP.mc.field_71462_r = null;
        }
    }

    @Override
    public void onDisable() {
        if (!EchestBP.fullNullCheck() && this.echestScreen != null) {
            mc.func_147108_a(this.echestScreen);
        }
        this.echestScreen = null;
    }
}

