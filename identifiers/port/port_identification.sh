# Step 1
# Open a mysql cli to get the values for top ports
# See top_ports.sql for the mysql statements.
cat top_ports.sql

# Take note of the last 11 results.

# Step 2
# Use grep to find the application/protocol to match the port
# Replace <port_number> with appropriate  value
grep <port_number> service-names-port-numbers.csv
