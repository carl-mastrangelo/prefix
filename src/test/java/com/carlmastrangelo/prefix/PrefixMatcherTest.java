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

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class PrefixMatcherTest {

  private static final List<String> PATHS =
      Arrays.asList(
          "/",
          "/index",
          "/home",
          "/about",
          "/contact",
          "/robots.txt",
          "/products/",
          "/products/1",
          "/products/2",
          "/products/3",
          "/products/3/image.jpg",
          "/admin",
          "/admin/products/",
          "/admin/products/create",
          "/admin/products/update",
          "/admin/products/delete");

  private static final List<String> KEYS =
      Arrays.asList(
          "/",
          "/notfound",
          "/admin/",
          "/admin/foo",
          "/contact",
          "/products",
          "/products/",
          "/products/33",
          "/products/3/image.jpg");

  private static final Map<String, String> KEYS_BEST = new HashMap<>();

  static {
    for (String key : KEYS) {
      String best = null;
      for (String prefix : PATHS) {
        if ((best == null || best.length() < prefix.length())) {
          if (key.equals(prefix) || (prefix.endsWith("/") && key.startsWith(prefix))) {
            best = prefix;
          }
        }
      }
      KEYS_BEST.put(key, best);
    }
  }

  @Parameterized.Parameter public PathPrefixMatcher<String> matcher;

  @Parameterized.Parameters(name = "matcher {0}")
  public static Object[] params() {
    Map<String, String> mapping = PATHS.stream().collect(Collectors.toMap(k -> k, v -> v));
    return new Object[] {
      new PlainPrefixMatcher<>(mapping),
      new SortedPrefixMatcher<>(mapping),
      new TriePathMatcher<>(mapping),
      new PrefixMapMatcher<>(mapping)
    };
  }

  @Test
  public void match() {
    for (String key : KEYS) {
      assertEquals(key, KEYS_BEST.get(key), matcher.match(key));
    }
  }
}
