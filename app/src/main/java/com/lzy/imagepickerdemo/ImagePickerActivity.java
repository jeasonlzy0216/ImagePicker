package com.lzy.imagepickerdemo;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.loader.GlideImageLoader;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.imagepicker.view.CropImageView;

import java.util.ArrayList;
import java.util.List;

public class ImagePickerActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener, CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    private ImagePicker imagePicker;

    private RadioButton rb_uil;
    private RadioButton rb_glide;
    private RadioButton rb_picasso;
    private RadioButton rb_fresco;
    private RadioButton rb_xutils3;
    private RadioButton rb_xutils;
    private RadioButton rb_single_select;
    private RadioButton rb_muti_select;
    private RadioButton rb_crop_square;
    private RadioButton rb_crop_circle;
    private TextView tv_select_limit;
    private GridView gridView;
    private EditText et_crop_width;
    private EditText et_crop_height;
    private EditText et_crop_radius;
    private EditText et_outputx;
    private EditText et_outputy;
    
    private Button btn_wxDemo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_picker);

        imagePicker = ImagePicker.getInstance();
        imagePicker.setImageLoader(new GlideImageLoader());

        rb_uil = (RadioButton) findViewById(R.id.rb_uil);
        rb_glide = (RadioButton) findViewById(R.id.rb_glide);
        rb_picasso = (RadioButton) findViewById(R.id.rb_picasso);
        rb_fresco = (RadioButton) findViewById(R.id.rb_fresco);
        rb_xutils3 = (RadioButton) findViewById(R.id.rb_xutils3);
        rb_xutils = (RadioButton) findViewById(R.id.rb_xutils);
        rb_single_select = (RadioButton) findViewById(R.id.rb_single_select);
        rb_muti_select = (RadioButton) findViewById(R.id.rb_muti_select);
        rb_crop_square = (RadioButton) findViewById(R.id.rb_crop_square);
        rb_crop_circle = (RadioButton) findViewById(R.id.rb_crop_circle);
        rb_glide.setChecked(true);
        rb_muti_select.setChecked(true);
        rb_crop_square.setChecked(true);

        et_crop_width = (EditText) findViewById(R.id.et_crop_width);
        et_crop_width.setText("280");
        et_crop_height = (EditText) findViewById(R.id.et_crop_height);
        et_crop_height.setText("280");
        et_crop_radius = (EditText) findViewById(R.id.et_crop_radius);
        et_crop_radius.setText("140");
        et_outputx = (EditText) findViewById(R.id.et_outputx);
        et_outputx.setText("800");
        et_outputy = (EditText) findViewById(R.id.et_outputy);
        et_outputy.setText("800");

        tv_select_limit = (TextView) findViewById(R.id.tv_select_limit);
        SeekBar sb_select_limit = (SeekBar) findViewById(R.id.sb_select_limit);
        sb_select_limit.setMax(15);
        sb_select_limit.setOnSeekBarChangeListener(this);
        sb_select_limit.setProgress(9);

        CheckBox cb_show_camera = (CheckBox) findViewById(R.id.cb_show_camera);
        cb_show_camera.setOnCheckedChangeListener(this);
        cb_show_camera.setChecked(true);
        CheckBox cb_crop = (CheckBox) findViewById(R.id.cb_crop);
        cb_crop.setOnCheckedChangeListener(this);
        cb_crop.setChecked(true);
        CheckBox cb_isSaveRectangle = (CheckBox) findViewById(R.id.cb_isSaveRectangle);
        cb_isSaveRectangle.setOnCheckedChangeListener(this);
        cb_isSaveRectangle.setChecked(true);

        Button btn_open_gallery = (Button) findViewById(R.id.btn_open_gallery);
        btn_open_gallery.setOnClickListener(this);

        gridView = (GridView) findViewById(R.id.gridview);

        btn_wxDemo = (Button) findViewById(R.id.btn_wxDemo);
        btn_wxDemo.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_open_gallery:
                if (rb_uil.isChecked()) imagePicker.setImageLoader(new UILImageLoader());
                else if (rb_glide.isChecked()) imagePicker.setImageLoader(new GlideImageLoader());
                else if (rb_picasso.isChecked())
                    imagePicker.setImageLoader(new PicassoImageLoader());
                else if (rb_fresco.isChecked()) imagePicker.setImageLoader(new GlideImageLoader());
                else if (rb_xutils3.isChecked())
                    imagePicker.setImageLoader(new XUtils3ImageLoader());
                else if (rb_xutils.isChecked()) imagePicker.setImageLoader(new GlideImageLoader());

                if (rb_single_select.isChecked()) imagePicker.setMultiMode(false);
                else if (rb_muti_select.isChecked()) imagePicker.setMultiMode(true);

                if (rb_crop_square.isChecked()) {
                    imagePicker.setStyle(CropImageView.Style.RECTANGLE);
                    Integer width = Integer.valueOf(et_crop_width.getText().toString());
                    Integer height = Integer.valueOf(et_crop_height.getText().toString());
                    width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, width, getResources().getDisplayMetrics());
                    height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, height, getResources().getDisplayMetrics());
                    imagePicker.setFocusWidth(width);
                    imagePicker.setFocusHeight(height);
                } else if (rb_crop_circle.isChecked()) {
                    imagePicker.setStyle(CropImageView.Style.CIRCLE);
                    Integer radius = Integer.valueOf(et_crop_radius.getText().toString());
                    radius = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, radius, getResources().getDisplayMetrics());
                    imagePicker.setFocusWidth(radius * 2);
                    imagePicker.setFocusHeight(radius * 2);
                }

                imagePicker.setOutPutX(Integer.valueOf(et_outputx.getText().toString()));
                imagePicker.setOutPutY(Integer.valueOf(et_outputy.getText().toString()));

                Intent intent = new Intent(this, ImageGridActivity.class);
                startActivityForResult(intent, 100);
                break;
            case R.id.btn_wxDemo:
                startActivity(new Intent(this,WxDemoActivity.class));
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.cb_show_camera:
                imagePicker.setShowCamera(isChecked);
                break;
            case R.id.cb_crop:
                imagePicker.setCrop(isChecked);
                break;
            case R.id.cb_isSaveRectangle:
                imagePicker.setSaveRectangle(isChecked);
                break;
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        tv_select_limit.setText(String.valueOf(progress));
        imagePicker.setSelectLimit(progress);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            if (data != null && requestCode == 100) {
                ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                MyAdapter adapter = new MyAdapter(images);
                gridView.setAdapter(adapter);
            } else {
                Toast.makeText(this, "没有数据", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class MyAdapter extends BaseAdapter {

        private List<ImageItem> items;

        public MyAdapter(List<ImageItem> items) {
            this.items = items;
        }

        public void setData(List<ImageItem> items) {
            this.items = items;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public ImageItem getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            int size = gridView.getWidth() / 3;
            if (convertView == null) {
                imageView = new ImageView(ImagePickerActivity.this);
                AbsListView.LayoutParams params = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, size);
                imageView.setLayoutParams(params);
                imageView.setBackgroundColor(Color.parseColor("#88888888"));
            } else {
                imageView = (ImageView) convertView;
            }
            imagePicker.getImageLoader().displayImage(ImagePickerActivity.this, getItem(position).path, imageView, size, size);
            return imageView;
        }
    }
}
