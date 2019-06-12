package com.dhu777.picm.data;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dhu777.picm.R;
import com.dhu777.picm.data.entity.PicInfo;
import com.dhu777.picm.data.remote.PicUpload;
import com.dhu777.picm.mock.Injection;
import com.dhu777.picm.util.ComUtil;

import java.util.List;

public class PicInfoRepositrory implements PicDataSource{
    private static PicInfoRepositrory INSTANCE = null;
//    private static PicInfoRepositrory USERINSTANCE = null; //todo fixed userinstance
    private PicDataSource picRemoteRepo;
    private final PicDataSource picLocalRepo;
    private final PicUpload picUploadApi;

    private static final String TAG = "PicInfoRepositrory";
    private static final String MSG_FAIL_FETCHPIC = ComUtil.getContext().getString(R.string.msg_fail_fetchpics);

    private List<PicInfo> picListCache;
    private Boolean CacheIsDirty = true;

    public static PicInfoRepositrory getInstance(){
        return INSTANCE;
    }

    public static PicInfoRepositrory getInstance(PicDataSource picRemoteRepo,
                                                 PicDataSource picLocalRepo,
                                                 PicUpload picUploadApi){
        if(INSTANCE == null){
            INSTANCE = new PicInfoRepositrory(picRemoteRepo,picLocalRepo,
                    picUploadApi);
        }else if(picRemoteRepo==null ||
                INSTANCE.picRemoteRepo.hashCode()!=picRemoteRepo.hashCode()){
            INSTANCE.picRemoteRepo = picRemoteRepo;
            INSTANCE.CacheIsDirty = true;
        }
        Log.d(TAG, "getInstance: preRemotehash "+INSTANCE.picRemoteRepo.hashCode());
        Log.d(TAG, "getInstance: newRemotehash "+picRemoteRepo.hashCode());

        return INSTANCE;
    }

//    public static void destory(){
//        INSTANCE = null;
//    }

    private PicInfoRepositrory(@NonNull PicDataSource picRemoteRepo,
                               @NonNull PicDataSource picLocalRepo,
                               @NonNull PicUpload picUploadApi) {
        this.picRemoteRepo = picRemoteRepo;
        this.picLocalRepo = picLocalRepo;
        this.picUploadApi = picUploadApi;
    }

    @Override
    public void refresh() {
        this.CacheIsDirty =true;
    }

    @Override
    public void fetchPicList(@NonNull final FetchPicsCallback callback) {
        if (picListCache!=null && !CacheIsDirty){
            Log.d(TAG, "fetchPicList: loadCache");
            callback.onDataLoaded(picListCache);
            return;
        }

        picRemoteRepo.fetchPicList(new FetchPicsCallback() {
            @Override
            public void onDataLoaded(@NonNull List<PicInfo> picList) {
                if(picList != null){
                    picListCache = picList;
                    CacheIsDirty = false;
                    Log.d(TAG, "fetchPicList: load from Remote");
                    callback.onDataLoaded(picListCache);
                }else{
                    Log.d(TAG, "onDataLoaded:got null from Remote Respone");
                    callback.onDataNotAvailable(new Exception(MSG_FAIL_FETCHPIC));
                }
            }
            @Override
            public void onDataNotAvailable(@NonNull Exception e) {
                callback.onDataNotAvailable(e);
            }
        });
    }

    @Override
    public void getPic(@NonNull String picId, @NonNull GetPicCallback callback) {
        //TODO
    }

    @Override
    public void deletePic(@NonNull String picId,@NonNull String jwt, @Nullable DelPicCallback callback) {
        picRemoteRepo.deletePic(picId,jwt,callback);
    }


    public void upload(byte[] file, String sourceid, String jwt, PicUpload.UpPicCallback callback){
        picUploadApi.upload(file,sourceid,jwt,callback);
    }

}
