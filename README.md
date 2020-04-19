# Longest Prefix Matching

Matching Prefixes is significantly faster than other kinds of matching, and usually good enough.
For example, when doing HTTP routing based on path, matching the longest prefix can pick the correct
handler.  This is much faster than regular expressions.

## Matching

I will define matching as finding a prefix that is the longest given a set of prefixes.  A prefix is 
"longer" than other paths if it ends in a `/`, and has a longer common prefix than the others
available.  Thus, given the following set of prefixes:

* `/`
* `/products`
* `/admin/`
* `/admin/del`

Given these handlers, the following paths will match:

* `/` matches `/` 
* `/foo` matches `/`
* `/admin` matches `/`
* `/admin/foo` matches `/admin/`
* `/products/` matches `/`
* `/admin/delete` matches  `/admin/`

This lines up with my expectation of how "directory"-like URL paths would be matched. 


## Algorithms

### Results:

```
Benchmark                      Mode  Cnt    Score    Error  Units
PrefixMatcherBenchmark.plain   avgt    5  150.819 ± 17.053  ns/op
PrefixMatcherBenchmark.sorted  avgt    5  134.283 ±  1.244  ns/op
PrefixMatcherBenchmark.trie    avgt    5  186.083 ±  4.477  ns/op
```

Feel free to doubt and criticise these, there are almost certainly bugs.


### Linear Scan

One way to find the longest match is to iterate through a list of possible matches, and pick the 
longest matching prefix.  This is O(N) and will be slow for a large set of prefixes.  This is 
ameliorated by combining it with a hashtable which is used for exact matches.   This makes it O(1)
for exact matches (hopefully common) and slower for prefixes.

Linear scan takes longer to search, but permits faster updates.  Matchers that update frequently
may prefer this.

### Sorted Linear Scan

Instead of scanning the set of prefixes, we can sort the matches by decreasing length.  Thus, as 
soon as we find a match, we can return early.  This has O(N) runtime complexity, but the constant
out front can be reduced.   If most matches are longer, this avoids reading most of the list.

This method is slower to calculate O(N log N).  Updating it usually takes between O(N) and O(log N)
time depending on the implementation.  Note: this is the matching behavior used by Go's 
DefaultServeMux.

### Trie Match

To avoid looking at every possible match, we could build a Trie and walk it looking for 
progressively longer matches.  The best matching element is returned once we cannot find a matching
child.  The runtime complexity is O(MaxP), where MaxP is the number of `/` characters in the longest
possible prefix.

Trie matching suffers from highly branching code and bad cache locality.  In addition, 
Java makes it hard to do substring matching efficiently.  Much to the chagrin of new college grads,
O() runtime complexity wins rarely turn into speed ups. 















