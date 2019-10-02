package com.kg.gettransfer.utilities

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

abstract class EndlessRecyclerViewScrollListener(var layoutManager: LinearLayoutManager) : RecyclerView.OnScrollListener() {

    // The minimum amount of items to have below your current scroll position
    // before loading more.
    private val visibleThreshold = 5

    // The current offset index of data you have loaded
    private var currentPage = 1

    // The total number of items in the dataset after the last load
    private var previousTotalItemCount = 0
    // True if we are still waiting for the last set of data to load.

    private var loading = true

    // Sets the starting page index
    private var startingPageIndex = 1

    private var totalPages = 0
    var pages: Int = 0
        set(value) {
        totalPages = value
            field = value
        }

    fun getLastVisibleItem(lastVisiblePosition: IntArray): Int {
        var maxSize = 0

        for (i in lastVisiblePosition) {
            if (i == 0) maxSize = lastVisiblePosition[i]
            else if (lastVisiblePosition[i] > maxSize) {
                maxSize = lastVisiblePosition[i]
            }
        }
        return maxSize
    }

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        val visibleItemCount = layoutManager.childCount
        val totalItemCount = layoutManager.itemCount
        val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()

        // If the total item count is zero and the previous isn't, assume the
        // list is invalidated and should be reset back to initial state
        if (totalItemCount < previousTotalItemCount) {
            currentPage = startingPageIndex
            previousTotalItemCount = totalItemCount
            if (totalItemCount == 0) loading = true
        }

        // If it’s still loading, we check to see if the dataset count has
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
        if (!loading && (lastVisibleItemPosition + visibleItemCount) > totalItemCount) {
            currentPage++
            if (currentPage < pages) {
                onLoadMore(currentPage, totalItemCount, recyclerView)
                loading = true
            } else loading = false
        }
    }

    // Call this method whenever performing new searches
    fun resetState() {
        currentPage = startingPageIndex
        previousTotalItemCount = 0
        loading = true
    }

    protected abstract fun onLoadMore(page: Int, totalItemsCount: Int, recyclerView: RecyclerView)

}
