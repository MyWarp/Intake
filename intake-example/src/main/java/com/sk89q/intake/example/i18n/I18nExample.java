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

import com.sk89q.intake.CommandCallable;
import com.sk89q.intake.CommandException;
import com.sk89q.intake.Intake;
import com.sk89q.intake.InvalidUsageException;
import com.sk89q.intake.InvocationCommandException;
import com.sk89q.intake.argument.Namespace;
import com.sk89q.intake.dispatcher.Dispatcher;
import com.sk89q.intake.dispatcher.NoSubcommandsException;
import com.sk89q.intake.dispatcher.SubcommandRequiredException;
import com.sk89q.intake.example.i18n.util.Messages;
import com.sk89q.intake.example.parametric.ExampleAuthorizer;
import com.sk89q.intake.example.parametric.Subject;
import com.sk89q.intake.example.parametric.model.Body;
import com.sk89q.intake.example.parametric.model.CelestialType;
import com.sk89q.intake.example.parametric.model.Universe;
import com.sk89q.intake.example.parametric.module.UniverseModule;
import com.sk89q.intake.fluent.CommandGraph;
import com.sk89q.intake.parametric.Injector;
import com.sk89q.intake.parametric.ParametricBuilder;
import com.sk89q.intake.parametric.provider.PrimitivesModule;
import com.sk89q.intake.util.auth.AuthorizationException;

import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

@SuppressWarnings("UseOfSystemOutOrSystemErr")
public final class I18nExample {

  public static final String RESOURCE_NAME = "com.sk89q.intake.example.lang";

  private static final Messages msg = new Messages(I18nExample.RESOURCE_NAME);
  private static final Logger log = Logger.getLogger(I18nExample.class.getCanonicalName());

  private I18nExample() {
  }

  public static void main(String[] args) {
    Subject subject = new Subject("example_user");
    subject.permit("body.settype");
    subject.permit("body.settemp");
    subject.permit("body.setdesc");
    subject.permit("body.info");

    Universe universe = new Universe();
    universe.put("mercury", new Body().setType(CelestialType.PLANET));
    universe.put("venus", new Body().setType(CelestialType.PLANET));
    universe.put("earth", new Body().setType(CelestialType.PLANET));
    universe.put("mars", new Body().setType(CelestialType.PLANET));
    universe.put("jupiter", new Body().setType(CelestialType.PLANET));
    universe.put("saturn", new Body().setType(CelestialType.PLANET));
    universe.put("uranus", new Body().setType(CelestialType.PLANET));
    universe.put("neptune", new Body().setType(CelestialType.PLANET));
    universe.put("pluto", new Body().setType(CelestialType.PLANET));

    Namespace namespace = new Namespace();
    namespace.put(Subject.class, subject); // We'll use this to check authorization

    Injector injector = Intake.createInjector();
    injector.install(new PrimitivesModule());
    injector.install(new UniverseModule(universe));

    ParametricBuilder builder = new ParametricBuilder(injector);
    builder.setAuthorizer(new ExampleAuthorizer());
    builder.setResourceProvider(new ExampleResourceProvider());
    builder.addExceptionConverter(new I18nExceptionConverter());

    Dispatcher
        dispatcher =
        new CommandGraph().builder(builder).commands().group("body").registerMethods(new I18nUniverseCommands()).parent()
            .graph().getDispatcher();

    executeCommand(namespace, dispatcher, "body info pluto");
    executeCommand(namespace, dispatcher, "body settype pluto dwarfplanet");
    executeCommand(namespace, dispatcher, "body info pluto");
    executeCommand(namespace, dispatcher, "body settype poseidon planet");
    executeCommand(namespace, dispatcher, "body settype pluto unknown");
    executeCommand(namespace, dispatcher, "body settemp mercury 167");
    executeCommand(namespace, dispatcher,
                   "body setdesc mercury Closest to the Sun"); // Use of @Text on the String parameter
    executeCommand(namespace, dispatcher, "body info mercury");
    executeCommand(namespace, dispatcher, "body info -f mercury"); // Use of a flag (-f)
    executeCommand(namespace, dispatcher, "body settemp earth 59 -f"); // Use of a flag (-f)
    executeCommand(namespace, dispatcher, "body info earth");
    executeCommand(namespace, dispatcher, "body delete earth"); // Permission fail
  }

  private static void executeCommand(Namespace namespace, CommandCallable callable, String command) {
    System.out.println();
    System.out.println("--------------------");
    System.out.println("/" + command);

    try {
      callable.call(command, namespace, Collections.<String>emptyList());
    } catch (NoSubcommandsException e) {
      System.out.println(msg.getString("subcommand.none"));
    } catch (SubcommandRequiredException e) {
      System.out.println(msg.getString("subcommand.choose"));
    } catch (InvalidUsageException e) {
      StringBuilder errorMsg = new StringBuilder();
      errorMsg.append(e.getMessage());

      if (e.isFullHelpSuggested()) {
        errorMsg.append(System.getProperty("line.separator"));
        errorMsg.append(e.getCommand().getDescription().getHelp());
      }
      System.out.println(errorMsg);
    } catch (CommandException e) {
      handleException(e);
    } catch (AuthorizationException e) {
      System.out.println(msg.getString("error.no-permission"));
    } catch (InvocationCommandException e) {
      handleException(e);
    }
  }

  private static void handleException(Exception e) {
    log.log(Level.WARNING, "An unexpected error occured while executing the command", e);
    System.out.println(msg.getString("error.internal"));
  }


}
