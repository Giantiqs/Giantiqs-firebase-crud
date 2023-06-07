package com.giantiqs.firebasecrud.Adapters;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.giantiqs.firebasecrud.R;
import com.giantiqs.firebasecrud.classes.UsersItem;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class UserRecyclerAdapter extends RecyclerView.Adapter<UserRecyclerAdapter.ViewHolder> {

    Context context;
    ArrayList<UsersItem> usersItemArrayList;
    DatabaseReference databaseReference;

    public UserRecyclerAdapter(Context context, ArrayList<UsersItem> usersItemArrayList) {
        this.context = context;
        this.usersItemArrayList = usersItemArrayList;
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.user_item, parent, false);

        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        UsersItem users = usersItemArrayList.get(position);

        holder.textName.setText("Name: " + users.getUserName());
        holder.textEmail.setText("Email: " + users.getUserEmail());
        holder.textCountry.setText("Country: " + users.getUserCountry());

        holder.buttonUpdate.setOnClickListener(v -> {
            viewDialogUpdate viewDialogUpdate = new viewDialogUpdate();
            viewDialogUpdate.showDialog(
                    context,
                    users.getUserID(),
                    users.getUserName(),
                    users.getUserEmail(),
                    users.getUserCountry()
            );
        });

        holder.buttonDelete.setOnClickListener(v -> {
            viewDialogConfirmDelete viewDialogConfirmDelete = new viewDialogConfirmDelete();
            viewDialogConfirmDelete.showDialog(context, users.getUserID());
        });
    }

    @Override
    public int getItemCount() {
        return usersItemArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView textName, textEmail, textCountry;

        Button buttonDelete, buttonUpdate;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textName = itemView.findViewById(R.id.textName);
            textEmail = itemView.findViewById(R.id.textEmail);
            textCountry = itemView.findViewById(R.id.textCountry);

            buttonDelete = itemView.findViewById(R.id.buttonDelete);
            buttonUpdate = itemView.findViewById(R.id.buttonUpdate);
        }
    }

    public class viewDialogUpdate {
        @SuppressLint("SetTextI18n")
        public void showDialog(
                Context context,
                String id,
                String name,
                String email,
                String country) {

            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.alert_dialog_add_new_user);

            EditText textName = dialog.findViewById(R.id.textName);
            EditText textEmail = dialog.findViewById(R.id.textEmail);
            EditText textCountry = dialog.findViewById(R.id.textCountry);

            textName.setText(name);
            textEmail.setText(email);
            textCountry.setText(country);

            Button buttonUpdate = dialog.findViewById(R.id.buttonAdd);
            Button buttonCancel = dialog.findViewById(R.id.buttonCancel);

            buttonUpdate.setText("UPDATE");
            buttonCancel.setOnClickListener(v -> dialog.dismiss());

            buttonUpdate.setOnClickListener(v -> {
                String newName = textName.getText().toString();
                String newEmail = textEmail.getText().toString();
                String newCountry = textCountry.getText().toString();

                if (name.isEmpty() || email.isEmpty() || country.isEmpty()) {
                    Toast.makeText(context, "Please enter something", Toast.LENGTH_LONG).show();
                } else {

                    if (newName.equals(name) && newEmail.equals(email) && newCountry.equals(country)) {
                        Toast.makeText(context, "Please enter new data", Toast.LENGTH_LONG).show();
                    } else {
                        databaseReference.child("USERS").child(id).setValue(new UsersItem(id, newName, newEmail, newCountry));
                        Toast.makeText(context, "Done", Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }
                }
            });

            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        }
    }

    public class viewDialogConfirmDelete {
        @SuppressLint("SetTextI18n")
        public void showDialog(Context context, String id) {

            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.view_dialog_confirm_delete);

            Button buttonDelete = dialog.findViewById(R.id.buttonDelete);
            Button buttonCancel = dialog.findViewById(R.id.buttonCancel);

            buttonDelete.setText("DELETE");
            buttonCancel.setOnClickListener(v ->dialog.dismiss());

            buttonDelete.setOnClickListener(v -> {
                databaseReference.child("USERS").child(id).removeValue();
                Toast.makeText(context, "Done", Toast.LENGTH_LONG).show();
                dialog.dismiss();
            });

            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        }
    }
}
