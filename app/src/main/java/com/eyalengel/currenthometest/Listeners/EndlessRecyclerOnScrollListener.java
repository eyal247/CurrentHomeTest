package com.eyalengel.currenthometest.Listeners;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;


public abstract class EndlessRecyclerOnScrollListener extends RecyclerView.OnScrollListener
{
    private int mFirstVisibleItem;
    private int mVisibleItemCount;
    private int mTotalItemCount;

    private int mPreviousTotal = 0; // The total number of items in the dataset after the last load
    private boolean mLoading = true; // True if we are still waiting for the last set of data to load.
    private int mVisibleThreshold = 2; // The minimum amount of items to have below your current scroll position before loading more.
    private int mCurrentPage = 1;

    private LinearLayoutManager mLayoutManager;

    public EndlessRecyclerOnScrollListener(LinearLayoutManager layoutManager)
    {
        mLayoutManager = layoutManager;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy)
    {
        super.onScrolled(recyclerView, dx, dy);

        mVisibleItemCount = recyclerView.getChildCount();
        mTotalItemCount = mLayoutManager.getItemCount();
        mFirstVisibleItem = mLayoutManager.findFirstVisibleItemPosition();

        if (mLoading) {
            if (mTotalItemCount > mPreviousTotal) {
                mLoading = false;
                mPreviousTotal = mTotalItemCount;
            }
        }
        if (!mLoading &&
                (mTotalItemCount - mVisibleItemCount) <= (mFirstVisibleItem + mVisibleThreshold)) {
            // End has been reached

            // Do something
            mCurrentPage++;

            onLoadMore(mCurrentPage);

            mLoading = true;
        }
    }

    public int getmCurrentPage()
    {
        return mCurrentPage;
    }

    public abstract void onLoadMore(int currentPage);
}

