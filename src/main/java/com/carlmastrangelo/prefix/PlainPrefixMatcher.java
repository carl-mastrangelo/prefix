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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nullable;

final class PlainPrefixMatcher<T> implements PathPrefixMatcher<T> {

  private final Map<String, T> exact = new HashMap<>();
  private final List<String> prefixes = new ArrayList<>();
  private final List<T> values = new ArrayList<>();

  PlainPrefixMatcher(Map<String, T> mapping) {
    for (Map.Entry<String, T> entry : mapping.entrySet()) {
      if (entry.getKey().length() == 0 || entry.getKey().charAt(0) != '/') {
        throw new IllegalArgumentException("Doesn't begin with '/': " + entry.getKey());
      }
      exact.put(entry.getKey(), entry.getValue());
      if (entry.getKey().endsWith("/")) {
        prefixes.add(Objects.requireNonNull(entry.getKey()));
        values.add(Objects.requireNonNull(entry.getValue()));
      }
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
    int longest = -1;
    int longestPos = -1;
    for (int i = 0; i < prefixes.size(); i++) {
      String prefix = prefixes.get(i);
      if (prefix.length() > longest && path.startsWith(prefix)) {
        longest = prefix.length();
        longestPos = i;
      }
    }

    if (longestPos >= 0) {
      return values.get(longestPos);
    } else {
      return null;
    }
  }

  @Override
  public String toString() {
    return getClass().getSimpleName();
  }
}
