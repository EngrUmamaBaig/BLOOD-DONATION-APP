package com.example.blooddonationapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.blooddonationapp.Adapter.UserAdapter;
import com.example.blooddonationapp.JavaMailApi.SendMailActivity;
import com.example.blooddonationapp.Model.User;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private NavigationView nav_view;
    private ActionBarDrawerToggle toggle;
    private CircleImageView nav_profile_image;
    private TextView nav_fullname, nav_email,nav_bloodgroup,nav_type;

    private RecyclerView recyclerView;
    private ProgressBar progressBar;

    private List<User> userList;
    private UserAdapter userAdapter;

    private DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("        BLOOD DONATION APP");

        drawerLayout = findViewById(R.id.drawerLayout);
        nav_view = findViewById(R.id.nav_view);

        toggle = new ActionBarDrawerToggle(MainActivity.this,drawerLayout,
                toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        nav_view.setNavigationItemSelectedListener(this);

        progressBar = findViewById(R.id.progressbar);
        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        userList = new ArrayList<>();
        userAdapter = new UserAdapter(MainActivity.this,userList);

        recyclerView.setAdapter(userAdapter);

        DatabaseReference reff = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        reff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String typee = snapshot.child("type").getValue().toString();
                if(typee.equals("donor")){
                    readRecipients();
                }
                else {
                   // readDonors();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        nav_profile_image = nav_view.getHeaderView(0).findViewById(R.id.nav_user_image);
        nav_fullname = nav_view.getHeaderView(0).findViewById(R.id.nav_user_fullname);
        nav_email = nav_view.getHeaderView(0).findViewById(R.id.nav_user_email);
        nav_bloodgroup = nav_view.getHeaderView(0).findViewById(R.id.nav_user_bloodgroup);
        nav_type = nav_view.getHeaderView(0).findViewById(R.id.nav_user_type);

        userRef = FirebaseDatabase.getInstance().getReference().child("users").
                child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){

                    String name = snapshot.child("name").getValue().toString();
                    nav_fullname.setText(name);

                    String email = snapshot.child("email").getValue().toString();
                    nav_email.setText(email);

                    String bloodgroup = snapshot.child("bloodgroup").getValue().toString();
                    nav_bloodgroup.setText(bloodgroup);

                    String type = snapshot.child("type").getValue().toString();
                    nav_type.setText(type);

                    if(snapshot.hasChild("profilepictureurl")){
                        String imageUrl = snapshot.child("profilepictureurl").getValue().toString();
                        Glide.with(getApplicationContext()).load(imageUrl).into(nav_profile_image);
                    }
                    else {
                        nav_profile_image.setImageResource(R.drawable.profile_image);
                    }

                     }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
    private void readRecipients() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users");
        Query query = reference.orderByChild("type").equalTo("recipient");
         query.addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot snapshot) {
                 userList.clear();
                 for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                     User user = dataSnapshot.getValue(User.class);
                     userList.add(user);
                 }
                 userAdapter.notifyDataSetChanged();
                 progressBar.setVisibility(View.GONE);

                 if(userList.isEmpty())
                 {
                     Toast.makeText(MainActivity.this, "NO RECIPIENTS", Toast.LENGTH_SHORT).show();
                     progressBar.setVisibility(View.GONE);
                 }
             }
             @Override
             public void onCancelled(@NonNull DatabaseError error) {

             }
         });
    }




    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.aplus:
                Intent intent1 = new Intent(MainActivity.this,CategorySelectedActivity.class);
                intent1.putExtra("group","A+");
                startActivity(intent1);
                break;

            case R.id.aminus:
                Intent intent2 = new Intent(MainActivity.this,CategorySelectedActivity.class);
                intent2.putExtra("group","A-");
                startActivity(intent2);
                break;

            case R.id.bplus:
                Intent intent3 = new Intent(MainActivity.this,CategorySelectedActivity.class);
                intent3.putExtra("group","B+");
                startActivity(intent3);
                break;

            case R.id.bminus:
                Intent intent4 = new Intent(MainActivity.this,CategorySelectedActivity.class);
                intent4.putExtra("group","B-");
                startActivity(intent4);
                break;

            case R.id.abplus:
                Intent intent5 = new Intent(MainActivity.this,CategorySelectedActivity.class);
                intent5.putExtra("group","AB+");
                startActivity(intent5);
                break;

            case R.id.abminus:
                Intent intent6 = new Intent(MainActivity.this,CategorySelectedActivity.class);
                intent6.putExtra("group","AB-");
                startActivity(intent6);
                break;

            case R.id.oplus:
                Intent intent7 = new Intent(MainActivity.this,CategorySelectedActivity.class);
                intent7.putExtra("group","  O+");
                startActivity(intent7);
                break;


            case R.id.ominus:
                Intent intent8 = new Intent(MainActivity.this,CategorySelectedActivity.class);
                intent8.putExtra("group","O-");
                startActivity(intent8);
                break;

            case R.id.compatible:
                Intent intent9 = new Intent(MainActivity.this,CategorySelectedActivity.class);
                intent9.putExtra("group","Compatible With Me");
                startActivity(intent9);
                break;

            case R.id.sentEmail:
                Intent intent10 = new Intent(MainActivity.this, SendMailActivity.class);
                startActivity(intent10);
                break;

            case R.id.proflie:
                Intent intent = new Intent(MainActivity.this,ProfileActivity.class);
                startActivity(intent);
                break;
        }

        if (item.getItemId() == R.id.logout) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

}