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

package com.sk89q.intake.parametric.provider;

import com.sk89q.intake.argument.ArgumentParseException;
import com.sk89q.intake.argument.Namespace;
import com.sk89q.intake.parametric.Provider;
import com.sk89q.intake.parametric.annotation.Range;
import com.sk89q.intake.parametric.provider.exception.NonnumericalInputException;
import com.sk89q.intake.parametric.provider.exception.OverRangeException;
import com.sk89q.intake.parametric.provider.exception.UnderRangeException;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

abstract class NumberProvider<T extends Number> implements Provider<T> {

    @Override
    public boolean isProvided() {
        return false;
    }

    @Override
    public List<String> getSuggestions(String prefix, Namespace locals) {
        return Collections.emptyList();
    }

    /**
     * Try to parse numeric input as either a number or a mathematical expression.
     *
     * @param input input
     * @return a number
     * @throws ArgumentParseException thrown on parse error
     */
    @Nullable
    protected static Double parseNumericInput(@Nullable String input) throws ArgumentParseException {
        if (input == null) {
            return null;
        }

        try {
            return Double.parseDouble(input);
        } catch (NumberFormatException ignored) {
            throw new NonnumericalInputException(input);
        }
    }

    /**
     * Validate a number value using relevant modifiers.
     *
     * @param number the number
     * @param modifiers the list of modifiers to scan
     * @throws ArgumentParseException on a validation error
     */
    protected static void validate(double number, List<? extends Annotation> modifiers) throws ArgumentParseException {
        for (Annotation modifier : modifiers) {
            if (modifier instanceof Range) {
                Range range = (Range) modifier;
                if (number < range.min()) {
                    throw new UnderRangeException(number, range.min());
                } else if (number > range.max()) {
                    throw new OverRangeException(number, range.max());
                }
            }
        }
    }

}
