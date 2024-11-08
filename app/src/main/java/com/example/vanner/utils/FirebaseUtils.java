package com.example.vanner.utils;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class FirebaseUtils {

    private static final String USERS_NODE = "users";
    private static final String JOBS_NODE = "jobs";
    private static final String APPLICATIONS_NODE = "applications";
    private static final String PROFILE_IMAGES_NODE = "profile_images";

    private FirebaseUtils() {

    }

    public static DatabaseReference getUsersDatabaseReference() {
        return FirebaseDatabase.getInstance().getReference(USERS_NODE);
    }

    public static DatabaseReference getJobsDatabaseReference() {
        return FirebaseDatabase.getInstance().getReference(JOBS_NODE);
    }

    public static DatabaseReference getApplicationsDatabaseReference() {
        return FirebaseDatabase.getInstance().getReference(APPLICATIONS_NODE);
    }

    public static StorageReference getProfileImagesStorageReference() {
        return FirebaseStorage.getInstance().getReference(PROFILE_IMAGES_NODE);
    }

    public static StorageReference getProfileImageReference(String userId) {
        return getProfileImagesStorageReference().child(userId);
    }

    public static StorageReference getJobImageReference(String jobId) {
        return getProfileImagesStorageReference().child("jobs").child(jobId);
    }
}
