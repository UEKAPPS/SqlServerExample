package com.uekapps.sqlserverexample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DetailActivity extends AppCompatActivity {

    EditText nameEdt, phoneEdt, emailEdt, addressEdt;
    Button addUpdateBtn, deleteBtn;

    Connection connect;
    String ConnectionResult = "";
    Boolean isSuccess = false;

    String gelenId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        nameEdt = findViewById(R.id.nameEdt);
        phoneEdt = findViewById(R.id.phoneEdt);
        emailEdt = findViewById(R.id.emailEdt);
        addressEdt = findViewById(R.id.addressEdt);
        addUpdateBtn = findViewById(R.id.addUpdateBtn);
        deleteBtn = findViewById(R.id.deleteBtn);

        gelenId = getIntent().getStringExtra("ID");
        nameEdt.setText(getIntent().getStringExtra("Name"));
        phoneEdt.setText(getIntent().getStringExtra("Phone"));
        emailEdt.setText(getIntent().getStringExtra("Email"));
        addressEdt.setText(getIntent().getStringExtra("Address"));

        addUpdateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isValidAddDetails()){
                    dataUpdate();
                    Intent intent= new Intent(DetailActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    private void dataUpdate() {
        try {
            ConnectionHelper conStr = new ConnectionHelper();
            connect = conStr.connectionClass(); // Veri tabanına bağlanıyoruz
            if (connect == null){
                ConnectionResult = "İnternet Erişiminizi kontrol edin";
                showToast(ConnectionResult);
            } else {
                // Aşağıdaki sorguyu kendi veritabınınıza göre değiştirin.
                String query = "Update [TestDB].[dbo].[TableTest] SET Name='" + nameEdt.getText().toString() + "' , Phone='"
                        + phoneEdt.getText().toString() + "' , Email='"
                        + emailEdt.getText().toString() + "' , Address='"
                        + addressEdt.getText().toString() + "' WHERE ID='" + gelenId + "'";
                Statement st = connect.createStatement();
                ResultSet rs = st.executeQuery(query);
            }
            isSuccess = true;
            if (connect != null) {
                connect.close();
            }
        } catch (SQLException sqlException) {
            isSuccess = false;
            sqlException.printStackTrace();
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private Boolean isValidAddDetails(){
        if (nameEdt.getText().toString().trim().isEmpty()){
            showToast("İsim girin");
            return false;
        } else if (phoneEdt.getText().toString().trim().isEmpty()){
            showToast("Telefon girin");
            return false;
        } else if (!Patterns.PHONE.matcher(phoneEdt.getText().toString().trim()).matches()){
            showToast("Geçerli bir telefon girin");
            return false;
        }  else if (emailEdt.getText().toString().trim().isEmpty()){
            showToast("Email girin");
            return false;
        }  else if (!Patterns.EMAIL_ADDRESS.matcher(emailEdt.getText().toString()).matches()){
            showToast("Geçerli bir email girin");
            return false;
        } else if (addressEdt.getText().toString().trim().isEmpty()){
            showToast("Adres girin");
            return false;
        } else {
            return true;
        }
    }
}