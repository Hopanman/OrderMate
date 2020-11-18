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
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.regex.Pattern;

import hopanman.android.ordermate.databinding.FragmentStoreSettingBinding;


public class StoreSettingFragment extends Fragment {

    private TextView storeNameView, storeTelView;
    private SwitchMaterial storeOpenSwitch;
    private ViewGroup passwordRow, logoutRow;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser user;
    private DocumentReference storeRef;
    private ProgressBar progressBar;
    private Window window;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup)FragmentStoreSettingBinding.inflate(inflater).getRoot();

        user = mAuth.getCurrentUser();
        if (user != null) storeRef = FirebaseFirestore.getInstance().collection("stores").document(user.getUid());

        progressBar = ((StoreActivity)getActivity()).progressBar;
        window = ((StoreActivity)getActivity()).getWindow();

        storeNameView = rootView.findViewById(R.id.store_name);
        ImageButton storeNameButton = rootView.findViewById(R.id.store_name_button);
        storeNameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = LayoutInflater.from(getContext()).inflate(R.layout.view_dialog_edittext, null);
                TextInputLayout layout = view.findViewById(R.id.edittext_layout);
                layout.setHint("가게이름");
                TextInputEditText editText = layout.findViewById(R.id.edittext);
                editText.setText(storeNameView.getText());
                editText.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_NORMAL);
                InputMethodManager inputManager = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                editText.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        editText.requestFocus();
                        editText.setSelection(storeNameView.getText().length());
                        inputManager.showSoftInput(editText, 0);
                    }
                }, 200);

                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
                builder.setView(view).setPositiveButton(R.string.dialog_positive_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String storeName = editText.getText().toString();
                        if (storeName == null || storeName.equals("") || storeName.equals(storeNameView.getText().toString())) return;

                        if (storeRef != null) {
                            activateProgressBar();
                            storeRef.update("storeName", storeName).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    deactivateProgressBar();
                                    storeNameView.setText(storeName);
                                    ((StoreActivity) getActivity()).getSupportActionBar().setTitle(storeName);
                                    Toast.makeText(getContext(), "가게이름이 성공적으로 변경되었습니다.", Toast.LENGTH_LONG).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    deactivateProgressBar();
                                    Toast.makeText(getContext(), "가게이름 변경에 실패하였습니다.", Toast.LENGTH_LONG).show();
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

        storeOpenSwitch = rootView.findViewById(R.id.store_open);
        storeOpenSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (storeRef != null) {
                    activateProgressBar();
                    storeRef.update("isOpen", isChecked).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            deactivateProgressBar();
                            if (isChecked) {
                                Toast.makeText(getContext(), "가게가 열렸습니다.", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getContext(), "가게가 닫혔습니다.", Toast.LENGTH_LONG).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            deactivateProgressBar();
                            buttonView.setChecked(!isChecked);
                            Toast.makeText(getContext(), "문제가 발생하였습니다. 관리자에게 문의해주세요.", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });

        ViewGroup storeTelRow = rootView.findViewById(R.id.store_tel);
        storeTelView = storeTelRow.findViewById(R.id.contents);
        storeTelRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processStoreTelChange();
            }
        });

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

                        activateProgressBar();

                        if (curPassword == null || curPassword.equals("")) {
                            deactivateProgressBar();

                            Toast.makeText(getContext(), "비밀번호가 일치하지 않습니다.", Toast.LENGTH_LONG).show();
                            return;
                        }

                        if (user != null) {
                            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), curPassword);
                            user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    deactivateProgressBar();

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
        ScrollView scrollView = new ScrollView(getContext());
        scrollView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
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
        scrollView.addView(rootLayout);

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
        builder.setTitle(R.string.password_change).setView(scrollView).setPositiveButton(R.string.dialog_positive_button, new DialogInterface.OnClickListener() {
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
                    activateProgressBar();

                    user.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            deactivateProgressBar();

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

    private void processStoreTelChange() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.view_dialog_edittext, null);
        TextInputLayout layout = view.findViewById(R.id.edittext_layout);
        layout.setHint("전화번호");
        TextInputEditText editText = layout.findViewById(R.id.edittext);
        editText.setText(storeTelView.getText());
        editText.setInputType(InputType.TYPE_CLASS_PHONE);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!isAvailableTel(s.toString())) {
                    layout.setError("전화번호를 '-' 포함해서 올바르게 입력해주세요");
                } else {
                    layout.setError(null);
                }
            }
        });
        InputMethodManager inputManager = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        editText.postDelayed(new Runnable() {
            @Override
            public void run() {
                editText.requestFocus();
                editText.setSelection(storeTelView.getText().length());
                inputManager.showSoftInput(editText, 0);
            }
        }, 200);

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
        builder.setView(view).setPositiveButton(R.string.dialog_positive_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String storeTel = editText.getText().toString();
                if (storeTel == null || storeTel.equals("") || storeTel.equals(storeTelView.getText().toString())) return;
                else if (!isAvailableTel(storeTel)) {
                    Toast.makeText(getContext(), "전화번호를 올바르게 입력해주세요.", Toast.LENGTH_LONG).show();
                    processStoreTelChange();
                } else {
                    if (storeRef != null) {
                        activateProgressBar();
                        storeRef.update("storeTel", storeTel).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                deactivateProgressBar();
                                storeTelView.setText(storeTel);
                                Toast.makeText(getContext(), "전화번호가 성공적으로 변경되었습니다.", Toast.LENGTH_LONG).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                deactivateProgressBar();
                                Toast.makeText(getContext(), "전화번호 변경에 실패하였습니다.", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
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

    private boolean isAvailableTel(String tel) {
        String regex = "^(01[016789]{1}|02|0[3-9]{1}[0-9]{1})-[0-9]{3,4}-[0-9]{4}$";
        boolean result = Pattern.compile(regex).matcher(tel).matches();

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