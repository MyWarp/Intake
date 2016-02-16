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

import com.sk89q.intake.CommandException;
import com.sk89q.intake.InvalidUsageException;
import com.sk89q.intake.InvocationCommandException;
import com.sk89q.intake.argument.AlreadyPresentFlagException;
import com.sk89q.intake.argument.ArgumentException;
import com.sk89q.intake.argument.MissingArgumentException;
import com.sk89q.intake.argument.MissingFlagValueException;
import com.sk89q.intake.argument.UnusedArgumentException;
import com.sk89q.intake.example.i18n.util.Messages;
import com.sk89q.intake.parametric.handler.ExceptionContext;
import com.sk89q.intake.parametric.handler.ExceptionConverterHelper;
import com.sk89q.intake.parametric.handler.ExceptionMatch;

public class I18nExceptionConverter extends ExceptionConverterHelper {

  private static final Messages msg = new Messages(I18nExample.RESOURCE_NAME);
  private final ArgumentExceptionConverter argumentExceptionConverter = new ArgumentExceptionConverter();

  @ExceptionMatch
  public void convert(MissingArgumentException e, ExceptionContext context) throws InvalidUsageException {
    if (e.getParameter() != null) {
      throw new InvalidUsageException(msg.getString("parameter.argument.missing", e.getParameter().getName()),
                                      context.getCommand(), context.getAliasStack(), false, e);
    } else {
      throw new InvalidUsageException(msg.getString("parameter.argument.missing.unknown"), context.getCommand(),
                                      context.getAliasStack(), false, e);
    }
  }

  @ExceptionMatch
  public void convert(UnusedArgumentException e, ExceptionContext context) throws InvalidUsageException {
    throw new InvalidUsageException(msg.getString("parameter.argument.unused", e.getUnconsumed()), context.getCommand(),
                                    context.getAliasStack(), false, e);
  }

  @ExceptionMatch
  public void convert(MissingFlagValueException e, ExceptionContext context) throws InvalidUsageException {
    throw new InvalidUsageException(msg.getString("parameter.flag.value.missing"), context.getCommand(),
                                    context.getAliasStack(), true, e);
  }

  @ExceptionMatch
  public void convert(AlreadyPresentFlagException e, ExceptionContext context) throws InvalidUsageException {
    throw new InvalidUsageException(msg.getString("parameter.flag.value.already-given"), context.getCommand(),
                                    context.getAliasStack(), true, e);
  }

  @ExceptionMatch
  public void convert(ArgumentException e, ExceptionContext context)
      throws InvocationCommandException, CommandException {
    argumentExceptionConverter.convert(e.getCause(), context);
  }

  // Note: ProvisionException and InterruptedException are not converted (and localised) here
  // because they are handled with a generic failure message in I18nExample!

}
