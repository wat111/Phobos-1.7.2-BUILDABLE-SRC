/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.inventory.GuiContainer
 *  net.minecraft.client.renderer.BufferBuilder
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.client.renderer.GlStateManager$DestFactor
 *  net.minecraft.client.renderer.GlStateManager$SourceFactor
 *  net.minecraft.client.renderer.RenderHelper
 *  net.minecraft.client.renderer.Tessellator
 *  net.minecraft.client.renderer.vertex.DefaultVertexFormats
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Items
 *  net.minecraft.inventory.IInventory
 *  net.minecraft.inventory.ItemStackHelper
 *  net.minecraft.inventory.Slot
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemMap
 *  net.minecraft.item.ItemShulkerBox
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.tileentity.TileEntityShulkerBox
 *  net.minecraft.util.NonNullList
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.world.World
 *  net.minecraft.world.storage.MapData
 *  net.minecraftforge.client.event.RenderTooltipEvent$PostText
 *  net.minecraftforge.event.entity.player.ItemTooltipEvent
 *  net.minecraftforge.fml.common.eventhandler.EventPriority
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 *  org.lwjgl.input.Keyboard
 */
package me.earth.phobos.features.modules.misc;

import java.awt.Color;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import me.earth.phobos.event.events.Render2DEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Bind;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.ColorUtil;
import me.earth.phobos.util.EntityUtil;
import me.earth.phobos.util.RenderUtil;
import me.earth.phobos.util.Timer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemShulkerBox;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityShulkerBox;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapData;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

public class ToolTips
extends Module {
    public Setting<Boolean> maps = this.register(new Setting<Boolean>("Maps", true));
    public Setting<Boolean> shulkers = this.register(new Setting<Boolean>("ShulkerViewer", true));
    public Setting<Bind> peek = this.register(new Setting<Bind>("Peek", new Bind(-1)));
    public Setting<Boolean> shulkerSpy = this.register(new Setting<Boolean>("ShulkerSpy", true));
    public Setting<Boolean> render = this.register(new Setting<Object>("Render", Boolean.valueOf(true), v -> this.shulkerSpy.getValue()));
    public Setting<Boolean> own = this.register(new Setting<Object>("OwnShulker", Boolean.valueOf(true), v -> this.shulkerSpy.getValue()));
    public Setting<Integer> cooldown = this.register(new Setting<Object>("ShowForS", Integer.valueOf(2), Integer.valueOf(0), Integer.valueOf(5), v -> this.shulkerSpy.getValue()));
    public Setting<Boolean> textColor = this.register(new Setting<Object>("TextColor", Boolean.valueOf(false), v -> this.shulkers.getValue()));
    private final Setting<Integer> red = this.register(new Setting<Object>("Red", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> this.textColor.getValue()));
    private final Setting<Integer> green = this.register(new Setting<Object>("Green", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> this.textColor.getValue()));
    private final Setting<Integer> blue = this.register(new Setting<Object>("Blue", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> this.textColor.getValue()));
    private final Setting<Integer> alpha = this.register(new Setting<Object>("Alpha", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> this.textColor.getValue()));
    public Setting<Boolean> offsets = this.register(new Setting<Boolean>("Offsets", false));
    private final Setting<Integer> yPerPlayer = this.register(new Setting<Object>("Y/Player", Integer.valueOf(18), v -> this.offsets.getValue()));
    private final Setting<Integer> xOffset = this.register(new Setting<Object>("XOffset", Integer.valueOf(4), v -> this.offsets.getValue()));
    private final Setting<Integer> yOffset = this.register(new Setting<Object>("YOffset", Integer.valueOf(2), v -> this.offsets.getValue()));
    private final Setting<Integer> trOffset = this.register(new Setting<Object>("TROffset", Integer.valueOf(2), v -> this.offsets.getValue()));
    public Setting<Integer> invH = this.register(new Setting<Object>("InvH", Integer.valueOf(3), v -> this.offsets.getValue()));
    private static final ResourceLocation MAP = new ResourceLocation("textures/map/map_background.png");
    private static final ResourceLocation SHULKER_GUI_TEXTURE = new ResourceLocation("textures/gui/container/shulker_box.png");
    private static ToolTips INSTANCE = new ToolTips();
    public Map<EntityPlayer, ItemStack> spiedPlayers = new ConcurrentHashMap<EntityPlayer, ItemStack>();
    public Map<EntityPlayer, Timer> playerTimers = new ConcurrentHashMap<EntityPlayer, Timer>();
    private int textRadarY = 0;

    public ToolTips() {
        super("ToolTips", "Several tweaks for tooltips.", Module.Category.MISC, true, false, false);
        this.setInstance();
    }

    private void setInstance() {
        INSTANCE = this;
    }

    public static ToolTips getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ToolTips();
        }
        return INSTANCE;
    }

    @Override
    public void onUpdate() {
        ItemStack stack;
        Slot slot;
        if (ToolTips.fullNullCheck() || !this.shulkerSpy.getValue().booleanValue()) {
            return;
        }
        if (this.peek.getValue().getKey() != -1 && ToolTips.mc.field_71462_r instanceof GuiContainer && Keyboard.isKeyDown((int)this.peek.getValue().getKey()) && (slot = ((GuiContainer)ToolTips.mc.field_71462_r).getSlotUnderMouse()) != null && (stack = slot.func_75211_c()) != null && stack.func_77973_b() instanceof ItemShulkerBox) {
            ToolTips.displayInv(stack, null);
        }
        for (EntityPlayer player : ToolTips.mc.field_71441_e.field_73010_i) {
            if (player == null || player.func_184614_ca() == null || !(player.func_184614_ca().func_77973_b() instanceof ItemShulkerBox) || EntityUtil.isFakePlayer(player) || !this.own.getValue().booleanValue() && ToolTips.mc.field_71439_g.equals((Object)player)) continue;
            ItemStack stack2 = player.func_184614_ca();
            this.spiedPlayers.put(player, stack2);
        }
    }

    @Override
    public void onRender2D(Render2DEvent event) {
        if (ToolTips.fullNullCheck() || !this.shulkerSpy.getValue().booleanValue() || !this.render.getValue().booleanValue()) {
            return;
        }
        int x = -4 + this.xOffset.getValue();
        int y = 10 + this.yOffset.getValue();
        this.textRadarY = 0;
        for (EntityPlayer player : ToolTips.mc.field_71441_e.field_73010_i) {
            Timer playerTimer;
            if (this.spiedPlayers.get((Object)player) == null) continue;
            if (player.func_184614_ca() == null || !(player.func_184614_ca().func_77973_b() instanceof ItemShulkerBox)) {
                playerTimer = this.playerTimers.get((Object)player);
                if (playerTimer == null) {
                    Timer timer = new Timer();
                    timer.reset();
                    this.playerTimers.put(player, timer);
                } else if (playerTimer.passedS(this.cooldown.getValue().intValue())) {
                    continue;
                }
            } else if (player.func_184614_ca().func_77973_b() instanceof ItemShulkerBox && (playerTimer = this.playerTimers.get((Object)player)) != null) {
                playerTimer.reset();
                this.playerTimers.put(player, playerTimer);
            }
            ItemStack stack = this.spiedPlayers.get((Object)player);
            this.renderShulkerToolTip(stack, x, y, player.func_70005_c_());
            this.textRadarY = (y += this.yPerPlayer.getValue() + 60) - 10 - this.yOffset.getValue() + this.trOffset.getValue();
        }
    }

    public int getTextRadarY() {
        return this.textRadarY;
    }

    @SubscribeEvent(priority=EventPriority.HIGHEST)
    public void makeTooltip(ItemTooltipEvent event) {
    }

    @SubscribeEvent
    public void renderTooltip(RenderTooltipEvent.PostText event) {
        MapData mapData;
        if (this.maps.getValue().booleanValue() && !event.getStack().func_190926_b() && event.getStack().func_77973_b() instanceof ItemMap && (mapData = Items.field_151098_aY.func_77873_a(event.getStack(), (World)ToolTips.mc.field_71441_e)) != null) {
            GlStateManager.func_179094_E();
            GlStateManager.func_179124_c((float)1.0f, (float)1.0f, (float)1.0f);
            RenderHelper.func_74518_a();
            mc.func_110434_K().func_110577_a(MAP);
            Tessellator instance = Tessellator.func_178181_a();
            BufferBuilder buffer = instance.func_178180_c();
            int n = 7;
            float n2 = 135.0f;
            float n3 = 0.5f;
            GlStateManager.func_179109_b((float)event.getX(), (float)((float)event.getY() - n2 * n3 - 5.0f), (float)0.0f);
            GlStateManager.func_179152_a((float)n3, (float)n3, (float)n3);
            buffer.func_181668_a(7, DefaultVertexFormats.field_181707_g);
            buffer.func_181662_b((double)(-n), (double)n2, 0.0).func_187315_a(0.0, 1.0).func_181675_d();
            buffer.func_181662_b((double)n2, (double)n2, 0.0).func_187315_a(1.0, 1.0).func_181675_d();
            buffer.func_181662_b((double)n2, (double)(-n), 0.0).func_187315_a(1.0, 0.0).func_181675_d();
            buffer.func_181662_b((double)(-n), (double)(-n), 0.0).func_187315_a(0.0, 0.0).func_181675_d();
            instance.func_78381_a();
            ToolTips.mc.field_71460_t.func_147701_i().func_148250_a(mapData, false);
            GlStateManager.func_179145_e();
            GlStateManager.func_179121_F();
        }
    }

    public void renderShulkerToolTip(ItemStack stack, int x, int y, String name) {
        NBTTagCompound blockEntityTag;
        NBTTagCompound tagCompound = stack.func_77978_p();
        if (tagCompound != null && tagCompound.func_150297_b("BlockEntityTag", 10) && (blockEntityTag = tagCompound.func_74775_l("BlockEntityTag")).func_150297_b("Items", 9)) {
            GlStateManager.func_179098_w();
            GlStateManager.func_179140_f();
            GlStateManager.func_179131_c((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
            GlStateManager.func_179147_l();
            GlStateManager.func_187428_a((GlStateManager.SourceFactor)GlStateManager.SourceFactor.SRC_ALPHA, (GlStateManager.DestFactor)GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, (GlStateManager.SourceFactor)GlStateManager.SourceFactor.ONE, (GlStateManager.DestFactor)GlStateManager.DestFactor.ZERO);
            mc.func_110434_K().func_110577_a(SHULKER_GUI_TEXTURE);
            RenderUtil.drawTexturedRect(x, y, 0, 0, 176, 16, 500);
            RenderUtil.drawTexturedRect(x, y + 16, 0, 16, 176, 54 + this.invH.getValue(), 500);
            RenderUtil.drawTexturedRect(x, y + 16 + 54, 0, 160, 176, 8, 500);
            GlStateManager.func_179097_i();
            Color color = new Color(0, 0, 0, 255);
            if (this.textColor.getValue().booleanValue()) {
                color = new Color(this.red.getValue(), this.green.getValue(), this.blue.getValue(), this.alpha.getValue());
            }
            this.renderer.drawStringWithShadow(name == null ? stack.func_82833_r() : name, x + 8, y + 6, ColorUtil.toRGBA(color));
            GlStateManager.func_179126_j();
            RenderHelper.func_74520_c();
            GlStateManager.func_179091_B();
            GlStateManager.func_179142_g();
            GlStateManager.func_179145_e();
            NonNullList nonnulllist = NonNullList.func_191197_a((int)27, (Object)ItemStack.field_190927_a);
            ItemStackHelper.func_191283_b((NBTTagCompound)blockEntityTag, (NonNullList)nonnulllist);
            for (int i = 0; i < nonnulllist.size(); ++i) {
                int iX = x + i % 9 * 18 + 8;
                int iY = y + i / 9 * 18 + 18;
                ItemStack itemStack = (ItemStack)nonnulllist.get(i);
                ToolTips.mc.func_175599_af().field_77023_b = 501.0f;
                RenderUtil.itemRender.func_180450_b(itemStack, iX, iY);
                RenderUtil.itemRender.func_180453_a(ToolTips.mc.field_71466_p, itemStack, iX, iY, null);
                ToolTips.mc.func_175599_af().field_77023_b = 0.0f;
            }
            GlStateManager.func_179140_f();
            GlStateManager.func_179084_k();
            GlStateManager.func_179131_c((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        }
    }

    public static void displayInv(ItemStack stack, String name) {
        try {
            Item item = stack.func_77973_b();
            TileEntityShulkerBox entityBox = new TileEntityShulkerBox();
            ItemShulkerBox shulker = (ItemShulkerBox)item;
            entityBox.field_145854_h = shulker.func_179223_d();
            entityBox.func_145834_a((World)ToolTips.mc.field_71441_e);
            ItemStackHelper.func_191283_b((NBTTagCompound)stack.func_77978_p().func_74775_l("BlockEntityTag"), (NonNullList)entityBox.field_190596_f);
            entityBox.func_145839_a(stack.func_77978_p().func_74775_l("BlockEntityTag"));
            entityBox.func_190575_a(name == null ? stack.func_82833_r() : name);
            new Thread(() -> {
                try {
                    Thread.sleep(200L);
                }
                catch (InterruptedException interruptedException) {
                    // empty catch block
                }
                ToolTips.mc.field_71439_g.func_71007_a((IInventory)entityBox);
            }).start();
        }
        catch (Exception exception) {
            // empty catch block
        }
    }
}

