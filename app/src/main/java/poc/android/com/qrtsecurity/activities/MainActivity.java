package poc.android.com.qrtsecurity.activities;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import poc.android.com.qrtsecurity.R;
import poc.android.com.qrtsecurity.fragments.EnterPasswordFragment;
import poc.android.com.qrtsecurity.fragments.SignInFragment;

public class MainActivity extends AppCompatActivity {

    private SignInFragment signInFragment;
    private EnterPasswordFragment enterPasswordFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // set UI Elements
        setUI();
    }

    /**
     * Method to set the UI Elements
     */
    private void setUI(){
        // add Sign in fragment
        addSignInFragment();
    }

    /**
     * Method to add signInFragment
     */
    public void addSignInFragment(){

        if (signInFragment == null)
            signInFragment = new SignInFragment();

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.rl_container, signInFragment);
        ft.commit();
    }

    public void addPassswordFragment(){
        if (enterPasswordFragment == null)
            enterPasswordFragment = new EnterPasswordFragment();

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.rl_container, enterPasswordFragment);
        ft.addToBackStack("Enter password");
        ft.commit();
    }
}
