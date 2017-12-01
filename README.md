# ImagePicker
Android自定义相册，完全仿微信UI，实现了拍照、图片选择（单选/多选）、 裁剪 、旋转、等功能。

### 由于个人时间有限，该项目停止维护
如果你发现有bug，或者好的建议，可以提merge request，我测试通过后会立即合并并发布新版本，确保该库处于可用的状态。

该项目参考了：

* [https://github.com/pengjianbo/GalleryFinal](https://github.com/pengjianbo/GalleryFinal) 
* [https://github.com/easonline/AndroidImagePicker](https://github.com/easonline/AndroidImagePicker)

喜欢原作的可以去使用。同时欢迎大家下载体验本项目，如果使用过程中遇到什么问题，欢迎反馈。

## 联系方式
 * email： liaojeason@126.com
 * QQ群： 489873144 <a target="_blank" href="//shang.qq.com/wpa/qunwpa?idkey=ba5dbb5115a165866ec77d96cb46685d1ad159ab765b796699d6763011ffe151"><img border="0" src="http://pub.idqqimg.com/wpa/images/group.png" alt="Android 格调小窝" title="Android 格调小窝"></a>（点击图标，可以直接加入，建议使用QQ群，邮箱使用较少，可能看的不及时）
 * 如果遇到问题欢迎在群里提问，个人能力也有限，希望一起学习一起进步。
 
## 演示
 ![image](https://github.com/jeasonlzy/Screenshots/blob/master/ImagePicker/demo1.png)![image](https://github.com/jeasonlzy/Screenshots/blob/master/ImagePicker/demo2.gif)
 ![image](https://github.com/jeasonlzy/Screenshots/blob/master/ImagePicker/demo3.gif)![image](https://github.com/jeasonlzy/Screenshots/blob/master/ImagePicker/demo5.gif)

## 1.用法

使用前，对于Android Studio的用户，可以选择添加:
```java
	compile 'com.lzy.widget:imagepicker:0.6.1'  //指定版本
```

## 2.功能和参数含义

### 温馨提示:目前库中的预览界面有个原图的复选框,暂时只做了UI,还没有做压缩的逻辑

|配置参数|参数含义|
|:--:|--|
|multiMode|图片选着模式，单选/多选|
|selectLimit|多选限制数量，默认为9|
|showCamera|选择照片时是否显示拍照按钮|
|crop|是否允许裁剪（单选有效）|
|style|有裁剪时，裁剪框是矩形还是圆形|
|focusWidth|矩形裁剪框宽度（圆形自动取宽高最小值）|
|focusHeight|矩形裁剪框高度（圆形自动取宽高最小值）|
|outPutX|裁剪后需要保存的图片宽度|
|outPutY|裁剪后需要保存的图片高度|
|isSaveRectangle|裁剪后的图片是按矩形区域保存还是裁剪框的形状，例如圆形裁剪的时候，该参数给true，那么保存的图片是矩形区域，如果该参数给fale，保存的图片是圆形区域|
|imageLoader|需要使用的图片加载器，自需要实现ImageLoader接口即可|

## 3.代码参考

更多使用，请下载demo参看源代码

1. 首先你需要继承 `com.lzy.imagepicker.loader.ImageLoader` 这个接口,实现其中的方法,比如以下代码是使用 `Picasso` 三方加载库实现的
```java
public class PicassoImageLoader implements ImageLoader {

    @Override
    public void displayImage(Activity activity, String path, ImageView imageView, int width, int height) {
        Picasso.with(activity)//
                   .load(Uri.fromFile(new File(path)))//
                .placeholder(R.mipmap.default_image)//
                .error(R.mipmap.default_image)//
                .resize(width, height)//
                .centerInside()//
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)//
                .into(imageView);
    }

    @Override
    public void clearMemoryCache() {
        //这里是清除缓存的方法,根据需要自己实现
    }
}
```

2. 然后配置图片选择器，一般在Application初始化配置一次就可以,这里就需要将上面的图片加载器设置进来,其余的配置根据需要设置
```java
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_image_picker);
    
    ImagePicker imagePicker = ImagePicker.getInstance();
    imagePicker.setImageLoader(new PicassoImageLoader());   //设置图片加载器
    imagePicker.setShowCamera(true);  //显示拍照按钮
    imagePicker.setCrop(true);        //允许裁剪（单选才有效）
    imagePicker.setSaveRectangle(true); //是否按矩形区域保存
    imagePicker.setSelectLimit(9);    //选中数量限制
    imagePicker.setStyle(CropImageView.Style.RECTANGLE);  //裁剪框的形状
    imagePicker.setFocusWidth(800);   //裁剪框的宽度。单位像素（圆形自动取宽高最小值）
    imagePicker.setFocusHeight(800);  //裁剪框的高度。单位像素（圆形自动取宽高最小值）
    imagePicker.setOutPutX(1000);//保存文件的宽度。单位像素
    imagePicker.setOutPutY(1000);//保存文件的高度。单位像素
}
```

3. 以上配置完成后，在适当的方法中开启相册，例如点击按钮时
```java
public void onClick(View v) {
        Intent intent = new Intent(this, ImageGridActivity.class);
        startActivityForResult(intent, IMAGE_PICKER);  
    }
}
```

4. 如果你想直接调用相机
```java
Intent intent = new Intent(this, ImageGridActivity.class);
intent.putExtra(ImageGridActivity.EXTRAS_TAKE_PICKERS,true); // 是否是直接打开相机
      startActivityForResult(intent, REQUEST_CODE_SELECT);
```

5. 重写`onActivityResult`方法,回调结果
```java
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
        if (data != null && requestCode == IMAGE_PICKER) {
            ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
            MyAdapter adapter = new MyAdapter(images);
            gridView.setAdapter(adapter);
        } else {
            Toast.makeText(this, "没有数据", Toast.LENGTH_SHORT).show();
        }
    }
}
```

## 更新日志

V 0.6.1
 * [合并] [优化图片选择页UI， 适配预览页的横竖屏切换 #195](https://github.com/jeasonlzy/ImagePicker/pull/195)

V 0.6.0
 * [合并] [调整UI,真正的完全仿微信](https://github.com/jeasonlzy/ImagePicker/pull/193)
 * [合并] [fix(location): 解决不合法图片导致的Bug](https://github.com/jeasonlzy/ImagePicker/pull/188)

V 0.5.5
 * [修复]选择图页面进入预览取消选择或者选择后返回列表不更新的问题；
 * [修复]6.0动态权限可能导致崩溃的bug；

V 0.5.4
 * [修复]部分内存泄漏问题；
 * [修复]新增显示已选中图片的调用方法：详情请查看demo首页的ImagePickerActivity；如果不需要此功能，则WxDemoActivity可能是你想要的。

V 0.5.3
 * [修复]矫正图片旋转导致的oom；
 * [修复]部分手机TitleBar和状态栏重复的问题；

V 0.5.1
 * [更正] 由于原图功能其实还没有做，所以本版本隐去了原图的显示。以免用户误解原图问题。
 * [修复] 使用RecyclerView替换GridView解决改变选中状态全局刷新的问题；
 * [提示] 虽然本次解决了全局刷新，但是如果使用的是Picasso依然会出现重新加载一张图片的问题，这是Picasso自己的问题，建议使用Glide框架。
 
V 0.5.0
 * [修复] 解决provider冲突问题； 

V 0.4.8
 * [修复] 解决demo中直接呼起相机并裁剪不会返回数据的bug，不需要这个功能的可以不更新;
 
V 0.4.7
 * [新增] 新增可直接调起相机的功能;
 * [修复] 解决可能和主项目provider冲突的潜在问题；
 * [修复] 点击图片预览空指针崩溃问题；
 * [修复] 使用Intent传值限制导致的崩溃问题;
 * [修复] 部分机型拍照后图片旋转问题；
 * [修复] 更改选择框图片背景为灰色，以免白色图看不清。
 
V 0.3.5
 * [新增] 提供直接调起相机的方式，并可直接设置牌照是否裁剪；
 * [修复] Android7.0设备调系统相机直接崩溃的问题；
 * [注意] 如果出现 java.lang.RuntimeException: Unable to get provider android.support.v4.content.FileProvider: java.lang.SecurityException: Provider must not be exported，请直接clean再运行即可。

## Licenses
```
 Copyright 2016 jeasonlzy(廖子尧)

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
```

