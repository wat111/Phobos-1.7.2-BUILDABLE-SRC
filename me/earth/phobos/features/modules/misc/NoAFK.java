/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.CPacketAnimation
 *  net.minecraft.util.EnumHand
 */
package me.earth.phobos.features.modules.misc;

import java.util.Random;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.util.EnumHand;

public class NoAFK
extends Module {
    private final Setting<Boolean> swing = this.register(new Setting<Boolean>("Swing", true));
    private final Setting<Boolean> turn = this.register(new Setting<Boolean>("Turn", true));
    private final Random random = new Random();

    public NoAFK() {
        super("NoAFK", "Prevents you from getting kicked for afk.", Module.Category.MISC, false, false, false);
    }

    @Override
    public void onUpdate() {
        if (NoAFK.mc.field_71442_b.func_181040_m()) {
            return;
        }
        if (NoAFK.mc.field_71439_g.field_70173_aa % 40 == 0 && this.swing.getValue().booleanValue()) {
            NoAFK.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketAnimation(EnumHand.MAIN_HAND));
        }
        if (NoAFK.mc.field_71439_g.field_70173_aa % 15 == 0 && this.turn.getValue().booleanValue()) {
            NoAFK.mc.field_71439_g.field_70177_z = this.random.nextInt(360) - 180;
        }
        if (!this.swing.getValue().booleanValue() && !this.turn.getValue().booleanValue() && NoAFK.mc.field_71439_g.field_70173_aa % 80 == 0) {
            NoAFK.mc.field_71439_g.func_70664_aZ();
        }
    }
}

