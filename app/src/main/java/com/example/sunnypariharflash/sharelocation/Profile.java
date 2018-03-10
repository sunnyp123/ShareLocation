package com.example.sunnypariharflash.sharelocation;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Profile extends AppCompatActivity {
ImageView image;
Button btn;
EditText edt1,edt2;
FirebaseAuth auth;
DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        image = findViewById(R.id.image);
        btn = findViewById(R.id.update);
        edt1 = findViewById(R.id.nameedit);
        edt2 = findViewById(R.id.Emailedit);
        auth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference().child(auth.getCurrentUser().getUid());
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(edt1.getText().toString().trim())&&TextUtils.isEmpty(edt2.getText().toString().trim())){
                    edt1.setError("Empty Field");
                }
                else{
                   String  data1 = edt1.getText().toString().trim();
                    String data2 = edt2.getText().toString().trim();
                    reference.push().setValue(new ProfileData(data1,data2));
                    startActivity(new Intent(Profile.this,Drawer.class));
                    Toast.makeText(Profile.this, "Your Data has been entered", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}
