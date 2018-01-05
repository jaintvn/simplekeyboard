package droidfreak.com.simplekeyboard

import android.inputmethodservice.InputMethodService
import android.view.View
import droidfreak.com.simplekeyboard.view.SimpleKeyboardView

class CustomInputMethodService : InputMethodService() {


    private lateinit var mKeyboardView: SimpleKeyboardView

    override fun onCreateInputView(): View {

        val layout = layoutInflater.inflate(R.layout.keyboard_view, null)
        mKeyboardView = layout.findViewById(R.id.keyboard_view) as SimpleKeyboardView
        mKeyboardView.setKeyboardView(mKeyboardView)
        mKeyboardView.initialize()
        return layout
    }
}