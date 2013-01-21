JOPTS=-cp junit-4.11.jar:.

run : compile
	java Main

test : compile
	java $(JOPTS) org.junit.runner.JUnitCore `ls *.class | grep Test.class | sed 's/.class//'`

clean :
	-rm *.class

compile : clean
	javac $(JOPTS) *.java

dist : compile test
	tar cjvf dist.tbz *.java *.class junit-4.11.jar
