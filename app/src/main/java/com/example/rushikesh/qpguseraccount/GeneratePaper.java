package com.example.rushikesh.qpguseraccount;

import android.*;
import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresPermission;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.ElementListener;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPHeaderCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.VerticalPositionMark;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class GeneratePaper extends AppCompatActivity implements View.OnClickListener {

    String subjectId, courseId, userUid, patternId,patternText,levelName,questionText="",chapterId,allQuestions="",patternName1;
    DatabaseReference databaseReferenceGetPatternName,databaseReferenceGetQuestion,databaseReferenceGetChapterId;
    ProgressDialog progressDialog;
    PatternName patternName;
    Pattern pattern;
    List<PatternName> patternNameList;
    List<String> questionList;
    Spinner spinnerPatternNames;
    ListView questionPaper;
    ArrayAdapter<String> adapter;
    public EditText pdfName;
    int flag =0,paper=0;
    static int count=0;
    String[] value;
    String[] questionEntered;
    final int STORAGE_REQUEST_CODE=2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_paper);

        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        userUid = bundle.getString(Home.USERUID);
        subjectId = bundle.getString(Home.SUBJECTID);
        courseId = bundle.getString(Home.COURSEID);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        questionEntered = new String[50];


        Toolbar toolbar = findViewById(R.id.toolbarSave);
        setSupportActionBar(toolbar);
        findViewById(R.id.generate).setOnClickListener(this);
        patternNameList = new ArrayList<>();
        spinnerPatternNames = findViewById(R.id.spinnerPatternNames);
        questionList = new ArrayList<>();


        progressDialog.show();

        fillPatternName();

        questionPaper = findViewById(R.id.listViewQuestion);
        adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.paper_layout);
        questionPaper.setAdapter(adapter);


        spinnerPatternNames.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                patternName = patternNameList.get(position);
                patternName1 = patternName.getPatternNameText();
                patternId = patternName.getPatternNameId();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    public void fillPatternName() {
        databaseReferenceGetPatternName = FirebaseDatabase.getInstance().getReference().child(userUid).child("pattern name").child(courseId).child(subjectId);

        final Query query = databaseReferenceGetPatternName;
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressDialog.cancel();

                patternNameList.clear();
                for (DataSnapshot courseSnapshot : dataSnapshot.getChildren()) {
                    PatternName patternName = courseSnapshot.getValue(PatternName.class);
                    patternNameList.add(patternName);

                }

                PatternNameList adapterPatternName = new PatternNameList(GeneratePaper.this, R.layout.list_layout, patternNameList);
                spinnerPatternNames.setAdapter(adapterPatternName);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.generate:

                if (paper==0) {
                    generatePaper();
                }else {
                    Toast.makeText(getApplicationContext(),"Paper Generated",Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    private void generatePaper() {

        paper=1;

        adapter.clear();

        progressDialog.show();

        databaseReferenceGetPatternName =  FirebaseDatabase.getInstance().getReference()
                .child(userUid);
        databaseReferenceGetPatternName.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot pattern1 :dataSnapshot.getChildren()){
                    if (pattern1.getKey().equals("question paper pattern")) {
                        for (DataSnapshot pattern2 : pattern1.getChildren()) {

                            if (pattern2.getKey().equals(patternId)) {
                                pattern = pattern2.getValue(Pattern.class);
                                assert pattern != null;
                                patternText = pattern.getPatternText();
                            }

                        }

                        adapter.clear();
                        adapter.notifyDataSetChanged();
                        String[] paperPattern = patternText.split("\\|");

                        for (String question : paperPattern) {

                            String[] parts = question.split("\\-");

                            if (parts.length > 2) {

                                String questionNumber = parts[0];
                                String chapterName = parts[1];
                                final String marks = parts[2];
                                levelName = parts[3];


                                for (DataSnapshot chapterID : dataSnapshot.getChildren()) {
                                    if (chapterID.getKey().equals("chapter")) {
                                        for (DataSnapshot chapter1 : chapterID.getChildren()) {
                                            if (chapter1.getKey().equals(courseId)){
                                                for (DataSnapshot chapter2 : chapter1.getChildren()){
                                                    if (chapter2.getKey().equals(subjectId)){
                                                        for (DataSnapshot chapter3 : chapter2.getChildren()){
                                                                Chapter chapter = chapter3.getValue(Chapter.class);
                                                                assert chapter != null;
                                                                String chapterName1 = chapter.getChapterName();
                                                                if (chapterName.equals(chapterName1)) {
                                                                    chapterId = chapter.getChapterId();
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }

                                        }
                                    }

                                    for (DataSnapshot question1 : dataSnapshot.getChildren()){
                                        if (question1.getKey().equals("question")) {
                                            for (DataSnapshot chapter1 : question1.getChildren()) {
                                                if (chapter1.getKey().equals(courseId)){
                                                    for (DataSnapshot chapter2 : chapter1.getChildren()){
                                                        if (chapter2.getKey().equals(subjectId)){
                                                            for (DataSnapshot chapter3 : chapter2.getChildren()){
                                                               if (chapter3.getKey().equals(chapterId)){

                                                                   for (DataSnapshot question4 : chapter3.getChildren()) {
                                                                       Question question12 = question4.getValue(Question.class);
                                                                       assert question1 != null;
                                                                       String level = question12.getQuestionLevel();


                                                                       if (level.equals(levelName)) {
                                                                           String q = question12.getQuestionText();
                                                                           int len = questionEntered.length;
                                                                           if (allQuestions==null){
                                                                               questionList.add(q);
                                                                           }else {
                                                                               for (int k = 0;k<len;k++){

                                                                                   if (questionEntered[k]==q){
                                                                                       flag=1;
                                                                                       break;
                                                                                   }else {
                                                                                       flag=0;
                                                                                   }

                                                                               }
                                                                           }

                                                                           if (flag==0){
                                                                               questionList.add(q);
                                                                           }


                                                                       }
                                                                   }

                                                                   if (questionList.size()==0){
                                                                       questionList.add("NULL");
                                                                   }

                                                                   long allNum = questionList.size();
                                                                   int maxNum = (int)allNum-1;
                                                                   int minNum = 0;
                                                                   int randomNum = new Random().nextInt(maxNum - minNum + 1) + minNum;


                                                                   questionText = questionList.get(randomNum);

                                                                   if (allQuestions.length() == 0) {
                                                                       if (questionText==null){
                                                                           questionText="NULL";
                                                                       }
                                                                       allQuestions = "   "+questionNumber+"."+questionText;
                                                                       questionEntered[count]=questionText;
                                                                       count++;
                                                                       questionList.clear();
                                                                       break;
                                                                   } else {
                                                                       if (questionText==null){
                                                                           questionText="NULL";
                                                                       }
                                                                       allQuestions = allQuestions + "|   " + questionNumber + "." + questionText;
                                                                       questionEntered[count]=questionText;
                                                                       count++;

                                                                       questionList.clear();



                                                                       }

                                                                       break;
                                                                   }

                                                               }

                                                            }
                                                        }
                                                    }
                                                }
                                            }

                                        }

                            }else {


                            if (allQuestions.length()==0){
                                allQuestions = parts[0]+"               ["+parts[1]+"]";
                            }else {
                                allQuestions = allQuestions+"|"+parts[0]+"               ["+parts[1]+"]";
                            }
                        }
                        }

                    }
                    }

                String[] tokens = allQuestions.split("\\|");
                value = allQuestions.split("\\|");

                adapter.clear();
                adapter.notifyDataSetChanged();


                for(String token : tokens) {

                    adapter.add(token);
                }
                progressDialog.cancel();

                }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_save, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.print:

                askPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,STORAGE_REQUEST_CODE);

                break;
        }
        return true;
    }

    private void pdfName(){

        LayoutInflater inflater = getLayoutInflater();
        View fragmentView = inflater.inflate(R.layout.pdf_name, null);
        pdfName = fragmentView.findViewById(R.id.pdf_name1);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String pdfName1 = pdfName.getText().toString();
                if (!TextUtils.isEmpty(pdfName1)) {

                    printPDF();
                } else {
                    pdfName.setError("Enter name");
                    pdfName.requestFocus();
                    return;
                }

            }
        });

        builder.setView(fragmentView);
        builder.show();

    }

    private void askPermission(String permission,int request){
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{permission}, request);
        } else {
            pdfName();
        }
    }

    private void printPDF() {

            String pdfName1 = pdfName.getText().toString();

            com.itextpdf.text.Document doc = new com.itextpdf.text.Document();
            String outPath = Environment.getExternalStorageDirectory() + "/" + pdfName1 + ".pdf";
            try {
                PdfWriter writer = PdfWriter.getInstance(doc, new FileOutputStream(outPath));
                doc.open();

                Font bold = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
                Font bold1 = new Font(Font.FontFamily.TIMES_ROMAN, 16, Font.BOLD);
                Font normal = new Font(Font.FontFamily.TIMES_ROMAN, 10);
                Paragraph p = new Paragraph(patternName1, bold1);

                p.setAlignment(Element.ALIGN_CENTER);
                doc.add(p);
                doc.add(new Paragraph(" "));
                doc.add(new Paragraph(" "));
                String que = "";
                String mark = "";
                for (String values : value) {
                    if (values.contains("[")) {

                        String[] para = values.split("\\               ");
                        que = para[0];
                        mark = para[1];

                        doc.add(new Paragraph(" "));
                        Paragraph questionTitle = new Paragraph();
                        Chunk glue = new Chunk(new VerticalPositionMark());
                        Phrase ph1 = new Phrase();
                        Paragraph main = new Paragraph();
                        ph1.add(new Chunk(que, bold));
                        ph1.add(glue);
                        ph1.add(new Chunk(mark, bold));
                        main.add(ph1);
                        questionTitle.add(main);
                        doc.add(questionTitle);
                        doc.add(new Paragraph(" "));
                    } else {
                        doc.add(new Paragraph(values, normal));
                        doc.add(new Paragraph(" "));
                    }
                }
                doc.close();
                Toast.makeText(GeneratePaper.this, "PDF Generated", Toast.LENGTH_LONG).show();

            } catch (DocumentException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case STORAGE_REQUEST_CODE:
                if (grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this,"Permission Granted",Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(this,"Permission Denied",Toast.LENGTH_LONG).show();
                }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        count=0;
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        count=0;
        finish();
    }
}
