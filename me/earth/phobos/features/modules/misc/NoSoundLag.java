/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.item.EntityEnderCrystal
 *  net.minecraft.init.SoundEvents
 *  net.minecraft.network.play.server.SPacketSoundEffect
 *  net.minecraft.util.SoundCategory
 *  net.minecraft.util.SoundEvent
 *  net.minecraft.util.math.BlockPos
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package me.earth.phobos.features.modules.misc;

import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Set;
import me.earth.phobos.event.events.PacketEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.modules.combat.AutoCrystal;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.MathUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class NoSoundLag
extends Module {
    public Setting<Boolean> crystals = this.register(new Setting<Boolean>("Crystals", true));
    public Setting<Boolean> armor = this.register(new Setting<Boolean>("Armor", true));
    public Setting<Float> soundRange = this.register(new Setting<Float>("SoundRange", Float.valueOf(12.0f), Float.valueOf(0.0f), Float.valueOf(12.0f)));
    private static NoSoundLag instance;
    private static final Set<SoundEvent> BLACKLIST;

    public NoSoundLag() {
        super("NoSoundLag", "Prevents Lag through sound spam.", Module.Category.MISC, true, false, false);
        instance = this;
    }

    public static NoSoundLag getInstance() {
        if (instance == null) {
            instance = new NoSoundLag();
        }
        return instance;
    }

    @SubscribeEvent
    public void onPacketReceived(PacketEvent.Receive event) {
        if (event != null && event.getPacket() != null && NoSoundLag.mc.field_71439_g != null && NoSoundLag.mc.field_71441_e != null && event.getPacket() instanceof SPacketSoundEffect) {
            SPacketSoundEffect packet = (SPacketSoundEffect)event.getPacket();
            if (this.crystals.getValue().booleanValue() && packet.func_186977_b() == SoundCategory.BLOCKS && packet.func_186978_a() == SoundEvents.field_187539_bB && (AutoCrystal.getInstance().isOff() || !AutoCrystal.getInstance().sound.getValue().booleanValue() && AutoCrystal.getInstance().threadMode.getValue() != AutoCrystal.ThreadMode.SOUND)) {
                NoSoundLag.removeEntities(packet, this.soundRange.getValue().floatValue());
            }
            if (BLACKLIST.contains((Object)packet.func_186978_a()) && this.armor.getValue().booleanValue()) {
                event.setCanceled(true);
            }
        }
    }

    public static void removeEntities(SPacketSoundEffect packet, float range) {
        BlockPos pos = new BlockPos(packet.func_149207_d(), packet.func_149211_e(), packet.func_149210_f());
        ArrayList<Entity> toRemove = new ArrayList<Entity>();
        for (Entity entity : NoSoundLag.mc.field_71441_e.field_72996_f) {
            if (!(entity instanceof EntityEnderCrystal) || !(entity.func_174818_b(pos) <= MathUtil.square(range))) continue;
            toRemove.add(entity);
        }
        for (Entity entity : toRemove) {
            entity.func_70106_y();
        }
    }

    static {
        BLACKLIST = Sets.newHashSet((Object[])new SoundEvent[]{SoundEvents.field_187719_p, SoundEvents.field_191258_p, SoundEvents.field_187716_o, SoundEvents.field_187725_r, SoundEvents.field_187722_q, SoundEvents.field_187713_n, SoundEvents.field_187728_s});
    }
}

