# Tasdks

Don't ever forget any steps of a task, neither underestimate its duration.
Add everything you want to have done, like sweeping th floor or playing the piano! The app will learn from you and distribute all your tasks for every day!

![1724260020987](https://github.com/user-attachments/assets/7ec6455c-e62b-4dbf-9967-c8f6bd8658a7)
![1724260020951](https://github.com/user-attachments/assets/fd3dc649-55c3-4311-acb4-b08100ec1327)


## Building

Clone and build it in Android Studio, as it was made there

## Features

- Organise and simplify your tasks.
- Remember you overdue tasks.
- Predict duration of tasks.
- Flatern tasks loads between days.
- Provide alerts, encouraging the user to finish their pending tasks.

## Based on

|              | Description                                                                                                | Pros                                                                                            | Cons                                                                                                                                                                                                                                                                                                 |
|--------------|------------------------------------------------------------------------------------------------------------|-------------------------------------------------------------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Goblin tasks | It breaks downs a tasks in simpler tasks                                                                   | facilitates getting tasks done by providing a reward system when checking tasks more frequently | AI based, doesn't take into account real details for successfully completing a task, like getting a towel before getting a shower and doesn't provide an unified task convention system                                                                                                              |
| Pomodoro     | tasks are distributed in 25 min work - 5 min rest periods, the 3º cycle, a 15 min rest period instead of 5 | It bears in mind user rests                                                                     | 5 min of rest can feel short for some users, can feel hard to start every period, having a low retetion rate for users "without enought motivation" and if you end a task you'll probably doubt about continuing and skip to the rest period (it's not necessarily a con but written for the record) |
| Pomodoro +   |                                                                                                            |                                                                                                 |                                                                                                                                                                                                                                                                                                      |
|              |                                                                                                            |                                                                                                 |                                                                                                                                                                                                                                                                                                      |
| FlowTime     |                                                                                                            |                                                                                                 |                                                                                                                                                                                                                                                                                                      |
|              |                                                                                                            |                                                                                                 |                                                                                                                                                                                                                                                                                                      |

## Elements used by the app

The app uses 2 elements:

- Tasks:
  - There are two types of tasks:
    - Group tasks: tasks that has subtasks.
    - Final task: tasks which the user will be actually be asked to do, can't have subtasks, as it
      would be a group.
- Activators:
  - Element that allows defining when should a task be done.
  - There exists two types of activators:
    - Date activators:   
      For tasks that must be executed in exact dates, like birthdays, monthly payments, etc.
    - Time activators:  
      For tasks that must be executed in relative date ranges, like daily or weekly.

## Creating tasks and activators tips

- When naming a task it should allow:
  - Recognise what its subtasks are 
  - Be descriptive enought so you can find it, for example, when creating an activator.
  - Be short enought so you can quickly read it when executing an activator.
  - Be decriptive enought so you can know exactly what you have to do without thinking about it.

  You should only use the description field when its scrictly needed, in most cases you might want to create subtasks or improve tasks naming, the execution time is reduced if the user has tu read less.
  It's recommended to use it for describing specific data, such as hard to convert quantities, for example `make rice on the microwave` would have the subtask `put water in the bowl`, but the user may not know how much is "water", so as the task description you may want to put:  
  ```
  1 serving -> 120 ml  
  2 servings -> 330 ml
  3 servings -> 450 ml
  ```
- A task should be highly reusable, so it should be generic and modular, so it can be used in
  different contexts.  
  Is recommended to put the not specific steps in a brother task.
  - For example, if you have `shave` as a subtask of `take a shower`, you should create the
    task `shower and shave`, with two direct subtasks, `take a shower` and `shave`.
- A final task should only ask the user to do ONE task, else it should be a group, whose subtasks are those tasks independently.
  - For example, the final task `shower and dry`, should be divided into 3 tasks, a
    parent `take a shower`, and its two children, `shower` and `dry`
- If a final task doesn't specify in a clear way what has to be done, then it should be a group with subtasks of the task making it clearer.
- If you always forget a step on a task you should add that step as a task, with the task it belongs to as its parent.

## Pending features
- Complete all screens layouts
  - Play activator screen
  - Create activator screen
  - Modify activator screen
  - Main screen
- Create more screens
  - stats screens
- Complete querying overdue and pending date activators
  - Fix year repeating activators so there can be overdue tasks > 1 year old, overlapping year
    repeating activators
- Remove Place and Object entities, maybe Resource too as probably the user should use no resource at all? reject dopamine embrace watching the tasdks execution activator screen
