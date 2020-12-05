package hopanman.android.ordermate;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

import hopanman.android.ordermate.databinding.FragmentLoginCustomerBinding;


public class LoginCustomerFragment extends Fragment {

    private TextInputEditText editText;
    private TextInputEditText editText2;
    private ProgressBar progressBar;
    private Window window;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private final int REQUEST_SIGN_IN_CODE = 101;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup)FragmentLoginCustomerBinding.inflate(inflater).getRoot();
        editText = rootView.findViewById(R.id.id_edittext);
        editText2 = rootView.findViewById(R.id.password_edittext);

        progressBar = ((LoginActivity)getActivity()).progressBar;
        window = ((LoginActivity)getActivity()).getWindow();

        mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                               .requestIdToken("723683449717-okhc35b03ims4d7vhmb44v5v7o8c0075.apps.googleusercontent.com")
                               .requestEmail()
                               .build();

        mGoogleSignInClient = GoogleSignIn.getClient(getContext(), gso);

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

       Button googleLoginButton = rootView.findViewById(R.id.customer_google_login_button);
       googleLoginButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               activateProgressBar();
               Intent intent = mGoogleSignInClient.getSignInIntent();
               startActivityForResult(intent, REQUEST_SIGN_IN_CODE);
           }
       });

       Button signupButton = rootView.findViewById(R.id.customer_signup_button);
       signupButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent = new Intent(getContext(), SignupActivity.class);
               startActivity(intent);
               getActivity().overridePendingTransition(R.anim.slide_in_right, android.R.anim.fade_out);
           }
       });

       return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_SIGN_IN_CODE) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                signInWithGoogle(account);
            } catch (ApiException e) {
                deactivateProgressBar();
                Toast.makeText(getContext(), "로그인에 실패하였습니다.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void signInWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    deactivateProgressBar();
                    Intent intent = new Intent(getContext(), MainActivity.class);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.slide_in_right, android.R.anim.fade_out);
                    getActivity().finish();
                }
            }
        });
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