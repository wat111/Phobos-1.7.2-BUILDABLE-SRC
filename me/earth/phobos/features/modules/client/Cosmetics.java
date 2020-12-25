/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.model.ModelBase
 *  net.minecraft.client.model.ModelRenderer
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.client.renderer.entity.RenderManager
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EnumPlayerModelParts
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.math.MathHelper
 *  net.minecraftforge.client.event.RenderPlayerEvent$Post
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 *  org.lwjgl.opengl.GL11
 */
package me.earth.phobos.features.modules.client;

import me.earth.phobos.Phobos;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.util.EntityUtil;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

public class Cosmetics
extends Module {
    public final ModelBetterPhysicsCape betterPhysicsCape = new ModelBetterPhysicsCape();
    public final ModelCloutGoggles cloutGoggles = new ModelCloutGoggles();
    public final ModelPhyscisCapes capesModel = new ModelPhyscisCapes();
    public final ModelSquidFlag flag = new ModelSquidFlag();
    public final TopHatModel hatModel = new TopHatModel();
    public final GlassesModel glassesModel = new GlassesModel();
    private final HatGlassesModel hatGlassesModel = new HatGlassesModel();
    public final SantaHatModel santaHatModel = new SantaHatModel();
    public final ModelHatFez fezModel = new ModelHatFez();
    private final ResourceLocation hatTexture = new ResourceLocation("textures/tophat.png");
    private final ResourceLocation fezTexture = new ResourceLocation("textures/fez.png");
    private final ResourceLocation glassesTexture = new ResourceLocation("textures/sunglasses.png");
    private final ResourceLocation santaHatTexture = new ResourceLocation("textures/santahat.png");
    private final ResourceLocation capeTexture = new ResourceLocation("textures/cape.png");
    private final ResourceLocation squidTexture = new ResourceLocation("textures/squid.png");
    private final ResourceLocation cloutGoggleTexture = new ResourceLocation("textures/cloutgoggles.png");
    private final ResourceLocation squidLauncherTexture = new ResourceLocation("textures/squidlauncher.png");
    public final ModelSquidLauncher squidLauncher = new ModelSquidLauncher();
    public static Cosmetics INSTANCE;

    public Cosmetics() {
        super("Cosmetics", "Bitch", Module.Category.CLIENT, true, false, false);
        INSTANCE = this;
    }

    @SubscribeEvent
    public void onRenderPlayer(RenderPlayerEvent.Post event) {
        if (!Phobos.cosmeticsManager.hasCosmetics(event.getEntityPlayer()) || EntityUtil.isFakePlayer(event.getEntityPlayer())) {
            return;
        }
        for (ModelBase model : Phobos.cosmeticsManager.getRenderModels(event.getEntityPlayer())) {
            GlStateManager.func_179094_E();
            RenderManager renderManager = mc.func_175598_ae();
            GlStateManager.func_179137_b((double)event.getX(), (double)event.getY(), (double)event.getZ());
            double scale = 1.0;
            double rotate = this.interpolate(event.getEntityPlayer().field_70758_at, event.getEntityPlayer().field_70759_as, event.getPartialRenderTick());
            double rotate1 = this.interpolate(event.getEntityPlayer().field_70127_C, event.getEntityPlayer().field_70125_A, event.getPartialRenderTick());
            double rotate3 = event.getEntityPlayer().func_70093_af() ? 22.0 : 0.0;
            float limbSwingAmount = this.interpolate(event.getEntityPlayer().field_184618_aE, event.getEntityPlayer().field_70721_aZ, event.getPartialRenderTick());
            float rotate2 = MathHelper.func_76134_b((float)(event.getEntityPlayer().field_184619_aG * 0.6662f + (float)Math.PI)) * 1.4f * limbSwingAmount / 1.0f;
            GL11.glScaled((double)(-scale), (double)(-scale), (double)scale);
            GL11.glTranslated((double)0.0, (double)(-((double)event.getEntityPlayer().field_70131_O - (event.getEntityPlayer().func_70093_af() ? 0.25 : 0.0) - 0.38) / scale), (double)0.0);
            GL11.glRotated((double)(180.0 + rotate), (double)0.0, (double)1.0, (double)0.0);
            if (!(model instanceof ModelSquidLauncher)) {
                GL11.glRotated((double)rotate1, (double)1.0, (double)0.0, (double)0.0);
            }
            if (model instanceof ModelSquidLauncher) {
                GL11.glRotated((double)rotate3, (double)1.0, (double)0.0, (double)0.0);
            }
            GlStateManager.func_179137_b((double)0.0, (double)-0.45, (double)0.0);
            if (model instanceof ModelSquidLauncher) {
                GlStateManager.func_179137_b((double)0.15, (double)1.3, (double)0.0);
                for (ModelRenderer renderer : this.squidLauncher.field_78092_r) {
                    renderer.field_78795_f = rotate2;
                }
            }
            if (model instanceof TopHatModel) {
                mc.func_110434_K().func_110577_a(this.hatTexture);
                this.hatModel.func_78088_a(event.getEntity(), 0.0f, 0.0f, -0.1f, 0.0f, 0.0f, 0.0625f);
                mc.func_110434_K().func_147645_c(this.hatTexture);
            } else if (model instanceof GlassesModel) {
                if (event.getEntityPlayer().func_175148_a(EnumPlayerModelParts.HAT)) {
                    mc.func_110434_K().func_110577_a(this.glassesTexture);
                    this.hatGlassesModel.func_78088_a(event.getEntity(), 0.0f, 0.0f, -0.1f, 0.0f, 0.0f, 0.0625f);
                    mc.func_110434_K().func_147645_c(this.glassesTexture);
                } else {
                    mc.func_110434_K().func_110577_a(this.glassesTexture);
                    this.glassesModel.func_78088_a(event.getEntity(), 0.0f, 0.0f, -0.1f, 0.0f, 0.0f, 0.0625f);
                    mc.func_110434_K().func_147645_c(this.glassesTexture);
                }
            } else if (model instanceof SantaHatModel) {
                mc.func_110434_K().func_110577_a(this.santaHatTexture);
                this.santaHatModel.func_78088_a(event.getEntity(), 0.0f, 0.0f, -0.1f, 0.0f, 0.0f, 0.0625f);
                mc.func_110434_K().func_147645_c(this.santaHatTexture);
            } else if (model instanceof ModelCloutGoggles) {
                mc.func_110434_K().func_110577_a(this.cloutGoggleTexture);
                this.cloutGoggles.func_78088_a(event.getEntity(), 0.0f, 0.0f, -0.1f, 0.0f, 0.0f, 0.0625f);
                mc.func_110434_K().func_147645_c(this.cloutGoggleTexture);
            } else if (model instanceof ModelSquidFlag) {
                mc.func_110434_K().func_110577_a(this.squidTexture);
                this.flag.func_78088_a(event.getEntity(), 0.0f, 0.0f, -0.1f, 0.0f, 0.0f, 0.0625f);
                mc.func_110434_K().func_147645_c(this.squidTexture);
            } else if (model instanceof ModelSquidLauncher) {
                mc.func_110434_K().func_110577_a(this.squidLauncherTexture);
                this.squidLauncher.func_78088_a(event.getEntity(), 0.0f, 0.0f, -0.1f, 0.0f, 0.0f, 0.0325f);
                mc.func_110434_K().func_147645_c(this.squidLauncherTexture);
            }
            GlStateManager.func_179121_F();
        }
    }

    public float interpolate(float yaw1, float yaw2, float percent) {
        float rotation = (yaw1 + (yaw2 - yaw1) * percent) % 360.0f;
        if (rotation < 0.0f) {
            rotation += 360.0f;
        }
        return rotation;
    }

    public class ModelBetterPhysicsCape
    extends ModelBase {
        public ModelRenderer segment1;

        public ModelBetterPhysicsCape() {
            this.field_78090_t = 256;
            this.field_78089_u = 128;
            for (int i = 0; i < 160; ++i) {
                ModelRenderer segment = new ModelRenderer((ModelBase)this, 0, i);
                segment.func_78793_a(0.0f, 0.0f, 0.0f);
                segment.func_78790_a(-5.0f, 0.0f + (float)i, 0.0f, 10, 1, 1, 0.0f);
                this.field_78092_r.add(segment);
            }
        }

        public void func_78088_a(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
            for (ModelRenderer model : this.field_78092_r) {
                GlStateManager.func_179094_E();
                GlStateManager.func_179109_b((float)model.field_82906_o, (float)model.field_82908_p, (float)model.field_82907_q);
                GlStateManager.func_179109_b((float)(model.field_78800_c * f5), (float)(model.field_78797_d * f5), (float)(model.field_78798_e * f5));
                GlStateManager.func_179139_a((double)1.0, (double)0.1, (double)1.0);
                GlStateManager.func_179109_b((float)(-model.field_82906_o), (float)(-model.field_82908_p), (float)(-model.field_82907_q));
                GlStateManager.func_179109_b((float)(-model.field_78800_c * f5), (float)(-model.field_78797_d * f5), (float)(-model.field_78798_e * f5));
                model.func_78785_a(f5);
                GlStateManager.func_179121_F();
            }
        }

        public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
            modelRenderer.field_78795_f = x;
            modelRenderer.field_78796_g = y;
            modelRenderer.field_78808_h = z;
        }
    }

    public class ModelCloutGoggles
    extends ModelBase {
        public ModelRenderer leftGlass;
        public ModelRenderer topLeftFrame;
        public ModelRenderer bottomLeftFrame;
        public ModelRenderer leftLeftFrame;
        public ModelRenderer rightLeftFrame;
        public ModelRenderer rightGlass;
        public ModelRenderer topRightFrame;
        public ModelRenderer bottomLeftFrame_1;
        public ModelRenderer leftRightFrame;
        public ModelRenderer rightRightFrame;
        public ModelRenderer leftEar;
        public ModelRenderer rightEar;

        public ModelCloutGoggles() {
            this.field_78090_t = 64;
            this.field_78089_u = 32;
            this.rightLeftFrame = new ModelRenderer((ModelBase)this, 18, 0);
            this.rightLeftFrame.func_78793_a(-3.0f, 3.0f, -4.0f);
            this.rightLeftFrame.func_78790_a(0.0f, 2.0f, 0.0f, 2, 1, 1, 0.0f);
            this.bottomLeftFrame_1 = new ModelRenderer((ModelBase)this, 26, 5);
            this.bottomLeftFrame_1.func_78793_a(-3.0f, 3.0f, -4.0f);
            this.bottomLeftFrame_1.func_78790_a(4.0f, 2.0f, 0.0f, 2, 1, 1, 0.0f);
            this.leftLeftFrame = new ModelRenderer((ModelBase)this, 10, 5);
            this.leftLeftFrame.func_78793_a(-3.0f, 3.0f, -4.0f);
            this.leftLeftFrame.func_78790_a(2.0f, 0.0f, 0.0f, 1, 2, 1, 0.0f);
            this.rightGlass = new ModelRenderer((ModelBase)this, 18, 5);
            this.rightGlass.func_78793_a(-3.0f, 3.0f, -4.0f);
            this.rightGlass.func_78790_a(4.0f, 0.0f, 0.0f, 2, 2, 1, 0.0f);
            this.rightRightFrame = new ModelRenderer((ModelBase)this, 10, 11);
            this.rightRightFrame.func_78793_a(3.0f, 3.0f, -4.0f);
            this.rightRightFrame.func_78790_a(0.0f, 0.0f, 0.0f, 1, 2, 1, 0.0f);
            this.leftEar = new ModelRenderer((ModelBase)this, 18, 11);
            this.leftEar.func_78793_a(-3.0f, 3.0f, -4.0f);
            this.leftEar.func_78790_a(-1.2f, 0.0f, 0.0f, 1, 1, 3, 0.0f);
            this.topRightFrame = new ModelRenderer((ModelBase)this, 26, 0);
            this.topRightFrame.func_78793_a(1.0f, 3.0f, -4.0f);
            this.topRightFrame.func_78790_a(0.0f, -1.0f, 0.0f, 2, 1, 1, 0.0f);
            this.topLeftFrame = new ModelRenderer((ModelBase)this, 0, 5);
            this.topLeftFrame.func_78793_a(-3.0f, 3.0f, -4.0f);
            this.topLeftFrame.func_78790_a(-1.0f, 0.0f, 0.0f, 1, 2, 1, 0.0f);
            this.rightEar = new ModelRenderer((ModelBase)this, 28, 11);
            this.rightEar.func_78793_a(-3.0f, 3.0f, -4.0f);
            this.rightEar.func_78790_a(6.2f, 0.0f, 0.0f, 1, 1, 3, 0.0f);
            this.leftGlass = new ModelRenderer((ModelBase)this, 0, 0);
            this.leftGlass.func_78793_a(-3.0f, 3.0f, -4.0f);
            this.leftGlass.func_78790_a(0.0f, 0.0f, 0.0f, 2, 2, 1, 0.0f);
            this.bottomLeftFrame = new ModelRenderer((ModelBase)this, 10, 0);
            this.bottomLeftFrame.func_78793_a(-3.0f, 3.0f, -4.0f);
            this.bottomLeftFrame.func_78790_a(0.0f, -1.0f, 0.0f, 2, 1, 1, 0.0f);
            this.leftRightFrame = new ModelRenderer((ModelBase)this, 0, 11);
            this.leftRightFrame.func_78793_a(-3.0f, 3.0f, -4.0f);
            this.leftRightFrame.func_78790_a(3.0f, 0.0f, 0.0f, 1, 2, 1, 0.0f);
        }

        public void func_78088_a(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
            this.rightLeftFrame.func_78785_a(f5);
            this.bottomLeftFrame_1.func_78785_a(f5);
            this.leftLeftFrame.func_78785_a(f5);
            this.rightGlass.func_78785_a(f5);
            this.rightRightFrame.func_78785_a(f5);
            this.leftEar.func_78785_a(f5);
            this.topRightFrame.func_78785_a(f5);
            this.topLeftFrame.func_78785_a(f5);
            this.rightEar.func_78785_a(f5);
            this.leftGlass.func_78785_a(f5);
            this.bottomLeftFrame.func_78785_a(f5);
            this.leftRightFrame.func_78785_a(f5);
        }

        public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
            modelRenderer.field_78795_f = x;
            modelRenderer.field_78796_g = y;
            modelRenderer.field_78808_h = z;
        }
    }

    public class ModelCosmetic
    extends ModelBase {
        public ResourceLocation texture;
    }

    public class ModelSquidLauncher
    extends ModelBase {
        public ModelRenderer barrel;
        public ModelRenderer squid;
        public ModelRenderer secondBarrel;
        public ModelRenderer barrelSide1;
        public ModelRenderer barrelSide2;
        public ModelRenderer barrelSide3;
        public ModelRenderer barrelSide4;
        public ModelRenderer stock;
        public ModelRenderer stockEnd;
        public ModelRenderer trigger;

        public ModelSquidLauncher() {
            this.field_78090_t = 64;
            this.field_78089_u = 32;
            this.barrelSide4 = new ModelRenderer((ModelBase)this, 0, 0);
            this.barrelSide4.func_78793_a(0.5f, 0.0f, 0.0f);
            this.barrelSide4.func_78790_a(0.0f, -2.0f, 0.2f, 4, 5, 1, 0.0f);
            this.setRotateAngle(this.barrelSide4, 0.091106184f, 0.0f, 0.0f);
            this.stock = new ModelRenderer((ModelBase)this, 0, 24);
            this.stock.func_78793_a(0.0f, 0.0f, 0.0f);
            this.stock.func_78790_a(1.5f, 3.0f, 1.5f, 2, 4, 2, 0.0f);
            this.squid = new ModelRenderer((ModelBase)this, 0, 16);
            this.squid.func_78793_a(0.0f, 0.0f, 0.0f);
            this.squid.func_78790_a(1.2f, -11.5f, 0.8f, 3, 4, 3, 0.0f);
            this.setRotateAngle(this.squid, 0.0f, -0.091106184f, 0.0f);
            this.barrelSide2 = new ModelRenderer((ModelBase)this, 18, 14);
            this.barrelSide2.func_78793_a(0.0f, 0.0f, 0.0f);
            this.barrelSide2.func_78790_a(3.8f, -2.5f, 0.5f, 1, 5, 4, 0.0f);
            this.setRotateAngle(this.barrelSide2, 0.0f, 0.0f, 0.091106184f);
            this.secondBarrel = new ModelRenderer((ModelBase)this, 32, 14);
            this.secondBarrel.func_78793_a(0.0f, 0.0f, 0.0f);
            this.secondBarrel.func_78790_a(0.5f, -2.0f, 0.5f, 4, 5, 4, 0.0f);
            this.stockEnd = new ModelRenderer((ModelBase)this, 18, 26);
            this.stockEnd.func_78793_a(0.0f, 0.0f, 0.0f);
            this.stockEnd.func_78790_a(2.0f, 7.0f, 1.5f, 1, 1, 4, 0.0f);
            this.barrelSide1 = new ModelRenderer((ModelBase)this, 18, 14);
            this.barrelSide1.func_78793_a(0.0f, 0.0f, 0.0f);
            this.barrelSide1.func_78790_a(0.2f, -2.0f, 0.5f, 1, 5, 4, 0.0f);
            this.setRotateAngle(this.barrelSide1, 0.0f, 0.0f, -0.091106184f);
            this.barrelSide3 = new ModelRenderer((ModelBase)this, 0, 0);
            this.barrelSide3.func_78793_a(0.0f, 0.0f, 0.0f);
            this.barrelSide3.func_78790_a(0.5f, -2.5f, 3.8f, 4, 5, 1, 0.0f);
            this.setRotateAngle(this.barrelSide3, -0.091106184f, 0.0f, 0.0f);
            this.trigger = new ModelRenderer((ModelBase)this, 40, 0);
            this.trigger.func_78793_a(0.0f, 0.0f, 0.0f);
            this.trigger.func_78790_a(12.0f, 6.6f, 5.4f, 1, 1, 1, 0.0f);
            this.barrel = new ModelRenderer((ModelBase)this, 18, 0);
            this.barrel.func_78793_a(0.0f, 0.0f, 0.0f);
            this.barrel.func_78790_a(0.0f, -8.0f, 0.0f, 5, 6, 5, 0.0f);
            this.field_78092_r.add(this.barrel);
            this.field_78092_r.add(this.squid);
            this.field_78092_r.add(this.secondBarrel);
            this.field_78092_r.add(this.barrelSide1);
            this.field_78092_r.add(this.barrelSide2);
            this.field_78092_r.add(this.barrelSide3);
            this.field_78092_r.add(this.barrelSide4);
            this.field_78092_r.add(this.stock);
            this.field_78092_r.add(this.stockEnd);
            this.field_78092_r.add(this.trigger);
        }

        public void func_78088_a(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
            this.stock.func_78785_a(f5);
            this.barrelSide1.func_78785_a(f5);
            this.stockEnd.func_78785_a(f5);
            this.secondBarrel.func_78785_a(f5);
            this.barrelSide3.func_78785_a(f5);
            this.squid.func_78785_a(f5);
            this.barrelSide4.func_78785_a(f5);
            this.barrel.func_78785_a(f5);
            this.barrelSide2.func_78785_a(f5);
            GlStateManager.func_179094_E();
            GlStateManager.func_179109_b((float)this.trigger.field_82906_o, (float)this.trigger.field_82908_p, (float)this.trigger.field_82907_q);
            GlStateManager.func_179109_b((float)(this.trigger.field_78800_c * f5), (float)(this.trigger.field_78797_d * f5), (float)(this.trigger.field_78798_e * f5));
            GlStateManager.func_179139_a((double)0.2, (double)1.0, (double)0.8);
            GlStateManager.func_179109_b((float)(-this.trigger.field_82906_o), (float)(-this.trigger.field_82908_p), (float)(-this.trigger.field_82907_q));
            GlStateManager.func_179109_b((float)(-this.trigger.field_78800_c * f5), (float)(-this.trigger.field_78797_d * f5), (float)(-this.trigger.field_78798_e * f5));
            this.trigger.func_78785_a(f5);
            GlStateManager.func_179121_F();
        }

        public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
            modelRenderer.field_78795_f = x;
            modelRenderer.field_78796_g = y;
            modelRenderer.field_78808_h = z;
        }
    }

    public class ModelSquidFlag
    extends ModelBase {
        public ModelRenderer flag;

        public ModelSquidFlag() {
            this.field_78090_t = 64;
            this.field_78089_u = 32;
            this.flag = new ModelRenderer((ModelBase)this, 0, 0);
            this.flag.func_78793_a(0.0f, 0.0f, 0.0f);
            this.flag.func_78790_a(-5.0f, -16.0f, 0.0f, 10, 16, 1, 0.0f);
        }

        public void func_78088_a(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
            this.flag.func_78785_a(f5);
        }

        public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
            modelRenderer.field_78795_f = x;
            modelRenderer.field_78796_g = y;
            modelRenderer.field_78808_h = z;
        }
    }

    public class ModelPhyscisCapes
    extends ModelBase {
        public ModelRenderer shape1;
        public ModelRenderer shape2;
        public ModelRenderer shape3;
        public ModelRenderer shape4;
        public ModelRenderer shape5;
        public ModelRenderer shape6;
        public ModelRenderer shape7;
        public ModelRenderer shape8;
        public ModelRenderer shape9;
        public ModelRenderer shape10;
        public ModelRenderer shape11;
        public ModelRenderer shape12;
        public ModelRenderer shape13;
        public ModelRenderer shape14;
        public ModelRenderer shape15;
        public ModelRenderer shape16;

        public ModelPhyscisCapes() {
            this.field_78090_t = 64;
            this.field_78089_u = 32;
            this.shape9 = new ModelRenderer((ModelBase)this, 0, 8);
            this.shape9.func_78793_a(-5.0f, 8.0f, -1.0f);
            this.shape9.func_78790_a(0.0f, 0.0f, 0.0f, 10, 1, 1, 0.0f);
            this.shape15 = new ModelRenderer((ModelBase)this, 0, 14);
            this.shape15.func_78793_a(-5.0f, 14.0f, -1.0f);
            this.shape15.func_78790_a(0.0f, 0.0f, 0.0f, 10, 1, 1, 0.0f);
            this.shape3 = new ModelRenderer((ModelBase)this, 0, 2);
            this.shape3.func_78793_a(-5.0f, 2.0f, -1.0f);
            this.shape3.func_78790_a(0.0f, 0.0f, 0.0f, 10, 1, 1, 0.0f);
            this.shape7 = new ModelRenderer((ModelBase)this, 0, 6);
            this.shape7.func_78793_a(-5.0f, 6.0f, -1.0f);
            this.shape7.func_78790_a(0.0f, 0.0f, 0.0f, 10, 1, 1, 0.0f);
            this.shape1 = new ModelRenderer((ModelBase)this, 0, 0);
            this.shape1.func_78793_a(-5.0f, 0.0f, -1.0f);
            this.shape1.func_78790_a(0.0f, 0.0f, 0.0f, 10, 1, 1, 0.0f);
            this.shape6 = new ModelRenderer((ModelBase)this, 0, 5);
            this.shape6.func_78793_a(-5.0f, 5.0f, -1.0f);
            this.shape6.func_78790_a(0.0f, 0.0f, 0.0f, 10, 1, 1, 0.0f);
            this.shape14 = new ModelRenderer((ModelBase)this, 0, 13);
            this.shape14.func_78793_a(-5.0f, 13.0f, -1.0f);
            this.shape14.func_78790_a(0.0f, 0.0f, 0.0f, 10, 1, 1, 0.0f);
            this.shape10 = new ModelRenderer((ModelBase)this, 0, 9);
            this.shape10.func_78793_a(-5.0f, 9.0f, -1.0f);
            this.shape10.func_78790_a(0.0f, 0.0f, 0.0f, 10, 1, 1, 0.0f);
            this.shape13 = new ModelRenderer((ModelBase)this, 0, 12);
            this.shape13.func_78793_a(-5.0f, 12.0f, -1.0f);
            this.shape13.func_78790_a(0.0f, 0.0f, 0.0f, 10, 1, 1, 0.0f);
            this.shape4 = new ModelRenderer((ModelBase)this, 0, 3);
            this.shape4.func_78793_a(-5.0f, 3.0f, -1.0f);
            this.shape4.func_78790_a(0.0f, 0.0f, 0.0f, 10, 1, 1, 0.0f);
            this.shape8 = new ModelRenderer((ModelBase)this, 0, 7);
            this.shape8.func_78793_a(-5.0f, 7.0f, -1.0f);
            this.shape8.func_78790_a(0.0f, 0.0f, 0.0f, 10, 1, 1, 0.0f);
            this.shape16 = new ModelRenderer((ModelBase)this, 0, 15);
            this.shape16.func_78793_a(-5.0f, 15.0f, -1.0f);
            this.shape16.func_78790_a(0.0f, 0.0f, 0.0f, 10, 1, 1, 0.0f);
            this.shape12 = new ModelRenderer((ModelBase)this, 0, 11);
            this.shape12.func_78793_a(-5.0f, 11.0f, -1.0f);
            this.shape12.func_78790_a(0.0f, 0.0f, 0.0f, 10, 1, 1, 0.0f);
            this.shape5 = new ModelRenderer((ModelBase)this, 0, 4);
            this.shape5.func_78793_a(-5.0f, 4.0f, -1.0f);
            this.shape5.func_78790_a(0.0f, 0.0f, 0.0f, 10, 1, 1, 0.0f);
            this.shape11 = new ModelRenderer((ModelBase)this, 0, 10);
            this.shape11.func_78793_a(-5.0f, 10.0f, -1.0f);
            this.shape11.func_78790_a(0.0f, 0.0f, 0.0f, 10, 1, 1, 0.0f);
            this.shape2 = new ModelRenderer((ModelBase)this, 0, 1);
            this.shape2.func_78793_a(-5.0f, 1.0f, -1.0f);
            this.shape2.func_78790_a(0.0f, 0.0f, 0.0f, 10, 1, 1, 0.0f);
            this.field_78092_r.add(this.shape1);
            this.field_78092_r.add(this.shape2);
            this.field_78092_r.add(this.shape3);
            this.field_78092_r.add(this.shape4);
            this.field_78092_r.add(this.shape5);
            this.field_78092_r.add(this.shape6);
            this.field_78092_r.add(this.shape7);
            this.field_78092_r.add(this.shape8);
            this.field_78092_r.add(this.shape9);
            this.field_78092_r.add(this.shape10);
            this.field_78092_r.add(this.shape11);
            this.field_78092_r.add(this.shape12);
            this.field_78092_r.add(this.shape13);
            this.field_78092_r.add(this.shape14);
            this.field_78092_r.add(this.shape15);
            this.field_78092_r.add(this.shape16);
        }

        public void func_78088_a(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
            this.shape9.func_78785_a(f5);
            this.shape15.func_78785_a(f5);
            this.shape3.func_78785_a(f5);
            this.shape7.func_78785_a(f5);
            this.shape1.func_78785_a(f5);
            this.shape6.func_78785_a(f5);
            this.shape14.func_78785_a(f5);
            this.shape10.func_78785_a(f5);
            this.shape13.func_78785_a(f5);
            this.shape4.func_78785_a(f5);
            this.shape8.func_78785_a(f5);
            this.shape16.func_78785_a(f5);
            this.shape12.func_78785_a(f5);
            this.shape5.func_78785_a(f5);
            this.shape11.func_78785_a(f5);
            this.shape2.func_78785_a(f5);
        }

        public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
            modelRenderer.field_78795_f = x;
            modelRenderer.field_78796_g = y;
            modelRenderer.field_78808_h = z;
        }
    }

    public class SantaHatModel
    extends ModelBase {
        public ModelRenderer baseLayer;
        public ModelRenderer baseRedLayer;
        public ModelRenderer midRedLayer;
        public ModelRenderer topRedLayer;
        public ModelRenderer lastRedLayer;
        public ModelRenderer realFinalLastLayer;
        public ModelRenderer whiteLayer;

        public SantaHatModel() {
            this.field_78090_t = 64;
            this.field_78089_u = 32;
            this.topRedLayer = new ModelRenderer((ModelBase)this, 46, 0);
            this.topRedLayer.func_78793_a(0.5f, -8.4f, -1.5f);
            this.topRedLayer.func_78790_a(0.0f, 0.0f, 0.0f, 3, 2, 3, 0.0f);
            this.setRotateAngle(this.topRedLayer, 0.0f, 0.0f, 0.5009095f);
            this.baseLayer = new ModelRenderer((ModelBase)this, 0, 0);
            this.baseLayer.func_78793_a(-4.0f, -1.0f, -4.0f);
            this.baseLayer.func_78790_a(0.0f, 0.0f, 0.0f, 8, 2, 8, 0.0f);
            this.midRedLayer = new ModelRenderer((ModelBase)this, 28, 0);
            this.midRedLayer.func_78793_a(-1.2f, -6.8f, -2.0f);
            this.midRedLayer.func_78790_a(0.0f, 0.0f, 0.0f, 4, 3, 4, 0.0f);
            this.setRotateAngle(this.midRedLayer, 0.0f, 0.0f, 0.22759093f);
            this.realFinalLastLayer = new ModelRenderer((ModelBase)this, 46, 8);
            this.realFinalLastLayer.func_78793_a(4.0f, -10.4f, 0.0f);
            this.realFinalLastLayer.func_78790_a(0.0f, 0.0f, 0.0f, 1, 3, 1, 0.0f);
            this.setRotateAngle(this.realFinalLastLayer, 0.0f, 0.0f, 1.0016445f);
            this.lastRedLayer = new ModelRenderer((ModelBase)this, 34, 8);
            this.lastRedLayer.func_78793_a(2.0f, -9.4f, 0.0f);
            this.lastRedLayer.func_78790_a(0.0f, 0.0f, 0.0f, 2, 2, 2, 0.0f);
            this.setRotateAngle(this.lastRedLayer, 0.0f, 0.0f, 0.8196066f);
            this.whiteLayer = new ModelRenderer((ModelBase)this, 0, 22);
            this.whiteLayer.func_78793_a(4.1f, -9.7f, -0.5f);
            this.whiteLayer.func_78790_a(0.0f, 0.0f, 0.0f, 2, 2, 2, 0.0f);
            this.setRotateAngle(this.whiteLayer, -0.091106184f, 0.0f, 0.18203785f);
            this.baseRedLayer = new ModelRenderer((ModelBase)this, 0, 11);
            this.baseRedLayer.func_78793_a(-3.0f, -4.0f, -3.0f);
            this.baseRedLayer.func_78790_a(0.0f, 0.0f, 0.0f, 6, 3, 6, 0.0f);
            this.setRotateAngle(this.baseRedLayer, 0.0f, 0.0f, 0.045553092f);
        }

        public void func_78088_a(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
            this.topRedLayer.func_78785_a(f5);
            this.baseLayer.func_78785_a(f5);
            this.midRedLayer.func_78785_a(f5);
            this.realFinalLastLayer.func_78785_a(f5);
            this.lastRedLayer.func_78785_a(f5);
            this.whiteLayer.func_78785_a(f5);
            this.baseRedLayer.func_78785_a(f5);
        }

        public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
            modelRenderer.field_78795_f = x;
            modelRenderer.field_78796_g = y;
            modelRenderer.field_78808_h = z;
        }
    }

    public class HatGlassesModel
    extends ModelBase {
        public final ResourceLocation glassesTexture = new ResourceLocation("textures/sunglasses.png");
        public ModelRenderer firstLeftFrame;
        public ModelRenderer firstRightFrame;
        public ModelRenderer centerBar;
        public ModelRenderer farLeftBar;
        public ModelRenderer farRightBar;
        public ModelRenderer leftEar;
        public ModelRenderer rightEar;

        public HatGlassesModel() {
            this.field_78090_t = 64;
            this.field_78089_u = 64;
            this.farLeftBar = new ModelRenderer((ModelBase)this, 0, 13);
            this.farLeftBar.func_78793_a(-4.0f, 3.5f, -5.0f);
            this.farLeftBar.func_78790_a(0.0f, 0.0f, 0.0f, 1, 1, 1, 0.0f);
            this.rightEar = new ModelRenderer((ModelBase)this, 10, 0);
            this.rightEar.func_78793_a(3.2f, 3.5f, -5.0f);
            this.rightEar.func_78790_a(0.0f, 0.0f, 0.0f, 1, 1, 3, 0.0f);
            this.centerBar = new ModelRenderer((ModelBase)this, 0, 9);
            this.centerBar.func_78793_a(-1.0f, 3.5f, -5.0f);
            this.centerBar.func_78790_a(0.0f, 0.0f, 0.0f, 2, 1, 1, 0.0f);
            this.firstLeftFrame = new ModelRenderer((ModelBase)this, 0, 0);
            this.firstLeftFrame.func_78793_a(-3.0f, 3.0f, -5.0f);
            this.firstLeftFrame.func_78790_a(0.0f, 0.0f, 0.0f, 2, 2, 1, 0.0f);
            this.firstRightFrame = new ModelRenderer((ModelBase)this, 0, 5);
            this.firstRightFrame.func_78793_a(1.0f, 3.0f, -5.0f);
            this.firstRightFrame.func_78790_a(0.0f, 0.0f, 0.0f, 2, 2, 1, 0.0f);
            this.leftEar = new ModelRenderer((ModelBase)this, 20, 0);
            this.leftEar.func_78793_a(-4.2f, 3.5f, -5.0f);
            this.leftEar.func_78790_a(0.0f, 0.0f, 0.0f, 1, 1, 3, 0.0f);
            this.farRightBar = new ModelRenderer((ModelBase)this, 0, 17);
            this.farRightBar.func_78793_a(3.0f, 3.5f, -5.0f);
            this.farRightBar.func_78790_a(0.0f, 0.0f, 0.0f, 1, 1, 1, 0.0f);
        }

        public void func_78088_a(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
            this.farLeftBar.func_78785_a(f5);
            this.rightEar.func_78785_a(f5);
            this.centerBar.func_78785_a(f5);
            this.firstLeftFrame.func_78785_a(f5);
            this.firstRightFrame.func_78785_a(f5);
            this.leftEar.func_78785_a(f5);
            this.farRightBar.func_78785_a(f5);
        }

        public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
            modelRenderer.field_78795_f = x;
            modelRenderer.field_78796_g = y;
            modelRenderer.field_78808_h = z;
        }
    }

    public class GlassesModel
    extends ModelBase {
        public final ResourceLocation glassesTexture = new ResourceLocation("textures/sunglasses.png");
        public ModelRenderer firstLeftFrame;
        public ModelRenderer firstRightFrame;
        public ModelRenderer centerBar;
        public ModelRenderer farLeftBar;
        public ModelRenderer farRightBar;
        public ModelRenderer leftEar;
        public ModelRenderer rightEar;

        public GlassesModel() {
            this.field_78090_t = 64;
            this.field_78089_u = 64;
            this.farLeftBar = new ModelRenderer((ModelBase)this, 0, 13);
            this.farLeftBar.func_78793_a(-4.0f, 3.5f, -4.0f);
            this.farLeftBar.func_78790_a(0.0f, 0.0f, 0.0f, 1, 1, 1, 0.0f);
            this.rightEar = new ModelRenderer((ModelBase)this, 10, 0);
            this.rightEar.func_78793_a(3.2f, 3.5f, -4.0f);
            this.rightEar.func_78790_a(0.0f, 0.0f, 0.0f, 1, 1, 3, 0.0f);
            this.centerBar = new ModelRenderer((ModelBase)this, 0, 9);
            this.centerBar.func_78793_a(-1.0f, 3.5f, -4.0f);
            this.centerBar.func_78790_a(0.0f, 0.0f, 0.0f, 2, 1, 1, 0.0f);
            this.firstLeftFrame = new ModelRenderer((ModelBase)this, 0, 0);
            this.firstLeftFrame.func_78793_a(-3.0f, 3.0f, -4.0f);
            this.firstLeftFrame.func_78790_a(0.0f, 0.0f, 0.0f, 2, 2, 1, 0.0f);
            this.firstRightFrame = new ModelRenderer((ModelBase)this, 0, 5);
            this.firstRightFrame.func_78793_a(1.0f, 3.0f, -4.0f);
            this.firstRightFrame.func_78790_a(0.0f, 0.0f, 0.0f, 2, 2, 1, 0.0f);
            this.leftEar = new ModelRenderer((ModelBase)this, 20, 0);
            this.leftEar.func_78793_a(-4.2f, 3.5f, -4.0f);
            this.leftEar.func_78790_a(0.0f, 0.0f, 0.0f, 1, 1, 3, 0.0f);
            this.farRightBar = new ModelRenderer((ModelBase)this, 0, 17);
            this.farRightBar.func_78793_a(3.0f, 3.5f, -4.0f);
            this.farRightBar.func_78790_a(0.0f, 0.0f, 0.0f, 1, 1, 1, 0.0f);
        }

        public void func_78088_a(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
            this.farLeftBar.func_78785_a(f5);
            this.rightEar.func_78785_a(f5);
            this.centerBar.func_78785_a(f5);
            this.firstLeftFrame.func_78785_a(f5);
            this.firstRightFrame.func_78785_a(f5);
            this.leftEar.func_78785_a(f5);
            this.farRightBar.func_78785_a(f5);
        }

        public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
            modelRenderer.field_78795_f = x;
            modelRenderer.field_78796_g = y;
            modelRenderer.field_78808_h = z;
        }
    }

    public class TopHatModel
    extends ModelBase {
        public final ResourceLocation hatTexture = new ResourceLocation("textures/tophat.png");
        public ModelRenderer bottom;
        public ModelRenderer top;

        public TopHatModel() {
            this.field_78090_t = 64;
            this.field_78089_u = 32;
            this.top = new ModelRenderer((ModelBase)this, 0, 10);
            this.top.func_78790_a(0.0f, 0.0f, 0.0f, 4, 10, 4, 0.0f);
            this.top.func_78793_a(-2.0f, -11.0f, -2.0f);
            this.bottom = new ModelRenderer((ModelBase)this, 0, 0);
            this.bottom.func_78790_a(0.0f, 0.0f, 0.0f, 8, 1, 8, 0.0f);
            this.bottom.func_78793_a(-4.0f, -1.0f, -4.0f);
        }

        public void func_78088_a(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
            this.top.func_78785_a(f5);
            this.bottom.func_78785_a(f5);
        }

        public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
            modelRenderer.field_78795_f = x;
            modelRenderer.field_78796_g = y;
            modelRenderer.field_78808_h = z;
        }
    }

    public static class ModelHatFez
    extends ModelBase {
        private ModelRenderer baseLayer;
        private ModelRenderer topLayer;
        private ModelRenderer stringLayer;
        private ModelRenderer danglingStringLayer;
        private ModelRenderer otherDanglingStringLayer;

        public ModelHatFez() {
            this.field_78090_t = 64;
            this.field_78089_u = 32;
            this.baseLayer = new ModelRenderer((ModelBase)this, 1, 1);
            this.baseLayer.func_78789_a(-3.0f, 0.0f, -3.0f, 6, 4, 6);
            this.baseLayer.func_78793_a(0.0f, -4.0f, 0.0f);
            this.baseLayer.func_78787_b(this.field_78090_t, this.field_78089_u);
            this.baseLayer.field_78809_i = true;
            this.setRotation(this.baseLayer, 0.0f, 0.12217305f, 0.0f);
            this.topLayer = new ModelRenderer((ModelBase)this, 1, 1);
            this.topLayer.func_78789_a(0.0f, 0.0f, 0.0f, 1, 1, 1);
            this.topLayer.func_78793_a(-0.5f, -4.75f, -0.5f);
            this.topLayer.func_78787_b(this.field_78090_t, this.field_78089_u);
            this.topLayer.field_78809_i = true;
            this.setRotation(this.topLayer, 0.0f, 0.0f, 0.0f);
            this.stringLayer = new ModelRenderer((ModelBase)this, 25, 1);
            this.stringLayer.func_78789_a(-0.5f, -0.5f, -0.5f, 3, 1, 1);
            this.stringLayer.func_78793_a(0.5f, -3.75f, 0.0f);
            this.stringLayer.func_78787_b(this.field_78090_t, this.field_78089_u);
            this.stringLayer.field_78809_i = true;
            this.setRotation(this.stringLayer, 0.7853982f, 0.0f, 0.0f);
            this.danglingStringLayer = new ModelRenderer((ModelBase)this, 41, 1);
            this.danglingStringLayer.func_78789_a(-0.5f, -0.5f, -0.5f, 3, 1, 1);
            this.danglingStringLayer.func_78793_a(3.0f, -3.5f, 0.0f);
            this.danglingStringLayer.func_78787_b(this.field_78090_t, this.field_78089_u);
            this.danglingStringLayer.field_78809_i = true;
            this.setRotation(this.danglingStringLayer, 0.2268928f, 0.7853982f, 1.2042772f);
            this.otherDanglingStringLayer = new ModelRenderer((ModelBase)this, 33, 9);
            this.otherDanglingStringLayer.func_78789_a(-0.5f, -0.5f, -0.5f, 3, 1, 1);
            this.otherDanglingStringLayer.func_78793_a(3.0f, -3.5f, 0.0f);
            this.otherDanglingStringLayer.func_78787_b(this.field_78090_t, this.field_78089_u);
            this.otherDanglingStringLayer.field_78809_i = true;
            this.setRotation(this.otherDanglingStringLayer, 0.2268928f, -0.9250245f, 1.2042772f);
        }

        public void func_78088_a(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
            super.func_78088_a(entity, f, f1, f2, f3, f4, f5);
            this.setRotationAngles(f, f1, f2, f3, f4, f5);
            this.baseLayer.func_78785_a(f5);
            this.topLayer.func_78785_a(f5);
            this.stringLayer.func_78785_a(f5);
            this.danglingStringLayer.func_78785_a(f5);
            this.otherDanglingStringLayer.func_78785_a(f5);
        }

        private void setRotation(ModelRenderer model, float x, float y, float z) {
            model.field_78795_f = x;
            model.field_78796_g = y;
            model.field_78808_h = z;
        }

        public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5) {
            super.func_78087_a(f, f1, f2, f3, f4, f5, null);
        }
    }
}

