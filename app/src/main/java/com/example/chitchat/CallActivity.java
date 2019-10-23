package com.example.chitchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.SinchClientListener;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallClient;
import com.sinch.android.rtc.calling.CallClientListener;
import com.sinch.android.rtc.calling.CallListener;

import java.util.ArrayList;
import java.util.List;

public class CallActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    SinchClient sinchClient;
    Call call;
    ArrayList<Users> userArrayList;
    DatabaseReference reference;
    private String mChatUser;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);
        mChatUser = getIntent().getStringExtra("user_id");

        mToolbar = (Toolbar) findViewById(R.id.call_bar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Call");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        recyclerView = findViewById(R.id.call_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        reference = FirebaseDatabase.getInstance().getReference().child("Users").child(mChatUser);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        sinchClient = Sinch.getSinchClientBuilder()
                .context(this)
                .userId(mUser.getUid())
                .applicationKey("a49b74ed-62e1-4ef0-9bf4-159aae9aab55")
                .applicationSecret("ijoVVDTWykWLU3gbb/awbQ==")
                .environmentHost("clientapi.sinch.com")
                .build();

        sinchClient.setSupportCalling(true);
        sinchClient.startListeningOnActiveConnection();
        sinchClient.getCallClient().addCallClientListener(new SinchCallClientListener(){

        });
        sinchClient.start();
        userArrayList = new ArrayList<Users>();
        fetchAllUsers();

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.RECORD_AUDIO,Manifest.permission.READ_PHONE_STATE,Manifest.permission.MODIFY_AUDIO_SETTINGS,Manifest.permission.READ_PHONE_STATE,Manifest.permission.ACCESS_NETWORK_STATE},
                1);

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {


                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {

                    Toast.makeText(CallActivity.this, "Permission denied to Make Calls", Toast.LENGTH_SHORT).show();
                }
                return;
            }


        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void fetchAllUsers() {
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                userArrayList.clear();

                    Users user = dataSnapshot.getValue(Users.class);
                    userArrayList.add(user);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(CallActivity.this,"Error",Toast.LENGTH_LONG).show();

            }
        });

        AllUsersAdapter adapter = new AllUsersAdapter(CallActivity.this,userArrayList);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
    private  class AllUsersAdapter extends RecyclerView.Adapter<AllUsersAdapter.AllUserViewHolder>{

        Activity context;
        ArrayList<Users> usersArrayList;

        public AllUsersAdapter(Activity context,ArrayList<Users> usersArrayList){
            this.context = context;
            this.usersArrayList = usersArrayList;
        }
        @NonNull
        @Override
        public AllUsersAdapter.AllUserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_users,parent,false);
            AllUserViewHolder allUsersAdapter =   new AllUserViewHolder(view);
            return allUsersAdapter;
        }

        @Override
        public void onBindViewHolder(@NonNull AllUsersAdapter.AllUserViewHolder holder, int position) {
            Users user = userArrayList.get(position);
            holder.textView.setText(user.getName());
        }

        @Override
        public int getItemCount() {
            return userArrayList.size();
        }

        public class AllUserViewHolder extends RecyclerView.ViewHolder{

            TextView textView;
            Button button;
            public AllUserViewHolder(@NonNull View itemView) {
                super(itemView);
                textView = itemView.findViewById(R.id.item_name);
                button = itemView.findViewById(R.id.callButton);

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Users user = userArrayList.get(getAdapterPosition());
                        if(sinchClient!=null && sinchClient.isStarted())
                        callUser(user);
                        else {
                            Toast.makeText(CallActivity.this,"Please wait Call Client is Starting",Toast.LENGTH_LONG).show();
                        }

                    }
                });
            }


        }
    }
    private class SinchCallListener implements CallListener{

        @Override
        public void onCallProgressing(Call call) {
            Toast.makeText(getApplicationContext(),"Ringing",Toast.LENGTH_LONG).show();


        }

        @Override
        public void onCallEstablished(Call call) {
            setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
            Toast.makeText(getApplicationContext(),"Call Established",Toast.LENGTH_LONG).show();

        }

        @Override
        public void onCallEnded(Call endedCall) {
            setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);
            Toast.makeText(getApplicationContext(),"Call Ended ",Toast.LENGTH_LONG).show();
            call = null;
            endedCall.hangup();



        }

        @Override
        public void onShouldSendPushNotification(Call call, List<PushPair> list) {

        }
    }

    private class SinchCallClientListener implements CallClientListener {
        @Override
        public void onIncomingCall(CallClient callClient, final Call incomingCall) {
            Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            final Ringtone ringtone = RingtoneManager.getRingtone(CallActivity.this,uri);
            ringtone.play();
            AlertDialog alertDialog = new AlertDialog.Builder(CallActivity.this).create();
            alertDialog.setTitle("Calling");
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Reject", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    if(call !=null) {
                        call.hangup();
                    }
                    ringtone.stop();
                }
            });
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Accept", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    call = incomingCall;
                    if(call !=null) {
                        call.answer();

                        call.addCallListener(new SinchCallListener());
                    }
                    Toast.makeText(CallActivity.this,"Call Started",Toast.LENGTH_LONG).show();
                    ringtone.stop();
                }
            });
            alertDialog.show();

        }
    }

    public void callUser(Users user){
        if(call == null){

            call = sinchClient.getCallClient().callUser(mChatUser);
            call.addCallListener(new SinchCallListener());

            openCallerDialog(call);
        }

    }

    private void openCallerDialog(final Call call) {
        AlertDialog alertDialog = new AlertDialog.Builder(CallActivity.this).create();
        alertDialog.setTitle("ALERT");
        alertDialog.setMessage("Calling");
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "HANGUP", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                call.hangup();
            }
        });

        alertDialog.show();
    }
}
