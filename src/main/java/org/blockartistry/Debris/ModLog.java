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

package org.blockartistry.Debris;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class ModLog {
	
	public static boolean DEBUGGING = false;

	private static final Logger LOGGER = LogManager.getLogger(Debris.MOD_ID());

	private ModLog() {
	}

	public static void info(@Nonnull final String msg, @Nullable final Object... parms) {
		if (LOGGER != null)
			LOGGER.info(String.format(msg, parms));
	}

	public static void warn(@Nonnull final String msg, @Nullable final Object... parms) {
		if (LOGGER != null)
			LOGGER.warn(String.format(msg, parms));
	}

	public static void debug(@Nonnull final String msg, @Nullable final Object... parms) {
		if (LOGGER != null && DEBUGGING) {
			LOGGER.info(String.format(msg, parms));
		}
	}

	public static void error(@Nonnull final String msg) {
		error(msg, null);
	}
	
	public static void error(@Nonnull final String msg, @Nullable final Throwable e) {
		if (LOGGER != null)
			LOGGER.error(msg);
		if(e != null)
			e.printStackTrace();
	}

	public static void catching(@Nonnull final Throwable t) {
		if (LOGGER != null) {
			LOGGER.catching(t);
			t.printStackTrace();
		}
	}
}
