/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Items
 *  net.minecraft.inventory.EntityEquipmentSlot
 *  net.minecraft.item.ItemElytra
 *  net.minecraft.item.ItemStack
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.CPacketEntityAction
 *  net.minecraft.network.play.client.CPacketEntityAction$Action
 *  net.minecraft.network.play.client.CPacketPlayer
 *  net.minecraft.util.math.MathHelper
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package me.earth.phobos.features.modules.movement;

import me.earth.phobos.Phobos;
import me.earth.phobos.event.events.MoveEvent;
import me.earth.phobos.event.events.PacketEvent;
import me.earth.phobos.event.events.UpdateWalkingPlayerEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.MathUtil;
import me.earth.phobos.util.Timer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemElytra;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ElytraFlight
extends Module {
    public Setting<Mode> mode = this.register(new Setting<Mode>("Mode", Mode.FLY));
    public Setting<Integer> devMode = this.register(new Setting<Object>("Type", 2, 1, 3, v -> this.mode.getValue() == Mode.BYPASS || this.mode.getValue() == Mode.BETTER, "EventMode"));
    public Setting<Float> speed = this.register(new Setting<Object>("Speed", Float.valueOf(1.0f), Float.valueOf(0.0f), Float.valueOf(10.0f), v -> this.mode.getValue() != Mode.FLY && this.mode.getValue() != Mode.BOOST && this.mode.getValue() != Mode.BETTER && this.mode.getValue() != Mode.OHARE, "The Speed."));
    public Setting<Float> vSpeed = this.register(new Setting<Object>("VSpeed", Float.valueOf(0.3f), Float.valueOf(0.0f), Float.valueOf(10.0f), v -> this.mode.getValue() == Mode.BETTER || this.mode.getValue() == Mode.OHARE, "Vertical Speed"));
    public Setting<Float> hSpeed = this.register(new Setting<Object>("HSpeed", Float.valueOf(1.0f), Float.valueOf(0.0f), Float.valueOf(10.0f), v -> this.mode.getValue() == Mode.BETTER || this.mode.getValue() == Mode.OHARE, "Horizontal Speed"));
    public Setting<Float> glide = this.register(new Setting<Object>("Glide", Float.valueOf(1.0E-4f), Float.valueOf(0.0f), Float.valueOf(0.2f), v -> this.mode.getValue() == Mode.BETTER, "Glide Speed"));
    public Setting<Float> tooBeeSpeed = this.register(new Setting<Object>("TooBeeSpeed", Float.valueOf(1.8000001f), Float.valueOf(1.0f), Float.valueOf(2.0f), v -> this.mode.getValue() == Mode.TOOBEE, "Speed for flight on 2b2t"));
    public Setting<Boolean> autoStart = this.register(new Setting<Boolean>("AutoStart", true));
    public Setting<Boolean> disableInLiquid = this.register(new Setting<Boolean>("NoLiquid", true));
    public Setting<Boolean> infiniteDura = this.register(new Setting<Boolean>("InfiniteDura", false));
    public Setting<Boolean> noKick = this.register(new Setting<Object>("NoKick", Boolean.valueOf(false), v -> this.mode.getValue() == Mode.PACKET));
    public Setting<Boolean> allowUp = this.register(new Setting<Object>("AllowUp", Boolean.valueOf(true), v -> this.mode.getValue() == Mode.BETTER));
    public Setting<Boolean> lockPitch = this.register(new Setting<Boolean>("LockPitch", false));
    private static ElytraFlight INSTANCE = new ElytraFlight();
    private final Timer timer = new Timer();
    private final Timer bypassTimer = new Timer();
    private boolean vertical;
    private Double posX;
    private Double flyHeight;
    private Double posZ;

    public ElytraFlight() {
        super("ElytraFlight", "Makes Elytra Flight better.", Module.Category.MOVEMENT, true, false, false);
        this.setInstance();
    }

    private void setInstance() {
        INSTANCE = this;
    }

    public static ElytraFlight getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ElytraFlight();
        }
        return INSTANCE;
    }

    @Override
    public void onEnable() {
        if (this.mode.getValue() == Mode.BETTER && !this.autoStart.getValue().booleanValue() && this.devMode.getValue() == 1) {
            ElytraFlight.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketEntityAction((Entity)ElytraFlight.mc.field_71439_g, CPacketEntityAction.Action.START_FALL_FLYING));
        }
        this.flyHeight = null;
        this.posX = null;
        this.posZ = null;
    }

    @Override
    public String getDisplayInfo() {
        return this.mode.currentEnumName();
    }

    @Override
    public void onUpdate() {
        if (this.mode.getValue() == Mode.BYPASS && this.devMode.getValue() == 1 && ElytraFlight.mc.field_71439_g.func_184613_cA()) {
            ElytraFlight.mc.field_71439_g.field_70159_w = 0.0;
            ElytraFlight.mc.field_71439_g.field_70181_x = -1.0E-4;
            ElytraFlight.mc.field_71439_g.field_70179_y = 0.0;
            double forwardInput = ElytraFlight.mc.field_71439_g.field_71158_b.field_192832_b;
            double strafeInput = ElytraFlight.mc.field_71439_g.field_71158_b.field_78902_a;
            double[] result = this.forwardStrafeYaw(forwardInput, strafeInput, ElytraFlight.mc.field_71439_g.field_70177_z);
            double forward = result[0];
            double strafe = result[1];
            double yaw = result[2];
            if (forwardInput != 0.0 || strafeInput != 0.0) {
                ElytraFlight.mc.field_71439_g.field_70159_w = forward * (double)this.speed.getValue().floatValue() * Math.cos(Math.toRadians(yaw + 90.0)) + strafe * (double)this.speed.getValue().floatValue() * Math.sin(Math.toRadians(yaw + 90.0));
                ElytraFlight.mc.field_71439_g.field_70179_y = forward * (double)this.speed.getValue().floatValue() * Math.sin(Math.toRadians(yaw + 90.0)) - strafe * (double)this.speed.getValue().floatValue() * Math.cos(Math.toRadians(yaw + 90.0));
            }
            if (ElytraFlight.mc.field_71474_y.field_74311_E.func_151470_d()) {
                ElytraFlight.mc.field_71439_g.field_70181_x = -1.0;
            }
        }
    }

    @SubscribeEvent
    public void onSendPacket(PacketEvent.Send event) {
        CPacketPlayer packet;
        if (event.getPacket() instanceof CPacketPlayer && this.mode.getValue() == Mode.TOOBEE) {
            packet = (CPacketPlayer)event.getPacket();
            if (ElytraFlight.mc.field_71439_g.func_184613_cA()) {
                // empty if block
            }
        }
        if (event.getPacket() instanceof CPacketPlayer && this.mode.getValue() == Mode.TOOBEEBYPASS) {
            packet = (CPacketPlayer)event.getPacket();
            if (ElytraFlight.mc.field_71439_g.func_184613_cA()) {
                // empty if block
            }
        }
    }

    @SubscribeEvent
    public void onMove(MoveEvent event) {
        if (this.mode.getValue() == Mode.OHARE) {
            ItemStack itemstack = ElytraFlight.mc.field_71439_g.func_184582_a(EntityEquipmentSlot.CHEST);
            if (itemstack.func_77973_b() == Items.field_185160_cR && ItemElytra.func_185069_d((ItemStack)itemstack) && ElytraFlight.mc.field_71439_g.func_184613_cA()) {
                event.setY(ElytraFlight.mc.field_71474_y.field_74314_A.func_151470_d() ? (double)this.vSpeed.getValue().floatValue() : (ElytraFlight.mc.field_71474_y.field_74311_E.func_151470_d() ? (double)(-this.vSpeed.getValue().floatValue()) : 0.0));
                ElytraFlight.mc.field_71439_g.func_70024_g(0.0, ElytraFlight.mc.field_71474_y.field_74314_A.func_151470_d() ? (double)this.vSpeed.getValue().floatValue() : (ElytraFlight.mc.field_71474_y.field_74311_E.func_151470_d() ? (double)(-this.vSpeed.getValue().floatValue()) : 0.0), 0.0);
                ElytraFlight.mc.field_71439_g.field_184835_a = 0.0f;
                ElytraFlight.mc.field_71439_g.field_184836_b = 0.0f;
                ElytraFlight.mc.field_71439_g.field_184837_c = 0.0f;
                ElytraFlight.mc.field_71439_g.field_70701_bs = ElytraFlight.mc.field_71474_y.field_74314_A.func_151470_d() ? this.vSpeed.getValue().floatValue() : (ElytraFlight.mc.field_71474_y.field_74311_E.func_151470_d() ? -this.vSpeed.getValue().floatValue() : 0.0f);
                double forward = ElytraFlight.mc.field_71439_g.field_71158_b.field_192832_b;
                double strafe = ElytraFlight.mc.field_71439_g.field_71158_b.field_78902_a;
                float yaw = ElytraFlight.mc.field_71439_g.field_70177_z;
                if (forward == 0.0 && strafe == 0.0) {
                    event.setX(0.0);
                    event.setZ(0.0);
                } else {
                    if (forward != 0.0) {
                        if (strafe > 0.0) {
                            yaw += (float)(forward > 0.0 ? -45 : 45);
                        } else if (strafe < 0.0) {
                            yaw += (float)(forward > 0.0 ? 45 : -45);
                        }
                        strafe = 0.0;
                        if (forward > 0.0) {
                            forward = 1.0;
                        } else if (forward < 0.0) {
                            forward = -1.0;
                        }
                    }
                    double cos = Math.cos(Math.toRadians(yaw + 90.0f));
                    double sin = Math.sin(Math.toRadians(yaw + 90.0f));
                    event.setX(forward * (double)this.hSpeed.getValue().floatValue() * cos + strafe * (double)this.hSpeed.getValue().floatValue() * sin);
                    event.setZ(forward * (double)this.hSpeed.getValue().floatValue() * sin - strafe * (double)this.hSpeed.getValue().floatValue() * cos);
                }
            }
        } else if (event.getStage() == 0 && this.mode.getValue() == Mode.BYPASS && this.devMode.getValue() == 3) {
            if (ElytraFlight.mc.field_71439_g.func_184613_cA()) {
                event.setX(0.0);
                event.setY(-1.0E-4);
                event.setZ(0.0);
                double forwardInput = ElytraFlight.mc.field_71439_g.field_71158_b.field_192832_b;
                double strafeInput = ElytraFlight.mc.field_71439_g.field_71158_b.field_78902_a;
                double[] result = this.forwardStrafeYaw(forwardInput, strafeInput, ElytraFlight.mc.field_71439_g.field_70177_z);
                double forward = result[0];
                double strafe = result[1];
                double yaw = result[2];
                if (forwardInput != 0.0 || strafeInput != 0.0) {
                    event.setX(forward * (double)this.speed.getValue().floatValue() * Math.cos(Math.toRadians(yaw + 90.0)) + strafe * (double)this.speed.getValue().floatValue() * Math.sin(Math.toRadians(yaw + 90.0)));
                    event.setY(forward * (double)this.speed.getValue().floatValue() * Math.sin(Math.toRadians(yaw + 90.0)) - strafe * (double)this.speed.getValue().floatValue() * Math.cos(Math.toRadians(yaw + 90.0)));
                }
                if (ElytraFlight.mc.field_71474_y.field_74311_E.func_151470_d()) {
                    event.setY(-1.0);
                }
            }
        } else if (this.mode.getValue() == Mode.TOOBEE) {
            if (!ElytraFlight.mc.field_71439_g.func_184613_cA()) {
                return;
            }
            if (!ElytraFlight.mc.field_71439_g.field_71158_b.field_78901_c) {
                if (ElytraFlight.mc.field_71439_g.field_71158_b.field_78899_d) {
                    ElytraFlight.mc.field_71439_g.field_70181_x = -(this.tooBeeSpeed.getValue().floatValue() / 2.0f);
                    event.setY(-(this.speed.getValue().floatValue() / 2.0f));
                } else if (event.getY() != -1.01E-4) {
                    event.setY(-1.01E-4);
                    ElytraFlight.mc.field_71439_g.field_70181_x = -1.01E-4;
                }
            } else {
                return;
            }
            this.setMoveSpeed(event, this.tooBeeSpeed.getValue().floatValue());
        } else if (this.mode.getValue() == Mode.TOOBEEBYPASS) {
            if (!ElytraFlight.mc.field_71439_g.func_184613_cA()) {
                return;
            }
            if (!ElytraFlight.mc.field_71439_g.field_71158_b.field_78901_c) {
                if (this.lockPitch.getValue().booleanValue()) {
                    ElytraFlight.mc.field_71439_g.field_70125_A = 4.0f;
                }
            } else {
                return;
            }
            if (Phobos.speedManager.getSpeedKpH() > 180.0) {
                return;
            }
            double yaw = Math.toRadians(ElytraFlight.mc.field_71439_g.field_70177_z);
            ElytraFlight.mc.field_71439_g.field_70159_w -= (double)ElytraFlight.mc.field_71439_g.field_71158_b.field_192832_b * Math.sin(yaw) * 0.04;
            ElytraFlight.mc.field_71439_g.field_70179_y += (double)ElytraFlight.mc.field_71439_g.field_71158_b.field_192832_b * Math.cos(yaw) * 0.04;
        }
    }

    private void setMoveSpeed(MoveEvent event, double speed) {
        double forward = ElytraFlight.mc.field_71439_g.field_71158_b.field_192832_b;
        double strafe = ElytraFlight.mc.field_71439_g.field_71158_b.field_78902_a;
        float yaw = ElytraFlight.mc.field_71439_g.field_70177_z;
        if (forward == 0.0 && strafe == 0.0) {
            event.setX(0.0);
            event.setZ(0.0);
            ElytraFlight.mc.field_71439_g.field_70159_w = 0.0;
            ElytraFlight.mc.field_71439_g.field_70179_y = 0.0;
        } else {
            if (forward != 0.0) {
                if (strafe > 0.0) {
                    yaw += (float)(forward > 0.0 ? -45 : 45);
                } else if (strafe < 0.0) {
                    yaw += (float)(forward > 0.0 ? 45 : -45);
                }
                strafe = 0.0;
                if (forward > 0.0) {
                    forward = 1.0;
                } else if (forward < 0.0) {
                    forward = -1.0;
                }
            }
            double x = forward * speed * -Math.sin(Math.toRadians(yaw)) + strafe * speed * Math.cos(Math.toRadians(yaw));
            double z = forward * speed * Math.cos(Math.toRadians(yaw)) - strafe * speed * -Math.sin(Math.toRadians(yaw));
            event.setX(x);
            event.setZ(z);
            ElytraFlight.mc.field_71439_g.field_70159_w = x;
            ElytraFlight.mc.field_71439_g.field_70179_y = z;
        }
    }

    @Override
    public void onTick() {
        if (!ElytraFlight.mc.field_71439_g.func_184613_cA()) {
            return;
        }
        switch (this.mode.getValue()) {
            case BOOST: {
                if (ElytraFlight.mc.field_71439_g.func_70090_H()) {
                    mc.func_147114_u().func_147297_a((Packet)new CPacketEntityAction((Entity)ElytraFlight.mc.field_71439_g, CPacketEntityAction.Action.START_FALL_FLYING));
                    return;
                }
                if (ElytraFlight.mc.field_71474_y.field_74314_A.func_151470_d()) {
                    ElytraFlight.mc.field_71439_g.field_70181_x += 0.08;
                } else if (ElytraFlight.mc.field_71474_y.field_74311_E.func_151470_d()) {
                    ElytraFlight.mc.field_71439_g.field_70181_x -= 0.04;
                }
                if (ElytraFlight.mc.field_71474_y.field_74351_w.func_151470_d()) {
                    float yaw = (float)Math.toRadians(ElytraFlight.mc.field_71439_g.field_70177_z);
                    ElytraFlight.mc.field_71439_g.field_70159_w -= (double)(MathHelper.func_76126_a((float)yaw) * 0.05f);
                    ElytraFlight.mc.field_71439_g.field_70179_y += (double)(MathHelper.func_76134_b((float)yaw) * 0.05f);
                    break;
                }
                if (!ElytraFlight.mc.field_71474_y.field_74368_y.func_151470_d()) break;
                float yaw = (float)Math.toRadians(ElytraFlight.mc.field_71439_g.field_70177_z);
                ElytraFlight.mc.field_71439_g.field_70159_w += (double)(MathHelper.func_76126_a((float)yaw) * 0.05f);
                ElytraFlight.mc.field_71439_g.field_70179_y -= (double)(MathHelper.func_76134_b((float)yaw) * 0.05f);
                break;
            }
            case FLY: {
                ElytraFlight.mc.field_71439_g.field_71075_bZ.field_75100_b = true;
            }
        }
    }

    @SubscribeEvent
    public void onUpdateWalkingPlayer(UpdateWalkingPlayerEvent event) {
        if (ElytraFlight.mc.field_71439_g.func_184582_a(EntityEquipmentSlot.CHEST).func_77973_b() != Items.field_185160_cR) {
            return;
        }
        switch (event.getStage()) {
            case 0: {
                if (this.disableInLiquid.getValue().booleanValue() && (ElytraFlight.mc.field_71439_g.func_70090_H() || ElytraFlight.mc.field_71439_g.func_180799_ab())) {
                    if (ElytraFlight.mc.field_71439_g.func_184613_cA()) {
                        mc.func_147114_u().func_147297_a((Packet)new CPacketEntityAction((Entity)ElytraFlight.mc.field_71439_g, CPacketEntityAction.Action.START_FALL_FLYING));
                    }
                    return;
                }
                if (this.autoStart.getValue().booleanValue() && ElytraFlight.mc.field_71474_y.field_74314_A.func_151470_d() && !ElytraFlight.mc.field_71439_g.func_184613_cA() && ElytraFlight.mc.field_71439_g.field_70181_x < 0.0 && this.timer.passedMs(250L)) {
                    mc.func_147114_u().func_147297_a((Packet)new CPacketEntityAction((Entity)ElytraFlight.mc.field_71439_g, CPacketEntityAction.Action.START_FALL_FLYING));
                    this.timer.reset();
                }
                if (this.mode.getValue() == Mode.BETTER) {
                    double[] dir = MathUtil.directionSpeed(this.devMode.getValue() == 1 ? (double)this.speed.getValue().floatValue() : (double)this.hSpeed.getValue().floatValue());
                    switch (this.devMode.getValue()) {
                        case 1: {
                            ElytraFlight.mc.field_71439_g.func_70016_h(0.0, 0.0, 0.0);
                            ElytraFlight.mc.field_71439_g.field_70747_aH = this.speed.getValue().floatValue();
                            if (ElytraFlight.mc.field_71474_y.field_74314_A.func_151470_d()) {
                                ElytraFlight.mc.field_71439_g.field_70181_x += (double)this.speed.getValue().floatValue();
                            }
                            if (ElytraFlight.mc.field_71474_y.field_74311_E.func_151470_d()) {
                                ElytraFlight.mc.field_71439_g.field_70181_x -= (double)this.speed.getValue().floatValue();
                            }
                            if (ElytraFlight.mc.field_71439_g.field_71158_b.field_78902_a != 0.0f || ElytraFlight.mc.field_71439_g.field_71158_b.field_192832_b != 0.0f) {
                                ElytraFlight.mc.field_71439_g.field_70159_w = dir[0];
                                ElytraFlight.mc.field_71439_g.field_70179_y = dir[1];
                                break;
                            }
                            ElytraFlight.mc.field_71439_g.field_70159_w = 0.0;
                            ElytraFlight.mc.field_71439_g.field_70179_y = 0.0;
                            break;
                        }
                        case 2: {
                            if (ElytraFlight.mc.field_71439_g.func_184613_cA()) {
                                if (this.flyHeight == null) {
                                    this.flyHeight = ElytraFlight.mc.field_71439_g.field_70163_u;
                                }
                            } else {
                                this.flyHeight = null;
                                return;
                            }
                            if (this.noKick.getValue().booleanValue()) {
                                this.flyHeight = this.flyHeight - (double)this.glide.getValue().floatValue();
                            }
                            this.posX = 0.0;
                            this.posZ = 0.0;
                            if (ElytraFlight.mc.field_71439_g.field_71158_b.field_78902_a != 0.0f || ElytraFlight.mc.field_71439_g.field_71158_b.field_192832_b != 0.0f) {
                                this.posX = dir[0];
                                this.posZ = dir[1];
                            }
                            if (ElytraFlight.mc.field_71474_y.field_74314_A.func_151470_d()) {
                                this.flyHeight = ElytraFlight.mc.field_71439_g.field_70163_u + (double)this.vSpeed.getValue().floatValue();
                            }
                            if (ElytraFlight.mc.field_71474_y.field_74311_E.func_151470_d()) {
                                this.flyHeight = ElytraFlight.mc.field_71439_g.field_70163_u - (double)this.vSpeed.getValue().floatValue();
                            }
                            ElytraFlight.mc.field_71439_g.func_70107_b(ElytraFlight.mc.field_71439_g.field_70165_t + this.posX, this.flyHeight.doubleValue(), ElytraFlight.mc.field_71439_g.field_70161_v + this.posZ);
                            ElytraFlight.mc.field_71439_g.func_70016_h(0.0, 0.0, 0.0);
                            break;
                        }
                        case 3: {
                            if (ElytraFlight.mc.field_71439_g.func_184613_cA()) {
                                if (this.flyHeight == null || this.posX == null || this.posX == 0.0 || this.posZ == null || this.posZ == 0.0) {
                                    this.flyHeight = ElytraFlight.mc.field_71439_g.field_70163_u;
                                    this.posX = ElytraFlight.mc.field_71439_g.field_70165_t;
                                    this.posZ = ElytraFlight.mc.field_71439_g.field_70161_v;
                                }
                            } else {
                                this.flyHeight = null;
                                this.posX = null;
                                this.posZ = null;
                                return;
                            }
                            if (this.noKick.getValue().booleanValue()) {
                                this.flyHeight = this.flyHeight - (double)this.glide.getValue().floatValue();
                            }
                            if (ElytraFlight.mc.field_71439_g.field_71158_b.field_78902_a != 0.0f || ElytraFlight.mc.field_71439_g.field_71158_b.field_192832_b != 0.0f) {
                                this.posX = this.posX + dir[0];
                                this.posZ = this.posZ + dir[1];
                            }
                            if (this.allowUp.getValue().booleanValue() && ElytraFlight.mc.field_71474_y.field_74314_A.func_151470_d()) {
                                this.flyHeight = ElytraFlight.mc.field_71439_g.field_70163_u + (double)(this.vSpeed.getValue().floatValue() / 10.0f);
                            }
                            if (ElytraFlight.mc.field_71474_y.field_74311_E.func_151470_d()) {
                                this.flyHeight = ElytraFlight.mc.field_71439_g.field_70163_u - (double)(this.vSpeed.getValue().floatValue() / 10.0f);
                            }
                            ElytraFlight.mc.field_71439_g.func_70107_b(this.posX.doubleValue(), this.flyHeight.doubleValue(), this.posZ.doubleValue());
                            ElytraFlight.mc.field_71439_g.func_70016_h(0.0, 0.0, 0.0);
                        }
                    }
                }
                double rotationYaw = Math.toRadians(ElytraFlight.mc.field_71439_g.field_70177_z);
                if (ElytraFlight.mc.field_71439_g.func_184613_cA()) {
                    switch (this.mode.getValue()) {
                        case VANILLA: {
                            float speedScaled = this.speed.getValue().floatValue() * 0.05f;
                            if (ElytraFlight.mc.field_71474_y.field_74314_A.func_151470_d()) {
                                ElytraFlight.mc.field_71439_g.field_70181_x += (double)speedScaled;
                            }
                            if (ElytraFlight.mc.field_71474_y.field_74311_E.func_151470_d()) {
                                ElytraFlight.mc.field_71439_g.field_70181_x -= (double)speedScaled;
                            }
                            if (ElytraFlight.mc.field_71474_y.field_74351_w.func_151470_d()) {
                                ElytraFlight.mc.field_71439_g.field_70159_w -= Math.sin(rotationYaw) * (double)speedScaled;
                                ElytraFlight.mc.field_71439_g.field_70179_y += Math.cos(rotationYaw) * (double)speedScaled;
                            }
                            if (!ElytraFlight.mc.field_71474_y.field_74368_y.func_151470_d()) break;
                            ElytraFlight.mc.field_71439_g.field_70159_w += Math.sin(rotationYaw) * (double)speedScaled;
                            ElytraFlight.mc.field_71439_g.field_70179_y -= Math.cos(rotationYaw) * (double)speedScaled;
                            break;
                        }
                        case PACKET: {
                            this.freezePlayer((EntityPlayer)ElytraFlight.mc.field_71439_g);
                            this.runNoKick((EntityPlayer)ElytraFlight.mc.field_71439_g);
                            double[] directionSpeedPacket = MathUtil.directionSpeed(this.speed.getValue().floatValue());
                            if (ElytraFlight.mc.field_71439_g.field_71158_b.field_78901_c) {
                                ElytraFlight.mc.field_71439_g.field_70181_x = this.speed.getValue().floatValue();
                            }
                            if (ElytraFlight.mc.field_71439_g.field_71158_b.field_78899_d) {
                                ElytraFlight.mc.field_71439_g.field_70181_x = -this.speed.getValue().floatValue();
                            }
                            if (ElytraFlight.mc.field_71439_g.field_71158_b.field_78902_a != 0.0f || ElytraFlight.mc.field_71439_g.field_71158_b.field_192832_b != 0.0f) {
                                ElytraFlight.mc.field_71439_g.field_70159_w = directionSpeedPacket[0];
                                ElytraFlight.mc.field_71439_g.field_70179_y = directionSpeedPacket[1];
                            }
                            mc.func_147114_u().func_147297_a((Packet)new CPacketEntityAction((Entity)ElytraFlight.mc.field_71439_g, CPacketEntityAction.Action.START_FALL_FLYING));
                            mc.func_147114_u().func_147297_a((Packet)new CPacketEntityAction((Entity)ElytraFlight.mc.field_71439_g, CPacketEntityAction.Action.START_FALL_FLYING));
                            break;
                        }
                        case BYPASS: {
                            if (this.devMode.getValue() != 3) break;
                            if (ElytraFlight.mc.field_71474_y.field_74314_A.func_151470_d()) {
                                ElytraFlight.mc.field_71439_g.field_70181_x = 0.02f;
                            }
                            if (ElytraFlight.mc.field_71474_y.field_74311_E.func_151470_d()) {
                                ElytraFlight.mc.field_71439_g.field_70181_x = -0.2f;
                            }
                            if (ElytraFlight.mc.field_71439_g.field_70173_aa % 8 == 0 && ElytraFlight.mc.field_71439_g.field_70163_u <= 240.0) {
                                ElytraFlight.mc.field_71439_g.field_70181_x = 0.02f;
                            }
                            ElytraFlight.mc.field_71439_g.field_71075_bZ.field_75100_b = true;
                            ElytraFlight.mc.field_71439_g.field_71075_bZ.func_75092_a(0.025f);
                            double[] directionSpeedBypass = MathUtil.directionSpeed(0.52f);
                            if (ElytraFlight.mc.field_71439_g.field_71158_b.field_78902_a != 0.0f || ElytraFlight.mc.field_71439_g.field_71158_b.field_192832_b != 0.0f) {
                                ElytraFlight.mc.field_71439_g.field_70159_w = directionSpeedBypass[0];
                                ElytraFlight.mc.field_71439_g.field_70179_y = directionSpeedBypass[1];
                                break;
                            }
                            ElytraFlight.mc.field_71439_g.field_70159_w = 0.0;
                            ElytraFlight.mc.field_71439_g.field_70179_y = 0.0;
                        }
                    }
                }
                if (!this.infiniteDura.getValue().booleanValue()) break;
                ElytraFlight.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketEntityAction((Entity)ElytraFlight.mc.field_71439_g, CPacketEntityAction.Action.START_FALL_FLYING));
                break;
            }
            case 1: {
                if (!this.infiniteDura.getValue().booleanValue()) break;
                ElytraFlight.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketEntityAction((Entity)ElytraFlight.mc.field_71439_g, CPacketEntityAction.Action.START_FALL_FLYING));
            }
        }
    }

    private double[] forwardStrafeYaw(double forward, double strafe, double yaw) {
        double[] result = new double[]{forward, strafe, yaw};
        if ((forward != 0.0 || strafe != 0.0) && forward != 0.0) {
            if (strafe > 0.0) {
                result[2] = result[2] + (double)(forward > 0.0 ? -45 : 45);
            } else if (strafe < 0.0) {
                result[2] = result[2] + (double)(forward > 0.0 ? 45 : -45);
            }
            result[1] = 0.0;
            if (forward > 0.0) {
                result[0] = 1.0;
            } else if (forward < 0.0) {
                result[0] = -1.0;
            }
        }
        return result;
    }

    private void freezePlayer(EntityPlayer player) {
        player.field_70159_w = 0.0;
        player.field_70181_x = 0.0;
        player.field_70179_y = 0.0;
    }

    private void runNoKick(EntityPlayer player) {
        if (this.noKick.getValue().booleanValue() && !player.func_184613_cA() && player.field_70173_aa % 4 == 0) {
            player.field_70181_x = -0.04f;
        }
    }

    @Override
    public void onDisable() {
        if (ElytraFlight.fullNullCheck() || ElytraFlight.mc.field_71439_g.field_71075_bZ.field_75098_d) {
            return;
        }
        ElytraFlight.mc.field_71439_g.field_71075_bZ.field_75100_b = false;
    }

    public static enum Mode {
        VANILLA,
        PACKET,
        BOOST,
        FLY,
        BYPASS,
        BETTER,
        OHARE,
        TOOBEE,
        TOOBEEBYPASS;

    }
}

