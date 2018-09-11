package poc.android.com.qrtsecurity.activities;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import poc.android.com.qrtsecurity.AppController;
import poc.android.com.qrtsecurity.R;
import poc.android.com.qrtsecurity.utils.AppPreferencesHandler;
import poc.android.com.qrtsecurity.utils.Constants;
import poc.android.com.qrtsecurity.utils.HelperMethods;
import poc.android.com.qrtsecurity.volleyWrapperClasses.UTF8JsonObjectRequest;

public class CompleteProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int CAMERA_REQUEST = 1001, GALLERY_REQUEST = 1002;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;

    private EditText etName, etDOB, etVehicleModel, etVehicleRegNo, etDrivingNo;
    private TextInputLayout tilName, tilVehicleModel, tilVehicleRegNo, tilDrivingNo, tilDOB;
    private Button btnUpload, btnSubmit;
    private ImageButton btnProfile;
    private ImageView ivUploaded, ivProfile;
    private RadioGroup rgGender;
    private ProgressBar progressBar;

    private int imageType = 0; //0: Licence 1: Profile
    private String gender = "MALE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_profile);

        setUI();
    }

    /**
     * method to set the ui elements
     */
    private void setUI() {
        btnUpload = findViewById(R.id.btn_upload);
        btnProfile = findViewById(R.id.btn_profile);
        btnSubmit = findViewById(R.id.btn_submit);

        ivUploaded = findViewById(R.id.iv_uploaded);
        ivProfile = findViewById(R.id.iv_profile);

        etDOB = findViewById(R.id.et_dob);
        etName = findViewById(R.id.et_name);
        etVehicleModel = findViewById(R.id.et_vehicle_model);
        etVehicleRegNo = findViewById(R.id.et_vehicle_reg_no);
        etDrivingNo = findViewById(R.id.et_driving_licence);

        tilName = findViewById(R.id.til_name);
        tilDOB = findViewById(R.id.til_dob);
        tilVehicleModel = findViewById(R.id.til_vehicle_model);
        tilVehicleRegNo = findViewById(R.id.til_vehicle_reg_no);
        tilDrivingNo = findViewById(R.id.til_driving_licence);

        progressBar = findViewById(R.id.progressBar);
        rgGender = findViewById(R.id.rg_gender);

        btnUpload.setOnClickListener(this);
        btnProfile.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
        etDOB.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    etDOB.clearFocus();
                    showDialogDOB();
                }

            }
        });

        rgGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {

                RadioButton checkedRadioButton = (RadioButton)radioGroup.findViewById(checkedId);
                if (checkedId == R.id.rb_male && checkedRadioButton.isChecked()){
                    gender = "MALE";
                }else if(checkedId == R.id.rb_female && checkedRadioButton.isChecked()){
                    gender = "FEMALE";
                }
            }
        });
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.btn_upload:
                imageType = 0;
                showUploadOptionDialog();
                break;

            case R.id.btn_profile:
                imageType = 1;
                showUploadOptionDialog();
                break;

            case R.id.btn_submit:

                if (validateData()){
                    updateUserProfile(etName.getText().toString(), etDOB.getText().toString(), etVehicleModel.getText().toString(),
                            etVehicleRegNo.getText().toString(), etDrivingNo.getText().toString(), gender);
                }
                break;
        }
    }

    /**
     * method to check if all the fields have valid data
     * @return
     */
    private boolean validateData(){

        if (etName.getText().toString().isEmpty()){
            tilName.setErrorEnabled(true);
            tilName.setError(getString(R.string.this_field_empty));
            return false;
        }else if (etDOB.getText().toString().isEmpty()){
            tilDOB.setErrorEnabled(true);
            tilDOB.setError(getString(R.string.this_field_empty));
            return  false;
        } else if (etVehicleModel.getText().toString().isEmpty()){
            tilVehicleModel.setErrorEnabled(true);
            tilVehicleModel.setError(getString(R.string.this_field_empty));
            return false;
        }else if (etVehicleRegNo.getText().toString().isEmpty()){
            tilVehicleRegNo.setErrorEnabled(true);
            tilVehicleRegNo.setError(getString(R.string.this_field_empty));
            return false;
        }else if (etDrivingNo.getText().toString().isEmpty()){
            tilDrivingNo.setErrorEnabled(true);
            tilDrivingNo.setError(getString(R.string.this_field_empty));
            return false;
        }

        return true;
    }


    /**
     * method to update responder profile
      * @param name
     * @param dob
     * @param vehicleModel
     * @param vehicleRegNo
     * @param drivingLicence
     */
    private void updateUserProfile(String name, String dob, String vehicleModel, String vehicleRegNo, String drivingLicence, String gender) {

        String url = Constants.baseUrl + Constants.responderProfileUpdateEndPoint +"?where={\"responderId\":"
                +AppPreferencesHandler.getUserId(this) +"}" +  "&access_token=" + AppPreferencesHandler.getUserToken(this) ;
        Log.d("url", url);
        JSONObject payload = new JSONObject();
        try{

            payload.put("responderName", name);
            payload.put("dob", dob);
            payload.put("vehicleRegNo", vehicleRegNo);
            payload.put("licenceNo", drivingLicence);
            payload.put("gender", gender);
        }catch (Exception ex){
            ex.printStackTrace();
            return;
        }
        Log.d("payload", payload.toString());
        if (HelperMethods.isNetWorkAvailable(this)) {

            UTF8JsonObjectRequest request = new UTF8JsonObjectRequest(Request.Method.POST, url, payload, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d("registerUser response", response.toString());
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(CompleteProfileActivity.this, getString(R.string.profile_update_success), Toast.LENGTH_SHORT)
                            .show();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("error", ""+error);
                    Toast.makeText(CompleteProfileActivity.this, getString(R.string.general_error), Toast.LENGTH_SHORT)
                            .show();
                    progressBar.setVisibility(View.GONE);

                }
            }){

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {

                    Map<String, String> header = new HashMap<>();
                    header.put("content-type",
                            "application/json");

                    return header;
                }
            };

            RetryPolicy retryPolicy = new DefaultRetryPolicy(
                    AppController.VOLLEY_TIMEOUT,
                    AppController.VOLLEY_MAX_RETRIES,
                    AppController.VOLLEY_BACKUP_MULT);
            request.setRetryPolicy(retryPolicy);
            AppController.getInstance().addToRequestQueue(request);

        } else {
            Toast.makeText(this, getString(R.string.internet_error), Toast.LENGTH_SHORT)
                    .show();
            progressBar.setVisibility(View.GONE);
        }
    }

    // method to choose image upload options
    private void showUploadOptionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(getString(R.string.add_image));
        builder.setPositiveButton(getString(R.string.camera), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                uploadPhotoUsingCamera();
                dialogInterface.dismiss();
            }
        });
        builder.setNegativeButton(getString(R.string.gallery), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                uploadPhotoUsingGallery();
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

    /**
     * method to open camera
     */
    private void uploadPhotoUsingCamera() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                checkSelfPermission(Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA},
                    MY_CAMERA_PERMISSION_CODE);
        } else {
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, CAMERA_REQUEST);
        }
    }

    private void uploadPhotoUsingGallery() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto, GALLERY_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new
                        Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }

        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) intent.getExtras().get("data");
//            Uri selectedImage = intent.getData();
            if (imageType == 0)
                ivUploaded.setImageBitmap(photo);
            else
                ivProfile.setImageBitmap(photo);
        } else if (requestCode == GALLERY_REQUEST && resultCode == Activity.RESULT_OK) {
//            Uri selectedImage = intent.getData();
//            ivUploaded.setImageURI(selectedImage);
            Uri selectedImage = intent.getData();
            if (imageType == 0)
                ivUploaded.setImageURI(selectedImage);
            else
                ivProfile.setImageURI(selectedImage);
        }
    }

    /**
     * method to show the dob dialog
     */
    private void showDialogDOB() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        etDOB.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                    }
                }, 1990, 1, 1);
        datePickerDialog.show();
    }

}

