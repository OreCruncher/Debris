/*
 * This file is part of Debris, licensed under the MIT License (MIT).
 *
 * Copyright (c) OreCruncher
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.blockartistry.Debris.blocks;

import java.util.List;
import java.util.Random;

import javax.annotation.Nonnull;

import org.blockartistry.Debris.ModOptions;
import org.blockartistry.Debris.data.RubbleLootTable;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockRubble extends BlockBase {

	protected static final AxisAlignedBB RUBBLE_AABB = new AxisAlignedBB(0.0D, 0.0D, 0D, 1D, 0.5D, 1D);

	public BlockRubble(@Nonnull final String name) {
		super(Material.GROUND, name);

		// Like gravel
		this.setSoundType(SoundType.GROUND);
		this.setHardness(0.6F);
	}

	@Override
	@Nonnull
	public AxisAlignedBB getBoundingBox(@Nonnull final IBlockState state, @Nonnull final IBlockAccess source,
			@Nonnull final BlockPos pos) {
		return RUBBLE_AABB;
	}

	@Override
	@Nonnull
	public AxisAlignedBB getCollisionBoundingBox(@Nonnull final IBlockState blockState, @Nonnull final World worldIn,
			@Nonnull final BlockPos pos) {
		return RUBBLE_AABB;
	}

	@Override
	public int quantityDropped(@Nonnull final Random random) {
		return 0;
	}

	@Override
	public boolean canSilkHarvest() {
		return false;
	}

	@Override
	public boolean canPlaceBlockAt(@Nonnull final World worldIn, @Nonnull final BlockPos pos) {
		return super.canPlaceBlockAt(worldIn, pos) && this.canBlockStay(worldIn, pos, this.getDefaultState());
	}

	public boolean canBlockStay(@Nonnull final World worldIn, @Nonnull final BlockPos pos,
			@Nonnull final IBlockState state) {
		if (pos.getY() >= 0 && pos.getY() < 256) {
			final IBlockState iblockstate = worldIn.getBlockState(pos.down());
			return iblockstate.getMaterial().isSolid();
		} else {
			return false;
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public void neighborChanged(@Nonnull final IBlockState state, @Nonnull final World worldIn,
			@Nonnull final BlockPos pos, @Nonnull final Block blockIn) {
		super.neighborChanged(state, worldIn, pos, blockIn);
		this.checkAndDropBlock(worldIn, pos, state);
	}

	@Override
	public void updateTick(@Nonnull final World worldIn, @Nonnull final BlockPos pos, @Nonnull final IBlockState state,
			@Nonnull final Random rand) {
		this.checkAndDropBlock(worldIn, pos, state);
	}

	protected void checkAndDropBlock(@Nonnull final World worldIn, @Nonnull final BlockPos pos,
			@Nonnull final IBlockState state) {
		if (!this.canBlockStay(worldIn, pos, state)) {
			this.dropBlockAsItem(worldIn, pos, state, 0);
			worldIn.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
		}
	}

	@Override
	public boolean isOpaqueCube(@Nonnull final IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullCube(@Nonnull final IBlockState state) {
		return false;
	}

	@SideOnly(Side.CLIENT)
	@Override
	@Nonnull
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	@Nonnull
	public List<ItemStack> getDrops(@Nonnull final IBlockAccess world, @Nonnull final BlockPos pos,
			@Nonnull final IBlockState state, final int fortune) {
		return RubbleLootTable.getDrops((World) world, ModOptions.rubbleDropCount, RANDOM);
	}
}
