# run ntopng as sudo
ntopng "/etc/ntopng/ntopng.conf"

# kill ntopng process
ps -A | grep "ntopng" # This will give the process id "pid" of ntopng
kill <pid> 
