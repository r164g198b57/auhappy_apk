// Галоўны пакет прылады
package com.github.myapp

// Імпартуем неабходныя бібліятэкі Android і Compose
import android.media.MediaPlayer
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.content.Context
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.util.*

// Аб'яўляем шрыфт Courier New
val courierNewFont = FontFamily(Font(R.font.courier_new))

// Галоўная Activity прыкладання
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Усталёўваем кампазіцыйны інтэрфейс
        setContent {
            MaterialTheme {
                HappyQuestionScreen() // Запускаем асноўны экран
            }
        }
    }
}

// Кампазіцыйная функцыя для галоўнага экрана
@Composable
fun HappyQuestionScreen() {
    // Атрыманне гадзіны
    val currentHour = remember {
        Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    }

    // Вызначаем колер фону ў залежнасці ад часу
    val backgroundColor = when (currentHour) {
        in 0..5, in 22..23 -> Color.DarkGray        // Ноч
        in 6..11 -> Color(0xFFFFF176)              // Раніца — жоўты
        in 12..17 -> Color.White                   // Дзень — белы
        in 18..21 -> Color(0xFF81D4FA)             // Вечар — блакітны
        else -> Color.LightGray                    // На ўсялякі выпадак
    }

    // Стэйты для тэксту і бачнасці кропак
    var answer by remember { mutableStateOf("Ты счастлив?") }
    var buttonsVisible by remember { mutableStateOf(true) }
    val context = LocalContext.current // Кантэкст для доступу да сістэмы

    // Функцыя для вібрацыі
    fun vibrate() {
        val vibrator = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE)
            if (vm is VibratorManager) vm.defaultVibrator else null
        } else {
            @Suppress("DEPRECATION")
            val vib = context.getSystemService(Context.VIBRATOR_SERVICE)
            if (vib is Vibrator) vib else null
        }

        vibrator?.let {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                it.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                it.vibrate(50)
            }
        }
    }

    // Функцыя для прайгравання гукавога файла
    fun playSound(resId: Int) {
        val player = MediaPlayer.create(context, resId)
        player.start()
        player.setOnCompletionListener { it.release() }
    }

    // Вёрстка ў Box з фонам у залежнасці ад часу сутак
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor) // Усталёўваем фон
            .padding(16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            // Тэкст пытання ці адказу
            Text(
                text = answer,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = MaterialTheme.typography.headlineMedium.fontSize * 1.5,
                    fontFamily = if (!buttonsVisible) courierNewFont else MaterialTheme.typography.headlineMedium.fontFamily,  // Усталёўваем Courier New, калі кнопкі невідочныя
                    fontWeight = if (!buttonsVisible) FontWeight.Bold else FontWeight.Normal
                ),
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // Калі кропкі бачныя — паказваем
            if (buttonsVisible) {
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Кропка "НЕТ"
                    Button(
                        onClick = {
                            answer = "Пидора ответ"
                            buttonsVisible = false
                            vibrate()
                            playSound(R.raw.no)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                    ) {
                        Text("НЕТ", color = Color.White)
                    }

                    // Кропка "ДА"
                    Button(
                        onClick = {
                            answer = "ПИЗДА!"
                            buttonsVisible = false
                            vibrate()
                            playSound(R.raw.yes)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Green)
                    ) {
                        Text("ДА", color = Color.White)
                    }
                }
            }
        }
    }

    // Апрацоўка кропкі "Назад" — вяртанне да пытання
    BackHandler(enabled = !buttonsVisible) {
        answer = "Ты счастлив?"
        buttonsVisible = true
    }
}

// Прэў'ю ў рэжыме распрацоўкі
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MaterialTheme {
        HappyQuestionScreen()
    }
}
