# Step 1
# Open a mysql cli to get the values for top out and in flows.
# See top_flows.sql for the mysql statements.
cat top_flows.sql

# Take note of the last 9 results for each statement.
# 	For out flow, save IP destination addresses of the top 9 and compare with long/outflow_dest_long.txt
# 	For in flow, save IP source addresses of the top 9 and compare with long/inflow_src_long.txt

# Step 2
# Convert integer (a.k.a. Java long datatype) IP addresses
# to quad-notation and save result to a new text file
java IPConverter long/inflow_src_long.txt > quad/inflow_src_quad.txt

# Step 3
# Get the company name associated with the IP address
# and save to text file
cat quad/inflow_src_quad.txt | while read line
do
	echo "$line = $(curl http://ipinfo.io/{$line}/org)" >> org/inflow_src_org.txt
done
