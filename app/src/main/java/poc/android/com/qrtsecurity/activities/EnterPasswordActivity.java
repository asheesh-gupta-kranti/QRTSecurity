package poc.android.com.qrtsecurity.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import poc.android.com.qrtsecurity.R;

public class EnterPasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_password);
        // Method to set the UI
        setUI();
    }

    /**
     * Method to set the layout and UI elements
     */
    void setUI(){
        findViewById(R.id.btn_forgot_password).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EnterPasswordActivity.this, CreatePasswordActivity.class));
            }
        });
    }
}
