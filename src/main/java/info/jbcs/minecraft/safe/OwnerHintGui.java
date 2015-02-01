package info.jbcs.minecraft.safe;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

public class OwnerHintGui {
    private Minecraft mc;

    static boolean hoveringBlock;
    static int hoverX;
    static int hoverY;
    static int hoverZ;

    public OwnerHintGui(Minecraft minecraft) {
        super();
        // We need this to invoke the render engine.
        this.mc = minecraft;
    }
    @SubscribeEvent(priority = EventPriority.NORMAL)
    public void onRenderInfo(RenderGameOverlayEvent.Post  event){
        if (event.isCancelable() || event.type != RenderGameOverlayEvent.ElementType.EXPERIENCE) {
            return;
        } else {
            hoveringBlock=false;

            if (mc == null || mc.thePlayer == null || mc.theWorld == null) {
                return;
            }
            EntityPlayer player = mc.thePlayer;
            World world = mc.theWorld;
            MovingObjectPosition mop = General.getMovingObjectPositionFromPlayer(world, player, false);
            if (mop == null) {
                return;
            }

            if(mc.objectMouseOver != null && mc.objectMouseOver.entityHit != null) {
                Entity Target = mc.objectMouseOver.entityHit;
                if(!(Target instanceof EntityFallingSafe)){
                    return;
                }
            }

            hoverX=mop.blockX;
            hoverY=mop.blockY;
            hoverZ=mop.blockZ;
            hoveringBlock=true;
        }
    }
}