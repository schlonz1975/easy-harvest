package com.easyharvest;

import java.util.List;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.NetherWartBlock;
import net.minecraft.world.level.block.state.BlockState;

public class EasyHarvest implements ModInitializer {
	@Override
	public void onInitialize() {
		UseBlockCallback.EVENT.register((player, level, hand, hit) -> {
			// Event fires once per hand: handle main hand only. Skip spectators.
			if (hand != InteractionHand.MAIN_HAND || player.isSpectator()) {
				return InteractionResult.PASS;
			}
			BlockPos pos = hit.getBlockPos();
			BlockState state = level.getBlockState(pos);
			Item seed = seedFor(state);
			if (seed == null || !isFullyGrown(state)) {
				return InteractionResult.PASS; // not a crop, or unripe: vanilla behavior
			}
			// Client: report success (arm swing). Server does the real work.
			if (!(level instanceof ServerLevel serverLevel)) {
				return InteractionResult.SUCCESS;
			}

			List<ItemStack> drops = Block.getDrops(state, serverLevel, pos, null, player, player.getMainHandItem());
			boolean replanted = false;
			for (ItemStack drop : drops) {
				if (!replanted && drop.is(seed)) {
					drop.shrink(1); // one seed goes back into the ground
					replanted = true;
				}
				if (!drop.isEmpty()) {
					Block.popResource(serverLevel, pos, drop);
				}
			}
			// Age 0 = default state. No seed in drops -> crop is simply removed.
			serverLevel.setBlockAndUpdate(pos, replanted ? state.getBlock().defaultBlockState() : Blocks.AIR.defaultBlockState());
			serverLevel.playSound(null, pos, SoundEvents.CROP_BREAK, SoundSource.BLOCKS, 1.0f, 1.0f);
			return InteractionResult.SUCCESS;
		});
	}

	private static Item seedFor(BlockState state) {
		Block b = state.getBlock();
		if (b == Blocks.WHEAT) return Items.WHEAT_SEEDS;
		if (b == Blocks.CARROTS) return Items.CARROT;
		if (b == Blocks.POTATOES) return Items.POTATO;
		if (b == Blocks.BEETROOTS) return Items.BEETROOT_SEEDS;
		if (b == Blocks.NETHER_WART) return Items.NETHER_WART;
		return null;
	}

	private static boolean isFullyGrown(BlockState state) {
		Block b = state.getBlock();
		if (b instanceof CropBlock crop) return crop.isMaxAge(state);
		if (b instanceof NetherWartBlock) return state.getValue(NetherWartBlock.AGE) >= 3;
		return false;
	}
}
