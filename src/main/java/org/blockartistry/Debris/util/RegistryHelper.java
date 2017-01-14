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

package org.blockartistry.Debris.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;

import org.blockartistry.Debris.ModLog;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public final class RegistryHelper {

	private static final Pattern pattern = Pattern.compile("^([^:]+:[^:]+)(?::?)([[\\d]+|[\\*]]*)");

	public static final int GENERIC = -1;
	public static final int NO_SUBTYPE = -100;

	private RegistryHelper() {

	}

	public static ItemStack getItemStack(@Nonnull final String itemId) {
		String workingName = itemId;
		int subType = NO_SUBTYPE;

		// Parse out the possible subtype from the end of the string
		final Matcher m = pattern.matcher(itemId);
		if (m.matches()) {
			workingName = m.group(1);
			final String num = m.group(2);

			if (num != null && !num.isEmpty()) {
				if ("*".compareTo(num) == 0)
					subType = GENERIC;
				else {
					try {
						subType = Integer.parseInt(num);
					} catch (Exception e) {
						// It appears malformed - assume the incoming name
						// is the real name and continue.
						;
					}
				}
			}
		} else {
			ModLog.warn("Unkown item id [%s]", itemId);
		}

		final Item item = MCHelper.getItemByName(workingName);
		if (item == null) {
			ModLog.warn("Unable to find item [%s]", itemId);
			return null;
		}

		ItemStack result = null;
		if (item.getHasSubtypes())
			result = new ItemStack(item, 1, subType);
		else if (item.isDamageable()) {
			result = new ItemStack(item, 1);
			if (subType != NO_SUBTYPE)
				result.setItemDamage(subType);
		} else {
			result = new ItemStack(item);
		}

		return result;
	}
}
