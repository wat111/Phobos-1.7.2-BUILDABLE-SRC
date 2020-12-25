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
 *  net.minecraftforge.client.event.RenderPlayerEvent$Post
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 *  org.lwjgl.opengl.GL11
 */
package me.earth.phobos.features.modules.render;

import me.earth.phobos.Phobos;
import me.earth.phobos.features.modules.Module;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

public class Cosmetics
extends Module {
    public final TopHatModel hatModel = new TopHatModel();
    public final GlassesModel glassesModel = new GlassesModel();
    private final HatGlassesModel hatGlassesModel = new HatGlassesModel();
    public final SantaHatModel santaHatModel = new SantaHatModel();
    public final ModelHatFez fezModel = new ModelHatFez();
    private final ResourceLocation hatTexture = new ResourceLocation("textures/tophat.png");
    private final ResourceLocation fezTexture = new ResourceLocation("textures/fez.png");
    private final ResourceLocation glassesTexture = new ResourceLocation("textures/sunglasses.png");
    private final ResourceLocation santaHatTexture = new ResourceLocation("textures/santahat.png");
    public static Cosmetics INSTANCE;

    public Cosmetics() {
        super("Cosmetics", "Bitch", Module.Category.RENDER, true, false, false);
        INSTANCE = this;
    }

    @SubscribeEvent
    public void onRenderPlayer(RenderPlayerEvent.Post event) {
        if (!Phobos.cosmeticsManager.hasCosmetics(event.getEntityPlayer())) {
            return;
        }
        GlStateManager.func_179094_E();
        RenderManager renderManager = mc.func_175598_ae();
        GlStateManager.func_179137_b((double)event.getX(), (double)event.getY(), (double)event.getZ());
        double scale = 1.0;
        double rotate = this.interpolate(event.getEntityPlayer().field_70758_at, event.getEntityPlayer().field_70759_as, event.getPartialRenderTick());
        double rotate1 = this.interpolate(event.getEntityPlayer().field_70127_C, event.getEntityPlayer().field_70125_A, event.getPartialRenderTick());
        GL11.glScaled((double)(-scale), (double)(-scale), (double)scale);
        GL11.glTranslated((double)0.0, (double)(-((double)event.getEntityPlayer().field_70131_O - (event.getEntityPlayer().func_70093_af() ? 0.25 : 0.0) - 0.38) / scale), (double)0.0);
        GL11.glRotated((double)(180.0 + rotate), (double)0.0, (double)1.0, (double)0.0);
        GL11.glRotated((double)rotate1, (double)1.0, (double)0.0, (double)0.0);
        GlStateManager.func_179137_b((double)0.0, (double)-0.45, (double)0.0);
        for (ModelBase model : Phobos.cosmeticsManager.getRenderModels(event.getEntityPlayer())) {
            if (model instanceof TopHatModel) {
                mc.func_110434_K().func_110577_a(this.hatTexture);
                this.hatModel.func_78088_a(event.getEntity(), 0.0f, 0.0f, -0.1f, 0.0f, 0.0f, 0.0625f);
                mc.func_110434_K().func_147645_c(this.hatTexture);
                continue;
            }
            if (model instanceof GlassesModel) {
                if (event.getEntityPlayer().func_175148_a(EnumPlayerModelParts.HAT)) {
                    mc.func_110434_K().func_110577_a(this.glassesTexture);
                    this.hatGlassesModel.func_78088_a(event.getEntity(), 0.0f, 0.0f, -0.1f, 0.0f, 0.0f, 0.0625f);
                    mc.func_110434_K().func_147645_c(this.glassesTexture);
                    continue;
                }
                mc.func_110434_K().func_110577_a(this.glassesTexture);
                this.glassesModel.func_78088_a(event.getEntity(), 0.0f, 0.0f, -0.1f, 0.0f, 0.0f, 0.0625f);
                mc.func_110434_K().func_147645_c(this.glassesTexture);
                continue;
            }
            if (!(model instanceof SantaHatModel)) continue;
            mc.func_110434_K().func_110577_a(this.santaHatTexture);
            this.santaHatModel.func_78088_a(event.getEntity(), 0.0f, 0.0f, -0.1f, 0.0f, 0.0f, 0.0625f);
            mc.func_110434_K().func_147645_c(this.santaHatTexture);
        }
        GlStateManager.func_179121_F();
    }

    public float interpolate(float yaw1, float yaw2, float percent) {
        float rotation = (yaw1 + (yaw2 - yaw1) * percent) % 360.0f;
        if (rotation < 0.0f) {
            rotation += 360.0f;
        }
        return rotation;
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

