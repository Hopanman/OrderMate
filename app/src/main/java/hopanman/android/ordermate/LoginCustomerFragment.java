package hopanman.android.ordermate;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.material.textfield.TextInputEditText;

import hopanman.android.ordermate.databinding.FragmentLoginCustomerBinding;


public class LoginCustomerFragment extends Fragment {

    private TextInputEditText editText;
    private TextInputEditText editText2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup)FragmentLoginCustomerBinding.inflate(inflater).getRoot();
        editText = rootView.findViewById(R.id.id_edittext);
        editText2 = rootView.findViewById(R.id.password_edittext);

        Button button = rootView.findViewById(R.id.login_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editText.getText().toString();
                if (email.equals("") || email == null) {
                    editText.requestFocus();
                    editText.setError("이메일주소를 입력해주세요.");
                    return;
                }

                String password = editText2.getText().toString();
                if (password.equals("") || password == null) {
                    editText2.requestFocus();
                    editText2.setError("비밀번호를 입력해주세요.");
                    return;
                }
            }
       });

       return rootView;
    }
}