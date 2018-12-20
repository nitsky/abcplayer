build:
	mkdir -p classes
	find ./src -name "*.java" -and -not -name "*Test.java" | xargs javac -d ./classes
	jar vcfm abcplayer.jar manifest.mf -C ./classes/ .
	rm -rf classes
clean:
	rm abcplayer.jar
