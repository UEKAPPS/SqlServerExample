package com.uekapps.sqlserverexample;

import android.annotation.SuppressLint;
import android.os.StrictMode;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionHelper {
    String ip, db, user, password;

    @SuppressLint("NewApi")
    public Connection connectionClass(){
        //Aşağıdaki bilgileri kendinize göre değiştirin
        ip = "192.168.2.180";

        // localhost ya da bilgisayar adı ile de giriş yapabilirsiniz
        // ip = "localhost";

        // port isterse = 1433
        // ip = "192.168.2.180:1433";

        db = "TestDB";
        user = "sa";
        password = "Uek123";
        //bilgileri kendinize göre değiştirin sonu


        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection connection = null;
        String ConnectionURL;
        try {
            // MICROSOFT
            // Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

            // JTDS
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            ConnectionURL = "jdbc:jtds:sqlserver://" + ip + ";databaseName=" + db + ";user=" + user + ";password=" + password + ";";
            connection = DriverManager.getConnection(ConnectionURL);
        } catch (SQLException se) {
            // Veri tabanı hatası
            Log.e("SE:", se.getMessage());
        } catch (ClassNotFoundException ce) {
            // Sınıfa erişim hatası
            Log.e("CE:" , ce.getMessage());
        } catch (Exception exception){
            // Genel hata
            Log.e("ex:" , exception.getMessage());
        }

        return connection;
    }

    // Kod ile tablo oluşturma
   /*
   CREATE TABLE [dbo].[TableTest] (
            [ID] [uniqueidentifier] DEFAULT (newsequentialid()),
            [Name] [varchar](50),
            [Phone] [varchar](50),
            [Email] [varchar](50),
            [Address] [varchar](50)
            )

    */
}
