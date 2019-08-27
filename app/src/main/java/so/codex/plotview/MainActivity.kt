package so.codex.plotview

import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        plotView.data = (1..10).map {
            PlotView.Point(it, Random.nextInt(10, 1000))
        }
        val date = Date().time
        val day = 1000 * 60 * 60 * 24
        val calendar = Calendar.getInstance()
        plotView.xLabel = (1..10).map {
            calendar.timeInMillis = date - day
            DateFormat.format("dd MMM", calendar.time).toString().also {
                Log.i("MainActivity", "$it")
            }
        }
    }
}
