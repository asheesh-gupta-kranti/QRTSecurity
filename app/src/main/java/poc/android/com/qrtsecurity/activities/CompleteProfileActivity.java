package poc.android.com.qrtsecurity.activities;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import poc.android.com.qrtsecurity.R;

public class CompleteProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int CAMERA_REQUEST = 1001, GALLERY_REQUEST = 1002;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;

    private EditText etName, etDOB, etVehicleModel, etVehicleRegNo, etDrivingNo;
    private Button btnUpload;
    private ImageButton btnProfile;
    private ImageView ivUploaded, ivProfile;

    private int imageType = 0; //0: Licence 1: Profile

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
        ivUploaded = findViewById(R.id.iv_uploaded);
        ivProfile = findViewById(R.id.iv_profile);

        btnUpload.setOnClickListener(this);
        btnProfile.setOnClickListener(this);
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
}

