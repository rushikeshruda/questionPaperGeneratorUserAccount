package com.example.rushikesh.qpguseraccount;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;


import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Home extends AppCompatActivity implements View.OnClickListener{

    public static final String USERUID = "UserUId";
    public static final String COURSEID = "CourseId";
    public static final String SUBJECTID = "SubjectId";

    private FirebaseAuth mAuth;
    Spinner spinnerUser,spinnerCourse,spinnerSubject;
    User user;
    Course course;
    Subject subject;
    List<User> userList;
    List<Subject> subjectList;
    List<Course> courseList;
    String userUid;
    String courseId;
    String subjectId;

    ProgressDialog progressDialog;
    Button button;
    DatabaseReference databaseReferenceGetUser,databaseReferenceGetCourse,databaseReferenceGetSubject;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mAuth = FirebaseAuth.getInstance();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        spinnerUser = findViewById(R.id.userName);
        spinnerCourse = findViewById(R.id.spinnerCourse);
        spinnerSubject = findViewById(R.id.spinnerSubject);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCanceledOnTouchOutside(false);

        progressDialog.show();


        courseList = new ArrayList<>();
        userList = new ArrayList<>();
        subjectList = new ArrayList<>();

        findViewById(R.id.buttonDone).setOnClickListener(this);

        databaseReferenceGetUser = FirebaseDatabase.getInstance().getReference().child("admin users");
        progressDialog.show();
        databaseReferenceGetUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                progressDialog.cancel();
                userList.clear();
                for (DataSnapshot userSnapshot: dataSnapshot.getChildren()){
                    User user = userSnapshot.getValue(User.class);
                    userList.add(user);
                }
                UserList adapter = new UserList(Home.this,R.layout.list_layout,userList);
                spinnerUser.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        spinnerUser.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                user = userList.get(position);
                userUid = user.getUserUid();
                fillCourse();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerCourse.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                course = courseList.get(position);
                courseId = course.getCourseId();
                fillSubject();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerSubject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                subject = subjectList.get(position);
                subjectId = subject.getSubjectId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });





    }

    private void fillSubject() {

        databaseReferenceGetSubject = FirebaseDatabase.getInstance().getReference().child(userUid).child("subject").child(courseId);

        databaseReferenceGetSubject.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                subjectList.clear();
                for (DataSnapshot courseSnapshot: dataSnapshot.getChildren()){
                    Subject subject = courseSnapshot.getValue(Subject.class);
                    subjectList.add(subject);

                }

                SubjectList adapterSubject = new SubjectList(Home.this,R.layout.list_layout,subjectList);
                spinnerSubject.setAdapter(adapterSubject);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu,menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){

            case R.id.menuLogout:

                FirebaseAuth.getInstance().signOut();
                finish();
                startActivity(new Intent(this,Login.class));
                break;
        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser()==null){
            finish();
            startActivity(new Intent(this,Login.class));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.buttonDone:

                Intent intent = new Intent(getApplicationContext(),GeneratePaper.class);
                intent.putExtra(USERUID,userUid);
                intent.putExtra(SUBJECTID,subjectId);
                intent.putExtra(COURSEID,courseId);
                startActivity(intent);

                break;
        }
    }

    public void fillCourse(){

        databaseReferenceGetCourse = FirebaseDatabase.getInstance().getReference().child(userUid).child("course");

        databaseReferenceGetCourse.addValueEventListener(new ValueEventListener() {
            @Override

            public void onDataChange(DataSnapshot dataSnapshot) {

                courseList.clear();
                for (DataSnapshot courseSnapshot: dataSnapshot.getChildren()){
                    Course course = courseSnapshot.getValue(Course.class);
                    courseList.add(course);

                }

                CourseList adapter = new CourseList(Home.this,R.layout.list_layout,courseList);
                spinnerCourse.setAdapter(adapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

}
