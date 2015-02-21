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

package com.sk89q.intake.fluent;

import com.sk89q.intake.dispatcher.Dispatcher;
import com.sk89q.intake.dispatcher.SimpleDispatcher;
import com.sk89q.intake.parametric.ParametricBuilder;
import com.sk89q.intake.util.i18n.DefaultResourceProvider;
import com.sk89q.intake.util.i18n.ResourceProvider;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A fluent interface to creating a command graph.
 * 
 * <p>A command graph may have multiple commands, and multiple sub-commands below that,
 * and possibly below that.</p>
 */
public class CommandGraph {

    private final ResourceProvider resourceProvider;
    private final DispatcherNode rootDispatcher;
    private ParametricBuilder builder;

    /**
     * Create a new command graph.
     */
    public CommandGraph () {
        this(new DefaultResourceProvider());
    }

    /**
     * Create a new command graph.
     *
     * @param resourceProvider the ResourceProvider that provides resources for all
     *                         commands on this graph
     */
    public CommandGraph(ResourceProvider resourceProvider) {
        this.resourceProvider = checkNotNull(resourceProvider);
        SimpleDispatcher dispatcher = new SimpleDispatcher(resourceProvider);
        rootDispatcher = new DispatcherNode(this, null, dispatcher);
    }
    
    /**
     * Get the root dispatcher node.
     * 
     * @return the root dispatcher node
     */
    public DispatcherNode commands() {
        return rootDispatcher;
    }

    /**
     * Get the {@link ParametricBuilder}.
     * 
     * @return the builder, or null.
     */
    public ParametricBuilder getBuilder() {
        return builder;
    }

    /**
     * Set the {@link ParametricBuilder} used for calls to 
     * {@link DispatcherNode#registerMethods(Object)}.
     * 
     * @param builder the builder, or null
     * @return this object
     */
    public CommandGraph builder(ParametricBuilder builder) {
        this.builder = builder;
        return this;
    }

    /**
     * Get the root dispatcher.
     * 
     * @return the root dispatcher
     */
    public Dispatcher getDispatcher() {
        return rootDispatcher.getDispatcher();
    }

    /**
     * Get the ResourceProvider.
     *
     * @return the ResourceProvider
     */
    public ResourceProvider getResourceProvider() {
        return resourceProvider;
    }
}
