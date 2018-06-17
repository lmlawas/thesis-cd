import java.io.*;
import java.util.LinkedList;

public class FirstInFirstOut implements Schedule {

	/* Attributes */
	public int flows_dropped_cnt;
	public int flows_switched_cnt;
	public int processing_time;
	public long total_wait_time;
	public double flows_dropped_size;
	public double flows_switched_size;
	public NetworkBuffer buffer;

	/* Constructor */
	public FirstInFirstOut() {
		// for throughput
		flows_dropped_size = 0;
		flows_switched_size = 0;

		// for total flows
		flows_dropped_cnt = 0;
		flows_switched_cnt = 0;

		// // for duration
		processing_time = -1;

		// for average wait time
		total_wait_time = 0;

		// for buffer
		buffer = new NetworkBuffer();
	}

	/* Methods */
	public boolean queueEmpty() {
		if (buffer.isEmpty()) return true;
		return false;
	}

	public int bufferSize() {
		return buffer.size();
	}

	public double throughput(int duration) {
		// System.out.println(flows_switched_size);
		// System.out.println(duration);
		return (flows_switched_size / (double)duration);
	}

	public boolean process(int bandwidth, int current_time, int timeout, boolean debug) {
		Flow f;

		while (!buffer.isEmpty()) {
			f = buffer.peekFirst();

			// if nothing is being processed
			if (processing_time == -1) {
				// (flow size in bits + overhead)/ bits/second
				processing_time = (int)Math.ceil((f.size + f.no_of_packets + 0.0) / bandwidth * 1.0);
				// if flow cannot be processed before timeout
				if (processing_time >= timeout) {
					dropFlow(f);
					total_wait_time = total_wait_time + (current_time - f.first_switched);
					processing_time = -1;
					if (debug) {
						System.out.println("-- Dropping flow --");
						bufferSize();
						f.info();
					}
				} else {
					switchFlow(f);
					total_wait_time = total_wait_time + (current_time - f.first_switched);
					processing_time--;
					if (debug) {
						System.out.println("++ Switching flow ++");
						bufferSize();
						f.info();
					}

					// if process is finished
					if (processing_time <= 0) break;
					// else process is ongoing
					else return true;
				}
			}
			// else the flow can be processed
			else if (processing_time > 0) {

				switchFlow(f);
				total_wait_time = total_wait_time + (current_time - f.first_switched);
				processing_time--;
				if (debug) {
					System.out.println("++ Switching flow ++");
					bufferSize();
					f.info();
				}

				// if process is finished
				if (processing_time <= 0) break;

				// else process is ongoing
				else return true;
			}
			else break;
		}

		processing_time = -1;
		// default: process is not ongoing
		return false;
	}

	public void addFlow(Flow f) {
		buffer.add(f);
	}

	public void dropFlow(Flow f) {
		buffer.remove();
		flows_dropped_size += (f.size + f.no_of_packets);
		flows_dropped_cnt++;
	}

	public void info(int bandwidth, int duration, String dateAsText) {
		System.out.println("\n\n------[ FIFO SIMULATION SUMMARY ]------");
		System.out.println("TIMESTAMP = " + dateAsText);
		System.out.println("BANDWIDTH = " + bandwidth + " bps\n");
		System.out.println("flows_dropped_size = " + flows_dropped_size + " b");
		System.out.println("flows_dropped_cnt = " + flows_dropped_cnt + " flows\n");
		System.out.println("flows_switched_size = " + flows_switched_size + " b");
		System.out.println("flows_switched_cnt = " + flows_switched_cnt + " flows\n");
		System.out.println("total_wait_time = " + total_wait_time + " seconds");
		System.out.println("duration = " + duration + " seconds");
		System.out.println("throughput = " + throughput(duration) + " bps");
	}

	public void saveResults(int bandwidth, int duration, String dateAsText) throws IOException {
		FileWriter fw = new FileWriter("results/" + dateAsText + "_" + bandwidth + ".txt", true);
		fw.write("\n\n------[ FIFO SIMULATION SUMMARY ]------");
		fw.write("\nTIMESTAMP = " + dateAsText);
		fw.write("\nBANDWIDTH = " + bandwidth + " bps\n");
		fw.write("\n\tflows_dropped_size = " + flows_dropped_size + " b");
		fw.write("\n\tflows_dropped_cnt = " + flows_dropped_cnt + " flows\n");
		fw.write("\n\tflows_switched_size = " + flows_switched_size + " b");
		fw.write("\n\tflows_switched_cnt = " + flows_switched_cnt + " flows\n");
		fw.write("\n\ttotal_wait_time = " + total_wait_time + " seconds");
		fw.write("\n\tduration = " + duration + " seconds");
		fw.write("\n\tthroughput = " + throughput(duration) + " bps");
		fw.close();
	}

	public void switchFlow(Flow f) {
		buffer.remove();
		flows_switched_size += (f.size + f.no_of_packets);
		flows_switched_cnt++;
	}
}
