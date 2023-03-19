package com.example.opencart.utils

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText

class OCEdittext(context: Context,attrs : AttributeSet) : AppCompatEditText(context, attrs) {

    init {
        applyFonts()
    }
    private fun applyFonts(){
        val typeFace : Typeface = Typeface.createFromAsset(context.assets,"Montserrat-Regular.ttf")
        typeface = typeFace
    }
}