package com.example.balancechecker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    ApiInterface apiInterface;
    Button scanButton;
    TextView addressLevel;
    TextView balanceLevel;
    Button submitBtn;
    EditText addressBox;

    Process process = new Process();
    Convert convert = new Convert();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        submitBtn = findViewById(R.id.submitBtn);
        addressBox = findViewById(R.id.addressBox);
        scanButton = findViewById(R.id.scanQrBtn);
        addressLevel = findViewById(R.id.addressLevel);
        balanceLevel = findViewById(R.id.balanceLevel);

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String address = addressBox.getText().toString();

                if(process.isValidAddress(address) || process.isChecksumAddress(address)){
                    getBalance(address);
                }
                else{
                    showInfo("", "");
                    Toast.makeText(MainActivity.this, "Invalid Address", Toast.LENGTH_SHORT).show();
                }
            }
        });


        scanButton.setOnClickListener(this);

    }

    protected void showInfo(String address, String balance){
        addressLevel.setText(address);
        balanceLevel.setText(balance);
    }

    protected void getBalance(String address){
        apiInterface = RetrofitInstance.getRetrofit().create(ApiInterface.class);

        apiInterface.getBalance(process.getOptions(address)).enqueue(new Callback<PostPojo>() {
            @Override
            public void onResponse(Call<PostPojo> call, Response<PostPojo> response) {
                addressBox.setText("");
                PostPojo result = response.body();
                String balance = result.getResult();
                showInfo("Address: " + address, "Balance:\n\n"+ balance + " Wei\n" +String.format("%.18f", convert.fromWei(balance, Convert.Unit.ETHER)) + " Ether");
            }

            @Override
            public void onFailure(Call<PostPojo> call, Throwable t) {
                showInfo("", "");
                Toast.makeText(MainActivity.this, "An error has occured", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setCaptureActivity(CaptureAct.class);
        integrator.setBeepEnabled(false);
        integrator.setOrientationLocked(true);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setPrompt("Scanning QR Code");
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if(result != null){
            if(result.getContents() == null){
                showInfo("", "");
                Toast.makeText(this, "Enter Valid Ethereum Address QR Code", Toast.LENGTH_SHORT).show();
            }
            else{
                String address = result.getContents();
                if(process.isValidAddress(address) || process.isChecksumAddress(address)){
                    getBalance(address);
                }
                else{
                    showInfo("", "");
                    Toast.makeText(this, "Enter Valid Ethereum Address QR Code", Toast.LENGTH_SHORT).show();
                }
            }
        }
        else{
            showInfo("", "");
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}