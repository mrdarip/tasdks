## SQLITE timediff is not working???

| attempted                                                                                                                                                                                                                                                                                                          | result                                           |
|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|--------------------------------------------------|
| timediff(dateA, dateB)                                                                                                                                                                                                                                                                                             | it has magically been removed from sqlite(?      |
| repetitionUnit = 'YEARS' AND datetime(     activators.`end`, 'unixepoch', (substr(timediff('now',datetime(activators.`end`, 'unixepoch')),1,5)+1)\|\| ' years',     '-'\|\|      datetime( activators.start, 'unixepoch', (substr(timediff('now',datetime( activators.start, 'unixepoch')),1,5)+0)\|\| ' years') ) | still timediff has been removed                  |
| select  dateTime(1718841600,'unixepoch', '+2024-06-20 00:00:00')                                                                                                                                                                                                                                                   | even is listed in the official docs, not working |

## using strftime instead of timediff

having 1-2-34 as start and 2-3-34 as end, lets check if we're in range in this year

```
select dateTime('2034-02-01 00:00', '-' || abs(strftime('%Y','now') - strftime('%Y','2034-02-01 00:00')) || ' years'),
dateTime('2034-02-02 00:00', '-' || abs(strftime('%Y','now') - strftime('%Y','2034-02-01 00:00')) || ' years')
```

this query returns the dates on current year