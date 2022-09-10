package com.uekapps.sqlserverexample;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ArrayList<ClassListItems> itemArrayList;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private boolean success = false;
    private ConnectionHelper connectionHelper;
    RecyclerAdapter recyclerAdapter;
    List<ClassListItems> personList;
    SwipeRefreshLayout swipeRefreshLayout;

    Connection connect;
    String ConnectionResult = "";
    Boolean isSuccess;

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);

        connectionHelper = new ConnectionHelper();
        itemArrayList = new ArrayList<>();

        SyncData orderData = new SyncData();
        orderData.execute("");

        recyclerAdapter = new RecyclerAdapter(itemArrayList, MainActivity.this, MainActivity.this);
        recyclerView.setAdapter(recyclerAdapter);

        personList = new ArrayList<>();

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            recyclerAdapter.notifyDataSetChanged();
            swipeRefreshLayout.setRefreshing(false);
        });

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

    }

    @SuppressLint("StaticFieldLeak")
    private class SyncData extends AsyncTask<String , String , String> {

        String msg = "msg";
        ProgressDialog progress;

        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(MainActivity.this, "Kişiler", "Yükleniyor...", true);
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                Connection connection = connectionHelper.connectionClass();
                if (connection == null){
                    success = false;
                } else {
                    String query = "SELECT * FROM [TestDB].[dbo].[TableTest]";
                    Statement st = connection.createStatement();
                    ResultSet rs = st.executeQuery(query);
                    if (rs != null){
                        while (rs.next()){
                            try {
                                itemArrayList.add(new ClassListItems(rs.getString("ID"),
                                        rs.getString("Name"),
                                        rs.getString("Phone"),
                                        rs.getString("Email"),
                                        rs.getString("Address")));
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                        msg = "Veri yüklendi";
                        success = true;
                    }
                }
            } catch (SQLException sqlException){
                sqlException.printStackTrace();
                Writer writer = new StringWriter();
                sqlException.printStackTrace(new PrintWriter(writer));
                msg = writer.toString();
                success = false;
            }

            return msg;
        }

        @Override
        protected void onPostExecute(String s) {
            progress.dismiss();
            Toast.makeText(MainActivity.this, msg + "", Toast.LENGTH_SHORT).show();
            if (!success) {

            } else {
                try {
                    recyclerAdapter = new RecyclerAdapter(itemArrayList, MainActivity.this, MainActivity.this);
                    recyclerView.setAdapter(recyclerAdapter);
                } catch (Exception ignored) {

                }
            }
        }
    }

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            final int position = viewHolder.getAdapterPosition();

            switch (direction) {
                case ItemTouchHelper.LEFT:
                case ItemTouchHelper.RIGHT:
                    final ClassListItems deletePerson = itemArrayList.get(position);
                    final String personID = deletePerson.getId();
                    final String personName = deletePerson.getName();
                    final String personPhone = deletePerson.getPhone();
                    final String personEmail = deletePerson.getEmail();
                    final String personAddress = deletePerson.getAddress();
                    Connection connection = connectionHelper.connectionClass();
                    try {
                        if (connection != null) {
                            // Aşağıdaki sorguyu kendinize göre değiştirin
                            String query = "DELETE FROM [TestDB].[dbo].[TableTest] WHERE ID='" + personID + "'";
                            Statement statement = connection.createStatement();
                            ResultSet resultSet = statement.executeQuery(query);
                        }
                    } catch (Exception e) {
                        Log.e("Error:", e.getMessage());
                    }

                    itemArrayList.remove(position);
                    recyclerAdapter.notifyItemRemoved(position);

                    Snackbar.make(recyclerView, deletePerson.getName() + "Silindi", Snackbar.LENGTH_LONG)
                            .setAction("Geri Al", view -> {
                                try {
                                    connect = connectionHelper.connectionClass();
                                    if (connect == null){
                                        ConnectionResult = "İnternet erişiminizi kontrol edin";
                                    } else {
                                        // Aşağıdaki sorguyu kendinize göre değiştirin
                                        String query = "Insert into [TestDB].[dbo].[TableTest] ([Name], [Phone], [Email], [Address]) VALUES ('" + personName + "' , '"
                                                + personPhone + "' , '"
                                                + personEmail + "' , '"
                                                + personAddress + "')";
                                        Statement st = connect.createStatement();
                                        ResultSet rs = st.executeQuery(query);
                                    }
                                    isSuccess = true;
                                    connect.close();
                                } catch (SQLException sqlException) {
                                    isSuccess = false;
                                    sqlException.printStackTrace();
                                }
                                itemArrayList.add(position, deletePerson);
                                recyclerAdapter.notifyItemInserted(position);
                            }).show();
                    break;
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.searchBar);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("Burada arama yapabilirsiniz");

        SearchView.OnQueryTextListener listener = new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                recyclerAdapter.getFilter().filter(newText);
                return true;
            }
        };

        searchView.setOnQueryTextListener(listener);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.addPerson) {
            Intent intent = new Intent(getApplicationContext(), AddPersonActivity.class);
            startActivity(intent);
        }


        return super.onOptionsItemSelected(item);
    }
}