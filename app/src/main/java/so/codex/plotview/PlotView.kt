package so.codex.plotview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.core.content.ContextCompat
import kotlin.math.roundToInt
import kotlin.properties.Delegates

class PlotView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    //private val foregroundDrawable: Drawable = ContextCompat.getDrawable(context, R.drawable.background)!!
    private var maxY = 0
    private var minY = 0
    private var data by Delegates.observable(listOf<Point>()) {_, new, old ->
        maxY = new.maxBy { it.y }?.y ?: 0
        minY = new.minBy { it.y }?.y ?: 0

    }

    private val startPoint = Point(0,0)

    init {

    }

    override fun getMinimumWidth(): Int {
        return (resources.displayMetrics.density * 320).roundToInt()
    }

    override fun getMinimumHeight(): Int {
        return (resources.displayMetrics.density * 100).roundToInt()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawRoundRect(
                paddingLeft.toFloat(),
                paddingTop.toFloat(),
                (width - (paddingRight + paddingLeft)).toFloat(),
                (height - (paddingBottom + paddingTop)).toFloat(),
                8.toDp(),
                8.toDp(),
                Paint().apply {
                    color = Color.DKGRAY
                }
        )

        val bottomPlot = (height - (paddingBottom + paddingTop))
        val leftPlot = (paddingLeft)

        data.forEachIndexed {index, it ->
            if(index == data.size)
                return@forEachIndexed

        }

    }

    fun Int.toDp(): Float = resources.displayMetrics.density * this

    data class Point(val x: Int, val y:Int)
}