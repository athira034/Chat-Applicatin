package com.example.chatapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity{
private Toolbar mToolbar;
private ViewPager myViewPager;
private TabLayout myTabLayout;
private TabAccessorAdapter myTabAccessorAdapter;
private FirebaseAuth mAuth;
private DatabaseReference RootRef;
private  String currentUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        mToolbar=(Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Talk n Dock");

        mAuth = FirebaseAuth.getInstance();
        RootRef= FirebaseDatabase.getInstance().getReference();

        myViewPager=(ViewPager) findViewById(R.id.main_tabs_pager);
        myTabAccessorAdapter=new TabAccessorAdapter(getSupportFragmentManager());
        myViewPager.setAdapter(myTabAccessorAdapter);

        myTabLayout=(TabLayout) findViewById(R.id.main_tabs);
        myTabLayout.setupWithViewPager(myViewPager);



    }

    @Override

    protected void onStart()
    {
        super.onStart();

        FirebaseUser  currentUser=mAuth.getCurrentUser();

        if (currentUser == null)
        {
            SendUserToLoginActivity();
        }
        else
        {
            updateUserStatus("online");
            VerrifyUserExistance();
        }
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        FirebaseUser  currentUser=mAuth.getCurrentUser();

        if (currentUser != null)
        {
            //SendUserToLoginActivity();
            updateUserStatus("offline");
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        FirebaseUser  currentUser=mAuth.getCurrentUser();


        if (currentUser != null)
        {
            //SendUserToLoginActivity();
            updateUserStatus("offline");
        }

    }


    private void VerrifyUserExistance()
    {
        final String currentUserID=mAuth.getCurrentUser().getUid();
        RootRef.child("Users").child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if((snapshot.child("name").exists()))
                {
                    Toast.makeText(MainActivity.this, "Welcome", Toast.LENGTH_SHORT).show();
                }

                else
                {
                    SendUserToSettingsActivity();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
         super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.options_menu,menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
         super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.main_logout_option)
        {


            updateUserStatus("offline");

          mAuth.signOut();
          SendUserToLoginActivity();
        }

        if (item.getItemId() == R.id.main_setting_option)
        {
           SendUserToSettingsActivity();
        }

        if (item.getItemId() == R.id.main_create_group_option)
        {
             RequestNewGroup();
        }

        if (item.getItemId() == R.id.main_create_event_option)
        {
            RequestNewEvent();
        }

        if (item.getItemId() == R.id.main_find_friends_option)
        {
          SendUserToFindFriendsActivity();
        }

        return true;
    }

    private void RequestNewEvent()
    {
        AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this,R.style.AlertDialog);
        builder.setTitle("Enter new Event : ");
        final EditText eventNameField= new EditText(MainActivity.this);
        eventNameField.setHint("E.g Project Assignments..!");
        builder.setView(eventNameField);

        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                   String eventName=eventNameField.getText().toString();
                   if(TextUtils.isEmpty(eventName))
                   {
                       Toast.makeText(MainActivity.this, "Please Enter your Event..!", Toast.LENGTH_SHORT).show();
                   }
                   else
                   {
                       CreateNewEvent(eventName);
                   }
            }
        });


        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
              dialogInterface.cancel();
            }
        });
        builder.show();
    }

    private void CreateNewEvent(final String eventName)
    {
        RootRef.child("Events").child(eventName).setValue("")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                      if(task.isSuccessful())
                      {
                          Toast.makeText(MainActivity.this, eventName+" is created Successfully..!", Toast.LENGTH_SHORT).show();
                      }
                    }
                });
    }

    private void RequestNewGroup()
    {
        AlertDialog.Builder builder= new AlertDialog.Builder(MainActivity.this,R.style.AlertDialog);
        builder.setTitle("Enter the Group Name : ");
         final EditText groupNameField= new EditText(MainActivity.this);
         groupNameField.setHint("E.g  Offical Group");
         builder.setView(groupNameField);

         builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
             @Override
             public void onClick(DialogInterface dialogInterface, int i)
             {
                 String groupName=groupNameField.getText().toString();
                 if(TextUtils.isEmpty(groupName))
                 {
                     Toast.makeText(MainActivity.this, "Please Enter your Group Name!", Toast.LENGTH_SHORT).show();
                 }
                 else
                 {
                   CreateNewGroup(groupName);
                 }
             }
         });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                  dialogInterface.cancel();
            }
        });

        builder.show();
    }


    private void CreateNewGroup(final String groupName)
    {
        RootRef.child("Groups").child(groupName).setValue("")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(MainActivity.this, groupName+" group is created Successfully..!", Toast.LENGTH_SHORT).show();
                        }
                        
                    }
                });
    }


    private void SendUserToLoginActivity()
    {
        Intent loginIntent=new Intent(MainActivity.this,LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);

    }

    private void SendUserToSettingsActivity()
    {
        Intent settingsIntent=new Intent(MainActivity.this,SettingsActivity.class);
        startActivity(settingsIntent);

    }

    private void SendUserToFindFriendsActivity()
    {
        Intent findfriendsIntent=new Intent(MainActivity.this,FindFriendsActivity.class);
        startActivity(findfriendsIntent);

    }


    private void updateUserStatus(String state)
    {
        String saveCurrentTime, saveCurrentDate;

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        HashMap<String, Object> onlineStateMap = new HashMap<>();
        onlineStateMap.put("time", saveCurrentTime);
        onlineStateMap.put("date", saveCurrentDate);
        onlineStateMap.put("state", state);

        currentUserID=mAuth.getCurrentUser().getUid();

        RootRef.child("Users").child(currentUserID).child("userState")
                .updateChildren(onlineStateMap);

    }

}