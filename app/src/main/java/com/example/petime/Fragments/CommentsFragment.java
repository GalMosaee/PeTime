package com.example.petime.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.petime.Adapters.CommentsAdapter;
import com.example.petime.Model.Comment;
import com.example.petime.Model.Model;
import com.example.petime.R;

import java.util.ArrayList;
import java.util.List;

public class CommentsFragment extends Fragment {

    EditText addComment;
    TextView post;
    String postid, publisherid;
    private RecyclerView recyclerView;
    CommentsAdapter commentAdapter;
    List<Comment> commentList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_comments, container, false);

        SharedPreferences prefs = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        postid = prefs.getString("postid", "none");
        publisherid = prefs.getString("publisherid", "none");

        addComment = view.findViewById(R.id.add_comment);
        post = view.findViewById(R.id.post);

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (addComment.getText().toString().equals("")) {
                    Toast.makeText(getActivity(), "You can't send empty comment", Toast.LENGTH_SHORT).show();
                } else {
                    Model.instance.addComment(new Comment(postid, Model.instance.getCurrentUser().getUid(), Model.instance.getCurrentUser().getDisplayName(),
                            addComment.getText().toString()),new Model.AddCommentListener() {
                        @Override
                        public void onComplete(boolean success) {
                            Toast.makeText(getActivity(), "Comment was added", Toast.LENGTH_SHORT).show();
                            //getActivity().getSupportFragmentManager().popBackStack();
                        }
                    });
                }
            }
        });

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);

        commentAdapter = new CommentsAdapter(commentList, getActivity());
        recyclerView.setAdapter(commentAdapter);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Model.instance.getCommentsByPostId(postid, new Model.GetAllCommentsListener() {
            @Override
            public void onComplete(List<Comment> data) {
                if (data != null){

                    for(Comment comment: data){
                        Log.d("TAG",comment.toString());
                    }
                    commentList = data;
                    commentAdapter = new CommentsAdapter(commentList, getActivity());
                    recyclerView.setAdapter(commentAdapter);
                    commentAdapter.notifyDataSetChanged();
                }
            }
        });

        getViewLifecycleOwner();
    }
}