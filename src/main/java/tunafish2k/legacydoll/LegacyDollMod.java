package tunafish2k.legacydoll;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = "legacydoll", useMetadata=true)
public class LegacyDollMod {
    public static ModConfig config;
    private final Minecraft mc = Minecraft.getMinecraft();


    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
        config = new ModConfig();
    }

    @SubscribeEvent
    public void onRenderGameOverlay(RenderGameOverlayEvent.Post event) {
        // only render after the hud is fully rendered
        if (event.type != RenderGameOverlayEvent.ElementType.ALL) {
            return;
        }

        if (!ModConfig.enabled) {
            return;
        }

        EntityPlayerSP player = mc.thePlayer;
        if (player == null) {
            return;
        }

        renderPlayerModel(player);
    }

    private void renderPlayerModel(EntityPlayerSP player) {
        // preserve previous things rendered
        GlStateManager.enableColorMaterial();
        GlStateManager.pushMatrix();

        ScaledResolution scaledRes = new ScaledResolution(mc);
        int screenWidth = scaledRes.getScaledWidth();

        int scale = ModConfig.scale;

        // a rough calculation, may require further optimization
        int modelWidth = (int)(scale * 1.5);

        int posX = ModConfig.side == 0 ? ModConfig.horizontalMargin : screenWidth - ModConfig.horizontalMargin - modelWidth;
        int posY = ModConfig.verticalMargin;

        GlStateManager.translate(posX, posY, 50.0F);
        GlStateManager.scale(-scale, scale, scale);
        GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);

        // save original rotations
        float renderYawOffset = player.renderYawOffset;
        float rotationYaw = player.rotationYaw;
        float rotationPitch = player.rotationPitch;
        float prevRotationYawHead = player.prevRotationYawHead;
        float rotationYawHead = player.rotationYawHead;

        // lighting
        GlStateManager.rotate(135.0F, 0.0F, 1.0F, 0.0F);
        RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.rotate(-135.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-((float) Math.atan(0.2F)) * 20.0F, 1.0F, 0.0F, 0.0F);

        // watch camera
        player.renderYawOffset = 0.0F;
        player.rotationYaw = 0.0F;
        player.rotationPitch = 0.0F;
        player.rotationYawHead = 0.0F;
        player.prevRotationYawHead = 0.0F;

        GlStateManager.enableAlpha();

        // rendering
        mc.getRenderManager().playerViewY = 180.0F;
        mc.getRenderManager().renderEntityWithPosYaw(
                player,
                0.0D, 0.0D, 0.0D,
                0.0F,
                1.0F
        );

        // restore rotations
        player.renderYawOffset = renderYawOffset;
        player.rotationYaw = rotationYaw;
        player.rotationPitch = rotationPitch;
        player.prevRotationYawHead = prevRotationYawHead;
        player.rotationYawHead = rotationYawHead;

        // restore opengl
        GlStateManager.popMatrix();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
    }
}
