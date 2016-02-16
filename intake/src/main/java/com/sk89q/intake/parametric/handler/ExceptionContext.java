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

import static com.google.common.base.Preconditions.checkNotNull;

import com.sk89q.intake.CommandCallable;

import java.util.List;

/**
 * Represents the context within which an exception occurs.
 *
 * <p>Use a {@link Builder} to create instances.</p>
 */
public class ExceptionContext {

  private final CommandCallable command;
  private final List<String> aliasStack;

  private ExceptionContext(CommandCallable command, List<String> aliasStack) {
    this.command = checkNotNull(command, "command");
    this.aliasStack = checkNotNull(aliasStack, "aliasStack");
  }

  public CommandCallable getCommand() {
    return command;
  }

  public List<String> getAliasStack() {
    return aliasStack;
  }

  public static class Builder {

    private CommandCallable command;
    private List<String> aliasStack;

    public Builder setCommand(CommandCallable command) {
      this.command = command;
      return this;
    }

    public Builder setAliasStack(List<String> aliasStack) {
      this.aliasStack = aliasStack;
      return this;
    }

    public ExceptionContext build() {
      return new ExceptionContext(command, aliasStack);
    }
  }
}
