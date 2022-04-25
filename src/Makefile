run:
	java -jar /usr/local/lib/antlr-4.10.1-complete.jar OwnLanguage.g4
	javac *.java
	java Main my_first_app2 > code.ll
	lli code.ll

clean:
	rm -f a.out
	rm -f OwnLanguage*.java
	rm -f *.class
	rm -f *.interp
	rm -f *.tokens
	rm -f *.bc
	rm -f *.ll
	rm -f *.s

