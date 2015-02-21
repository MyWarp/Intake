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
 * An internal resource provider that handles Intake's default
 * internationalization. It is used if the user does not set a custom
 * {@link com.sk89q.intake.util.i18n.ResourceProvider}.
 */
public final class DefaultResourceProvider implements ResourceProvider {

    private static final String RESOURCE_BUNDLE_NAME = "com.sk89q.intake.lang.messages";

    @Override
    public final Locale getLocale() {
        return Locale.getDefault();
    }

    @Override
    public ResourceBundle getBundle(Locale locale) {
        return ResourceBundle.getBundle(RESOURCE_BUNDLE_NAME);
    }

    @Override
    public boolean supportsCommandAnnotations() {
        return false;
    }
}
