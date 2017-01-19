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

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import org.blockartistry.Debris.ModLog;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.google.common.io.Resources;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootEntry;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.conditions.LootConditionManager;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.minecraft.world.storage.loot.functions.LootFunctionManager;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public final class Loot {

	/**
	 * Handy well known things
	 */
	public static final LootCondition[] EMPTY_CONDITIONS = {};
	public static final LootPool[] EMPTY_POOLS = {};
	public static final LootEntry[] EMPTY_ENTRIES = {};

	private static final Gson GSON_INSTANCE = (new GsonBuilder())
			.registerTypeAdapter(RandomValueRange.class, new RandomValueRange.Serializer())
			.registerTypeAdapter(LootPool.class, new LootPool.Serializer())
			.registerTypeAdapter(LootTable.class, new LootTable.Serializer())
			.registerTypeHierarchyAdapter(LootEntry.class, new LootEntry.Serializer())
			.registerTypeHierarchyAdapter(LootFunction.class, new LootFunctionManager.Serializer())
			.registerTypeHierarchyAdapter(LootCondition.class, new LootConditionManager.Serializer())
			.registerTypeHierarchyAdapter(LootContext.EntityTarget.class, new LootContext.EntityTarget.Serializer())
			.create();

	private static final Field POOLS;
	private static final Field ENTRIES;

	@SuppressWarnings("unchecked")
	private static List<LootPool> getPools(@Nonnull final LootTable table) {
		try {
			return (List<LootPool>) POOLS.get(table);
		} catch (@Nonnull final Throwable t) {
			ModLog.error("Cannot locate 'pools' in table", t);
		}

		return ImmutableList.of();
	}

	@SuppressWarnings("unchecked")
	private static List<LootEntry> getEntries(@Nonnull final LootPool pool) {
		try {
			return (List<LootEntry>) ENTRIES.get(pool);
		} catch (@Nonnull final Throwable t) {
			ModLog.error("Cannot locate 'entries' in pool", t);
		}

		return ImmutableList.of();
	}

	static {
		POOLS = ReflectionHelper.findField(LootTable.class, "pools", "field_186466_c");
		ENTRIES = ReflectionHelper.findField(LootPool.class, "lootEntries", "field_186453_a");
	}

	private Loot() {

	}

	/**
	 * Loads a LootTable from the specified URL.
	 */
	public static LootTable loadLootTable(@Nonnull final ResourceLocation resource, @Nonnull final URL url) {
		String s;

		try {
			s = Resources.toString(url, Charsets.UTF_8);
		} catch (@Nonnull final IOException ioexception) {
			ModLog.error("Couldn\'t load loot table " + resource + " from " + url, ioexception);
			return LootTable.EMPTY_LOOT_TABLE;
		}

		try {
			return net.minecraftforge.common.ForgeHooks.loadLootTable(GSON_INSTANCE, resource, s, false);
		} catch (@Nonnull final JsonParseException jsonparseexception) {
			ModLog.error("Couldn\'t load loot table " + resource + " from " + url, jsonparseexception);
			return LootTable.EMPTY_LOOT_TABLE;
		}
	}

	/**
	 * Loads a LootTable from the specfied file location.
	 */
	public static LootTable loadLootTable(@Nonnull final ResourceLocation resource, @Nonnull final File file) {
		try {
			return loadLootTable(resource, file.toURI().toURL());
		} catch (@Nonnull final MalformedURLException e) {
			ModLog.error("Couldn\'t load loot table from " + file.toString(), e);
		}
		return LootTable.EMPTY_LOOT_TABLE;
	}

	/**
	 * Loads a LootTable from the specified resource location. It is assumed
	 * that the source Json is coming from within the mod's archive (JAR).
	 */
	@Nonnull
	public static LootTable loadLootTable(@Nonnull final ResourceLocation resource) {

		final URL url = Loot.class.getResource(
				"/assets/" + resource.getResourceDomain() + "/loot_tables/" + resource.getResourcePath() + ".json");

		return url != null ? loadLootTable(resource, url) : LootTable.EMPTY_LOOT_TABLE;
	}

	/**
	 * Merges a LootPool into a target table.
	 */
	public static LootPool merge(@Nonnull final LootTable target, @Nonnull final LootPool pool) {
		final LootPool targetPool = target.getPool(pool.getName());
		if (targetPool == null) {
			ModLog.info("Adding pool [%s] to table", pool.getName());
			target.addPool(pool);
		} else {
			// Collect the duplicates in the pool so we can get
			// rid of them.
			final List<String> duplicates = new ArrayList<String>();
			for (final LootEntry le : getEntries(pool))
				if (targetPool.getEntry(le.getEntryName()) != null)
					duplicates.add(le.getEntryName());

			// Get rid of the dupes
			for (final String dupe : duplicates) {
				ModLog.info("Replacing entry [%s] in pool [%s]", dupe, pool.getName());
				targetPool.removeEntry(dupe);
			}

			// Add the entries
			for (final LootEntry le : getEntries(pool)) {
				targetPool.addEntry(le);
			}
		}
		return targetPool;
	}

	/**
	 * Merges the source LootTable into the target LootTable. If the pools
	 * doesn't exist in target it is assigned to target; if the pool exists, the
	 * LootEntries are merged. Duplicates are overwritten in the target.
	 */
	@Nonnull
	public static LootTable merge(@Nonnull final LootTable target, @Nonnull final LootTable source) {
		for (final LootPool lp : getPools(source)) {
			merge(target, lp);
		}
		return target;
	}
}
