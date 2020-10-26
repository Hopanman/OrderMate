package hopanman.android.ordermate;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import hopanman.android.ordermate.databinding.FragmentLoginCustomerBinding;


public class LoginCustomerFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return FragmentLoginCustomerBinding.inflate(inflater).getRoot();
    }
}