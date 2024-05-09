package com.example.vaccinationmanagerapp.decorators

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
/**
 * A RecyclerView.ItemDecoration that adds space between items in a RecyclerView.
 *
 * @property space The space to add between items in the RecyclerView.
 *
 * @constructor Creates an instance of SpaceItemDecoration with the provided space.
 */
class SpaceItemDecoration(private val space: Int) : RecyclerView.ItemDecoration() {
    /**
     * Retrieve any offsets for the given item. Each field of outRect specifies the number of pixels that the item view should be inset by, similar to padding or margin.
     *
     * @param outRect The Rect to receive the output.
     * @param view The child view to decorate.
     * @param parent The RecyclerView this ItemDecoration is decorating.
     * @param state The current state of RecyclerView.
     */
    override fun getItemOffsets(
        outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
    ) {
        outRect.left = space
        outRect.right = space
    }
}