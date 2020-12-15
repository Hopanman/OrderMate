package hopanman.android.ordermate;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.regex.Pattern;

import hopanman.android.ordermate.databinding.FragmentLoginCustomerBinding;


public class LoginCustomerFragment extends Fragment {

    private TextInputEditText editText;
    private TextInputEditText editText2;
    private ProgressBar progressBar;
    private Window window;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
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

                if (!isAvailableEmail(email)) {
                    editText.requestFocus();
                    editText.setError("이메일을 올바르게 입력해주세요.");
                    return;
                }

                String password = editText2.getText().toString();
                if (password.equals("") || password == null) {
                    editText2.requestFocus();
                    editText2.setError("비밀번호를 입력해주세요.");
                    return;
                }

                if (isStoreAccount(email)) {
                    Toast.makeText(getContext(), "이메일 혹은 비밀번호가 일치하지 않습니다", Toast.LENGTH_LONG).show();
                    return;
                }

                activateProgressBar();
                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            deactivateProgressBar();
                            Intent intent = new Intent(getContext(), CustomerActivity.class);
                            startActivity(intent);
                            getActivity().overridePendingTransition(R.anim.slide_in_right, android.R.anim.fade_out);
                            getActivity().finish();
                        } else {
                            deactivateProgressBar();
                            Toast.makeText(getContext(), "이메일 혹은 비밀번호가 일치하지 않습니다", Toast.LENGTH_LONG).show();
                        }
                    }
                });
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
                db = FirebaseFirestore.getInstance();
                db.collection("customers")
                        .whereEqualTo("customerEmail", account.getEmail())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    QuerySnapshot result = task.getResult();

                                    if (result.isEmpty()) {
                                        deactivateProgressBar();
                                        View view = LayoutInflater.from(getContext()).inflate(R.layout.view_dialog_edittext, null);
                                        TextInputLayout phoneLayout = view.findViewById(R.id.edittext_layout);
                                        phoneLayout.setHint("휴대폰번호");
                                        TextInputEditText phoneEditText = phoneLayout.findViewById(R.id.edittext);
                                        phoneEditText.setInputType(InputType.TYPE_CLASS_PHONE);
                                        InputMethodManager inputManager = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                                        phoneEditText.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                phoneEditText.requestFocus();
                                                inputManager.showSoftInput(phoneEditText, 0);
                                            }
                                        }, 200);

                                        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
                                        AlertDialog dialog = builder.setTitle(R.string.signup).setView(view).setPositiveButton(R.string.done, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                activateProgressBar();
                                                String customerTel = phoneEditText.getText().toString();
                                                signInWithGoogle(account, customerTel);
                                            }
                                        }).setNegativeButton(R.string.dialog_negative_button, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Toast.makeText(getContext(), "로그인에 실패하였습니다.", Toast.LENGTH_LONG).show();
                                            }
                                        }).setCancelable(false).show();
                                        Button doneButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                                        doneButton.setEnabled(false);

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
                                                    doneButton.setEnabled(true);
                                                } else {
                                                    doneButton.setEnabled(false);
                                                }
                                            }
                                        });
                                    } else {
                                        signInWithGoogle(account, null);
                                    }
                                } else {
                                    deactivateProgressBar();
                                    Toast.makeText(getContext(), R.string.toast_problem_occurred, Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            } catch (ApiException e) {
                deactivateProgressBar();
                Toast.makeText(getContext(), "로그인에 실패하였습니다.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void signInWithGoogle(GoogleSignInAccount account, String customerTel) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    if (customerTel != null) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        Customer customer = new Customer(user.getEmail(), user.getDisplayName(), customerTel);
                        db.collection("customers")
                                .document(user.getUid())
                                .set(customer)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        deactivateProgressBar();
                                        Intent intent = new Intent(getContext(), CustomerActivity.class);
                                        startActivity(intent);
                                        getActivity().overridePendingTransition(R.anim.slide_in_right, android.R.anim.fade_out);
                                        getActivity().finish();
                                    }
                                });
                    } else {
                        deactivateProgressBar();
                        Intent intent = new Intent(getContext(), CustomerActivity.class);
                        startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.slide_in_right, android.R.anim.fade_out);
                        getActivity().finish();
                    }
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

    private boolean isAvailableEmail(String email) {
        String regex = "^[a-z0-9-_]+(\\.[a-z0-9-_]+)*@([a-z0-9-]+\\.)+[a-z]{2,6}$";
        boolean result = Pattern.compile(regex).matcher(email).matches();

        return result;
    }

    private boolean isAvailablePhone(String phone) {
        String regex = "^01[016789]{1}-[0-9]{3,4}-[0-9]{4}$";
        boolean result = Pattern.compile(regex).matcher(phone).matches();

        return result;
    }

    private boolean isStoreAccount(String email) {
        String regex = "^[a-z0-9-_]+(\\.[a-z0-9-_]+)*@ordermate.co.kr$";
        boolean result = Pattern.compile(regex).matcher(email).matches();

        return result;
    }
}