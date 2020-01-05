package com.example.petime.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.petime.Fragments.CommentsFragment;
import com.example.petime.Fragments.EditPostFragment;
import com.example.petime.Fragments.ProfileFragment;
import com.example.petime.Model.Comment;
import com.example.petime.Model.Model;
import com.example.petime.Model.Post;
import com.example.petime.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.Date;
import java.util.List;

import static androidx.constraintlayout.widget.Constraints.TAG;
import static com.example.petime.PeTimeApp.getContext;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ViewHolder> {

    public List<Post> allPost;
    private String profileid;
    private FragmentActivity myContext;

    // Provide a suitable constructor
    public ProfileAdapter(List<Post> postList, Activity activity) {
        allPost = postList;
        myContext=(FragmentActivity) activity;

        Log.d(TAG, "ProfileAdapter:created");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.profile_post_item, parent,false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        // - get element from list at this position
        // - replace the contents of the view with that element
        final Post post = allPost.get(position);
        holder.bind(post);

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Model.instance.deletePost(post.getId(), new Model.DeletePostListener() {
                    @Override
                    public void onComplete(boolean success) {
                        Log.d("TAG", "post deleted");
                        Toast.makeText(myContext, "post has been deleted", Toast.LENGTH_SHORT).show();
                        myContext.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).addToBackStack("delete").commit();
                    }
                });
            }
        });

        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                editor.putString("postid", post.getId());
                editor.apply();
                myContext.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new EditPostFragment()).addToBackStack("edit").commit();
            }
        });

        holder.comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                editor.putString("postid", post.getId());
                editor.putString("publisherid", post.getUserId());
                editor.apply();
                myContext.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new CommentsFragment()).addToBackStack("comment").commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return allPost.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView description,comments, lastUpdate;
        ImageView image;
        ProgressBar postProgress;
        Button edit, delete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            description = itemView.findViewById(R.id.description_tv);
            image = itemView.findViewById(R.id.image_iv);
            comments = itemView.findViewById(R.id.comments);
            lastUpdate = itemView.findViewById(R.id.lastUpdate_tv);
            postProgress = itemView.findViewById(R.id.post_progress);
            edit = itemView.findViewById(R.id.edit_post_bt);
            delete = itemView.findViewById(R.id.delete_post_bt);
        }

        public void bind(final Post post) {

            if (post == null) return;

            Date temp = new Date(post.getLastUpdate().getSeconds() * 1000);

            if (post.getText().equals("")){
                description.setVisibility(View.GONE);
            } else {
                description.setVisibility(View.VISIBLE);
                description.setText(post.getText());
            }

            image.setTag(post.getId());
            //image.setImageResource(R.drawable.ic_menu_gallery);
            if(post.getImage() != null){
                Picasso.get().setIndicatorsEnabled(true);
                Target target = new Target(){
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        if (image.getTag() == post.getId()) {
                            image.setImageBitmap(bitmap);
                            postProgress.setVisibility(View.INVISIBLE);
                        }
                    }

                    @Override
                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                        postProgress.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {
                        postProgress.setVisibility(View.VISIBLE);
                    }
                };

                Picasso.get().load(post.getImage())
                        .placeholder(R.drawable.ic_menu_gallery)
                        .into(target);
            } else{
                postProgress.setVisibility(View.INVISIBLE);
            }
            //image.setImageURI(Uri.parse(post.getImage()));
            lastUpdate.setText(temp.toString());

            SharedPreferences prefs = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
            profileid = prefs.getString("profileid", "none");

            if (profileid.equals(Model.instance.getCurrentUser().getUid())) {
                edit.setVisibility(View.VISIBLE);
                delete.setVisibility(View.VISIBLE);
            }

            Model.instance.getCommentsByPostId(post.getId(), new Model.GetAllCommentsListener() {
                @Override
                public void onComplete(List<Comment> data) {
                    comments.setText("view all " + data.size()+ " comments...");
                }
            });
        }
    }
}

