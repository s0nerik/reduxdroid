package com.github.s0nerik.reduxdroid.core.databinding

import android.view.View
import androidx.databinding.BindingConversion

@BindingConversion
fun convertBooleanToVisibility(isVisible: Boolean) = if (isVisible) View.VISIBLE else View.GONE