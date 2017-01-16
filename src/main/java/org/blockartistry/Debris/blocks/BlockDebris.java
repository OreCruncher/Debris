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
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
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

	public static final PropertyEnum<EnumType> VARIANT = PropertyEnum.<EnumType>create("variant", EnumType.class);

	protected static final AxisAlignedBB DEBRIS_AABB = new AxisAlignedBB(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.375F,
			0.9375F);

	public BlockDebris(@Nonnull final String name) {
		super(Material.ROCK, name);

		// Like cobblestone
		this.setSoundType(SoundType.GROUND);
		this.setHardness(2F);
		this.setResistance(10F);

		this.setDefaultState(this.blockState.getBaseState().withProperty(VARIANT, BlockDebris.EnumType.PILE_OF_RUBBLE));
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
			return downState.getBlock() != ModBlocks.debris && !downState.getBlock().isLeaves(downState, world, pos)
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
		return EnumType.byMetadata(metadata).getName();
	}
	
	@Override
	public String[] getVariantNames() {
		return EnumType.getVariantNames();
	}
	
	/**
	 * returns a list of blocks with the same ID, but different meta (eg: wood
	 * returns 4 blocks)
	 */
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(@Nonnull final Item itemIn, @Nonnull final CreativeTabs tab,
			@Nonnull final List<ItemStack> list) {
		for (final BlockDebris.EnumType et : BlockDebris.EnumType.values()) {
			list.add(new ItemStack(itemIn, 1, et.getMetadata()));
		}
	}

	/**
	 * Convert the given metadata into a BlockState for this Block
	 */
	@Nonnull
	public IBlockState getStateFromMeta(final int meta) {
		return this.getDefaultState().withProperty(VARIANT, BlockDebris.EnumType.byMetadata(meta));
	}

	/**
	 * Convert the BlockState into the correct metadata value
	 */
	public int getMetaFromState(@Nonnull final IBlockState state) {
		return ((BlockDebris.EnumType) state.getValue(VARIANT)).getMetadata();
	}

	@Nonnull
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, VARIANT);
	}

	@Override
	@Nonnull
	public List<ItemStack> getDrops(@Nonnull final IBlockAccess world, @Nonnull final BlockPos pos,
			@Nonnull final IBlockState state, final int fortune) {
		return RubbleLootTable.getDrops(BlockDebris.EnumType.PILE_OF_RUBBLE, (World) world, RANDOM);
	}

	public static enum EnumType implements IStringSerializable {

		PILE_OF_RUBBLE(0, MapColor.STONE, "pile_of_rubble");

		private final ResourceLocation res;

		/** Array of the Block's BlockStates */
		private static final BlockDebris.EnumType[] META_LOOKUP = new BlockDebris.EnumType[values().length];
		/** The BlockState's metadata. */
		private final int meta;
		/** The EnumType's name. */
		private final String name;
		private final String unlocalizedName;
		private final MapColor mapColor;

		private EnumType(int meta, MapColor mapColor, String name) {
			this(meta, mapColor, name, name);
		}

		private EnumType(int meta, MapColor mapColor, String name, String unlocalizedName) {
			this.meta = meta;
			this.name = name;
			this.unlocalizedName = unlocalizedName;
			this.mapColor = mapColor;

			this.res = new ResourceLocation(Debris.RESOURCE_ID, name);
		}

		/**
		 * Returns the EnumType's metadata value.
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

		/**
		 * Returns an EnumType for the BlockState from a metadata value.
		 */
		@Nonnull
		public static BlockDebris.EnumType byMetadata(int meta) {
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
		public static EnumType find(@Nonnull final ResourceLocation res) {
			for (final EnumType et : values())
				if (et.getResource().equals(res))
					return et;
			return null;
		}
		
		public static String[] getVariantNames() {
			final List<String> result = new ArrayList<String>();
			for(final EnumType et: values())
				result.add(et.getName());
			return result.toArray(new String[result.size()]);
		}

		static {
			for (final BlockDebris.EnumType et : values()) {
				META_LOOKUP[et.getMetadata()] = et;
			}
		}
	}

}
