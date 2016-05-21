package com.ikkong.wximagepicker;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.R;
import com.lzy.imagepicker.bean.ImageItem;

import java.util.ArrayList;

/**
 * Author:  ikkong
 * Email:   ikkong@163.com
 * Date:    2016/5/19
 * Description:
 */
public class ImagePickerAdapter extends RecyclerView.Adapter<SelectedPicViewHolder> implements
        View.OnClickListener {
    private int maxImgCount;
    private Context mContext;
    private ArrayList<ImageItem> list;
    private LayoutInflater mInflater;
    private OnRecyclerViewItemClickListener listener;

    public void setList(ArrayList<ImageItem> list) {
        this.list = list;
    }

    public ImagePickerAdapter(Context mContext, ArrayList<ImageItem> list, int maxImgCount) {
        this.mContext = mContext;
        this.list = list;
        this.maxImgCount = maxImgCount;
        this.mInflater = LayoutInflater.from(mContext);
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View view) {
        if (listener != null) {
            //注意这里使用getTag方法获取数据
            listener.onItemClick(view,(String)view.getTag());
        }
    }

    public static interface OnRecyclerViewItemClickListener{
        void onItemClick(View view, String data);
    }

    @Override
    public SelectedPicViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.list_item_image,parent,false);
        SelectedPicViewHolder holder = new SelectedPicViewHolder(view);
        view.setOnClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(SelectedPicViewHolder holder, int position) {
        if(!list.get(position).path.equals(Constants.IMAGEITEM_DEFAULT_ADD)) {
            ImagePicker.getInstance().getImageLoader().displayImage((Activity) mContext,
                    list.get(position).path, holder.iv_img, 0, 0);
            //将数据保存在itemView的Tag中，以便点击时进行获取
            holder.itemView.setTag(position+"");
        }else{
            holder.iv_img.setImageResource(R.drawable.selector_image_add);
            //将数据保存在itemView的Tag中，以便点击时进行获取
            holder.itemView.setTag(Constants.IMAGEITEM_DEFAULT_ADD);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void refresh(){
        if(getItemCount() > maxImgCount
                && list.get(getItemCount()-1).path.equals(Constants.IMAGEITEM_DEFAULT_ADD)){
            //del last
            list.remove(getItemCount()-1);
        }
        if(getItemCount() < maxImgCount){
            if(getItemCount() == 0 ||
                    !list.get(getItemCount()-1).path.equals(Constants.IMAGEITEM_DEFAULT_ADD)){
                ImageItem imageItem = new ImageItem();
                imageItem.path = Constants.IMAGEITEM_DEFAULT_ADD;
                list.add(imageItem);
            }
        }
        notifyDataSetChanged();
    }
    
    public ArrayList<ImageItem> getRealSelImage(){
        //由于图片未选满时，最后一张显示添加图片，因此这个方法返回真正的已选图片
        if(list.get(list.size()-1).path.equals(Constants.IMAGEITEM_DEFAULT_ADD)){
            ArrayList<ImageItem> arrayList = new ArrayList<>();
            for(int i = 0; i<list.size()-1;i++){
                arrayList.add(list.get(i));
            }
            return arrayList;
        }
        return list;
    }
}

class SelectedPicViewHolder extends RecyclerView.ViewHolder{
    ImageView iv_img;

    public SelectedPicViewHolder(View itemView) {
        super(itemView);
        iv_img = (ImageView) itemView.findViewById(R.id.iv_img);
    }
}
