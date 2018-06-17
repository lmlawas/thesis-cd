import java.io.*;
import java.util.LinkedList;

interface Schedule {

	/* Constants */
	final public static int FIFO = 0;
	final public static int PQ = 1;
	final public static int WFQ = 2;

	/* Methods */
	abstract boolean process(int bandwidth, int current_time, int timeout, boolean debug);
	abstract boolean queueEmpty();
	abstract double throughput(int current_time);
	abstract int bufferSize();
	abstract void addFlow(Flow f);
	abstract void dropFlow(Flow f);
	abstract void info(int bandwidth, int duration, String dateAsText);
	abstract void saveResults(int bandwidth, int duration, String dateAsText) throws IOException;
	abstract void switchFlow(Flow f);
}