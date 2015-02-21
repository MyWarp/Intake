/*
 * Intake, a command processing library
 * Copyright (C) sk89q <http://www.sk89q.com>
 * Copyright (C) Intake team and contributors
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.sk89q.intake.util.i18n;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Locale;
import java.util.MissingResourceException;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Provides translated and formatted messages.
 */
public final class Messages {

    private final ResourceProvider provider;

    /**
     * Creates an instance that uses the given resourceProvider to get the
     * translated resources.
     *
     * @param provider the ResourceProvider
     */
    public Messages(ResourceProvider provider) {
        this.provider = checkNotNull(provider);
    }

    /**
     * Gets a translated string.
     *
     * <p> If no translation exits, {@code $&#123;key&#125;} will be returned. </p>
     *
     * @param key the key
     * @return the translated string
     */
    public final String getString(String key) {
        try {
            return provider.getBundle(provider.getLocale()).getString(key);
        } catch (MissingResourceException e) {
            return "{" + key + "}";
        }
    }

    /**
     * Gets a translated and formatted string.
     *
     * <p> If no translation exits, {@code $&#123;key&#125;:args} will be returned. </p>
     *
     * @param key    the key
     * @param args   the arguments
     * @return a translated string
     */
    public final String getString(String key, Object... args) {
        try {
            Locale locale = provider.getLocale();
            MessageFormat format = new MessageFormat(provider.getBundle(locale).getString(key), locale);
            return format.format(args);
        } catch (MissingResourceException e) {
            return "{" + key + "}:" + Arrays.toString(args);
        }
    }
}
