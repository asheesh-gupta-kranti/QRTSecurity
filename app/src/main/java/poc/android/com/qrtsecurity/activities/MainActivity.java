package poc.android.com.qrtsecurity.activities;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import poc.android.com.qrtsecurity.R;
import poc.android.com.qrtsecurity.fragments.CreatePasswordFragment;
import poc.android.com.qrtsecurity.fragments.EnterPasswordFragment;
import poc.android.com.qrtsecurity.fragments.SignInFragment;

public class MainActivity extends AppCompatActivity {

    private SignInFragment signInFragment;
    private EnterPasswordFragment enterPasswordFragment;
    private CreatePasswordFragment createPasswordFragment;

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

    /**
     * Method to add the password fragment
     */
    public void addPasswordFragment(){
        if (enterPasswordFragment == null)
            enterPasswordFragment = new EnterPasswordFragment();

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.rl_container, enterPasswordFragment);
        ft.addToBackStack("Enter password");
        ft.commit();
    }

    /**
     * Method to add the password fragment
     */
    public void addCreatePassswordFragment(){
        if (createPasswordFragment == null)
            createPasswordFragment = new CreatePasswordFragment();

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.rl_container, createPasswordFragment);
        ft.addToBackStack("Create password");
        ft.commit();
    }
}
