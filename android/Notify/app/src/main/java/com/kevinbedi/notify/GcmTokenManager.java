package com.kevinbedi.notify;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

public class GcmTokenManager {

    public static interface Listener {
        void onTokenGenerated();
    }

    private static final String PREFS_NAME = "gcm_token_pref";
    private static final String PREFS_TOKEN_KEY = "gcm_token_key";
    private static final Character[] VALID_TOKEN_CHARACTERS = new Character[] {
            '0', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 
            'i', 'j', 'k', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 
            'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'
    };
    private static final int TOKEN_LENGTH = 9;

    private static Listener mListener;

    public static void setListener(Listener listener) {
        mListener = listener;
    }

    public static void removeListener() {
        mListener = null;
    }

    public static void storeToken(final Context context) {
        // We'll save it when they log in
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            return;
        }

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                final String refreshedToken = instanceIdResult.getToken();
                final FirebaseFirestore database = FirebaseFirestore.getInstance();

                final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                database.collection("users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            String token;
                            if (document != null && document.get("token") != null) {
                                token = document.getString("token");
                            } else {
                                token = maybeGenerateToken(context);
                            }

                            Map<String, Object> tokens = new HashMap<>();
                            tokens.put("gcmToken", refreshedToken);
                            database.collection("tokens").document(token).set(tokens);
                            tokens.clear();
                            tokens.put("token", token);
                            database.collection("users").document(userId).set(tokens);
                            if (mListener != null) {
                                mListener.onTokenGenerated();
                            }
                        } else {
                            Log.d("NOTIFY:GCMTKMNGR", "task unsuccessful ", task.getException());
                        }
                    }
                });
            }
        });
    }

    public static String getExistingToken(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        if (prefs.contains(PREFS_TOKEN_KEY)) {
            return prefs.getString(PREFS_TOKEN_KEY, "");
        }
        return null;
    }

    private static String maybeGenerateToken(Context context) {
        String existingToken = getExistingToken(context);
        if (existingToken != null) {
            return existingToken;
        }

        String token = generateToken();
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        prefs.edit().putString(PREFS_TOKEN_KEY, token).apply();
        return token;
    }

    private static String generateToken() {
        String token = "";
        for (int i = 0; i < TOKEN_LENGTH; i++) {
            token += getRandomValidChar();
        }

        return token;
    }

    private static Character getRandomValidChar() {
        return VALID_TOKEN_CHARACTERS[
                (int) Math.floor(Math.random() * VALID_TOKEN_CHARACTERS.length)];
    }
}
