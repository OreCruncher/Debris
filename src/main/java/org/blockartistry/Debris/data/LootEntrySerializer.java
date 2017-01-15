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

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;

import net.minecraft.item.Item;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootEntry;
import net.minecraft.world.storage.loot.LootEntryEmpty;
import net.minecraft.world.storage.loot.LootEntryItem;
import net.minecraft.world.storage.loot.LootEntryTable;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;

/**
 * Provide our own serializer because the built-in Forge one uses this thing
 * called a loot context.  We want to be able to deserialize Json without
 * this side lookup so we can do our own processing.
 */
public final class LootEntrySerializer extends LootEntry.Serializer {

	@Override
	public LootEntry deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_,
			JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
		JsonObject jsonobject = JsonUtils.getJsonObject(p_deserialize_1_, "loot item");
		String s = JsonUtils.getString(jsonobject, "type");
		int i = JsonUtils.getInt(jsonobject, "weight", 1);
		int j = JsonUtils.getInt(jsonobject, "quality", 0);
		LootCondition[] alootcondition;

		if (jsonobject.has("conditions")) {
			alootcondition = (LootCondition[]) JsonUtils.deserializeClass(jsonobject, "conditions", p_deserialize_3_,
					LootCondition[].class);
		} else {
			alootcondition = new LootCondition[0];
		}

		if ("item".equals(s)) {
			return deserializeItem(jsonobject, p_deserialize_3_, i, j, alootcondition);
		} else if ("loot_table".equals(s)) {
			return deserializeTable(jsonobject, p_deserialize_3_, i, j, alootcondition);
		} else if ("empty".equals(s)) {
			return deserializeEmpty(jsonobject, p_deserialize_3_, i, j, alootcondition);
		} else {
			throw new JsonSyntaxException("Unknown loot entry type \'" + s + "\'");
		}
	}

	private static LootEntryItem deserializeItem(JsonObject object, JsonDeserializationContext deserializationContext,
			int weightIn, int qualityIn, LootCondition[] conditionsIn) {
		String name = JsonUtils.getString(object, "name");
		Item item = JsonUtils.getItem(object, "name");
		LootFunction[] alootfunction;

		if (object.has("functions")) {
			alootfunction = (LootFunction[]) JsonUtils.deserializeClass(object, "functions", deserializationContext,
					LootFunction[].class);
		} else {
			alootfunction = new LootFunction[0];
		}

		return new LootEntryItem(item, weightIn, qualityIn, alootfunction, conditionsIn, name);
	}

	private static LootEntryTable deserializeTable(JsonObject object, JsonDeserializationContext deserializationContext,
			int weightIn, int qualityIn, LootCondition[] conditionsIn) {
		String name = JsonUtils.getString(object, "loot_table");
		ResourceLocation resourcelocation = new ResourceLocation(JsonUtils.getString(object, "name"));
		return new LootEntryTable(resourcelocation, weightIn, qualityIn, conditionsIn, name);
	}

	private static LootEntryEmpty deserializeEmpty(JsonObject object, JsonDeserializationContext deserializationContext,
			int weightIn, int qualityIn, LootCondition[] conditionsIn) {
		return new LootEntryEmpty(weightIn, qualityIn, conditionsIn, "empty");
	}

}
