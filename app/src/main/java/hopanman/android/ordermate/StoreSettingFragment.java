package hopanman.android.ordermate;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Pattern;

import hopanman.android.ordermate.databinding.FragmentStoreSettingBinding;


public class StoreSettingFragment extends Fragment {

    private ViewGroup passwordRow;
    private ViewGroup logoutRow;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private ProgressBar progressBar;
    private Window window;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup)FragmentStoreSettingBinding.inflate(inflater).getRoot();

        progressBar = ((StoreActivity)getActivity()).progressBar;
        window = ((StoreActivity)getActivity()).getWindow();

        passwordRow = rootView.findViewById(R.id.password_change);
        passwordRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = LayoutInflater.from(getContext()).inflate(R.layout.view_dialog_edittext, null);
                TextInputLayout layout = view.findViewById(R.id.edittext_layout);
                layout.setHint("현재 비밀번호");
                TextInputEditText editText = layout.findViewById(R.id.edittext);
                editText.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
                InputMethodManager inputManager = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                editText.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        editText.requestFocus();
                        inputManager.showSoftInput(editText, 0);
                    }
                }, 200);

                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
                builder.setTitle(R.string.password_change).setView(view).setPositiveButton(R.string.dialog_positive_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String curPassword = editText.getText().toString();
                        FirebaseUser user = mAuth.getCurrentUser();

                        progressBar.setVisibility(View.VISIBLE);
                        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                        if (curPassword == null || curPassword.equals("")) {
                            progressBar.setVisibility(View.INVISIBLE);
                            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                            Toast.makeText(getContext(), "비밀번호가 일치하지 않습니다.", Toast.LENGTH_LONG).show();
                            return;
                        }

                        if (user != null) {
                            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), curPassword);
                            user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                    if(task.isSuccessful()) {
                                        processPasswordChange();
                                    } else {
                                        Toast.makeText(getContext(), "비밀번호가 일치하지 않습니다.", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }
                    }
                }).setNegativeButton(R.string.dialog_negative_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).setCancelable(false).show();
            }
        });

        logoutRow = rootView.findViewById(R.id.logout);
        logoutRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
                builder.setMessage(R.string.logout_dialog_body).setPositiveButton(R.string.dialog_positive_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mAuth.signOut();
                        Intent intent = new Intent(getContext(), LoginActivity.class);
                        startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.slide_in_right, android.R.anim.fade_out);
                        getActivity().finish();
                    }
                }).setNegativeButton(R.string.dialog_negative_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
            }
        });

        return rootView;
    }

    private void processPasswordChange() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout rootLayout = new LinearLayout(getContext());
        rootLayout.setLayoutParams(params);
        rootLayout.setOrientation(LinearLayout.VERTICAL);

        View view1 = LayoutInflater.from(getContext()).inflate(R.layout.view_dialog_edittext, null);
        TextInputLayout layout1 = view1.findViewById(R.id.edittext_layout);
        layout1.setHint("새 비밀번호");
        TextInputEditText editText1 = layout1.findViewById(R.id.edittext);
        editText1.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
        editText1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!isAvailablePassword(s.toString())) {
                    layout1.setError("영문,숫자,특수문자 포함 8자 이상 입력해주세요");
                } else {
                    layout1.setError(null);
                }
            }
        });
        InputMethodManager inputManager = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        editText1.postDelayed(new Runnable() {
            @Override
            public void run() {
                editText1.requestFocus();
                inputManager.showSoftInput(editText1, 0);
            }
        }, 200);

        View view2 = LayoutInflater.from(getContext()).inflate(R.layout.view_dialog_edittext, null);
        TextInputLayout layout2 = view2.findViewById(R.id.edittext_layout);
        layout2.setHint("새 비밀번호 확인");
        TextInputEditText editText2 = layout2.findViewById(R.id.edittext);
        editText2.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
        editText2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().equals(editText1.getText().toString())) {
                    layout2.setError("비밀번호가 일치하지 않습니다");
                } else {
                    layout2.setError(null);
                }
            }
        });

        rootLayout.addView(view1);
        rootLayout.addView(view2);

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
        builder.setTitle(R.string.password_change).setView(rootLayout).setPositiveButton(R.string.dialog_positive_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newPassword = editText1.getText().toString();
                if (!isAvailablePassword(newPassword)) {
                    Toast.makeText(getContext(), R.string.toast_password_incorrect, Toast.LENGTH_LONG).show();
                    processPasswordChange();
                } else if (!newPassword.equals(editText2.getText().toString())) {
                    Toast.makeText(getContext(), R.string.toast_password_confirm_unmatch, Toast.LENGTH_LONG).show();
                    processPasswordChange();
                } else {
                    FirebaseUser user = mAuth.getCurrentUser();

                    progressBar.setVisibility(View.VISIBLE);
                    window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                    user.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            progressBar.setVisibility(View.INVISIBLE);
                            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                            if (task.isSuccessful()) {
                                Toast.makeText(getContext(), R.string.toast_password_updated, Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getContext(), R.string.toast_password_updated_failed, Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        }).setNegativeButton(R.string.dialog_negative_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).setCancelable(false).show();
    }

    private boolean isAvailablePassword(String password) {
        String regex = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$";
        boolean result = Pattern.compile(regex).matcher(password).matches();

        return result;
    }
}