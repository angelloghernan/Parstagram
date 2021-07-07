package com.example.parstagram;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.parse.ParseFile;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import javax.security.auth.callback.CallbackHandler;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    Context context;
    List<Post> posts;
    AdapterCallback callback;

    public PostAdapter(Context context, List<Post> posts, AdapterCallback callback) {
        this.context = context;
        this.posts = posts;
        this.callback = callback;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.bind(post);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    // Clean all elements of the recycler
    public void clear() {
        posts.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Post> list) {
        posts.addAll(list);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView ivProfilePicture;
        TextView tvUsername;
        ImageView ivPostImage;
        ImageButton ibLike;
        ImageButton ibReply;
        ImageButton ibDM;
        TextView tvDescriptionUsername;
        TextView tvDescription;
        TextView tvTimestamp;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this::onClick);
            ivProfilePicture = itemView.findViewById(R.id.ivProfilePicture);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            ivPostImage = itemView.findViewById(R.id.ivPostImage);
            ibLike = itemView.findViewById(R.id.ibLike);
            ibReply = itemView.findViewById(R.id.ibReply);
            ibDM = itemView.findViewById(R.id.ibDM);
            tvDescriptionUsername = itemView.findViewById(R.id.tvDescriptionUsername);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
        }

        @Override
        public void onClick(View v) {
            int position = getBindingAdapterPosition();

            if (position != RecyclerView.NO_POSITION) {
                final Post post = posts.get(position);
                callback.onAdapterSelected(position, post);
            }
        }

        public void bind(Post post) {
            ParseUser user = post.getUser();
            ParseFile profilePicture = user.getParseFile("profilePicture");
            if (profilePicture != null) {
                Glide.with(context)
                        .load(profilePicture.getUrl())
                        .circleCrop()
                        .into(ivProfilePicture);
            } else {
                Glide.with(context)
                        .load(ResourcesCompat.getDrawable(context.getResources(),
                                R.drawable.instagram_profile_default, context.getTheme()))
                        .circleCrop()
                        .into(ivProfilePicture);
            }
            if (post.getImage() != null) {
                ivPostImage.setVisibility(View.VISIBLE);
                Glide.with(context)
                        .load(post.getImage().getUrl())
                        .into(ivPostImage);
            } else {
                ivPostImage.setVisibility(View.GONE);
            }
            tvUsername.setText(user.getUsername());
            tvDescription.setText(post.getDescription());
            tvDescriptionUsername.setText(user.getUsername());
            tvTimestamp.setText(Post.calculateTimeAgo(post.getCreatedAt()));
        }
    }

    public interface AdapterCallback {
        void onAdapterSelected(int pos, Post post);
    }
}
