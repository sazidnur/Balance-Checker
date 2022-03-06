package com.example.balancechecker;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity {

    ApiInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Process process = new Process();
        Convert convert = new Convert();

        Button submitBtn = findViewById(R.id.submitBtn);
        EditText addressBox = findViewById(R.id.addressBox);
        TextView addressLevel = findViewById(R.id.addressLevel);
        TextView balanceLevel = findViewById(R.id.balanceLevel);

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String address = addressBox.getText().toString();

                if(process.isValidAddress(address) || process.isChecksumAddress(address)){

                    apiInterface = RetrofitInstance.getRetrofit().create(ApiInterface.class);

                    apiInterface.getBalance(process.getOptions(address)).enqueue(new Callback<PostPojo>() {
                        @Override
                        public void onResponse(Call<PostPojo> call, Response<PostPojo> response) {
                            addressBox.setText("");
                            addressLevel.setText("Address: " + address);

                            PostPojo result = response.body();
                            String balance = result.getResult();
                            balanceLevel.setText("Balance:\n\n"+ balance + " Wei\n" +String.format("%.18f", convert.fromWei(balance, Convert.Unit.ETHER)) + " Ether");
                        }

                        @Override
                        public void onFailure(Call<PostPojo> call, Throwable t) {
                            addressLevel.setText("");
                            balanceLevel.setText("");
                            Toast.makeText(MainActivity.this, "An error has occured", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else{
                    addressLevel.setText("");
                    balanceLevel.setText("");
                    Toast.makeText(MainActivity.this, "Invalid Address", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}