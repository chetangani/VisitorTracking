package com.tvd.visitortracking.fragments;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.tvd.visitortracking.MainActivity;
import com.tvd.visitortracking.R;
import com.tvd.visitortracking.goojprt.Canvas;
import com.tvd.visitortracking.goojprt.Pos;
import com.tvd.visitortracking.posting.SendingData;
import com.tvd.visitortracking.services.BluetoothService;
import com.tvd.visitortracking.values.ConstantValues;
import com.tvd.visitortracking.values.FunctionsCall;
import com.tvd.visitortracking.values.GetSetValues;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.ExecutorService;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class Visitor_details extends Fragment implements View.OnClickListener {
    View view;

    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    public static final int MEDIA_TYPE_IMAGE = 1;

    private static Uri fileUri; // file url to store image/video
    static File mediaFile;

    ImageView preview_image;
    Bitmap bitmap, barcode;

    Button submit_btn;
    EditText et_visitor_name, et_visitor_mbl_number, et_visitor_email, et_visitor_tomeet, et_visitor_from;
    String visitor_name="", visitor_number="", visitor_email="", visitor_tomeet="", visitor_from="", visitor_image="",
            visitor_image_encoded="";
    boolean image_taken = false;
    GetSetValues getSetValues;
    ProgressDialog progressDialog;
    FunctionsCall functionsCall = new FunctionsCall();

    Pos mPos = BluetoothService.mPos;
    Canvas mCanvas = BluetoothService.mCanvas;
    ExecutorService es = BluetoothService.es;
    float yaxis = 0;

    private final Handler mHandler;
    {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case ConstantValues.VISITOR_CHECK_IN_SUCCESS:
                        progressDialog.dismiss();
                        snackbar(submit_btn, "Successfully Updated");
                        clearEditText();
                        Random rand = new Random();
                        barcode = functionsCall.getBitmap(getActivity(), ""+(rand.nextInt(9000000) + 1000000) + getSetValues.getVisitorID(), 1, 450, 45);
                        es.submit(new TaskPrint(mPos));
                        break;

                    case ConstantValues.VISITOR_CHECK_IN_FAILURE:
                        break;
                }
            }
        };
    }

    public Visitor_details() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_visitor_details, container, false);

        getSetValues = ((MainActivity) getActivity()).getSetValues();

        submit_btn = (Button) view.findViewById(R.id.submit_btn);
        submit_btn.setOnClickListener(this);
        et_visitor_name = (EditText) view.findViewById(R.id.et_visitor_name);
        et_visitor_mbl_number = (EditText) view.findViewById(R.id.et_visitor_mbl_number);
        et_visitor_email = (EditText) view.findViewById(R.id.et_visitor_email);
        et_visitor_tomeet = (EditText) view.findViewById(R.id.et_visitor_to_meet);
        et_visitor_from = (EditText) view.findViewById(R.id.et_visitor_from);
        preview_image = (ImageView) view.findViewById(R.id.visitor_image);
        preview_image.setOnClickListener(this);

        return view;
    }

    @TargetApi(24)
    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE, getActivity());
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                previewCapturedImage();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getActivity(), "User cancelled image capture", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "Sorry! Failed to capture image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void previewCapturedImage() {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 8;
            bitmap = BitmapFactory.decodeFile(fileUri.getPath(), options);
            preview_image.setImageBitmap(rotateImage(bitmap, fileUri.getPath()));
            visitor_image = mediaFile.toString();
            image_taken = true;
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 8;
            bitmap = BitmapFactory.decodeFile(fileUri.getPath(), options);
            preview_image.setImageBitmap(rotateImage(bitmap, fileUri.getPath()));
            visitor_image = mediaFile.toString();
            image_taken = true;
        }
    }

    public static Bitmap rotateImage(Bitmap src, String Imagepath) {
        Bitmap bmp = null;
        Matrix matrix = new Matrix();
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(Imagepath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        Log.d("debug", "" + orientation);
        if (orientation == 1) {
            bmp = src;
        } else if (orientation == 3) {
            matrix.postRotate(180);
            bmp = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
        } else if (orientation == 8) {
            matrix.postRotate(270);
            bmp = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
        } else {
            matrix.postRotate(90);
            bmp = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
        }
        return bmp;
    }

    public Uri getOutputMediaFileUri(int type, Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", getOutputMediaFile(type));
        } else return Uri.fromFile(getOutputMediaFile(type));
    }

    private static File getOutputMediaFile(int type) {
        File mediaStorageDir = new File(android.os.Environment.getExternalStorageDirectory(), "VisitorTracking");

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("MMdd_HHmmss", Locale.getDefault()).format(new Date());
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + timeStamp + ".jpg");
        } else {
            return null;
        }
        return mediaFile;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.submit_btn:
                submitdetails(v);
                break;

            case R.id.visitor_image:
                captureImage();
                break;
        }
    }

    private void submitdetails(View view) {
        /*if (printer_connected) {

        } else snackbar(view, "Please connect to Bluetooth Printer and proceed...");*/
        if (functionsCall.isInternetOn(getActivity())) {
            visitor_name = et_visitor_name.getText().toString();
            if (functionsCall.checkEditTextValue(visitor_name, et_visitor_name, "Enter Visitor Name")) {
                visitor_number = et_visitor_mbl_number.getText().toString();
                if (functionsCall.checkEditTextValue(visitor_number, et_visitor_mbl_number, "Enter Visitor Mobile Number")) {
                    visitor_email = et_visitor_email.getText().toString();
                    visitor_tomeet = et_visitor_tomeet.getText().toString();
                    if (functionsCall.checkEditTextValue(visitor_tomeet, et_visitor_tomeet, "Enter Visitor To Meet")) {
                        visitor_from = et_visitor_from.getText().toString();
                        if (functionsCall.checkEditTextValue(visitor_from, et_visitor_from, "Enter Visitor From")) {
                            if (image_taken) {
                                try {
                                    visitor_image_encoded = functionsCall.encodeImage(rotateImage(bitmap, fileUri.getPath()), 100);
                                } catch (OutOfMemoryError e) {
                                    visitor_image_encoded = functionsCall.encodeImage(rotateImage(bitmap, fileUri.getPath()), 75);
                                }
                                progressDialog = ProgressDialog.show(getActivity(), "", "Logging In please wait..", true);
                                SendingData sendingData = new SendingData();
                                SendingData.Check_In_Visitors checkInVisitors = sendingData.new Check_In_Visitors(mHandler, getSetValues);
                                checkInVisitors.execute(visitor_name, visitor_number, visitor_email, visitor_tomeet, visitor_from,
                                        visitor_image_encoded);
                            } else snackbar(view, "Please take image and proceed...");
                        }
                    }
                }
            }
        } else snackbar(view, "Please connect to Internet and proceed...");
    }

    private void snackbar(View view, String msg) {
        Snackbar.make(view, msg, Snackbar.LENGTH_LONG).show();
    }

    public class TaskPrint implements Runnable {
        Pos pos = null;

        public TaskPrint(Pos pos) {
            this.pos = pos;
        }

        @Override
        public void run() {
            final boolean bPrintResult = PrintTicket(getActivity(), pos, 200, 0, false);
            final boolean bIsOpened = pos.GetIO().IsOpened();
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getActivity(), bPrintResult ? getResources().getString(R.string.printsuccess) : getResources().getString(R.string.printfailed), Toast.LENGTH_SHORT).show();
                    if (bIsOpened)
                        es.submit(new CanvasTaskPrint(mCanvas));
                }
            });
        }

        public boolean PrintTicket(Context ctx, Pos pos, int nPrintWidth, int nCompressMethod, boolean bCheckReturn) {
            boolean bPrintResult;
            byte[] status = new byte[1];
            if (!bCheckReturn || (bCheckReturn && pos.POS_QueryStatus(status, 3000, 2))) {
                Bitmap bmBlackWhite = rotateImage(bitmap, fileUri.getPath());
                if (bmBlackWhite != null) {
                    pos.POS_PrintPicture(bmBlackWhite, nPrintWidth, 0, nCompressMethod);
                }
            }
            bPrintResult = pos.GetIO().IsOpened();
            return bPrintResult;
        }
    }

    public class CanvasTaskPrint implements Runnable {
        Canvas canvas = null;

        public CanvasTaskPrint(Canvas canvas) {
            this.canvas = canvas;
        }

        @Override
        public void run() {
            final boolean bPrintResult = PrintTicket(getActivity(), canvas, 576, 300);
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getActivity(), bPrintResult ? getResources().getString(R.string.printsuccess) : getResources().getString(R.string.printfailed), Toast.LENGTH_SHORT).show();
                    yaxis = 0;
                }
            });
        }

        public boolean PrintTicket(Context ctx, Canvas canvas, int nPrintWidth, int nPrintHeight) {
            boolean bPrintResult;

            Typeface tfNumber = Typeface.createFromAsset(ctx.getAssets(), "DroidSansMono.ttf");
            canvas.CanvasBegin(nPrintWidth, nPrintHeight);
            canvas.SetPrintDirection(0);

            printtext(canvas, "Name: "+visitor_name, tfNumber, 25);
            printtext(canvas, "Mobile: "+visitor_number, tfNumber, 25);
            printtext(canvas, "To Meet: "+visitor_tomeet, tfNumber, 25);
            yaxis = yaxis + 15;
            canvas.DrawBitmap(barcode, 0, yaxis, 0);

            canvas.CanvasPrint(1, 0);

            bPrintResult = canvas.GetIO().IsOpened();
            return bPrintResult;
        }
    }

    private void printtext(Canvas canvas, String text, Typeface tfNumber, float textsize) {
        canvas.DrawText(text+"\r\n", 0, yaxis, 0, tfNumber, textsize, Canvas.DIRECTION_LEFT_TO_RIGHT);
        yaxis = yaxis + textsize + 8;
    }

    private void clearEditText() {
        et_visitor_name.setText("");
        et_visitor_mbl_number.setText("");
        et_visitor_email.setText("");
        et_visitor_tomeet.setText("");
        et_visitor_from.setText("");
        preview_image.setImageResource(R.mipmap.ic_camera);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mHandler.removeCallbacksAndMessages(null);
    }
}
