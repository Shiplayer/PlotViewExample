package so.codex.plotview

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PathMeasure
import android.graphics.Rect
import android.graphics.Shader
import android.graphics.Typeface
import android.text.TextPaint
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Log
import android.view.View
import kotlin.math.roundToInt
import kotlin.properties.Delegates

class PlotView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    //private val foregroundDrawable: Drawable = ContextCompat.getDrawable(context, R.drawable.background)!!
    private var maxX = 0
    private var maxY = 0
    private var paint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeWidth = 2.toDp()
    }

    private val plotPaddingBottom = 16.toDp()
    private val plotPaddingLeft = 0.toDp()

    private var measure: PathMeasure? = null
    public var data by Delegates.observable(listOf<Point>()) { _, old, new ->
        post {
            maxX = new.maxBy { it.x }?.x ?: 0
            maxY = new.maxBy { it.y }?.y ?: 0
            path.reset()
            val width = width - plotPaddingLeft
            val height = height - plotPaddingBottom
            paint.shader = LinearGradient(
                    0f,
                    0f,
                    0f,
                    height,
                    Color.RED,
                    Color.BLACK,
                    Shader.TileMode.REPEAT
            )
            val points = new.sortedBy { it.x }.takeLast(16)
            val step = width / (points.size - 1)
            Log.i("PlotView", "step point = $step")
            points.forEachIndexed { i, it ->
                val y = height - it.y.toFloat() / maxY * height
                val x = plotPaddingLeft + (step * i)
                Log.i("PlotViewPoint", "($x, $y) ($width, $height)")
                if (i == 0) {
                    path.moveTo(x, y)
                }
                path.lineTo(x, y)
            }
            measure = PathMeasure(path, false)
            ObjectAnimator.ofFloat(this, "phase", 1f, 0f).apply {
                duration = 2000
                start()
            }
        }
    }

    private var mXLabel: List<Label> = listOf()
    private val labelPaint = TextPaint().apply {
        isAntiAlias = true
        textSize = 10.toDp()
        typeface = Typeface.create("roboto", Typeface.NORMAL)
        color = Color.GRAY
        isLinearText = true
        textAlign = Paint.Align.LEFT
    }

    public var xLabel by Delegates.observable(listOf<String>()) { _, old, new ->
        post {
            val width = width - plotPaddingLeft
            val step = width / (data.size - 1)
            Log.i("PlotView", "step = $step")
            val betweenPadding = 2.toDp()
            mXLabel = List(data.size - 2) {
                Label(
                        TextUtils.ellipsize(
                                new[it],
                                labelPaint,
                                step - betweenPadding * 2,
                                TextUtils.TruncateAt.END
                        ),
                        (step * (it + 1)) - (step - betweenPadding * 2)/ 2 + betweenPadding * 2,
                        height - 2.toDp()
                ).also { l ->
                    Log.i("PlotView", "Label$it text = ${l.text} x = ${l.x} y = ${l.y}")
                }
            }
        }
    }

    private val path = Path()

    init {
        Log.i("PlotView", "width = $width\theight = $height")
    }

    fun setPhase(phase: Float) {
        Log.i("PlotView", "Phase is $phase")
        paint.pathEffect = DashPathEffect(
                floatArrayOf(
                        measure?.length ?: 0f,
                        measure?.length ?: 0f
                ), phase * (measure?.length ?: 0f)
        ).also {
            Log.i("PlotView", "${measure?.length ?: 0f}, ${phase * (measure?.length ?: 0f)}")
        }
        invalidate()
    }

    override fun getMinimumWidth(): Int {
        return (resources.displayMetrics.density * 320).roundToInt()
    }

    override fun getMinimumHeight(): Int {
        return (resources.displayMetrics.density * 100).roundToInt()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawPath(path, paint)
        mXLabel.forEach {
            canvas.drawText(it.text.toString(), it.x, it.y, labelPaint)
            val rect = Rect()
            labelPaint.getTextBounds(it.text.toString(),0, it.text.length, rect)
            canvas.drawLine(it.x + rect.exactCenterX(), it.y, it.x + rect.exactCenterX(), 0f, Paint().apply {
                isAntiAlias = true
                color = Color.RED
            })
            rect.offsetTo(it.x.toInt(), it.y.toInt() - rect.height() + 1)
            rect.left -= 2.toDp().toInt()
            rect.right += 2.toDp().toInt()
            canvas.drawRect(rect, Paint().apply {
                isAntiAlias = true
                color = Color.RED
                style = Paint.Style.STROKE
                strokeWidth = 1.toDp()
            })
        }

    }

    fun Int.toDp(): Float = resources.displayMetrics.density * this

    data class Point(val x: Int, val y: Int)
    data class Label(val text: CharSequence, val x: Float, val y: Float)
}