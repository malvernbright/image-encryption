package com.mgsuperuser.imageencryption;

import android.Manifest;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.mgsuperuser.imageencryption.Utils.MyEncrypter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.crypto.NoSuchPaddingException;

public class MainActivity extends AppCompatActivity {

    String FILE_NAME_ENC = "android_hood_enc.enc";
    String FILE_NAME_DEC = "android_hood_dec.png";
    Button btn_enc, btn_decr;
    ImageView imageView;


    // get encryption, decryption keys
    String my_key = "hEaUIJK0ZX4x6HMe";
    String my_spec_key = "QsXy08ekdL2bTWt9"; // 16 chars = 128 bit

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_decr = findViewById(R.id.decr_btn);
        btn_enc = findViewById(R.id.encr_btn);
        imageView = findViewById(R.id.image_view);
        FileOutputStream fileOutputStream;

        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        btn_decr.setEnabled(true);
                        btn_enc.setEnabled(true);
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        showMessage("You should enable permissions first!");
                    }
                }).check();
        // Init path
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/saved_images");
        myDir.mkdirs();

//        String fname = filename;
        if (myDir.exists()) {
            showMessage("Directory exists");
        }

        btn_enc.setOnClickListener(v -> {
            // Convert drawable to Bitmap
            String dir = "storage/emulated/0/saved_images/android.png";
            Drawable drawable = ContextCompat.getDrawable(this, R.drawable.african);
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            Bitmap bitmap = bitmapDrawable.getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            InputStream inputStream = new ByteArrayInputStream(stream.toByteArray());

            // Create file
            File outputFileEnc = new File(myDir, FILE_NAME_ENC);
            if (outputFileEnc.exists()) {
                outputFileEnc.delete();
            }
            try {
                outputFileEnc.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                MyEncrypter.encryptToFile(my_key, my_spec_key, inputStream, new FileOutputStream(outputFileEnc));
                showMessage("Encrypted!");
            } catch (IOException e) {
                e.printStackTrace();
                showMessage(e.toString());
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            } catch (InvalidAlgorithmParameterException e) {
                e.printStackTrace();
            } catch (NoSuchPaddingException e) {
                e.printStackTrace();
            }
        });

        btn_decr.setOnClickListener(v -> {
            File outputFileDec = new File(myDir, FILE_NAME_DEC);
            File encFile = new File(myDir, FILE_NAME_ENC);

            try {
                MyEncrypter.decryptToFile(my_key, my_spec_key, new FileInputStream(encFile),
                        new FileOutputStream(outputFileDec));

                // After that, set for imageView
                imageView.setImageURI(Uri.fromFile(outputFileDec));
                // to delete the file:
//                outputFileDec.delete();
                showMessage("Decrypted!");
            } catch (IOException | NoSuchAlgorithmException | InvalidKeyException | InvalidAlgorithmParameterException | NoSuchPaddingException e) {
                e.printStackTrace();
                showMessage(e.toString());
            }
        });


    }

//    private void SaveImage(Bitmap finalBitmap,String filename) throws IOException {
//
//        String root = Environment.getExternalStorageDirectory().toString();
//        File myDir = new File(root + "/saved_images");
//        myDir.mkdirs();
//
//        String fname = filename;
//        File file = new File (myDir, fname);
//        if (file.exists ()) file.delete ();
//        file.createNewFile();
//        try {
//            FileOutputStream out = new FileOutputStream(file);
//            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
//            out.flush();
//            out.close();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    public void showMessage(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }
}