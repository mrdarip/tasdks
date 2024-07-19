# Problems and their solutions during the development

## SQLITE timediff is not working??? How to fix getPending() YEAR query

| attempted                                                                                                                                                                                                                                                                                                          | result                                           |
|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|--------------------------------------------------|
| timediff(dateA, dateB)                                                                                                                                                                                                                                                                                             | it has magically been removed from sqlite(?      |
| repetitionUnit = 'YEARS' AND datetime(     activators.`end`, 'unixepoch', (substr(timediff('now',datetime(activators.`end`, 'unixepoch')),1,5)+1)\|\| ' years',     '-'\|\|      datetime( activators.start, 'unixepoch', (substr(timediff('now',datetime( activators.start, 'unixepoch')),1,5)+0)\|\| ' years') ) | still timediff has been removed                  |
| select dateTime(1718841600,'unixepoch', '+2024-06-20 00:00:00')                                                                                                                                                                                                                                                    | even is listed in the official docs, not working |

### Using strftime instead of timediff

Having 1-2-34 as start and 2-3-34 as end, lets check if we're in range in this year

```roomsql
select dateTime('2034-02-01 00:00', '-' || abs(strftime('%Y','now') - strftime('%Y','2034-02-01 00:00')) || ' years'),
dateTime('2034-02-02 00:00', '-' || abs(strftime('%Y','now') - strftime('%Y','2034-02-01 00:00')) || ' years')
```

This query returns the dates on current year, lets try now comparing with current date

```roomsql
select dateTime('now') > dateTime('2034-02-01 00:00', '-' || abs(strftime('%Y','now') - strftime('%Y','
2034-02-01 00:00')) || ' years') AND
dateTime('now')  < dateTime('2034-02-02 00:00', '-' || abs(strftime('%Y','now') - strftime('%Y','
2034-02-01 00:00')) || ' years')
```

This query returns if we are in the range of 2034-02-01 00:00 and 2034-02-02 00:00

### The query fails when start and end dates are on different years

If you put 2000-12-31 as start and 2001-12-31 as end date, the query will fail, the query will
convert that to 2024 and 2025 on 2024 so if we were on 31 of december the query would say we are in
range but on 2025 the query converts the dates to 2025 2026
so you no longer are on range until the next 12-31

### Fixing this is slowing down the development too much, lets write some simple rules and completely fix the query later

- Year-repeating activators are limited to be executed in max 1 year until the activator is
  overdue (activator.end - activator.start <= 1 year)
- Overdue year-repeating activators aren't overdue if now > next year's from overdue activator's
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
C)

### This still is not working, lets make some test to debug

```roomsql
select (
dateTime('now') > dateTime(activators.start,'unixepoch', '-' || abs(strftime('%Y','now') - strftime('%Y',activators.start,'unixepoch')) || ' years') AND
dateTime('now') < dateTime(activators.`end`,'unixepoch', '-' || abs(strftime('%Y','now') - strftime('%Y',activators.start,'unixepoch')) || ' years') OR
dateTime('now') < dateTime(activators.`end`,'unixepoch', '-' || abs(strftime('%Y','now', '-1 years') - strftime('%Y',activators.start,'unixepoch')) || ' years')
) AND
(
SELECT COUNT(*) FROM executions WHERE
activators.activatorId = executions.activatorId AND
dateTime(executions.`end`,'unixepoch') > dateTime(activators.start,'unixepoch', '-' || abs(strftime('%Y','now') - strftime('%Y',activators.start,'unixepoch')) || ' years') AND
dateTime(executions.`end`,'unixepoch') < dateTime(activators.`end`,'unixepoch', '-' || abs(strftime('%Y','now') - strftime('%Y',activators.start,'unixepoch')) || ' years') OR
dateTime(executions.`end`,'unixepoch') < dateTime(activators.`end`,'unixepoch', '-' || abs(strftime('%Y','now', '-1 years') - strftime('%Y',activators.start,'unixepoch')) || ' years')
) = 0
```

We have to debug each parameter on the query, lets make a table for just the first part of the query

```roomsql
/*Full query:*/
select
dateTime('now') > dateTime(activators.start,'unixepoch', '-' || abs(strftime('%Y','now') - strftime('%Y',activators.start,'unixepoch')) || ' years') AND
dateTime('now') < dateTime(activators.`end`,'unixepoch', '-' || abs(strftime('%Y','now') - strftime('%Y',activators.start,'unixepoch')) || ' years') OR
dateTime('now') < dateTime(activators.`end`,'unixepoch', '-' || abs(strftime('%Y','now', '-1 years') - strftime('%Y',activators.start,'unixepoch')) || ' years');
```

```roomsql
/*A:*/
select
dateTime('now') > dateTime(activators.start,'unixepoch', '-' || abs(strftime('%Y','now') - strftime('%Y',activators.start,'unixepoch')) || ' years')
```

```roomsql
/*B:*/
select
dateTime('now') < dateTime(activators.`end`,'unixepoch', '-' || abs(strftime('%Y','now') - strftime('%Y',activators.start,'unixepoch')) || ' years') 
```

```roomsql
/*C:*/
select
dateTime('now') < dateTime(activators.`end`,'unixepoch', '-' || abs(strftime('%Y','now', '-1 years') - strftime('%Y',activators.start,'unixepoch')) || ' years')
```

| activatorId | Description                                                                                        | Expected full query value | now       | activator.start | activator.end | A | B | C |
|-------------|----------------------------------------------------------------------------------------------------|---------------------------|-----------|-----------------|---------------|---|---|---|
| 0           | range a month before today (1 month duration)                                                      | 0                         | 15/7/2024 | 1/6/2024        | 30/6/2024     | 1 | 0 | 0 |
| 1           | range a month and years before today (1 month duration)                                            | 0                         | 15/7/2024 | 1/6/2020        | 30/6/2020     | 1 | 0 | 0 |
| 2           | range a month after today (1 month duration)                                                       | 0                         | 15/7/2024 | 1/8/2024        | 30/8/2024     | 0 | 1 | 0 |
| 3           | range a month and years after today (1 month duration)                                             | 0                         | 15/7/2024 | 1/8/2020        | 30/8/2020     | 0 | 1 | 0 |
| 4           | range starting past month and ending in 2 days from now (<1 month duration)                        | 1                         | 15/7/2024 | 25/6/2024       | 17/7/2024     | 1 | 1 | 0 |
| 5           | range starting past month and ending in 2 days before now (<1 month duration)                      | 0                         | 15/7/2024 | 25/6/2024       | 13/7/2024     | 1 | 0 | 0 |
| 6           | range starting 2 days before now and ending next month (<1 month duration)                         | 1                         | 15/7/2024 | 13/7/2024       | 5/8/2024      | 1 | 1 | 0 |
| 7           | range starting 2 days after now and ending next month (<1 month duration)                          | 0                         | 15/7/2024 | 17/7/2024       | 5/8/2024      | 0 | 1 | 0 |
| 8           | range starting past month in x years and ending in 2 days + x years from now (<1 month duration)   | 1                         | 15/7/2024 | 25/6/2020       | 17/7/2020     | 1 | 1 | 0 |
| 9           | range starting past month in x years and ending in 2 days + x years before now (<1 month duration) | 0                         | 15/7/2024 | 25/6/2020       | 13/7/2020     | 1 | 0 | 0 |
| 10          | range starting 2 days + x years before now and ending next month + x years (<1 month duration)     | 1                         | 15/7/2024 | 13/7/2020       | 5/8/2020      | 1 | 1 | 0 |
| 11          | range starting in last year's december, ending 1 month before now                                  | 0                         | 15/7/2024 | 30/12/2023      | 15/6/2024     | 0 | 1 | 0 |
| 12          | range starting in last year's december, ending 1 mont after now                                    | 1                         | 15/7/2024 | 30/12/2023      | 15/8/2024     | 0 | 1 | 1 |
| 13          | range starting -2 days -1 year before now, ending -1 day before now                                | 0                         | 15/7/2024 | 13/7/2023       | 14/7/2024     | 1 | 1 | 0 |

#### Lets make some sqlite to test it

```roomsql
    CREATE TABLE activators(
        activatorId INTEGER PRIMARY KEY,
        start INTEGER NOT NULL,
        `end` INTEGER NOT NULL
    );
```

```roomsql
INSERT INTO activators VALUES
    (0, strftime('%s','2024-06-01 00:00:00'), strftime('%s','2024-06-30 00:00:00')),
    (1, strftime('%s','2020-06-01 00:00:00'), strftime('%s','2020-06-30 00:00:00')),
    (2, strftime('%s','2024-08-01 00:00:00'), strftime('%s','2024-08-30 00:00:00')),
    (3, strftime('%s','2020-08-01 00:00:00'), strftime('%s','2020-08-30 00:00:00')),
    (4, strftime('%s','2024-06-25 00:00:00'), strftime('%s','2024-07-17 00:00:00')),
    (5, strftime('%s','2024-06-25 00:00:00'), strftime('%s','2024-07-13 00:00:00')),
    (6, strftime('%s','2024-07-13 00:00:00'), strftime('%s','2024-08-05 00:00:00')),
    (7, strftime('%s','2024-07-17 00:00:00'), strftime('%s','2024-08-05 00:00:00')),
    (8, strftime('%s','2020-06-25 00:00:00'), strftime('%s','2020-07-17 00:00:00')),
    (9, strftime('%s','2020-06-25 00:00:00'), strftime('%s','2020-07-13 00:00:00')),
    (10, strftime('%s','2020-07-13 00:00:00'), strftime('%s','2020-08-05 00:00:00')),
    (11, strftime('%s','2023-12-30 00:00:00'), strftime('%s','2024-06-15 00:00:00')),
    (12, strftime('%s','2023-12-30 00:00:00'), strftime('%s','2024-08-15 00:00:00')),
    (13, strftime('%s','2023-07-13 00:00:00'), strftime('%s','2024-07-14 00:00:00'));
```

```roomsql   
SELECT 
activatorid
as ID,
(
    (
        dateTime('now') > dateTime(activators.start,'unixepoch',printf('%+d',abs(strftime('%Y','now') - strftime('%Y',activators.start,'unixepoch'))) || ' years') AND
        dateTime('now') < dateTime(activators.`end`,'unixepoch', printf('%+d',abs(strftime('%Y','now') - strftime('%Y',activators.start,'unixepoch'))) || ' years')
    ) OR
    (
        dateTime('now') < dateTime(activators.`end`,'unixepoch', printf('%+d',strftime('%Y','now', '-1 years') - strftime('%Y',activators.start,'unixepoch')) || ' years') 
    )
)
as fullQuery,
dateTime('now') > dateTime(activators.start,'unixepoch', printf('%+d', abs(strftime('%Y','now') - strftime('%Y',activators.start,'unixepoch'))) || ' years') 
as A,
dateTime('now') < dateTime(activators.`end`,'unixepoch',printf('%+d',abs(strftime('%Y','now') - strftime('%Y',activators.start,'unixepoch'))) || ' years') 
as B,
dateTime('now') < dateTime(activators.`end`,'unixepoch', printf('%+d',strftime('%Y','now', '-1 years') - strftime('%Y',activators.start,'unixepoch')) || ' years') 
AS C,
dateTime(activators.start,'unixepoch', printf('%+d',abs(strftime('%Y','now') - strftime('%Y',activators.start,'unixepoch'))) || ' years') as thisYearStart,
dateTime(activators.`end`,'unixepoch', printf('%+d', abs(strftime('%Y','now') - strftime('%Y',activators.start,'unixepoch'))) || ' years') as thisYearEnd,
dateTime(activators.`end`,'unixepoch', printf('%+d',strftime('%Y','now', '-1 years') - strftime('%Y',activators.start,'unixepoch')) || ' years') as lastYearStartEnd
FROM activators;
```

Then... It's finally fixed 😎🥨🐱‍💻
(it's not, the execution checking is missing...)

## Implement overdue year-repeating query

A year-repeating activator is overdue if:

- if now > this year's end date (A)
    - AND no task has been done since this year's start date (B)
- else
    - if now > last year's end date (C)
        - AND now < this year's start date (D)
            - AND no task has been done since last year's start date (E)

So our query will be like

```roomsql
SELECT (A AND B) OR (C AND D AND E) 
```

### Lets define each letter

- A
    ```roomsql
    SELECT dateTime('now') > dateTime(activators.`end`,'unixepoch', printf('%+d',abs(strftime('%Y','now') - strftime('%Y',activators.start,'unixepoch'))) || ' years')
    ```
- B
    ```roomsql
    SELECT (SELECT COUNT(*) FROM executions WHERE
    activators.activatorId = executions.activatorId AND
    dateTime(executions.`end`,'unixepoch') > dateTime(activators.start,'unixepoch', printf('%+d',abs(strftime('%Y','now') - strftime('%Y',activators.start,'unixepoch'))) || ' years')
    ) = 0
    ```
- C
    ```roomsql
    SELECT dateTime('now') > dateTime(activators.`end`,'unixepoch', printf('%+d',strftime('%Y','now', '-1 years') - strftime('%Y',activators.start,'unixepoch')) || ' years')
    ```
- D
    ```roomsql
    SELECT dateTime('now') < dateTime(activators.start,'unixepoch', printf('%+d',abs(strftime('%Y','now') - strftime('%Y',activators.start,'unixepoch'))) || ' years')
    ```
- E
    ```roomsql
    SELECT (SELECT COUNT(*) FROM executions WHERE
    activators.activatorId = executions.activatorId AND
    dateTime(executions.`end`,'unixepoch') > dateTime(activators.start,'unixepoch', printf('%+d',strftime('%Y','now', '-1 years') - strftime('%Y',activators.start,'unixepoch')) || ' years')
    ) = 0
    ```

### Final query

```roomsql
SELECT 
(
    dateTime('now') > dateTime(activators.`end`,'unixepoch', printf('%+d',abs(strftime('%Y','now') - strftime('%Y',activators.start,'unixepoch'))) || ' years')
    AND
    (
        SELECT COUNT(*) FROM executions WHERE
        activators.activatorId = executions.activatorId AND
        dateTime(executions.`end`,'unixepoch') > dateTime(activators.start,'unixepoch', printf('%+d',abs(strftime('%Y','now') - strftime('%Y',activators.start,'unixepoch'))) || ' years')
    ) = 0
)
OR
(
    dateTime('now') > dateTime(activators.`end`,'unixepoch', printf('%+d',strftime('%Y','now', '-1 years') - strftime('%Y',activators.start,'unixepoch')) || ' years') AND
    dateTime('now') < dateTime(activators.start,'unixepoch', printf('%+d',abs(strftime('%Y','now') - strftime('%Y',activators.start,'unixepoch'))) || ' years') AND
    (
        SELECT COUNT(*) FROM executions WHERE
        activators.activatorId = executions.activatorId AND
        dateTime(executions.`end`,'unixepoch') > dateTime(activators.start,'unixepoch', printf('%+d',strftime('%Y','now', '-1 years') - strftime('%Y',activators.start,'unixepoch')) || ' years')
    ) = 0
)
```

## Play Activator screen need to be reworked

### UI

The play activator screen is too simple, it should have a more complex UI, like rendering the tree
graph

```mermaid
flowchart LR
    A["hi mom"] --> B["this is mermaid!"]
```

- ActivatorCardRow's cards should display task's icon

### UX

- Maybe approximating it to something like spotify UI would give user a familiar experience
- Main screen should be less monotone and more distinct
  - Design a new page layout

- Playlists need to be implemented
    - Allow skipping to next subtask
  - as specified by task.isPlaylist
- ~~Allow pausing and resuming~~
  - If you started a task, you must finish it
- Allow doing tasks in the background like in spotify mini player
- Allow setting a task as a favorite
    - This one sounds like a good idea, but it's not necessary
- Allow doing tasks while you're in a waiting task
  - As specified by task.allowParallelTasks and task.waitTime

### Code

- Background-running code should be moved to its viewmodel
  - Prioritize starting with play activator screen


