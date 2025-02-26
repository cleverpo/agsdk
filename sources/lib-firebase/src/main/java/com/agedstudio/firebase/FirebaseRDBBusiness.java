package com.agedstudio.firebase;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import com.agedstudio.base.AbstractRDBApi;
import com.agedstudio.base.utils.LogUtil;
import com.agedstudio.base.utils.NetworkUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;
import java.util.Map;

public class FirebaseRDBBusiness extends AbstractRDBApi {

    private static final String TAG = FirebaseRDBBusiness.class.getSimpleName();

    private DatabaseReference mDatabaseReference;

    @Override
    public void init(Context context) {
        super.init(context);
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public void getData(String jsMethodKey, String dbName, String id, AbstractRDBApi.OnGetDataListener listener) {
        LogUtil.i(TAG, "get data:" + " dbName=" + dbName + "   id=" + id);

        NetworkUtils.isAvailableAsync(new NetworkUtils.OnNetworkListener() {
            @Override
            public void OnAccept(Boolean bool) {
                if (!bool) {
                    if (null != listener) {
                        listener.onGetDataFail(jsMethodKey, -1, "network error");
                    }
                } else {
                    mDatabaseReference.child(dbName + "/" + id).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            if (task != null && task.isSuccessful()) {
                                LogUtil.i(TAG, "get data success");
                                String data = task.getResult().getValue(String.class);
                                if (null != listener) {
                                    listener.onGetDataSuccess(jsMethodKey, data);
                                }
                            } else {
                                LogUtil.i(TAG, "get data error");
                                if (null != listener) {
                                    listener.onGetDataFail(jsMethodKey, -1, "error");
                                }
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            LogUtil.i(TAG, "get data fail");
                            if (null != listener) {
                                listener.onGetDataFail(jsMethodKey, -1, "fail");
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    public void setData(String jsMethodKey, String dbName, String id, String data, AbstractRDBApi.OnSetDataListener listener) {
        LogUtil.i(TAG, "set data");

        NetworkUtils.isAvailableAsync(new NetworkUtils.OnNetworkListener() {
            @Override
            public void OnAccept(Boolean bool) {
                if (!bool) {
                    if (null != listener) {
                        listener.onSetDataFail(jsMethodKey, -1, "network error");
                    }
                } else {
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put(dbName + "/" + id, data);
                    mDatabaseReference.updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            LogUtil.i(TAG, "set data success");
                            if (null != listener) {
                                listener.onSetDataSuccess(jsMethodKey);
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            LogUtil.i(TAG, "set data fail");
                            if (null != listener) {
                                listener.onSetDataFail(jsMethodKey, -1, "fail");
                            }
                        }
                    });
                }
            }
        });
    }
}
