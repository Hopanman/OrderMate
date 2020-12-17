package hopanman.android.ordermate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class CustomerActivity extends AppCompatActivity {

    private static final String TAG_FRAGMENT_MAIN = "main";
    private static final String TAG_FRAGMENT_ORDER = "order";
    private static final String TAG_FRAGMENT_FAVORITE = "favorite";
    private static final String TAG_FRAGMENT_REVIEW = "review";
    private static final String TAG_FRAGMENT_PROFILE = "profile";

    private FragmentManager fragmentManager;
    private Fragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer);

        fragmentManager = getSupportFragmentManager();
        BottomNavigationView navigation = findViewById(R.id.customer_navigation);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;

                switch (item.getItemId()) {
                    case R.id.menu_customer_home:
                        selectedFragment = fragmentManager.findFragmentByTag(TAG_FRAGMENT_MAIN);

                        if (selectedFragment == null) {
                            selectedFragment = new CustomerMainFragment();
                            loadFragment(selectedFragment, TAG_FRAGMENT_MAIN);
                        } else {
                            showFragment(selectedFragment, TAG_FRAGMENT_MAIN);
                        }

                        return true;
                    case R.id.menu_customer_order_list:
                        selectedFragment = fragmentManager.findFragmentByTag(TAG_FRAGMENT_ORDER);

                        if (selectedFragment == null) {
                            selectedFragment = new CustomerOrderFragment();
                            loadFragment(selectedFragment, TAG_FRAGMENT_ORDER);
                        } else {
                            showFragment(selectedFragment, TAG_FRAGMENT_ORDER);
                        }

                        return true;
                    case R.id.menu_customer_favorite_store:
                        selectedFragment = fragmentManager.findFragmentByTag(TAG_FRAGMENT_FAVORITE);

                        if (selectedFragment == null) {
                            selectedFragment = new CustomerFavoriteFragment();
                            loadFragment(selectedFragment, TAG_FRAGMENT_FAVORITE);
                        } else {
                            showFragment(selectedFragment, TAG_FRAGMENT_FAVORITE);
                        }

                        return true;
                    case R.id.menu_customer_review_list:
                        selectedFragment = fragmentManager.findFragmentByTag(TAG_FRAGMENT_REVIEW);

                        if (selectedFragment == null) {
                            selectedFragment = new CustomerReviewFragment();
                            loadFragment(selectedFragment, TAG_FRAGMENT_REVIEW);
                        } else {
                            showFragment(selectedFragment, TAG_FRAGMENT_REVIEW);
                        }

                        return true;
                    case R.id.menu_customer_profile:
                        selectedFragment = fragmentManager.findFragmentByTag(TAG_FRAGMENT_PROFILE);

                        if (selectedFragment == null) {
                            selectedFragment = new CustomerProfileFragment();
                            loadFragment(selectedFragment, TAG_FRAGMENT_PROFILE);
                        } else {
                            showFragment(selectedFragment, TAG_FRAGMENT_PROFILE);
                        }

                        return true;
                }

                return false;
            }
        });

        loadFragment(new CustomerMainFragment(), TAG_FRAGMENT_MAIN);

        FirebaseAuth.getInstance().signOut();
    }

    private void loadFragment(Fragment fragment, String tag) {
        if (currentFragment != null) {
            fragmentManager.beginTransaction().hide(currentFragment).add(R.id.customer_fragment, fragment, tag).commit();
        } else {
            fragmentManager.beginTransaction().add(R.id.customer_fragment, fragment, tag).commit();
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