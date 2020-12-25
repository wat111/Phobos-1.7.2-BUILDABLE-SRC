/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.client.renderer.GlStateManager$DestFactor
 *  net.minecraft.client.renderer.GlStateManager$SourceFactor
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.projectile.EntityArrow
 *  net.minecraft.entity.projectile.EntityThrowable
 *  net.minecraft.util.math.Vec3d
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 *  org.lwjgl.opengl.GL11
 */
package me.earth.phobos.features.modules.render;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import me.earth.phobos.event.events.Render3DEvent;
import me.earth.phobos.event.events.UpdateWalkingPlayerEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.RenderUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

public class Trails
extends Module {
    private final Setting<Float> lineWidth = this.register(new Setting<Float>("LineWidth", Float.valueOf(1.5f), Float.valueOf(0.1f), Float.valueOf(5.0f)));
    private final Setting<Integer> red = this.register(new Setting<Integer>("Red", 0, 0, 255));
    private final Setting<Integer> green = this.register(new Setting<Integer>("Green", 255, 0, 255));
    private final Setting<Integer> blue = this.register(new Setting<Integer>("Blue", 0, 0, 255));
    private final Setting<Integer> alpha = this.register(new Setting<Integer>("Alpha", 255, 0, 255));
    private Map<Entity, List<Vec3d>> renderMap = new HashMap<Entity, List<Vec3d>>();

    public Trails() {
        super("Trails", "Draws trails on projectiles", Module.Category.RENDER, true, false, false);
    }

    @SubscribeEvent
    public void onUpdateWalkingPlayer(UpdateWalkingPlayerEvent event) {
        for (Entity entity : Trails.mc.field_71441_e.field_72996_f) {
            if (!(entity instanceof EntityThrowable) && !(entity instanceof EntityArrow)) continue;
            List<Vec3d> vectors = this.renderMap.get((Object)entity) != null ? this.renderMap.get((Object)entity) : new ArrayList<Vec3d>();
            vectors.add(new Vec3d(entity.field_70165_t, entity.field_70163_u, entity.field_70161_v));
            this.renderMap.put(entity, vectors);
        }
    }

    @Override
    public void onRender3D(Render3DEvent event) {
        for (Entity entity : Trails.mc.field_71441_e.field_72996_f) {
            if (!this.renderMap.containsKey((Object)entity)) continue;
            GlStateManager.func_179094_E();
            RenderUtil.GLPre(this.lineWidth.getValue().floatValue());
            GlStateManager.func_179147_l();
            GlStateManager.func_179090_x();
            GlStateManager.func_179132_a((boolean)false);
            GlStateManager.func_179097_i();
            GlStateManager.func_187428_a((GlStateManager.SourceFactor)GlStateManager.SourceFactor.SRC_ALPHA, (GlStateManager.DestFactor)GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, (GlStateManager.SourceFactor)GlStateManager.SourceFactor.ONE, (GlStateManager.DestFactor)GlStateManager.DestFactor.ZERO);
            GL11.glColor4f((float)((float)this.red.getValue().intValue() / 255.0f), (float)((float)this.green.getValue().intValue() / 255.0f), (float)((float)this.blue.getValue().intValue() / 255.0f), (float)((float)this.alpha.getValue().intValue() / 255.0f));
            GL11.glLineWidth((float)this.lineWidth.getValue().floatValue());
            GL11.glBegin((int)1);
            for (int i = 0; i < this.renderMap.get((Object)entity).size() - 1; ++i) {
                GL11.glVertex3d((double)this.renderMap.get((Object)entity).get((int)i).field_72450_a, (double)this.renderMap.get((Object)entity).get((int)i).field_72448_b, (double)this.renderMap.get((Object)entity).get((int)i).field_72449_c);
                GL11.glVertex3d((double)this.renderMap.get((Object)entity).get((int)(i + 1)).field_72450_a, (double)this.renderMap.get((Object)entity).get((int)(i + 1)).field_72448_b, (double)this.renderMap.get((Object)entity).get((int)(i + 1)).field_72449_c);
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
    }
}

