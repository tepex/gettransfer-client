package com.kg.gettransfer.utilities

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

abstract class PaginationHelper(var layoutManager: LinearLayoutManager) : RecyclerView.OnScrollListener() {

    private var currentPage = PAGE_START

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        val visibleItemCount = layoutManager.childCount
        val totalItemCount = layoutManager.itemCount
        val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

        if (!isLoading() && !isLastPage()) {
            if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                    && firstVisibleItemPosition >= 0
                    && totalItemCount >= PAGE_SIZE) {

                currentPage++
                loadMore()
            }
        }
    }

    protected abstract fun loadMore()

    protected abstract fun isLastPage(): Boolean

    protected abstract fun isLoading(): Boolean

    companion object {
        const val PAGE_START = 1
        private const val PAGE_SIZE = 10 // scrolling threshold
    }
}
