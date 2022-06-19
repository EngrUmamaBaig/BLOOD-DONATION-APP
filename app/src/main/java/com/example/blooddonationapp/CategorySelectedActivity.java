package com.example.blooddonationapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.blooddonationapp.Adapter.UserAdapter;
import com.example.blooddonationapp.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CategorySelectedActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<User> userList;
    private UserAdapter userAdapter;
    private String title = "";
    public Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_selected);

        Toolbar toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("MY PROFILE");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        userList = new ArrayList<>();
        userAdapter = new UserAdapter(CategorySelectedActivity.this,userList);
        recyclerView.setAdapter(userAdapter);

        if(getIntent().getExtras() != null){
            title = getIntent().getStringExtra("group");

            if(title.equals("Compatible with me")){
                getCompatibleUsers();
                getSupportActionBar().setTitle("Compatible with me");
            }
            else {
                readUsers();
            }

            getSupportActionBar().setTitle("BLOOD GROUP "+title);

            readUsers();
        }
    }

    private void getCompatibleUsers() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String result;
                String type = (String) snapshot.child("type").getValue();
                if(type == "donor")
                {
                    result = "donor";
                }
                else {
                    result = "recipient";
                }

                String bloodgroup = snapshot.child("bloodgroup").getValue().toString();

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users");
                Query query = reference.orderByChild("search").equalTo(result+bloodgroup);
                query.addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        userList.clear();
                        for(DataSnapshot dataSnapshot: snapshot.getChildren())
                        {
                            User user = dataSnapshot.getValue(User.class);
                            userList.add(user);
                        }
                        userAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readUsers() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String result;
                String type = (String) snapshot.child("type").getValue();
                if(type == "donor")
                {
                    result = "donor";
                }
                else {
                    result = "recipient";
                }

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users");
                Query query = reference.orderByChild("search").equalTo(result+title);
                query.addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        userList.clear();
                        for(DataSnapshot dataSnapshot: snapshot.getChildren())
                        {
                            User user = dataSnapshot.getValue(User.class);
                            userList.add(user);
                        }
                        userAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                Intent intent = new Intent(CategorySelectedActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

}