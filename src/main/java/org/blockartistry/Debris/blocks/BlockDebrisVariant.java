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
import javax.annotation.Nonnull;
import org.blockartistry.Debris.Debris;
import org.blockartistry.Debris.data.RubbleLootTable;
import org.blockartistry.Debris.util.IVariant;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockDebrisVariant extends BlockDebris {

	public static final PropertyEnum<Variant> VARIANT = PropertyEnum.<Variant>create("variant", Variant.class);
	public static final PropertyBool ITEM = PropertyBool.create("item");

	public BlockDebrisVariant(@Nonnull final Material material, @Nonnull final String name) {
		super(material, name);

		this.setDefaultState(this.blockState.getBaseState()
				.withProperty(VARIANT, BlockDebrisVariant.Variant.PILE_OF_RUBBLE).withProperty(ITEM, false));
	}

	@Override
	protected void registerLootTables() {
		for (final Variant v : Variant.values())
			RubbleLootTable.register(v.getLootTableResource());
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void getSubBlocks(@Nonnull final Item itemIn, @Nonnull final CreativeTabs tab,
			@Nonnull final List<ItemStack> list) {
		for (final Variant et : Variant.values()) {
			list.add(new ItemStack(itemIn, 1, et.getMeta()));
		}
	}

	@Nonnull
	public IBlockState getBlockState(@Nonnull final Variant type) {
		return this.getDefaultState().withProperty(VARIANT, type);
	}

	@Override
	@Nonnull
	public IBlockState getStateFromMeta(final int meta) {
		return this.getDefaultState().withProperty(VARIANT, getVariant(meta));
	}

	@Override
	public int getMetaFromState(@Nonnull final IBlockState state) {
		return state.getValue(VARIANT).getMeta();
	}

	@Override
	@Nonnull
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] { VARIANT, ITEM });
	}

	@Nonnull
	public static Variant getVariant(final int meta) {
		return Variant.byMetadata(meta);
	}

	@Nonnull
	protected ResourceLocation getLootTable(@Nonnull final IBlockState state) {
		return state.getValue(VARIANT).getLootTableResource();
	}

	public static enum Variant implements IVariant {

		PILE_OF_RUBBLE(0, MapColor.STONE, "pile_of_rubble", 20), BONE_PILE(1, MapColor.STONE, "bone_pile", 10);

		private static final Variant[] META_LOOKUP = new Variant[values().length];

		private final int meta;
		private final String name;
		private final MapColor mapColor;
		private final ResourceLocation res;
		private final int weight;

		private Variant(final int meta, @Nonnull final MapColor mapColor, @Nonnull final String name,
				final int weight) {
			this(meta, mapColor, name, name, weight);
		}

		private Variant(final int meta, @Nonnull final MapColor mapColor, @Nonnull final String name,
				@Nonnull final String lootTable, final int weight) {
			this.meta = meta;
			this.name = name;
			this.mapColor = mapColor;

			this.res = new ResourceLocation(Debris.RESOURCE_ID, lootTable);
			this.weight = weight;
		}

		@Nonnull
		public MapColor getMapColor() {
			return this.mapColor;
		}

		@Nonnull
		public ResourceLocation getLootTableResource() {
			return this.res;
		}

		public int getWeight() {
			return this.weight;
		}

		@Override
		@Nonnull
		public String toString() {
			return getName();
		}

		@Override
		public int getMeta() {
			return this.meta;
		}

		@Override
		@Nonnull
		public String getName() {
			return this.name;
		}

		@Nonnull
		public static Variant byMetadata(int meta) {
			if (meta < 0 || meta >= META_LOOKUP.length) {
				meta = 0;
			}

			return META_LOOKUP[meta];
		}

		@Nonnull
		public static String[] getVariantNames() {
			final List<String> result = new ArrayList<String>();
			for (final Variant et : values())
				result.add(et.getName());
			return result.toArray(new String[result.size()]);
		}

		static {
			for (final Variant et : values()) {
				META_LOOKUP[et.getMeta()] = et;
			}
		}
	}

}
