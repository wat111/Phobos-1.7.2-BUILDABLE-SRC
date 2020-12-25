/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.util.MovementInput
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package me.earth.phobos.features.modules.movement;

import java.util.Random;
import me.earth.phobos.Phobos;
import me.earth.phobos.event.events.ClientEvent;
import me.earth.phobos.event.events.MoveEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.BlockUtil;
import me.earth.phobos.util.EntityUtil;
import me.earth.phobos.util.MathUtil;
import net.minecraft.entity.Entity;
import net.minecraft.util.MovementInput;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Speed
extends Module {
    public Setting<Mode> mode = this.register(new Setting<Mode>("Mode", Mode.INSTANT));
    public Setting<Boolean> strafeJump = this.register(new Setting<Object>("Jump", Boolean.valueOf(false), v -> this.mode.getValue() == Mode.INSTANT));
    public Setting<Boolean> noShake = this.register(new Setting<Object>("NoShake", Boolean.valueOf(true), v -> this.mode.getValue() != Mode.INSTANT));
    public Setting<Boolean> useTimer = this.register(new Setting<Object>("UseTimer", Boolean.valueOf(false), v -> this.mode.getValue() != Mode.INSTANT));
    private static Speed INSTANCE = new Speed();
    private double highChainVal = 0.0;
    private double lowChainVal = 0.0;
    private boolean oneTime = false;
    public double startY = 0.0;
    public boolean antiShake = false;
    private double bounceHeight = 0.4;
    private float move = 0.26f;

    public Speed() {
        super("Speed", "Makes you faster", Module.Category.MOVEMENT, true, false, false);
        this.setInstance();
    }

    private void setInstance() {
        INSTANCE = this;
    }

    public static Speed getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Speed();
        }
        return INSTANCE;
    }

    private boolean shouldReturn() {
        return Phobos.moduleManager.isModuleEnabled("Freecam") || Phobos.moduleManager.isModuleEnabled("Phase") || Phobos.moduleManager.isModuleEnabled("ElytraFlight") || Phobos.moduleManager.isModuleEnabled("Strafe") || Phobos.moduleManager.isModuleEnabled("Flight");
    }

    @Override
    public void onUpdate() {
        if (this.shouldReturn() || Speed.mc.field_71439_g.func_70093_af() || Speed.mc.field_71439_g.func_70090_H() || Speed.mc.field_71439_g.func_180799_ab()) {
            return;
        }
        switch (this.mode.getValue()) {
            case BOOST: {
                this.doBoost();
                break;
            }
            case ACCEL: {
                this.doAccel();
                break;
            }
            case ONGROUND: {
                this.doOnground();
                break;
            }
        }
    }

    private void doBoost() {
        this.bounceHeight = 0.4;
        this.move = 0.26f;
        if (Speed.mc.field_71439_g.field_70122_E) {
            this.startY = Speed.mc.field_71439_g.field_70163_u;
        }
        if (EntityUtil.getEntitySpeed((Entity)Speed.mc.field_71439_g) <= 1.0) {
            this.lowChainVal = 1.0;
            this.highChainVal = 1.0;
        }
        if (EntityUtil.isEntityMoving((Entity)Speed.mc.field_71439_g) && !Speed.mc.field_71439_g.field_70123_F && !BlockUtil.isBlockAboveEntitySolid((Entity)Speed.mc.field_71439_g) && BlockUtil.isBlockBelowEntitySolid((Entity)Speed.mc.field_71439_g)) {
            this.oneTime = true;
            this.antiShake = this.noShake.getValue() != false && Speed.mc.field_71439_g.func_184187_bx() == null;
            Random random = new Random();
            boolean rnd = random.nextBoolean();
            if (Speed.mc.field_71439_g.field_70163_u >= this.startY + this.bounceHeight) {
                Speed.mc.field_71439_g.field_70181_x = -this.bounceHeight;
                this.lowChainVal += 1.0;
                if (this.lowChainVal == 1.0) {
                    this.move = 0.075f;
                }
                if (this.lowChainVal == 2.0) {
                    this.move = 0.15f;
                }
                if (this.lowChainVal == 3.0) {
                    this.move = 0.175f;
                }
                if (this.lowChainVal == 4.0) {
                    this.move = 0.2f;
                }
                if (this.lowChainVal == 5.0) {
                    this.move = 0.225f;
                }
                if (this.lowChainVal == 6.0) {
                    this.move = 0.25f;
                }
                if (this.lowChainVal >= 7.0) {
                    this.move = 0.27895f;
                }
                if (this.useTimer.getValue().booleanValue()) {
                    Phobos.timerManager.setTimer(1.0f);
                }
            }
            if (Speed.mc.field_71439_g.field_70163_u == this.startY) {
                Speed.mc.field_71439_g.field_70181_x = this.bounceHeight;
                this.highChainVal += 1.0;
                if (this.highChainVal == 1.0) {
                    this.move = 0.075f;
                }
                if (this.highChainVal == 2.0) {
                    this.move = 0.175f;
                }
                if (this.highChainVal == 3.0) {
                    this.move = 0.325f;
                }
                if (this.highChainVal == 4.0) {
                    this.move = 0.375f;
                }
                if (this.highChainVal == 5.0) {
                    this.move = 0.4f;
                }
                if (this.highChainVal >= 6.0) {
                    this.move = 0.43395f;
                }
                if (this.useTimer.getValue().booleanValue()) {
                    if (rnd) {
                        Phobos.timerManager.setTimer(1.3f);
                    } else {
                        Phobos.timerManager.setTimer(1.0f);
                    }
                }
            }
            EntityUtil.moveEntityStrafe(this.move, (Entity)Speed.mc.field_71439_g);
        } else {
            if (this.oneTime) {
                Speed.mc.field_71439_g.field_70181_x = -0.1;
                this.oneTime = false;
            }
            this.highChainVal = 0.0;
            this.lowChainVal = 0.0;
            this.antiShake = false;
            this.speedOff();
        }
    }

    private void doAccel() {
        this.bounceHeight = 0.4;
        this.move = 0.26f;
        if (Speed.mc.field_71439_g.field_70122_E) {
            this.startY = Speed.mc.field_71439_g.field_70163_u;
        }
        if (EntityUtil.getEntitySpeed((Entity)Speed.mc.field_71439_g) <= 1.0) {
            this.lowChainVal = 1.0;
            this.highChainVal = 1.0;
        }
        if (EntityUtil.isEntityMoving((Entity)Speed.mc.field_71439_g) && !Speed.mc.field_71439_g.field_70123_F && !BlockUtil.isBlockAboveEntitySolid((Entity)Speed.mc.field_71439_g) && BlockUtil.isBlockBelowEntitySolid((Entity)Speed.mc.field_71439_g)) {
            this.oneTime = true;
            this.antiShake = this.noShake.getValue() != false && Speed.mc.field_71439_g.func_184187_bx() == null;
            Random random = new Random();
            boolean rnd = random.nextBoolean();
            if (Speed.mc.field_71439_g.field_70163_u >= this.startY + this.bounceHeight) {
                Speed.mc.field_71439_g.field_70181_x = -this.bounceHeight;
                this.lowChainVal += 1.0;
                if (this.lowChainVal == 1.0) {
                    this.move = 0.075f;
                }
                if (this.lowChainVal == 2.0) {
                    this.move = 0.175f;
                }
                if (this.lowChainVal == 3.0) {
                    this.move = 0.275f;
                }
                if (this.lowChainVal == 4.0) {
                    this.move = 0.35f;
                }
                if (this.lowChainVal == 5.0) {
                    this.move = 0.375f;
                }
                if (this.lowChainVal == 6.0) {
                    this.move = 0.4f;
                }
                if (this.lowChainVal == 7.0) {
                    this.move = 0.425f;
                }
                if (this.lowChainVal == 8.0) {
                    this.move = 0.45f;
                }
                if (this.lowChainVal == 9.0) {
                    this.move = 0.475f;
                }
                if (this.lowChainVal == 10.0) {
                    this.move = 0.5f;
                }
                if (this.lowChainVal == 11.0) {
                    this.move = 0.5f;
                }
                if (this.lowChainVal == 12.0) {
                    this.move = 0.525f;
                }
                if (this.lowChainVal == 13.0) {
                    this.move = 0.525f;
                }
                if (this.lowChainVal == 14.0) {
                    this.move = 0.535f;
                }
                if (this.lowChainVal == 15.0) {
                    this.move = 0.535f;
                }
                if (this.lowChainVal == 16.0) {
                    this.move = 0.545f;
                }
                if (this.lowChainVal >= 17.0) {
                    this.move = 0.545f;
                }
                if (this.useTimer.getValue().booleanValue()) {
                    Phobos.timerManager.setTimer(1.0f);
                }
            }
            if (Speed.mc.field_71439_g.field_70163_u == this.startY) {
                Speed.mc.field_71439_g.field_70181_x = this.bounceHeight;
                this.highChainVal += 1.0;
                if (this.highChainVal == 1.0) {
                    this.move = 0.075f;
                }
                if (this.highChainVal == 2.0) {
                    this.move = 0.175f;
                }
                if (this.highChainVal == 3.0) {
                    this.move = 0.375f;
                }
                if (this.highChainVal == 4.0) {
                    this.move = 0.6f;
                }
                if (this.highChainVal == 5.0) {
                    this.move = 0.775f;
                }
                if (this.highChainVal == 6.0) {
                    this.move = 0.825f;
                }
                if (this.highChainVal == 7.0) {
                    this.move = 0.875f;
                }
                if (this.highChainVal == 8.0) {
                    this.move = 0.925f;
                }
                if (this.highChainVal == 9.0) {
                    this.move = 0.975f;
                }
                if (this.highChainVal == 10.0) {
                    this.move = 1.05f;
                }
                if (this.highChainVal == 11.0) {
                    this.move = 1.1f;
                }
                if (this.highChainVal == 12.0) {
                    this.move = 1.1f;
                }
                if (this.highChainVal == 13.0) {
                    this.move = 1.15f;
                }
                if (this.highChainVal == 14.0) {
                    this.move = 1.15f;
                }
                if (this.highChainVal == 15.0) {
                    this.move = 1.175f;
                }
                if (this.highChainVal == 16.0) {
                    this.move = 1.175f;
                }
                if (this.highChainVal >= 17.0) {
                    this.move = 1.175f;
                }
                if (this.useTimer.getValue().booleanValue()) {
                    if (rnd) {
                        Phobos.timerManager.setTimer(1.3f);
                    } else {
                        Phobos.timerManager.setTimer(1.0f);
                    }
                }
            }
            EntityUtil.moveEntityStrafe(this.move, (Entity)Speed.mc.field_71439_g);
        } else {
            if (this.oneTime) {
                Speed.mc.field_71439_g.field_70181_x = -0.1;
                this.oneTime = false;
            }
            this.antiShake = false;
            this.highChainVal = 0.0;
            this.lowChainVal = 0.0;
            this.speedOff();
        }
    }

    private void doOnground() {
        this.bounceHeight = 0.4;
        this.move = 0.26f;
        if (Speed.mc.field_71439_g.field_70122_E) {
            this.startY = Speed.mc.field_71439_g.field_70163_u;
        }
        if (EntityUtil.getEntitySpeed((Entity)Speed.mc.field_71439_g) <= 1.0) {
            this.lowChainVal = 1.0;
            this.highChainVal = 1.0;
        }
        if (EntityUtil.isEntityMoving((Entity)Speed.mc.field_71439_g) && !Speed.mc.field_71439_g.field_70123_F && !BlockUtil.isBlockAboveEntitySolid((Entity)Speed.mc.field_71439_g) && BlockUtil.isBlockBelowEntitySolid((Entity)Speed.mc.field_71439_g)) {
            this.oneTime = true;
            this.antiShake = this.noShake.getValue() != false && Speed.mc.field_71439_g.func_184187_bx() == null;
            Random random = new Random();
            boolean rnd = random.nextBoolean();
            if (Speed.mc.field_71439_g.field_70163_u >= this.startY + this.bounceHeight) {
                Speed.mc.field_71439_g.field_70181_x = -this.bounceHeight;
                this.lowChainVal += 1.0;
                if (this.lowChainVal == 1.0) {
                    this.move = 0.075f;
                }
                if (this.lowChainVal == 2.0) {
                    this.move = 0.175f;
                }
                if (this.lowChainVal == 3.0) {
                    this.move = 0.275f;
                }
                if (this.lowChainVal == 4.0) {
                    this.move = 0.35f;
                }
                if (this.lowChainVal == 5.0) {
                    this.move = 0.375f;
                }
                if (this.lowChainVal == 6.0) {
                    this.move = 0.4f;
                }
                if (this.lowChainVal == 7.0) {
                    this.move = 0.425f;
                }
                if (this.lowChainVal == 8.0) {
                    this.move = 0.45f;
                }
                if (this.lowChainVal == 9.0) {
                    this.move = 0.475f;
                }
                if (this.lowChainVal == 10.0) {
                    this.move = 0.5f;
                }
                if (this.lowChainVal == 11.0) {
                    this.move = 0.5f;
                }
                if (this.lowChainVal == 12.0) {
                    this.move = 0.525f;
                }
                if (this.lowChainVal == 13.0) {
                    this.move = 0.525f;
                }
                if (this.lowChainVal == 14.0) {
                    this.move = 0.535f;
                }
                if (this.lowChainVal == 15.0) {
                    this.move = 0.535f;
                }
                if (this.lowChainVal == 16.0) {
                    this.move = 0.545f;
                }
                if (this.lowChainVal >= 17.0) {
                    this.move = 0.545f;
                }
                if (this.useTimer.getValue().booleanValue()) {
                    Phobos.timerManager.setTimer(1.0f);
                }
            }
            if (Speed.mc.field_71439_g.field_70163_u == this.startY) {
                Speed.mc.field_71439_g.field_70181_x = this.bounceHeight;
                this.highChainVal += 1.0;
                if (this.highChainVal == 1.0) {
                    this.move = 0.075f;
                }
                if (this.highChainVal == 2.0) {
                    this.move = 0.175f;
                }
                if (this.highChainVal == 3.0) {
                    this.move = 0.375f;
                }
                if (this.highChainVal == 4.0) {
                    this.move = 0.6f;
                }
                if (this.highChainVal == 5.0) {
                    this.move = 0.775f;
                }
                if (this.highChainVal == 6.0) {
                    this.move = 0.825f;
                }
                if (this.highChainVal == 7.0) {
                    this.move = 0.875f;
                }
                if (this.highChainVal == 8.0) {
                    this.move = 0.925f;
                }
                if (this.highChainVal == 9.0) {
                    this.move = 0.975f;
                }
                if (this.highChainVal == 10.0) {
                    this.move = 1.05f;
                }
                if (this.highChainVal == 11.0) {
                    this.move = 1.1f;
                }
                if (this.highChainVal == 12.0) {
                    this.move = 1.1f;
                }
                if (this.highChainVal == 13.0) {
                    this.move = 1.15f;
                }
                if (this.highChainVal == 14.0) {
                    this.move = 1.15f;
                }
                if (this.highChainVal == 15.0) {
                    this.move = 1.175f;
                }
                if (this.highChainVal == 16.0) {
                    this.move = 1.175f;
                }
                if (this.highChainVal >= 17.0) {
                    this.move = 1.2f;
                }
                if (this.useTimer.getValue().booleanValue()) {
                    if (rnd) {
                        Phobos.timerManager.setTimer(1.3f);
                    } else {
                        Phobos.timerManager.setTimer(1.0f);
                    }
                }
            }
            EntityUtil.moveEntityStrafe(this.move, (Entity)Speed.mc.field_71439_g);
        } else {
            if (this.oneTime) {
                Speed.mc.field_71439_g.field_70181_x = -0.1;
                this.oneTime = false;
            }
            this.antiShake = false;
            this.highChainVal = 0.0;
            this.lowChainVal = 0.0;
            this.speedOff();
        }
    }

    @Override
    public void onDisable() {
        if (this.mode.getValue() == Mode.ONGROUND || this.mode.getValue() == Mode.BOOST) {
            Speed.mc.field_71439_g.field_70181_x = -0.1;
        }
        Phobos.timerManager.setTimer(1.0f);
        this.highChainVal = 0.0;
        this.lowChainVal = 0.0;
        this.antiShake = false;
    }

    @SubscribeEvent
    public void onSettingChange(ClientEvent event) {
        if (event.getStage() == 2 && event.getSetting().equals(this.mode) && this.mode.getPlannedValue() == Mode.INSTANT) {
            Speed.mc.field_71439_g.field_70181_x = -0.1;
        }
    }

    @Override
    public String getDisplayInfo() {
        return this.mode.currentEnumName();
    }

    @SubscribeEvent
    public void onMode(MoveEvent event) {
        if (!(this.shouldReturn() || event.getStage() != 0 || this.mode.getValue() != Mode.INSTANT || Speed.nullCheck() || Speed.mc.field_71439_g.func_70093_af() || Speed.mc.field_71439_g.func_70090_H() || Speed.mc.field_71439_g.func_180799_ab() || Speed.mc.field_71439_g.field_71158_b.field_192832_b == 0.0f && Speed.mc.field_71439_g.field_71158_b.field_78902_a == 0.0f)) {
            if (Speed.mc.field_71439_g.field_70122_E && this.strafeJump.getValue().booleanValue()) {
                Speed.mc.field_71439_g.field_70181_x = 0.4;
                event.setY(0.4);
            }
            MovementInput movementInput = Speed.mc.field_71439_g.field_71158_b;
            float moveForward = movementInput.field_192832_b;
            float moveStrafe = movementInput.field_78902_a;
            float rotationYaw = Speed.mc.field_71439_g.field_70177_z;
            if ((double)moveForward == 0.0 && (double)moveStrafe == 0.0) {
                event.setX(0.0);
                event.setZ(0.0);
            } else {
                if ((double)moveForward != 0.0) {
                    if ((double)moveStrafe > 0.0) {
                        rotationYaw += (float)((double)moveForward > 0.0 ? -45 : 45);
                    } else if ((double)moveStrafe < 0.0) {
                        rotationYaw += (float)((double)moveForward > 0.0 ? 45 : -45);
                    }
                    moveStrafe = 0.0f;
                    float f = moveForward == 0.0f ? moveForward : (moveForward = (double)moveForward > 0.0 ? 1.0f : -1.0f);
                }
                moveStrafe = moveStrafe == 0.0f ? moveStrafe : ((double)moveStrafe > 0.0 ? 1.0f : -1.0f);
                event.setX((double)moveForward * EntityUtil.getMaxSpeed() * Math.cos(Math.toRadians(rotationYaw + 90.0f)) + (double)moveStrafe * EntityUtil.getMaxSpeed() * Math.sin(Math.toRadians(rotationYaw + 90.0f)));
                event.setZ((double)moveForward * EntityUtil.getMaxSpeed() * Math.sin(Math.toRadians(rotationYaw + 90.0f)) - (double)moveStrafe * EntityUtil.getMaxSpeed() * Math.cos(Math.toRadians(rotationYaw + 90.0f)));
            }
        }
    }

    private void speedOff() {
        float yaw = (float)Math.toRadians(Speed.mc.field_71439_g.field_70177_z);
        if (BlockUtil.isBlockAboveEntitySolid((Entity)Speed.mc.field_71439_g)) {
            if (Speed.mc.field_71474_y.field_74351_w.func_151470_d() && !Speed.mc.field_71474_y.field_74311_E.func_151470_d() && Speed.mc.field_71439_g.field_70122_E) {
                Speed.mc.field_71439_g.field_70159_w -= (double)MathUtil.sin(yaw) * 0.15;
                Speed.mc.field_71439_g.field_70179_y += (double)MathUtil.cos(yaw) * 0.15;
            }
        } else if (Speed.mc.field_71439_g.field_70123_F) {
            if (Speed.mc.field_71474_y.field_74351_w.func_151470_d() && !Speed.mc.field_71474_y.field_74311_E.func_151470_d() && Speed.mc.field_71439_g.field_70122_E) {
                Speed.mc.field_71439_g.field_70159_w -= (double)MathUtil.sin(yaw) * 0.03;
                Speed.mc.field_71439_g.field_70179_y += (double)MathUtil.cos(yaw) * 0.03;
            }
        } else if (!BlockUtil.isBlockBelowEntitySolid((Entity)Speed.mc.field_71439_g)) {
            if (Speed.mc.field_71474_y.field_74351_w.func_151470_d() && !Speed.mc.field_71474_y.field_74311_E.func_151470_d() && Speed.mc.field_71439_g.field_70122_E) {
                Speed.mc.field_71439_g.field_70159_w -= (double)MathUtil.sin(yaw) * 0.03;
                Speed.mc.field_71439_g.field_70179_y += (double)MathUtil.cos(yaw) * 0.03;
            }
        } else {
            Speed.mc.field_71439_g.field_70159_w = 0.0;
            Speed.mc.field_71439_g.field_70179_y = 0.0;
        }
    }

    public static enum Mode {
        INSTANT,
        ONGROUND,
        ACCEL,
        BOOST;

    }
}

