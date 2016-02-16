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
import com.sk89q.intake.InvocationCommandException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * An implementation of an {@link ExceptionConverter} that calls methods defined in subclasses that have been annotated
 * with {@link ExceptionMatch}.
 *
 * <p>Only public methods with one or two parameters are accepted: <ul><li>The first parameter must be the {@code
 * Exception} to convert</li> <li>The second parameter is the corresponding {@code ExceptionContext}. it is
 * optional.</li></ul>
 *
 * <p>Methods will be called in order of decreasing levels of inheritance (between classes where one inherits the
 * other). For two different inheritance branches, the order between them is undefined.</p>
 */
public abstract class ExceptionConverterHelper implements ExceptionConverter {

  private final List<ExceptionHandler> handlers;

  @SuppressWarnings("unchecked")
  protected ExceptionConverterHelper() {
    List<ExceptionHandler> handlers = new ArrayList<ExceptionHandler>();

    for (Method method : this.getClass().getMethods()) {
      if (method.getAnnotation(ExceptionMatch.class) == null) {
        continue;
      }

      Class<?>[] parameters = method.getParameterTypes();

      if (parameters.length < 1 || parameters.length > 2) {
        continue;
      }

      Class<?> throwableCls = parameters[0];
      if (!Throwable.class.isAssignableFrom(throwableCls)) {
        continue;
      }

      if (parameters.length == 1) {
        handlers.add(new LegacyExceptionhandler((Class<? extends Throwable>) throwableCls, method));
      } else if (ExceptionContext.class.isAssignableFrom(parameters[1])) {
        handlers.add(new ExceptionHandler((Class<? extends Throwable>) throwableCls, method));
      }
    }

    Collections.sort(handlers);

    this.handlers = handlers;
  }

  @Override
  public void convert(Throwable t, ExceptionContext context) throws CommandException, InvocationCommandException {
    Class<?> throwableClass = t.getClass();
    for (ExceptionHandler handler : handlers) {
      if (handler.type.isAssignableFrom(throwableClass)) {
        try {
          handler.invoke(t, context);
        } catch (InvocationTargetException e) {
          if (e.getCause() instanceof CommandException) {
            throw (CommandException) e.getCause();
          }
          throw new InvocationCommandException(e);
        } catch (IllegalArgumentException e) {
          throw new InvocationCommandException(e);
        } catch (IllegalAccessException e) {
          throw new InvocationCommandException(e);
        }
      }
    }
  }

  private class LegacyExceptionhandler extends ExceptionHandler {

    protected LegacyExceptionhandler(Class<? extends Throwable> type, Method method) {
      super(type, method);
    }

    @Override
    protected void invoke(Throwable t, ExceptionContext context)
        throws InvocationTargetException, IllegalAccessException {
      method.invoke(ExceptionConverterHelper.this, t);
    }
  }

  private class ExceptionHandler implements Comparable<ExceptionHandler> {

    protected final Class<? extends Throwable> type;
    protected final Method method;

    protected ExceptionHandler(Class<? extends Throwable> type, Method method) {
      this.type = type;
      this.method = method;
    }

    protected void invoke(Throwable t, ExceptionContext context)
        throws InvocationTargetException, IllegalAccessException {
      method.invoke(ExceptionConverterHelper.this, t, context);
    }

    @Override
    public int compareTo(ExceptionHandler o) {
      if (type.equals(o.type)) {
        return 0;
      } else if (type.isAssignableFrom(o.type)) {
        return 1;
      } else {
        return -1;
      }
    }
  }

}
