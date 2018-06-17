# package installation
dpkg -i apt-ntop-stable.deb
apt-get clean all
apt-get update
apt-get install pfring nprobe ntopng ntopng-data n2disk cento nbox

# to keep system up to date
apt-get update
apt-get upgrade