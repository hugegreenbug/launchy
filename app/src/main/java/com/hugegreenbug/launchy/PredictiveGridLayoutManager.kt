package com.hugegreenbug.launchy

import androidx.recyclerview.widget.GridLayoutManager
import android.content.Context

class PredictiveGridLayoutManager(context: Context, spanCount:Int) :
    GridLayoutManager(context, spanCount) {
        override fun supportsPredictiveItemAnimations() : Boolean {
            return true
        }
}


