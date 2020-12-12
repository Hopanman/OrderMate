package hopanman.android.ordermate;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import hopanman.android.ordermate.databinding.FragmentSignup4Binding;


public class Signup4Fragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup)FragmentSignup4Binding.inflate(inflater).getRoot();
//        Log.d("Signup4Fragment", Signup4FragmentArgs.fromBundle(getArguments()).getCustomerEmail());
//        Log.d("Signup4Fragment", Signup4FragmentArgs.fromBundle(getArguments()).getCustomerPassword());
//        Log.d("Signup4Fragment", Signup4FragmentArgs.fromBundle(getArguments()).getCustomerName());
        View backButton = rootView.findViewById(R.id.signup_button_back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        Button nextButton = rootView.findViewById(R.id.signup_next_button);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return rootView;
    }
}