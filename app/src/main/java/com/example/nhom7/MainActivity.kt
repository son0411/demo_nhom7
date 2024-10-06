package com.example.nhom7

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.abs

class MainActivity : AppCompatActivity() {

    private lateinit var gestureStatus: TextView
    private lateinit var draggableView: View
    private var x1: Float = 0.0f
    private var x2: Float = 0.0f
    private var y1: Float = 0.0f
    private var y2: Float = 0.0f
    private var isLongPress = false
    private var handler = Handler(Looper.getMainLooper())
    private var scaleFactor = 1.0f
    private lateinit var scaleGestureDetector: ScaleGestureDetector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Khởi tạo TextView và View kéo thả
        gestureStatus = findViewById(R.id.gestureStatus)
        draggableView = findViewById(R.id.draggableView)
        gestureStatus.text = "Chờ cử chỉ..."

        // Phóng to/thu nhỏ (Pinch)
        scaleGestureDetector = ScaleGestureDetector(this, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                scaleFactor *= detector.scaleFactor
                scaleFactor = scaleFactor.coerceIn(0.5f, 2.0f) // Giới hạn tỷ lệ phóng to/thu nhỏ
                draggableView.scaleX = scaleFactor
                draggableView.scaleY = scaleFactor
                gestureStatus.text = "Phóng to/Thu nhỏ..."
                return true
            }
        })

        // Kéo và thả (Drag and Drop)
        draggableView.setOnTouchListener { view, event ->
            scaleGestureDetector.onTouchEvent(event)

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    gestureStatus.text = "Bắt đầu kéo..."
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    view.x = event.rawX - view.width / 2
                    view.y = event.rawY - view.height / 2
                    gestureStatus.text = "Đang kéo..."
                    true
                }
                MotionEvent.ACTION_UP -> {
                    gestureStatus.text = "Thả!"
                    true
                }
                else -> false
            }
        }
    }

    // Lắng nghe sự kiện chạm màn hình
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            // Bắt đầu chạm xuống
            MotionEvent.ACTION_DOWN -> {
                x1 = event.x
                y1 = event.y
                isLongPress = false

                // Xử lý nhấn giữ (long press) sau 500ms
                handler.postDelayed({
                    isLongPress = true
                    gestureStatus.text = "Nhấn giữ!"
                }, 500)
            }

            // Khi ngón tay được nhấc lên
            MotionEvent.ACTION_UP -> {
                x2 = event.x
                y2 = event.y

                // Hủy sự kiện nhấn giữ nếu ngón tay nhấc lên trước 500ms
                handler.removeCallbacksAndMessages(null)

                if (!isLongPress) {
                    // Kiểm tra khoảng cách để xác định vuốt
                    val deltaX = x2 - x1
                    val deltaY = y2 - y1

                    if (abs(deltaX) > abs(deltaY)) {
                        if (deltaX > 100) {
                            gestureStatus.text = "Vuốt sang phải!"
                        } else if (deltaX < -100) {
                            gestureStatus.text = "Vuốt sang trái!"
                        }
                    } else {
                        if (deltaY > 100) {
                            gestureStatus.text = "Vuốt xuống!"
                        } else if (deltaY < -100) {
                            gestureStatus.text = "Vuốt lên!"
                        }
                    }

                    // Nếu không vuốt, thì đó là chạm một lần
                    if (abs(deltaX) < 100 && abs(deltaY) < 100) {
                        gestureStatus.text = "Chạm một lần!"
                    }
                }
            }

            // Hủy sự kiện chạm (khi chuyển động khác xảy ra)
            MotionEvent.ACTION_CANCEL -> {
                handler.removeCallbacksAndMessages(null)
            }
        }

        // Xử lý cử chỉ phóng to/thu nhỏ
        scaleGestureDetector.onTouchEvent(event)
        return super.onTouchEvent(event)
    }
}
