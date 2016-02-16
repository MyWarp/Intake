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

package com.sk89q.intake.example.i18n;

import com.sk89q.intake.InvalidUsageException;
import com.sk89q.intake.argument.ArgumentException;
import com.sk89q.intake.argument.ArgumentParseException;
import com.sk89q.intake.example.i18n.util.Messages;
import com.sk89q.intake.parametric.handler.ExceptionContext;
import com.sk89q.intake.parametric.handler.ExceptionConverterHelper;
import com.sk89q.intake.parametric.handler.ExceptionMatch;
import com.sk89q.intake.parametric.provider.exception.NoMatchInEnumException;
import com.sk89q.intake.parametric.provider.exception.NonnumericalInputException;
import com.sk89q.intake.parametric.provider.exception.OverRangeException;
import com.sk89q.intake.parametric.provider.exception.StringFormatException;
import com.sk89q.intake.parametric.provider.exception.UnderRangeException;

/**
 * Converts exceptions thrown by individual parsers.
 *
 * <p>Exceptions thrown by parsers are normally wrapped in an {@link ArgumentParseException} and and only accessible by
 * caling {@link ArgumentParseException#getCause()}. Therefore registering this class with the {@link
 * com.sk89q.intake.parametric.ParametricBuilder} does nothing!</p>
 *
 * <p>It is instead called by by {@link I18nExceptionConverter#convert(ArgumentException, ExceptionContext)} with the
 * respective cause.</p>
 */
public class ArgumentExceptionConverter extends ExceptionConverterHelper {

  private static final Messages msg = new Messages(I18nExample.RESOURCE_NAME);

  @ExceptionMatch
  public void convert(StringFormatException e, ExceptionContext context) throws InvalidUsageException {
    throw ex(msg.getString("primitives.invalid.format", e.getFormat()), context, e);
  }

  @ExceptionMatch
  public void convert(NonnumericalInputException e, ExceptionContext context)
      throws InvalidUsageException {
    throw ex(msg.getString("primitives.invalid.non-number", e.getInput()), context, e);
  }

  @ExceptionMatch
  public void convert(OverRangeException e, ExceptionContext context) throws InvalidUsageException {
    throw ex(msg.getString("primitives.invalid.greater-or-equal", e.getMaximum(), e.getInput()), context, e);
  }

  @ExceptionMatch
  public void convert(UnderRangeException e, ExceptionContext context) throws InvalidUsageException {
    throw ex(msg.getString("primitives.invalid.less-or-equal", e.getMinimum(), e.getInput()), context, e);
  }

  @ExceptionMatch
  public void convert(NoMatchInEnumException e, ExceptionContext context) throws InvalidUsageException {
    throw ex(msg.getString("enum.no-matching-value", e.getEnumName()), context, e);
  }

  private InvalidUsageException ex(String errorMsg, ExceptionContext context, ArgumentParseException cause) {
    if (cause.getParameter() != null) {
      return new InvalidUsageException(msg.getString("parameter.error", cause.getParameter().getName(), errorMsg),
                                       context.getCommand(), context.getAliasStack(), false, cause);
    } else {
      return new InvalidUsageException(msg.getString("parameter.error.unknown", errorMsg), context.getCommand(),
                                       context.getAliasStack(), false, cause);
    }
  }

}
