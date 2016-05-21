package com.ikkong.wximagepicker;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.R;
import com.lzy.imagepicker.Utils;
import com.lzy.imagepicker.adapter.ImagePageAdapter;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageBaseActivity;
import com.lzy.imagepicker.view.ViewPagerFixed;

import java.util.ArrayList;

/**
 * 预览已经选择的图片，并可以删除
 */
public class ImagePreviewDelActivity extends ImageBaseActivity implements View.OnClickListener{

    private int mCurrentPosition = 0;         //跳转进来时的序号，第几个图片
    private TextView mTitleCount;             //显示当前图片的位置  例如  5/31
    private ArrayList<ImageItem> selectedImages;   //所有已经选中的图片
    private ImagePageAdapter adapter;
    private View topBar;
    private FrameLayout frameLay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview_del);

        mCurrentPosition = getIntent().getIntExtra(ImagePicker.EXTRA_SELECTED_IMAGE_POSITION, 0);
        selectedImages = (ArrayList<ImageItem>) getIntent().getSerializableExtra(ImagePicker.EXTRA_IMAGE_ITEMS);

        topBar = findViewById(R.id.top_bar);
        //因为状态栏透明后，布局整体会上移，所以给头部加上状态栏的margin值，保证头部不会被覆盖
        frameLay = (FrameLayout) findViewById(R.id.frameLay);
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) frameLay.getLayoutParams();
        params.topMargin = Utils.getStatusHeight(this);

        findViewById(R.id.btn_back).setOnClickListener(this);
        mTitleCount = (TextView) findViewById(R.id.tv_des);
        findViewById(R.id.btn_del).setOnClickListener(this);
        ViewPagerFixed viewPager = (ViewPagerFixed) findViewById(R.id.pager);
        adapter = new ImagePageAdapter(this, selectedImages);
        adapter.setPhotoViewClickListener(new ImagePageAdapter.PhotoViewClickListener() {
            @Override
            public void OnPhotoTapListener(View view, float v, float v1) {
                onImageSingleTap();
            }
        });
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(mCurrentPosition, false);

        ImageItem item = selectedImages.get(mCurrentPosition);
        mTitleCount.setText(getString(com.lzy.imagepicker.R.string.preview_image_count, mCurrentPosition + 1, selectedImages.size()));
        //滑动ViewPager的时候，根据外界的数据改变当前的图片的位置描述文本
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                mCurrentPosition = position;
//                ImageItem item = selectedImages.get(mCurrentPosition);
                mTitleCount.setText(getString(com.lzy.imagepicker.R.string.preview_image_count, mCurrentPosition + 1, selectedImages.size()));
            }
        });
        
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_del) {
            //是否删除此张图片
            new android.support.v7.app.AlertDialog.Builder(this)
                    .setTitle("提示")
                    .setMessage("要删除这张照片吗？")
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //移除当前图片刷新界面
                            selectedImages.remove(mCurrentPosition);
                            if(selectedImages.size() > 0){
                                adapter.notifyDataSetChanged();
                                //刷新显示当前位置
                                mTitleCount.setText(getString(com.lzy.imagepicker.R.string.preview_image_count, mCurrentPosition + 1, selectedImages.size()));
                            }
                            if(selectedImages.size() == 0){
                                onBackPressed();
                            }
                        }
                    }).show();
        } else if (id == R.id.btn_back) {
            onBackPressed();
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        //带回最新数据
        intent.putExtra(ImagePicker.EXTRA_IMAGE_ITEMS,selectedImages);
        setResult(ImagePicker.RESULT_CODE_BACK, intent);
        finish();
        super.onBackPressed();
    }


    /** 单击时，隐藏头部 */
    public void onImageSingleTap() {
        if (topBar.getVisibility() == View.VISIBLE) {
            topBar.setAnimation(AnimationUtils.loadAnimation(this, com.lzy.imagepicker.R.anim.top_out));
            topBar.setVisibility(View.GONE);
            tintManager.setStatusBarTintResource(com.lzy.imagepicker.R.color.transparent);//通知栏所需颜色
            //给最外层布局加上这个属性表示，Activity全屏显示，且状态栏被隐藏覆盖掉。
            if (Build.VERSION.SDK_INT >= 16)
                frameLay.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        } else {
            topBar.setAnimation(AnimationUtils.loadAnimation(this, com.lzy.imagepicker.R.anim.top_in));
            topBar.setVisibility(View.VISIBLE);
            tintManager.setStatusBarTintResource(com.lzy.imagepicker.R.color.status_bar);//通知栏所需颜色
            //Activity全屏显示，但状态栏不会被隐藏覆盖，状态栏依然可见，Activity顶端布局部分会被状态遮住
            if (Build.VERSION.SDK_INT >= 16)
                frameLay.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
    }
}
