package com.giantiqs.firebasecrud;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.giantiqs.firebasecrud.Adapters.UserRecyclerAdapter;
import com.giantiqs.firebasecrud.classes.UsersItem;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    DatabaseReference databaseReference;
    RecyclerView recyclerView;
    ArrayList<UsersItem> usersItemArrayList;
    UserRecyclerAdapter adapter;
    Button buttonAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        usersItemArrayList = new ArrayList<>();

        buttonAdd = findViewById(R.id.buttonAdd);
        buttonAdd.setOnClickListener(v -> {
            viewDialogAdd viewDialogAdd = new viewDialogAdd();
            viewDialogAdd.showDialog(MainActivity.this);
        });

        readData();
    }

    private void readData() {

        databaseReference.child("USERS").orderByChild("userName").addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usersItemArrayList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    UsersItem users = dataSnapshot.getValue(UsersItem.class);
                    usersItemArrayList.add(users);
                }

                adapter = new UserRecyclerAdapter(MainActivity.this, usersItemArrayList);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public class viewDialogAdd {
        @SuppressLint("SetTextI18n")
        public void showDialog(Context context) {

            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.alert_dialog_add_new_user);

            EditText textName = dialog.findViewById(R.id.textName);
            EditText textEmail = dialog.findViewById(R.id.textEmail);
            EditText textCountry = dialog.findViewById(R.id.textCountry);

            Button buttonAdd = dialog.findViewById(R.id.buttonAdd);
            Button buttonCancel = dialog.findViewById(R.id.buttonCancel);

            buttonAdd.setText("ADD");
            buttonCancel.setOnClickListener(v -> dialog.dismiss());

            buttonAdd.setOnClickListener(v -> {
                String id = "user" + new Date().getTime();
                String name = textName.getText().toString();
                String email = textEmail.getText().toString();
                String country = textCountry.getText().toString();

                if (name.isEmpty() || email.isEmpty() || country.isEmpty()) {
                    Toast.makeText(context, "Please enter something", Toast.LENGTH_LONG).show();
                } else {
                    databaseReference.child("USERS").child(id).setValue(new UsersItem(id, name, email, country));
                    Toast.makeText(context, "Done", Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                }
            });

            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        }
    }
}