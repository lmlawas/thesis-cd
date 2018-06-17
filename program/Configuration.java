import java.io.BufferedReader;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.time.LocalDate;
import java.util.regex.Pattern;

public class Configuration {

    /* Attributes */
    private String tableName,
            dbName,
            ipAddr,
            password,
            port,
            username;

    private boolean debug,
            with_password;

    private int bandwidth,
            end_time,
            schedule_type,
            start_time,
            timeout;

    /* Getters */

    // ...for connection.

    boolean hasPassword() {
        return with_password;
    }

    String getIpAddress() {
        return ipAddr;
    }

    String getPortNumber() {
        return port;
    }

    String getDbName() {
        return dbName;
    }

    String getTableName() {
        return tableName;
    }

    String getUsername() {
        return username;
    }

    String getPassword() {
        return password;
    }

    // ...for queue.
    int getSchedule() {
        return schedule_type;
    }

    int getStartTime() {
        return start_time;
    }

    int getEndTime() {
        return end_time;
    }

    int getBandwidth() {
        return bandwidth;
    }

    int getTimeout() {
        return timeout;
    }

    // ... for main
    boolean getDebug() {
        return debug;
    }

    /* Setters */

    // ...for connection.
    boolean setIpAddress(String ipAddr) {
        boolean match = Pattern.matches("([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                                        "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                                        "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                                        "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$",
                                        ipAddr);
        if (match) {
            this.ipAddr = ipAddr;
            return true;
        }
        return false;
    }

    boolean setPortNumber(String port) {
        boolean match = Pattern.matches("[0-9]{1,5}+", port);
        if (match) {
            this.port = port;
            return true;
        }
        return false;
    }

    void setDbName(String dbName) {
        this.dbName = dbName;
    }

    void setTableName(String tableName) {
        this.tableName = tableName;
    }

    void setUsername(String username) {
        this.username = username;
    }

    void setPassword(String password) {
        this.password = password;
    }

    // for queue
    void setSchedule(int schedule_type) {
        this.schedule_type = schedule_type;
    }

    void setStartTime(int start_time) {
        this.start_time = start_time;
    }

    void setEndTime(int end_time) {
        this.end_time = end_time;
    }

    void setBandwidth(int bandwidth) {
        this.bandwidth = bandwidth;
    }

    void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    // ...for main
    void setDebug(boolean d) {
        debug = d;
    }

    /* Methods */

    void setDefault() {

        // connection configuration
        ipAddr = "localhost";
        port = "3306";
        dbName = "ntopng";
        tableName = "flowsv4";
        username = "root";
        with_password = false;
        password = "";

        // queue configuration
        bandwidth = 1000000000; // 1Gb
        schedule_type = 0; // FIFO
        timeout = 60; // 60 seconds
        start_time = 0; // init
        end_time = 86400; // 24 hours

        // program mode
        debug = false;
    }

    int updateConfig(String input) {
        boolean match = false;

        // comment
        match = Pattern.matches("#.*", input);
        if (match) {
            return 0; // no update
        }

        // white space
        match = Pattern.matches("", input);
        if (match) {
            return 0; // no update
        }

        match = Pattern.matches("^--ip-address=" +
                                "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                                "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                                "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                                "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$",
                                input);
        if (match) {
            String[] parts = input.split("=");
            setIpAddress(parts[1]);
            return 1;
        }

        match = Pattern.matches("^--port=" + "[0-9]{1,5}+", input);
        if (match) {
            String[] parts = input.split("=");
            setPortNumber(parts[1]);
            return 1;
        }

        match = Pattern.matches("^--database=.+", input);
        if (match) {
            String[] parts = input.split("=");
            setDbName(parts[1]);
            return 1;
        }

        match = Pattern.matches("^--table=.+", input);
        if (match) {
            String[] parts = input.split("=");
            setTableName(parts[1]);
            return 1;
        }

        match = Pattern.matches("^--username=.+", input);
        if (match) {
            String[] parts = input.split("=");
            setUsername(parts[1]);
            return 1;
        }

        match = Pattern.matches("^--password", input);
        if (match) {
            with_password = true;
            return 1;
        }

        match = Pattern.matches("^--bandwidth=[0-9]+?", input);
        if (match) {
            String[] parts = input.split("=");
            setBandwidth(Integer.parseInt(parts[1]));
            return 1;
        }

        match = Pattern.matches("^--schedule=[0-2]", input);
        if (match) {
            String[] parts = input.split("=");
            setSchedule(Integer.parseInt(parts[1]));
            return 1;
        }

        match = Pattern.matches("^--timeout=[0-9]+?", input);
        if (match) {
            String[] parts = input.split("=");
            setTimeout(Integer.parseInt(parts[1]));
            return 1;
        }

        match = Pattern.matches("^--starttime=[0-9]+?", input);
        if (match) {
            String[] parts = input.split("=");
            setStartTime(Integer.parseInt(parts[1]));
            return 1;
        }

         match = Pattern.matches("^--endtime=[0-9]+?", input);
        if (match) {
            String[] parts = input.split("=");
            setEndTime(Integer.parseInt(parts[1]));
            return 1;
        }

        match = Pattern.matches("^--debug", input);
        if (match) {
            debug = true;
            return 1;
        }

        return -1;
    }

    int readFile(String filename) {
        try {
            FileReader fp = new FileReader(filename);
            BufferedReader br = new BufferedReader(fp);
            String line = null;
            int line_no = 1, update = 0;

            while ((line = br.readLine()) != null) {
                update = updateConfig(line);
                if (update == -1) {
                    System.out.println("Error in .conf file line " + line_no);
                    return -1;
                }
                line_no++;
            }

            return 1;
        } catch (Exception e) {
            System.out.println("Error reading file.");
            return -1;
        }
    }

    void show() {

        String sched = null;
        if (schedule_type == Schedule.FIFO) {
            sched = "FIFO";
        } else if (schedule_type == Schedule.PQ) {
            sched = "PQ";
        } else if (schedule_type == Schedule.WFQ) {
            sched = "WFQ";
        }

        System.out.println("-------------------------------");
        System.out.println("CONFIGURATION");

        // connection configuration
        System.out.println("\t[ database connection ]");
        System.out.println("\tusername = " + username);
        System.out.println("\twith_password = " + with_password);
        System.out.println("\tipAddr = " + ipAddr);
        System.out.println("\tport = " + port);
        System.out.println("\tdbName = " + dbName);
        System.out.println("\ttableName = " + tableName);

        // queue configuration
        System.out.println("\n\t[ queue ]");
        System.out.println("\tbandwidth = " + bandwidth + " bps");
        System.out.println("\tschedule = " + sched);
        System.out.println("\ttimeout = " + timeout + " s");
        System.out.println("\tstart time = " + start_time + " s");
        System.out.println("\tend time = " + end_time + " s");

        // program mode
        System.out.println("\n\t[ program mode ]");
        System.out.println("\tdebug = " + debug + "\n");
    }
}
