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

package com.sk89q.intake.parametric.handler;

import com.sk89q.intake.CommandException;
import com.sk89q.intake.InvalidUsageException;
import com.sk89q.intake.InvocationCommandException;
import com.sk89q.intake.argument.ArgumentException;
import com.sk89q.intake.argument.ArgumentParseException;
import com.sk89q.intake.argument.FlagException;
import com.sk89q.intake.argument.MissingArgumentException;
import com.sk89q.intake.argument.UnusedArgumentException;
import com.sk89q.intake.parametric.ProvisionException;

/**
 * Intake's default exception converter.
 *
 * <p>It covers:
 * <ul>
 *   <li>{@link MissingArgumentException}</li>
 *   <li>{@link UnusedArgumentException}</li>
 *   <li>{@link FlagException}</li>
 *   <li>{@link ArgumentParseException}</li>
 *   <li>{@link ArgumentException}</li>
 *   <li>{@link CommandException}</li>
 *   <li>{@link ProvisionException}</li>
 *   <li>{@link InterruptedException}</li>
 *  </ul>
 */
public final class DefaultExceptionConverter extends ExceptionConverterHelper {

  @ExceptionMatch
  public void convert(MissingArgumentException e, ExceptionContext context) throws InvalidUsageException {
    if (e.getParameter() != null) {
      throw new InvalidUsageException(
          "Too few arguments! No value found for parameter '" + e.getParameter().getName() + "'", context.getCommand(),
          context.getAliasStack(), false, e);
    } else {
      throw new InvalidUsageException("Too few arguments!", context.getCommand(), context.getAliasStack(), false, e);
    }
  }

  @ExceptionMatch
  public void convert(UnusedArgumentException e, ExceptionContext context) throws InvalidUsageException {
    throw new InvalidUsageException("Too many arguments! Unused arguments: " + e.getUnconsumed(), context.getCommand(),
                                    context.getAliasStack(), false, e);
  }

  @ExceptionMatch
  public void convert(FlagException e, ExceptionContext context) throws InvalidUsageException {
    throw new InvalidUsageException(e.getMessage(), context.getCommand(), context.getAliasStack(), true, e);
  }

  @ExceptionMatch
  public void convert(ArgumentParseException e, ExceptionContext context) throws InvalidUsageException {
    if (e.getParameter() != null) {
      throw new InvalidUsageException("For parameter '" + e.getParameter().getName() + "': " + e.getMessage(),
                                      context.getCommand(), context.getAliasStack(), false, e);
    } else {
      throw new InvalidUsageException("Error parsing arguments: " + e.getMessage(), context.getCommand(),
                                      context.getAliasStack(), false, e);
    }
  }

  @ExceptionMatch
  public void convert(ArgumentException e, ExceptionContext context) throws InvalidUsageException {
    throw new InvalidUsageException("Error parsing arguments: " + e.getMessage(), context.getCommand(),
                                    context.getAliasStack(), false, e);
  }

  @ExceptionMatch
  public void convert(CommandException e, ExceptionContext context) throws CommandException {
    throw e;
  }

  @ExceptionMatch
  public void convert(ProvisionException e, ExceptionContext context) throws InvocationCommandException {
    throw new InvocationCommandException("Internal error occurred: " + e.getMessage(), e);
  }

  @ExceptionMatch
  public void convert(InterruptedException e, ExceptionContext context) throws InvocationCommandException {
    throw new InvocationCommandException("Execution of the command was interrupted", e.getCause());
  }
}
