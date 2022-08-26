package eu.kanade.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.material3.TriStateCheckbox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import eu.kanade.core.prefs.CheckboxState
import eu.kanade.domain.category.model.Category
import eu.kanade.presentation.category.visualName
import eu.kanade.presentation.util.horizontalPadding
import eu.kanade.tachiyomi.R

@Composable
fun ChangeCategoryDialog(
    initialSelection: List<CheckboxState<Category>>,
    onDismissRequest: () -> Unit,
    onEditCategories: () -> Unit,
    onConfirm: (List<Long>, List<Long>) -> Unit,
) {
    if (initialSelection.isEmpty()) {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            confirmButton = {
                TextButton(
                    onClick = {
                        onDismissRequest()
                        onEditCategories()
                    },
                ) {
                    Text(text = stringResource(R.string.action_edit_categories))
                }
            },
            title = {
                Text(text = stringResource(R.string.action_move_category))
            },
            text = {
                Text(text = stringResource(R.string.information_empty_category_dialog))
            },
        )
        return
    }
    var selection by remember { mutableStateOf(initialSelection) }
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            Row {
                TextButton(onClick = {
                    onDismissRequest()
                    onEditCategories()
                },) {
                    Text(text = stringResource(R.string.action_edit))
                }
                Spacer(modifier = Modifier.weight(1f))
                TextButton(onClick = onDismissRequest) {
                    Text(text = stringResource(android.R.string.cancel))
                }
                TextButton(
                    onClick = {
                        onDismissRequest()
                        onConfirm(
                            selection.filter { it is CheckboxState.State.Checked || it is CheckboxState.TriState.Include }.map { it.value.id },
                            selection.filter { it is CheckboxState.TriState.Exclude }.map { it.value.id },
                        )
                    },
                ) {
                    Text(text = stringResource(R.string.action_add))
                }
            }
        },
        title = {
            Text(text = stringResource(R.string.action_move_category))
        },
        text = {
            Column {
                selection.forEach { checkbox ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        val onCheckboxChange: (CheckboxState<Category>) -> Unit = {
                            val index = selection.indexOf(it)
                            val mutableList = selection.toMutableList()
                            mutableList.removeAt(index)
                            mutableList.add(index, it.next())
                            selection = mutableList.toList()
                        }
                        when (checkbox) {
                            is CheckboxState.TriState -> {
                                TriStateCheckbox(
                                    state = checkbox.asState(),
                                    onClick = { onCheckboxChange(checkbox) },
                                )
                            }
                            is CheckboxState.State -> {
                                Checkbox(
                                    checked = checkbox.isChecked,
                                    onCheckedChange = { onCheckboxChange(checkbox) },
                                )
                            }
                        }

                        Text(
                            text = checkbox.value.visualName,
                            modifier = Modifier.padding(horizontal = horizontalPadding),
                        )
                    }
                }
            }
        },
    )
}