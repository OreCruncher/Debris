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

import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nonnull;

import com.google.common.base.Function;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemMultiTexture;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.IForgeRegistry;

public class ModBlocks {

	public static final BlockDebris DEBRIS = (BlockDebris) new BlockDebris("debris")
			.setCreativeTab(CreativeTabs.BUILDING_BLOCKS);

	@Mod.EventBusSubscriber
	public static class RegistrationHandler {

		public static final Set<ItemBlock> ITEM_BLOCKS = new HashSet<>();

		@SubscribeEvent
		public static void registerBlocks(@Nonnull final RegistryEvent.Register<Block> event) {
			final IForgeRegistry<Block> registry = event.getRegistry();
			final BlockBase[] blocks = { DEBRIS };

			registry.registerAll(blocks);
		}

		@SubscribeEvent
		public static void registerItemBlocks(@Nonnull final RegistryEvent.Register<Item> event) {
			final IForgeRegistry<Item> registry = event.getRegistry();
			final ItemBlock[] items = { new ItemMultiTexture(DEBRIS, DEBRIS, new Function<ItemStack, String>() {
				@Override
				public String apply(@Nonnull final ItemStack input) {
					return ((BlockDebris) DEBRIS).getName(input);
				}
			}) };

			for (final ItemBlock item : items) {
				registry.register(item.setRegistryName(item.getBlock().getRegistryName()));
				if (item.getBlock() instanceof BlockBase) {
					((BlockBase) item.getBlock()).registerItemModel(item);
				}
				ITEM_BLOCKS.add(item);
			}
		}

	}

}