# NOTE:
# 	eth0 must be static
#	Check if overall bandwidth exceeds physical link of monitor port

# # #

# Turn off promiscuous eth1 START
sudo nano /etc/sysctl.conf	
	net.ipv6.conf.eth1.disable_ipv6=1 # add to "sysctl.conf" file

sudo nano /etc/network/interfaces
	# comment out existing eth1 references and add to "interfaces" file
	auto eth1
	iface eth1 inet manual
	        up ifconfig eth1 promisc up
	        down ifconfig eth1 promisc down

sudo reboot	# restart the machine
# Turn off promiscuous eth1 END

# # #

# Make necessary changes to "ntopng.conf"
# for the following fields:
# ntopngusername, ipaddress, dbname, tablename, dbusername, dbpassword
nano ntopng.conf

# Copy the modified configuration file
# from the current directory
# to "/etc/ntopng/ntopng.conf"
sudo cat ntopng.conf > /etc/ntopng/ntopng.conf
