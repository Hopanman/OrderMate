package hopanman.android.ordermate;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import hopanman.android.ordermate.databinding.FragmentLoginStoreBinding;


public class LoginStoreFragment extends Fragment {

    private TextInputEditText editText;
    private TextInputEditText editText2;
    private ProgressBar progressBar;
    private Window window;
    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup)FragmentLoginStoreBinding.inflate(inflater).getRoot();
        editText = rootView.findViewById(R.id.id_edittext);
        editText2 = rootView.findViewById(R.id.password_edittext);

        progressBar = ((LoginActivity)getActivity()).progressBar;
        window = ((LoginActivity)getActivity()).getWindow();

        mAuth = FirebaseAuth.getInstance();

        Button button = rootView.findViewById(R.id.login_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editText.getText().toString();

                if (email.equals("") || email == null) {
                    editText.requestFocus();
                    editText.setError("아이디를 입력해주세요.");
                    return;
                }

                String password = editText2.getText().toString();
                if (password.equals("") || password == null) {
                    editText2.requestFocus();
                    editText2.setError("비밀번호를 입력해주세요.");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            DocumentReference docRef = db.collection("stores").document(task.getResult().getUser().getUid());
                            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        progressBar.setVisibility(View.INVISIBLE);
                                        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                        if (document.exists()) {
                                            String storeName = (String)document.get("storeName");
                                            Intent intent = new Intent(getContext(), StoreActivity.class);
                                            intent.putExtra("storeName", storeName);
                                            startActivity(intent);
                                            getActivity().overridePendingTransition(R.anim.slide_in_right, android.R.anim.fade_out);
                                            getActivity().finish();
                                        } else {
                                            Toast.makeText(getContext(), "아이디 혹은 비밀번호가 일치하지 않습니다", Toast.LENGTH_LONG).show();
                                            mAuth.signOut();
                                        }
                                    }
                                }
                            });
                        } else {
                            progressBar.setVisibility(View.INVISIBLE);
                            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            Toast.makeText(getContext(), "아이디 혹은 비밀번호가 일치하지 않습니다", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

        return rootView;
    }
}