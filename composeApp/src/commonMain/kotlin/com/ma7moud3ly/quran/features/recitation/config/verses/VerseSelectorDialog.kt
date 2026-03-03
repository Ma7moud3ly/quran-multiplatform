package com.ma7moud3ly.quran.features.recitation.config.verses

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ma7moud3ly.quran.model.asVerseNumber
import com.ma7moud3ly.quran.ui.AppTheme
import com.ma7moud3ly.quran.ui.DialogHeader
import com.ma7moud3ly.quran.ui.MyAlertDialog
import com.ma7moud3ly.quran.ui.MySurface
import com.ma7moud3ly.quran.ui.hafsSmartFamily
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import quran.composeapp.generated.resources.Res
import quran.composeapp.generated.resources.ok
import quran.composeapp.generated.resources.recite_select_begin

@Preview
@Composable
private fun VerseSelectorDialogPreview() {
    AppTheme(darkTheme = true) {
        VerseSelectorDialog(
            start = 1,
            end = 7,
            limit = 7,
            selectStart = true,
            onConfirm = { _, _ -> },
            onDismiss = {}
        )
    }
}

@Preview
@Composable
private fun VerseSelectorDialogPreviewLight() {
    AppTheme(darkTheme = false) {
        VerseSelectorDialog(
            start = 1,
            end = 7,
            limit = 7,
            selectStart = true,
            onConfirm = { _, _ -> },
            onDismiss = {}
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerseSelectorDialog(
    start: Int,
    end: Int,
    limit: Int,
    selectStart: Boolean,
    showKeyboard: Boolean = true,
    title: StringResource = Res.string.recite_select_begin,
    onConfirm: (Int, Int) -> Unit,
    onDismiss: () -> Unit
) {

    val current by remember {
        derivedStateOf {
            when (selectStart) {
                true -> start
                false -> end
            }
        }
    }

    var startValue by remember(selectStart) {
        mutableStateOf(
            if (!selectStart) start
            else 1
        )
    }

    var endValue by remember(selectStart) {
        mutableStateOf(
            if (selectStart) end
            else limit
        )
    }

    var number by remember { mutableStateOf<Int?>(current) }
    var textNumber by remember { mutableStateOf(TextFieldValue("")) }
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    fun setTextFieldValue(value: Any?) {
        if (value == null) return
        textNumber = TextFieldValue(
            text = value.toString(),
            selection = TextRange(value.toString().length)
        )
    }

    LaunchedEffect(Unit) {
        setTextFieldValue(current)
    }

    fun incrementNumber() {
        if (number == null) number = current
        else if (number!! < endValue) number = number!! + 1
        setTextFieldValue(number)
    }

    fun decrementNumber() {
        if (number == null) number = current
        else if (number!! > startValue) number = number!! - 1
        setTextFieldValue(number)
    }

    fun resetToStaring() {
        number = startValue
        setTextFieldValue(startValue)
    }

    fun resetToEnd() {
        number = endValue
        setTextFieldValue(endValue)
    }

    fun getNumber(value: String) {
        val newNumber = value.toIntOrNull()
        number = if (newNumber != null && newNumber <= endValue && newNumber >= startValue) {
            newNumber
        } else null
    }

    fun commit(value: Int) {
        if (selectStart) startValue = value
        else endValue = value
        if (startValue > endValue) endValue = limit
    }


    LaunchedEffect(Unit) {
        if (showKeyboard) {
            focusRequester.requestFocus()
            keyboardController?.show()
        }
    }

    MyAlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.padding(16.dp),
        header = {
            DialogHeader(
                text = stringResource(title),
                onBack = onDismiss
            )
        },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ArrowButton(
                number = startValue,
                onClick = ::decrementNumber,
                onLongClick = ::resetToStaring
            )
            BasicTextField(
                value = textNumber,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                onValueChange = { value ->
                    getNumber(value.text)
                    setTextFieldValue(value.text)
                },
                modifier = Modifier.weight(1f).focusRequester(focusRequester),
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 30.sp,
                    textAlign = TextAlign.Center
                ),
                maxLines = 1
            )
            ArrowButton(
                number = endValue,
                onClick = ::incrementNumber,
                onLongClick = ::resetToEnd
            )
        }
        Button(
            onClick = {
                if (number != null) commit(number!!)
                onConfirm(startValue, endValue)
            },
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            enabled = number != null,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = stringResource(Res.string.ok),
                style = MaterialTheme.typography.bodyLarge,
                fontSize = 18.sp
            )
        }
    }
}


@Composable
private fun ArrowButton(
    number: Int,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    MySurface(
        onClick = null,
        color = Color.Transparent,
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
    ) {
        Text(
            text = number.asVerseNumber(),
            fontFamily = hafsSmartFamily(),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onPrimary,
            fontSize = 50.sp
        )
    }
}
