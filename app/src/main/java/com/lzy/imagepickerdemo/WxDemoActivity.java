package com.lzy.imagepickerdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.ikkong.wximagepicker.Constants;
import com.ikkong.wximagepicker.ImagePickerAdapter;
import com.ikkong.wximagepicker.ImagePreviewDelActivity;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.loader.GlideImageLoader;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.imagepicker.view.CropImageView;

import java.util.ArrayList;

public class WxDemoActivity extends AppCompatActivity implements
        ImagePickerAdapter.OnRecyclerViewItemClickListener{

    private RecyclerView recylerView;
    private ImagePickerAdapter adapter;
    private ArrayList<ImageItem> selImageList;//当前选择的所有图片
    private int maxImgCount = 8;//允许选择图片最大数

    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wxdemo);
        initImagePicker();//最好放到 Application oncreate执行
        initWidget();
    }

    private void initWidget() {
        recylerView = (RecyclerView) findViewById(R.id.recylerView);
        selImageList = new ArrayList<>();
        adapter = new ImagePickerAdapter(this,selImageList,maxImgCount);
        adapter.refresh();
        GridLayoutManager manager = new GridLayoutManager(this,4);
        recylerView.setLayoutManager(manager);
        recylerView.setHasFixedSize(true);
        recylerView.setAdapter(adapter);
        adapter.setOnItemClickListener(this);
    }

    private void initImagePicker() {
        ImagePicker imagePicker = ImagePicker.getInstance();
        imagePicker.setImageLoader(new GlideImageLoader());   //设置图片加载器
        imagePicker.setShowCamera(true);  //显示拍照按钮
        imagePicker.setCrop(false);        //允许裁剪（单选才有效）
        imagePicker.setSaveRectangle(true); //是否按矩形区域保存
        imagePicker.setSelectLimit(maxImgCount);    //选中数量限制
        imagePicker.setStyle(CropImageView.Style.RECTANGLE);  //裁剪框的形状
        imagePicker.setFocusWidth(800);   //裁剪框的宽度。单位像素（圆形自动取宽高最小值）
        imagePicker.setFocusHeight(800);  //裁剪框的高度。单位像素（圆形自动取宽高最小值）
        imagePicker.setOutPutX(1000);//保存文件的宽度。单位像素
        imagePicker.setOutPutY(1000);//保存文件的高度。单位像素
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            if (data != null && requestCode == Constants.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
                ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                selImageList.addAll(selImageList.size()-1,images);
                adapter.refresh();
            }
        }else if(resultCode == ImagePicker.RESULT_CODE_BACK){
            if (data != null&& requestCode == Constants.IMAGE_PREVIEW_ACTIVITY_REQUEST_CODE) {
                ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_IMAGE_ITEMS);
                selImageList.clear();
                selImageList.addAll(images);
                adapter.refresh();
            }
        }
    }

    @Override
    public void onItemClick(View view, String data) {
        switch(data){
            case Constants.IMAGEITEM_DEFAULT_ADD:
                //打开选择
                //本次允许选择的数量
                ImagePicker.getInstance().setSelectLimit(maxImgCount - selImageList.size() + 1);
                //打开选择器
                Intent intent = new Intent(this, ImageGridActivity.class);
                startActivityForResult(intent, Constants.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
                break;
            default:
                //打开预览
                Intent intent1 = new Intent(this, ImagePreviewDelActivity.class);
                intent1.putExtra(ImagePicker.EXTRA_IMAGE_ITEMS, adapter.getRealSelImage());
                intent1.putExtra(ImagePicker.EXTRA_SELECTED_IMAGE_POSITION,Integer.parseInt(data));
                startActivityForResult(intent1, Constants.IMAGE_PREVIEW_ACTIVITY_REQUEST_CODE);
                break;
        }
    }
}
