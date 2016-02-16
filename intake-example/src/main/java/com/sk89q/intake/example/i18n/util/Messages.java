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

package com.sk89q.intake.example.i18n.util;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class Messages {

  private static final Logger log = Logger.getLogger(Messages.class.getCanonicalName());

  private final String bundleName;

  public Messages(String bundleName) {
    this.bundleName = bundleName;
  }

  public String getString(String key) {
    try {
      return getBundle().getString(key);
    } catch (MissingResourceException e) {
      log.log(Level.WARNING, "Translation for '" + key + "' is missing.", e);
      return "${" + key + "}";
    }
  }

  public String getString(String key, Object... args) {
    try {
      ResourceBundle bundle = getBundle();
      MessageFormat format = new MessageFormat(bundle.getString(key), bundle.getLocale());
      return format.format(args);
    } catch (MissingResourceException e) {
      log.log(Level.WARNING, "Translation for '" + key + "' is missing.", e);
      return "${" + key + "}:" + Arrays.toString(args);
    }

  }

  private ResourceBundle getBundle() {
    return ResourceBundle.getBundle(bundleName);
  }

}
