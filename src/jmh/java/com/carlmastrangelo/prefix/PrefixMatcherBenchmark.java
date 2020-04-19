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

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

@State(Scope.Benchmark)
public class PrefixMatcherBenchmark {
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
          "/products/3/image.jpg");

  PathPrefixMatcher<String> plainMatcher;
  PathPrefixMatcher<String> sortedMatcher;
  PathPrefixMatcher<String> trieMatcher;

  @Setup
  public void setup() {
    Map<String, String> mapping = PATHS.stream().collect(Collectors.toMap(k -> k, v -> v));
    plainMatcher = new PlainPrefixMatcher<>(mapping);
    sortedMatcher = new SortedPrefixMatcher<>(mapping);
    trieMatcher = new TriePathMatcher<>(mapping);
  }

  @Benchmark
  @BenchmarkMode(Mode.AverageTime)
  @OutputTimeUnit(TimeUnit.NANOSECONDS)
  public int plain() {
    int tot = 0;
    for (String key : KEYS) {
      tot += plainMatcher.match(key).length();
    }
    return tot;
  }

  @Benchmark
  @BenchmarkMode(Mode.AverageTime)
  @OutputTimeUnit(TimeUnit.NANOSECONDS)
  public int sorted() {
    int tot = 0;
    for (String key : KEYS) {
      tot += sortedMatcher.match(key).length();
    }
    return tot;
  }

  /*
  @Benchmark
  @BenchmarkMode(Mode.AverageTime)
  @OutputTimeUnit(TimeUnit.NANOSECONDS)
  public int paddedSortedNormal() {
    int tot = 0;
    for (String key : KEYS) {
      tot += paddedSortedNormal(key).length();
    }
    return tot;
  }

   */

  @Benchmark
  @BenchmarkMode(Mode.AverageTime)
  @OutputTimeUnit(TimeUnit.NANOSECONDS)
  public int trie() {
    int tot = 0;
    for (String key : KEYS) {
      tot += trieMatcher.match(key).length();
    }
    return tot;
  }
}
