import java.io.*;

public class IPConverter {

	public static void main(String[] args) throws Exception {
		if (args.length == 1) {
			FileReader fr = new FileReader(args[0]);
			BufferedReader br = new BufferedReader(fr);
			String s = null;
			int i = 0;
			int j = 0;
			while ((s = br.readLine()) != null) {
				String ipAddr = longToIp(Long.parseLong(s));
				System.out.println(ipAddr);
			}
			fr.close();
		} else {
			System.out.println("Missing argument.\n");
		}		
	}

	public static String longToIp(long i) {
		// source: https://www.mkyong.com/java/java-convert-ip-address-to-decimal-number/
		return ((i >> 24) & 0xFF) +
		       "." + ((i >> 16) & 0xFF) +
		       "." + ((i >> 8) & 0xFF) +
		       "." + (i & 0xFF);

	}
}
