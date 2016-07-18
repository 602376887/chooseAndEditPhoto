package dd.com.chooseandeditphoto;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    private File myAvatarFile;
    private int crop = 720;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myAvatarFile = new File("/mnt/sdcard/ddpicdemo/");
        if(!myAvatarFile.exists()){
            if(myAvatarFile.mkdirs()){
                Log.d("MainActivity", "mkdirs ok");
            }else {
                Log.d("MainActivity","mkdirs failed");
            }
        }
        myAvatarFile = new File("/mnt/sdcard/ddpicdemo/", "demo.jpg");
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // 如果是直接从相册获取
            case 1:
                if (data != null) {
                    startPhotoZoom(data.getData());
                }
                break;
            // 如果是调用相机拍照时
            case 2:
                if (data == null) {
                    System.out.println("图片");
                    startPhotoZoom(Uri.fromFile(myAvatarFile));
                }
                break;
            // 取得裁剪后的图片
            case 3:
                /**
                 * 非空判断大家一定要验证，如果不验证的话， 在剪裁之后如果发现不满意，要重新裁剪，丢弃
                 * 当前功能时，会报NullException，小马只 在这个地方加下，大家可以根据不同情况在合适的 地方做判断处理类似情况
                 *
                 */
                if (data != null) {
                    BitmapFactory.Options opts = new BitmapFactory.Options();
                    opts.inSampleSize = 2;
                    Bitmap bmp = BitmapFactory.decodeFile(
                            myAvatarFile.getAbsolutePath(), opts);
                    ((ImageView)findViewById(R.id.iv_pic)).setImageBitmap(bmp);

                }
                break;
            default:
                break;

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 裁剪图片方法实现
     *
     * @param uri
     */
    public void startPhotoZoom(Uri uri) {
		/*
		 * 至于下面这个Intent的ACTION是怎么知道的，大家可以看下自己路径下的如下网页
		 * yourself_sdk_path/docs/reference/android/content/Intent.html
		 * 直接在里面Ctrl+F搜：CROP ，之前小马没仔细看过，其实安卓系统早已经有自带图片裁剪功能, 是直接调本地库的，小马不懂C C++
		 * 这个不做详细了解去了，有轮子就用轮子，不再研究轮子是怎么 制做的了...吼吼
		 */
        Intent intent_crop = new Intent("com.android.camera.action.CROP");
        intent_crop.setDataAndType(uri, "image/*");
        // 下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent_crop.putExtra("output", Uri.fromFile(myAvatarFile));
        intent_crop.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent_crop.putExtra("aspectX", 1);
        intent_crop.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent_crop.putExtra("outputX", crop);
        intent_crop.putExtra("outputY", crop);
        startActivityForResult(intent_crop, 3);
    }

    public void choosePic(View view){
        new AlertDialog.Builder(this).setItems(
                new String[] { "相机", "相册" },
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which) {

                        if (which == 0) {
                            Intent intent_camera = new Intent(
                                    MediaStore.ACTION_IMAGE_CAPTURE);
                            // 下面这句指定调用相机拍照后的照片存储的路径
                            intent_camera.putExtra(
                                    MediaStore.EXTRA_OUTPUT,
                                    Uri.fromFile(myAvatarFile));
                            startActivityForResult(intent_camera, 2);

                        } else {
                            Intent intent_media = new Intent(
                                    Intent.ACTION_PICK, null);

                            intent_media
                                    .setDataAndType(
                                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                            "image/*");

                            startActivityForResult(intent_media, 1);
                        }

                    }

                }).create().show();
    }
}
