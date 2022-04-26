run:
	@lli code.ll

compile:
	@java -jar /usr/local/lib/antlr-4.10.1-complete.jar OwnLanguage.g4
	@javac *.java
	@java Main $(source) > code.ll

clean:
	@rm -f OwnLanguage*.java
	@rm -f *.class
	@rm -f *.interp
	@rm -f *.tokens
	@rm -f *.ll

