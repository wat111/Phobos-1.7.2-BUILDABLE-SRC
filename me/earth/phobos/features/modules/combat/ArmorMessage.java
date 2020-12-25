/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Items
 *  net.minecraft.item.ItemStack
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package me.earth.phobos.features.modules.combat;

import java.util.HashMap;
import java.util.Map;
import me.earth.phobos.Phobos;
import me.earth.phobos.event.events.UpdateWalkingPlayerEvent;
import me.earth.phobos.features.command.Command;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.DamageUtil;
import me.earth.phobos.util.Timer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ArmorMessage
extends Module {
    private final Setting<Integer> armorThreshhold = this.register(new Setting<Integer>("Armor%", 20, 1, 100));
    private final Setting<Boolean> notifySelf = this.register(new Setting<Boolean>("NotifySelf", true));
    private final Setting<Boolean> notification = this.register(new Setting<Boolean>("Notification", true));
    private final Map<EntityPlayer, Integer> entityArmorArraylist = new HashMap<EntityPlayer, Integer>();
    private final Timer timer = new Timer();

    public ArmorMessage() {
        super("ArmorMessage", "Message friends when their armor is low", Module.Category.COMBAT, true, false, false);
    }

    @SubscribeEvent
    public void onUpdate(UpdateWalkingPlayerEvent event) {
        for (EntityPlayer player : ArmorMessage.mc.field_71441_e.field_73010_i) {
            if (player.field_70128_L || !Phobos.friendManager.isFriend(player.func_70005_c_())) continue;
            for (ItemStack stack : player.field_71071_by.field_70460_b) {
                if (stack == ItemStack.field_190927_a) continue;
                int percent = DamageUtil.getRoundedDamage(stack);
                if (percent <= this.armorThreshhold.getValue() && !this.entityArmorArraylist.containsKey((Object)player)) {
                    if (player == ArmorMessage.mc.field_71439_g && this.notifySelf.getValue().booleanValue()) {
                        Command.sendMessage(player.func_70005_c_() + " watchout your " + this.getArmorPieceName(stack) + " low dura!", this.notification.getValue());
                    } else {
                        ArmorMessage.mc.field_71439_g.func_71165_d("/msg " + player.func_70005_c_() + " " + player.func_70005_c_() + " watchout your " + this.getArmorPieceName(stack) + " low dura!");
                    }
                    this.entityArmorArraylist.put(player, player.field_71071_by.field_70460_b.indexOf((Object)stack));
                }
                if (!this.entityArmorArraylist.containsKey((Object)player) || this.entityArmorArraylist.get((Object)player).intValue() != player.field_71071_by.field_70460_b.indexOf((Object)stack) || percent <= this.armorThreshhold.getValue()) continue;
                this.entityArmorArraylist.remove((Object)player);
            }
            if (!this.entityArmorArraylist.containsKey((Object)player) || player.field_71071_by.field_70460_b.get(this.entityArmorArraylist.get((Object)player).intValue()) != ItemStack.field_190927_a) continue;
            this.entityArmorArraylist.remove((Object)player);
        }
    }

    private String getArmorPieceName(ItemStack stack) {
        if (stack.func_77973_b() == Items.field_151161_ac || stack.func_77973_b() == Items.field_151169_ag || stack.func_77973_b() == Items.field_151028_Y || stack.func_77973_b() == Items.field_151020_U || stack.func_77973_b() == Items.field_151024_Q) {
            return "helmet is";
        }
        if (stack.func_77973_b() == Items.field_151163_ad || stack.func_77973_b() == Items.field_151171_ah || stack.func_77973_b() == Items.field_151030_Z || stack.func_77973_b() == Items.field_151023_V || stack.func_77973_b() == Items.field_151027_R) {
            return "chestplate is";
        }
        if (stack.func_77973_b() == Items.field_151173_ae || stack.func_77973_b() == Items.field_151149_ai || stack.func_77973_b() == Items.field_151165_aa || stack.func_77973_b() == Items.field_151022_W || stack.func_77973_b() == Items.field_151026_S) {
            return "leggings are";
        }
        return "boots are";
    }
}

