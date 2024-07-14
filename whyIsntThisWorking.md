# Problems and their solutions during the development

## SQLITE timediff is not working??? How to fix getPending() YEAR query

| attempted                                                                                                                                                                                                                                                                                                          | result                                           |
|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|--------------------------------------------------|
| timediff(dateA, dateB)                                                                                                                                                                                                                                                                                             | it has magically been removed from sqlite(?      |
| repetitionUnit = 'YEARS' AND datetime(     activators.`end`, 'unixepoch', (substr(timediff('now',datetime(activators.`end`, 'unixepoch')),1,5)+1)\|\| ' years',     '-'\|\|      datetime( activators.start, 'unixepoch', (substr(timediff('now',datetime( activators.start, 'unixepoch')),1,5)+0)\|\| ' years') ) | still timediff has been removed                  |
| select  dateTime(1718841600,'unixepoch', '+2024-06-20 00:00:00')                                                                                                                                                                                                                                                   | even is listed in the official docs, not working |

### using strftime instead of timediff

having 1-2-34 as start and 2-3-34 as end, lets check if we're in range in this year

```
select dateTime('2034-02-01 00:00', '-' || abs(strftime('%Y','now') - strftime('%Y','2034-02-01 00:00')) || ' years'),
dateTime('2034-02-02 00:00', '-' || abs(strftime('%Y','now') - strftime('%Y','2034-02-01 00:00')) || ' years')
```

this query returns the dates on current year, lets try now comparing with current date

dateTime('now') > dateTime('2034-02-01 00:00', '-' || abs(strftime('%Y','now') - strftime('%Y','
2034-02-01 00:00')) || ' years') AND
dateTime('now')  < dateTime('2034-02-02 00:00', '-' || abs(strftime('%Y','now') - strftime('%Y','
2034-02-01 00:00')) || ' years')

this query returns if we are in the range of 2034-02-01 00:00 and 2034-02-02 00:00

### The query fails when start and end dates are on different years

If you put 2000-12-31 as start and 2001-12-31 as end date, the query will fail, the query will
convert that to 2024 and 2025 on 2024 so if we were on 31 of december the query would say we are in
range but on 2025 the query converts the dates to 2025 2026
so you no longer are on range until the next 12-31

### What should be taken into account

- The activator can repeat every 1,2,3... years
- The activator can start and end on different years
- There can be activators that are overlapping themselves

We should therefore check every year since the activator was created, and check
if the current date is in the range of the start and end date of the activator

We'll be using WITH RECURSIVE to generate the years

```
WITH RECURSIVE pseudo-entity-name(column-names) AS (
    Initial-SELECT
UNION ALL
    Recursive-SELECT using pseudo-entity-name
)
Outer-SELECT using pseudo-entity-name
```

our initial select will be the current year, and the recursive select will be the current year - 1
until we reach the start date year of the activator

```
WITH RECURSIVE executed(year,done) AS (
    SELECT strftime('%Y','now'), tbd
UNION ALL
    SELECT year-1 year, tbd done from executed where year > strftime('%Y',activator.start,'unixepoch')
)
SELECT * from executed
```