package gurveen.com.snapchatjava;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChooseUser extends AppCompatActivity {

    ListView chooseUserListView;
    ArrayAdapter<String> arrayAdapter;
    ArrayList<String> emails = new ArrayList<>();
    ArrayList<String> keys = new ArrayList<>();


    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_user);
        setTitle("Choose a Friend");

        chooseUserListView = findViewById(R.id.chooseUserListView);
        arrayAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,emails);
        chooseUserListView.setAdapter(arrayAdapter);

        FirebaseDatabase.getInstance().getReference().child("users").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
               String email = dataSnapshot.child("email").getValue().toString();
               emails.add(email);
               keys.add(dataSnapshot.getKey());
               arrayAdapter.notifyDataSetChanged();
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) { }
            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        chooseUserListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                HashMap<String,String> snapMap = new HashMap<>();
                snapMap.put("from",mAuth.getCurrentUser().getEmail());
                snapMap.put("imageName",getIntent().getStringExtra("imageName"));
                snapMap.put("imageUrl",getIntent().getStringExtra("imageUrl"));
                snapMap.put("message",getIntent().getStringExtra("message"));

               FirebaseDatabase.getInstance().getReference().child("users").child(keys.get(position))
                        .child("snaps").push().setValue(snapMap).addOnFailureListener(new OnFailureListener() {
                   @Override
                   public void onFailure(@NonNull Exception e) {
                       Log.v("Snap Addition","Failed");
                       e.printStackTrace();
                   }
               });

                Intent intent = new Intent(ChooseUser.this,SnapsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }
}
