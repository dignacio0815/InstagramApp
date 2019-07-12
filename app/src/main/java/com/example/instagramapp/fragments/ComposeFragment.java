package com.example.instagramapp.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.instagramapp.ComposeActivity;
import com.example.instagramapp.MainActivity;
import com.example.instagramapp.R;
import com.example.instagramapp.model.Post;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;

public class ComposeFragment extends Fragment {
    private static final int RESULT_OK = -1;
    public final String APP_TAG = "ComposeFragment";
    private ComposeActivity parent;
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    public String photoFileName = "photo.jpg";
    private File photoFile;

    private EditText etDescription;
    private Button btnCreate;
    private Button btnCapture;
    private ImageView ivImage;
    private Button btnLogout;
    // The onCreateView method is called when Fragment should create its View object hierarchy,
    // either dynamically or via XML layout inflation.

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // make a reference to fragment compose
        parent = (ComposeActivity) getContext();
        return inflater.inflate(R.layout.fragment_compose, container, false);
    }

    // This event is triggered soon after onCreateView().
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        etDescription = view.findViewById(R.id.etDescription);
        btnCreate = view.findViewById(R.id.btnCreate);
        btnCapture = view.findViewById(R.id.btnCapture);
        ivImage = view.findViewById(R.id.ivImage);
        // on some click or some loading we need to wait for...
        final ProgressBar pb = (ProgressBar) view.findViewById(R.id.pbProgressAction);

        btnLogout = view.findViewById(R.id.btnLogout);

        btnCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLaunchCamera(v);
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser.logOut();
                ParseUser parseUser = ParseUser.getCurrentUser(); // this will now be null
                Intent backToLogin = new Intent(getContext(), MainActivity.class);
                startActivity(backToLogin);
            }
        });

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String description = etDescription.getText().toString();
                ParseUser user = ParseUser.getCurrentUser();
                if(photoFile == null) {
                    Log.e("ComposeActivity", "No photo to submit");
                    Toast.makeText(getContext(), "There is no photo", Toast.LENGTH_LONG).show();
                    return;
                }
                savePost(description, user, photoFile);
            }

            private void savePost(String description, ParseUser user, File photoFile) {
                Post post = new Post();
                post.setKeyDescription(description);
                post.setKeyUser(user);
                post.setKeyImage(new ParseFile(photoFile));
                pb.setVisibility(View.VISIBLE);
                post.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            Log.d("ComposeActivity", "error while saving");
                            e.printStackTrace();
                            return;
                        }
                        Log.d("ComposeActivity", "Success");
                        etDescription.setText("");
                        // after image is created and sent to local server, ImageView is reset to blank template
                        ivImage.setImageResource(0);
                    }
                });
                pb.setVisibility(View.INVISIBLE);
            }
        });
    }

    public void onLaunchCamera(View view) {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference to access to future access

        photoFile = getPhotoFileUri(photoFileName);
        if(photoFile == null) {
            Log.i("Compose", "Null");
        }
        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        Uri fileProvider = FileProvider.getUriForFile(getContext(), "com.codepath.fileprovider", photoFile);
        // See code above
        // by this point we have the camera photo on disk
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    private File getPhotoFileUri(String photoFileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(APP_TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        File file = new File(mediaStorageDir.getPath() + File.separator + photoFileName);
        Uri bmpUri = FileProvider.getUriForFile(getContext(), "com.codepath.fileprovider", file);
        return file;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // by this point we have the camera photo on disk
                // RESIZE BITMAP, see section below
                // Load the taken image into a preview
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                ivImage.setImageBitmap(takenImage);
                if (photoFile == null) {
                    Log.i("Compose", "Null");
                } else { // Result was a failure
                    Toast.makeText(getContext(), "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
