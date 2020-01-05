package com.example.petime.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.petime.Fragments.CommentsFragment;
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

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    public List<Post> allPost;
    private FragmentActivity myContext;

    // Provide a suitable constructor
    public PostAdapter(List<Post> postList, Activity activity) {
        allPost = postList;
        myContext=(FragmentActivity) activity;

        Log.d(TAG, "PostAdapter:created");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.post_item, parent,false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        // - get element from list at this position
        // - replace the contents of the view with that element
        final Post post = allPost.get(position);
        holder.bind(post);

        holder.username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                editor.putString("profileid", post.getUserId());
                editor.putString("displayname", post.getUserName());
                editor.apply();

                myContext.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).addToBackStack("tag").commit();
                //myContext.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
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

        TextView username,description,comments, lastUpdate;
        ImageView image;
        ProgressBar postProgress;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username_tv);
            description = itemView.findViewById(R.id.description_tv);
            image = itemView.findViewById(R.id.image_iv);
            comments = itemView.findViewById(R.id.comments);
            lastUpdate = itemView.findViewById(R.id.lastUpdate_tv);
            postProgress = itemView.findViewById(R.id.post_progress);
        }

        public void bind(final Post post) {

            if(post==null || post.getLastUpdate() == null) return;

            Model.instance.getPost(post.getId(), new Model.GetPostListener() {
                @Override
                public void onComplete(final Post post) {
                    Date temp = new Date(post.getLastUpdate().getSeconds() * 1000);

                    if (post.getText().equals("")){
                        description.setVisibility(View.GONE);
                    } else {
                        description.setVisibility(View.VISIBLE);
                        description.setText(post.getText());
                    }

                    username.setText(post.getUserName());
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
                }
            });

            Model.instance.getCommentsByPostId(post.getId(), new Model.GetAllCommentsListener() {
                @Override
                public void onComplete(List<Comment> data) {
                    comments.setText("view all " + data.size()+ " comments...");
                }
            });
        }
    }
}
