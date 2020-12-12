package hopanman.android.ordermate;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.regex.Pattern;

import hopanman.android.ordermate.databinding.FragmentSignup1Binding;


public class Signup1Fragment extends Fragment {

    private Button nextButton;
    private Button checkDuplicateButton;
    private TextInputEditText emailEditText;
    private ProgressBar progressBar;
    private Window window;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup)FragmentSignup1Binding.inflate(inflater).getRoot();

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
                NavHostFragment.findNavController(Signup1Fragment.this)
                               .navigate(Signup1FragmentDirections.actionSignup1FragmentToSignup2Fragment(emailEditText.getText().toString()));
            }
        });

        checkDuplicateButton = rootView.findViewById(R.id.signup_check_duplicate_button);

        TextInputLayout emailLayout = rootView.findViewById(R.id.signup_email_layout);
        emailEditText = rootView.findViewById(R.id.signup_email_edittext);
        emailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                emailLayout.setHelperText(null);
                nextButton.setEnabled(false);

                if (!isAvailableEmail(s.toString())) {
                    emailLayout.setError("이메일을 올바르게 입력해주세요.");
                } else {
                    emailLayout.setError(null);
                }

                if (emailLayout.getError() == null) checkDuplicateButton.setEnabled(true);
                else checkDuplicateButton.setEnabled(false);
            }
        });
        InputMethodManager inputManager = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        emailEditText.postDelayed(new Runnable() {
            @Override
            public void run() {
                emailEditText.requestFocus();
                inputManager.showSoftInput(emailEditText, 0);
            }
        }, 100);

        checkDuplicateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString();

                if (isStoreAccount(email)) {
                    emailLayout.setError("사용할 수 없는 이메일입니다.");
                    checkDuplicateButton.setEnabled(false);
                    return;
                }

                activateProgressBar();
                FirebaseFirestore.getInstance()
                                 .collection("customers")
                                 .whereEqualTo("customerEmail", email)
                                 .get()
                                 .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                     @Override
                                     public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                         if (task.isSuccessful()) {
                                             deactivateProgressBar();
                                             QuerySnapshot result = task.getResult();

                                             if (result.isEmpty()) {
                                                 emailLayout.setHelperText("사용 가능한 이메일입니다.");
                                                 checkDuplicateButton.setEnabled(false);
                                                 nextButton.setEnabled(true);
                                             } else {
                                                 emailLayout.setError("이미 사용 중인 이메일입니다.");
                                                 checkDuplicateButton.setEnabled(false);
                                             }
                                         } else {
                                             deactivateProgressBar();
                                             emailLayout.setError("문제가 발생하였습니다. 관리자에게 문의해주세요.");
                                         }
                                     }
                                 });
            }
        });

        return rootView;
    }

    private boolean isAvailableEmail(String email) {
        String regex = "^[a-z0-9-_]+(\\.[a-z0-9-_]+)*@([a-z0-9-]+\\.)+[a-z]{2,6}$";
        boolean result = Pattern.compile(regex).matcher(email).matches();

        return result;
    }

    private boolean isStoreAccount(String email) {
        String regex = "^[a-z0-9-_]+(\\.[a-z0-9-_]+)*@ordermate.co.kr$";
        boolean result = Pattern.compile(regex).matcher(email).matches();

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