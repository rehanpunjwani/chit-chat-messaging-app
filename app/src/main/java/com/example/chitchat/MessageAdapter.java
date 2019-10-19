package com.example.chitchat;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<Messages> mMessageList;
    private DatabaseReference mUserDatabase;

    public MessageAdapter(List<Messages> mMessageList) {

        this.mMessageList = mMessageList;

    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {



        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_single_layout ,parent, false);

        return new MessageViewHolder(v);

    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {

        public TextView messageText;
        public CircleImageView profileImage;
        public TextView displayName;
        public ImageView messageImage;
        public TextView timeSent;
        public RelativeLayout relativeLayout;
        public LinearLayout linearLayout;

        public MessageViewHolder(View view) {
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
    public void onBindViewHolder(final MessageViewHolder viewHolder, int i) {

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String current_user_id = mAuth.getCurrentUser().getUid();
        Messages c = mMessageList.get(i);

        String from_user = c.getFrom();
        String message_type = c.getType();

        if(from_user.equals(current_user_id)) {
            viewHolder.messageText.setBackgroundResource(R.drawable.my_message);
            viewHolder.messageText.setTextColor(Color.WHITE);
            viewHolder.relativeLayout.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

            viewHolder.profileImage.setVisibility(View.INVISIBLE);
            viewHolder.displayName.setVisibility(View.INVISIBLE);




        }else
        {
            viewHolder.messageText.setBackgroundResource(R.drawable.their_message);
            viewHolder.messageText.setTextColor(Color.parseColor("#7213B6"));
            viewHolder.relativeLayout.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
            viewHolder.displayName.setVisibility(View.VISIBLE);
            viewHolder.profileImage.setVisibility(View.VISIBLE);






        }

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(from_user);

        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("name").getValue().toString();
                String image = dataSnapshot.child("thumb_image").getValue().toString();

                viewHolder.displayName.setText(name);


                Picasso.get().load(image)
                        .placeholder(R.drawable.default_avatar).into(viewHolder.profileImage);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        if(message_type.equals("text")  || message_type.equals("document")) {
            DateFormat df = new SimpleDateFormat("HH:mm", Locale.US);
            viewHolder.timeSent.setText(df.format(c.getTime()));
            viewHolder.messageText.setText(c.getMessage());
            viewHolder.messageImage.setVisibility(View.INVISIBLE);


        } else {

            viewHolder.messageText.setVisibility(View.INVISIBLE);
            Picasso.get().load(c.getMessage())
                    .placeholder(R.drawable.default_avatar).into(viewHolder.messageImage);
            DateFormat df = new SimpleDateFormat("HH:mm", Locale.US);
            viewHolder.timeSent.setText(df.format(c.getTime()));
            //  viewHolder.messageText.setText(c.getMessage());
            //viewHolder.messageImage.setVisibility(View.INVISIBLE);

        }

    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }




}
