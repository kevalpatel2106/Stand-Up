package com.kevalpatel2106.rulerview

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.TextView

/**
 * Created by Keval on 16/12/17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
class RulerDemoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ruler_demo)

        val valueTv = findViewById<TextView>(R.id.selected_ruler_value_tv)
        val valuePicker = findViewById<ScrollingValuePicker>(R.id.ruler_view_demo)

        valuePicker.setMinMaxValue(125F, 350F)
        valuePicker.setInitValue(150F)
        valuePicker.viewMultipleSize = 10F
        valuePicker.setOnScrollChangedListener(object : ObservableHorizontalScrollView.OnScrollChangedListener {
            override fun onScrollChanged(view: ObservableHorizontalScrollView?, l: Int, t: Int) {
                valueTv.text = String.format("%d Kgs", valuePicker.getCurrentValue(l))

            }

            override fun onScrollStopped(l: Int, t: Int) {
                valueTv.text = String.format("%d Kgs\nScroll Stopped.", valuePicker.getCurrentValue(l))
            }

        })
    }

}