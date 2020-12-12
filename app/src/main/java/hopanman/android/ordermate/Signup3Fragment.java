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

import hopanman.android.ordermate.databinding.FragmentSignup3Binding;


public class Signup3Fragment extends Fragment {

    private Button nextButton;
    private TextInputLayout nicknameLayout;
    private TextInputEditText nicknameEditText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup)FragmentSignup3Binding.inflate(inflater).getRoot();

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
                String customerEmail = Signup3FragmentArgs.fromBundle(getArguments()).getCustomerEmail();
                String customerPassword = Signup3FragmentArgs.fromBundle(getArguments()).getCustomerPassword();
                NavHostFragment.findNavController(Signup3Fragment.this)
                               .navigate(Signup3FragmentDirections.actionSignup3FragmentToSignup4Fragment(customerEmail, customerPassword, nicknameEditText.getText().toString()));
            }
        });

        nicknameLayout = rootView.findViewById(R.id.signup_nickname_layout);
        nicknameEditText = rootView.findViewById(R.id.signup_nickname_edittext);
        nicknameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!isAvailableNickname(s.toString())) {
                    nicknameLayout.setError("한글,영문,숫자로 2자 이상 10자 이하로 입력해주세요.");
                } else {
                    nicknameLayout.setError(null);
                }

                if (nicknameLayout.getError() == null) {
                    nextButton.setEnabled(true);
                } else {
                    nextButton.setEnabled(false);
                }
            }
        });

        return rootView;
    }

    private boolean isAvailableNickname(String nickname) {
        String regex = "^[A-Za-z0-9ㄱ-ㅎㅏ-ㅣ가-힣]{2,10}$";
        boolean result = Pattern.compile(regex).matcher(nickname).matches();

        return result;
    }
}