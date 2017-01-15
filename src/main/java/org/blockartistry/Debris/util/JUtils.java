/*
 * This file is part of Dynamic Surroundings, licensed under the MIT License (MIT).
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

import java.io.InputStream;
import java.io.InputStreamReader;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.blockartistry.Debris.ModLog;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.google.gson.reflect.TypeToken;

public final class JUtils {

	private JUtils() {

	}

	@Nullable
	public static <T> T load(@Nonnull final String modId, @Nonnull final Gson gson, @Nonnull final TypeToken<T> type) {
		final String fileName = modId.replaceAll("[^a-zA-Z0-9.-]", "_");
		try (final InputStream stream = JUtils.class
				.getResourceAsStream("/assets/debris/loot_tables/" + fileName + ".json")) {
			if (stream != null)
				try (final InputStreamReader reader = new InputStreamReader(stream)) {
					try (final JsonReader jReader = new JsonReader(reader)) {
						jReader.setLenient(false);
						return gson.getAdapter(type).read(jReader);
					}
				} catch (final Throwable t) {
					ModLog.error("Unable to process Json from stream", t);
				}
		} catch (final Throwable t) {
			ModLog.error("Unable to process Json from stream", t);
		}

		return null;

	}

	@Nullable
	public static <T> T load(@Nonnull final String modId, @Nonnull final TypeToken<T> type) {
		return load(modId, new Gson(), type);
	}
}
