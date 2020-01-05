package com.example.petime.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.petime.Model.Model;
import com.example.petime.R;

public class EditPostFragment extends Fragment {

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_edit, container, false);

        //ImageView image_profile = view.findViewById(R.id.image_profile);
        final TextView description = view.findViewById(R.id.description);
        TextView update = view.findViewById(R.id.update);

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences prefs = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
                String postid = prefs.getString("postid", "none");
                Model.instance.editPost(postid, description.getText().toString(), new Model.EditPostListener() {
                    @Override
                    public void onComplete(boolean success) {
                        Toast.makeText(getActivity(), "post was updated", Toast.LENGTH_SHORT).show();
                        getActivity().getSupportFragmentManager().popBackStack();
                    }
                });
            }
        });

        return view;
    }
}
