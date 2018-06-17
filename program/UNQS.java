import java.io.Console;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Scanner;

public class UNQS {
    public static void main(String args[]) {

        Configuration config = new Configuration();
        Console sensitive = System.console();
        Scanner input = new Scanner(System.in);
        String choice, filename = null, s;
        int read = 0;

        config.setDefault();

        if (args.length == 0) {
            do {
                System.out.print("Continue with the default configuration? (Y/N) ");
                choice = input.nextLine();

                /* use default configuration */
                if (choice.equalsIgnoreCase("y") || choice.equalsIgnoreCase("yes")) {
                    break;
                } else if (choice.equalsIgnoreCase("n") || choice.equalsIgnoreCase("no")) {
                    System.out.print("Enter <filename>.conf (must be in same directory of UNQS): ");
                    filename = input.nextLine();
                    break;
                } else {
                    System.out.println("Invalid choice. Try again.");
                }
            } while (true);
        } else if (args.length > 1) {
            System.out.println("Error. Too many arguments. Please re-run program.");
            return;
        } else if (args.length == 1) {
            filename = args[0];
        }

        /* configure using .conf file */
        if (filename != null) {
            read = config.readFile(filename);
            if (read == -1) {
                return;
            }
        }

        /* get password if any */
        if (config.hasPassword()) {
            if (sensitive == null) return;
            System.out.print("MySQL password: ");
            s = String.valueOf(sensitive.readPassword());
            config.setPassword(s);
        }

        config.show();

        /* connect to database */
        System.out.println("-------------------------------");
        System.out.print("\nConnecting to database...");
        try {

            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://" + config.getIpAddress() + ":" + config.getPortNumber() + "/" + config.getDbName(), config.getUsername(), config.getPassword());

            System.out.print("successfully connected.\n");

            Statement stmt = con.createStatement();
            ResultSet flows;
            int current_time = config.getStartTime();
            Flow single_flow;
            Schedule sched;

            if (config.getSchedule() ==  Schedule.FIFO) {
                sched = new FirstInFirstOut();
            } else {
                System.out.println("Invalid schedule type.\n");
                return;
            }
            // else if (config.getSchedule() == Schedule.PQ) {
            //     sched = new PriorityQueue();
            // } else if (config.getSchedule() == Schedule.WFQ) {
            //     sched = new WaitFairQueue();
            // }

            System.out.print("Processing flows...");

            while (current_time <= config.getEndTime()) {

                // get flows at current_time from the database
                flows = stmt.executeQuery("select FIRST_SWITCHED, PACKETS, L4_DST_PORT, IN_BYTES from `" + config.getTableName() + "` WHERE FIRST_SWITCHED = " + current_time + ";");

                if (config.getDebug()) System.out.println("[ current_time = " + current_time + " ]");

                // if there are flows, add them to buffer
                while ( flows.next() ) {
                    if (config.getSchedule() == Schedule.FIFO) {
                        single_flow = new Flow(flows.getInt(1), flows.getInt(2), flows.getInt(4));
                    } else {
                        single_flow = new Flow(flows.getInt(1), flows.getInt(2), flows.getInt(3), flows.getInt(4));
                    }

                    // add to schedule's buffer
                    sched.addFlow(single_flow);

                    if (config.getDebug()) {
                        System.out.println("++Add flow");
                        single_flow.info();
                    }
                }
                
                sched.process(config.getBandwidth(), current_time, config.getTimeout(), config.getDebug());

                if (config.getDebug()) {
                    System.out.println("buffer size = " + sched.bufferSize());
                }
                current_time++;
            }

            while (!sched.queueEmpty()) {
                if (config.getDebug()) System.out.println("[ current_time = " + current_time + " ]");

                    sched.process(config.getBandwidth(), current_time, config.getTimeout(), config.getDebug());

                if (config.getDebug()) {
                    System.out.println("buffer size = " + sched.bufferSize());
                }
                current_time++;
            }

            System.out.print("done.\n\n");
            sched.info(config.getBandwidth(), current_time - 1 - config.getStartTime(), readableDate(config.getStartTime()));
            sched.saveResults(config.getBandwidth(), current_time - 1 - config.getStartTime(), readableDate(config.getStartTime()));

            con.close();
        } catch (Exception e) {
            System.out.print("error connecting.\n");
            System.out.println(e);
            e.printStackTrace();
        }
    }

    public static String readableDate(int timestamp) {
        String dateAsText = new SimpleDateFormat("yyyy-MM-dd")
        .format(new Date(timestamp * 1000L));
        return dateAsText;
    }
}
