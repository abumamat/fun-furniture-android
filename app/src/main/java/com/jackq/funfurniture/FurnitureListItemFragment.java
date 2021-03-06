package com.jackq.funfurniture;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.reflect.TypeToken;
import com.jackq.funfurniture.API.APIServer;
import com.jackq.funfurniture.model.Furniture;
import com.jackq.funfurniture.model.MockFurnitureData;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class FurnitureListItemFragment extends Fragment {
    private static final String DEBUG_TAG = "FURNITURE_LIST";
    private static final String ARG_CATEGORY = "categoryCode";
    MyFurnitureListItemRecyclerViewAdapter adapter = null;
    private int mColumnCount = 1;
    private int mCategoryCode = 1;
    private OnListFragmentInteractionListener mListener;
    private String mCategoryName;
    private List<Furniture> furnitures = new ArrayList<>();
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public FurnitureListItemFragment() {
    }

    public static FurnitureListItemFragment newInstance(int categoryCode) {
        FurnitureListItemFragment fragment = new FurnitureListItemFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_CATEGORY, categoryCode);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mCategoryCode = getArguments().getInt(ARG_CATEGORY);
            String[] stringArray = getResources().getStringArray(R.array.list_categories);
            mCategoryName = mCategoryCode < stringArray.length ? stringArray[mCategoryCode] : ("New Category " + mCategoryCode);

            // After creating the item start loading image from the server
            APIServer.getItemList(this.getContext(), mCategoryCode, new APIServer.APIServerCallback<List<Furniture>>() {
                @Override
                public void onResource(List<Furniture> resource) {
                    for (Furniture f : resource) {
                        Log.d(DEBUG_TAG, "onCompleted: " + f.getName());
                        furnitures.add(f);
                    }
                    if(adapter != null)  adapter.notifyItemRangeInserted(0, furnitures.size());
                }

                @Override
                public void onError(Exception e) {
                    Log.e(DEBUG_TAG, "onCompleted: No data received");
                    Log.e(DEBUG_TAG, e.toString());
                }
            });
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_furniturelistitem_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            adapter = new MyFurnitureListItemRecyclerViewAdapter(context, this.furnitures, mListener);
            recyclerView.setAdapter(adapter);
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(Furniture item);
    }
}
