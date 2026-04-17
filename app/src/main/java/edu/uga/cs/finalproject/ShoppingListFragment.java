package edu.uga.cs.finalproject;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ShoppingListFragment extends Fragment {

    private EditText boxName;
    private EditText boxCount;
    private Button addBtn;
    private Button logoutBtn;
    private DatabaseReference listRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_shopping_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        boxName = view.findViewById(R.id.editTextItemName);
        boxCount = view.findViewById(R.id.editTextAmount);
        addBtn = view.findViewById(R.id.buttonAddItem);
        logoutBtn = view.findViewById(R.id.buttonLogoutNow);

        listRef = FirebaseDatabase.getInstance().getReference("shoppingList");

        addBtn.setOnClickListener(v -> saveItem());

        logoutBtn.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).onLogout();
            }
        });
    }

    private void saveItem() {
        String itemTxt = boxName.getText().toString().trim();
        String countTxt = boxCount.getText().toString().trim();

        if (TextUtils.isEmpty(itemTxt)) {
            boxName.setError("Enter item");
            return;
        }

        if (TextUtils.isEmpty(countTxt)) {
            countTxt = "1";
        }

        ShoppingItem oneItem = new ShoppingItem(itemTxt, countTxt);
        String newId = listRef.push().getKey();

        if (newId != null) {
            listRef.child(newId).setValue(oneItem)
                    .addOnSuccessListener(unused -> {
                        boxName.setText("");
                        boxCount.setText("");
                        Toast.makeText(getContext(), "Item added", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
        }
    }
}