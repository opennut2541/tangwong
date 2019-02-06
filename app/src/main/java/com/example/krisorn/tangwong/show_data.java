package com.example.krisorn.tangwong;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class show_data extends AppCompatActivity {
    private DatabaseReference Polldatabase;
    public FirebaseAuth mAuth;
    private String getKey ;
    LinearLayout  mLinearLayout ;
    FormBuilder formBuilder;
    private String Question;
    private long getcount = 0;
    List<FormObject> formObjects = new ArrayList<FormObject> ();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_showdata);
        mAuth= FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();
        Polldatabase = FirebaseDatabase.getInstance().getReference();
        Polldatabase.addListenerForSingleValueEvent (new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                getKey = dataSnapshot.child("user").child(user.getUid()).child("livenow").getValue(String.class);
                getcount = dataSnapshot.child("room").child(getKey).child("Poll").getChildrenCount();
                formObjects.clear();
                for(int i = 1 ; i < getcount ;i++){
                    long getChoice = dataSnapshot.child("room").child(getKey).child("Poll").child(String.valueOf (i)).child("Choice").getChildrenCount();
                    Question = dataSnapshot.child("room").child(getKey).child("Poll").child(String.valueOf (i)).child("Topic").getValue(String.class);
                    mLinearLayout = (LinearLayout) findViewById(R.id.poll_interface);
                    formBuilder = new FormBuilder(show_data.this, mLinearLayout);
                    formObjects.add(new FormHeader().setTitle(Question));
                    for (int j = 1;j < getChoice;j++){
                        if(dataSnapshot.child("room").child(getKey).child("Poll").child(String.valueOf (i)).child("Choice").child (String.valueOf (i)).child ("select").getValue (String.class)== "1"){
                            String gettemp = dataSnapshot.child("room").child(getKey).child("Poll").child(String.valueOf (i)).child("Choice").child (String.valueOf (i)).child ("number").getValue (String.class);
                            long integernumber = Integer.parseInt(gettemp);
                            Polldatabase.child("room").child(getKey).child("Poll").child(String.valueOf (i)).child("Choice").child (String.valueOf (i)).child ("number").setValue (String.valueOf (integernumber++));
                        }
                    }
                    for (int j = 1;j < getChoice;j++){
                        String data = dataSnapshot.child("room").child(getKey).child("Poll").child(String.valueOf (i)).child("Choice").child(Long.toString(i)).child("sub-topic").getValue(String.class) ;
                        String number = dataSnapshot.child("room").child(getKey).child("Poll").child(String.valueOf (i)).child("Choice").child (String.valueOf (i)).child ("number").getValue (String.class);
                        formObjects.add(new FormElement().setTag("view").setHint(data +" Chosen :" + number ).setType(FormElement.Type.TEXTVIEW));
                    }
                }
                formBuilder.build(formObjects);
                formObjects.clear();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
