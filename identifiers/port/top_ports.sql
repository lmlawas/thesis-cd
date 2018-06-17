/* get the top ports */
select DISTINCT L4_DST_PORT,
count(idx),
round((count(idx)/554392)*100, 1)
from flowsv4
group by L4_DST_PORT
order by count(idx) asc;
