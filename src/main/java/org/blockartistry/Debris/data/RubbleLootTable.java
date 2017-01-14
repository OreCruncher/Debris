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

package org.blockartistry.Debris.data;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import javax.annotation.Nonnull;

import org.blockartistry.Debris.Debris;
import org.blockartistry.Debris.ModEnvironment;
import org.blockartistry.Debris.ModLog;
import org.blockartistry.Debris.util.JsonUtils;
import org.blockartistry.Debris.util.RegistryHelper;

import com.google.gson.reflect.TypeToken;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootEntry;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.OreDictionary;

@Mod.EventBusSubscriber
public class RubbleLootTable {

	private static final Type type = new TypeToken<Map<String, ItemEntry>>() {
	}.getType();

	private static ResourceLocation tableName;

	static {
	}

	public static void init() {
		tableName = LootTableList.register(new ResourceLocation(Debris.RESOURCE_ID, "pileOfRubble"));
	}

	public static List<ItemStack> getDrops(@Nonnull final World world, final int count, final Random rand) {
		final List<ItemStack> result = new ArrayList<ItemStack>();
		final LootTable table = world.getLootTableManager().getLootTableFromLocation(tableName);

		if (table != null) {
			final LootContext.Builder builder = new LootContext.Builder((WorldServer) world);
			for (int i = 0; i < count; i++) {
				result.addAll(table.generateLootForPools(rand, builder.build()));
			}
		}
		return result;
	}

	private static void addToTable(@Nonnull final LootPool pool, @Nonnull final String itemId, final int weight,
			final int min, final int max) {
		final ItemStack stack = RegistryHelper.getItemStack(itemId);
		if (stack != null) {
			final LootEntry entry = new LootEntryBuilder().setName(itemId).setItemStack(stack).setWeight(weight)
					.setMinMax(min, max).build();
			pool.addEntry(entry);
		}
	}

	private static void process(@Nonnull final LootPool pool, @Nonnull final String modId) {
		try {
			@SuppressWarnings("unchecked")
			final Map<String, ItemEntry> entries = (Map<String, ItemEntry>) JsonUtils.load(modId, type);
			for (final Entry<String, ItemEntry> e : entries.entrySet()) {
				final ItemEntry item = e.getValue();
				addToTable(pool, e.getKey(), item.weight, item.min, item.max);
			}
		} catch (final Throwable t) {
			ModLog.warn("Unable to process loot table from [%s]", modId);
		}
	}
	
	private static void doDictionaryLookup(@Nonnull final LootPool pool, @Nonnull final String dictionaryName, final int weight, final int min, final int max) {
		
		if(OreDictionary.doesOreNameExist(dictionaryName)) {
			final List<ItemStack> stacks = OreDictionary.getOres(dictionaryName);
			if(stacks != null && !stacks.isEmpty()) {
				final ItemStack stack = stacks.get(0);
				final LootEntry entry = new LootEntryBuilder().setName(dictionaryName).setItemStack(stack).setWeight(weight)
						.setMinMax(min, max).build();
				pool.addEntry(entry);
			}
			
		}
	}

	@SubscribeEvent
	public static void onLootTableLoad(@Nonnull final LootTableLoadEvent event) {
		if (event.getName().equals(tableName)) {
			final LootPool pool = event.getTable().getPool("main");
			if (pool == null) {
				ModLog.warn("Can't find pool [main] in loot table [pileOfRubble]");
				return;
			}
			
			doDictionaryLookup(pool, "oreCopper", 5, 1, 3);
			doDictionaryLookup(pool, "oreTin", 5, 1, 3);
			
			for (final ModEnvironment me : ModEnvironment.values())
				if (me.isLoaded())
					process(pool, me.name());

		}
	}

}
