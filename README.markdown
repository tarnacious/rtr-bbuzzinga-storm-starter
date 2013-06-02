# Example Storm topologies

1. word-count 

2. group-word-count

## Dependencies

[Leiningen](https://github.com/technomancy/leiningen)

### To run a Clojure example:

```
lein deps
lein compile
lein run -m storm.starter.clj.word-count
```

### group-word-count

A bolt to try out some simple bolts needed for more complex
topologies. Include aggregation and periodic emits.

Also the spout uses a jeromq socket to get data. There is a 
simple program included to read data from a file and push
it down a ZMQ socket.

1) Spout reads words from ZMQ (or test-line-spout)
2) Groups words by word to word bolt 
3) Word bolt keeps dict count for each unique word
4) Word bolt periodically emits dict or counts
5) Aggregation bolt combines dicts from word bolts into a big dict
6) Pass to sorting bolt TODO.
7) Push to top results somewhere, ZMQ? TODO.

The data source reads lines for a file ./pagecounts/out.txt (not included)
and pushes them down a ZMQ socket. It can be started with:

```
lein run
```

One that is started we can start the topology:

```
lein run -m storm.starter.clj.group-word-count
```
