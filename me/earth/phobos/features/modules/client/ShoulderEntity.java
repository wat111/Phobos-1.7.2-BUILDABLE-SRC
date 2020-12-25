/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.passive.EntityOcelot
 *  net.minecraft.nbt.NBTBase
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.nbt.NBTTagInt
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.world.World
 *  net.minecraftforge.client.event.RenderPlayerEvent$Post
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package me.earth.phobos.features.modules.client;

import me.earth.phobos.features.modules.Module;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ShoulderEntity
extends Module {
    private static final ResourceLocation BLACK_OCELOT_TEXTURES = new ResourceLocation("textures/entity/cat/black.png");

    public ShoulderEntity() {
        super("ShoulderEntity", "Test", Module.Category.CLIENT, true, false, false);
    }

    @Override
    public void onEnable() {
        ShoulderEntity.mc.field_71441_e.func_73027_a(-101, (Entity)new EntityOcelot((World)ShoulderEntity.mc.field_71441_e));
        NBTTagCompound tag = new NBTTagCompound();
        tag.func_74782_a("id", (NBTBase)new NBTTagInt(-101));
        ShoulderEntity.mc.field_71439_g.func_192027_g(tag);
    }

    @Override
    public void onDisable() {
        ShoulderEntity.mc.field_71441_e.func_73028_b(-101);
    }

    @SubscribeEvent
    public void onRenderPlayer(RenderPlayerEvent.Post event) {
    }

    public float interpolate(float yaw1, float yaw2, float percent) {
        float rotation = (yaw1 + (yaw2 - yaw1) * percent) % 360.0f;
        if (rotation < 0.0f) {
            rotation += 360.0f;
        }
        return rotation;
    }
}

