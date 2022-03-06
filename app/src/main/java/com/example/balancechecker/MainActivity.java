package com.example.balancechecker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

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
    TextView usdLevel;

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
        usdLevel = findViewById(R.id.usdBalanceLevel);

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String address = addressBox.getText().toString();

                if(process.isValidAddress(address) || process.isChecksumAddress(address)){
                    try {
                        InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);     //gifing keyboard after clicked on check button with valid address
                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                    getBalance(address); //calling for ethereum balance after input valid address via keyboard
                }
                else{
                    showInfo();
                    usdLevel.setText("");
                    Toast.makeText(MainActivity.this, "Invalid Address", Toast.LENGTH_SHORT).show();
                }
            }
        });


        scanButton.setOnClickListener(this);

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
                showInfo();
                Toast.makeText(this, "Enter Valid Ethereum Address QR Code", Toast.LENGTH_SHORT).show();
            }
            else{
                String address = result.getContents();
                if(process.isValidAddress(address) || process.isChecksumAddress(address)){
                    getBalance(address); //calling for ethereum balance after scanning valid QR code
                }
                else{
                    showInfo();
                    Toast.makeText(this, "Enter Valid Ethereum Address QR Code", Toast.LENGTH_SHORT).show();
                }
            }
        }
        else{
            showInfo();
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    protected void showInfo(String address, String wei, double eth){
        addressLevel.setText("Address: " + address);
        balanceLevel.setText("Balance:\n\n" + wei + " Wei\n\n" + String.format("%.18f",eth) + " ETH\n");
    }

    //hiding already showed  balance after an invalid request
    protected void showInfo(){
        addressLevel.setText("");
        balanceLevel.setText("");
    }

    //Getting ethereum balance
    protected void getBalance(String address){
        apiInterface = RetrofitInstance.getRetrofit().create(ApiInterface.class);

        apiInterface.getBalance(process.getOptions(address)).enqueue(new Callback<EtherPojo>() {
            @Override
            public void onResponse(Call<EtherPojo> call, Response<EtherPojo> response) {
                addressBox.setText("");
                EtherPojo result = response.body();
                String wei = result.getResult();
                double ethBalance = Double.parseDouble(String.format("%.18f", convert.fromWei(wei, Convert.Unit.ETHER)));
                showInfo(address, wei, ethBalance);
                setUsdBalance(ethBalance);
            }

            @Override
            public void onFailure(Call<EtherPojo> call, Throwable t) {
                showInfo();
                Toast.makeText(MainActivity.this, "An error has occured", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //getting equivalent USD balance
    protected void setUsdBalance(double eth){
        apiInterface = RetrofitInstance.getRetrofit2().create(ApiInterface.class);

        apiInterface.getCurrencyRate(process.getUsdOptions()).enqueue(new Callback<CurrencyPojo>() {
            @Override
            public void onResponse(Call<CurrencyPojo> call, Response<CurrencyPojo> response) {
                CurrencyPojo result = response.body();
                double usdBalance = result.getUSD() * eth;
                usdLevel.setText(String.format("%.9f", usdBalance) + " USD");
            }

            @Override
            public void onFailure(Call<CurrencyPojo> call, Throwable t) {
                usdLevel.setText("");
                Toast.makeText(MainActivity.this, "Usd option unavailable", Toast.LENGTH_SHORT).show();
            }
        });
    }
}