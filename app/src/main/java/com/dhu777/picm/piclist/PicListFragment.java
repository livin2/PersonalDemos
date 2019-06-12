package com.dhu777.picm.piclist;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.dhu777.picm.R;
import com.dhu777.picm.data.entity.PicInfo;
import com.dhu777.picm.data.remote.PicRemoteContract;
import com.dhu777.picm.picdetail.PicDetailActivity;
import com.dhu777.picm.util.DataHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.dhu777.picm.util.ComUtil.checkNotNull;

public class PicListFragment extends Fragment implements PicListContract.View{
    private PicListContract.Presenter mPresenter;
    private PicListAdapter mPicListAdapter;
    private StaggeredGridLayoutManager layoutManager;

    public static PicListFragment newInstance() { return new PicListFragment(); }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPicListAdapter = new PicListAdapter(null);
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.piclis_frag,container,false);
        RecyclerView recyclerView = (RecyclerView)root;
        recyclerView.setAdapter(mPicListAdapter);

        layoutManager = new StaggeredGridLayoutManager(2,
                StaggeredGridLayoutManager.VERTICAL);
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        recyclerView.setLayoutManager(layoutManager);
//        recyclerView.setItemAnimator(null);
        return root;
    }

    @Override
    public void setLayoutSpanCount(int count){
        layoutManager.setSpanCount(count);
    }

    @Override
    public void showPictures(List<PicInfo> pics) {
        mPicListAdapter.replaceData(pics);
    }

    @Override
    public void showLoadingPicturesError() {
        Toast.makeText(PicListFragment.this.getContext(),
                R.string.load_piclist_error,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setPresenter(PicListContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }

    private void openDetail(@NonNull PicInfo picI){
        Intent intent = new Intent(getContext(), PicDetailActivity.class);
        DataHolder.getInstance().setImageInfo(picI);
        startActivity(intent);
    }

    private  class PicListAdapter extends RecyclerView.Adapter<PicListAdapter.ViewHolder>{
        private  class ViewHolder extends RecyclerView.ViewHolder{
            CardView cardView;
            ImageView imageView;
            ConstraintLayout constrainImgHold;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                cardView = (CardView)itemView;
                imageView = checkNotNull(itemView).findViewById(R.id.card_img);
                constrainImgHold = checkNotNull(itemView).findViewById(R.id.hold_img);
            }
        }

        private List<PicInfo> picInfos;
        private Context mContext = null; //gilde neet it
        private ConstraintSet mConstraintSet = new ConstraintSet();
        public PicListAdapter(@Nullable List<PicInfo> picInfos) {
            this.picInfos = picInfos;
        }

        public void replaceData(List<PicInfo> pics){
            Log.d("PicListAdapter", "replaceData: new list size"+pics.size());
            if(picInfos == null){
                picInfos = pics;
                notifyItemRangeInserted(0,picInfos.size());
            }else if(picInfos.hashCode()!=pics.hashCode()){
                picInfos = pics;
                notifyDataSetChanged();
            }
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if(mContext == null)
                mContext=parent.getContext();
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.piclist_item,parent,false);
            ViewHolder vh = new ViewHolder(view);
            return vh;
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
            final PicInfo picI =checkNotNull(picInfos.get(position));
            Log.i("PicListAdapter", "onBindViewHolder: "+picI.getThumbURL());

            Glide.with(checkNotNull(mContext)).load(picI.getThumbURL()).into(holder.imageView);
            String ratio =String.format("%d:%d", picI.getWidth(),picI.getHeight());
            mConstraintSet.clone(holder.constrainImgHold);
            mConstraintSet.setDimensionRatio(R.id.card_img,ratio);
            mConstraintSet.applyTo(holder.constrainImgHold);
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openDetail(picI);
                }
            });
        }

        @Override
        public int getItemCount() {
            if (picInfos==null)
                return 0;
            return picInfos.size();
        }
    }
}
