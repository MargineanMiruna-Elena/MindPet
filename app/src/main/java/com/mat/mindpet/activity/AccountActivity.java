package com.mat.mindpet.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseError;
import com.mat.mindpet.R;
import com.mat.mindpet.model.User;
import com.mat.mindpet.repository.UserRepository;
import com.mat.mindpet.service.AuthService;
import com.mat.mindpet.service.UserService;
import com.mat.mindpet.utils.NavigationHelper;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AccountActivity extends AppCompatActivity {

    @Inject
    AuthService authService;

    @Inject
    UserService userService;

    private TextView profileNameTextView;
    private TextView profileEmailTextView;
    private View rowLastName;
    private View rowFirstName;
    private View rowEmail;
    private View rowPet;
    private View logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        NavigationHelper.setupNavigationBar(this);

        profileNameTextView = findViewById(R.id.profileName);
        profileEmailTextView = findViewById(R.id.profileEmail);
        logoutButton = findViewById(R.id.btnLogout);

        rowLastName = findViewById(R.id.row_last_name);
        rowFirstName = findViewById(R.id.row_first_name);
        rowEmail = findViewById(R.id.row_email);
        rowPet = findViewById(R.id.row_pet);

        loadCurrentUser();

        logoutButton.setOnClickListener(v -> {
            authService.logout();
            startActivity(new Intent(AccountActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void loadCurrentUser() {
        userService.getCurrentUser(new UserRepository.UserCallback() {
            @Override
            public void onSuccess(User user) {
                if (user == null) {
                    Toast.makeText(AccountActivity.this, "No account found. Register!", Toast.LENGTH_SHORT).show();
                    return;
                }

                updateProfileUI(user);
                updateUserUI(user);

                setupEditableRows(user);
            }

            @Override
            public void onFailure(DatabaseError error) {
                Toast.makeText(AccountActivity.this, "Error loading account information: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateProfileUI(User user) {
        String fullName = safe(user.getFirstName()) + " " + safe(user.getLastName());
        profileNameTextView.setText(fullName.trim());
        profileEmailTextView.setText(safe(user.getEmail()));
    }

    private void updateUserUI(User user) {
        setupRow(rowLastName,  "Last Name",  safe(user.getLastName()), R.drawable.ic_user);
        setupRow(rowFirstName, "First Name", safe(user.getFirstName()), R.drawable.ic_user);
        setupRow(rowEmail,     "Email",      safe(user.getEmail()),     R.drawable.ic_mail);

        if (user.getPet() != null) {
            setupRow(rowPet, "Pet Name", safe(user.getPet().getPetName()), R.drawable.ic_paw);
            rowPet.setVisibility(View.VISIBLE);
        } else {
            rowPet.setVisibility(View.GONE);
        }
    }

    private void setupRow(View row, String title, String value, int iconResId) {
        TextView titleView = row.findViewById(R.id.title);
        EditText valueEdit = row.findViewById(R.id.value);
        ImageView iconView = row.findViewById(R.id.icon);
        ImageView checkView = row.findViewById(R.id.check);

        titleView.setText(title);
        valueEdit.setText(value);
        valueEdit.setEnabled(false);
        valueEdit.setFocusable(false);
        iconView.setImageResource(iconResId);
        if (checkView != null) checkView.setVisibility(View.GONE);
    }

    private void setupEditableRows(User user) {
        setupEditableRow(rowFirstName, "firstName");
        setupEditableRow(rowLastName,  "lastName");
        setupEditableRow(rowEmail,     "email");

        if (user.getPet() != null) {
            setupEditableRow(rowPet, "pet/petName");
        }
    }

    /**
     * Makes the row enter edit mode on click and saves when the check icon is tapped.
     *
     * @param row               the included row view
     * @param firebaseFieldName the field path in DB (e.g. "firstName" or "pet/petName")
     */
    private void setupEditableRow(View row, String firebaseFieldName) {
        EditText valueEdit = row.findViewById(R.id.value);
        ImageView checkIcon = row.findViewById(R.id.check);

        row.setOnClickListener(v -> {
            setRowEditable(row, true);
            valueEdit.requestFocus();
            valueEdit.setSelection(valueEdit.length());
        });

        if (checkIcon != null) {
            checkIcon.setOnClickListener(v -> {
                String newValue = valueEdit.getText().toString().trim();

                if (newValue.isEmpty()) {
                    valueEdit.setError("Cannot be empty");
                    valueEdit.requestFocus();
                    return;
                }

                if (firebaseFieldName.equals("email") && !Patterns.EMAIL_ADDRESS.matcher(newValue).matches()) {
                    valueEdit.setError("Invalid email format");
                    return;
                }

                userService.updateCurrentUserField(firebaseFieldName, newValue);

                Toast.makeText(AccountActivity.this, "Saved", Toast.LENGTH_SHORT).show();

                setRowEditable(row, false);

                if ("firstName".equals(firebaseFieldName) || "lastName".equals(firebaseFieldName) || "email".equals(firebaseFieldName)) {
                    userService.getCurrentUser(new UserRepository.UserCallback() {
                        @Override
                        public void onSuccess(User user) {
                            if (user != null) updateProfileUI(user);
                        }

                        @Override
                        public void onFailure(DatabaseError error) { }
                    });
                }
            });
        }
    }

    private void setRowEditable(View row, boolean editable) {
        EditText valueEdit = row.findViewById(R.id.value);
        ImageView checkIcon = row.findViewById(R.id.check);

        valueEdit.setEnabled(editable);
        valueEdit.setFocusable(editable);
        valueEdit.setFocusableInTouchMode(editable);

        if (checkIcon != null) checkIcon.setVisibility(editable ? View.VISIBLE : View.GONE);
    }

    private String safe(String s) {
        return s == null ? "" : s;
    }
}
