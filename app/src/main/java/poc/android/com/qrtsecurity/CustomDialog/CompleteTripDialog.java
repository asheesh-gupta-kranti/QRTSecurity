package poc.android.com.qrtsecurity.CustomDialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import poc.android.com.qrtsecurity.Models.NotificationModel;
import poc.android.com.qrtsecurity.R;

public class CompleteTripDialog extends Dialog {

    private NotificationModel data;
    public boolean isCancelTrip = false;

    private TextView tvMessage;
    private EditText etOTP;
    private Button btnCancel, btnSubmit;

    public CompleteTripDialog(Context context, NotificationModel data) {
        super(context);

        this.data = data;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_complete_trip);

        tvMessage = findViewById(R.id.tv_message);
        etOTP = findViewById(R.id.et_otp);
        btnCancel = findViewById(R.id.btn_cancel);
        btnSubmit = findViewById(R.id.btn_submit);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dismiss();

            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!etOTP.getText().toString().isEmpty() && etOTP.getText().toString().equalsIgnoreCase("4567")){
                    isCancelTrip = true;
                    dismiss();

                }else{
                    Toast.makeText(getContext(), "Please enter correct OTP.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
