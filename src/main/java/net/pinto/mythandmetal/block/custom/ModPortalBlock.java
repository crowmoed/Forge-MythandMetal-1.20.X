package net.pinto.mythandmetal.block.custom;

import net.minecraft.core.BlockPos;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;


import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;


import net.minecraft.world.phys.BlockHitResult;


import net.pinto.mythandmetal.block.ModBlocks;
import net.pinto.mythandmetal.worldgen.dimension.ModDimensions;



public class ModPortalBlock extends Block {

    public ModPortalBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (pPlayer.canChangeDimensions()) {
            handleKaupenPortal(pPlayer, pPos);
            return InteractionResult.SUCCESS;
        } else {
            return InteractionResult.CONSUME;
        }
    }

    private void handleKaupenPortal(Entity player, BlockPos portalBlockPos) {
        if (player.level() instanceof ServerLevel currentLevel) {
            ServerPlayer serverPlayer = (ServerPlayer) player;
            MinecraftServer minecraftServer = currentLevel.getServer();

            ResourceKey<Level> targetDimensionKey = player.level().dimension() == ModDimensions.MYTHANDMETAL_LEVEL_KEY
                    ? Level.OVERWORLD
                    : ModDimensions.MYTHANDMETAL_LEVEL_KEY;

            ServerLevel targetDimension = minecraftServer.getLevel(targetDimensionKey);

            if (targetDimension != null && !player.isPassenger()) {
                serverPlayer.changeDimension(targetDimension);

                BlockPos targetPortalPos;
                if (targetDimensionKey == ModDimensions.MYTHANDMETAL_LEVEL_KEY) {
                    targetPortalPos = new BlockPos(0, portalBlockPos.getY(), 0); // Fixed position in the modded dimension
                    serverPlayer.getPersistentData().putIntArray("portalPosition", new int[]{portalBlockPos.getX(), portalBlockPos.getY(), portalBlockPos.getZ()});
                } else {
                    int[] savedPortalPos = serverPlayer.getPersistentData().getIntArray("portalPosition");
                    targetPortalPos = new BlockPos(savedPortalPos[0], savedPortalPos[1], savedPortalPos[2]);
                }

                targetPortalPos = ensureSafePortalLocation(targetDimension, targetPortalPos);

                serverPlayer.teleportTo(
                        targetDimension,
                        targetPortalPos.getX() + 0.5, // Center the player on the block
                        targetPortalPos.getY(),
                        targetPortalPos.getZ() + 0.5,
                        player.getYRot(),
                        player.getXRot());
            }
        }
    }


    private BlockPos ensureSafePortalLocation(ServerLevel targetDimension, BlockPos portalPos) {
        BlockState portalState = targetDimension.getBlockState(portalPos);



        return portalPos;
    }






}
