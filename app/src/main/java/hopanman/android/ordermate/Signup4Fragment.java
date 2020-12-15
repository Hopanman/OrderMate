package hopanman.android.ordermate;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.regex.Pattern;

import hopanman.android.ordermate.databinding.FragmentSignup4Binding;


public class Signup4Fragment extends Fragment {

    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    private Window window;
    private Button nextButton;
    private TextInputLayout phoneLayout;
    private TextInputEditText phoneEditText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup)FragmentSignup4Binding.inflate(inflater).getRoot();

        progressBar = rootView.findViewById(R.id.progressBar);
        window = getActivity().getWindow();

        View backButton = rootView.findViewById(R.id.signup_button_back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        nextButton = rootView.findViewById(R.id.signup_next_button);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String customerEmail = Signup4FragmentArgs.fromBundle(getArguments()).getCustomerEmail();
                String customerPassword = Signup4FragmentArgs.fromBundle(getArguments()).getCustomerPassword();
                String customerName = Signup4FragmentArgs.fromBundle(getArguments()).getCustomerName();
                String customerTel = phoneEditText.getText().toString();

                activateProgressBar();
                mAuth = FirebaseAuth.getInstance();
                mAuth.createUserWithEmailAndPassword(customerEmail, customerPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Customer customer = new Customer(customerEmail, customerName, customerTel);

                            FirebaseUser user = mAuth.getCurrentUser();
                            FirebaseFirestore.getInstance()
                                             .collection("customers")
                                             .document(user.getUid())
                                             .set(customer)
                                             .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                 @Override
                                                 public void onSuccess(Void aVoid) {
                                                     deactivateProgressBar();
                                                     Intent intent = new Intent(getContext(), CustomerActivity.class);
                                                     intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                     startActivity(intent);
                                                     getActivity().overridePendingTransition(R.anim.slide_in_right, android.R.anim.fade_out);
                                                 }
                                             });
                        } else {
                            deactivateProgressBar();
                            Toast.makeText(getContext(), "회원가입에 실패하였습니다.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

        phoneLayout = rootView.findViewById(R.id.signup_phone_layout);
        phoneEditText = rootView.findViewById(R.id.signup_phone_edittext);
        phoneEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!isAvailablePhone(s.toString())) {
                    phoneLayout.setError("휴대폰번호를 '-' 포함해서 올바르게 입력해주세요.");
                } else {
                    phoneLayout.setError(null);
                }

                if (phoneLayout.getError() == null) {
                    nextButton.setEnabled(true);
                } else {
                    nextButton.setEnabled(false);
                }
            }
        });

        return rootView;
    }

    private boolean isAvailablePhone(String phone) {
        String regex = "^01[016789]{1}-[0-9]{3,4}-[0-9]{4}$";
        boolean result = Pattern.compile(regex).matcher(phone).matches();

        return result;
    }

    private void activateProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void deactivateProgressBar() {
        progressBar.setVisibility(View.INVISIBLE);
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }
}