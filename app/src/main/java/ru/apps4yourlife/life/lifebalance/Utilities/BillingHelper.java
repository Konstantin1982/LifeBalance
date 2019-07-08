package ru.apps4yourlife.life.lifebalance.Utilities;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by ksharafutdinov on 08-Aug-18.
 */

public class BillingHelper {

    public interface LastPurchaseListener {
        public void setLastPurchase(String code);
    }

    private BillingClient mBillingClient;
    private Context mContext;
    private String mSkuCode;
    private LastPurchaseListener mPurchasesListener;

    public BillingHelper(Context context, PurchasesUpdatedListener listener, LastPurchaseListener listener2, String skuCode) {
        mContext = context;
        mSkuCode = skuCode;
        mBillingClient = BillingClient.newBuilder(mContext).setListener(listener).build();
        Purchase.PurchasesResult purchasesResult = mBillingClient.queryPurchases(BillingClient.SkuType.INAPP);
        listener.onPurchasesUpdated(BillingClient.BillingResponse.OK, purchasesResult.getPurchasesList());
        mPurchasesListener = listener2;
    }

    protected void startPurchase() {

        mPurchasesListener.setLastPurchase(mSkuCode);
        BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                .setSku(mSkuCode)
                .setType(BillingClient.SkuType.INAPP) // SkuType.SUB for subscription
                .build();
        int responseCode = mBillingClient.launchBillingFlow( (Activity) mContext, flowParams);
        //Log.e("ADS","CODE IS = " + responseCode);
    }


    public void StartOperationInStore() {
        mBillingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@BillingClient.BillingResponse int billingResponseCode) {
                if (billingResponseCode == BillingClient.BillingResponse.OK) {
                    List skuList = new ArrayList<>();
                    skuList.add(mSkuCode);
                    SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
                    params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP);
                    mBillingClient.querySkuDetailsAsync(params.build(),
                            new SkuDetailsResponseListener() {
                                @Override
                                public void onSkuDetailsResponse(int responseCode, List<SkuDetails> skuDetailsList) {
                                    if (responseCode == BillingClient.BillingResponse.OK) {
                                        for (SkuDetails skuDetails : skuDetailsList) {
                                            String sku = skuDetails.getSku();
                                            String price = skuDetails.getPrice();
                                            if (sku.equalsIgnoreCase(mSkuCode)) {
                                                startPurchase();
                                            }
                                        }

                                    }
                                }
                            });                        //
                } else {
                    Toast.makeText(mContext,"Ошибка при попытке соединения с Google Play. Попробуйте еще раз.", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onBillingServiceDisconnected() {
                Toast.makeText(mContext,"Ошибка при попытке соединения с Google Play. Попробуйте еще раз.", Toast.LENGTH_LONG).show();
            }
        });
    }

}
