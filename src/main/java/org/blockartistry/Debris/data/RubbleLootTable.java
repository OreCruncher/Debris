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
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.blockartistry.Debris.Debris;
import org.blockartistry.Debris.ModEnvironment;
import org.blockartistry.Debris.ModLog;
import org.blockartistry.Debris.ModOptions;
import com.google.common.collect.ImmutableList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootEntry;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.OreDictionary;

@Mod.EventBusSubscriber
public class RubbleLootTable {

	private static final Set<ResourceLocation> TABLES = new HashSet<ResourceLocation>();

	public static void register(@Nonnull final ResourceLocation resource) {
		TABLES.add(resource);
		LootTableList.register(resource);
	}

	@Nonnull
	public static List<ItemStack> getDrops(@Nonnull final ResourceLocation lootTable, @Nonnull final World world,
			@Nullable final EntityPlayer player, @Nonnull final Random rand) {
		final LootTable table = world.getLootTableManager().getLootTableFromLocation(lootTable);
		if (table != null) {
			final LootContext.Builder builder = new LootContext.Builder((WorldServer) world);
			if (player != null) {
				builder.withPlayer(player);
				if (ModOptions.useLuck)
					builder.withLuck(player.getLuck());
			}
			return table.generateLootForPools(rand, builder.build());
		}
		return ImmutableList.of();
	}

	private static void process(@Nonnull final LootTable table, @Nonnull final LootTable source,
			@Nonnull final String poolName) {
		if (source != null && source != LootTable.EMPTY_LOOT_TABLE) {
			final LootPool sourcePool = source.getPool(poolName);
			if (sourcePool != null)
				Loot.merge(table, sourcePool);
		}
	}

	private static void process(@Nonnull final LootTable table, @Nonnull final String modId,
			@Nonnull final String poolName) {
		final ResourceLocation resource = new ResourceLocation(Debris.RESOURCE_ID(), modId);
		final LootTable sourceTable = Loot.loadLootTable(resource);
		process(table, sourceTable, poolName);
	}

	private static void process(@Nonnull final LootTable table, @Nonnull final File file,
			@Nonnull final String poolName) {
		final ResourceLocation resource = new ResourceLocation(Debris.RESOURCE_ID(), file.getName());
		final LootTable sourceTable = Loot.loadLootTable(resource, file);
		process(table, sourceTable, poolName);
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

		if (!TABLES.contains(event.getName()))
			return;

		final String poolName = event.getName().getResourcePath();
		final LootPool pool = event.getTable().getPool(poolName);

		if (pool == null) {
			ModLog.warn("Can't find pool [%s] in loot table [%s]", poolName, event.getName());
			return;
		}

		pool.setRolls(new RandomValueRange(ModOptions.rubbleRollsMin, ModOptions.rubbleRollsMax));
		pool.setBonusRolls(new RandomValueRange(ModOptions.bonusRollsMin, ModOptions.bonusRollsMax));

		if ("pile_of_rubble".equals(poolName)) {
			applyDictionary(pool, "oreCopper", 50, 1, 2);
			applyDictionary(pool, "oreTin", 50, 1, 2);
		}

		for (final ModEnvironment me : ModEnvironment.values())
			if (me.isLoaded())
				process(event.getTable(), me.getModId(), poolName);

		for (final String external : ModOptions.externalScriptFiles) {
			final File file = new File(Debris.dataDirectory(), external);
			if(file.exists())
				process(event.getTable(), file, poolName);
			else
				ModLog.warn("Unable to locate external configuration file [%s]", file.toString());
		}

	}

}
