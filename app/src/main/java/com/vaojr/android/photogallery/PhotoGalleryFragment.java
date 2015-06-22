package com.vaojr.android.photogallery;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TableRow;

import java.util.ArrayList;

public class PhotoGalleryFragment extends Fragment {
    private static final String TAG = "PhotoGalleryFragment";

    GridView mGridView;
    ArrayList<GalleryItem> mItems;

    private int mPages;
    private int mTotalCount = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPages = 0;
        mItems = new ArrayList<GalleryItem>();


        setRetainInstance(true);
        new FetchItemsTask().execute(++mPages);
        Log.e(TAG, "onCreate(): current_page: " + mPages);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_photo_gallery, container, false);

        mGridView = (GridView)v.findViewById(R.id.gridView);
        mGridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                                 int totalItemCount) {
                if ((visibleItemCount > 0)
                        && ((firstVisibleItem + visibleItemCount) == totalItemCount)
                        && (totalItemCount > mTotalCount)) {
                    mTotalCount = totalItemCount;
                    new FetchItemsTask().execute(++mPages);
                }
            }
        });

        setupAdapter();

        return v;
    }

    void setupAdapter() {
        if (getActivity() == null || mGridView == null) return;

        if (!mItems.isEmpty()) {
            if (mGridView.getAdapter() == null) {
                mGridView.setAdapter(new ArrayAdapter<GalleryItem>(getActivity(),
                        android.R.layout.simple_gallery_item, mItems));
            } else {
                ArrayAdapter<GalleryItem> galleryItemArrayAdapter =
                        (ArrayAdapter<GalleryItem>)mGridView.getAdapter();
                galleryItemArrayAdapter.notifyDataSetChanged();
            }

        } else {
            mGridView.setAdapter(null);
        }
    }

    private class FetchItemsTask extends AsyncTask<Integer,Void,ArrayList<GalleryItem>> {
        @Override
        protected ArrayList<GalleryItem> doInBackground(Integer... params) {
            return new FlickrFetchr().fetchItems(params[0]);
        }

        @Override
        protected void onPostExecute(ArrayList<GalleryItem> items) {
            mItems.addAll(items);
            setupAdapter();
        }
    }
}
