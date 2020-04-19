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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;
import nl.basjes.collections.PrefixMap;
import nl.basjes.collections.prefixmap.StringPrefixMap;

final class PrefixMapMatcher<T extends Serializable> implements PathPrefixMatcher<T> {
  private final PrefixMap<T> map = new StringPrefixMap<T>(true);
  private final Map<String, T> exact = new HashMap<>();

  PrefixMapMatcher(Map<String, T> mapping) {
    exact.putAll(mapping);
    for (Map.Entry<String, T> entry : mapping.entrySet()) {
      if (entry.getKey().endsWith("/")) {
        map.put(entry.getKey(), entry.getValue());
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
    return map.getLongestMatch(path);
  }
}
