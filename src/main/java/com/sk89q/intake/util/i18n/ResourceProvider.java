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

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Provides internationalization resources.
 */
public interface ResourceProvider {

    /**
     * Gets the Locale that is active when calling this method.
     *
     * @return the Locale
     */
    Locale getLocale();

    /**
     * Gets a ResourceBundle that contains the translated strings for the given
     * Locale.
     *
     * @param locale the Locale
     * @return the appliciable ResourceBundle
     * @throws java.util.MissingResourceException if such a ResourceBundle does
     * not exist
     */
    ResourceBundle getBundle(Locale locale);

    /**
     * Returns whether this implementation supports translations of command
     * annotations. If set to {@code true}, {@link com.sk89q.intake.Command#desc()}
     * and {@link com.sk89q.intake.Command#help()} entries will be parsed against
     * the ResourceBundle returned by {@link #getBundle(java.util.Locale)}.
     *
     * @return {@code true} if command annotations are supported by this
     * implementation
     */
    boolean supportsCommandAnnotations();

}
