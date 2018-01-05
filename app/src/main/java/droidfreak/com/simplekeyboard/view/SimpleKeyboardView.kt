package droidfreak.com.simplekeyboard.view

import android.content.Context
import android.inputmethodservice.Keyboard
import android.inputmethodservice.KeyboardView
import android.inputmethodservice.KeyboardView.OnKeyboardActionListener
import android.util.AttributeSet
import droidfreak.com.simplekeyboard.CustomInputMethodService
import droidfreak.com.simplekeyboard.R
import android.media.AudioManager
import android.support.v4.content.ContextCompat
import android.view.KeyEvent

class SimpleKeyboardView(context: Context, attrs: AttributeSet) : KeyboardView(context, attrs), OnKeyboardActionListener {


    private lateinit var keyboardView: SimpleKeyboardView
    private lateinit var mQwertyKeyboard: Keyboard
    private lateinit var mSymbolKeyboard: Keyboard
    private lateinit var mSymbolShiftKeyboard: Keyboard

    private var mCapsLock: Boolean = false

    fun setKeyboardView(keyboardView: SimpleKeyboardView) {
        this.keyboardView = keyboardView
        this.keyboardView.onKeyboardActionListener = this
    }

    override fun onKey(primaryCode: Int, ints: IntArray) {

        val ic = (context as CustomInputMethodService).currentInputConnection

        playClick(primaryCode)

        when (primaryCode) {

            Keyboard.KEYCODE_DELETE -> ic.deleteSurroundingText(1, 0)

            Keyboard.KEYCODE_DONE -> ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER))

            Keyboard.KEYCODE_MODE_CHANGE -> run({

                var currentKeyboard = keyboardView.keyboard

                currentKeyboard = if (currentKeyboard === mSymbolKeyboard || currentKeyboard === mSymbolShiftKeyboard) {
                    mQwertyKeyboard

                } else {
                    mSymbolKeyboard
                }
                keyboardView.keyboard = currentKeyboard

                if (currentKeyboard === mSymbolKeyboard) {
                    currentKeyboard.isShifted = false
                }
            }
            )

            Keyboard.KEYCODE_SHIFT -> run({
                controlShiftButton()
            }
            )

            else -> {

                val key = if (keyboardView.isShifted) {
                    Character.toUpperCase(primaryCode)
                } else {
                    primaryCode
                }
                val c = key.toChar()

                ic.commitText(c.toString(), 1)
            }
        }
    }

    fun initialize() {
        mQwertyKeyboard = Keyboard(context, R.xml.qwerty_keyboard)
        mSymbolKeyboard = Keyboard(context, R.xml.symbols_keyboard)
        mSymbolShiftKeyboard = Keyboard(context, R.xml.symbols_2_keyboard)

        keyboardView.keyboard = mQwertyKeyboard
    }

    private fun playClick(keyCode: Int) {
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        when (keyCode) {
            32 -> am.playSoundEffect(AudioManager.FX_KEYPRESS_SPACEBAR)
            Keyboard.KEYCODE_DONE, 10 -> am.playSoundEffect(AudioManager.FX_KEYPRESS_RETURN)
            Keyboard.KEYCODE_DELETE -> am.playSoundEffect(AudioManager.FX_KEYPRESS_DELETE)
            else -> am.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD)
        }
    }

    private fun controlShiftButton() {

        val currentKeyboard = keyboardView.keyboard
        val keys = currentKeyboard.keys
        val keyShift: Keyboard.Key? = keys.lastOrNull { it.codes[0] == -1 }

        when (currentKeyboard) {
            mQwertyKeyboard -> {
                keyboardView.isShifted = mCapsLock || !keyboardView.isShifted

                if (!mCapsLock) {
                    if (keyShift != null) {
                        if (keyboardView.isShifted) {
                            keyShift.icon = ContextCompat.getDrawable(context, R.drawable.ic_shift_down)
                        } else {
                            keyShift.icon = ContextCompat.getDrawable(context, R.drawable.ic_shift_up)
                        }
                    }
                }
            }
            mSymbolKeyboard -> {
                mSymbolKeyboard.isShifted = true
                keyboardView.keyboard = mSymbolShiftKeyboard
                mSymbolShiftKeyboard.isShifted = true
            }

            mSymbolShiftKeyboard -> {
                mSymbolShiftKeyboard.isShifted = false
                keyboardView.keyboard = mSymbolKeyboard
                mSymbolKeyboard.isShifted = false
            }

        }

        if ((keyboardView.isShifted || mCapsLock) && keyShift != null) {
            keyShift.icon = ContextCompat.getDrawable(context, R.drawable.ic_shift_down)
        } else {
            if (keyShift != null)
                keyShift.icon = ContextCompat.getDrawable(context, R.drawable.ic_shift_up)
        }
    }

    override fun onText(charSequence: CharSequence) {

    }

    override fun swipeLeft() {

    }

    override fun swipeRight() {

    }

    override fun swipeDown() {

    }

    override fun swipeUp() {

    }

    override fun onPress(primaryCode: Int) {
    }

    override fun onRelease(i: Int) {

    }

}