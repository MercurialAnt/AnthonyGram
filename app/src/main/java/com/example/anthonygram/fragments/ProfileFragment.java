package com.example.anthonygram.fragments;

import android.content.Context;
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
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.anthonygram.LoginActivity;
import com.example.anthonygram.PostAdapter;
import com.example.anthonygram.R;
import com.example.anthonygram.model.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static com.example.anthonygram.fragments.PostFragment.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE;

public class ProfileFragment extends Fragment {

    private Button btnLogout;
    private ImageView ivProfile;
    private ImageView ivAdd;
    private RecyclerView rvGrid;
    protected PostAdapter postAdapter;
    protected ArrayList<Post> posts;

    final int current = 20;

    public static final String TAG = "ProfileFragement";
    public String photoFileName = "photo.jpg";
    private File photoFile;
    Context context;
    ParseUser user;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        user = ParseUser.getCurrentUser();

        context = getContext();
        btnLogout = view.findViewById(R.id.btnLogout);
        ivProfile = view.findViewById(R.id.ivProfile);
        ivAdd = view.findViewById(R.id.ivAdd);
        rvGrid = view.findViewById(R.id.rvGrid);

        posts = new ArrayList<>();
        postAdapter = new PostAdapter(getContext(), posts);
        rvGrid.setAdapter(postAdapter);
        rvGrid.setLayoutManager(new GridLayoutManager(context, 3));

        ivAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLaunchCamera(v);
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser.logOut();
                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        try {
            ParseFile imgFile = user.fetchIfNeeded().getParseFile("profile_pic");
            if (imgFile != null) {
                Glide.with(context)
                        .load(imgFile.getUrl())
                        .apply(RequestOptions.circleCropTransform())
                        .into(ivProfile);
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        loadTopPosts(0, current);
    }

    public void onLaunchCamera(View view) {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference to access to future access
        photoFile = getPhotoFileUri(photoFileName);

        // wrap File object into a content provider
        Uri fileProvider = FileProvider.getUriForFile(context, "com.codepath.fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    // Returns the File for a photo stored on disk given the fileName
    public File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);

        return file;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // by this point we have the camera photo on disk
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                // Load the taken image into a preview
                Bitmap bMapScaled = Bitmap.createScaledBitmap(takenImage, 150, 100, true);
                ivProfile.setImageBitmap(bMapScaled);
                ivProfile.setVisibility(View.VISIBLE);
                ParseFile img = new ParseFile(photoFile);
                user.put("profile_pic", img);
                try {
                    user.save();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else { // Result was a failure
                Toast.makeText(context, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    protected void loadTopPosts(int offset, int amount) {
        final Post.Query postQuery = new Post.Query();
        postQuery.offSet(offset).getTop(amount).withUser().onlyCurrentUser().orderByDate();


        postQuery.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> objects, ParseException e) {
                if (e == null) {
                    posts.addAll(objects);
                    postAdapter.notifyDataSetChanged();

                } else {
                    e.printStackTrace();
                }

            }
        });

    }




}
