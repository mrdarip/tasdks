# Tasdks

Don't ever forget any steps of a task, neither underestimate its duration

## Features

- Organise and simplify your tasks
- Remember you overdue tasks
- Predict duration of tasks
- Flatern tasks loads between days
- Provide alerts, encouraging the user to finish their tasks

## Based on

|              | Description                                                                                                | Pros                                                                                            | Cons                                                                                                                                                                                    |
|--------------|------------------------------------------------------------------------------------------------------------|-------------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Goblin tasks | It breaks downs a tasks in simpler tasks                                                                   | facilitates getting tasks done by providing a reward system when checking tasks more frequently | AI based, doesn't take into account real details for successfully completing a task, like getting a towel before getting a shower and doesn't provide an unified task convention system |
| Pomodoro     | tasks are distributed in 25 min work - 5 min rest periods, the 3º cycle, a 15 min rest period instead of 5 |                                                                                                 |                                                                                                                                                                                         |
| FlowTime     |                                                                                                            |                                                                                                 |                                                                                                                                                                                         |
|              |                                                                                                            |                                                                                                 |                                                                                                                                                                                         |

## Elements used by the app

The app uses 2 elements

- Tasks:
  - There are two types of tasks:
    - Group tasks: tasks that has subtasks
    - Final task: tasks which the user will be actually be asked to do, can't have subtasks, as it
      would be a group
- Activators:
  - Element that allows defining when should a task be done
  - There exists two types of activators
    - Date activators:   
      For tasks that must be executed in exact dates, like birthdays, monthly payments, etc.
    - Time activators:  
      For tasks that must be executed in relative date ranges, like daily or weekly

## Creating tasks and activators tips

- A task should be highly reusable, so it should be generic and modular, so it can be used in
  different contexts.
  Is recommended to put the not specific steps in a brother task
  - for example, if you have `shave` as a subtask of `take a shower`, you should create the
    task `shower and shave`, with the subtasks `take a shower` and `shave`
- A final task should only ask the user to do ONE task, else it should be a group, whose subtasks are those tasks independently
  - for example, the final task `shower and dry`, should be divided into 3 tasks, a
    parent `take a shower`, and its two children, `shower` and `dry`
- If a final task doesn't specify in a clear way, then it should be a group with subtasks of the task making it clearer
- If you always forget a step on a task you should add that step as a task, with the task it belongs to as its parent