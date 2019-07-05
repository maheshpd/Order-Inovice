package com.arfeenkhan.orderinvoice;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    TextInputEditText idnumber, name, contact, email, rate, gst, totalamt, paid;
    Spinner category, status_spinner, product;

    String sid, sname, scontact, semail, srate, sqty, stotalamt, spaid, sproduct, status, scategory,pname,pprice;

    Button btn_submit, btn_decrease, btn_increase;

    //url
    String getdatafromInfusionUrl = "http://magicconversion.com/ctf-product-invoice/getcontact.php";
    String getCategoryData = "http://magicconversion.com/ctf-product-invoice/getcategory.php";
    String insertData = " http://magicconversion.com/ctf-product-invoice/insertinvoice.php";
    String getProductData = "http://magicconversion.com/ctf-product-invoice/getproduct.php";

    //Array List
//    ArrayList<String> categoryList = new ArrayList<>();
    ArrayList<ProductModel> categoryList = new ArrayList<>();
    ArrayList<List<String>> list = new ArrayList<>();
    final ArrayList<String> list1 = new ArrayList<>();
    ArrayList<String> categoryList1 = new ArrayList<>();
    private ProgressDialog progressDialog;

    int minteger = 1;
    int sum, a, b;

    TextView quantity1;

    CategoryAdapter adapter;

    String categoryid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressDialog = new ProgressDialog(this);


        category = findViewById(R.id.category);
        product = findViewById(R.id.product);
        idnumber = findViewById(R.id.IDNumber);
        name = findViewById(R.id.name);
        contact = findViewById(R.id.contact);
        email = findViewById(R.id.email);
        rate = findViewById(R.id.rate);
        gst = findViewById(R.id.gst);
        totalamt = findViewById(R.id.totalamt);
        paid = findViewById(R.id.paid);
        quantity1 = findViewById(R.id.quantity);
        status_spinner = findViewById(R.id.status_spinner);

        btn_decrease = findViewById(R.id.decrease);
        btn_increase = findViewById(R.id.increase);
        btn_submit = findViewById(R.id.submit);

        adapter = new CategoryAdapter(this, categoryList);
        category.setAdapter(adapter);

        name.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE) {
                    // Your action on done
                    sid = idnumber.getText().toString();
                    getDataFromInfusion();
                    return true;
                }
                return false;
            }
        });

        rate.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_NEXT) {

                    getGstNo();

//                    Toast.makeText(MainActivity.this, String.valueOf(rate), Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
            }
        });
//        rate.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//                double amount = Double.parseDouble(srate);
//                double rate = (amount /100) * 18;
//                Toast.makeText(MainActivity.this, String.valueOf(rate), Toast.LENGTH_SHORT).show();
////                totalamt.setText(String.valueOf(rate));
//            }
//        });
        name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    sid = idnumber.getText().toString();
                    idnumber.requestFocus();

                    if (idnumber.length() == 0) {
                        Toast.makeText(MainActivity.this, "Enter all field", Toast.LENGTH_SHORT).show();
                    } else {
                        getDataFromInfusion();
                    }
                }
            }
        });

        gst.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    rate.requestFocus();
                    if (rate.length() == 0) {
                        Toast.makeText(MainActivity.this, "Enter rate field", Toast.LENGTH_SHORT).show();
                    } else {
                        getGstNo();
                    }
                }
            }
        });

        getCategory();

        category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                ProductModel pm = (ProductModel) adapterView.getItemAtPosition(i);

                categoryid = pm.getId();
                scategory = pm.getName();
//                Toast.makeText(MainActivity.this, categoryid, Toast.LENGTH_SHORT).show();

                if (scategory.equals("Please select Category")) {
                    Toast.makeText(MainActivity.this, "Please select Category", Toast.LENGTH_SHORT).show();
                }else {
                    getProduct(categoryid);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        product.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                sproduct = product.getSelectedItem().toString();
                String price = list.get(i).get(1);
                rate.setText(price);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        status_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                status = status_spinner.getSelectedItem().toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

//        quantity1.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                totalamt.setText(s);
//            }
//        });

        btn_decrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                decrease();
            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitAmount();
            }
        });

    }

    private void getGstNo() {
        srate = rate.getText().toString().trim();
        double amount = Double.parseDouble(srate);
        double rate = (amount /100) * 18;
        gst.setText(String.valueOf(rate));
    }

    private void getProduct(final String categoryid) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        list.clear();
        list1.clear();
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest sr = new StringRequest(Request.Method.POST, getProductData, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray array = new JSONArray(response);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject c = array.getJSONObject(i);
                        pname = c.getString("name");
                        pprice = c.getString("price");
                        list.add(Arrays.asList(pname,pprice));
                        list1.add(pname);
                        rate.setText(list.get(i).get(0));
                        progressDialog.dismiss();
                    }
                    product.setAdapter(new ArrayAdapter<>(MainActivity.this,android.R.layout.simple_spinner_dropdown_item,list1));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("cat_id", categoryid);
                return params;
            }
        };
        queue.add(sr);
    }

    private void submitAmount() {

        spaid = paid.getText().toString();
        sqty = quantity1.getText().toString();
        stotalamt = totalamt.getText().toString();
        if (TextUtils.isEmpty(sid)) {
            Toast.makeText(this, "Enter id number", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(sname)) {
            Toast.makeText(this, "Enter name", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(scontact)) {
            Toast.makeText(this, "Enter contact no", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(semail)) {
            Toast.makeText(this, "Enter email", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(srate)) {
            Toast.makeText(this, "Enter rate amount", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(spaid)) {
            Toast.makeText(this, "Enter paid amount", Toast.LENGTH_SHORT).show();
        } else if (status.equals("Please select status")) {
            Toast.makeText(MainActivity.this, "Please select status", Toast.LENGTH_SHORT).show();
        } else {

            progressDialog.setMessage("Please wait...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            submit();

        }

    }

    private void submit() {
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest sr = new StringRequest(Request.Method.POST, insertData, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray arr = new JSONArray(response);
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject c = arr.getJSONObject(0);
                        String message = c.getString("message");
                        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();

                        idnumber.setText("");
                        name.setText("");
                        contact.setText("");
                        email.setText("");
                        rate.setText("0");
                        totalamt.setText("0");
                        paid.setText("");

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("contactid", sid);
                params.put("name", sname);
                params.put("email", semail);
                params.put("phone", scontact);
                params.put("category", sproduct);
                params.put("price", srate);
                params.put("paid_amount", spaid);
                params.put("total", stotalamt);
                params.put("qty", sqty);
                params.put("pay_method", status);
                return params;
            }
        };
        queue.add(sr);

    }

    private void decrease() {
        srate = rate.getText().toString();
        if (TextUtils.isEmpty(srate)) {
            Toast.makeText(this, "Enter Price", Toast.LENGTH_SHORT).show();
        } else {
            if (minteger > 1) {
                minteger = minteger - 1;
                quantity1.setText("" + minteger);
                String qty = quantity1.getText().toString();
                totalamt.setText(qty);
                a = Integer.parseInt(srate);
                b = Integer.parseInt(qty);
                sum = a * b;
                totalamt.setText(String.valueOf(sum));
            }
        }
    }

    private void getCategory() {

        categoryList.clear();
        categoryList.add(new ProductModel("Please select Category"));
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest sr = new StringRequest(Request.Method.GET, getCategoryData, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray array = new JSONArray(response);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject c = array.getJSONObject(i);

                        String id = c.getString("id");
                        String name = c.getString("name");

                        categoryList.add(new ProductModel(id, name));
                    }
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(sr);

    }


    private void getDataFromInfusion() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest sr = new StringRequest(Request.Method.POST, getdatafromInfusionUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray array = new JSONArray(response);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject c = array.getJSONObject(i);
                        sname = c.getString("name");
                        semail = c.getString("Email");
                        scontact = c.getString("phone");
                        contact.setText(scontact);
                        email.setText(semail);
                        name.setText(sname);

                        InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        in.hideSoftInputFromWindow(name.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                        progressDialog.dismiss();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("contactid", sid);
                return params;
            }
        };
        queue.add(sr);
    }

    public void increaseNumber(View view) {
        srate = rate.getText().toString();

        if (TextUtils.isEmpty(srate)) {
            Toast.makeText(this, "Enter Price", Toast.LENGTH_SHORT).show();
        } else {
            minteger = minteger + 1;
            quantity1.setText("" + minteger);
            String qty = quantity1.getText().toString();
            a = Integer.parseInt(srate);
            b = Integer.parseInt(qty);
            sum = a * b;
            totalamt.setText(String.valueOf(sum));
        }
    }
}
