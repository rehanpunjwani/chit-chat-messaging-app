package com.example.chitchat;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class RequestFragment extends Fragment {



    private RecyclerView mReqList;
    TextView text;


    private DatabaseReference mReqDatabase;
    private DatabaseReference mUsersDatabase;

    private FirebaseAuth mAuth;

    private String mCurrent_user_id;
    private Button mAcceptBtn;
    private Button mDeclineBtn;
    private DatabaseReference mRootRef;
    private View mMainView;
    private RelativeLayout mRequestLayout;


    public RequestFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mMainView = inflater.inflate(R.layout.fragment_request, container, false);


        mReqList = (RecyclerView) mMainView.findViewById(R.id.req_list);
        mRootRef =FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        if(mAuth.getCurrentUser() !=null)
        mCurrent_user_id = mAuth.getCurrentUser().getUid();

        if(mCurrent_user_id !=null) {
            mReqDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_req").child(mCurrent_user_id);
            mReqDatabase.keepSynced(true);

        }

         text = (TextView) mMainView.findViewById(R.id.request_fragment_text);


        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mUsersDatabase.keepSynced(true);
//        mReqDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_req").child(mCurrent_user_id);
//
//        mReqDatabase.keepSynced(true);
//        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
//        mUsersDatabase.keepSynced(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        mReqList.setHasFixedSize(true);
        mReqList.setLayoutManager(linearLayoutManager);



        // Inflate the layout for this fragment
        return mMainView;
    }

    @Override
    public void onStart() {
        super.onStart();



    if(mReqDatabase != null) {

        final Query requestQuery = mReqDatabase.orderByChild("request_type");


        FirebaseRecyclerAdapter<Request, RequestFragment.ReqViewHolder> firebaseReqAdapter = new FirebaseRecyclerAdapter<Request, RequestFragment.ReqViewHolder>(
                Request.class,
                R.layout.single_request_layout,
                ReqViewHolder.class,
                requestQuery


        ) {

            @Override
            protected void onDataChanged() {
                if(getItemCount() == 0){
                    text.setText("No New Friend Request");
                }
                else{
                    text.setText("");
                }
            }

            @Override
            protected void populateViewHolder(final RequestFragment.ReqViewHolder reqViewHolder, final Request req, int i) {


                final String list_user_id = getRef(i).getKey();

                mAcceptBtn = reqViewHolder.mView.findViewById(R.id.single_req_accept_btn2);
                mDeclineBtn = reqViewHolder.mView.findViewById(R.id.single_req_decline_btn);

                if (req.getRequest_type().equals("received")) {


                    mUsersDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                                    text.setText("");
                            final String userName = dataSnapshot.child("name").getValue().toString();
                            String userThumb = dataSnapshot.child("thumb_image").getValue().toString();


                            if (dataSnapshot.hasChild("online")) {

                                String userOnline = dataSnapshot.child("online").getValue().toString();
                                reqViewHolder.setUserOnline(userOnline);

                            }

                            reqViewHolder.setName(userName);
                            reqViewHolder.setUserImage(userThumb, getContext());

                            mAcceptBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

//
//                                    Intent chatIntent = new Intent(getContext(), UsersActivity.class);
//                                    chatIntent.putExtra("user_id", list_user_id);
//                                    chatIntent.putExtra("user_name", userName);
//                                    //startActivity(chatIntent);

                                    final String currentDate = DateFormat.getDateTimeInstance().format(new Date());

                                    Map friendsMap = new HashMap();
                                    friendsMap.put("Friends/" + mCurrent_user_id + "/" + list_user_id + "/date", currentDate);
                                    friendsMap.put("Friends/" + list_user_id + "/" + mCurrent_user_id + "/date", currentDate);


                                    friendsMap.put("Friend_req/" + mCurrent_user_id + "/" + list_user_id, null);
                                    friendsMap.put("Friend_req/" + list_user_id + "/" + mCurrent_user_id, null);


                                    mRootRef.updateChildren(friendsMap, new DatabaseReference.CompletionListener() {
                                        @Override
                                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {


                                            if (databaseError == null) {

                                                reqViewHolder.mView.setVisibility(View.INVISIBLE);


                                            } else {

                                                String error = databaseError.getMessage();

                                                Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();


                                            }

                                        }
                                    });

                                }
                            });
                            mDeclineBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {


                                    final String currentDate = DateFormat.getDateTimeInstance().format(new Date());

                                    Map friendsMap = new HashMap();

                                    friendsMap.put("Friend_req/" + mCurrent_user_id + "/" + list_user_id, null);
                                    friendsMap.put("Friend_req/" + list_user_id + "/" + mCurrent_user_id, null);


                                    mRootRef.updateChildren(friendsMap, new DatabaseReference.CompletionListener() {
                                        @Override
                                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {


                                            if (databaseError == null) {

                                                reqViewHolder.mView.setVisibility(View.INVISIBLE);


                                            } else {

                                                String error = databaseError.getMessage();

                                                Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();


                                            }

                                        }
                                    });

                                }
                            });


                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                } else {

                    reqViewHolder.Invisible();


                }


            }
        };



        mReqList.setAdapter(firebaseReqAdapter);
    }



    }

    public static class ReqViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public ReqViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

        }


        public void setStatus(String status) {
           TextView fragmentStatus = mView.findViewById(R.id.request_fragment_text);
           fragmentStatus.setText(status);

        }

        public void setName(String name){

            TextView userNameView = (TextView) mView.findViewById(R.id.req_single_name);
            userNameView.setText(name);

        }

        public void setUserImage(String thumb_image, Context ctx){

            CircleImageView userImageView = (CircleImageView) mView.findViewById(R.id.req_single_image);
            Picasso.get().load(thumb_image).placeholder(R.drawable.default_avatar).into(userImageView);

        }


        public void setUserOnline(String online_status) {

            ImageView userOnlineView = (ImageView) mView.findViewById(R.id.req_single_online_icon);

            if(online_status.equals("true")){

                userOnlineView.setVisibility(View.VISIBLE);

            } else {

                userOnlineView.setVisibility(View.INVISIBLE);

            }

        }
        public void Invisible(){
            mView.setVisibility(View.INVISIBLE);
        }


    }

    }

