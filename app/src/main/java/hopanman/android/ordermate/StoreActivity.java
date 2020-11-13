package hopanman.android.ordermate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ProgressBar;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class StoreActivity extends AppCompatActivity {

    private StoreMainFragment mainFragment;
    private StoreOrderFragment orderFragment;
    private StoreMenuFragment menuFragment;
    private StoreReviewFragment reviewFragment;
    private StoreSettingFragment settingFragment;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);

        MaterialToolbar toolbar = findViewById(R.id.store_toolbar);
        toolbar.setTitle(getIntent().getStringExtra("storeName"));
        setSupportActionBar(toolbar);

        mainFragment = new StoreMainFragment();
        orderFragment = new StoreOrderFragment();
        menuFragment = new StoreMenuFragment();
        reviewFragment = new StoreReviewFragment();
        settingFragment = new StoreSettingFragment();

        progressBar = findViewById(R.id.progressBar);

        BottomNavigationView navigation = findViewById(R.id.store_navigation);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_store_order_list:
                        getSupportFragmentManager().beginTransaction().replace(R.id.store_fragment, mainFragment).commit();
                        return true;
                    case R.id.menu_store_last_order:
                        getSupportFragmentManager().beginTransaction().replace(R.id.store_fragment, orderFragment).commit();
                        return true;
                    case R.id.menu_store_menu_mgm:
                        getSupportFragmentManager().beginTransaction().replace(R.id.store_fragment, menuFragment).commit();
                        return true;
                    case R.id.menu_store_review_mgm:
                        getSupportFragmentManager().beginTransaction().replace(R.id.store_fragment, reviewFragment).commit();
                        return true;
                    case R.id.menu_store_setting:
                        getSupportFragmentManager().beginTransaction().replace(R.id.store_fragment, settingFragment).commit();
                        return true;
                }

                return false;
            }
        });
    }
}