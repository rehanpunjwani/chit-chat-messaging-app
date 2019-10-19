package com.example.chitchat;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.chitchat.Group;
import com.example.chitchat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.GroupViewHolder> {

    private List<Group> mGroupList;


    public GroupAdapter(List<Group> mGroupList) {

        this.mGroupList = mGroupList;

    }

    @Override
    public GroupViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {



        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_single_layout2 ,parent, false);

        return new GroupViewHolder(v);

    }

    public class GroupViewHolder extends RecyclerView.ViewHolder {

        public TextView messageText;
        public CircleImageView profileImage;
        public TextView displayName;
        public ImageView messageImage;
        public TextView timeSent;
        public RelativeLayout relativeLayout;


        public GroupViewHolder(View view) {
            super(view);

            messageText = (TextView) view.findViewById(R.id.message_text_layout);
            profileImage = (CircleImageView) view.findViewById(R.id.message_profile_layout);
            displayName = (TextView) view.findViewById(R.id.name_text_layout);
            messageImage = (ImageView) view.findViewById(R.id.message_image_layout);
            timeSent = view.findViewById(R.id.time_text_layout);
            relativeLayout = view.findViewById(R.id.message_single_layout);

//        messageText = view.findViewById(R.id.message_text_layout);
//        linearLayout = view.findViewById(R.id.message_single_layout);


        }
    }

    @Override
    public void onBindViewHolder(final GroupViewHolder viewHolder, int i) {

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String current_user_id = mAuth.getCurrentUser().getUid();
        Group c = mGroupList.get(i);
        String message_type = c.getType();




        viewHolder.displayName.setText(c.getName());


//
//                Picasso.get().load()
//                        .placeholder(R.drawable.default_avatar).into(viewHolder.profileImage);


        if(message_type.equals("text")) {
            viewHolder.timeSent.setText(c.getTime());
            viewHolder.messageText.setText(c.getMessage());
            viewHolder.messageImage.setVisibility(View.INVISIBLE);


        } else {

            viewHolder.messageText.setVisibility(View.INVISIBLE);
            Picasso.get().load(c.getMessage())
                    .placeholder(R.drawable.default_avatar).into(viewHolder.messageImage);

            viewHolder.timeSent.setText(c.getTime());
            //  viewHolder.messageText.setText(c.getMessage());
            //viewHolder.messageImage.setVisibility(View.INVISIBLE);

        }

    }

    @Override
    public int getItemCount() {
        return mGroupList.size();
    }




}
