package com.example.petime.Adapters;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.petime.Model.Comment;
import com.example.petime.R;

import java.util.Date;
import java.util.List;

import static androidx.constraintlayout.widget.Constraints.TAG;
import static com.example.petime.PeTimeApp.getContext;

public class CommentsAdapter extends  RecyclerView.Adapter<CommentsAdapter.ViewHolder> {
    public List<Comment> comments;
    private Activity myContext;

    // Provide a suitable constructor
    public CommentsAdapter(List<Comment> comments, Activity activity) {
        this.comments = comments;
        myContext = activity;

        Log.d(TAG, "CommentsAdapter: created");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.comment_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        // - get element from list at this position
        // - replace the contents of the view with that element
        final Comment comment = comments.get(position);
        holder.bind(comment);

        /*holder.username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = getContext().getSharedPreferences("PREFS", myContext.MODE_PRIVATE).edit();
                editor.putString("profileid", comment.getUserId());
                editor.putString("displayname", comment.getUserName());
                editor.apply();

                myContext.getFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).addToBackStack("tag").commit();
                //myContext.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
            }
        });
*/
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView username, comment, lastUpdate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username);
            comment = itemView.findViewById(R.id.comment);
            lastUpdate = itemView.findViewById(R.id.lastupdate);
        }

        public void bind(final Comment comment) {
            username.setText(comment.getUserName());
            this.comment.setText(comment.getText());
            Date temp = new Date(comment.getLastUpdate().getSeconds() * 1000);
            lastUpdate.setText(temp.toString());
        }
    }
}

