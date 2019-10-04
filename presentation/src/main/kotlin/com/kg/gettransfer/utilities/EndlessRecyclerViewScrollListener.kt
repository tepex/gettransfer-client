package com.kg.gettransfer.utilities

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

abstract class EndlessRecyclerViewScrollListener(var layoutManager: LinearLayoutManager) : RecyclerView.OnScrollListener() {

    // The current offset index of data you have loaded
    private var currentPage = START_PAGE

    // The total number of items in the data set after the last load
    private var previousTotalItemCount = PREVIOUS_TOTAL_ITEM_COUNT_DEFAULT
    // True if we are still waiting for the last set of data to load.

    private var loading = true

    private var totalPages = 0
    var pages: Int = 0
        set(value) {
        totalPages = value
            field = value
        }

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        val totalItemCount = layoutManager.itemCount
        val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()

        // If the total item count is zero and the previous isn't, assume the
        // list is invalidated and should be reset back to initial state
        if (totalItemCount < previousTotalItemCount) {
            currentPage = STARTING_PAGE_INDEX
            previousTotalItemCount = totalItemCount
            if (totalItemCount == 0) loading = true
        }

        // If it’s still loading, we check to see if the data set count has
        // changed, if so we conclude it has finished loading and update the current page
        // number and total item count.
        if (loading && (totalItemCount > previousTotalItemCount)) {
            loading = false
            previousTotalItemCount = totalItemCount
        }

        // If it isn’t currently loading, we check to see if we have breached
        // the visibleThreshold and need to reload more data.
        // If we do need to reload some more data, we execute onLoadMore to fetch the data.
        // threshold should reflect how many total columns there are too
        if (!loading && (lastVisibleItemPosition + VISIBLE_THRESHOLD) >= totalItemCount && dy > 0) {
            loading = if (currentPage < totalPages) {
                currentPage++
                onLoadMore(currentPage)
                true
            } else false
        }
    }

    fun setLoaded() {
        loading = false
    }

    // Call this method whenever performing new searches
    fun resetState() {
        currentPage = START_PAGE
        previousTotalItemCount = PREVIOUS_TOTAL_ITEM_COUNT_DEFAULT
        loading = true
    }

    protected abstract fun onLoadMore(page: Int)

    companion object {
        private const val START_PAGE = 1

        // The minimum amount of items to have below your current scroll position
        // before loading more.
        private const val VISIBLE_THRESHOLD = 5

        private const val PREVIOUS_TOTAL_ITEM_COUNT_DEFAULT = 0

        private const val STARTING_PAGE_INDEX = 1
    }
}
