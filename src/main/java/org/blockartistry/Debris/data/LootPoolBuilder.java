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

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import org.blockartistry.Debris.util.RegistryHelper;

import net.minecraft.item.ItemStack;
import net.minecraft.world.storage.loot.LootEntry;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.RandomValueRange;

public class LootPoolBuilder {

	private final String name;
	private final List<LootEntry> entries = new ArrayList<LootEntry>();
	private RandomValueRange roll = new RandomValueRange(0);
	private RandomValueRange bonus = new RandomValueRange(0);

	public LootPoolBuilder(@Nonnull final String name) {
		this.name = name;
	}

	public LootPoolBuilder add(@Nonnull final String itemId, final int weight, final int min, final int max) {
		final ItemStack stack = RegistryHelper.getItemStack(itemId);
		if (stack != null) {
			final LootEntry entry = new LootEntryBuilder().setItemStack(stack).setWeight(weight).setMinMax(min, max)
					.build();
			this.entries.add(entry);
		}
		return this;
	}

	public LootPoolBuilder setRoll(final int min, final int max) {
		this.roll = new RandomValueRange(min, max);
		return this;
	}

	public LootPoolBuilder setBonus(final int min, final int max) {
		this.bonus = new RandomValueRange(min, max);
		return this;
	}

	public LootPool build() {
		final LootEntry[] poolEntries = this.entries.toArray(new LootEntry[this.entries.size()]);
		return new LootPool(poolEntries, LootEntryBuilder.EMPTY_CONDITIONS, this.roll, this.bonus, this.name);
	}
}
