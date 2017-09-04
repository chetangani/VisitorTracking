package com.tvd.visitortracking.posting;

import android.os.Handler;

import com.tvd.visitortracking.adapter.VisitorAdapter;
import com.tvd.visitortracking.values.ConstantValues;
import com.tvd.visitortracking.values.FunctionsCall;
import com.tvd.visitortracking.values.GetSetValues;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;

public class ReceivingData {
    FunctionsCall functionsCall = new FunctionsCall();

    public String parseServerXML(String result) {
        String value="";
        XmlPullParserFactory pullParserFactory;
        InputStream res;
        try {
            res = new ByteArrayInputStream(result.getBytes());
            pullParserFactory = XmlPullParserFactory.newInstance();
            pullParserFactory.setNamespaceAware(true);
            XmlPullParser parser = pullParserFactory.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(res, null);
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String name = parser.getName();
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        switch (name) {
                            case "string":
                                value =  parser.nextText();
                                break;
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                }
                eventType = parser.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    public void check_in_visitor_status(String result, Handler handler, GetSetValues getset) {
        result = parseServerXML(result);
        functionsCall.logStatus("Receiving result: "+result);
        getset.setVisitorID(result.substring(1, result.lastIndexOf(',')));
        getset.setVisitor_Checkin(result.substring(result.indexOf(',')+1, result.length()-1));
        handler.sendEmptyMessage(ConstantValues.VISITOR_CHECK_IN_SUCCESS);
    }

    public void check_out_visitor_status(String result, Handler handler, GetSetValues getset) {
        result = parseServerXML(result);
        functionsCall.logStatus("Receiving result: "+result);
        getset.setVisitor_Checkout_Result(result.substring(1, result.length()-1));
        handler.sendEmptyMessage(ConstantValues.VISITOR_CHECKOUT_SUCCESS);
    }

    public void visitor_data_status(String result, Handler handler, GetSetValues getset) {
        result = parseServerXML(result);
        functionsCall.logStatus("Receiving result: "+result);
        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(result);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                getset.setVisitor_data_name(jsonObject.getString("VName"));
                getset.setVisitor_data_number(jsonObject.getString("MobNo"));
                getset.setVisitor_data_tomeet(jsonObject.getString("ToMeet"));
                getset.setVisitor_data_from(jsonObject.getString("FrmLoc"));
                getset.setVisitor_data_checkin(jsonObject.getString("ChkIN"));
                if (jsonObject.getString("ChkOUT").equals("null")) {
                    getset.setVisitor_data_checkout("");
                } else getset.setVisitor_data_checkout(jsonObject.getString("ChkOUT"));
                String image = jsonObject.getString("VPhoto");
                getset.setVisitor_data_image(image.substring(image.lastIndexOf('/')+1, image.length()));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            getset.setVisitor_data_name("");
            getset.setVisitor_data_number("");
            getset.setVisitor_data_tomeet("");
            getset.setVisitor_data_from("");
            getset.setVisitor_data_checkin("");
            getset.setVisitor_data_checkout("");
            getset.setVisitor_data_image("");
        }
        handler.sendEmptyMessage(ConstantValues.VISITOR_DATA_SUCCESS);
    }

    public void visitors_all_data_status(String result, Handler handler, GetSetValues getset,
                                         ArrayList<GetSetValues> arrayList, VisitorAdapter adapter) {
        result = parseServerXML(result);
        functionsCall.logStatus("Receiving result: "+result);
        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(result);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                getset = new GetSetValues();
                getset.setVisitor_view_ID(jsonObject.getString("VID"));
                getset.setVisitor_view_name(jsonObject.getString("VName"));
                getset.setVisitor_view_number(jsonObject.getString("MobNo"));
                getset.setVisitor_view_tomeet(jsonObject.getString("ToMeet"));
                getset.setVisitor_view_from(jsonObject.getString("FrmLoc"));
                getset.setVisitor_view_checkin(jsonObject.getString("ChkIN"));
                if (jsonObject.getString("ChkOUT").equals("null")) {
                    getset.setVisitor_view_checkout("");
                } else getset.setVisitor_view_checkout(jsonObject.getString("ChkOUT"));
                String image = jsonObject.getString("VPhoto");
                functionsCall.logStatus("Image: "+image.substring(image.lastIndexOf('/')+1, image.length()));
                getset.setVisitor_view_image(image.substring(image.lastIndexOf('/')+1, image.length()));
                arrayList.add(getset);
                adapter.notifyDataSetChanged();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        handler.sendEmptyMessage(ConstantValues.VISITOR_SUCCESS);
    }

    public void login_status(String result, Handler handler, GetSetValues getset) {
        result = parseServerXML(result);
        handler.sendEmptyMessage(ConstantValues.LOGIN_SUCCESSFUL);
    }

    public void logout_status(String result, Handler handler, GetSetValues getset) {
        result = parseServerXML(result);
        handler.sendEmptyMessage(ConstantValues.LOGOUT_SUCCESSFUL);
    }
}
