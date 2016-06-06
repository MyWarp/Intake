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

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.annotation.Nullable;

/**
 * Represents a parameter that a binding can provide a value for.
 */
public final class Key<T> {

    private final Type type;
    @Nullable
    private final Class<? extends Annotation> classifier;

    private Key(Type type, @Nullable Class<? extends Annotation> classifier) {
        this.type = type;
        this.classifier = classifier;
    }

    public Type getType() {
        return type;
    }

    @Nullable
    public Class<? extends Annotation> getClassifier() {
        return classifier;
    }

    public Key<T> setClassifier(@Nullable Class<? extends Annotation> classifier) {
        return new Key<T>(type, classifier);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Key<?> key = (Key<?>) o;

        if (type != null ? !type.equals(key.type) : key.type != null) {
            return false;
        }
        return classifier != null ? classifier.equals(key.classifier) : key.classifier == null;

    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (classifier != null ? classifier.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Key{" +
                "type=" + type +
                ", classifier=" + classifier +
                '}';
    }

    public static <T> Key<T> get(Class<T> type) {
        return new Key<T>(type, null);
    }

    public static <T> Key<T> get(Class<T> type, @Nullable Class<? extends Annotation> classifier) {
        return new Key<T>(type, classifier);
    }

    public static <T> Key<T> get(Type type) {
        return new Key<T>(type, null);
    }

    public static <T> Key<T> get(Type type, @Nullable Class<? extends Annotation> classifier) {
        return new Key<T>(type, classifier);
    }

}
