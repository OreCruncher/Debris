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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.blockartistry.Debris.Debris;
import org.blockartistry.Debris.data.RubbleLootTable;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockDebris extends BlockBase implements IVariants {

	public static final PropertyEnum<Variant> VARIANT = PropertyEnum.<Variant>create("variant", Variant.class);
	public static final PropertyEnum<Facing> FACING = PropertyEnum.<Facing>create("facing", Facing.class);

	protected static final AxisAlignedBB DEBRIS_AABB = new AxisAlignedBB(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.375F,
			0.9375F);

	public BlockDebris(@Nonnull final String name) {
		super(Material.ROCK, name);

		// Like cobblestone
		this.setSoundType(SoundType.GROUND);
		this.setHardness(2F);
		this.setResistance(10F);

		this.setDefaultState(this.blockState.getBaseState().withProperty(VARIANT, BlockDebris.Variant.PILE_OF_RUBBLE)
				.withProperty(FACING, Facing.NORTH));
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
	public boolean canPlaceBlockAt(@Nonnull final World world, @Nonnull final BlockPos pos) {
		return super.canPlaceBlockAt(world, pos) && this.canBlockStay(world, pos, this.getDefaultState());
	}

	public boolean canBlockStay(@Nonnull final World world, @Nonnull final BlockPos pos,
			@Nonnull final IBlockState state) {
		if (pos.getY() > 0 && pos.getY() < 256) {
			final IBlockState downState = world.getBlockState(pos.down());
			return downState.getBlock() != ModBlocks.DEBRIS && !downState.getBlock().isLeaves(downState, world, pos)
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
	public String getName(@Nonnull final ItemStack stack) {
		final int metadata = stack.getMetadata();
		return Variant.byMetadata(metadata).getName();
	}

	@Override
	public String[] getVariantNames() {
		return Variant.getVariantNames();
	}

	/**
	 * returns a list of blocks with the same ID, but different meta (eg: wood
	 * returns 4 blocks)
	 */
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(@Nonnull final Item itemIn, @Nonnull final CreativeTabs tab,
			@Nonnull final List<ItemStack> list) {
		for (final Variant et : Variant.values()) {
			list.add(new ItemStack(itemIn, 1, et.getMetadata()));
		}
	}

	@Nonnull
	public IBlockState getBlockState(@Nonnull final Variant type) {
		return getStateFromMeta(type.meta);
	}

	/**
	 * Convert the given metadata into a BlockState for this Block
	 */
	@Nonnull
	public IBlockState getStateFromMeta(final int meta) {
		return this.getDefaultState().withProperty(FACING, getFacing(meta)).withProperty(VARIANT, getVariant(meta));
	}

	/**
	 * Convert the BlockState into the correct metadata value
	 */
	public int getMetaFromState(@Nonnull final IBlockState state) {
		final int variantMeta = state.getValue(VARIANT).getMetadata();
		final int directionMeta = state.getValue(FACING).getMetadata();
		return (variantMeta << 2) | directionMeta;
	}

	@Nonnull
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] { VARIANT, FACING });
	}

	@Override
	public void onBlockHarvested(@Nonnull final World world, @Nonnull final BlockPos pos,
			@Nonnull final IBlockState state, @Nonnull final EntityPlayer player) {
		if (world.isRemote)
			return;

		final Variant type = ((Variant) state.getValue(VARIANT));
		final List<ItemStack> stacks = RubbleLootTable.getDrops(type, (World) world, player, RANDOM);
		for (final ItemStack stack : stacks)
			spawnAsEntity(world, pos, stack);
	}

	@Override
	public void dropBlockAsItemWithChance(@Nonnull final World worldIn, @Nonnull final BlockPos pos,
			@Nonnull final IBlockState state, final float chance, final int fortune) {
		// Do nothing - block effect should have taken place in
		// onBlockHarvested.
	}

	@Nonnull
	public static Facing getFacing(final int meta) {
		return Facing.byMetadata((meta >> 2) & 3);
	}

	@Nonnull
	public static Variant getVariant(final int meta) {
		return Variant.byMetadata(meta & 3);
	}

	public static enum Facing implements IStringSerializable {

		NORTH(0, "north", EnumFacing.NORTH), SOUTH(1, "south", EnumFacing.SOUTH), WEST(2, "east",
				EnumFacing.WEST), EAST(3, "west", EnumFacing.EAST)

		;

		private static final Facing[] META_LOOKUP = new Facing[values().length];

		private final String name;
		private final int meta;
		private final EnumFacing facing;

		private Facing(final int meta, @Nonnull final String name, @Nonnull final EnumFacing facing) {
			this.name = name;
			this.meta = meta;
			this.facing = facing;
		}

		public int getMetadata() {
			return this.meta;
		}

		public EnumFacing getFacing() {
			return this.facing;
		}

		@Override
		public String getName() {
			return this.name;
		}

		@Nonnull
		public static Facing byMetadata(int meta) {
			if (meta < 0 || meta >= META_LOOKUP.length) {
				meta = 0;
			}

			return META_LOOKUP[meta];
		}

		static {
			for (final Facing f : values()) {
				META_LOOKUP[f.getMetadata()] = f;
			}
		}
	}

	public static enum Variant implements IStringSerializable {

		PILE_OF_RUBBLE(0, MapColor.STONE, "pile_of_rubble");

		private final ResourceLocation res;

		private static final Variant[] META_LOOKUP = new Variant[values().length];

		private final int meta;
		private final String name;
		private final String unlocalizedName;
		private final MapColor mapColor;

		private Variant(int meta, MapColor mapColor, String name) {
			this(meta, mapColor, name, name);
		}

		private Variant(int meta, MapColor mapColor, String name, String unlocalizedName) {
			this.meta = meta;
			this.name = name;
			this.unlocalizedName = unlocalizedName;
			this.mapColor = mapColor;

			this.res = new ResourceLocation(Debris.RESOURCE_ID, name);
		}

		/**
		 * Returns the Variant's metadata value.
		 */
		public int getMetadata() {
			return this.meta;
		}

		@Nonnull
		public MapColor getMapColor() {
			return this.mapColor;
		}

		@Nonnull
		public ResourceLocation getResource() {
			return this.res;
		}

		@Nonnull
		public String toString() {
			return getName();
		}

		@Nonnull
		public static Variant byMetadata(int meta) {
			if (meta < 0 || meta >= META_LOOKUP.length) {
				meta = 0;
			}

			return META_LOOKUP[meta];
		}

		@Nonnull
		public String getName() {
			return this.name;
		}

		@Nonnull
		public String getUnlocalizedName() {
			return this.unlocalizedName;
		}

		@Nullable
		public static Variant find(@Nonnull final ResourceLocation res) {
			for (final Variant et : values())
				if (et.getResource().equals(res))
					return et;
			return null;
		}

		public static String[] getVariantNames() {
			final List<String> result = new ArrayList<String>();
			for (final Variant et : values())
				result.add(et.getName());
			return result.toArray(new String[result.size()]);
		}

		static {
			for (final Variant et : values()) {
				META_LOOKUP[et.getMetadata()] = et;
			}
		}
	}

}
