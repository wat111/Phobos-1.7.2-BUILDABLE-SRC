/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.init.Blocks
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.server.SPacketBlockChange
 *  net.minecraft.network.play.server.SPacketDisconnect
 *  net.minecraft.util.text.ITextComponent
 *  net.minecraft.util.text.TextComponentString
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package me.earth.phobos.features.modules.misc;

import me.earth.phobos.Phobos;
import me.earth.phobos.event.events.PacketEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.MathUtil;
import net.minecraft.init.Blocks;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.network.play.server.SPacketDisconnect;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AutoLog
extends Module {
    private Setting<Float> health = this.register(new Setting<Float>("Health", Float.valueOf(16.0f), Float.valueOf(0.1f), Float.valueOf(36.0f)));
    private Setting<Boolean> bed = this.register(new Setting<Boolean>("Beds", true));
    private Setting<Float> range = this.register(new Setting<Object>("BedRange", Float.valueOf(6.0f), Float.valueOf(0.1f), Float.valueOf(36.0f), v -> this.bed.getValue()));
    private Setting<Boolean> logout = this.register(new Setting<Boolean>("LogoutOff", true));
    private static AutoLog INSTANCE = new AutoLog();

    public AutoLog() {
        super("AutoLog", "Logs when in danger.", Module.Category.MISC, false, false, false);
        this.setInstance();
    }

    private void setInstance() {
        INSTANCE = this;
    }

    public static AutoLog getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new AutoLog();
        }
        return INSTANCE;
    }

    @Override
    public void onTick() {
        if (!AutoLog.nullCheck() && AutoLog.mc.field_71439_g.func_110143_aJ() <= this.health.getValue().floatValue()) {
            Phobos.moduleManager.disableModule("AutoReconnect");
            AutoLog.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new SPacketDisconnect((ITextComponent)new TextComponentString("AutoLogged")));
            if (this.logout.getValue().booleanValue()) {
                this.disable();
            }
        }
    }

    @SubscribeEvent
    public void onReceivePacket(PacketEvent.Receive event) {
        SPacketBlockChange packet;
        if (event.getPacket() instanceof SPacketBlockChange && this.bed.getValue().booleanValue() && (packet = (SPacketBlockChange)event.getPacket()).func_180728_a().func_177230_c() == Blocks.field_150324_C && AutoLog.mc.field_71439_g.func_174831_c(packet.func_179827_b()) <= MathUtil.square(this.range.getValue().floatValue())) {
            Phobos.moduleManager.disableModule("AutoReconnect");
            AutoLog.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new SPacketDisconnect((ITextComponent)new TextComponentString("AutoLogged")));
            if (this.logout.getValue().booleanValue()) {
                this.disable();
            }
        }
    }
}

