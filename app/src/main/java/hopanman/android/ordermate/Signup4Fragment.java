package hopanman.android.ordermate;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import hopanman.android.ordermate.databinding.FragmentSignup3Binding;
import hopanman.android.ordermate.databinding.FragmentSignup4Binding;


public class Signup4Fragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup)FragmentSignup4Binding.inflate(inflater).getRoot();

        Button nextButton = rootView.findViewById(R.id.signup_next_button);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return rootView;
    }
}