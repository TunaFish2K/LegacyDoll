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

        if (!ModConfig.dollEnabled) {
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

        // Get player dimensions dynamically
        float playerHeight = player.height;  // ~1.8 blocks
        float playerWidth = player.width;    // ~0.6 blocks

        // Calculate model dimensions after scaling
        int modelHeight = (int)(scale * playerHeight);

        // Calculate model width after scaling
        // The visual width includes arms spread out and rotation effects
        // Use configurable multiplier (widthMultiplier / 10.0) for fine-tuning
        float visualWidth = playerWidth * (ModConfig.widthMultiplier / 10.0F);
        int modelWidth = (int)(scale * visualWidth);

        // Calculate anchor point for scaling
        // Left side (0): anchor at left margin, model expands right-down
        // Right side (1): anchor at right margin, model expands left-down
        //   For right side, need to offset left by modelWidth so right edge stays at margin
        int posX = ModConfig.side == 0
            ? ModConfig.horizontalMargin
            : screenWidth - ModConfig.horizontalMargin - modelWidth;

        // For vertical: anchor at top margin, model expands downward
        // After all transformations (rotate + translate in model space + scale),
        // the head ends up at: posY - 2*modelHeight
        // the feet ends up at: posY - modelHeight
        // To keep head at verticalMargin: verticalMargin = posY - 2*modelHeight
        // Therefore: posY = verticalMargin + 2*modelHeight
        int posY = ModConfig.verticalMargin + 2 * modelHeight;

        GlStateManager.translate(posX, posY, 50.0F);
        GlStateManager.scale(-scale, scale, scale);
        // Offset in model space to make top of head the anchor point instead of feet
        // Use actual player height so top stays at verticalMargin
        GlStateManager.translate(0, -playerHeight, 0);
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
