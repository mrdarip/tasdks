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

### Fixing this is slowing down the development too much, lets write some simple rules and completely fix the query later

- year-repeating activators are limited to be executed in max 1 year until the activator is
  overdue (activator.end - activator.start <= 1 year)
- overdue year-repeating activators aren't overdue if now > next year's from overdue activator's
  start date

### So our fix is

- if now > this year's start (A) and
  - now < this year's start's end (B)
- else
  - if now > last year's start (always true, ignore) (C) and
    - now < last year's start's end (D)

Using Karnaugh:

|       | !C !D | !C D | C D | C !D |
|-------|-------|------|-----|------|
| !A !B | 0     | 1    | 1   | 0    |
| !A B  | 0     | 1    | 1   | 0    |
| A  B  | 1     | 1    | 1   | 1    |
| A  !B | 0     | 1    | 1   | 0    |

S = D + AB

In other words, we are in range if
now > this year's start (A) AND now < this year's start's end (B) OR now < last year's start's end (
D)

