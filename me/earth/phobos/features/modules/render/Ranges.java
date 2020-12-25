/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.client.renderer.GlStateManager$DestFactor
 *  net.minecraft.client.renderer.GlStateManager$SourceFactor
 *  net.minecraft.client.renderer.entity.RenderManager
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.util.math.RayTraceResult
 *  net.minecraft.util.math.Vec3d
 *  org.lwjgl.opengl.GL11
 */
package me.earth.phobos.features.modules.render;

import java.awt.Color;
import java.util.ArrayList;
import me.earth.phobos.Phobos;
import me.earth.phobos.event.events.Render3DEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.EntityUtil;
import me.earth.phobos.util.RenderUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

public class Ranges
extends Module {
    private final Setting<Boolean> hitSpheres = this.register(new Setting<Boolean>("HitSpheres", false));
    private final Setting<Boolean> circle = this.register(new Setting<Boolean>("Circle", true));
    private final Setting<Boolean> ownSphere = this.register(new Setting<Object>("OwnSphere", Boolean.valueOf(false), v -> this.hitSpheres.getValue()));
    private final Setting<Boolean> raytrace = this.register(new Setting<Object>("RayTrace", Boolean.valueOf(false), v -> this.circle.getValue()));
    private final Setting<Float> lineWidth = this.register(new Setting<Float>("LineWidth", Float.valueOf(1.5f), Float.valueOf(0.1f), Float.valueOf(5.0f)));
    private final Setting<Double> radius = this.register(new Setting<Double>("Radius", 4.5, 0.1, 8.0));

    public Ranges() {
        super("Ranges", "Draws a circle around the player.", Module.Category.RENDER, false, false, false);
    }

    @Override
    public void onRender3D(Render3DEvent event) {
        if (this.circle.getValue().booleanValue()) {
            GlStateManager.func_179094_E();
            RenderUtil.GLPre(this.lineWidth.getValue().floatValue());
            GlStateManager.func_179147_l();
            GlStateManager.func_187441_d((float)3.0f);
            GlStateManager.func_179090_x();
            GlStateManager.func_179132_a((boolean)false);
            GlStateManager.func_179097_i();
            GlStateManager.func_187428_a((GlStateManager.SourceFactor)GlStateManager.SourceFactor.SRC_ALPHA, (GlStateManager.DestFactor)GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, (GlStateManager.SourceFactor)GlStateManager.SourceFactor.ONE, (GlStateManager.DestFactor)GlStateManager.DestFactor.ZERO);
            RenderManager renderManager = mc.func_175598_ae();
            Color color = Color.RED;
            ArrayList<Vec3d> hVectors = new ArrayList<Vec3d>();
            double x = Ranges.mc.field_71439_g.field_70142_S + (Ranges.mc.field_71439_g.field_70165_t - Ranges.mc.field_71439_g.field_70142_S) * (double)event.getPartialTicks() - renderManager.field_78725_b;
            double y = Ranges.mc.field_71439_g.field_70137_T + (Ranges.mc.field_71439_g.field_70163_u - Ranges.mc.field_71439_g.field_70137_T) * (double)event.getPartialTicks() - renderManager.field_78726_c;
            double z = Ranges.mc.field_71439_g.field_70136_U + (Ranges.mc.field_71439_g.field_70161_v - Ranges.mc.field_71439_g.field_70136_U) * (double)event.getPartialTicks() - renderManager.field_78723_d;
            GL11.glColor4f((float)((float)color.getRed() / 255.0f), (float)((float)color.getGreen() / 255.0f), (float)((float)color.getBlue() / 255.0f), (float)((float)color.getAlpha() / 255.0f));
            GL11.glLineWidth((float)this.lineWidth.getValue().floatValue());
            GL11.glBegin((int)1);
            for (int i = 0; i <= 360; ++i) {
                Vec3d vec = new Vec3d(x + Math.sin((double)i * Math.PI / 180.0) * this.radius.getValue(), y + 0.1, z + Math.cos((double)i * Math.PI / 180.0) * this.radius.getValue());
                RayTraceResult result = Ranges.mc.field_71441_e.func_147447_a(new Vec3d(x, y + 0.1, z), vec, false, true, false);
                if (result != null && this.raytrace.getValue().booleanValue()) {
                    hVectors.add(result.field_72307_f);
                    continue;
                }
                hVectors.add(vec);
            }
            for (int j = 0; j < hVectors.size() - 1; ++j) {
                GL11.glVertex3d((double)((Vec3d)hVectors.get((int)j)).field_72450_a, (double)((Vec3d)hVectors.get((int)j)).field_72448_b, (double)((Vec3d)hVectors.get((int)j)).field_72449_c);
                GL11.glVertex3d((double)((Vec3d)hVectors.get((int)(j + 1))).field_72450_a, (double)((Vec3d)hVectors.get((int)(j + 1))).field_72448_b, (double)((Vec3d)hVectors.get((int)(j + 1))).field_72449_c);
            }
            GL11.glEnd();
            GlStateManager.func_179117_G();
            GlStateManager.func_179126_j();
            GlStateManager.func_179132_a((boolean)true);
            GlStateManager.func_179098_w();
            GlStateManager.func_179084_k();
            RenderUtil.GlPost();
            GlStateManager.func_179121_F();
        }
        if (this.hitSpheres.getValue().booleanValue()) {
            for (EntityPlayer player : Ranges.mc.field_71441_e.field_73010_i) {
                if (player == null || player.equals((Object)Ranges.mc.field_71439_g) && !this.ownSphere.getValue().booleanValue()) continue;
                Vec3d interpolated = EntityUtil.interpolateEntity((Entity)player, event.getPartialTicks());
                if (Phobos.friendManager.isFriend(player.func_70005_c_())) {
                    GL11.glColor4f((float)0.15f, (float)0.15f, (float)1.0f, (float)1.0f);
                } else if (Ranges.mc.field_71439_g.func_70032_d((Entity)player) >= 64.0f) {
                    GL11.glColor4f((float)0.0f, (float)1.0f, (float)0.0f, (float)1.0f);
                } else {
                    GL11.glColor4f((float)1.0f, (float)(Ranges.mc.field_71439_g.func_70032_d((Entity)player) / 150.0f), (float)0.0f, (float)1.0f);
                }
                RenderUtil.drawSphere(interpolated.field_72450_a, interpolated.field_72448_b, interpolated.field_72449_c, this.radius.getValue().floatValue(), 20, 15);
            }
        }
    }
}

