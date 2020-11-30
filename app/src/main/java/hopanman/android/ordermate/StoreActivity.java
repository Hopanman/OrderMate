package hopanman.android.ordermate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ProgressBar;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class StoreActivity extends AppCompatActivity {

    private static final String TAG_FRAGMENT_MAIN = "main";
    private static final String TAG_FRAGMENT_ORDER = "order";
    private static final String TAG_FRAGMENT_MENU = "menu";
    private static final String TAG_FRAGMENT_REVIEW = "review";
    private static final String TAG_FRAGMENT_SETTING = "setting";

    private FragmentManager fragmentManager;
    private Fragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);

        MaterialToolbar toolbar = findViewById(R.id.store_toolbar);
        toolbar.setTitle(getIntent().getStringExtra("storeName"));
        setSupportActionBar(toolbar);

        fragmentManager = getSupportFragmentManager();
        BottomNavigationView navigation = findViewById(R.id.store_navigation);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;

                switch (item.getItemId()) {
                    case R.id.menu_store_order_list:
                        selectedFragment = fragmentManager.findFragmentByTag(TAG_FRAGMENT_MAIN);

                        if (selectedFragment == null) {
                            selectedFragment = new StoreMainFragment();
                            loadFragment(selectedFragment, TAG_FRAGMENT_MAIN);
                        } else {
                            showFragment(selectedFragment, TAG_FRAGMENT_MAIN);
                        }

                        return true;
                    case R.id.menu_store_last_order:
                        selectedFragment = fragmentManager.findFragmentByTag(TAG_FRAGMENT_ORDER);

                        if (selectedFragment == null) {
                            selectedFragment = new StoreOrderFragment();
                            loadFragment(selectedFragment, TAG_FRAGMENT_ORDER);
                        } else {
                            showFragment(selectedFragment, TAG_FRAGMENT_ORDER);
                        }

                        return true;
                    case R.id.menu_store_menu_mgm:
                        selectedFragment = fragmentManager.findFragmentByTag(TAG_FRAGMENT_MENU);

                        if (selectedFragment == null) {
                            selectedFragment = new StoreMenuFragment();
                            loadFragment(selectedFragment, TAG_FRAGMENT_MENU);
                        } else {
                            showFragment(selectedFragment, TAG_FRAGMENT_MENU);
                        }

                        return true;
                    case R.id.menu_store_review_mgm:
                        selectedFragment = fragmentManager.findFragmentByTag(TAG_FRAGMENT_REVIEW);

                        if (selectedFragment == null) {
                            selectedFragment = new StoreReviewFragment();
                            loadFragment(selectedFragment, TAG_FRAGMENT_REVIEW);
                        } else {
                            showFragment(selectedFragment, TAG_FRAGMENT_REVIEW);
                        }

                        return true;
                    case R.id.menu_store_setting:
                        selectedFragment = fragmentManager.findFragmentByTag(TAG_FRAGMENT_SETTING);

                        if (selectedFragment == null) {
                            selectedFragment = new StoreSettingFragment();
                            loadFragment(selectedFragment, TAG_FRAGMENT_SETTING);
                        } else {
                            showFragment(selectedFragment, TAG_FRAGMENT_SETTING);
                        }

                        return true;
                }

                return false;
            }
        });

        loadFragment(new StoreMainFragment(), TAG_FRAGMENT_MAIN);
    }

    private void loadFragment(Fragment fragment, String tag) {
        if (currentFragment != null) {
            fragmentManager.beginTransaction().hide(currentFragment).add(R.id.store_fragment, fragment, tag).commit();
        } else {
            fragmentManager.beginTransaction().add(R.id.store_fragment, fragment, tag).commit();
        }

        currentFragment = fragment;
    }

    private void showFragment(Fragment fragment, String tag) {
        if (!fragment.equals(currentFragment)) {
            fragmentManager.beginTransaction().hide(currentFragment).show(fragment).commit();
            currentFragment = fragment;
        }
    }
}