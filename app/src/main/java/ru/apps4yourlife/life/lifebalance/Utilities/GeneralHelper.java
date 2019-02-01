package ru.apps4yourlife.life.lifebalance.Utilities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.view.accessibility.AccessibilityManager;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import ru.apps4yourlife.life.lifebalance.R;

/**
 * Created by 123 on 27.03.2018.
 */

public class GeneralHelper {

    public static ArrayList<Integer> extractTypesFromWish(String types) {
        String[] typesArray = types.split(",");
        ArrayList<Integer> resultArray = new ArrayList<>();
        for (String type : typesArray) {
            resultArray.add(Integer.valueOf(type));
        }
        return resultArray;
    }

    public static Date GetCurrentDate() {
        GregorianCalendar tmpDate = new GregorianCalendar();
        GregorianCalendar normalDate = new GregorianCalendar(
                tmpDate.get(Calendar.YEAR),
                tmpDate.get(Calendar.MONTH),
                tmpDate.get(Calendar.DAY_OF_MONTH),
                0,
                0,
                0);
        return normalDate.getTime();
    }

    public static class WishStatusesClass {
        public static final int WISH_STATUS_NEW = 0;
        public static final int WISH_STATUS_UNDER_REVIEW = 1;
        public static final int WISH_STATUS_ACCEPTED = 2;
        public static final int WISH_STATUS_ACCEPTED_ITSELF = 22;
        public static final int WISH_STATUS_REJECTED = 3;
    }


    public static boolean isUserSubscribed() {
        return false;
    }

    public static void ShowRecommendToSubscribe(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.title_recommend_subscribe)
                .setMessage(R.string.text_recommend_subscribe)
                .setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(context,"УРА! Спасибо за подписку!",Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(R.string.myself, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(context,"Успехов! При необходимости всегда можно подключить ментора",Toast.LENGTH_LONG).show();
                    }
                });
        builder.show();
    }

}
