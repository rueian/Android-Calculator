package com.rueian.calc;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;


public class MainActivity extends Activity implements View.OnLongClickListener {

    protected TextView inputField;
    protected TextView resultField;
    protected Button clearBtn;
    protected NotificationManager mNotificationManager;
    protected final int mId = 7374;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        inputField  = (TextView)findViewById(R.id.input);
        resultField = (TextView)findViewById(R.id.result);
        clearBtn    = (Button)findViewById(R.id.btn_del);

        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        clearBtn.setOnLongClickListener(this);
    }

    public void onBtnClick(View view) {
        switch (view.getId()) {
            case R.id.btn_eq:
                onEquals();
                break;
            case R.id.btn_del:
                onDelete();
                break;
            default:
                inputField.append(((Button)view).getText());
                break;
        }
    }

    private void onEquals() {
        String input = inputField.getText().toString();
        try {
            Expression expression = new Expression(input);
            BigDecimal result = expression.eval();
            resultField.setText(result.toString());
        } catch (Exception e) {
            errorNotification(input);
        }
    }

    private void onDelete() {
        Editable inputText = inputField.getEditableText();
        int length = inputText.length();
        if (inputText.length() > 0) {
            inputText.delete(length - 1, length);
        }
    }

    private void onClear() {
        inputField.setText("");
        resultField.setText("");
        mNotificationManager.cancelAll();
    }

    @Override
    public boolean onLongClick(View view) {
        if (view.getId() == R.id.btn_del) {
            onClear();
            Toast.makeText(this, "已清除", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    private void errorNotification(String input) {
        Toast.makeText(this, "錯誤！詳細看通知欄", Toast.LENGTH_SHORT).show();

        Notification.Builder builder = new Notification.Builder(this);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle("計算錯誤");
        builder.setContentText("您的算式有錯誤喔！");
        builder.setAutoCancel(true);
        Notification.InboxStyle inboxStyle = new Notification.InboxStyle();

        inboxStyle.setBigContentTitle("計算錯誤");
        inboxStyle.addLine("您的算式有錯誤喔！");
        inboxStyle.addLine("您錯誤的算式:");
        inboxStyle.addLine(input);
        builder.setStyle(inboxStyle);

        Intent resultIntent = new Intent(this, MainActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        builder.setContentIntent(resultPendingIntent);

        // mId allows you to update the notification later on.
        mNotificationManager.notify(this.mId, builder.build());
    }
}
