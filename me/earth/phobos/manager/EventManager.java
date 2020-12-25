/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Strings
 *  net.minecraft.client.gui.ScaledResolution
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.network.play.client.CPacketHeldItemChange
 *  net.minecraft.network.play.server.SPacketEntityStatus
 *  net.minecraft.network.play.server.SPacketPlayerListItem
 *  net.minecraft.network.play.server.SPacketPlayerListItem$Action
 *  net.minecraft.network.play.server.SPacketTimeUpdate
 *  net.minecraft.world.World
 *  net.minecraftforge.client.event.ClientChatEvent
 *  net.minecraftforge.client.event.RenderGameOverlayEvent$ElementType
 *  net.minecraftforge.client.event.RenderGameOverlayEvent$Post
 *  net.minecraftforge.client.event.RenderGameOverlayEvent$Text
 *  net.minecraftforge.client.event.RenderWorldLastEvent
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.event.entity.living.LivingEvent$LivingUpdateEvent
 *  net.minecraftforge.fml.common.eventhandler.Event
 *  net.minecraftforge.fml.common.eventhandler.EventPriority
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 *  net.minecraftforge.fml.common.gameevent.TickEvent$ClientTickEvent
 *  net.minecraftforge.fml.common.gameevent.TickEvent$Phase
 *  net.minecraftforge.fml.common.network.FMLNetworkEvent$ClientConnectedToServerEvent
 *  net.minecraftforge.fml.common.network.FMLNetworkEvent$ClientDisconnectionFromServerEvent
 */
package me.earth.phobos.manager;

import com.google.common.base.Strings;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import me.earth.phobos.Phobos;
import me.earth.phobos.event.events.ConnectionEvent;
import me.earth.phobos.event.events.PacketEvent;
import me.earth.phobos.event.events.Render2DEvent;
import me.earth.phobos.event.events.Render3DEvent;
import me.earth.phobos.event.events.TotemPopEvent;
import me.earth.phobos.event.events.UpdateWalkingPlayerEvent;
import me.earth.phobos.features.Feature;
import me.earth.phobos.features.command.Command;
import me.earth.phobos.features.modules.client.Managers;
import me.earth.phobos.util.Timer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraft.network.play.server.SPacketPlayerListItem;
import net.minecraft.network.play.server.SPacketTimeUpdate;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

public class EventManager
extends Feature {
    private final Timer timer = new Timer();
    private final Timer logoutTimer = new Timer();
    private boolean keyTimeout;
    private final Timer switchTimer = new Timer();
    private AtomicBoolean tickOngoing = new AtomicBoolean(false);

    public void init() {
        MinecraftForge.EVENT_BUS.register((Object)this);
    }

    public void onUnload() {
        MinecraftForge.EVENT_BUS.unregister((Object)this);
    }

    @SubscribeEvent
    public void onUpdate(LivingEvent.LivingUpdateEvent event) {
        if (!EventManager.fullNullCheck() && event.getEntity().func_130014_f_().field_72995_K && event.getEntityLiving().equals((Object)EventManager.mc.field_71439_g)) {
            Phobos.potionManager.update();
            Phobos.totemPopManager.onUpdate();
            Phobos.inventoryManager.update();
            Phobos.holeManager.update();
            Phobos.safetyManager.onUpdate();
            Phobos.moduleManager.onUpdate();
            Phobos.timerManager.update();
            if (this.timer.passedMs(Managers.getInstance().moduleListUpdates.getValue().intValue())) {
                Phobos.moduleManager.sortModules(true);
                Phobos.moduleManager.alphabeticallySortModules();
                this.timer.reset();
            }
        }
    }

    @SubscribeEvent(priority=EventPriority.HIGHEST)
    public void onTickHighest(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            this.tickOngoing.set(true);
        }
    }

    @SubscribeEvent(priority=EventPriority.LOWEST)
    public void onTickLowest(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            this.tickOngoing.set(false);
        }
    }

    public boolean ticksOngoing() {
        return this.tickOngoing.get();
    }

    @SubscribeEvent
    public void onClientConnect(FMLNetworkEvent.ClientConnectedToServerEvent event) {
        this.logoutTimer.reset();
        Phobos.moduleManager.onLogin();
    }

    @SubscribeEvent
    public void onClientDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        Phobos.moduleManager.onLogout();
        Phobos.totemPopManager.onLogout();
        Phobos.potionManager.onLogout();
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (EventManager.fullNullCheck()) {
            return;
        }
        Phobos.moduleManager.onTick();
    }

    @SubscribeEvent(priority=EventPriority.HIGHEST)
    public void onUpdateWalkingPlayer(UpdateWalkingPlayerEvent event) {
        if (EventManager.fullNullCheck()) {
            return;
        }
        if (event.getStage() == 0) {
            Phobos.baritoneManager.onUpdateWalkingPlayer();
            Phobos.speedManager.updateValues();
            Phobos.rotationManager.updateRotations();
            Phobos.positionManager.updatePosition();
        }
        if (event.getStage() == 1) {
            Phobos.rotationManager.restoreRotations();
            Phobos.positionManager.restorePosition();
        }
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        if (event.getPacket() instanceof CPacketHeldItemChange) {
            this.switchTimer.reset();
        }
    }

    public boolean isOnSwitchCoolDown() {
        return !this.switchTimer.passedMs(500L);
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {
        if (event.getStage() != 0) {
            return;
        }
        Phobos.serverManager.onPacketReceived();
        if (event.getPacket() instanceof SPacketEntityStatus) {
            SPacketEntityStatus packet = (SPacketEntityStatus)event.getPacket();
            if (packet.func_149160_c() == 35 && packet.func_149161_a((World)EventManager.mc.field_71441_e) instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer)packet.func_149161_a((World)EventManager.mc.field_71441_e);
                MinecraftForge.EVENT_BUS.post((Event)new TotemPopEvent(player));
                Phobos.totemPopManager.onTotemPop(player);
                Phobos.potionManager.onTotemPop(player);
            }
        } else if (event.getPacket() instanceof SPacketPlayerListItem && !EventManager.fullNullCheck() && this.logoutTimer.passedS(1.0)) {
            SPacketPlayerListItem packet = (SPacketPlayerListItem)event.getPacket();
            if (!SPacketPlayerListItem.Action.ADD_PLAYER.equals((Object)packet.func_179768_b()) && !SPacketPlayerListItem.Action.REMOVE_PLAYER.equals((Object)packet.func_179768_b())) {
                return;
            }
            packet.func_179767_a().stream().filter(Objects::nonNull).filter(data -> !Strings.isNullOrEmpty((String)data.func_179962_a().getName()) || data.func_179962_a().getId() != null).forEach(data -> {
                UUID id = data.func_179962_a().getId();
                switch (packet.func_179768_b()) {
                    case ADD_PLAYER: {
                        String name = data.func_179962_a().getName();
                        MinecraftForge.EVENT_BUS.post((Event)new ConnectionEvent(0, id, name));
                        break;
                    }
                    case REMOVE_PLAYER: {
                        EntityPlayer entity = EventManager.mc.field_71441_e.func_152378_a(id);
                        if (entity != null) {
                            String logoutName = entity.func_70005_c_();
                            MinecraftForge.EVENT_BUS.post((Event)new ConnectionEvent(1, entity, id, logoutName));
                            break;
                        }
                        MinecraftForge.EVENT_BUS.post((Event)new ConnectionEvent(2, id, null));
                    }
                }
            });
        } else if (event.getPacket() instanceof SPacketTimeUpdate) {
            Phobos.serverManager.update();
        }
    }

    @SubscribeEvent
    public void onWorldRender(RenderWorldLastEvent event) {
        if (event.isCanceled()) {
            return;
        }
        EventManager.mc.field_71424_I.func_76320_a("phobos");
        GlStateManager.func_179090_x();
        GlStateManager.func_179147_l();
        GlStateManager.func_179118_c();
        GlStateManager.func_179120_a((int)770, (int)771, (int)1, (int)0);
        GlStateManager.func_179103_j((int)7425);
        GlStateManager.func_179097_i();
        GlStateManager.func_187441_d((float)1.0f);
        Render3DEvent render3dEvent = new Render3DEvent(event.getPartialTicks());
        Phobos.moduleManager.onRender3D(render3dEvent);
        GlStateManager.func_187441_d((float)1.0f);
        GlStateManager.func_179103_j((int)7424);
        GlStateManager.func_179084_k();
        GlStateManager.func_179141_d();
        GlStateManager.func_179098_w();
        GlStateManager.func_179126_j();
        GlStateManager.func_179089_o();
        GlStateManager.func_179089_o();
        GlStateManager.func_179132_a((boolean)true);
        GlStateManager.func_179098_w();
        GlStateManager.func_179147_l();
        GlStateManager.func_179126_j();
        EventManager.mc.field_71424_I.func_76319_b();
    }

    @SubscribeEvent
    public void renderHUD(RenderGameOverlayEvent.Post event) {
        if (event.getType() == RenderGameOverlayEvent.ElementType.HOTBAR) {
            Phobos.textManager.updateResolution();
        }
    }

    @SubscribeEvent(priority=EventPriority.LOW)
    public void onRenderGameOverlayEvent(RenderGameOverlayEvent.Text event) {
        if (event.getType().equals((Object)RenderGameOverlayEvent.ElementType.TEXT)) {
            ScaledResolution resolution = new ScaledResolution(mc);
            Render2DEvent render2DEvent = new Render2DEvent(event.getPartialTicks(), resolution);
            Phobos.moduleManager.onRender2D(render2DEvent);
            GlStateManager.func_179131_c((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        }
    }

    @SubscribeEvent(priority=EventPriority.HIGHEST)
    public void onChatSent(ClientChatEvent event) {
        if (event.getMessage().startsWith(Command.getCommandPrefix())) {
            event.setCanceled(true);
            try {
                EventManager.mc.field_71456_v.func_146158_b().func_146239_a(event.getMessage());
                if (event.getMessage().length() > 1) {
                    Phobos.commandManager.executeCommand(event.getMessage().substring(Command.getCommandPrefix().length() - 1));
                } else {
                    Command.sendMessage("Please enter a command.");
                }
            }
            catch (Exception e) {
                e.printStackTrace();
                Command.sendMessage("\u00a7cAn error occurred while running this command. Check the log!");
            }
            event.setMessage("");
        }
    }
}

