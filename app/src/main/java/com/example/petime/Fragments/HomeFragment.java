package com.example.petime.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.petime.Adapters.PostAdapter;
import com.example.petime.Model.Model;
import com.example.petime.Model.Post;
import com.example.petime.R;

import java.util.ArrayList;

import java.util.List;

public class HomeFragment extends Fragment {
    private RecyclerView recyclerView;
    private List<Post> postList = new ArrayList<>();
    PostAdapter postAdapter;

    //Display post from the user we are following
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);

        postAdapter = new PostAdapter(postList, getActivity());
        recyclerView.setAdapter(postAdapter);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Model.instance.getAllPosts(new Model.GetAllPostsListener() {
            @Override
            public void onComplete(List<Post> data) {
                if (data != null){
                    postList = data;
                    postAdapter = new PostAdapter(postList, getActivity());
                    recyclerView.setAdapter(postAdapter);
                    postAdapter.notifyDataSetChanged();
                }
            }
        });

        getViewLifecycleOwner();
    }
}