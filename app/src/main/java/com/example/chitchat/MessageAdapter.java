package com.example.chitchat;

import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.chitchat.Model.Messages;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

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
        public ImageButton playButton;
        public RelativeLayout relativeLayout;
        public LinearLayout linearLayout;

        public MessageViewHolder(View view) {
            super(view);

            messageText = (TextView) view.findViewById(R.id.message_text_layout);
            profileImage = (CircleImageView) view.findViewById(R.id.message_profile_layout);
            displayName = (TextView) view.findViewById(R.id.name_text_layout);
            messageImage = (ImageView) view.findViewById(R.id.message_image_layout);
            timeSent = view.findViewById(R.id.time_text_layout);
            playButton = (ImageButton) view.findViewById(R.id.message_btn_voice);
            relativeLayout = view.findViewById(R.id.message_single_layout);


        }


    }

    public static void setMargins (View v, int l, int t, int r, int b) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(l, t, r, b);
            v.requestLayout();
        }
    }
    @Override
    public void onBindViewHolder(final MessageViewHolder viewHolder, int i) {

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String current_user_id = mAuth.getCurrentUser().getUid();
        final Messages c = mMessageList.get(i);


        String from_user = c.getFrom();
        final String message_type = c.getType();

        if(from_user.equals(current_user_id)) {
            viewHolder.messageText.setBackgroundResource(R.drawable.my_message);
            viewHolder.messageText.setTextColor(Color.WHITE);
            viewHolder.relativeLayout.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);





        }else
        {
            viewHolder.messageText.setBackgroundResource(R.drawable.their_message);
            viewHolder.messageText.setTextColor(Color.parseColor("#154AAD"));
            viewHolder.relativeLayout.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);








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
            viewHolder.playButton.setVisibility(View.INVISIBLE);


        }
        else if(message_type.equals("voice")){
            DateFormat df = new SimpleDateFormat("HH:mm", Locale.US);
            viewHolder.timeSent.setText(df.format(c.getTime()));
            viewHolder.messageText.setText("Voice Message");
            viewHolder.playButton.setVisibility(View.VISIBLE);
            viewHolder.messageImage.setVisibility(View.INVISIBLE);

        }
        else {

            viewHolder.messageText.setVisibility(View.INVISIBLE);
            Picasso.get().load(c.getMessage())
                    .placeholder(R.drawable.default_avatar).into(viewHolder.messageImage);
            DateFormat df = new SimpleDateFormat("HH:mm", Locale.US);
            viewHolder.playButton.setVisibility(View.INVISIBLE);
            viewHolder.timeSent.setText(df.format(c.getTime()));


        }

        //added
        viewHolder.messageText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(message_type.equals("document")){ //changes
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(c.getMessage()));
                    v.getContext().startActivity(browserIntent);
                }

            }


        });

        viewHolder.playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                play_audio();
            }

            private void play_audio(){
                MediaPlayer mediaPlayer = new MediaPlayer();
                try {
                    mediaPlayer.setDataSource(c.getMessage());
                    mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            mp.start();
                        }
                    });
                    mediaPlayer.prepare();
                }

                catch (IOException  e){
                    e.printStackTrace();
                }

            }

        });

    }


    @Override
    public int getItemCount() {
        return mMessageList.size();
    }





}
