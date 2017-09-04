package com.tvd.visitortracking.posting;

import android.os.AsyncTask;
import android.os.Handler;

import com.tvd.visitortracking.adapter.VisitorAdapter;
import com.tvd.visitortracking.values.FunctionsCall;
import com.tvd.visitortracking.values.GetSetValues;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class SendingData {
    ReceivingData receivingData = new ReceivingData();
    FunctionsCall functionsCall = new FunctionsCall();

    private String UrlPostConnection(String Post_Url, HashMap<String, String> datamap) throws IOException {
        String response = "";
        URL url = new URL(Post_Url);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(15000);
        conn.setConnectTimeout(15000);
        conn.setRequestMethod("POST");
        conn.setDoInput(true);
        conn.setDoOutput(true);

        OutputStream os = conn.getOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
        writer.write(getPostDataString(datamap));
        writer.flush();
        writer.close();
        os.close();
        int responseCode=conn.getResponseCode();
        if (responseCode == HttpsURLConnection.HTTP_OK) {
            String line;
            BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
            while ((line=br.readLine()) != null) {
                response+=line;
            }
        }
        else {
            response="";
        }
        return response;
    }

    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String, String> entry : params.entrySet()){
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            functionsCall.logStatus(result.toString());
        }

        return result.toString();
    }

    private String UrlGetConnection(String Get_Url) throws IOException {
        String response = "";
        URL url = new URL(Get_Url);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(15000);
        conn.setConnectTimeout(15000);
        int responseCode=conn.getResponseCode();
        if (responseCode == HttpsURLConnection.HTTP_OK) {
            String line;
            BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
            while ((line=br.readLine()) != null) {
                response+=line;
            }
        }
        else {
            response="";
        }
        return response;
    }

    public class Check_In_Visitors extends AsyncTask<String, String, String> {
        Handler handler;
        GetSetValues getSetValues;
        String response="";

        public Check_In_Visitors(Handler handler, GetSetValues getSetValues) {
            this.handler = handler;
            this.getSetValues = getSetValues;
        }

        @Override
        protected String doInBackground(String... params) {
            HashMap<String, String> datamap = new HashMap<>();
            datamap.put("VName", params[0]);
            datamap.put("EMail", params[2]);
            datamap.put("MobNo", params[1]);
            datamap.put("FrmLoc", params[4]);
            datamap.put("ToMeet", params[3]);
            datamap.put("VPhoto", params[5]);
            try {
                response = UrlPostConnection(DataAPI.BASE_URL+"VisitorChkIN", datamap);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            receivingData.check_in_visitor_status(result, handler, getSetValues);
        }
    }

    public class Check_Out_Visitor extends AsyncTask<String, String, String> {
        String response="";
        Handler handler;
        GetSetValues getSetValues;

        public Check_Out_Visitor(Handler handler, GetSetValues getSetValues) {
            this.handler = handler;
            this.getSetValues = getSetValues;
        }

        @Override
        protected String doInBackground(String... params) {
            HashMap<String, String> datamap = new HashMap<>();
            datamap.put("VID", params[0]);
            try {
                response = UrlPostConnection(DataAPI.BASE_URL+"VisitorChkOUT", datamap);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            receivingData.check_out_visitor_status(result, handler, getSetValues);
        }
    }

    public class Visitor_Data extends AsyncTask<String, String, String> {
        String response="";
        Handler handler;
        GetSetValues getSetValues;

        public Visitor_Data(Handler handler, GetSetValues getSetValues) {
            this.handler = handler;
            this.getSetValues = getSetValues;
        }

        @Override
        protected String doInBackground(String... params) {
            HashMap<String, String> datamap = new HashMap<>();
            datamap.put("VId", params[0]);
            try {
                response = UrlPostConnection(DataAPI.BASE_URL+"VData", datamap);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            receivingData.visitor_data_status(result, handler, getSetValues);
        }
    }

    public class AllVisitors_Data extends AsyncTask<String, String, String> {
        String response="";
        Handler handler;
        GetSetValues getSetValues;
        ArrayList<GetSetValues> arrayList;
        VisitorAdapter visitorAdapter;

        public AllVisitors_Data(Handler handler, GetSetValues getSetValues, ArrayList<GetSetValues> arrayList,
                                VisitorAdapter visitorAdapter) {
            this.handler = handler;
            this.getSetValues = getSetValues;
            this.arrayList = arrayList;
            this.visitorAdapter = visitorAdapter;
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                response = UrlGetConnection(DataAPI.BASE_URL+"AllVData");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            receivingData.visitors_all_data_status(result, handler, getSetValues, arrayList, visitorAdapter);
        }
    }

    public class Login_Details extends AsyncTask<String, String, String> {
        String response="";
        Handler handler;
        GetSetValues getSetValues;

        public Login_Details(Handler handler, GetSetValues getSetValues) {
            this.handler = handler;
            this.getSetValues = getSetValues;
        }

        @Override
        protected String doInBackground(String... params) {
            HashMap<String, String> datamap = new HashMap<>();
            datamap.put("", params[0]);
            datamap.put("", params[1]);
            try {
                response = UrlPostConnection(DataAPI.BASE_URL, datamap);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            receivingData.login_status(result, handler, getSetValues);
        }
    }

    public class Logout_Details extends AsyncTask<String, String, String> {
        String response="";
        Handler handler;
        GetSetValues getSetValues;

        @Override
        protected String doInBackground(String... params) {
            HashMap<String, String> datamap = new HashMap<>();
            datamap.put("", params[0]);
            try {
                response = UrlPostConnection(DataAPI.BASE_URL, datamap);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            receivingData.logout_status(result, handler, getSetValues);
        }
    }

}
