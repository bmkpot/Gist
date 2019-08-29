package com.obenacademy.gist.Data;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.storage.FirebaseStorage;
import com.obenacademy.gist.Activities.AddPostActivity;
import com.obenacademy.gist.Activities.MainActivity;
import com.obenacademy.gist.Activities.PostListActivity;
import com.obenacademy.gist.Activities.SingleActivity;
import com.obenacademy.gist.Model.Gist;
import com.obenacademy.gist.R;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import static android.support.v4.content.ContextCompat.startActivity;

public class GistRecyclerAdapter extends RecyclerView.Adapter<GistRecyclerAdapter.ViewHolder> {

    private Context context;
    private List<Gist> gistList;
    private FirebaseStorage firebaseStorage;

    public GistRecyclerAdapter(Context context, List<Gist> gistList) {
        this.context = context;
        this.gistList = gistList;
        this.firebaseStorage = firebaseStorage;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.post_row, parent, false);

        return new ViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Gist gist = gistList.get(position);
        String imageUrl = null;

        holder.title.setText(gist.getTitle());
        holder.desc.setText(gist.getDesc());

        DateFormat dateFormat = DateFormat.getDateInstance();
        String formattedDate = dateFormat.format(new Date(Long.valueOf(gist.getTimestamp())).getTime());

        holder.timestamp.setText(formattedDate);

        imageUrl = gist.getImage();

        // Use picasso library to load image

        Picasso.with(context)
                .load(imageUrl)
                .into(holder.image);

    }

    @Override
    public int getItemCount() {
        return gistList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView desc;
        public  TextView timestamp;
        public ImageView image;
        String userid;

        public ViewHolder(@NonNull final View view, final Context ctx) {
            super(view);

            context = ctx;

            title = (TextView) view.findViewById(R.id.postTitleList);
            desc = (TextView) view.findViewById(R.id.postTextList);
            image = (ImageView) view.findViewById(R.id.postImageList);
            timestamp = (TextView) view.findViewById(R.id.timestampList);

            userid = null;

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // go to the SingleActivity
                  //  Intent intent = new Intent(getApplicationContext(), SingleActivity.class);




                }
            });
        }
    }
}
