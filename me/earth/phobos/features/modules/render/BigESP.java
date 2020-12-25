/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 */
package me.earth.phobos.features.modules.render;

import me.earth.phobos.event.events.Render3DEvent;
import me.earth.phobos.features.modules.Module;
import net.minecraft.entity.player.EntityPlayer;

public class BigESP
extends Module {
    public BigESP() {
        super("BigModule", "Big fucking module", Module.Category.RENDER, true, false, false);
    }

    @Override
    public void onRender3D(Render3DEvent event) {
        if (!BigESP.fullNullCheck()) {
            for (EntityPlayer player : BigESP.mc.field_71441_e.field_73010_i) {
                double x = this.interpolate(player.field_70142_S, player.field_70165_t, event.getPartialTicks()) - BigESP.mc.func_175598_ae().field_78725_b;
                double y = this.interpolate(player.field_70137_T, player.field_70163_u, event.getPartialTicks()) - BigESP.mc.func_175598_ae().field_78726_c;
                double z = this.interpolate(player.field_70136_U, player.field_70161_v, event.getPartialTicks()) - BigESP.mc.func_175598_ae().field_78723_d;
                this.renderBigESP(player, x, y, z, event.getPartialTicks());
            }
        }
    }

    public void renderBigESP(EntityPlayer player, double x, double y, double z, float delta) {
    }

    private double interpolate(double previous, double current, float delta) {
        return previous + (current - previous) * (double)delta;
    }
}

