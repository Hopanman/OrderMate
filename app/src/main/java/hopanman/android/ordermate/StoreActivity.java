package hopanman.android.ordermate;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.android.material.appbar.MaterialToolbar;

public class StoreActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);

        MaterialToolbar toolbar = findViewById(R.id.store_toolbar);
        toolbar.setTitle(getIntent().getStringExtra("storeName"));
        setSupportActionBar(toolbar);
    }
}