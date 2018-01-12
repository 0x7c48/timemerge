LEIN ?= lein

uberjar:
	$(LEIN) uberjar && \
	mv target/uberjar/tm-0.1.0-SNAPSHOT-standalone.jar target/uberjar/tm.jar

run:
	java -jar target/uberjar/tm.jar --help

simple-test:
	lein run test/test.txt test/test.txt -g -c -o 1.txt

real-test:
	java -jar target/uberjar/tm.jar /tmp/data1.txt /tmp/data1.txt -c -o mergeddata1.txt

clean:
	rm -rf target

lein-test:
	$(LEIN) test

check-formatting:
	$(LEIN) cljfmt check

java-home:
	echo $(JAVA_HOME)

export-java-home8:
	export JAVA_HOME=$(/usr/libexec/java_home -v 1.8)
