/* mysql statements */

/* get the top out flows */
select IP_SRC_ADDR src,
IP_DST_ADDR dst,
SUM(IN_BYTES+OUT_BYTES) total_bytes,
ROUND(SUM(IN_BYTES+OUT_BYTES)/102982614457*100, 1) percent_bytes
from flowsv4
where IP_DST_ADDR NOT IN (select IP_DST_ADDR from local_ip_addr)
group by src, dst order by SUM(IN_BYTES+OUT_BYTES) asc;

/* get the top in flows */
select IP_SRC_ADDR src,
IP_DST_ADDR dst,
SUM(IN_BYTES+OUT_BYTES) total_bytes,
ROUND(SUM(IN_BYTES+OUT_BYTES)/184191603946*100, 1) percent_bytes
from flowsv4
where IP_DST_ADDR IN (select IP_DST_ADDR from local_ip_addr)
group by src, dst order by SUM(IN_BYTES+OUT_BYTES) asc;

/* get the top ports */
select DISTINCT L4_DST_PORT,
count(idx),
round((count(idx)/554392)*100, 1)
from flowsv4
group by L4_DST_PORT
order by count(idx) asc;
