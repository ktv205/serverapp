package com.example.tejavelagapudi.filesharing;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.tejavelagapudi.filesharing.databinding.ActivityMainBinding;

import org.apache.commons.lang3.RandomStringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_CHOOSE_PICTURE_FROM_GALLARY = 22;
    private static final String TAG = MainActivity.class.getSimpleName();
    File mImagesDir;
    File mPrivateRootDir;
    File[] mImageFiles;
    ActivityMainBinding mMainBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
    }

    /*
         This is the listener for  image picking button
         The listener is defined the activity_main xml file via android:onClick
         When the user clicks on the button we are sending an implicit intent
         and system checks for apps who can share images and presents the apps the user

         Note: See what startActivityForResult does compared to just startActivity

     */

    public void onPickImagesButtonClicked(View view) {

        Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
        photoPickerIntent.setType("image/*");
        photoPickerIntent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.name());
        photoPickerIntent.putExtra("return-data", true);
        startActivityForResult(photoPickerIntent, REQUEST_CODE_CHOOSE_PICTURE_FROM_GALLARY);
    }




    /*
        Once the user selects an image we are saving the image to the private directory of this app
        I am making a subdirectory called images and saving the images there.
        In this method I am getting the Uri of the selected image and sending it to copyFile method
        I am also previewing the image in the activity

     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "in activityResult");
        if (data != null && data.getData()!=null) {
            Log.d(TAG, "uriImagePath Gallary :" + data.getData().toString());
            mMainBinding.testImage.setImageURI(data.getData());
            mPrivateRootDir = getFilesDir();
            mImagesDir = new File(mPrivateRootDir, "images");
            Log.d(TAG, "directory creared from mkdirs->" + mImagesDir.mkdir());
            mImageFiles = mImagesDir.listFiles();
            File sourceFile = new File(data.getData().toString());
            Log.d(TAG, "absolute pah of the private directory->" + mPrivateRootDir.getAbsolutePath());
            Log.d(TAG, "absoulte path of the image directory->" + mImagesDir.getAbsolutePath());
            Log.d(TAG, "sourceFile->" + sourceFile.getAbsolutePath());
            if (mImageFiles != null) {
                Log.d(TAG, "total files->" + mImageFiles.length);
            } else {
                Log.d(TAG, "mImageFiles length is null");
            }
            copyFile(data.getData());
        }



    }

    /*
            In copy file method I am just copying the original image to our app's
            private folder that I created previously
     */

    private boolean copyFile(Uri sourceFile) {
        InputStream inputStream = null;
        OutputStream outputStream = null;

        try {
            ContentResolver content = this.getContentResolver();
            inputStream = content.openInputStream(sourceFile);

            File root = Environment.getExternalStorageDirectory();
            if (root == null) {
                Log.d(TAG, "Failed to get root");
            }
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            File outputFile = new File(mImagesDir, generateUniqueFileName() + ".png");
            outputStream = new FileOutputStream(outputFile);
            if (outputStream != null) {
                Log.e(TAG, "Output Stream Opened successfully");
            }

            byte[] buffer = new byte[1000];
            int bytesRead = 0;
            while ((bytesRead = inputStream.read(buffer, 0, buffer.length)) >= 0) {
                outputStream.write(buffer, 0, buffer.length);
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception occurred " + e.getMessage());
        } finally {

        }
        return true;
    }


    /*
         This method just creates a unique name for our copied image
     */


    String generateUniqueFileName() {
        String filename = "";
        long millis = System.currentTimeMillis();
        String datetime = new Date().toGMTString();
        datetime = datetime.replace(" ", "");
        datetime = datetime.replace(":", "");
        String rndchars = RandomStringUtils.randomAlphanumeric(16);
        filename = rndchars + "_" + datetime + "_" + millis;
        return filename;
    }

    /*
       This button listener will send the user to the image gallery activity that was created from
       the images we copied
     */

    public void onShareScreenButtonClicked(View view) {
        Intent intent = new Intent(this, FileSelectActivity.class);
        startActivity(intent);
    }
}
