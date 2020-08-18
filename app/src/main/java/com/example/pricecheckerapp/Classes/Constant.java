package com.example.pricecheckerapp.Classes;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Constant {

/// constant to connect to server on login activity
//    public final static String LoginURL ="http://localhost:8080/PhpQB/connecttosqlserver.php";

//    public final static String LoginURL ="http://10.2.1.220:8080/connecttosqlserver.php";

    public final static String GetDetialsURL ="http://10.2.1.220:8080/PriceChecker/SelectDataForBarcode_New_Version.php";
    public final static String WriteIntableURL="http://10.2.1.220:8080/QB/writeintables.php";
    public final static String WriteInprinttableURL="http://10.2.1.220:8080/QB/WriteinPrintAgaintable.php";
    public final static String RetrieveFromttableURL="http://10.2.1.220:8080/QB/RetriveOrderById.php";

    public static boolean isOnline(Context context){

        ConnectivityManager connectivityManager=(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo =connectivityManager.getActiveNetworkInfo();

        if (networkInfo !=null && networkInfo.isConnected()){

            return true;

        }else {

            return false;

        }

    }


}
