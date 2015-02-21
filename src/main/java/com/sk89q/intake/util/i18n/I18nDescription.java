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

import com.sk89q.intake.SettableDescription;

/**
 * A Description implementation that translates help and short description.
 */
public class I18nDescription extends SettableDescription {

    private final Messages messages;

    /**
     * Creates an instance. Help and short description will be resolved using the
     * given ResourceProvider.
     *
     * @param provider the ResourceProvider
     */
    public I18nDescription(ResourceProvider provider) {
        messages = new Messages(provider);
    }

    @Override
    public String getShortDescription() {
        String shortDescriptionKey = super.getShortDescription();

        if (shortDescriptionKey == null) {
            //no description set
            return null;
        }
        return messages.getString(shortDescriptionKey);
    }

    @Override
    public String getHelp() {
        String helpKey = super.getHelp();
        if (helpKey == null) {
            //no help set
            return null;
        }
        return messages.getString(helpKey);
    }

}
