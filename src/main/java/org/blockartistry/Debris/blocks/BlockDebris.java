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
import javax.annotation.Nullable;

import org.blockartistry.Debris.Debris;
import org.blockartistry.Debris.data.RubbleLootTable;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockDebris extends BlockBase {

	// Simple low profile BB that matches the graphics (cross)
	protected static final AxisAlignedBB DEBRIS_AABB = new AxisAlignedBB(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.375F,
			0.9375F);

	public BlockDebris(@Nonnull final Material material, @Nonnull final String name) {
		super(material, name);

		registerLootTables();
	}
	
	protected void registerLootTables() {
		RubbleLootTable.register(new ResourceLocation(Debris.RESOURCE_ID, this.name));
	}
	
	@Override
	@Nonnull
	public AxisAlignedBB getBoundingBox(@Nonnull final IBlockState state, @Nonnull final IBlockAccess source,
			@Nonnull final BlockPos pos) {
		return DEBRIS_AABB;
	}

	@Override
	@Nonnull
	public AxisAlignedBB getCollisionBoundingBox(@Nonnull final IBlockState blockState, @Nonnull final World worldIn,
			@Nonnull final BlockPos pos) {
		return DEBRIS_AABB;
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
	public void dropBlockAsItemWithChance(@Nonnull final World worldIn, @Nonnull final BlockPos pos,
			@Nonnull final IBlockState state, final float chance, final int fortune) {
		// Do nothing - block effect should have taken place in
		// onBlockHarvested.
	}

	@Override
	public boolean canPlaceBlockAt(@Nonnull final World world, @Nonnull final BlockPos pos) {
		return super.canPlaceBlockAt(world, pos) && this.canBlockStay(world, pos, null);
	}

	public boolean canBlockStay(@Nonnull final World world, @Nonnull final BlockPos pos,
			@Nullable final IBlockState state) {
		if (pos.getY() > 1 && pos.getY() < 256) {
			final IBlockState downState = world.getBlockState(pos.down());
			return !(downState.getBlock() instanceof BlockDebris) && !downState.getBlock().isLeaves(downState, world, pos)
					&& downState.isSideSolid(world, pos, EnumFacing.UP);
		} else {
			return false;
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public void neighborChanged(@Nonnull final IBlockState state, @Nonnull final World world,
			@Nonnull final BlockPos pos, @Nonnull final Block block) {
		super.neighborChanged(state, world, pos, block);
		this.checkAndDropBlock(world, pos, state);
	}

	@Override
	public void updateTick(@Nonnull final World world, @Nonnull final BlockPos pos, @Nonnull final IBlockState state,
			@Nonnull final Random rand) {
		this.checkAndDropBlock(world, pos, state);
	}

	protected void checkAndDropBlock(@Nonnull final World world, @Nonnull final BlockPos pos,
			@Nonnull final IBlockState state) {
		if (!this.canBlockStay(world, pos, state)) {
			this.dropBlockAsItem(world, pos, state, 0);
			world.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
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
	
	@Nonnull
	protected ResourceLocation getLootTable(@Nonnull final IBlockState state) {
		return new ResourceLocation(Debris.RESOURCE_ID, this.name);
	}

	@Override
	public void onBlockHarvested(@Nonnull final World world, @Nonnull final BlockPos pos,
			@Nonnull final IBlockState state, @Nonnull final EntityPlayer player) {
		if (world.isRemote)
			return;

		final List<ItemStack> stacks = RubbleLootTable.getDrops(getLootTable(state), (World) world, player, RANDOM);
		for (final ItemStack stack : stacks)
			spawnAsEntity(world, pos, stack);
	}

}
