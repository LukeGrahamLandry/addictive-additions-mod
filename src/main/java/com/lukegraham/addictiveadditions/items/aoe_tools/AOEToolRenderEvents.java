package com.lukegraham.addictiveadditions.items.aoe_tools;


import com.lukegraham.addictiveadditions.AddictiveAdditions;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.mojang.blaze3d.vertex.MatrixApplyingVertexBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerController;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.DrawHighlightEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

// Class stolen from tinkers construct
// TODO: refactor this so its readable / I understand it
// PlayerController#curBlockDamageMP is made public by an access transformer
@Mod.EventBusSubscriber(modid = AddictiveAdditions.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class AOEToolRenderEvents {

    @SubscribeEvent
    static void renderBlockHighlights(DrawHighlightEvent.HighlightBlock event) {
        PlayerEntity player = Minecraft.getInstance().player;

        if (player == null) {
            return;
        }

        World world = player.world;

        ItemStack tool = player.getHeldItemMainhand();

        // AOE preview
        if (!tool.isEmpty()) {
            if (tool.getItem() instanceof AOEToolUtil.IAOEtool) {
                ActiveRenderInfo renderInfo = Minecraft.getInstance().gameRenderer.getActiveRenderInfo();
                Iterable<BlockPos> extraBlocks = ((AOEToolUtil.IAOEtool) tool.getItem()).getAOEBlocks(tool, world, player, event.getTarget().getPos());

                MatrixStack matrix = event.getMatrix();
                IVertexBuilder vertexBuilder = Minecraft.getInstance().getRenderTypeBuffers().getBufferSource().getBuffer(RenderType.getLines());
                Entity viewEntity = renderInfo.getRenderViewEntity();


                Vector3d vector3d = renderInfo.getProjectedView();
                double d0 = vector3d.getX();
                double d1 = vector3d.getY();
                double d2 = vector3d.getZ();

                matrix.push();

                for (BlockPos pos : extraBlocks) {
                    if (world.getWorldBorder().contains(pos)) {
                        drawSelectionBox(matrix, vertexBuilder, viewEntity, d0, d1, d2, pos, world.getBlockState(pos));
                    }
                }

                matrix.pop();
            }
        }
    }

    /**
     * Renders the block damage process on the extra blocks
     *
     * @param event the RenderWorldLastEvent
     */
    @SubscribeEvent
    public static void renderBlockDamageProgress(RenderWorldLastEvent event) {
        PlayerController controller = Minecraft.getInstance().playerController;
        PlayerEntity player = Minecraft.getInstance().player;

        if (controller == null || player == null) return;

        ItemStack tool = player.getHeldItemMainhand();

        if (!tool.isEmpty()) {
            if (tool.getItem() instanceof AOEToolUtil.IAOEtool) {
                Entity renderEntity = Minecraft.getInstance().getRenderViewEntity();
                BlockRayTraceResult traceResult = AOEToolUtil.rayTracePlayer(player);

                if (renderEntity == null || traceResult.getType() != RayTraceResult.Type.BLOCK) return;

                Iterable<BlockPos> extraBlocks = ((AOEToolUtil.IAOEtool) tool.getItem()).getAOEBlocks(tool, player.world, player, traceResult.getPos());

                if (controller.getIsHittingBlock()) {
                    drawBlockDamageTexture(event.getContext(), event.getMatrixStack(), Minecraft.getInstance().gameRenderer.getActiveRenderInfo(), player.getEntityWorld(), extraBlocks);
                }
            }
        }
    }

    private static void drawSelectionBox(MatrixStack matrixStackIn, IVertexBuilder bufferIn, Entity entityIn, double xIn, double yIn, double zIn, BlockPos blockPosIn, BlockState blockStateIn) {
        drawShape(matrixStackIn, bufferIn, blockStateIn.getShape(entityIn.getEntityWorld(), blockPosIn, ISelectionContext.forEntity(entityIn)), (double)blockPosIn.getX() - xIn, (double)blockPosIn.getY() - yIn, (double)blockPosIn.getZ() - zIn, 0.0F, 0.0F, 0.0F, 0.4F);
    }

    private static void drawShape(MatrixStack matrixStackIn, IVertexBuilder bufferIn, VoxelShape shapeIn, double xIn, double yIn, double zIn, float red, float green, float blue, float alpha) {
        Matrix4f matrix4f = matrixStackIn.getLast().getMatrix();
        shapeIn.forEachEdge((p_230013_12_, p_230013_14_, p_230013_16_, p_230013_18_, p_230013_20_, p_230013_22_) -> {
            bufferIn.pos(matrix4f, (float)(p_230013_12_ + xIn), (float)(p_230013_14_ + yIn), (float)(p_230013_16_ + zIn)).color(red, green, blue, alpha).endVertex();
            bufferIn.pos(matrix4f, (float)(p_230013_18_ + xIn), (float)(p_230013_20_ + yIn), (float)(p_230013_22_ + zIn)).color(red, green, blue, alpha).endVertex();
        });
    }

    /**
     * Draws the damaged texture on the given blocks
     *
     * @param worldRender the current world renderer
     * @param matrixStackIn the matrix stack
     * @param renderInfo the current render info from the client
     * @param world the active world
     * @param extraBlocks the list of blocks
     */
    private static void drawBlockDamageTexture(WorldRenderer worldRender, MatrixStack matrixStackIn, ActiveRenderInfo renderInfo, World world, Iterable<BlockPos> extraBlocks) {
        double d0 = renderInfo.getProjectedView().x;
        double d1 = renderInfo.getProjectedView().y;
        double d2 = renderInfo.getProjectedView().z;

        assert Minecraft.getInstance().playerController != null;
        int progress = (int) (Minecraft.getInstance().playerController.curBlockDamageMP * 10.0F) - 1;

        if (progress < 0) {
            return;
        }

        progress = Math.min(progress, 10); // Ensure that for whatever reason the progress level doesn't go OOB.

        BlockRendererDispatcher dispatcher = Minecraft.getInstance().getBlockRendererDispatcher();
        IVertexBuilder vertexBuilder = Minecraft.getInstance().getRenderTypeBuffers().getCrumblingBufferSource().getBuffer(ModelBakery.DESTROY_RENDER_TYPES.get(progress));

        for (BlockPos pos : extraBlocks) {
            matrixStackIn.push();
            matrixStackIn.translate((double) pos.getX() - d0, (double) pos.getY() - d1, (double) pos.getZ() - d2);
            IVertexBuilder matrixBuilder = new MatrixApplyingVertexBuilder(vertexBuilder, matrixStackIn.getLast().getMatrix(), matrixStackIn.getLast().getNormal());
            dispatcher.renderBlockDamage(world.getBlockState(pos), pos, world, matrixStackIn, matrixBuilder);
            matrixStackIn.pop();
        }
    }
}