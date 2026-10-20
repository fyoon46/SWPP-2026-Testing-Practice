# Spec: `TasksListBindings.kt`

> Post-conditions are the test oracle: write the expected behavior from intent, not from the code.
> Tag each post-condition [Unit] if its test runs on the JVM; otherwise tag it [Integration].
> End each valid case with `Tested by <test>` if an existing test checks it, or `Tip:` describing the test to write.

## `@BindingAdapter("app:dueDateLabel") fun setDueDateLabel(textView: TextView, task: Task?)`

**Intent**: Shows the D-day label of a task row in the task list. The label view shows the task's label (`"D-N"`, `"D-Day"`, `"D+N"` or `"CLEAR"`, counted from the device's current date) and is hidden when there is nothing to show: an active task without a due date, or no task at all.

**Pre-conditions**
- Called on the main thread, as data binding does when it binds a `task_item` row.
- `textView` is the row's `due_date_text` view. The task list reuses rows, so the view may still hold the text and visibility it had for an earlier task.
- `task` may be `null` (data binding passes `null` before the row's task is set).
- "Today" is the device's current date in its default time zone (`LocalDate.now()`), so each Tip builds its due dates from `LocalDate.now()` read in the test.

**Post-conditions**
- Success cases
  - Active task due in N days: the view shows `"D-N"` and is visible. `[Integration]`
    - Tip: Given a new TextView(context) and Task(title = "Buy milk", isCompleted = false, dueDate = LocalDate.now().plusDays(3)) / When setDueDateLabel(textView, task) is called / Then textView.text is "D-3" and textView.visibility is View.VISIBLE
  - Active task due today: the view shows `"D-Day"` and is visible. `[Integration]`
    - Tip: Given a new TextView(context) and Task(title = "Buy milk", isCompleted = false, dueDate = LocalDate.now()) / When setDueDateLabel(textView, task) is called / Then textView.text is "D-Day" and textView.visibility is View.VISIBLE
  - Active task overdue by N days: the view shows `"D+N"` and is visible. `[Integration]`
    - Tip: Given a new TextView(context) and Task(title = "Buy milk", isCompleted = false, dueDate = LocalDate.now().minusDays(3)) / When setDueDateLabel(textView, task) is called / Then textView.text is "D+3" and textView.visibility is View.VISIBLE
  - Completed task with a due date: the view shows `"CLEAR"` and is visible. `[Integration]`
    - Tip: Given a new TextView(context) and Task(title = "Buy milk", isCompleted = true, dueDate = LocalDate.now().plusDays(3)) / When setDueDateLabel(textView, task) is called / Then textView.text is "CLEAR" and textView.visibility is View.VISIBLE
  - Completed task with no due date: the view shows `"CLEAR"` and is visible. `[Integration]`
    - Tip: Given a new TextView(context) and Task(title = "Buy milk", isCompleted = true, dueDate = null) / When setDueDateLabel(textView, task) is called / Then textView.text is "CLEAR" and textView.visibility is View.VISIBLE
  - Active task with no due date (`dueDate` left empty): nothing is shown; the view is hidden. `[Integration]`
    - Tip: Given a new TextView(context) and Task(title = "Buy milk", isCompleted = false, dueDate = null) / When setDueDateLabel(textView, task) is called / Then textView.visibility is View.GONE
  - No task (`task` left empty): nothing is shown; the view is hidden. `[Integration]`
    - Tip: Given a new TextView(context) / When setDueDateLabel(textView, null) is called / Then textView.visibility is View.GONE
- Error cases
  - None. Every `TextView` and every `Task?`, including `null`, is valid.
- Edge cases
  - Due-today boundary, one day before: an active task due yesterday shows `"D+1"`, so "today" is the device's current date. `[Integration]`
    - Tip: Given a new TextView(context) and Task(title = "Buy milk", isCompleted = false, dueDate = LocalDate.now().minusDays(1)) / When setDueDateLabel(textView, task) is called / Then textView.text is "D+1" and textView.visibility is View.VISIBLE
  - Due-today boundary, at the boundary, on a hidden reused view: a view hidden for an earlier task with no due date becomes visible and shows `"D-Day"` for an active task due today. `[Integration]`
    - Tip: Given a new TextView(context) on which setDueDateLabel(textView, Task(title = "Read book", isCompleted = false, dueDate = null)) has run, so it is hidden / When setDueDateLabel(textView, Task(title = "Buy milk", isCompleted = false, dueDate = LocalDate.now())) is called / Then textView.text is "D-Day" and textView.visibility is View.VISIBLE
  - Due-today boundary, one day after: an active task due tomorrow shows `"D-1"`. `[Integration]`
    - Tip: Given a new TextView(context) and Task(title = "Buy milk", isCompleted = false, dueDate = LocalDate.now().plusDays(1)) / When setDueDateLabel(textView, task) is called / Then textView.text is "D-1" and textView.visibility is View.VISIBLE
  - Shown label to no label: a reused view that shows `"D-3"` is hidden when bound to an active task with no due date, so the old label does not stay on screen. `[Integration]`
    - Tip: Given a new TextView(context) on which setDueDateLabel(textView, Task(title = "Buy milk", isCompleted = false, dueDate = LocalDate.now().plusDays(3))) has run, so it shows "D-3" / When setDueDateLabel(textView, Task(title = "Read book", isCompleted = false, dueDate = null)) is called / Then textView.visibility is View.GONE
  - Shown label to no task: a reused view that shows `"D-3"` is hidden when bound to `null`. `[Integration]`
    - Tip: Given a new TextView(context) on which setDueDateLabel(textView, Task(title = "Buy milk", isCompleted = false, dueDate = LocalDate.now().plusDays(3))) has run, so it shows "D-3" / When setDueDateLabel(textView, null) is called / Then textView.visibility is View.GONE
  - Shown label to another label: a reused view that shows `"D-3"` shows `"CLEAR"` once the same task is completed. `[Integration]`
    - Tip: Given a new TextView(context) on which setDueDateLabel(textView, Task(title = "Buy milk", isCompleted = false, dueDate = LocalDate.now().plusDays(3), id = "task1")) has run, so it shows "D-3" / When setDueDateLabel(textView, Task(title = "Buy milk", isCompleted = true, dueDate = LocalDate.now().plusDays(3), id = "task1")) is called / Then textView.text is "CLEAR" and textView.visibility is View.VISIBLE
  - Completion boundary without a due date: a reused view that shows `"CLEAR"` for a completed task with no due date is hidden once the same task is marked active again. `[Integration]`
    - Tip: Given a new TextView(context) on which setDueDateLabel(textView, Task(title = "Buy milk", isCompleted = true, dueDate = null, id = "task1")) has run, so it shows "CLEAR" / When setDueDateLabel(textView, Task(title = "Buy milk", isCompleted = false, dueDate = null, id = "task1")) is called / Then textView.visibility is View.GONE

**Undefined behavior** (optional)
- A call that runs while the device date changes (local midnight passes, or the date or time zone is changed) may count days from either date, so its label may be one day off from the one computed in the test. The function reads the clock itself, so a test cannot pin "today"; run the tests away from midnight.
