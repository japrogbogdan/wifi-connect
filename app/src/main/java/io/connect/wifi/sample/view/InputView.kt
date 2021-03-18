package io.connect.wifi.sample.view

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import io.connect.wifi.sample.R

class InputView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    init {
        inflate(context, R.layout.input_view, this)
    }

    private val layout: TextInputLayout by lazy {
        findViewById(R.id.tl_input)
    }

    private val editor: TextInputEditText by lazy {
        findViewById(R.id.et_input)
    }

    fun setHint(text: CharSequence) {
        layout.hint = text
    }

    fun setText(text: CharSequence) {
        editor.setText(text)
        editor.setSelection(editor.length())
    }

    fun getText(): String = editor.text.toString()
}