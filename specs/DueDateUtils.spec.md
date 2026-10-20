# Spec: `DueDateUtils.kt`

> Post-conditions are the test oracle: write the expected behavior from intent, not from the code.
> Tag each post-condition [Unit] if its test runs on the JVM; otherwise tag it [Integration].
> End each valid case with `Tested by <test>` if an existing test checks it, or `Tip:` describing the test to write.

## `internal fun dueDateLabel(dueDate: LocalDate?, today: LocalDate, isCompleted: Boolean): String`

**Intent**: Builds the D-day label shown next to a task in the task list: `"CLEAR"` for a completed task, `""` (nothing shown) for an active task without a due date, and otherwise `"D-N"` (due in N days), `"D-Day"` (due today) or `"D+N"` (overdue by N days). N is the number of calendar days between `today` and `dueDate`, written as a plain decimal number with no leading zeros.

**Pre-conditions**
- `today` is the user's current local date. The caller passes it in; the function does not read the clock.
- `dueDate` is the task's due date, or `null` when the task has none.
- `isCompleted` is `true` for a completed task and `false` for an active task.
- Any `LocalDate` values are allowed, and `dueDate` may be before, equal to or after `today`.

**Post-conditions**
- Success cases
  - Active task due N days after `today` returns `"D-N"`. `[Unit]`
    - Tip: Given today = 2026-10-15, dueDate = 2026-10-18, isCompleted = false / When dueDateLabel(dueDate, today, isCompleted) is called / Then it returns "D-3"
  - Active task due on `today` returns `"D-Day"`. `[Unit]`
    - Tip: Given today = 2026-10-15, dueDate = 2026-10-15, isCompleted = false / When dueDateLabel(dueDate, today, isCompleted) is called / Then it returns "D-Day"
  - Active task whose due date was N days before `today` returns `"D+N"`. `[Unit]`
    - Tip: Given today = 2026-10-15, dueDate = 2026-10-12, isCompleted = false / When dueDateLabel(dueDate, today, isCompleted) is called / Then it returns "D+3"
  - Active task due more than a month ahead returns `"D-N"` where N counts every calendar day in between, not only the days left over after whole months. `[Unit]`
    - Tip: Given today = 2026-10-15, dueDate = 2026-12-25, isCompleted = false / When dueDateLabel(dueDate, today, isCompleted) is called / Then it returns "D-71"
  - Active task overdue by more than a month returns `"D+N"` where N counts every calendar day in between. `[Unit]`
    - Tip: Given today = 2026-10-15, dueDate = 2026-07-04, isCompleted = false / When dueDateLabel(dueDate, today, isCompleted) is called / Then it returns "D+103"
  - Active task with no due date (`dueDate` left empty) returns `""`. `[Unit]`
    - Tip: Given today = 2026-10-15, dueDate = null, isCompleted = false / When dueDateLabel(dueDate, today, isCompleted) is called / Then it returns ""
  - Completed task with a future due date returns `"CLEAR"`. `[Unit]`
    - Tip: Given today = 2026-10-15, dueDate = 2026-10-18, isCompleted = true / When dueDateLabel(dueDate, today, isCompleted) is called / Then it returns "CLEAR"
  - Completed task with a past due date returns `"CLEAR"`, not an overdue label. `[Unit]`
    - Tip: Given today = 2026-10-15, dueDate = 2026-10-12, isCompleted = true / When dueDateLabel(dueDate, today, isCompleted) is called / Then it returns "CLEAR"
  - Completed task with no due date returns `"CLEAR"`. `[Unit]`
    - Tip: Given today = 2026-10-15, dueDate = null, isCompleted = true / When dueDateLabel(dueDate, today, isCompleted) is called / Then it returns "CLEAR"
- Error cases
  - None. Every combination of inputs is valid.
- Edge cases
  - Due-today boundary, one day before: active task due the day before `today` returns `"D+1"`. `[Unit]`
    - Tip: Given today = 2026-06-10, dueDate = 2026-06-09, isCompleted = false / When dueDateLabel(dueDate, today, isCompleted) is called / Then it returns "D+1"
  - Due-today boundary, at the boundary: active task due on `today` returns `"D-Day"`. `[Unit]`
    - Tip: Given today = 2026-06-10, dueDate = 2026-06-10, isCompleted = false / When dueDateLabel(dueDate, today, isCompleted) is called / Then it returns "D-Day"
  - Due-today boundary, one day after: active task due the day after `today` returns `"D-1"`. `[Unit]`
    - Tip: Given today = 2026-06-10, dueDate = 2026-06-11, isCompleted = false / When dueDateLabel(dueDate, today, isCompleted) is called / Then it returns "D-1"
  - Completion at the due-today boundary: completed task due on `today` returns `"CLEAR"`, not `"D-Day"`. `[Unit]`
    - Tip: Given today = 2026-10-15, dueDate = 2026-10-15, isCompleted = true / When dueDateLabel(dueDate, today, isCompleted) is called / Then it returns "CLEAR"
  - Month end, day before: `today` is the last day of a month and the task was due the day before. `[Unit]`
    - Tip: Given today = 2026-10-31, dueDate = 2026-10-30, isCompleted = false / When dueDateLabel(dueDate, today, isCompleted) is called / Then it returns "D+1"
  - Month end, at the boundary: `today` and the due date are both the last day of a month. `[Unit]`
    - Tip: Given today = 2026-10-31, dueDate = 2026-10-31, isCompleted = false / When dueDateLabel(dueDate, today, isCompleted) is called / Then it returns "D-Day"
  - Month end, day after: `today` is the last day of a month and the task is due on the first day of the next month. `[Unit]`
    - Tip: Given today = 2026-10-31, dueDate = 2026-11-01, isCompleted = false / When dueDateLabel(dueDate, today, isCompleted) is called / Then it returns "D-1"
  - Year end, day before: `today` is December 31 and the task was due December 30. `[Unit]`
    - Tip: Given today = 2026-12-31, dueDate = 2026-12-30, isCompleted = false / When dueDateLabel(dueDate, today, isCompleted) is called / Then it returns "D+1"
  - Year end, at the boundary: `today` and the due date are both December 31. `[Unit]`
    - Tip: Given today = 2026-12-31, dueDate = 2026-12-31, isCompleted = false / When dueDateLabel(dueDate, today, isCompleted) is called / Then it returns "D-Day"
  - Year end, day after: `today` is December 31 and the task is due January 1 of the next year. `[Unit]`
    - Tip: Given today = 2026-12-31, dueDate = 2027-01-01, isCompleted = false / When dueDateLabel(dueDate, today, isCompleted) is called / Then it returns "D-1"
  - Leap day, day before: `today` is February 29 and the task was due February 28. `[Unit]`
    - Tip: Given today = 2028-02-29, dueDate = 2028-02-28, isCompleted = false / When dueDateLabel(dueDate, today, isCompleted) is called / Then it returns "D+1"
  - Leap day, at the boundary: `today` and the due date are both February 29. `[Unit]`
    - Tip: Given today = 2028-02-29, dueDate = 2028-02-29, isCompleted = false / When dueDateLabel(dueDate, today, isCompleted) is called / Then it returns "D-Day"
  - Leap day, day after: `today` is February 29 and the task is due March 1. `[Unit]`
    - Tip: Given today = 2028-02-29, dueDate = 2028-03-01, isCompleted = false / When dueDateLabel(dueDate, today, isCompleted) is called / Then it returns "D-1"
  - Span across a leap day counts February 29 as a day. `[Unit]`
    - Tip: Given today = 2028-02-28, dueDate = 2028-03-01, isCompleted = false / When dueDateLabel(dueDate, today, isCompleted) is called / Then it returns "D-2"
  - Same span in a non-leap year has no February 29 to count. `[Unit]`
    - Tip: Given today = 2027-02-28, dueDate = 2027-03-01, isCompleted = false / When dueDateLabel(dueDate, today, isCompleted) is called / Then it returns "D-1"
  - One whole month ahead, one day short: due one day before the same day-of-month next month. `[Unit]`
    - Tip: Given today = 2026-10-15, dueDate = 2026-11-14, isCompleted = false / When dueDateLabel(dueDate, today, isCompleted) is called / Then it returns "D-30"
  - One whole month ahead, at the boundary: due on the same day-of-month next month. `[Unit]`
    - Tip: Given today = 2026-10-15, dueDate = 2026-11-15, isCompleted = false / When dueDateLabel(dueDate, today, isCompleted) is called / Then it returns "D-31"
  - One whole month ahead, one day over: due one day after the same day-of-month next month. `[Unit]`
    - Tip: Given today = 2026-10-15, dueDate = 2026-11-16, isCompleted = false / When dueDateLabel(dueDate, today, isCompleted) is called / Then it returns "D-32"
  - One whole month overdue, one day short: due one day after the same day-of-month last month. `[Unit]`
    - Tip: Given today = 2026-10-15, dueDate = 2026-09-16, isCompleted = false / When dueDateLabel(dueDate, today, isCompleted) is called / Then it returns "D+29"
  - One whole month overdue, at the boundary: due on the same day-of-month last month. `[Unit]`
    - Tip: Given today = 2026-10-15, dueDate = 2026-09-15, isCompleted = false / When dueDateLabel(dueDate, today, isCompleted) is called / Then it returns "D+30"
  - One whole month overdue, one day over: due one day before the same day-of-month last month. `[Unit]`
    - Tip: Given today = 2026-10-15, dueDate = 2026-09-14, isCompleted = false / When dueDateLabel(dueDate, today, isCompleted) is called / Then it returns "D+31"
  - One whole year ahead, one day short: due one day before the same date next year. `[Unit]`
    - Tip: Given today = 2026-10-15, dueDate = 2027-10-14, isCompleted = false / When dueDateLabel(dueDate, today, isCompleted) is called / Then it returns "D-364"
  - One whole year ahead, at the boundary: due on the same date next year. `[Unit]`
    - Tip: Given today = 2026-10-15, dueDate = 2027-10-15, isCompleted = false / When dueDateLabel(dueDate, today, isCompleted) is called / Then it returns "D-365"
  - One whole year ahead, one day over: due one day after the same date next year. `[Unit]`
    - Tip: Given today = 2026-10-15, dueDate = 2027-10-16, isCompleted = false / When dueDateLabel(dueDate, today, isCompleted) is called / Then it returns "D-366"
  - One whole year overdue, one day short: due one day after the same date last year. `[Unit]`
    - Tip: Given today = 2026-10-15, dueDate = 2025-10-16, isCompleted = false / When dueDateLabel(dueDate, today, isCompleted) is called / Then it returns "D+364"
  - One whole year overdue, at the boundary: due on the same date last year. `[Unit]`
    - Tip: Given today = 2026-10-15, dueDate = 2025-10-15, isCompleted = false / When dueDateLabel(dueDate, today, isCompleted) is called / Then it returns "D+365"
  - One whole year overdue, one day over: due one day before the same date last year. `[Unit]`
    - Tip: Given today = 2026-10-15, dueDate = 2025-10-14, isCompleted = false / When dueDateLabel(dueDate, today, isCompleted) is called / Then it returns "D+366"

**Undefined behavior** (optional)
- None. Every combination of a nullable `dueDate`, a `today` and an `isCompleted` flag has a defined label.
