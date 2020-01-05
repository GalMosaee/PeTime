package com.example.petime.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.petime.Adapters.ProfileAdapter;
import com.example.petime.Model.Model;
import com.example.petime.Model.Post;
import com.example.petime.R;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {
    //private Button edit_profile;
    private RecyclerView recyclerView;
    private String profileid, displayname, email;
    ProfileAdapter postAdapter;
    private List<Post> postList = new ArrayList<>();

    // Inflate profile fragment to the reusable container
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        SharedPreferences prefs = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        profileid = prefs.getString("profileid", "none");
        displayname = prefs.getString("displayname", "none");
        email = prefs.getString("email", "none");

        //ImageView image_profile = view.findViewById(R.id.image_profile);
        TextView username = view.findViewById(R.id.username);
        //edit_profile = view.findViewById(R.id.edit_profile);

        //user
        username.setText(displayname);
        //image_profile.setImageResource(profileid);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new GridLayoutManager(this.getActivity(), 3);
        recyclerView.setLayoutManager(linearLayoutManager);

        postAdapter = new ProfileAdapter(postList, getActivity());
        recyclerView.setAdapter(postAdapter);

//        if (profileid.equals(Model.instance.getCurrentUser().getUid())) {
//            edit_profile.setText("Edit Profile");
//        }
//
//        edit_profile.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String btn = edit_profile.getText().toString();
//
//                if (btn.equals("Edit Profile")) {
//                   // startActivity(new Intent(getContext(), EditProfileActivity.class));
//                }
//            }
//        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Model.instance.getPostsByUserId(profileid, new Model.GetAllPostsListener() {
            @Override
            public void onComplete(List<Post> data) {
                if (data != null){
                    postList = data;
                    postAdapter = new ProfileAdapter(postList,getActivity());
                    recyclerView.setAdapter(postAdapter);
                    postAdapter.notifyDataSetChanged();
                }
            }
        });

        getViewLifecycleOwner();
    }
}
