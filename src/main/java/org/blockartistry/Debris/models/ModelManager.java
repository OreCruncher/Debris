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

package org.blockartistry.Debris.models;

import javax.annotation.Nonnull;

import org.blockartistry.Debris.blocks.BlockDebrisVariant;
import org.blockartistry.Debris.blocks.ModBlocks;
import org.blockartistry.Debris.util.IVariant;

import com.google.common.base.Function;

import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

// https://github.com/Choonster/TestMod3/blob/1.11.2/src/main/java/choonster/testmod3/client/model/ModModelManager.java

@Mod.EventBusSubscriber(Side.CLIENT)
public class ModelManager {

	private static final StateMapperBase propertyStringMapper = new StateMapperBase() {
		@Override
		protected ModelResourceLocation getModelResourceLocation(@Nonnull final IBlockState state) {
			return new ModelResourceLocation("minecraft:air");
		}
	};

	private ModelManager() {

	}

	@SubscribeEvent
	public static void registerAllModels(@Nonnull final ModelRegistryEvent event) {
		registerBlockModels();
		registerItemModels();
	}

	private static void registerBlockModels() {
		registerVariantBlockItemModels(ModBlocks.DEBRIS.getDefaultState().withProperty(BlockDebrisVariant.ITEM, true),
				BlockDebrisVariant.VARIANT);
	}

	private static void registerItemModels() {

	}

	private static void registerBlockItemModelForMeta(IBlockState state, int metadata) {
		final Item item = Item.getItemFromBlock(state.getBlock());

		if (item != null) {
			registerItemModelForMeta(item, metadata, propertyStringMapper.getPropertyString(state.getProperties()));
		}
	}

	private static <T extends Comparable<T>> void registerVariantBlockItemModels(IBlockState baseState,
			IProperty<T> property, Function<T, Integer> getMeta) {

		for (final T t : property.getAllowedValues()) {
			registerBlockItemModelForMeta(baseState.withProperty(property, t), getMeta.apply(t));
		}
	}

	private static <T extends IVariant & Comparable<T>> void registerVariantBlockItemModels(IBlockState baseState,
			IProperty<T> property) {
		registerVariantBlockItemModels(baseState, property, new Function<T, Integer>() {
			@Override
			public Integer apply(@Nonnull final T value) {
				return ((IVariant) value).getMeta();
			}
		});
	}

	private static void registerItemModelForMeta(Item item, int metadata, String variant) {
		registerItemModelForMeta(item, metadata, new ModelResourceLocation(item.getRegistryName(), variant));
	}

	private static void registerItemModelForMeta(Item item, int metadata, ModelResourceLocation modelResourceLocation) {
		ModelLoader.setCustomModelResourceLocation(item, metadata, modelResourceLocation);
	}
}
