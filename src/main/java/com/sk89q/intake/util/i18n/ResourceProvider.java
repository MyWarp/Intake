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

import java.util.ResourceBundle;

/**
 * Provides localised resources.
 *
 * <p>{@link #getBundle()} is called whenever a localised resource is needed. The
 * {@link java.util.Locale} of that resource is completely controlled by the
 * implementation of this class. If a Locale is needed (e.g. to control
 * formatting) {@link ResourceBundle#getLocale()} is called.</p>
 */
public interface ResourceProvider {

    /**
     * Gets a ResourceBundle that contains the localised strings applicable in the
     * context it is called.
     *
     * @return the ResourceBundle
     * @throws java.util.MissingResourceException if such a ResourceBundle does
     * not exist
     */
    ResourceBundle getBundle();

}
