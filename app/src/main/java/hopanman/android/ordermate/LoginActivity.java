package hopanman.android.ordermate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.widget.ProgressBar;

import com.google.android.material.tabs.TabLayout;

public class LoginActivity extends AppCompatActivity {

    private LoginCustomerFragment customerFragment;
    private LoginStoreFragment storeFragment;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        
        customerFragment = new LoginCustomerFragment();
        storeFragment = new LoginStoreFragment();

        progressBar = findViewById(R.id.progressBar);

        TabLayout tabs = findViewById(R.id.login_tabs);
        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();

                Fragment selected = null;
                if (position == 0) {
                    selected = customerFragment;
                } else if (position == 1) {
                    selected = storeFragment;
                }

                getSupportFragmentManager().beginTransaction().replace(R.id.login_fragment, selected).commit();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
}