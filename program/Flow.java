import java.io.BufferedReader;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.regex.Pattern;

public class Flow {

	public static int BITS_PER_BYTE = 8;

	/* Attributes */
	public int first_switched,
	       no_of_packets,
	       priority,
	       size;

	/* Constructors */
	public Flow() {
	}

	public Flow(int first_switched, int no_of_packets, int size) {
		this.first_switched = first_switched;
		this.no_of_packets = no_of_packets * BITS_PER_BYTE;
		this.priority = 0;
		this.size = size * BITS_PER_BYTE;
	}

	public Flow(int first_switched, int no_of_packets, int protocol, int size) {
		this.first_switched = first_switched;
		this.no_of_packets = no_of_packets * BITS_PER_BYTE;
		this.priority = getPriority(protocol);
		this.size = size * BITS_PER_BYTE;
	}

	/* Methods */
	// public LinkedList<Packet> convertToPackets(int schedule_type) {
	// 	LinkedList<Packet> packets = new LinkedList<Packet>();
	// 	int i;

	// 	for (i = 0; i < no_of_packets; i++) {
	// 		packets.add(new Packet(first_switched, priority, (double)size/no_of_packets));
	// 	}

	// 	return packets;
	// }

	public boolean matchCommas(String line) {
		boolean match = false;

		match = Pattern.matches("(.*),([0-9]+)(,.*)*", line);
		return match;
	}

	public int getPriority(int protocol) {
		try {

			String file_location = "../priority/1.txt";
			FileReader fp = new FileReader(file_location);
			BufferedReader br = new BufferedReader(fp);
			String line = null;

			while ((line = br.readLine()) != null) {
				if (matchCommas(line)) {
					String[] tokens = line.split(",");
					int port = Integer.parseInt(tokens[1]);
					if (protocol == port) {
						return 0;
					}
				}
			}
			br.close();
			fp.close();
			return 1;
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}

	public int waitTime(int current_time){
		return (current_time-first_switched);
	}

	public void info(){
		System.out.println("------------\nflow info:\n------------");
		System.out.println("\tfirst_switched = " + first_switched);
		System.out.println("\tpriority = " + priority);
		System.out.println("\tno_of_packets = " + no_of_packets);
		System.out.println("\tsize = " + size + "\n------------\n");
	}
}