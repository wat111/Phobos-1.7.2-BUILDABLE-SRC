/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.play.client.CPacketEntityAction
 *  net.minecraft.network.play.client.CPacketEntityAction$Action
 *  net.minecraft.network.play.client.CPacketPlayer
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package me.earth.phobos.features.modules.player;

import me.earth.phobos.event.events.PacketEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class NoHunger
extends Module {
    public Setting<Boolean> cancelSprint = this.register(new Setting<Boolean>("CancelSprint", true));

    public NoHunger() {
        super("NoHunger", "Prevents you from getting Hungry", Module.Category.PLAYER, true, false, false);
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        CPacketPlayer packet;
        if (event.getPacket() instanceof CPacketPlayer) {
            packet = (CPacketPlayer)event.getPacket();
            boolean bl = packet.field_149474_g = NoHunger.mc.field_71439_g.field_70143_R >= 0.0f || NoHunger.mc.field_71442_b.field_78778_j;
        }
        if (this.cancelSprint.getValue().booleanValue() && event.getPacket() instanceof CPacketEntityAction && ((packet = (CPacketEntityAction)event.getPacket()).func_180764_b() == CPacketEntityAction.Action.START_SPRINTING || packet.func_180764_b() == CPacketEntityAction.Action.STOP_SPRINTING)) {
            event.setCanceled(true);
        }
    }
}

