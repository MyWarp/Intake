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

/**
 * Provides localised resources.
 *
 * <p>Whenever a resource is needed, {@link #getString(String)} is called. The implementation must resolve the resource
 * based on the given {@code key}, e.g. by using a {@link java.util.ResourceBundle}.</p>
 */
public interface ResourceProvider {


  /**
   * Gets the message that is associated with the given {@code key}.
   *
   * @param key the key
   * @return the corresponding message
   */
  String getString(String key);

}
