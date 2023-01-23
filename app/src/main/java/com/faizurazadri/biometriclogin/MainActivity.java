package com.faizurazadri.biometriclogin;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.faizurazadri.biometriclogin.databinding.ActivityMainBinding;

import java.util.concurrent.Executor;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding mainBinding;

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());

        checkBiometrictSSupported();

        Executor executor = ContextCompat.getMainExecutor(this);
        BiometricPrompt biometricPrompt = new BiometricPrompt(MainActivity.this,
                executor,
                new BiometricPrompt.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                        super.onAuthenticationError(errorCode, errString);
                        Toast.makeText(getApplicationContext(), "Auth error : " + errString, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                        super.onAuthenticationSucceeded(result);
                        Toast.makeText(getApplicationContext(), "Auth Berhasil", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onAuthenticationFailed() {
                        super.onAuthenticationFailed();
                        Toast.makeText(MainActivity.this, "AUth failed", Toast.LENGTH_LONG).show();
                    }
                });

        mainBinding.btnFingerprint.setOnClickListener(view -> {
            BiometricPrompt.PromptInfo.Builder promptInfo = dialogMetric();
            promptInfo.setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_WEAK | BiometricManager.Authenticators.DEVICE_CREDENTIAL);
            //promptInfo.setNegativeButtonText("Cancel");
            promptInfo.setConfirmationRequired(false);
            biometricPrompt.authenticate(promptInfo.build());
        });
    }

    BiometricPrompt.PromptInfo.Builder dialogMetric() {
        return new BiometricPrompt.PromptInfo.Builder().setTitle("Biometric login").setSubtitle("Login using yout biometric credential");
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    private void checkBiometrictSSupported() {
        String information = "";
        BiometricManager biometricManager = BiometricManager.from(this);
        switch (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK | BiometricManager.Authenticators.DEVICE_CREDENTIAL)) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                information = "App can authenticate using biometrics";
                enableButon(true);
                break;

            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                information = "No Biometric features available on this device";
                enableButon(false);
                break;

            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                information = "Biometric features are currently unavailable";
                enableButon(false);
                break;

            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                information = "Need register at least one fingerprint";
                Intent enrollIntent = new Intent(Settings.ACTION_BIOMETRIC_ENROLL);
                enrollIntent.putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED, BiometricManager.Authenticators.BIOMETRIC_STRONG | BiometricManager.Authenticators.BIOMETRIC_WEAK);
                startActivity(enrollIntent);
                break;

            default:
                information = "Unknown cause";
                break;
        }

        mainBinding.txtInfo.setText(information);
    }

    void enableButon(boolean enable) {
        mainBinding.btnFingerprint.setEnabled(enable);
    }
}