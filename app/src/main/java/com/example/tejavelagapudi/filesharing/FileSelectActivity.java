package com.example.tejavelagapudi.filesharing;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.tejavelagapudi.filesharing.databinding.ActivityFileSelectBinding;
import com.example.tejavelagapudi.filesharing.databinding.ItemRecyclerViewBinding;

import java.io.File;

/**
 * Created by tejavelagapudi on 10/28/15.
 *
 * This activity creates a gallery of images
 * and which ever app whats to get a image from the app
 * it will send back the content uri of the image selected
 *
 *Check out the app manifest where you can find how this activity is setup
 * and also check the provider tag.
 *
 */
public class FileSelectActivity extends AppCompatActivity {
    ActivityFileSelectBinding mActivityFileSelectBinding;
    private static final String TAG = FileSelectActivity.class.getSimpleName();
    // The path to the root of this app's internal storage
    private File mPrivateRootDir;
    // The path to the "images" subdirectory
    private File mImagesDir;
    // Array of files in the images subdirectory
    File[] mImageFiles;
    // Array of filenames corresponding to mImageFiles
    String[] mImageFilenames;
    private Intent mResultIntent;
    // Initialize the Activity




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityFileSelectBinding = DataBindingUtil.setContentView(this, R.layout.activity_file_select);

        /*
            we are just created a result intent
            that can be sent back to any app that started this activity
         */
        mResultIntent =
                new Intent("com.example.myapp.ACTION_RETURN_FILE");
        // Get the files/ subdirectory of internal storage
        mPrivateRootDir = getFilesDir();
        // Get the files/images subdirectory;
        mImagesDir = new File(mPrivateRootDir, "images");
        // Get the files in the images subdirectory
        mImageFiles = mImagesDir.listFiles();
        // Set the Activity's result to null to begin with
        setResult(Activity.RESULT_CANCELED, null);

        /*
            Recycler view is used to create the image gallery
            ImageRecyclerViewAdapter class is where we are laying out images that were saved
            in the private folder of this app.

         */
        RecyclerView recyclerView = mActivityFileSelectBinding.recylcerView;
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(gridLayoutManager);
        ImageRecyclerViewAdapter adapter = new ImageRecyclerViewAdapter();
        recyclerView.setAdapter(adapter);

    }


    class ImageRecyclerViewAdapter extends RecyclerView.Adapter<ImageRecyclerViewAdapter.Holder> {

        @Override
        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            ItemRecyclerViewBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_recycler_view, parent, false);
            return new Holder(binding);
        }

        @Override
        public void onBindViewHolder(Holder holder, int position) {
            final int pos = position;
            if (mImageFiles[position].exists()) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                Bitmap bitmap = BitmapFactory.decodeFile(mImageFiles[position].getAbsolutePath());
                Bitmap resized = getResizedBitmap(bitmap, 100, 100);
                holder.imageView.setImageBitmap(resized);

                /*
                   Thus is the onclick listener for the images
                   When an user selects an image we are grabbing the content uri
                   and updating the result intent that we initialized in the onCreate method
                   and finishing the current activity

                 */
                holder.imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "clicked");
                        /*
                 * Get a File for the selected file name.
                 * Assume that the file names are in the
                 * mImageFilename array.
                 */
                        File requestFile = mImageFiles[pos];
                        Uri fileUri = null;
                /*
                 * Most file-related method calls need to be in
                 * try-catch blocks.
                 */
                        // Use the FileProvider to get a content URI
                        try {
                            fileUri = FileProvider.getUriForFile(FileSelectActivity.this, "com.example.tejavelagapudi.filesharing.fileprovider", requestFile);
                        } catch (IllegalArgumentException e) {
                            Log.e("File Selector",
                                    "The selected file can't be shared: ");
                        }

                        if (fileUri != null) {
                            // Grant temporary read permission to the content URI
                            mResultIntent.addFlags(
                                    Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            mResultIntent.setDataAndType(
                                    fileUri,
                                    getContentResolver().getType(fileUri));
                            FileSelectActivity.this.setResult(Activity.RESULT_OK, mResultIntent);
                        } else {
                            mResultIntent.setDataAndType(null, "");
                            FileSelectActivity.this.setResult(RESULT_CANCELED, mResultIntent);
                        }
                        finish();

                    }
                });

            }

        }

        @Override
        public int getItemCount() {
            if (mImageFiles != null) {
                return mImageFiles.length;
            } else {
                return 0;
            }

        }

        public class Holder extends RecyclerView.ViewHolder {
            ImageView imageView;

            public Holder(ItemRecyclerViewBinding itemRecyclerViewBinding) {
                super(itemRecyclerViewBinding.getRoot());
                imageView = itemRecyclerViewBinding.imageView;
            }
        }

        public Bitmap getResizedBitmap(Bitmap image, int newHeight, int newWidth) {
            int width = image.getWidth();
            int height = image.getHeight();
            float scaleWidth = ((float) newWidth) / width;
            float scaleHeight = ((float) newHeight) / height;
            // create a matrix for the manipulation
            Matrix matrix = new Matrix();
            // resize the bit map
            matrix.postScale(scaleWidth, scaleHeight);
            // recreate the new Bitmap
            Bitmap resizedBitmap = Bitmap.createBitmap(image, 0, 0, width, height,
                    matrix, false);
            return resizedBitmap;
        }


    }
}
