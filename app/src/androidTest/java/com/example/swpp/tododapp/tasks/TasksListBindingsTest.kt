package com.example.swpp.tododapp.tasks

import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.test.annotation.UiThreadTest
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.example.swpp.tododapp.data.Task
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate

/**
 * Tests for [setDueDateLabel], written from the post-conditions in specs/TasksListBindings.spec.md.
 *
 * The binding adapter reads "today" from the device clock, so each test builds its due dates from
 * [LocalDate.now]. Run these away from local midnight.
 */
@SmallTest
@RunWith(AndroidJUnit4::class)
class TasksListBindingsTest {

    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
    }

    // Success cases

    @Test
    @UiThreadTest
    fun setDueDateLabel_activeTaskDueInThreeDays_showsDMinus3() {
        // GIVEN - A new view and an active task due in 3 days
        val textView = TextView(context)
        val task = Task(title = "Buy milk", isCompleted = false, dueDate = LocalDate.now().plusDays(3))

        // WHEN - Bind the task to the view
        setDueDateLabel(textView, task)

        // THEN - The view shows "D-3" and is visible
        assertEquals("D-3", textView.text.toString())
        assertEquals(View.VISIBLE, textView.visibility)
    }

    @Test
    @UiThreadTest
    fun setDueDateLabel_activeTaskDueToday_showsDDay() {
        // GIVEN - A new view and an active task due today
        val textView = TextView(context)
        val task = Task(title = "Buy milk", isCompleted = false, dueDate = LocalDate.now())

        // WHEN - Bind the task to the view
        setDueDateLabel(textView, task)

        // THEN - The view shows "D-Day" and is visible
        assertEquals("D-Day", textView.text.toString())
        assertEquals(View.VISIBLE, textView.visibility)
    }

    @Test
    @UiThreadTest
    fun setDueDateLabel_activeTaskOverdueByThreeDays_showsDPlus3() {
        // GIVEN - A new view and an active task that was due 3 days ago
        val textView = TextView(context)
        val task = Task(title = "Buy milk", isCompleted = false, dueDate = LocalDate.now().minusDays(3))

        // WHEN - Bind the task to the view
        setDueDateLabel(textView, task)

        // THEN - The view shows "D+3" and is visible
        assertEquals("D+3", textView.text.toString())
        assertEquals(View.VISIBLE, textView.visibility)
    }

    @Test
    @UiThreadTest
    fun setDueDateLabel_completedTaskWithDueDate_showsClear() {
        // GIVEN - A new view and a completed task due in 3 days
        val textView = TextView(context)
        val task = Task(title = "Buy milk", isCompleted = true, dueDate = LocalDate.now().plusDays(3))

        // WHEN - Bind the task to the view
        setDueDateLabel(textView, task)

        // THEN - The view shows "CLEAR" and is visible
        assertEquals("CLEAR", textView.text.toString())
        assertEquals(View.VISIBLE, textView.visibility)
    }

    @Test
    @UiThreadTest
    fun setDueDateLabel_completedTaskWithoutDueDate_showsClear() {
        // GIVEN - A new view and a completed task with no due date
        val textView = TextView(context)
        val task = Task(title = "Buy milk", isCompleted = true, dueDate = null)

        // WHEN - Bind the task to the view
        setDueDateLabel(textView, task)

        // THEN - The view shows "CLEAR" and is visible
        assertEquals("CLEAR", textView.text.toString())
        assertEquals(View.VISIBLE, textView.visibility)
    }

    @Test
    @UiThreadTest
    fun setDueDateLabel_activeTaskWithoutDueDate_showsNoLabel() {
        // GIVEN - A new view and an active task with no due date
        val textView = TextView(context)
        val task = Task(title = "Buy milk", isCompleted = false, dueDate = null)

        // WHEN - Bind the task to the view
        setDueDateLabel(textView, task)

        // THEN - The view shows no label
        assertShowsNoLabel(textView)
    }

    @Test
    @UiThreadTest
    fun setDueDateLabel_nullTask_showsNoLabel() {
        // GIVEN - A new view
        val textView = TextView(context)

        // WHEN - Bind no task to the view
        setDueDateLabel(textView, null)

        // THEN - The view shows no label
        assertShowsNoLabel(textView)
    }

    // Edge cases: due-today boundary

    @Test
    @UiThreadTest
    fun setDueDateLabel_activeTaskDueYesterday_showsDPlus1() {
        // GIVEN - A new view and an active task due yesterday
        val textView = TextView(context)
        val task = Task(title = "Buy milk", isCompleted = false, dueDate = LocalDate.now().minusDays(1))

        // WHEN - Bind the task to the view
        setDueDateLabel(textView, task)

        // THEN - The view shows "D+1" and is visible
        assertEquals("D+1", textView.text.toString())
        assertEquals(View.VISIBLE, textView.visibility)
    }

    @Test
    @UiThreadTest
    fun setDueDateLabel_hiddenViewActiveTaskDueToday_showsDDay() {
        // GIVEN - A view left hidden by an earlier row
        val textView = TextView(context)
        textView.text = ""
        textView.visibility = View.GONE

        // WHEN - Bind an active task due today to the view
        setDueDateLabel(textView, Task(title = "Buy milk", isCompleted = false, dueDate = LocalDate.now()))

        // THEN - The view shows "D-Day" and is visible
        assertEquals("D-Day", textView.text.toString())
        assertEquals(View.VISIBLE, textView.visibility)
    }

    @Test
    @UiThreadTest
    fun setDueDateLabel_activeTaskDueTomorrow_showsDMinus1() {
        // GIVEN - A new view and an active task due tomorrow
        val textView = TextView(context)
        val task = Task(title = "Buy milk", isCompleted = false, dueDate = LocalDate.now().plusDays(1))

        // WHEN - Bind the task to the view
        setDueDateLabel(textView, task)

        // THEN - The view shows "D-1" and is visible
        assertEquals("D-1", textView.text.toString())
        assertEquals(View.VISIBLE, textView.visibility)
    }

    // Edge cases: reused view going from a shown label to no label

    @Test
    @UiThreadTest
    fun setDueDateLabel_shownViewActiveTaskWithoutDueDate_showsNoLabel() {
        // GIVEN - A reused view that shows "D-3"
        val textView = TextView(context)
        textView.text = "D-3"
        textView.visibility = View.VISIBLE

        // WHEN - Bind an active task with no due date to the view
        setDueDateLabel(textView, Task(title = "Read book", isCompleted = false, dueDate = null))

        // THEN - The old label does not stay on screen
        assertShowsNoLabel(textView)
    }

    @Test
    @UiThreadTest
    fun setDueDateLabel_shownViewNullTask_showsNoLabel() {
        // GIVEN - A reused view that shows "D-3"
        val textView = TextView(context)
        textView.text = "D-3"
        textView.visibility = View.VISIBLE

        // WHEN - Bind no task to the view
        setDueDateLabel(textView, null)

        // THEN - The old label does not stay on screen
        assertShowsNoLabel(textView)
    }

    // Edge cases: completion boundary

    @Test
    @UiThreadTest
    fun setDueDateLabel_taskWithDueDateChecked_showsClear() {
        // GIVEN - A view that shows "D-3" for an active task
        val textView = TextView(context)
        textView.text = "D-3"
        textView.visibility = View.VISIBLE

        // WHEN - Bind the same task once it is completed
        setDueDateLabel(
            textView,
            Task(title = "Buy milk", isCompleted = true, dueDate = LocalDate.now().plusDays(3), id = "task1")
        )

        // THEN - The view shows "CLEAR" and is visible
        assertEquals("CLEAR", textView.text.toString())
        assertEquals(View.VISIBLE, textView.visibility)
    }

    @Test
    @UiThreadTest
    fun setDueDateLabel_taskWithDueDateUnchecked_showsDMinus3() {
        // GIVEN - A view that shows "CLEAR" for a completed task
        val textView = TextView(context)
        textView.text = "CLEAR"
        textView.visibility = View.VISIBLE

        // WHEN - Bind the same task once it is marked active again
        setDueDateLabel(
            textView,
            Task(title = "Buy milk", isCompleted = false, dueDate = LocalDate.now().plusDays(3), id = "task1")
        )

        // THEN - The view shows "D-3" and is visible
        assertEquals("D-3", textView.text.toString())
        assertEquals(View.VISIBLE, textView.visibility)
    }

    @Test
    @UiThreadTest
    fun setDueDateLabel_taskWithoutDueDateChecked_showsClear() {
        // GIVEN - A hidden view for an active task with no due date
        val textView = TextView(context)
        textView.text = ""
        textView.visibility = View.GONE

        // WHEN - Bind the same task once it is completed
        setDueDateLabel(textView, Task(title = "Buy milk", isCompleted = true, dueDate = null, id = "task1"))

        // THEN - The view shows "CLEAR" and is visible
        assertEquals("CLEAR", textView.text.toString())
        assertEquals(View.VISIBLE, textView.visibility)
    }

    @Test
    @UiThreadTest
    fun setDueDateLabel_taskWithoutDueDateUnchecked_showsNoLabel() {
        // GIVEN - A view that shows "CLEAR" for a completed task with no due date
        val textView = TextView(context)
        textView.text = "CLEAR"
        textView.visibility = View.VISIBLE

        // WHEN - Bind the same task once it is marked active again
        setDueDateLabel(textView, Task(title = "Buy milk", isCompleted = false, dueDate = null, id = "task1"))

        // THEN - The view shows no label
        assertShowsNoLabel(textView)
    }

    /** "Shows no label" means the view is not visible, or its text is empty. */
    private fun assertShowsNoLabel(textView: TextView) {
        assertTrue(
            "Expected no label, but view is visible with text \"${textView.text}\"",
            textView.visibility != View.VISIBLE || textView.text.toString() == ""
        )
    }
}
