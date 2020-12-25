/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.init.Blocks
 */
package me.earth.phobos.features.modules.movement;

import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import net.minecraft.init.Blocks;

public class IceSpeed
extends Module {
    private Setting<Float> speed = this.register(new Setting<Float>("Speed", Float.valueOf(0.4f), Float.valueOf(0.2f), Float.valueOf(1.5f)));
    private static IceSpeed INSTANCE = new IceSpeed();

    public IceSpeed() {
        super("IceSpeed", "Speeds you up on ice.", Module.Category.MOVEMENT, false, false, false);
        INSTANCE = this;
    }

    public static IceSpeed getINSTANCE() {
        if (INSTANCE == null) {
            INSTANCE = new IceSpeed();
        }
        return INSTANCE;
    }

    @Override
    public void onUpdate() {
        Blocks.field_150432_aD.field_149765_K = this.speed.getValue().floatValue();
        Blocks.field_150403_cj.field_149765_K = this.speed.getValue().floatValue();
        Blocks.field_185778_de.field_149765_K = this.speed.getValue().floatValue();
    }

    @Override
    public void onDisable() {
        Blocks.field_150432_aD.field_149765_K = 0.98f;
        Blocks.field_150403_cj.field_149765_K = 0.98f;
        Blocks.field_185778_de.field_149765_K = 0.98f;
    }
}

