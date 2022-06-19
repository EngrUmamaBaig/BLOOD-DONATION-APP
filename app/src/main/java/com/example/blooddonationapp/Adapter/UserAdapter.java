package com.example.blooddonationapp.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.blooddonationapp.Model.User;
import com.example.blooddonationapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder>{

    private Context context;
    private List<User>  userList;

    public UserAdapter(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_displayed_layout,parent,
                false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final User user = userList.get(position);
        holder.type.setText(user.getType());

        if(user.getType().equals("donor") || user.getType().equals("recipient") ){
         holder.emailNow.setVisibility(View.VISIBLE);
        }

        holder.userEmail.setText(user.getEmail());
        holder.phoneNumber.setText(user.getPhonenumber());
        holder.userName.setText(user.getName());
        holder.bloodGroup.setText(user.getBloodgroup());

        Glide.with(context).load(user.getProfilepictureurl()).into(holder.userProfileImage);

        final String nameofTheReceiver = user.getName();
        final String idOfTheReciver = user.getIdnumber();
        //sending email
        holder.emailNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context).setTitle("SEND EMAIL")
                        .setMessage("Send email to "+user.getName() + "?")
                        .setCancelable(false).setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users")
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        reference.addValueEventListener(new ValueEventListener() {
                            private Object Context;

                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String nameOfSender = snapshot.child("name").getValue().toString();
                                String email = snapshot.child("email").getValue().toString();
                                String phone = snapshot.child("phonenumber").getValue().toString();
                                String blood = snapshot.child("bloodgroup").getValue().toString();

                                String mEmail = user.getEmail();
                                String mSubject = "BLOOD DONATION";
                                String mMessage = "HELLO" +nameOfSender+ "Would you like to blood donation from you" +
                                        ". Here's his/her details: \n" +"Name: "+nameOfSender+"\n"+
                                        "Phone Number: " +phone+"\n"+
                                        "Email: "+email+"\n"+"Kindly Reach out to him/her. Thank you!\n"+
                                        "BLOOD DONATION APP - DONATE BLOOD, SAVE LIVES";

                                //JavaMailApi javaMailApi = new JavaMailApi(context,mEmail,mSubject,mMessage);
                               // javaMailApi.execute();
                                DatabaseReference receiverRef = FirebaseDatabase.getInstance().getReference("emails")
                                        .child(idOfTheReciver);
                                receiverRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);


                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                })
                        .setNegativeButton("NO",null)
                        .show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public CircleImageView userProfileImage;
        public TextView type, userName, userEmail, phoneNumber, bloodGroup;
        public Button emailNow;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            userProfileImage = itemView.findViewById(R.id.userProfileImage);
            type = itemView.findViewById(R.id.type);
            userName = itemView.findViewById(R.id.userName);
            userEmail = itemView.findViewById(R.id.userEmail);
            phoneNumber = itemView.findViewById(R.id.phoneNumber);
            bloodGroup = itemView.findViewById(R.id.bloodGroup);
            emailNow = itemView.findViewById(R.id.emailNow);

        }
    }
}
