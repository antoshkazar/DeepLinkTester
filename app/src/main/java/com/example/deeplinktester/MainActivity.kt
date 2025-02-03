package com.example.deeplinktester

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.deeplinktester.ui.theme.DeepLinkTesterTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DeepLinkTesterTheme {
                AppContent { personnelNumber, tc, taskNumber, action ->
                    navigateToProject(personnelNumber, tc, taskNumber, action)
                }
            }
        }
    }

    private fun navigateToProject(
        personnelNumber: String,
        market: String,
        taskNumber: String,
        target: TARGET,
    ) {
        val targetSchema = when (target) {
            TARGET.INV -> INV_SCHEMA
            TARGET.WOB -> WOB_SCHEMA
            TARGET.WKL -> WKL_SCHEMA
        }
        val full = "будет передаваться"
        val uri = "${targetSchema}?personnel=$personnelNumber&market=$market&taskNumber=$taskNumber&fullName=$full"
        val deepLink = Uri.parse(uri)
        val intent = Intent(Intent.ACTION_VIEW, deepLink).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        startActivity(intent)
    }
}

private const val WOB_SCHEMA = "writeoff_breakage://auth"
private const val INV_SCHEMA = "inventory://auth"
private const val WKL_SCHEMA = "work_list://auth"

enum class TARGET(projectName: String) {
    INV("Инвентаризация"),
    WKL("Рабочий список"),
    WOB("Списание");
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppContent(onNavigate: (String, String, String, TARGET) -> Unit) {
    var personnelNumber by remember { mutableStateOf("") }
    var market by remember { mutableStateOf("") }
    var taskNumber by remember { mutableStateOf("") }
    var selectedAction by remember { mutableStateOf(TARGET.WOB) }

    val actions = listOf(TARGET.INV, TARGET.WOB, TARGET.WKL)

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Введите данные:", style = MaterialTheme.typography.titleMedium)

            OutlinedTextField(
                value = personnelNumber,
                onValueChange = { personnelNumber = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Табельный номер") }
            )
            OutlinedTextField(
                value = market,
                onValueChange = { market = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("ТК") }
            )
            OutlinedTextField(
                value = taskNumber,
                onValueChange = { taskNumber = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Номер задания") }
            )


            var expanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = selectedAction.name,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Выберите действие") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    actions.forEach { action ->
                        DropdownMenuItem(
                            text = { Text(action.name) },
                            onClick = {
                                selectedAction = action
                                expanded = false
                            }
                        )
                    }
                }
            }

            // Кнопка перехода
            Button(
                onClick = {
                    onNavigate(
                        personnelNumber,
                        market,
                        taskNumber,
                        selectedAction
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Перейти")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewAppContent() {
    DeepLinkTesterTheme {
        AppContent { _, _, _, _ -> }
    }
}