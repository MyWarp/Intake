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

package com.sk89q.intake.internal.parametric;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.sk89q.intake.parametric.Binding;
import com.sk89q.intake.parametric.Key;
import com.sk89q.intake.parametric.Provider;

import java.lang.reflect.Type;

import javax.annotation.Nullable;

class BindingList {

    private final Multimap<Type, BindingEntry<?>> providers = HashMultimap.create();

    public <T> void addBinding(Key<T> key, Provider<T> provider) {
        checkNotNull(key, "key");
        checkNotNull(provider, "provider");

        if (!providers.put(key.getType(), new BindingEntry<T>(key, provider))) {
            throw new IllegalArgumentException(
                "Failed to register " + provider + ", as a provider is already registered for " + key + "!");
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Nullable
    public <T> Binding<T> getBinding(Key<T> key) {
        checkNotNull(key, "key");
        for (BindingEntry binding : providers.get(key.getType())) {
            if (binding.getKey().equals(key)) {
                return (Binding<T>) binding;
            }
        }

        return null;
    }

    private static final class BindingEntry<T> implements Binding<T> {
        private final Key<T> key;
        private final Provider<T> provider;

        private BindingEntry(Key<T> key, Provider<T> provider) {
            this.key = key;
            this.provider = provider;
        }

        @Override
        public Key<T> getKey() {
            return key;
        }

        @Override
        public Provider<T> getProvider() {
            return provider;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            BindingEntry<?> that = (BindingEntry<?>) o;

            if (key != null ? !key.equals(that.key) : that.key != null) {
                return false;
            }
            return provider != null ? provider.equals(that.provider) : that.provider == null;

        }

        @Override
        public int hashCode() {
            int result = key != null ? key.hashCode() : 0;
            result = 31 * result + (provider != null ? provider.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return "BindingEntry{" +
                    "key=" + key +
                    ", provider=" + provider +
                    '}';
        }
    }
}
