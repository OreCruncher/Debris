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

import java.util.List;
import java.util.Random;

import javax.annotation.Nonnull;

import org.blockartistry.Debris.ModEnvironment;
import org.blockartistry.Debris.ModLog;
import org.blockartistry.Debris.ModOptions;
import org.blockartistry.Debris.blocks.BlockDebris;
import org.blockartistry.Debris.util.JUtils;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootEntry;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.conditions.LootConditionManager;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.minecraft.world.storage.loot.functions.LootFunctionManager;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.OreDictionary;

@Mod.EventBusSubscriber
public class RubbleLootTable {

	private static final Gson GSON_INSTANCE = (new GsonBuilder())
			.registerTypeAdapter(RandomValueRange.class, new RandomValueRange.Serializer())
			.registerTypeHierarchyAdapter(LootEntry.class, new LootEntrySerializer())
			.registerTypeHierarchyAdapter(LootFunction.class, new LootFunctionManager.Serializer())
			.registerTypeHierarchyAdapter(LootCondition.class, new LootConditionManager.Serializer())
			.registerTypeHierarchyAdapter(LootContext.EntityTarget.class, new LootContext.EntityTarget.Serializer())
			.create();

	private static final TypeToken<List<LootEntry>> TOKEN_TYPE = new TypeToken<List<LootEntry>>() {
	};

	static {
	}

	public static void init() {
		for (final BlockDebris.EnumType rt : BlockDebris.EnumType.values())
			LootTableList.register(rt.getResource());
	}

	@Nonnull
	public static List<ItemStack> getDrops(@Nonnull final BlockDebris.EnumType rt, @Nonnull final World world,
			@Nonnull final Random rand) {
		final LootTable table = world.getLootTableManager().getLootTableFromLocation(rt.getResource());
		if (table != null) {
			final LootContext.Builder builder = new LootContext.Builder((WorldServer) world);
			return table.generateLootForPools(rand, builder.build());
		}
		return ImmutableList.of();
	}

	private static boolean duplicateCheck(@Nonnull final String modId, @Nonnull final LootPool pool,
			@Nonnull final LootEntry entry, final boolean remove) {
		if (pool.getEntry(entry.getEntryName()) != null) {
			ModLog.warn("Duplicate entry [%s] from [%s] detected for pool [%s]", entry.getEntryName(), modId,
					pool.getName());
			if (remove)
				pool.removeEntry(entry.getEntryName());
			else
				return false;
		}
		return true;
	}

	private static void process(@Nonnull final LootPool pool, @Nonnull final String modId) {
		try {
			final List<LootEntry> entries = (List<LootEntry>) JUtils.load(modId, GSON_INSTANCE, TOKEN_TYPE);
			for (final LootEntry le : entries)
				if (duplicateCheck(modId, pool, le, true))
					pool.addEntry(le);
		} catch (final Throwable t) {
			ModLog.warn("Unable to process loot table from [%s]", modId);
		}
	}

	private static void applyDictionary(@Nonnull final LootPool pool, @Nonnull final String dictionaryName,
			final int weight, final int min, final int max) {

		if (OreDictionary.doesOreNameExist(dictionaryName)) {
			final List<ItemStack> stacks = OreDictionary.getOres(dictionaryName);
			if (stacks != null && !stacks.isEmpty()) {
				final ItemStack stack = stacks.get(0);
				final LootEntry entry = new LootEntryBuilder(dictionaryName).setItemStack(stack).setWeight(weight)
						.setAmount(min, max).build();
				pool.addEntry(entry);
			}

		}
	}

	@SubscribeEvent(priority = EventPriority.HIGH, receiveCanceled = false)
	public static void onLootTableLoad(@Nonnull final LootTableLoadEvent event) {
		final BlockDebris.EnumType rt = BlockDebris.EnumType.find(event.getName());
		if (rt != null) {
			final LootPool pool = event.getTable().getPool("main");
			if (pool == null) {
				ModLog.warn("Can't find pool [main] in loot table [%s]", rt.name());
				return;
			}

			pool.setRolls(new RandomValueRange(ModOptions.rubbleRollsMin, ModOptions.rubbleRollsMax));

			applyDictionary(pool, "oreCopper", 50, 1, 2);
			applyDictionary(pool, "oreTin", 50, 1, 2);

			for (final ModEnvironment me : ModEnvironment.values())
				if (me.isLoaded())
					process(pool, me.name());

		}
	}

}
