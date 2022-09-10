package com.uekapps.sqlserverexample;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class AddPersonActivity extends AppCompatActivity {

    EditText nameEdt, phoneEdt, emailEdt, addressEdt;
    Button addUpdateBtn, deleteBtn;

    Connection connect;
    String ConnectionResult = "";
    Boolean isSuccess = false;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        nameEdt = findViewById(R.id.nameEdt);
        phoneEdt = findViewById(R.id.phoneEdt);
        emailEdt = findViewById(R.id.emailEdt);
        addressEdt = findViewById(R.id.addressEdt);
        addUpdateBtn = findViewById(R.id.addUpdateBtn);
        deleteBtn = findViewById(R.id.deleteBtn);

        addUpdateBtn.setText("Insert");
        addUpdateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isValidAddDetails()){
                    dataInsert();
                    Intent intent = new Intent(AddPersonActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    private void dataInsert() {
        try {
            ConnectionHelper conStr = new ConnectionHelper();
            connect = conStr.connectionClass(); // Veri tabanına bağlanıyoruz
            if (connect == null){
                ConnectionResult = "İnternet Erişiminizi kontrol edin";
                showToast(ConnectionResult);
            } else {
                // Aşağıdaki sorguyu kendi veritabınınıza göre değiştirin.
                String query = "Insert into [TestDB].[dbo].[TableTest] ([Name], [Phone], [Email], [Address]) VALUES ('" + nameEdt.getText().toString() + "' , '"
                        + phoneEdt.getText().toString() + "' , '"
                        + emailEdt.getText().toString() + "' , '"
                        + addressEdt.getText().toString() + "')";
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
