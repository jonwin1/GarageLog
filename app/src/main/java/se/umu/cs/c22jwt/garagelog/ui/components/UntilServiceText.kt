package se.umu.cs.c22jwt.garagelog.ui.components

import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import se.umu.cs.c22jwt.garagelog.data.Reminder
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit

/**
 * A text item that describes how long it is until the next service.
 *
 * @param reminders         List of all reminders for the vehicle.
 * @param currentMileage    The current mileage of the vehicle.
 * @param leadingText       Text to start the sentence with.
 * @param style             The text style to apply.
 *
 * @author Jonatan Wincent (c22jwt@cs.umu.se)
 */
@Composable
fun UntilServiceText(
    reminders: List<Reminder>,
    currentMileage: Int,
    leadingText: String = "In",
    style: TextStyle = LocalTextStyle.current
) {
    var minMileage: Int? = null
    var minMonths: Long? = null

    for (reminder in reminders) {
        val (mileageRemaining, monthsRemaining) = getRemaining(reminder, currentMileage)

        if ((minMileage == null || mileageRemaining < minMileage) && reminder.mileageInterval != 0) {
            minMileage = mileageRemaining
        }
        if ((minMonths == null || monthsRemaining < minMonths) && reminder.dateInterval != 0) {
            minMonths = monthsRemaining
        }
    }

    val (text, color) = untilServiceText(minMileage, minMonths)

    if (!text.isEmpty()) {
        Text(
            text = "$leadingText $text", color = color, style = style
        )
    }
}

/**
 * A text item that describes how long it is until a service reminder.
 *
 * @param reminder          The reminder.
 * @param currentMileage    The current mileage of the vehicle.
 * @param leadingText       Text to start the sentence with.
 * @param style             The text style to apply.
 *
 * @author Jonatan Wincent (c22jwt@cs.umu.se)
 */
@Composable
fun UntilServiceText(
    reminder: Reminder,
    currentMileage: Int,
    leadingText: String = "In",
    style: TextStyle = LocalTextStyle.current
) {
    val remaining = getRemaining(reminder, currentMileage)

    val mileageRemaining = if (reminder.mileageInterval == 0) null else remaining.first
    val monthsRemaining = if (reminder.dateInterval == 0) null else remaining.second

    val (text, color) = untilServiceText(mileageRemaining, monthsRemaining)

    if (!text.isEmpty()) {
        Text(
            text = "$leadingText $text", color = color, style = style
        )
    }
}

/**
 * Create the time remaining text given the calculated values.
 *
 * @param mileageRemaining  Kilometers remaining until reminder.
 * @param monthsRemaining   Months remaining until reminder.
 * @return                  A pair with the text and the color to make the text.
 *
 * @author Jonatan Wincent (c22jwt@cs.umu.se)
 */
@Composable
private fun untilServiceText(mileageRemaining: Int?, monthsRemaining: Long?): Pair<String, Color> {
    val months = if (monthsRemaining == 1L) "month" else "months"
    var text = ""

    val color =
        if (mileageRemaining?.let { (it < 100) } ?: false || monthsRemaining?.let { it < 2 } ?: false) {
            MaterialTheme.colorScheme.error
        } else {
            Color.Unspecified
        }

    if (mileageRemaining != null && monthsRemaining != null) {
        text = "$mileageRemaining km or $monthsRemaining $months"
    } else if (mileageRemaining != null) {
        text = "$mileageRemaining km"
    } else if (monthsRemaining != null) {
        text = "$monthsRemaining $months"
    }

    return Pair(text, color)
}

/**
 * Calculate the time remaining util a service reminder.
 *
 * @param reminder          The reminder.
 * @param currentMileage    The current mileage of the vehicle.
 * @return                  A pair with first the mileage remaining and second months remaining.
 *
 * @author Jonatan Wincent (c22jwt@cs.umu.se)
 */
private fun getRemaining(reminder: Reminder, currentMileage: Int): Pair<Int, Long> {
    var mileageRemaining = reminder.mileageInterval - (currentMileage - reminder.mileage)

    val now = LocalDate.now()
    val creation = reminder.date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
    var monthsRemaining = reminder.dateInterval - ChronoUnit.MONTHS.between(creation, now)

    if (mileageRemaining < 0) mileageRemaining = 0
    if (monthsRemaining < 0) monthsRemaining = 0

    return Pair(mileageRemaining, monthsRemaining)
}