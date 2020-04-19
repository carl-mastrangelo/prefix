/*
 * Copyright 2020 Carl Mastrangelo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.carlmastrangelo.prefix;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nullable;

final class TriePathMatcher<T> implements PathPrefixMatcher<T> {

  private final Map<String, T> exact = new HashMap<>();
  private final TrieNode<T> root = new TrieNode<>();

  TriePathMatcher(Map<String, T> mapping) {
    exact.putAll(mapping);
    for (Map.Entry<String, T> entry : mapping.entrySet()) {
      String prefix = Objects.requireNonNull(entry.getKey());
      T element = Objects.requireNonNull(entry.getValue());
      if (prefix.length() == 0 || prefix.charAt(0) != '/') {
        throw new IllegalArgumentException("Doesn't begin with '/': " + prefix);
      }
      if (!prefix.endsWith("/")) {
        continue;
      }
      if (prefix.equals("/")) {
        root.element = element;
        continue;
      }
      TrieNode<T> current = root;
      int from = 1;
      do {
        int to = prefix.indexOf('/', from);
        if (to == -1) {
          to = prefix.length();
        } else {
          to += 1;
        }
        String part = prefix.substring(from, to);
        from = to;
        current = current.getOrDefault(part);
      } while (from < prefix.length());
      assert current.element == null;
      current.element = element;
    }
  }

  @Nullable
  @Override
  public T match(String path) {
    if (path.length() == 0 || path.charAt(0) != '/') {
      throw new IllegalArgumentException("Doesn't begin with '/': " + path);
    }
    T exactMatch = exact.get(path);
    if (exactMatch != null) {
      return exactMatch;
    }
    TrieNode<T> current = root;
    T element = current.element;
    int from = 1;
    do {
      int to = path.indexOf('/', from);
      if (to == -1) {
        to = path.length();
      } else {
        to += 1;
      }
      TrieNode<T> child = current.get(path, from, to);
      from = to;
      if (child == null) {
        return element;
      } else {
        current = child;
        if (current.element != null) {
          element = current.element;
        }
      }
    } while (from < path.length());
    return element;
  }

  private static final class TrieNode<T> {

    private static final int SIZE = 8;

    private final String[] keys = new String[SIZE];

    @SuppressWarnings("unchecked")
    private final TrieNode<T>[] values = new TrieNode[SIZE];

    @Nullable T element;

    TrieNode<T> getOrDefault(String part) {
      int h = hash(part, 0, part.length()) & (SIZE - 1);
      if (keys[h] != null) {
        if (keys[h].equals(part)) {
          return values[h];
        } else {
          throw new AssertionError("too small");
        }
      } else {
        keys[h] = part;
        values[h] = new TrieNode<>();
        return values[h];
      }
    }

    TrieNode<T> get(String key, int start, int stop) {
      int h = hash(key, start, stop) & (SIZE - 1);
      if (keys[h] != null) {
        if (key.regionMatches(start, keys[h], 0, stop - start)) {
          return values[h];
        } else {
          throw new AssertionError("too small");
        }
      } else {
        return null;
      }
    }

    private int hash(String key, int start, int stop) {
      int h = 0;
      for (int i = start; i < stop; i++) {
        h = h * 31 + key.charAt(i);
      }
      return h;
    }
  }

  @Override
  public String toString() {
    return getClass().getSimpleName();
  }
}
