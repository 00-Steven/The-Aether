package com.aether.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.WallBlock;

import net.minecraft.block.AbstractBlock.Properties;

public class AerogelWallBlock extends WallBlock {

	public AerogelWallBlock(Properties properties) {
		super(properties);
	}

	@Override
	public boolean isTransparent(BlockState state) {
		return true;
	}
	
}
