package hopanman.android.ordermate;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.regex.Pattern;

import hopanman.android.ordermate.databinding.FragmentSignup2Binding;


public class Signup2Fragment extends Fragment {

    private Button nextButton;
    private TextInputLayout passwordLayout, passwordConfirmLayout;
    private TextInputEditText passwordEditText, passwordConfirmEditText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup)FragmentSignup2Binding.inflate(inflater).getRoot();

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
                String customerEmail = Signup2FragmentArgs.fromBundle(getArguments()).getCustomerEmail();
                NavHostFragment.findNavController(Signup2Fragment.this)
                               .navigate(Signup2FragmentDirections.actionSignup2FragmentToSignup3Fragment(customerEmail, passwordEditText.getText().toString()));
            }
        });

        passwordLayout = rootView.findViewById(R.id.signup_password_layout);
        passwordEditText = rootView.findViewById(R.id.signup_password_edittext);
        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String password = s.toString();

                if (!isAvailablePassword(password)) {
                    passwordLayout.setError("영문,숫자,특수문자 포함 8자 이상 입력해주세요.");
                } else {
                    passwordLayout.setError(null);
                }

                if (!password.equals(passwordConfirmEditText.getText().toString())) {
                    passwordConfirmLayout.setError("비밀번호가 일치하지 않습니다.");
                } else {
                    passwordConfirmLayout.setError(null);
                }

                if (passwordLayout.getError() == null && passwordConfirmLayout.getError() == null) {
                    nextButton.setEnabled(true);
                } else {
                    nextButton.setEnabled(false);
                }
            }
        });

        passwordConfirmLayout = rootView.findViewById(R.id.signup_password_confirm_layout);
        passwordConfirmEditText = rootView.findViewById(R.id.signup_password_confirm_edittext);
        passwordConfirmEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String password = passwordEditText.getText().toString();
                if (password == null || !passwordEditText.getText().toString().equals(s.toString())) {
                    passwordConfirmLayout.setError("비밀번호가 일치하지 않습니다.");
                } else {
                    passwordConfirmLayout.setError(null);
                }

                if (passwordLayout.getError() == null && passwordConfirmLayout.getError() == null) {
                    nextButton.setEnabled(true);
                } else {
                    nextButton.setEnabled(false);
                }
            }
        });

        return rootView;
    }

    private boolean isAvailablePassword(String password) {
        String regex = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$";
        boolean result = Pattern.compile(regex).matcher(password).matches();

        return result;
    }
}