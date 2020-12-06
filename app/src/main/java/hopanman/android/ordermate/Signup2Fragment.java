package hopanman.android.ordermate;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import hopanman.android.ordermate.databinding.FragmentSignup1Binding;
import hopanman.android.ordermate.databinding.FragmentSignup2Binding;


public class Signup2Fragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup)FragmentSignup2Binding.inflate(inflater).getRoot();

        Button nextButton = rootView.findViewById(R.id.signup_next_button);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(Signup2Fragment.this).navigate(R.id.action_signup2Fragment_to_signup3Fragment);
            }
        });

        return rootView;
    }
}