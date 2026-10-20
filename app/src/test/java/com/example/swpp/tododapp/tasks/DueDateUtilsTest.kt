package com.example.swpp.tododapp.tasks

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

/**
 * Tests for [dueDateLabel], written from the post-conditions in specs/DueDateUtils.spec.md.
 */
class DueDateUtilsTest {

    // Success cases

    @Test
    fun dueDateLabel_activeTaskDueInThreeDays_returnsDMinus3() {
        // GIVEN - An active task due 3 days after today
        val today = LocalDate.of(2026, 10, 15)
        val dueDate = LocalDate.of(2026, 10, 18)

        // WHEN - Build the label
        val label = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN - The label is "D-3"
        assertEquals("D-3", label)
    }

    @Test
    fun dueDateLabel_activeTaskDueToday_returnsDDay() {
        // GIVEN - An active task due today
        val today = LocalDate.of(2026, 10, 15)
        val dueDate = LocalDate.of(2026, 10, 15)

        // WHEN - Build the label
        val label = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN - The label is "D-Day"
        assertEquals("D-Day", label)
    }

    @Test
    fun dueDateLabel_activeTaskOverdueByThreeDays_returnsDPlus3() {
        // GIVEN - An active task that was due 3 days before today
        val today = LocalDate.of(2026, 10, 15)
        val dueDate = LocalDate.of(2026, 10, 12)

        // WHEN - Build the label
        val label = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN - The label is "D+3"
        assertEquals("D+3", label)
    }

    @Test
    fun dueDateLabel_activeTaskDueMoreThanAMonthAhead_returnsDMinus71() {
        // GIVEN - An active task due more than two months after today
        val today = LocalDate.of(2026, 10, 15)
        val dueDate = LocalDate.of(2026, 12, 25)

        // WHEN - Build the label
        val label = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN - N counts every calendar day in between
        assertEquals("D-71", label)
    }

    @Test
    fun dueDateLabel_activeTaskOverdueByMoreThanAMonth_returnsDPlus103() {
        // GIVEN - An active task that was due more than three months before today
        val today = LocalDate.of(2026, 10, 15)
        val dueDate = LocalDate.of(2026, 7, 4)

        // WHEN - Build the label
        val label = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN - N counts every calendar day in between
        assertEquals("D+103", label)
    }

    @Test
    fun dueDateLabel_activeTaskWithoutDueDate_returnsEmpty() {
        // GIVEN - An active task with no due date
        val today = LocalDate.of(2026, 10, 15)
        val dueDate: LocalDate? = null

        // WHEN - Build the label
        val label = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN - Nothing is shown
        assertEquals("", label)
    }

    @Test
    fun dueDateLabel_completedTaskWithFutureDueDate_returnsClear() {
        // GIVEN - A completed task due 3 days after today
        val today = LocalDate.of(2026, 10, 15)
        val dueDate = LocalDate.of(2026, 10, 18)

        // WHEN - Build the label
        val label = dueDateLabel(dueDate, today, isCompleted = true)

        // THEN - The label is "CLEAR"
        assertEquals("CLEAR", label)
    }

    @Test
    fun dueDateLabel_completedTaskWithPastDueDate_returnsClear() {
        // GIVEN - A completed task that was due 3 days before today
        val today = LocalDate.of(2026, 10, 15)
        val dueDate = LocalDate.of(2026, 10, 12)

        // WHEN - Build the label
        val label = dueDateLabel(dueDate, today, isCompleted = true)

        // THEN - The label is "CLEAR", not an overdue label
        assertEquals("CLEAR", label)
    }

    @Test
    fun dueDateLabel_completedTaskWithoutDueDate_returnsClear() {
        // GIVEN - A completed task with no due date
        val today = LocalDate.of(2026, 10, 15)
        val dueDate: LocalDate? = null

        // WHEN - Build the label
        val label = dueDateLabel(dueDate, today, isCompleted = true)

        // THEN - The label is "CLEAR"
        assertEquals("CLEAR", label)
    }

    // Edge cases: due-today boundary

    @Test
    fun dueDateLabel_dueTodayBoundaryDayBefore_returnsDPlus1() {
        // GIVEN - An active task due the day before today
        val today = LocalDate.of(2026, 6, 10)
        val dueDate = LocalDate.of(2026, 6, 9)

        // WHEN - Build the label
        val label = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN - The label is "D+1"
        assertEquals("D+1", label)
    }

    @Test
    fun dueDateLabel_dueTodayBoundaryAtBoundary_returnsDDay() {
        // GIVEN - An active task due today
        val today = LocalDate.of(2026, 6, 10)
        val dueDate = LocalDate.of(2026, 6, 10)

        // WHEN - Build the label
        val label = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN - The label is "D-Day"
        assertEquals("D-Day", label)
    }

    @Test
    fun dueDateLabel_dueTodayBoundaryDayAfter_returnsDMinus1() {
        // GIVEN - An active task due the day after today
        val today = LocalDate.of(2026, 6, 10)
        val dueDate = LocalDate.of(2026, 6, 11)

        // WHEN - Build the label
        val label = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN - The label is "D-1"
        assertEquals("D-1", label)
    }

    @Test
    fun dueDateLabel_completedTaskDueToday_returnsClear() {
        // GIVEN - A completed task due today
        val today = LocalDate.of(2026, 10, 15)
        val dueDate = LocalDate.of(2026, 10, 15)

        // WHEN - Build the label
        val label = dueDateLabel(dueDate, today, isCompleted = true)

        // THEN - The label is "CLEAR", not "D-Day"
        assertEquals("CLEAR", label)
    }

    // Edge cases: month end

    @Test
    fun dueDateLabel_monthEndDayBefore_returnsDPlus1() {
        // GIVEN - Today is the last day of October and the task was due the day before
        val today = LocalDate.of(2026, 10, 31)
        val dueDate = LocalDate.of(2026, 10, 30)

        // WHEN - Build the label
        val label = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN - The label is "D+1"
        assertEquals("D+1", label)
    }

    @Test
    fun dueDateLabel_monthEndAtBoundary_returnsDDay() {
        // GIVEN - Today and the due date are both the last day of October
        val today = LocalDate.of(2026, 10, 31)
        val dueDate = LocalDate.of(2026, 10, 31)

        // WHEN - Build the label
        val label = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN - The label is "D-Day"
        assertEquals("D-Day", label)
    }

    @Test
    fun dueDateLabel_monthEndDayAfter_returnsDMinus1() {
        // GIVEN - Today is the last day of October and the task is due November 1
        val today = LocalDate.of(2026, 10, 31)
        val dueDate = LocalDate.of(2026, 11, 1)

        // WHEN - Build the label
        val label = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN - The label is "D-1"
        assertEquals("D-1", label)
    }

    // Edge cases: year end

    @Test
    fun dueDateLabel_yearEndDayBefore_returnsDPlus1() {
        // GIVEN - Today is December 31 and the task was due December 30
        val today = LocalDate.of(2026, 12, 31)
        val dueDate = LocalDate.of(2026, 12, 30)

        // WHEN - Build the label
        val label = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN - The label is "D+1"
        assertEquals("D+1", label)
    }

    @Test
    fun dueDateLabel_yearEndAtBoundary_returnsDDay() {
        // GIVEN - Today and the due date are both December 31
        val today = LocalDate.of(2026, 12, 31)
        val dueDate = LocalDate.of(2026, 12, 31)

        // WHEN - Build the label
        val label = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN - The label is "D-Day"
        assertEquals("D-Day", label)
    }

    @Test
    fun dueDateLabel_yearEndDayAfter_returnsDMinus1() {
        // GIVEN - Today is December 31 and the task is due January 1 of the next year
        val today = LocalDate.of(2026, 12, 31)
        val dueDate = LocalDate.of(2027, 1, 1)

        // WHEN - Build the label
        val label = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN - The label is "D-1"
        assertEquals("D-1", label)
    }

    // Edge cases: leap day

    @Test
    fun dueDateLabel_leapDayDayBefore_returnsDPlus1() {
        // GIVEN - Today is February 29 and the task was due February 28
        val today = LocalDate.of(2028, 2, 29)
        val dueDate = LocalDate.of(2028, 2, 28)

        // WHEN - Build the label
        val label = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN - The label is "D+1"
        assertEquals("D+1", label)
    }

    @Test
    fun dueDateLabel_leapDayAtBoundary_returnsDDay() {
        // GIVEN - Today and the due date are both February 29
        val today = LocalDate.of(2028, 2, 29)
        val dueDate = LocalDate.of(2028, 2, 29)

        // WHEN - Build the label
        val label = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN - The label is "D-Day"
        assertEquals("D-Day", label)
    }

    @Test
    fun dueDateLabel_leapDayDayAfter_returnsDMinus1() {
        // GIVEN - Today is February 29 and the task is due March 1
        val today = LocalDate.of(2028, 2, 29)
        val dueDate = LocalDate.of(2028, 3, 1)

        // WHEN - Build the label
        val label = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN - The label is "D-1"
        assertEquals("D-1", label)
    }

    @Test
    fun dueDateLabel_spanAcrossLeapDay_returnsDMinus2() {
        // GIVEN - Today is February 28 of a leap year and the task is due March 1
        val today = LocalDate.of(2028, 2, 28)
        val dueDate = LocalDate.of(2028, 3, 1)

        // WHEN - Build the label
        val label = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN - February 29 is counted as a day
        assertEquals("D-2", label)
    }

    @Test
    fun dueDateLabel_sameSpanInNonLeapYear_returnsDMinus1() {
        // GIVEN - Today is February 28 of a non-leap year and the task is due March 1
        val today = LocalDate.of(2027, 2, 28)
        val dueDate = LocalDate.of(2027, 3, 1)

        // WHEN - Build the label
        val label = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN - There is no February 29 to count
        assertEquals("D-1", label)
    }

    // Edge cases: one whole month ahead

    @Test
    fun dueDateLabel_oneMonthAheadOneDayShort_returnsDMinus30() {
        // GIVEN - An active task due one day before the same day-of-month next month
        val today = LocalDate.of(2026, 10, 15)
        val dueDate = LocalDate.of(2026, 11, 14)

        // WHEN - Build the label
        val label = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN - The label is "D-30"
        assertEquals("D-30", label)
    }

    @Test
    fun dueDateLabel_oneMonthAheadAtBoundary_returnsDMinus31() {
        // GIVEN - An active task due on the same day-of-month next month
        val today = LocalDate.of(2026, 10, 15)
        val dueDate = LocalDate.of(2026, 11, 15)

        // WHEN - Build the label
        val label = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN - The label is "D-31"
        assertEquals("D-31", label)
    }

    @Test
    fun dueDateLabel_oneMonthAheadOneDayOver_returnsDMinus32() {
        // GIVEN - An active task due one day after the same day-of-month next month
        val today = LocalDate.of(2026, 10, 15)
        val dueDate = LocalDate.of(2026, 11, 16)

        // WHEN - Build the label
        val label = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN - The label is "D-32"
        assertEquals("D-32", label)
    }

    // Edge cases: one whole month overdue

    @Test
    fun dueDateLabel_oneMonthOverdueOneDayShort_returnsDPlus29() {
        // GIVEN - An active task that was due one day after the same day-of-month last month
        val today = LocalDate.of(2026, 10, 15)
        val dueDate = LocalDate.of(2026, 9, 16)

        // WHEN - Build the label
        val label = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN - The label is "D+29"
        assertEquals("D+29", label)
    }

    @Test
    fun dueDateLabel_oneMonthOverdueAtBoundary_returnsDPlus30() {
        // GIVEN - An active task that was due on the same day-of-month last month
        val today = LocalDate.of(2026, 10, 15)
        val dueDate = LocalDate.of(2026, 9, 15)

        // WHEN - Build the label
        val label = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN - The label is "D+30"
        assertEquals("D+30", label)
    }

    @Test
    fun dueDateLabel_oneMonthOverdueOneDayOver_returnsDPlus31() {
        // GIVEN - An active task that was due one day before the same day-of-month last month
        val today = LocalDate.of(2026, 10, 15)
        val dueDate = LocalDate.of(2026, 9, 14)

        // WHEN - Build the label
        val label = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN - The label is "D+31"
        assertEquals("D+31", label)
    }

    // Edge cases: one whole year ahead

    @Test
    fun dueDateLabel_oneYearAheadOneDayShort_returnsDMinus364() {
        // GIVEN - An active task due one day before the same date next year
        val today = LocalDate.of(2026, 10, 15)
        val dueDate = LocalDate.of(2027, 10, 14)

        // WHEN - Build the label
        val label = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN - The label is "D-364"
        assertEquals("D-364", label)
    }

    @Test
    fun dueDateLabel_oneYearAheadAtBoundary_returnsDMinus365() {
        // GIVEN - An active task due on the same date next year
        val today = LocalDate.of(2026, 10, 15)
        val dueDate = LocalDate.of(2027, 10, 15)

        // WHEN - Build the label
        val label = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN - The label is "D-365"
        assertEquals("D-365", label)
    }

    @Test
    fun dueDateLabel_oneYearAheadOneDayOver_returnsDMinus366() {
        // GIVEN - An active task due one day after the same date next year
        val today = LocalDate.of(2026, 10, 15)
        val dueDate = LocalDate.of(2027, 10, 16)

        // WHEN - Build the label
        val label = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN - The label is "D-366"
        assertEquals("D-366", label)
    }

    // Edge cases: one whole year overdue

    @Test
    fun dueDateLabel_oneYearOverdueOneDayShort_returnsDPlus364() {
        // GIVEN - An active task that was due one day after the same date last year
        val today = LocalDate.of(2026, 10, 15)
        val dueDate = LocalDate.of(2025, 10, 16)

        // WHEN - Build the label
        val label = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN - The label is "D+364"
        assertEquals("D+364", label)
    }

    @Test
    fun dueDateLabel_oneYearOverdueAtBoundary_returnsDPlus365() {
        // GIVEN - An active task that was due on the same date last year
        val today = LocalDate.of(2026, 10, 15)
        val dueDate = LocalDate.of(2025, 10, 15)

        // WHEN - Build the label
        val label = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN - The label is "D+365"
        assertEquals("D+365", label)
    }

    @Test
    fun dueDateLabel_oneYearOverdueOneDayOver_returnsDPlus366() {
        // GIVEN - An active task that was due one day before the same date last year
        val today = LocalDate.of(2026, 10, 15)
        val dueDate = LocalDate.of(2025, 10, 14)

        // WHEN - Build the label
        val label = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN - The label is "D+366"
        assertEquals("D+366", label)
    }
}
