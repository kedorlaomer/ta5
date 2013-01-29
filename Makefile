JOPTS=-cp junit-4.11.jar:.

run : compile
	java Main

test : compileTests
	java $(JOPTS) org.junit.runner.JUnitCore `ls *.class | grep Test.class | sed 's/.class//'`

clean :
	-rm *.class

compile : clean
	javac -g $(JOPTS) *.java

compileTests : clean
	ls *.java | grep Test.java | xargs javac $(JOPTS)

dist : compile test
	tar cjvf dist.tbz *.java *.class junit-4.11.jar
