package com.example.pricecheckerapp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.pricecheckerapp.Classes.Constant;
import com.example.pricecheckerapp.Classes.PrefManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.math.RoundingMode;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import androidx.appcompat.app.AppCompatActivity;


@SuppressLint("SetTextI18n")
public class MainActivity extends AppCompatActivity {
Button btn_ipaddress;
    private static final String WRITE_EXTERNAL_STORAGE = "1";
    private static final String READ_EXTERNAL_STORAGE = "2";
    EditText et_Barcode, et_branch;
    TextView txt_price,txt_description ,txt_price_before_discount ,
    txt_offer_from_date, txt_offer_to_date , txt_offer_how_much_day_remind,txtPoint;

    public StringRequest request = null;
    public VolleyError volleyErrorPublic = null;
    String  Ip_Address="0";
    String editbarcode;
    String Depart = "";
    String TotalQTYFor23 = "", KQTY = "", GQTY = "";
    String BarcodeFor23 = "";
//    ProgressBar progressBar;
    Animation animfrom_left_x;
    LinearLayout linear_title_animation ,linear_for_ip,linear_before_work,linear_details, linear_for_offer;
    CountDownTimer cdtTimer;
    PrefManager prefManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        //TODO to get ip address to determine branch
       /* WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        String ipAddress = Formatter.formatIpAddress(wifiManager.getConnectionInfo().getIpAddress());
        Log.e("zzipAddress",""+ipAddress);
        if (ipAddress.contains("10.128.")){
            Branch="1";
        }else if (ipAddress.contains("10.3.")){
            Branch="3";
        }else if (ipAddress.contains("10.4.")){
            Branch="4";
        }*/
        linear_for_ip=findViewById(R.id.linear_for_ip);
        btn_ipaddress=findViewById(R.id.btn_ipaddress);
        et_branch = findViewById(R.id.et_branch);
        linear_before_work=findViewById(R.id.linear_before_work);
        linear_details=findViewById(R.id.linear_details);

        et_Barcode = findViewById(R.id.et_text);
        et_Barcode.requestFocus();

        txt_offer_to_date=findViewById(R.id.txt_offer_to_date);
        txt_offer_from_date=findViewById(R.id.txt_offer_from_date);
        txt_offer_how_much_day_remind=findViewById(R.id.txt_offer_how_much_day_remind);


        txt_description=findViewById(R.id.txt_description);
        txt_price=findViewById(R.id.txt_price);
        linear_title_animation=findViewById(R.id.linear_title_animation);
        linear_for_offer=findViewById(R.id.linear_for_offer);
        txt_price_before_discount=findViewById(R.id.txt_price_before_discount);

        txtPoint=findViewById(R.id.txt_View);
        txtPoint.setVisibility(View.GONE);
        prefManager=new PrefManager(this);
       if (prefManager.get_branch().equalsIgnoreCase("10.0.0.0")){
           linear_for_ip.setVisibility(View.VISIBLE);
           linear_before_work.setVisibility(View.GONE);
            et_branch.requestFocus();
           btn_ipaddress.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   if (!et_branch.getText().toString().isEmpty()) {
                       prefManager.set_branch(et_branch.getText().toString());
                       Log.e("zzzFirstTimeLaunch", "convert to false"+ et_branch.getText().toString());
                       linear_for_ip.setVisibility(View.GONE);
                       linear_before_work.setVisibility(View.VISIBLE);
                       linear_details.setVisibility(View.VISIBLE);
                   }else {
                       et_branch.requestFocus();
                       Toast.makeText(MainActivity.this, "Enter IP Address", Toast.LENGTH_SHORT).show();
                   }
               }
           });

       }else {
           linear_for_ip.setVisibility(View.GONE);
           linear_before_work.setVisibility(View.VISIBLE);
           linear_details.setVisibility(View.VISIBLE);
           Log.e("zzzFirstTimeLaunch","still false");
       }

        animfrom_left_x = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.from_left_x);
        animfrom_left_x.setRepeatCount(Animation.INFINITE);

        linear_title_animation.startAnimation(animfrom_left_x);

        StartTimer();
        cdtTimer.cancel();
        et_Barcode.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_GO
                        || actionId == EditorInfo.IME_ACTION_NEXT
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                        || keyEvent == null
                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER
                        || keyEvent.getAction() == KeyEvent.KEYCODE_NUMPAD_ENTER
                        || keyEvent.getAction() == KeyEvent.KEYCODE_DPAD_CENTER) {

                    linear_before_work.setVisibility(View.GONE);
                    linear_details.setVisibility(View.VISIBLE);
                    try {
                        GetDetialsForBarcod();
                    }catch (Exception e){
                        Log.e("error-->",""+e);
                    }
                    cdtTimer.cancel();
                    cdtTimer.start();

                }

                return false;
            }
        });
  //      RequestRunTimePermission();

    }
/*
    // Requesting run time permission method starts from here.
    public void RequestRunTimePermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            ///  Toast.makeText(LoginActivity.this,"أذن كتابه الى الكارت", Toast.LENGTH_LONG).show();
            writeFileOnInternalStorage(this,"Ipaddress.txt","10.0.0.0");

        } else {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int RC, String per[], int[] Result) {

        switch (RC) {

            case 1:

                if (Result.length > 0 && Result[0] == PackageManager.PERMISSION_GRANTED) {
//                    GetVersionFromServer();

                    writeFileOnInternalStorage(this,"Ipaddress","10.0.0.0");

                } else {

                    Toast.makeText(this, "تم إلغاء الأذن", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }
    public void writeFileOnInternalStorage(Context mcoContext, String sFileName , String sBody){
        try {
            File path = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS);
            File myFile = new File(path, "mytextfile.txt");
            FileOutputStream fOut = new FileOutputStream(myFile,true);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            myOutWriter.append("the text I want added to the file");
            myOutWriter.close();
            fOut.close();

            Toast.makeText(this,"Text file Saved !",Toast.LENGTH_LONG).show();
        }

        catch (java.io.IOException e) {

            //do something if an IOException occurs.
            Toast.makeText(this,"ERROR - Text could't be added",Toast.LENGTH_LONG).show();
        }
    }*/

    private void StartTimer(){


        final long EndTime   = 20;
         cdtTimer = new CountDownTimer(EndTime*1000, 1000) {

            public void onTick(long millisUntilFinished) {

                long secondUntilFinished = (long) (millisUntilFinished/1000);
                long secondsPassed = (EndTime - secondUntilFinished);
                long minutesPassed = (long) (secondsPassed/60);
                secondsPassed = secondsPassed%60;
//                txt_description.setText(String.format("%02d", minutesPassed) + ":" + String.format("%02d", secondsPassed));
            }

            public void onFinish() {
                linear_before_work.setVisibility(View.VISIBLE);
                linear_details.setVisibility(View.GONE);
                et_Barcode.requestFocus();
                et_Barcode.setError(null);
            }
        }.start();
    }

    public void GetDetialsForBarcod() {
        Log.e("editbarcode", "" + et_Barcode.getImeActionId());
        Log.e("editbarcode", "" + et_Barcode.getId());
        Log.e("editbarcode", "" + et_Barcode.getImeActionLabel());

        ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

      /*  if (et_Barcode.getText().toString().isEmpty()) {
            et_Barcode.setError("من فضلك أدخل الباركود");
            et_Barcode.setText("");
            et_Barcode.requestFocus();

        } else {*/
            Depart = "";
            KQTY = "00";
            GQTY = "000";
            TotalQTYFor23 = "";
            BarcodeFor23 = "";
            Depart = et_Barcode.getText().toString().substring(0, 2);
            if (Depart.equalsIgnoreCase("23")
                    && et_Barcode.getText().toString().length() == 13) {
                KQTY = et_Barcode.getText().toString().substring(7, 9);
                GQTY = et_Barcode.getText().toString().substring(9, 12);
                TotalQTYFor23 = KQTY + "." + GQTY;

                //BarcodeFor23 = et_Barcode.getText().toString().replace(TotalQTYFor23.replace(".", ""), "00000");
                BarcodeFor23 = et_Barcode.getText().toString().substring(0, 7);


            } else if (Depart.equalsIgnoreCase("23")
                    && et_Barcode.getText().toString().length() != 13) {
                et_Barcode.setError("الباركود أقل أو أكبر من 13");
                et_Barcode.setText("");
                et_Barcode.requestFocus();
            }
            if (Depart.equalsIgnoreCase("23") && Double.valueOf(KQTY + GQTY) < 10) {
                et_Barcode.setError("تم إدخال قيمه أقل من 10 جرام");
                et_Barcode.setText("");
                et_Barcode.requestFocus();
            } else {
                Log.e("editbarcodeeeee", "" + et_Barcode.getText().toString().length());
                Log.e("editbarcodeeeee", "" + KQTY + "." + GQTY);

                editbarcode = et_Barcode.getText().toString();
                Log.e("editbarcode", "" + editbarcode);
//                progressBar.setVisibility(View.VISIBLE);
                et_Barcode.setEnabled(false);
                RequestQueue queue = Volley.newRequestQueue(this);
                request = new StringRequest(Request.Method.POST, Constant.GetDetialsURL,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                String encodedstring = null;
                                try {
                                    encodedstring = URLEncoder.encode(response, "ISO-8859-1");
                                    response = URLDecoder.decode(encodedstring, "UTF-8");

                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                                Log.e("onResponse", "response" + response);
                                Log.e("onResponse", "request" + request);
                                try {
//                                  progressBar.setVisibility(View.GONE);
                                    et_Barcode.setEnabled(true);
                                    et_Barcode.requestFocus();
                                    JSONObject object = new JSONObject(response);
                                    //Log.e("onResponse", "object"+object);

                                    //JSONObject object2 = object.getJSONObject("user");
                                    //String username = object.getString("status");
                                    // Log.e("onResponse", "object2"+object2);
                                    boolean b2 = Pattern.compile("01..........").matcher(editbarcode).matches();

                                    if (b2 == true) {
                                        txtPoint.setVisibility(View.VISIBLE);
                                        String name=object.getString("Name");
                                        txt_description.setText(name);
                                        String balance=object.getString("Balance");
                                        txt_price.setText(balance);
                                        et_Barcode.setText("");
                                        editbarcode.equals("");
                                        linear_for_offer.setVisibility(View.GONE);
                                    }else {
                                    String status = object.getString("status");
                                    Log.d("onResponse", status);

                                    if (status.equalsIgnoreCase("1")) {
                                        txtPoint.setVisibility(View.GONE);
                                        String description = object.getString("a_name");
                                        txt_description.setText(description);

                                        DecimalFormat dwith0 = new DecimalFormat("######.00");
                                        dwith0.setRoundingMode(RoundingMode.HALF_UP);
                                        Log.e("onResponse", "responseDepart" + editbarcode);

                                        String discounttype = object.getString("discounttype");
                                        String discountV =  object.getString("discountV");

                                        String date_from = object.getString("date_from");
                                        String date_to = object.getString("date_to");
                                        txt_offer_from_date.setText(date_from.substring(0, 10));
                                        txt_offer_to_date.setText(date_to.substring(0, 10));

                                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

                                        try {

                                            Date dateToday = simpleDateFormat.parse(simpleDateFormat.format(new Date()));
                                            Date dateTo = simpleDateFormat.parse(date_to.substring(0, date_to.length() - 3));

                                            if (dateTo.after(dateToday) && !discountV.equalsIgnoreCase("0")) {
                                                linear_for_offer.setVisibility(View.VISIBLE);
                                                txt_offer_how_much_day_remind.setText(printDifference(dateToday, dateTo));
                                            } else {
                                                linear_for_offer.setVisibility(View.GONE);
                                            }
                                            Log.e("onResponse", "response" + dateTo);
                                            Log.e("onResponse", "response" + dateToday);
                                            Log.e("onResponse", "response" + dateTo.after(dateToday));


                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                        if (!Depart.equalsIgnoreCase("23")) {
                                            String netPrice = "0";
                                            String netPriceBeforeDicount = "0";
                                            Double Discountvalue = 0.0;

                                            Discountvalue = Double.valueOf(object.getString("discountV"));

                                            netPrice = String.valueOf((Double.valueOf(object.getString("sell_price"))
                                                    - Double.valueOf(Discountvalue))
                                                    * (1 + (Double.valueOf(object.getString("vatrate")) / 100)));

                                            netPriceBeforeDicount = String.valueOf(Double.valueOf(object.getString("sell_price"))
                                                    * (1 + (Double.valueOf(object.getString("vatrate")) / 100)));

                                            /*if (discounttype.equalsIgnoreCase("3")) {
                                                Discountvalue = Double.valueOf(object.getString("discountV"));

                                                netPrice = String.valueOf((Double.valueOf(object.getString("sell_price"))
                                                        - Double.valueOf(Discountvalue))
                                                        * (1 + (Double.valueOf(object.getString("vatrate")) / 100)));
                                                netPriceBeforeDicount = String.valueOf(Double.valueOf(object.getString("sell_price"))
                                                        * (1 + (Double.valueOf(object.getString("vatrate")) / 100)));
                                            } else if (discounttype.equalsIgnoreCase("101")) {

                                                Discountvalue = Double.valueOf(object.getString("discountV")) * 0.01
                                                        * Double.valueOf(object.getString("sell_price"));

                                                netPrice = String.valueOf((Double.valueOf(object.getString("sell_price"))
                                                        - Discountvalue)
                                                        * (1 + (Double.valueOf(object.getString("vatrate")) / 100)));
                                                netPriceBeforeDicount = String.valueOf(Double.valueOf(object.getString("sell_price"))
                                                        * (1 + (Double.valueOf(object.getString("vatrate")) / 100)));
                                            } else {
                                                Discountvalue = 0.0;
                                                netPrice = String.valueOf((Double.valueOf(object.getString("sell_price"))
                                                        - Discountvalue)
                                                        * (1 + (Double.valueOf(object.getString("vatrate")) / 100)));
                                                netPriceBeforeDicount = String.valueOf(Double.valueOf(object.getString("sell_price"))
                                                        * (1 + (Double.valueOf(object.getString("vatrate")) / 100)));
                                            }*/
                                            Log.e("onResponse", "response" + netPrice.indexOf("."));
                                            Log.e("onResponse", "response" + netPrice);

                                            txt_price.setText(dwith0.format(Double.valueOf(netPrice)) + " جنيه");
                                            txt_price_before_discount.setText(dwith0.format(Double.valueOf(netPriceBeforeDicount)));
                                            et_Barcode.setText(null);
                                            et_Barcode.requestFocus();
                                            // Toast.makeText(MainActivity.this, "تم", Toast.LENGTH_SHORT).show();

                                        } else if (Depart.equalsIgnoreCase("23")) {

                                            String netPrice = "0.0";
                                            String netPriceBeforeDicount = "0";
                                            Double Discountvalue = 0.0;

                                            Discountvalue = Double.valueOf(object.getString("discountV"));

                                            netPrice = String.valueOf((Double.valueOf(object.getString("sell_price"))
                                                    - Double.valueOf(Discountvalue))
                                                    * (1 + (Double.valueOf(object.getString("vatrate")) / 100)));

                                            netPriceBeforeDicount = String.valueOf(Double.valueOf(object.getString("sell_price"))
                                                    * (1 + (Double.valueOf(object.getString("vatrate")) / 100)));


                                           /* if (discounttype.equalsIgnoreCase("3")) {

                                                Discountvalue = Double.valueOf(object.getString("discountV"));

                                                netPrice = String.valueOf((Double.valueOf(object.getString("sell_price"))
                                                        - Double.valueOf(Discountvalue))
                                                        * (1 + (Double.valueOf(object.getString("vatrate")) / 100)));
                                                netPriceBeforeDicount = String.valueOf(Double.valueOf(object.getString("sell_price"))
                                                        * (1 + (Double.valueOf(object.getString("vatrate")) / 100)));
                                            } else if (discounttype.equalsIgnoreCase("101")) {
//
                                                Discountvalue = Double.valueOf(object.getString("discountV")) * 0.01
                                                        * Double.valueOf(object.getString("sell_price"));

                                                netPrice = String.valueOf((Double.valueOf(object.getString("sell_price"))
                                                        - Discountvalue)
                                                        * (1 + (Double.valueOf(object.getString("vatrate")) / 100)));
                                                netPriceBeforeDicount = String.valueOf(Double.valueOf(object.getString("sell_price"))
                                                        * (1 + (Double.valueOf(object.getString("vatrate")) / 100)));
                                            } else {
                                                Discountvalue = 0.0;
                                                netPrice = String.valueOf((Double.valueOf(object.getString("sell_price"))
                                                        - Discountvalue)
                                                        * (1 + (Double.valueOf(object.getString("vatrate")) / 100)));
                                                netPriceBeforeDicount = String.valueOf((Double.valueOf(object.getString("sell_price"))
                                                        - Discountvalue)
                                                        * (1 + (Double.valueOf(object.getString("vatrate")) / 100)));
                                            }*/

                                            Log.e("onResponse", "response" + netPrice.indexOf("."));
                                            Log.e("onResponse", "response" + netPrice);

                                            KQTY = et_Barcode.getText().toString().substring(7, 9);
                                            GQTY = et_Barcode.getText().toString().substring(9, 12);
                                            TotalQTYFor23 = KQTY + "." + GQTY;
                                            Log.e("editbarcodeeeee", "" + KQTY + "." + GQTY);
                                            Log.e("editbarcodeeeee", "" + TotalQTYFor23);
                                            String BarcodeFor23ForInsert = object.getString("barcode").toString().substring(0, 7)
                                                    + KQTY
                                                    + GQTY
                                                    + object.getString("barcode").toString().substring(12, 13);

                                            Log.e("BarcodeFor23ForInsert", BarcodeFor23ForInsert);

                                            txt_price.setText(dwith0.format(Double.valueOf(netPrice)) + " جنيه");
                                            txt_price_before_discount.setText(dwith0.format(netPriceBeforeDicount));
                                            et_Barcode.setText(null);
                                            et_Barcode.requestFocus();
                                            editbarcode="";
                                            et_Barcode.setText("");
                                            // Toast.makeText(MainActivity.this, "تم", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Toast.makeText(MainActivity.this, "الباركود غير موجود", Toast.LENGTH_SHORT).show();
                                        et_Barcode.setText("");
                                        et_Barcode.requestFocus();
                                    }
                                }
                                } catch (JSONException e) {
//                                    progressBar.setVisibility(View.GONE);
                                    et_Barcode.setEnabled(true);
                                    Log.d("et_Barcode",et_Barcode.getText().toString());
                                    txtPoint.setVisibility(View.GONE);
                                    linear_for_offer.setVisibility(View.GONE);
                                    et_Barcode.setText("");
                                    et_Barcode.requestFocus();
                                    txt_price.setText("");
                                    txt_description.setText("الباركود غير معرف");
                                   // Toast.makeText(getBaseContext(), "" + e.toString(), Toast.LENGTH_SHORT).show();
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                                NetworkResponse response = error.networkResponse;
                                String errorMsg = "";
                                if (response != null && response.data != null) {
                                    String errorString = new String(response.data);
                                    et_Barcode.setEnabled(true);
                                    et_Barcode.setError("لم يتم أضافة الباركود .Net. حاول مره اخرى");
                                    et_Barcode.setText("");
                                    et_Barcode.requestFocus();
                                    Log.i("log error", errorString);
                                }
                            }
                        }
                ) {
                    @Override
                    protected VolleyError parseNetworkError(VolleyError volleyError) {
                        Log.i("log error no respon", "se6");
                        Log.i("log error no respon", "" + volleyError);
                        volleyErrorPublic = volleyError;
                        return super.parseNetworkError(volleyError);
                    }

                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();
                        if (Depart.equalsIgnoreCase("23")) {
                            params.put("Scales", "23");
                            params.put("Barcode", BarcodeFor23 + "00000");
                            //    params.put("Branch", BarcodeFor23 + "00000");
                            Log.e("bbbbbbbbbbbb", "ifff" + et_Barcode.getText().toString().replace(TotalQTYFor23, "00000"));
                        } else {
                            boolean b2 = Pattern.compile("01..........").matcher(editbarcode).matches();
                            if (b2 == true) {
                                params.put("Scales", "00");
                                params.put("Barcode", "-" + editbarcode);
                                Log.e("bbbbbbbbbbbb", "ifff" + Depart);
                            }
                            else {
                                params.put("Scales", "00");
                                params.put("Barcode", editbarcode);
                                Log.e("bbbbbbbbbbbb", "ifff" + Depart);}
                        }
            //TODO to check branch for select from server

                        params.put("Ip_Address", prefManager.get_branch().toString());
                        Log.e("bbbbbbbbbbbb", "ifff " + prefManager.get_branch().toString());

                        return params;
                    }
                };

                // Add the realibility on the connection.
                request.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));
                queue.add(request);

                if (volleyErrorPublic != null) {
                    Toast.makeText(MainActivity.this, "لم يتم الاتصال بالسيرفر", Toast.LENGTH_SHORT).show();

                }

            }
 //       }
    }

//1 minute = 60 seconds
//1 hour = 60 x 60 = 3600
//1 day = 3600 x 24 = 86400
    public String printDifference(Date startDate, Date endDate) {
        //milliseconds
        long different = endDate.getTime() - startDate.getTime();

        System.out.println("startDate : " + startDate);
        System.out.println("endDate : "+ endDate);
        System.out.println("different : " + different);

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;

        System.out.printf(
                "%d days, %d hours, %d minutes, %d seconds%n",
                elapsedDays, elapsedHours, elapsedMinutes, elapsedSeconds);
        return elapsedDays +"يوم,"+elapsedHours +"ساعه,"+ elapsedMinutes+"دقيقه";
    }

 /*   public void WriteIntableOfSqlServer() {

        RequestQueue queue = Volley.newRequestQueue(this);
        // String URL = Constant.LoginURL;
        request = new StringRequest(Request.Method.POST, Constant.WriteIntableURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.d("onResponseUmmm", response);
                        Log.e("onResponseUR", " " + request);

                        try {

                            JSONObject object = new JSONObject(response);
                            String status = object.getString("status");
                            Log.d("onResponse", status);

                            String message = object.getString("message");
                            Log.d("onResponse", message);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this, "" + e.toString(), Toast.LENGTH_SHORT).show();

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        NetworkResponse response = error.networkResponse;
                        String errorMsg = " ";
                        if (response != null && response.data != null) {
                            String errorString = new String(response.data);
                            Log.i("log error", errorString);
                        }
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<String, String>();
                JSONObject obj = null;
                JSONArray array = new JSONArray();
                ArrayList arrayreqest = new ArrayList();

                Po_Items_For_LogsArray = new ArrayList<Po_item_for_log>();
                Po_Item_For_Recycly = new ArrayList<Po_Item>();
                Po_Item_For_Recycly = databaseHelper.Get_Items();

                //if (!LastOrderArry.contains(Serialnum))
                databaseHelper.insertSerialNumber(Serialnum);
                // LastOrderArry.add(Serialnum);

                for (int i = 0; i < Po_Item_For_Recycly.size(); i++) {

                    Po_item_for_log Po_item_for_log = new Po_item_for_log();
                    Po_item_for_log.setTime(Serialnum);
                    Po_item_for_log.setBarcode(Po_Item_For_Recycly.get(i).getBarcode1());
                    Po_item_for_log.setDescription(Po_Item_For_Recycly.get(i).getDescribtion1());
                    Po_item_for_log.setSell_price(Po_Item_For_Recycly.get(i).getPrice1());
                    Po_item_for_log.setTax(Po_Item_For_Recycly.get(i).getTax1());
                    Po_item_for_log.setDiscount_Value(Po_Item_For_Recycly.get(i).qgetDiscount1());
                    Po_item_for_log.setTotalPrice(String.valueOf(Double.valueOf(Po_Item_For_Recycly.get(i).getQuantity1())
                            * Double.valueOf(Po_Item_For_Recycly.get(i).getNetprice1())));
                    Po_item_for_log.setQuantity(Po_Item_For_Recycly.get(i).getQuantity1());


                    Log.d("Build.MODELMacRCV", "RCV");

                    Po_Items_For_LogsArray.add(Po_item_for_log);

                }

                Gson gson = new GsonBuilder().create();
                JsonArray equipmentJsonArray = gson.toJsonTree(Po_Items_For_LogsArray).getAsJsonArray();

                //From_Sap_Or_Not=false;
                params.put("RequestArray", equipmentJsonArray.toString());

                params.put("Time", Serialnum);
                params.put("Branch", Branch);
                params.put("UserName", UserName);

                return params;
            }

        };

        // Add the realibility on the connection.
        request.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));

        // Start the request immediately
        queue.add(request);

    }*/

}