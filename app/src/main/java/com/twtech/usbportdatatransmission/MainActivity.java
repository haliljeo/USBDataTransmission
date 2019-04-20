package com.twtech.usbportdatatransmission;

import com.physicaloid.lib.Physicaloid;
import com.physicaloid.lib.usb.driver.uart.ReadLisener;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.util.Date;

public class MainActivity extends Activity {
    Button btOpen, btClose, btWrite,btClear;
    EditText etWrite;
    TextView tvRead;
    Spinner spBaud;
    CheckBox cbAutoscroll;
    Boolean isAscii = Boolean.valueOf(true);
    Boolean isReceive_ASCII = Boolean.valueOf(false);

    Physicaloid mPhysicaloid; // initialising library

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btOpen = (Button) findViewById(R.id.btOpen);
        btClose = (Button) findViewById(R.id.btClose);
        btWrite = (Button) findViewById(R.id.btWrite);
        etWrite = (EditText) findViewById(R.id.etWrite);
        tvRead = (TextView) findViewById(R.id.tvRead);
        spBaud = (Spinner) findViewById(R.id.spBaud);
        btClear = (Button) findViewById(R.id.btClear);
        cbAutoscroll = (CheckBox) findViewById(R.id.autoscroll);
        setEnabledUi(false);
        mPhysicaloid = new Physicaloid(this);
        // new MyLogger().storeMessage("onCreate","Main Activity called");
        //mPhysicaloid.setBaudrate(115200);





    }

    public void onClear(View v){
        tvRead.setText("");
    }

    public void onClickOpen(View v) {
        // String baudtext = spBaud.getSelectedItem().toString();
        /*switch (baudtext) {
            case "300 baud":
                mPhysicaloid.setBaudrate(300);
                break;
            case "1200 baud":
                mPhysicaloid.setBaudrate(1200);
                break;
            case "2400 baud":
                mPhysicaloid.setBaudrate(2400);
                break;
            case "4800 baud":
                mPhysicaloid.setBaudrate(4800);
                break;
            case "9600 baud":
                mPhysicaloid.setBaudrate(9600);
                break;
            case "19200 baud":
                mPhysicaloid.setBaudrate(19200);
                break;
            case "38400 baud":
                mPhysicaloid.setBaudrate(38400);
                break;
            case "576600 baud":
                mPhysicaloid.setBaudrate(576600);
                break;
            case "744880 baud":115200
                mPhysicaloid.setBaudrate(744880);
                break;
            case "115200 baud":
                mPhysicaloid.setBaudrate(115200);
                break;
            case "230400 baud":
                mPhysicaloid.setBaudrate(230400);
                break;
            case "250000 baud":
                mPhysicaloid.setBaudrate(250000);
                break;
            default:
                mPhysicaloid.setBaudrate(9600);
        }*/

        if (mPhysicaloid.open()) {
            setEnabledUi(true);
            mPhysicaloid.setBaudrate(115200);

            if (cbAutoscroll.isChecked()) {
                tvRead.setMovementMethod(new ScrollingMovementMethod());
                Toast.makeText(this, "inside cbAutoscroll", Toast.LENGTH_SHORT).show();
            }
            mPhysicaloid.addReadListener(new ReadLisener() {
                @Override
                public void onRead(int size) {

                    byte[] buf = new byte[size];
                    mPhysicaloid.read(buf, size);
                    // new MyLogger().storeMessage("mPhysicaloid","open method called...");
                    //   tvRead.append(asciiToHex(new String(buf)));
                   // tvAppend(tvRead, Html.fromHtml("<font color=blue>" + asciiToHex(new String(buf)) + "</font>"));
                    updateReceivedData(buf);
                }

            });
        } else {
            Toast.makeText(this, "Cannot open", Toast.LENGTH_LONG).show();
        }
    }

    public static void appendColoredText(TextView paramTextView, String paramString, int paramInt) {
        int i = paramTextView.getText().length();
        paramTextView.append(paramString);
        int j = paramTextView.getText().length();
        ((Spannable) paramTextView.getText()).setSpan(new ForegroundColorSpan(paramInt), i, j, 0);
    }


    private void updateReceivedData(byte[] paramArrayOfByte) {
        int[] arrayOfInt = new int[paramArrayOfByte.length];
        for (int i = 0; i < paramArrayOfByte.length; i++) {
            arrayOfInt[i] = (0xFF & paramArrayOfByte[i]);
        }
        String str1 = new String(arrayOfInt, 0, paramArrayOfByte.length);

        tvAppend(tvRead, Html.fromHtml("<font color=blue>" + asciiToHex(new String(str1)) + "</font>"));


    }


    public void onClickClose(View v) {
        if(mPhysicaloid.close()) {
            mPhysicaloid.clearReadListener();
            setEnabledUi(false);
        }
    }

    public void onClickWrite(View v) {
        String str = etWrite.getText().toString()+"\r\n";
        if(str.length()>0) {
            byte[] buf = str.getBytes();
            // new MyLogger().storeMessage("onClickWrite", String.valueOf(buf));
            mPhysicaloid.write(new byte[]{0x02, 0x00, 0x01, (byte) 0xF2}, buf.length);
        }
    }

    public void onSendCommand(View v) {
        // String str = "A004FF8901D3" + "\r\n";
        try {
            sendHEX("A004FF8901D3");
        }catch (Exception e){
            Toast.makeText(this, "excep "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }


    }


    private static String asciiToHex(String paramString)
    {
        char[] arrayOfChar = paramString.toCharArray();
        StringBuilder localStringBuilder = new StringBuilder();
        int i = arrayOfChar.length;
        for (int j = 0;; j++) {
            if (j < i)
            {
                int k = arrayOfChar[j];
                try
                {
                    String str = Integer.toHexString(k);
                    if (str.length() == 1) {
                        localStringBuilder.append("0").append(str).append(" ");
                    } else {
                        localStringBuilder.append(str).append(" ");
                    }
                }
                catch (Exception localException)
                {
                    localException.printStackTrace();
                }
            }
            else
            {
                return localStringBuilder.toString();
            }
        }
    }

    public void sendHEX(String paramString)
    {

        try {
            byte[] arrayOfByte;
            paramString = paramString + "0d0a";

            //byte[] arrayOfByte;
            // for (; ; ) {
            int i = paramString.length();
            arrayOfByte = new byte[i / 2];
            for (int j = 0; j < i; j += 2) {
                arrayOfByte[(j / 2)] = ((byte) ((Character.digit(paramString.charAt(j), 16) << 4) + Character.digit(paramString.charAt(j + 1), 16)));
            }

            paramString = paramString + "0d";

            // String str2 = paramString + "0a";
            //paramString = str2;


            mPhysicaloid.write(arrayOfByte, arrayOfByte.length);

        }
        catch (NumberFormatException localNumberFormatException)
        {
            Log.d("toHex NumberFormatExce", localNumberFormatException.getMessage());
            Toast.makeText(this, "Invalid Value", Toast.LENGTH_LONG).show();
            return;
        }
        catch (StringIndexOutOfBoundsException localStringIndexOutOfBoundsException)
        {
            Log.d("StringIndexOutofbound", localStringIndexOutOfBoundsException.getMessage());
        }
    }

    private void setEnabledUi(boolean on) {
        if(on) {
            btOpen.setEnabled(false);
            spBaud.setEnabled(false);
            cbAutoscroll.setEnabled(false);
            btClose.setEnabled(true);
            btWrite.setEnabled(true);
            etWrite.setEnabled(true);
        } else {
            btOpen.setEnabled(true);
            spBaud.setEnabled(true);
            cbAutoscroll.setEnabled(true);
            btClose.setEnabled(false);
            btWrite.setEnabled(false);
            etWrite.setEnabled(false);
        }
    }

    Handler mHandler = new Handler();
    private void tvAppend(TextView tv, CharSequence text) {
        final TextView ftv = tv;
        final CharSequence ftext = text;
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                ftv.append(ftext);
            }
        });
    }
}