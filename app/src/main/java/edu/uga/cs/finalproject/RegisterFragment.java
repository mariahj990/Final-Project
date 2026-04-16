package edu.uga.cs.finalproject;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterFragment extends Fragment {

    private static final String DEBUG_TAG = "RegisterFragment";

    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private Button registerButton;
    private Button backToLoginButton;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        emailEditText = view.findViewById(R.id.editTextRegisterEmail);
        passwordEditText = view.findViewById(R.id.editTextRegisterPassword);
        confirmPasswordEditText = view.findViewById(R.id.editTextConfirmPassword);
        registerButton = view.findViewById(R.id.buttonRegister);
        backToLoginButton = view.findViewById(R.id.buttonBackToLogin);

        registerButton.setOnClickListener(v -> attemptRegister());

        backToLoginButton.setOnClickListener(v -> {
            // Pop back to LoginFragment
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
    }

    private void attemptRegister() {
        String email           = emailEditText.getText().toString().trim();
        String password        = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();

        // Validation
        if (email.isEmpty()) {
            emailEditText.setError("Email is required");
            emailEditText.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            passwordEditText.setError("Password is required");
            passwordEditText.requestFocus();
            return;
        }
        if (password.length() < 6) {
            passwordEditText.setError("Password must be at least 6 characters");
            passwordEditText.requestFocus();
            return;
        }
        if (!password.equals(confirmPassword)) {
            confirmPasswordEditText.setError("Passwords do not match");
            confirmPasswordEditText.requestFocus();
            return;
        }

        setFormEnabled(false);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        setFormEnabled(true);
                        if (task.isSuccessful()) {
                            Log.d(DEBUG_TAG, "createUserWithEmail: success");

                            // Save the new user's email into Firebase Realtime Database
                            // so other parts of the app can look up all roommates.
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            if (firebaseUser != null) {
                                User newUser = new User(firebaseUser.getUid(), email);
                                mDatabase.child("users")
                                        .child(firebaseUser.getUid())
                                        .setValue(newUser);
                            }

                            Toast.makeText(getContext(),
                                    "Account created! Welcome, " + email,
                                    Toast.LENGTH_SHORT).show();

                            // Navigate to the main app
                            if (getActivity() instanceof MainActivity) {
                                ((MainActivity) getActivity()).onLoginSuccess();
                            }
                        } else {
                            Log.w(DEBUG_TAG, "createUserWithEmail: failure", task.getException());
                            Toast.makeText(getContext(),
                                    "Registration failed: " + task.getException().getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void setFormEnabled(boolean enabled) {
        emailEditText.setEnabled(enabled);
        passwordEditText.setEnabled(enabled);
        confirmPasswordEditText.setEnabled(enabled);
        registerButton.setEnabled(enabled);
        backToLoginButton.setEnabled(enabled);
    }
}