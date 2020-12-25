/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiChat
 *  net.minecraft.client.gui.ScaledResolution
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.init.Items
 *  net.minecraft.init.MobEffects
 *  net.minecraft.item.ItemStack
 *  net.minecraft.potion.Potion
 *  net.minecraft.potion.PotionEffect
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.SoundEvent
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package me.earth.phobos.features.modules.client;

import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import me.earth.phobos.Phobos;
import me.earth.phobos.event.events.ClientEvent;
import me.earth.phobos.event.events.Render2DEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.modules.client.Colors;
import me.earth.phobos.features.modules.client.Managers;
import me.earth.phobos.features.modules.client.ServerModule;
import me.earth.phobos.features.modules.misc.ToolTips;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.ColorUtil;
import me.earth.phobos.util.EntityUtil;
import me.earth.phobos.util.MathUtil;
import me.earth.phobos.util.RenderUtil;
import me.earth.phobos.util.Timer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class HUD
extends Module {
    private final Setting<Boolean> renderingUp = this.register(new Setting<Boolean>("RenderingUp", Boolean.valueOf(false), "Orientation of the HUD-Elements."));
    public Setting<Boolean> colorSync = this.register(new Setting<Boolean>("Sync", Boolean.valueOf(false), "Universal colors for hud."));
    public Setting<Boolean> rainbow = this.register(new Setting<Boolean>("Rainbow", Boolean.valueOf(false), "Rainbow hud."));
    public Setting<Integer> factor = this.register(new Setting<Object>("Factor", Integer.valueOf(1), Integer.valueOf(0), Integer.valueOf(20), v -> this.rainbow.getValue()));
    public Setting<Boolean> rolling = this.register(new Setting<Object>("Rolling", Boolean.valueOf(false), v -> this.rainbow.getValue()));
    public Setting<Boolean> staticRainbow = this.register(new Setting<Object>("Static", Boolean.valueOf(false), v -> this.rainbow.getValue()));
    public Setting<Integer> rainbowSpeed = this.register(new Setting<Object>("RSpeed", Integer.valueOf(20), Integer.valueOf(0), Integer.valueOf(100), v -> this.rainbow.getValue()));
    public Setting<Integer> rainbowSaturation = this.register(new Setting<Object>("Saturation", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> this.rainbow.getValue()));
    public Setting<Integer> rainbowBrightness = this.register(new Setting<Object>("Brightness", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> this.rainbow.getValue()));
    public Setting<Boolean> potionIcons = this.register(new Setting<Boolean>("PotionIcons", Boolean.valueOf(true), "Draws Potion Icons."));
    public Setting<Boolean> shadow = this.register(new Setting<Boolean>("Shadow", Boolean.valueOf(false), "Draws the text with a shadow."));
    private final Setting<WaterMark> watermark = this.register(new Setting<WaterMark>("Logo", WaterMark.NONE, "WaterMark"));
    private final Setting<String> customWatermark = this.register(new Setting<String>("WatermarkName", "megyn.club b1"));
    private final Setting<Boolean> modeVer = this.register(new Setting<Object>("Version", Boolean.valueOf(false), v -> this.watermark.getValue() != WaterMark.NONE));
    private final Setting<Boolean> arrayList = this.register(new Setting<Boolean>("ActiveModules", Boolean.valueOf(false), "Lists the active modules."));
    private final Setting<Boolean> moduleColors = this.register(new Setting<Object>("ModuleColors", Boolean.valueOf(false), v -> this.arrayList.getValue()));
    public Setting<Integer> animationHorizontalTime = this.register(new Setting<Object>("AnimationHTime", Integer.valueOf(500), Integer.valueOf(1), Integer.valueOf(1000), v -> this.arrayList.getValue()));
    public Setting<Integer> animationVerticalTime = this.register(new Setting<Object>("AnimationVTime", Integer.valueOf(50), Integer.valueOf(1), Integer.valueOf(500), v -> this.arrayList.getValue()));
    private final Setting<Boolean> alphabeticalSorting = this.register(new Setting<Object>("AlphabeticalSorting", Boolean.valueOf(false), v -> this.arrayList.getValue()));
    private final Setting<Boolean> serverBrand = this.register(new Setting<Boolean>("ServerBrand", Boolean.valueOf(false), "Brand of the server you are on."));
    private final Setting<Boolean> ping = this.register(new Setting<Boolean>("Ping", Boolean.valueOf(false), "Your response time to the server."));
    private final Setting<Boolean> tps = this.register(new Setting<Boolean>("TPS", Boolean.valueOf(false), "Ticks per second of the server."));
    private final Setting<Boolean> fps = this.register(new Setting<Boolean>("FPS", Boolean.valueOf(false), "Your frames per second."));
    private final Setting<Boolean> coords = this.register(new Setting<Boolean>("Coords", Boolean.valueOf(false), "Your current coordinates"));
    private final Setting<Boolean> direction = this.register(new Setting<Boolean>("Direction", Boolean.valueOf(false), "The Direction you are facing."));
    private final Setting<Boolean> speed = this.register(new Setting<Boolean>("Speed", Boolean.valueOf(false), "Your Speed"));
    private final Setting<Boolean> potions = this.register(new Setting<Boolean>("Potions", Boolean.valueOf(false), "Active potion effects"));
    private final Setting<Boolean> altPotionsColors = this.register(new Setting<Object>("AltPotionColors", Boolean.valueOf(false), v -> this.potions.getValue()));
    public Setting<Boolean> textRadar = this.register(new Setting<Boolean>("TextRadar", Boolean.valueOf(false), "A TextRadar"));
    private final Setting<Boolean> armor = this.register(new Setting<Boolean>("Armor", Boolean.valueOf(false), "ArmorHUD"));
    private final Setting<Boolean> durability = this.register(new Setting<Boolean>("Durability", Boolean.valueOf(false), "Durability"));
    private final Setting<Boolean> percent = this.register(new Setting<Object>("Percent", Boolean.valueOf(true), v -> this.armor.getValue()));
    private final Setting<Boolean> totems = this.register(new Setting<Boolean>("Totems", Boolean.valueOf(false), "TotemHUD"));
    private final Setting<Boolean> queue = this.register(new Setting<Boolean>("2b2tQueue", Boolean.valueOf(false), "Shows the 2b2t queue."));
    private final Setting<Greeter> greeter = this.register(new Setting<Greeter>("Greeter", Greeter.NONE, "Greets you."));
    private final Setting<String> spoofGreeter = this.register(new Setting<Object>("GreeterName", "3arthqu4ke", v -> this.greeter.getValue() == Greeter.CUSTOM));
    public Setting<Boolean> time = this.register(new Setting<Boolean>("Time", Boolean.valueOf(false), "The time"));
    private final Setting<LagNotify> lag = this.register(new Setting<LagNotify>("Lag", LagNotify.GRAY, "Lag Notifier"));
    private final Setting<Boolean> hitMarkers = this.register(new Setting<Boolean>("HitMarkers", true));
    private final Setting<Sound> sound = this.register(new Setting<Object>("Sound", (Object)Sound.NONE, v -> this.hitMarkers.getValue()));
    public Setting<Integer> hudRed = this.register(new Setting<Object>("Red", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> this.rainbow.getValue() == false));
    public Setting<Integer> hudGreen = this.register(new Setting<Object>("Green", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> this.rainbow.getValue() == false));
    public Setting<Integer> hudBlue = this.register(new Setting<Object>("Blue", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> this.rainbow.getValue() == false));
    private final Setting<Boolean> grayNess = this.register(new Setting<Boolean>("FutureColour", true));
    public Setting<Boolean> potions1 = this.register(new Setting<Object>("LevelPotions", Boolean.valueOf(false), v -> this.potions.getValue()));
    public Setting<Boolean> MS = this.register(new Setting<Object>("ms", Boolean.valueOf(false), v -> this.ping.getValue()));
    private static HUD INSTANCE = new HUD();
    private Map<String, Integer> players = new HashMap<String, Integer>();
    private Map<Potion, Color> potionColorMap = new HashMap<Potion, Color>();
    public Map<Module, Float> moduleProgressMap = new HashMap<Module, Float>();
    private static final ResourceLocation box = new ResourceLocation("textures/gui/container/shulker_box.png");
    private static final ItemStack totem = new ItemStack(Items.field_190929_cY);
    private int color;
    private boolean shouldIncrement;
    private int hitMarkerTimer;
    private final Timer timer = new Timer();
    private final Timer moduleTimer = new Timer();
    public Map<Integer, Integer> colorMap = new HashMap<Integer, Integer>();
    private static final ResourceLocation codHitmarker = new ResourceLocation("earthhack", "cod_hitmarker");
    private static final ResourceLocation csgoHitmarker = new ResourceLocation("earthhack", "csgo_hitmarker");
    public static final SoundEvent COD_EVENT = new SoundEvent(codHitmarker);
    public static final SoundEvent CSGO_EVENT = new SoundEvent(csgoHitmarker);

    public HUD() {
        super("HUD", "HUD Elements rendered on your screen", Module.Category.CLIENT, true, false, false);
        this.setInstance();
        this.potionColorMap.put(MobEffects.field_76424_c, new Color(124, 175, 198));
        this.potionColorMap.put(MobEffects.field_76421_d, new Color(90, 108, 129));
        this.potionColorMap.put(MobEffects.field_76422_e, new Color(217, 192, 67));
        this.potionColorMap.put(MobEffects.field_76419_f, new Color(74, 66, 23));
        this.potionColorMap.put(MobEffects.field_76420_g, new Color(147, 36, 35));
        this.potionColorMap.put(MobEffects.field_76432_h, new Color(67, 10, 9));
        this.potionColorMap.put(MobEffects.field_76433_i, new Color(67, 10, 9));
        this.potionColorMap.put(MobEffects.field_76430_j, new Color(34, 255, 76));
        this.potionColorMap.put(MobEffects.field_76431_k, new Color(85, 29, 74));
        this.potionColorMap.put(MobEffects.field_76428_l, new Color(205, 92, 171));
        this.potionColorMap.put(MobEffects.field_76429_m, new Color(153, 69, 58));
        this.potionColorMap.put(MobEffects.field_76426_n, new Color(228, 154, 58));
        this.potionColorMap.put(MobEffects.field_76427_o, new Color(46, 82, 153));
        this.potionColorMap.put(MobEffects.field_76441_p, new Color(127, 131, 146));
        this.potionColorMap.put(MobEffects.field_76440_q, new Color(31, 31, 35));
        this.potionColorMap.put(MobEffects.field_76439_r, new Color(31, 31, 161));
        this.potionColorMap.put(MobEffects.field_76438_s, new Color(88, 118, 83));
        this.potionColorMap.put(MobEffects.field_76437_t, new Color(72, 77, 72));
        this.potionColorMap.put(MobEffects.field_76436_u, new Color(78, 147, 49));
        this.potionColorMap.put(MobEffects.field_82731_v, new Color(53, 42, 39));
        this.potionColorMap.put(MobEffects.field_180152_w, new Color(248, 125, 35));
        this.potionColorMap.put(MobEffects.field_76444_x, new Color(37, 82, 165));
        this.potionColorMap.put(MobEffects.field_76443_y, new Color(248, 36, 35));
        this.potionColorMap.put(MobEffects.field_188423_x, new Color(148, 160, 97));
        this.potionColorMap.put(MobEffects.field_188424_y, new Color(206, 255, 255));
        this.potionColorMap.put(MobEffects.field_188425_z, new Color(51, 153, 0));
        this.potionColorMap.put(MobEffects.field_189112_A, new Color(192, 164, 77));
    }

    private void setInstance() {
        INSTANCE = this;
    }

    public static HUD getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new HUD();
        }
        return INSTANCE;
    }

    @Override
    public void onUpdate() {
        for (Module module : Phobos.moduleManager.sortedModules) {
            if (!module.isDisabled() || module.arrayListOffset != 0.0f) continue;
            module.sliding = true;
        }
        if (this.timer.passedMs(Managers.getInstance().textRadarUpdates.getValue().intValue())) {
            this.players = this.getTextRadarPlayers();
            this.timer.reset();
        }
        if (this.shouldIncrement) {
            ++this.hitMarkerTimer;
        }
        if (this.hitMarkerTimer == 10) {
            this.hitMarkerTimer = 0;
            this.shouldIncrement = false;
        }
    }

    @SubscribeEvent
    public void onModuleToggle(ClientEvent event) {
        block4: {
            block5: {
                if (!(event.getFeature() instanceof Module)) break block4;
                if (event.getStage() != 0) break block5;
                for (float i = 0.0f; i <= (float)this.renderer.getStringWidth(((Module)event.getFeature()).getDisplayName()); i += (float)this.renderer.getStringWidth(((Module)event.getFeature()).getDisplayName()) / 500.0f) {
                    if (this.moduleTimer.passedMs(1L)) {
                        this.moduleProgressMap.put((Module)event.getFeature(), Float.valueOf((float)this.renderer.getStringWidth(((Module)event.getFeature()).getDisplayName()) - i));
                    }
                    this.timer.reset();
                }
                break block4;
            }
            if (event.getStage() != 1) break block4;
            for (float i = 0.0f; i <= (float)this.renderer.getStringWidth(((Module)event.getFeature()).getDisplayName()); i += (float)this.renderer.getStringWidth(((Module)event.getFeature()).getDisplayName()) / 500.0f) {
                if (this.moduleTimer.passedMs(1L)) {
                    this.moduleProgressMap.put((Module)event.getFeature(), Float.valueOf((float)this.renderer.getStringWidth(((Module)event.getFeature()).getDisplayName()) - i));
                }
                this.timer.reset();
            }
        }
    }

    @Override
    public void onRender2D(Render2DEvent event) {
        String fpsText;
        String text;
        Object text2;
        String text3;
        int i;
        int j;
        if (HUD.fullNullCheck()) {
            return;
        }
        int colorSpeed = 101 - this.rainbowSpeed.getValue();
        float hue = this.colorSync.getValue() != false ? Colors.INSTANCE.hue : (float)(System.currentTimeMillis() % (long)(360 * colorSpeed)) / (360.0f * (float)colorSpeed);
        int width = this.renderer.scaledWidth;
        int height = this.renderer.scaledHeight;
        float tempHue = hue;
        for (int i2 = 0; i2 <= height; ++i2) {
            if (this.colorSync.getValue().booleanValue()) {
                this.colorMap.put(i2, Color.HSBtoRGB(tempHue, (float)Colors.INSTANCE.rainbowSaturation.getValue().intValue() / 255.0f, (float)Colors.INSTANCE.rainbowBrightness.getValue().intValue() / 255.0f));
            } else {
                this.colorMap.put(i2, Color.HSBtoRGB(tempHue, (float)this.rainbowSaturation.getValue().intValue() / 255.0f, (float)this.rainbowBrightness.getValue().intValue() / 255.0f));
            }
            tempHue += 1.0f / (float)height * (float)this.factor.getValue().intValue();
        }
        if (this.rainbow.getValue().booleanValue() && !this.rolling.getValue().booleanValue()) {
            this.color = this.colorSync.getValue() != false ? Colors.INSTANCE.getCurrentColorHex() : Color.HSBtoRGB(hue, (float)this.rainbowSaturation.getValue().intValue() / 255.0f, (float)this.rainbowBrightness.getValue().intValue() / 255.0f);
        } else if (!this.rainbow.getValue().booleanValue()) {
            this.color = this.colorSync.getValue() != false ? Colors.INSTANCE.getCurrentColorHex() : ColorUtil.toRGBA(this.hudRed.getValue(), this.hudGreen.getValue(), this.hudBlue.getValue());
        }
        String grayString = this.grayNess.getValue() != false ? "\u00a77" : "";
        switch (this.watermark.getValue()) {
            case PHOBOS: {
                this.renderer.drawString("Phobos" + (this.modeVer.getValue() != false ? " v1.7.2" : ""), 2.0f, 2.0f, this.rolling.getValue() != false && this.rainbow.getValue() != false ? this.colorMap.get(2) : this.color, true);
                break;
            }
            case EARTH: {
                this.renderer.drawString("3arthh4ck" + (this.modeVer.getValue() != false ? " v1.7.2" : ""), 2.0f, 2.0f, this.rolling.getValue() != false && this.rainbow.getValue() != false ? this.colorMap.get(2) : this.color, true);
                break;
            }
            case CUSTOM: {
                this.renderer.drawString(this.customWatermark.getValue() + (this.modeVer.getValue() != false ? " v1.7.2" : ""), 2.0f, 2.0f, this.rolling.getValue() != false && this.rainbow.getValue() != false ? this.colorMap.get(2) : this.color, true);
            }
        }
        if (this.textRadar.getValue().booleanValue()) {
            this.drawTextRadar(ToolTips.getInstance().isOff() || ToolTips.getInstance().shulkerSpy.getValue() == false || ToolTips.getInstance().render.getValue() == false ? 0 : ToolTips.getInstance().getTextRadarY());
        }
        int n = this.renderingUp.getValue() != false ? 0 : (j = HUD.mc.field_71462_r instanceof GuiChat ? 14 : 0);
        if (this.arrayList.getValue().booleanValue()) {
            Color moduleColor;
            Module module;
            if (this.renderingUp.getValue().booleanValue()) {
                for (i = 0; i < (this.alphabeticalSorting.getValue() != false ? Phobos.moduleManager.alphabeticallySortedModules.size() : Phobos.moduleManager.sortedModules.size()); ++i) {
                    module = this.alphabeticalSorting.getValue() != false ? Phobos.moduleManager.alphabeticallySortedModules.get(i) : Phobos.moduleManager.sortedModules.get(i);
                    text3 = module.getDisplayName() + "\u00a77" + (module.getDisplayInfo() != null ? " [\u00a7f" + module.getDisplayInfo() + "\u00a77" + "]" : "");
                    moduleColor = Phobos.moduleManager.moduleColorMap.get(module);
                    this.renderer.drawString(text3, (float)(width - 2 - this.renderer.getStringWidth(text3)) + (this.animationHorizontalTime.getValue() == 1 ? 0.0f : module.arrayListOffset), 2 + j * 10, this.rolling.getValue() != false && this.rainbow.getValue() != false ? this.colorMap.get(MathUtil.clamp(2 + j * 10, 0, height)) : (this.moduleColors.getValue() != false && moduleColor != null ? moduleColor.getRGB() : this.color), true);
                    ++j;
                }
            } else {
                for (i = 0; i < (this.alphabeticalSorting.getValue() != false ? Phobos.moduleManager.alphabeticallySortedModules.size() : Phobos.moduleManager.sortedModules.size()); ++i) {
                    module = this.alphabeticalSorting.getValue() != false ? Phobos.moduleManager.alphabeticallySortedModules.get(Phobos.moduleManager.alphabeticallySortedModules.size() - 1 - i) : Phobos.moduleManager.sortedModules.get(i);
                    text3 = module.getDisplayName() + "\u00a77" + (module.getDisplayInfo() != null ? " [\u00a7f" + module.getDisplayInfo() + "\u00a77" + "]" : "");
                    moduleColor = Phobos.moduleManager.moduleColorMap.get(module);
                    this.renderer.drawString(text3, (float)(width - 2 - this.renderer.getStringWidth(text3)) + (this.animationHorizontalTime.getValue() == 1 ? 0.0f : module.arrayListOffset), height - (j += 10), this.rolling.getValue() != false && this.rainbow.getValue() != false ? this.colorMap.get(MathUtil.clamp(height - j, 0, height)) : (this.moduleColors.getValue() != false && moduleColor != null ? moduleColor.getRGB() : this.color), true);
                }
            }
        }
        int n2 = this.renderingUp.getValue() == false ? 0 : (i = HUD.mc.field_71462_r instanceof GuiChat ? 0 : 0);
        if (this.renderingUp.getValue().booleanValue()) {
            int itemDamage;
            if (this.serverBrand.getValue().booleanValue()) {
                text2 = grayString + "Server brand " + "\u00a7f" + Phobos.serverManager.getServerBrand();
                this.renderer.drawString((String)text2, width - (this.renderer.getStringWidth((String)text2) + 2), height - 2 - (i += 10), this.rolling.getValue() != false && this.rainbow.getValue() != false ? this.colorMap.get(height - i) : this.color, true);
            }
            if (this.potions.getValue().booleanValue()) {
                for (PotionEffect effect : Phobos.potionManager.getOwnPotions()) {
                    text = this.altPotionsColors.getValue() != false ? Phobos.potionManager.getPotionString(effect) : Phobos.potionManager.getColoredPotionString(effect);
                    this.renderer.drawString(text, width - (this.renderer.getStringWidth(text) + 2), height - 2 - (i += 10), this.rolling.getValue() != false && this.rainbow.getValue() != false ? this.colorMap.get(height - i) : (this.altPotionsColors.getValue() != false ? this.potionColorMap.get((Object)effect.func_188419_a()).getRGB() : this.color), true);
                }
            }
            if (this.speed.getValue().booleanValue()) {
                text2 = grayString + "Speed " + "\u00a7f" + Phobos.speedManager.getSpeedKpH() + " km/h";
                this.renderer.drawString((String)text2, width - (this.renderer.getStringWidth((String)text2) + 2), height - 2 - (i += 10), this.rolling.getValue() != false && this.rainbow.getValue() != false ? this.colorMap.get(height - i) : this.color, true);
            }
            if (this.time.getValue().booleanValue()) {
                text2 = grayString + "Time " + "\u00a7f" + new SimpleDateFormat("h:mm a").format(new Date());
                this.renderer.drawString((String)text2, width - (this.renderer.getStringWidth((String)text2) + 2), height - 2 - (i += 10), this.rolling.getValue() != false && this.rainbow.getValue() != false ? this.colorMap.get(height - i) : this.color, true);
            }
            if (this.durability.getValue().booleanValue() && (itemDamage = HUD.mc.field_71439_g.func_184614_ca().func_77958_k() - HUD.mc.field_71439_g.func_184614_ca().func_77952_i()) > 0) {
                text3 = grayString + "Durability " + "\u00a7a" + itemDamage;
                this.renderer.drawString(text3, width - (this.renderer.getStringWidth(text3) + 2), height - 2 - (i += 10), this.rolling.getValue() != false && this.rainbow.getValue() != false ? this.colorMap.get(height - i) : this.color, true);
            }
            if (this.tps.getValue().booleanValue()) {
                String text4 = grayString + "TPS " + "\u00a7f" + Phobos.serverManager.getTPS();
                this.renderer.drawString(text4, width - (this.renderer.getStringWidth(text4) + 2), height - 2 - (i += 10), this.rolling.getValue() != false && this.rainbow.getValue() != false ? this.colorMap.get(height - i) : this.color, true);
            }
            fpsText = grayString + "FPS " + "\u00a7f" + Minecraft.field_71470_ab;
            text3 = grayString + "Ping " + "\u00a7f" + (ServerModule.getInstance().isConnected() ? ServerModule.getInstance().getServerPing() : (long)Phobos.serverManager.getPing()) + (this.MS.getValue() != false ? "ms" : "");
            if (this.renderer.getStringWidth(text3) > this.renderer.getStringWidth(fpsText)) {
                if (this.ping.getValue().booleanValue()) {
                    this.renderer.drawString(text3, width - (this.renderer.getStringWidth(text3) + 2), height - 2 - (i += 10), this.rolling.getValue() != false && this.rainbow.getValue() != false ? this.colorMap.get(height - i) : this.color, true);
                }
                if (this.fps.getValue().booleanValue()) {
                    this.renderer.drawString(fpsText, width - (this.renderer.getStringWidth(fpsText) + 2), height - 2 - (i += 10), this.rolling.getValue() != false && this.rainbow.getValue() != false ? this.colorMap.get(height - i) : this.color, true);
                }
            } else {
                if (this.fps.getValue().booleanValue()) {
                    this.renderer.drawString(fpsText, width - (this.renderer.getStringWidth(fpsText) + 2), height - 2 - (i += 10), this.rolling.getValue() != false && this.rainbow.getValue() != false ? this.colorMap.get(height - i) : this.color, true);
                }
                if (this.ping.getValue().booleanValue()) {
                    this.renderer.drawString(text3, width - (this.renderer.getStringWidth(text3) + 2), height - 2 - (i += 10), this.rolling.getValue() != false && this.rainbow.getValue() != false ? this.colorMap.get(height - i) : this.color, true);
                }
            }
        } else {
            int itemDamage;
            if (this.serverBrand.getValue().booleanValue()) {
                text2 = grayString + "Server brand " + "\u00a7f" + Phobos.serverManager.getServerBrand();
                this.renderer.drawString((String)text2, width - (this.renderer.getStringWidth((String)text2) + 2), 2 + i++ * 10, this.rolling.getValue() != false && this.rainbow.getValue() != false ? this.colorMap.get(2 + i * 10) : this.color, true);
            }
            if (this.potions.getValue().booleanValue()) {
                for (PotionEffect effect : Phobos.potionManager.getOwnPotions()) {
                    text = this.altPotionsColors.getValue() != false ? Phobos.potionManager.getPotionString(effect) : Phobos.potionManager.getColoredPotionString(effect);
                    this.renderer.drawString(text, width - (this.renderer.getStringWidth(text) + 2), 2 + i++ * 10, this.rolling.getValue() != false && this.rainbow.getValue() != false ? this.colorMap.get(2 + i * 10) : (this.altPotionsColors.getValue() != false ? this.potionColorMap.get((Object)effect.func_188419_a()).getRGB() : this.color), true);
                }
            }
            if (this.speed.getValue().booleanValue()) {
                text2 = grayString + "Speed " + "\u00a7f" + Phobos.speedManager.getSpeedKpH() + " km/h";
                this.renderer.drawString((String)text2, width - (this.renderer.getStringWidth((String)text2) + 2), 2 + i++ * 10, this.rolling.getValue() != false && this.rainbow.getValue() != false ? this.colorMap.get(2 + i * 10) : this.color, true);
            }
            if (this.time.getValue().booleanValue()) {
                text2 = grayString + "Time " + "\u00a7f" + new SimpleDateFormat("h:mm a").format(new Date());
                this.renderer.drawString((String)text2, width - (this.renderer.getStringWidth((String)text2) + 2), 2 + i++ * 10, this.rolling.getValue() != false && this.rainbow.getValue() != false ? this.colorMap.get(2 + i * 10) : this.color, true);
            }
            if (this.durability.getValue().booleanValue() && (itemDamage = HUD.mc.field_71439_g.func_184614_ca().func_77958_k() - HUD.mc.field_71439_g.func_184614_ca().func_77952_i()) > 0) {
                text3 = grayString + "Durability " + "\u00a7a" + itemDamage;
                this.renderer.drawString(text3, width - (this.renderer.getStringWidth(text3) + 2), 2 + i++ * 10, this.rolling.getValue() != false && this.rainbow.getValue() != false ? this.colorMap.get(2 + i * 10) : this.color, true);
            }
            if (this.tps.getValue().booleanValue()) {
                String text5 = grayString + "TPS " + "\u00a7f" + Phobos.serverManager.getTPS();
                this.renderer.drawString(text5, width - (this.renderer.getStringWidth(text5) + 2), 2 + i++ * 10, this.rolling.getValue() != false && this.rainbow.getValue() != false ? this.colorMap.get(2 + i * 10) : this.color, true);
            }
            fpsText = grayString + "FPS " + "\u00a7f" + Minecraft.field_71470_ab;
            text3 = grayString + "Ping " + "\u00a7f" + Phobos.serverManager.getPing();
            if (this.renderer.getStringWidth(text3) > this.renderer.getStringWidth(fpsText)) {
                if (this.ping.getValue().booleanValue()) {
                    this.renderer.drawString(text3, width - (this.renderer.getStringWidth(text3) + 2), 2 + i++ * 10, this.rolling.getValue() != false && this.rainbow.getValue() != false ? this.colorMap.get(2 + i * 10) : this.color, true);
                }
                if (this.fps.getValue().booleanValue()) {
                    this.renderer.drawString(fpsText, width - (this.renderer.getStringWidth(fpsText) + 2), 2 + i++ * 10, this.rolling.getValue() != false && this.rainbow.getValue() != false ? this.colorMap.get(2 + i * 10) : this.color, true);
                }
            } else {
                if (this.fps.getValue().booleanValue()) {
                    this.renderer.drawString(fpsText, width - (this.renderer.getStringWidth(fpsText) + 2), 2 + i++ * 10, this.rolling.getValue() != false && this.rainbow.getValue() != false ? this.colorMap.get(2 + i * 10) : this.color, true);
                }
                if (this.ping.getValue().booleanValue()) {
                    this.renderer.drawString(text3, width - (this.renderer.getStringWidth(text3) + 2), 2 + i++ * 10, this.rolling.getValue() != false && this.rainbow.getValue() != false ? this.colorMap.get(2 + i * 10) : this.color, true);
                }
            }
        }
        boolean inHell = HUD.mc.field_71441_e.func_180494_b(HUD.mc.field_71439_g.func_180425_c()).func_185359_l().equals("Hell");
        int posX = (int)HUD.mc.field_71439_g.field_70165_t;
        int posY = (int)HUD.mc.field_71439_g.field_70163_u;
        int posZ = (int)HUD.mc.field_71439_g.field_70161_v;
        float nether = !inHell ? 0.125f : 8.0f;
        int hposX = (int)(HUD.mc.field_71439_g.field_70165_t * (double)nether);
        int hposZ = (int)(HUD.mc.field_71439_g.field_70161_v * (double)nether);
        if (this.renderingUp.getValue().booleanValue()) {
            Phobos.notificationManager.handleNotifications(height - (i + 16));
        } else {
            Phobos.notificationManager.handleNotifications(height - (j + 16));
        }
        i = HUD.mc.field_71462_r instanceof GuiChat ? 14 : 0;
        String coordinates = grayString + "XYZ " + "\u00a7f" + posX + ", " + posY + ", " + posZ + " " + grayString + "[" + "\u00a7f" + hposX + ", " + hposZ + grayString + "]";
        String text6 = (this.direction.getValue() != false ? Phobos.rotationManager.getDirection4D(false) + " " : "") + (this.coords.getValue() != false ? coordinates : "") + "";
        this.renderer.drawString(text6, 2.0f, height - (i += 10), this.rolling.getValue() != false && this.rainbow.getValue() != false ? this.colorMap.get(height - (i += 10)) : this.color, true);
        if (this.armor.getValue().booleanValue()) {
            this.renderArmorHUD(this.percent.getValue());
        }
        if (this.totems.getValue().booleanValue()) {
            this.renderTotemHUD();
        }
        if (this.greeter.getValue() != Greeter.NONE) {
            this.renderGreeter();
        }
        if (this.lag.getValue() != LagNotify.NONE) {
            this.renderLag();
        }
        if (this.hitMarkers.getValue().booleanValue() && this.hitMarkerTimer > 0) {
            this.drawHitMarkers();
        }
    }

    public Map<String, Integer> getTextRadarPlayers() {
        return EntityUtil.getTextRadarPlayers();
    }

    public void renderGreeter() {
        int width = this.renderer.scaledWidth;
        String text = "";
        switch (this.greeter.getValue()) {
            case TIME: {
                text = text + MathUtil.getTimeOfDay() + HUD.mc.field_71439_g.getDisplayNameString();
                break;
            }
            case LONG: {
                text = text + "Welcome to Phobos.eu " + HUD.mc.field_71439_g.getDisplayNameString() + " :^)";
                break;
            }
            case CUSTOM: {
                text = text + this.spoofGreeter.getValue();
                break;
            }
            default: {
                text = text + "Welcome " + HUD.mc.field_71439_g.getDisplayNameString();
            }
        }
        this.renderer.drawString(text, (float)width / 2.0f - (float)this.renderer.getStringWidth(text) / 2.0f + 2.0f, 2.0f, this.rolling.getValue() != false && this.rainbow.getValue() != false ? this.colorMap.get(2) : this.color, true);
    }

    public void renderLag() {
        int width = this.renderer.scaledWidth;
        if (Phobos.serverManager.isServerNotResponding()) {
            String text = (this.lag.getValue() == LagNotify.GRAY ? "\u00a77" : "\u00a7c") + "Server not responding: " + MathUtil.round((float)Phobos.serverManager.serverRespondingTime() / 1000.0f, 1) + "s.";
            this.renderer.drawString(text, (float)width / 2.0f - (float)this.renderer.getStringWidth(text) / 2.0f + 2.0f, 20.0f, this.rolling.getValue() != false && this.rainbow.getValue() != false ? this.colorMap.get(20) : this.color, true);
        }
    }

    public void renderArrayList() {
    }

    public void renderTotemHUD() {
        int width = this.renderer.scaledWidth;
        int height = this.renderer.scaledHeight;
        int totems = HUD.mc.field_71439_g.field_71071_by.field_70462_a.stream().filter(itemStack -> itemStack.func_77973_b() == Items.field_190929_cY).mapToInt(ItemStack::func_190916_E).sum();
        if (HUD.mc.field_71439_g.func_184592_cb().func_77973_b() == Items.field_190929_cY) {
            totems += HUD.mc.field_71439_g.func_184592_cb().func_190916_E();
        }
        if (totems > 0) {
            GlStateManager.func_179098_w();
            int i = width / 2;
            boolean iteration = false;
            int y = height - 55 - (HUD.mc.field_71439_g.func_70090_H() && HUD.mc.field_71442_b.func_78763_f() ? 10 : 0);
            int x = i - 189 + 180 + 2;
            GlStateManager.func_179126_j();
            RenderUtil.itemRender.field_77023_b = 200.0f;
            RenderUtil.itemRender.func_180450_b(totem, x, y);
            RenderUtil.itemRender.func_180453_a(HUD.mc.field_71466_p, totem, x, y, "");
            RenderUtil.itemRender.field_77023_b = 0.0f;
            GlStateManager.func_179098_w();
            GlStateManager.func_179140_f();
            GlStateManager.func_179097_i();
            this.renderer.drawStringWithShadow(totems + "", x + 19 - 2 - this.renderer.getStringWidth(totems + ""), y + 9, 0xFFFFFF);
            GlStateManager.func_179126_j();
            GlStateManager.func_179140_f();
        }
    }

    public void renderArmorHUD(boolean percent) {
        int width = this.renderer.scaledWidth;
        int height = this.renderer.scaledHeight;
        GlStateManager.func_179098_w();
        int i = width / 2;
        int iteration = 0;
        int y = height - 55 - (HUD.mc.field_71439_g.func_70090_H() && HUD.mc.field_71442_b.func_78763_f() ? 10 : 0);
        for (ItemStack is : HUD.mc.field_71439_g.field_71071_by.field_70460_b) {
            ++iteration;
            if (is.func_190926_b()) continue;
            int x = i - 90 + (9 - iteration) * 20 + 2;
            GlStateManager.func_179126_j();
            RenderUtil.itemRender.field_77023_b = 200.0f;
            RenderUtil.itemRender.func_180450_b(is, x, y);
            RenderUtil.itemRender.func_180453_a(HUD.mc.field_71466_p, is, x, y, "");
            RenderUtil.itemRender.field_77023_b = 0.0f;
            GlStateManager.func_179098_w();
            GlStateManager.func_179140_f();
            GlStateManager.func_179097_i();
            String s = is.func_190916_E() > 1 ? is.func_190916_E() + "" : "";
            this.renderer.drawStringWithShadow(s, x + 19 - 2 - this.renderer.getStringWidth(s), y + 9, 0xFFFFFF);
            if (!percent) continue;
            int dmg = 0;
            int itemDurability = is.func_77958_k() - is.func_77952_i();
            float green = ((float)is.func_77958_k() - (float)is.func_77952_i()) / (float)is.func_77958_k();
            float red = 1.0f - green;
            dmg = percent ? 100 - (int)(red * 100.0f) : itemDurability;
            this.renderer.drawStringWithShadow(dmg + "", x + 8 - this.renderer.getStringWidth(dmg + "") / 2, y - 11, ColorUtil.toRGBA((int)(red * 255.0f), (int)(green * 255.0f), 0));
        }
        GlStateManager.func_179126_j();
        GlStateManager.func_179140_f();
    }

    public void drawHitMarkers() {
        ScaledResolution resolution = new ScaledResolution(mc);
        RenderUtil.drawLine((float)resolution.func_78326_a() / 2.0f - 4.0f, (float)resolution.func_78328_b() / 2.0f - 4.0f, (float)resolution.func_78326_a() / 2.0f - 8.0f, (float)resolution.func_78328_b() / 2.0f - 8.0f, 1.0f, ColorUtil.toRGBA(255, 255, 255, 255));
        RenderUtil.drawLine((float)resolution.func_78326_a() / 2.0f + 4.0f, (float)resolution.func_78328_b() / 2.0f - 4.0f, (float)resolution.func_78326_a() / 2.0f + 8.0f, (float)resolution.func_78328_b() / 2.0f - 8.0f, 1.0f, ColorUtil.toRGBA(255, 255, 255, 255));
        RenderUtil.drawLine((float)resolution.func_78326_a() / 2.0f - 4.0f, (float)resolution.func_78328_b() / 2.0f + 4.0f, (float)resolution.func_78326_a() / 2.0f - 8.0f, (float)resolution.func_78328_b() / 2.0f + 8.0f, 1.0f, ColorUtil.toRGBA(255, 255, 255, 255));
        RenderUtil.drawLine((float)resolution.func_78326_a() / 2.0f + 4.0f, (float)resolution.func_78328_b() / 2.0f + 4.0f, (float)resolution.func_78326_a() / 2.0f + 8.0f, (float)resolution.func_78328_b() / 2.0f + 8.0f, 1.0f, ColorUtil.toRGBA(255, 255, 255, 255));
    }

    public void drawTextRadar(int yOffset) {
        if (!this.players.isEmpty()) {
            int y = this.renderer.getFontHeight() + 7 + yOffset;
            for (Map.Entry<String, Integer> player : this.players.entrySet()) {
                String text = player.getKey() + " ";
                int textheight = this.renderer.getFontHeight() + 1;
                this.renderer.drawString(text, 2.0f, y, this.rolling.getValue() != false && this.rainbow.getValue() != false ? this.colorMap.get(y) : this.color, true);
                y += textheight;
            }
        }
    }

    public static enum Sound {
        NONE,
        COD,
        CSGO;

    }

    public static enum WaterMark {
        NONE,
        PHOBOS,
        EARTH,
        CUSTOM;

    }

    public static enum LagNotify {
        NONE,
        RED,
        GRAY;

    }

    public static enum Greeter {
        NONE,
        NAME,
        TIME,
        LONG,
        CUSTOM;

    }
}

