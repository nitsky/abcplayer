abcplayer
=========

Interprets and plays back music files in [ABC Notation](https://en.wikipedia.org/wiki/ABC_notation) with a MIDI sequencer.

To play some tunes:

```bash
mkdir bin
find ./src -name "*.java" -and -not -name "*Test.java" | xargs javac -d ./bin
java -classpath ./bin player.Main music/invention.abc
```

