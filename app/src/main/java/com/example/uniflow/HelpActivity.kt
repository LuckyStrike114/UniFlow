package com.example.uniflow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uniflow.ui.theme.UniFlowTheme

class HelpActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            UniFlowTheme {
                HelpScreen()
            }
        }
    }
}

@Composable
fun HelpScreen() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Pomoć",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Za pomoć pri korištenju aplikacije UniFlow:",
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "1. Dodirnite '+' za unos novih obaveza.\n" +
                            "2. Koristite boje za razlikovanje tipova aktivnosti.\n" +
                            "3. Postavke su dostupne kroz izbornik.\n" +
                            "4. Korisnički podaci se čuvaju lokalno.",
                    fontSize = 16.sp,
                    lineHeight = 22.sp
                )
            }

            Text(
                text = "Verzija aplikacije: 1.0",
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 24.dp)
            )
        }
    }
}
