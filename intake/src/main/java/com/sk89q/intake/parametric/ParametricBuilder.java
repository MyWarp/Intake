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

package com.sk89q.intake.parametric;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.AbstractListeningExecutorService;
import com.sk89q.intake.Command;
import com.sk89q.intake.CommandCallable;
import com.sk89q.intake.CommandException;
import com.sk89q.intake.Default;
import com.sk89q.intake.completion.CommandCompleter;
import com.sk89q.intake.completion.NullCompleter;
import com.sk89q.intake.dispatcher.Dispatcher;
import com.sk89q.intake.parametric.handler.DefaultExceptionConverter;
import com.sk89q.intake.parametric.handler.ExceptionConverter;
import com.sk89q.intake.parametric.handler.InvokeHandler;
import com.sk89q.intake.parametric.handler.InvokeListener;
import com.sk89q.intake.util.auth.Authorizer;
import com.sk89q.intake.util.auth.NullAuthorizer;
import com.sk89q.intake.util.i18n.ResourceProvider;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;
import javax.annotation.concurrent.GuardedBy;

/**
 * Keeps a mapping of types to bindings and generates commands from classes with appropriate annotations.
 */
public class ParametricBuilder {

  private final Injector injector;
  private final List<InvokeListener> invokeListeners = Lists.newArrayList();
  private final LinkedList<ExceptionConverter> exceptionConverters = Lists.newLinkedList();
  private Authorizer authorizer = new NullAuthorizer();
  private CommandCompleter defaultCompleter = new NullCompleter();
  // see comment on DirectExecutorService for its reason of being
  private CommandExecutor commandExecutor = new CommandExecutorWrapper(new DirectExecutorService());
  @Nullable
  private ResourceProvider resourceProvider;

  public ParametricBuilder(Injector injector) {
    this.injector = injector;

    exceptionConverters.add(new DefaultExceptionConverter());
  }

  public Injector getInjector() {
    return injector;
  }

  /**
   * Attach an invocation listener.
   * <p>Invocation handlers are called in order that their listeners are registered with a ParametricBuilder. It is
   * not
   * guaranteed that a listener may be called, in the case of a {@link CommandException} being thrown at any time
   * before
   * the appropriate listener or handler is called.
   *
   * @param listener The listener
   * @see InvokeHandler tThe handler
   */
  public void addInvokeListener(InvokeListener listener) {
    checkNotNull(listener);
    invokeListeners.add(listener);
  }

  /**
   * Attach an exception converter to this builder in order to wrap unknown {@link Throwable}s into known {@link
   * CommandException}s.
   * <p>Exception converters are called in order that they are registered.</p>
   *
   * @param converter The converter
   * @see ExceptionConverter for an explanation
   */
  public void addExceptionConverter(ExceptionConverter converter) {
    checkNotNull(converter);
    exceptionConverters.addFirst(converter);
  }

  /**
   * Get the executor service used to invoke the actual command.
   * <p>Bindings will still be resolved in the thread in which the callable was called.</p>
   *
   * @return The command executor
   */
  public CommandExecutor getCommandExecutor() {
    return commandExecutor;
  }

  /**
   * Set the executor service used to invoke the actual command.
   * <p>Bindings will still be resolved in the thread in which the callable was called.</p>
   *
   * @param commandExecutor The executor
   */
  public void setCommandExecutor(ExecutorService commandExecutor) {
    setCommandExecutor(new CommandExecutorWrapper(commandExecutor));
  }

  /**
   * Set the executor service used to invoke the actual command.
   * <p>Bindings will still be resolved in the thread in which the callable was called.</p> a
   *
   * @param commandExecutor The executor
   */
  public void setCommandExecutor(CommandExecutor commandExecutor) {
    checkNotNull(commandExecutor, "commandExecutor");
    this.commandExecutor = commandExecutor;
  }

  /**
   * Build a list of commands from methods specially annotated with {@link Command} (and other relevant
   * annotations) and register them all with the given {@link Dispatcher}.
   *
   * @param dispatcher The dispatcher to register commands with
   * @param object     The object contain the methods
   * @throws ParametricException thrown if the commands cannot be registered
   */
  public void registerMethodsAsCommands(Dispatcher dispatcher, Object object) throws ParametricException {
    checkNotNull(dispatcher);
    checkNotNull(object);

    for (Method method : object.getClass().getDeclaredMethods()) {
      Command definition = method.getAnnotation(Command.class);
      if (definition != null) {
        CommandCallable callable = build(object, method);

        Default defaultDefinition = method.getAnnotation(Default.class);
        if (defaultDefinition != null) {
          dispatcher.registerDefaultCommand(callable, defaultDefinition, definition.aliases());
        } else {
          dispatcher.registerCommand(callable, definition.aliases());
        }
      }
    }
  }

  /**
   * Build a {@link CommandCallable} for the given method.
   *
   * @param object The object to be invoked on
   * @param method The method to invoke
   * @return The command executor
   * @throws ParametricException Thrown on an error
   */
  public CommandCallable build(Object object, Method method) throws ParametricException {
    return MethodCallable.create(this, object, method);
  }

  @Nullable
  protected ResourceProvider getResourceProvider() {
    return resourceProvider;
  }

  /**
   * Sets the resource provider used to resolve description, help or usage override for commands.
   *
   * <p>If no resource provider is configured, description, help and usage override of commands build by this builder
   * will return the configured Strings directly.</p>
   *
   * @param resourceProvider the resource provider
   */
  public void setResourceProvider(ResourceProvider resourceProvider) {
    this.resourceProvider = resourceProvider;
  }

  /**
   * Get a list of invocation listeners.
   *
   * @return A list of invocation listeners
   */
  List<InvokeListener> getInvokeListeners() {
    return invokeListeners;
  }

  /**
   * Get the list of exception converters.
   *
   * @return A list of exception converters
   */
  List<ExceptionConverter> getExceptionConverters() {
    return exceptionConverters;
  }

  /**
   * Get the authorizer.
   *
   * @return The authorizer
   */
  public Authorizer getAuthorizer() {
    return authorizer;
  }

  /**
   * Set the authorizer.
   *
   * @param authorizer The authorizer
   */
  public void setAuthorizer(Authorizer authorizer) {
    checkNotNull(authorizer);
    this.authorizer = authorizer;
  }

  /**
   * Get the default command suggestions provider that will be used if no suggestions are available.
   *
   * @return The default command completer
   */
  public CommandCompleter getDefaultCompleter() {
    return defaultCompleter;
  }

  /**
   * Set the default command suggestions provider that will be used if no suggestions are available.
   *
   * @param defaultCompleter The default command completer
   */
  public void setDefaultCompleter(CommandCompleter defaultCompleter) {
    checkNotNull(defaultCompleter);
    this.defaultCompleter = defaultCompleter;
  }

  // In Guava 18, this function is available under MoreExecutors.sameThreadExecutor().
  // In Guava 18 this method was renamed to MoreExecutors.directExecutor(), the original one was deprecated and later
  // removed.
  // Here we need to support clients running Guava 10 - 22, so we need to supply this function by ourselves.
  private static final class DirectExecutorService extends AbstractListeningExecutorService {

    /**
     * Lock used whenever accessing the state variables (runningTasks, shutdown) of the executor
     */
    private final Object lock = new Object();

    /*
     * Conceptually, these two variables describe the executor being in
     * one of three states:
     *   - Active: shutdown == false
     *   - Shutdown: runningTasks > 0 and shutdown == true
     *   - Terminated: runningTasks == 0 and shutdown == true
     */
    @GuardedBy("lock")
    private int runningTasks = 0;

    @GuardedBy("lock")
    private boolean shutdown = false;

    @Override
    public void execute(Runnable command) {
      startTask();
      try {
        command.run();
      } finally {
        endTask();
      }
    }

    @Override
    public boolean isShutdown() {
      synchronized (lock) {
        return shutdown;
      }
    }

    @Override
    public void shutdown() {
      synchronized (lock) {
        shutdown = true;
        if (runningTasks == 0) {
          lock.notifyAll();
        }
      }
    }

    // See newDirectExecutorService javadoc for unusual behavior of this method.
    @Override
    public List<Runnable> shutdownNow() {
      shutdown();
      return Collections.emptyList();
    }

    @Override
    public boolean isTerminated() {
      synchronized (lock) {
        return shutdown && runningTasks == 0;
      }
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
      long nanos = unit.toNanos(timeout);
      synchronized (lock) {
        while (true) {
          if (shutdown && runningTasks == 0) {
            return true;
          } else if (nanos <= 0) {
            return false;
          } else {
            long now = System.nanoTime();
            TimeUnit.NANOSECONDS.timedWait(lock, nanos);
            nanos -= System.nanoTime() - now; // subtract the actual time we waited
          }
        }
      }
    }

    /**
     * Checks if the executor has been shut down and increments the running task count.
     *
     * @throws RejectedExecutionException if the executor has been previously shutdown
     */
    private void startTask() {
      synchronized (lock) {
        if (shutdown) {
          throw new RejectedExecutionException("Executor already shutdown");
        }
        runningTasks++;
      }
    }

    /**
     * Decrements the running task count.
     */
    private void endTask() {
      synchronized (lock) {
        int numRunning = --runningTasks;
        if (numRunning == 0) {
          lock.notifyAll();
        }
      }
    }
  }


}
