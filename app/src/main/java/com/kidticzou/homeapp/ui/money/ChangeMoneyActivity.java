package com.kidticzou.homeapp.ui.money;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.os.EnvironmentCompat;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.kidticzou.homeapp.MainActivity;
import com.kidticzou.homeapp.R;
import com.kidticzou.homeapp.model.Base64Utils;
import com.kidticzou.homeapp.model.Bill;
import com.kidticzou.homeapp.model.NetMsg;
import com.kidticzou.homeapp.model.SaveBill;
import com.kidticzou.homeapp.model.Uri2PathUtil;
import com.kidticzou.homeapp.myapp;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import id.zelory.compressor.Compressor;
import id.zelory.compressor.constraint.Compression;
import top.zibin.luban.CompressionPredicate;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

public class ChangeMoneyActivity extends AppCompatActivity implements NetMsg.ServerReturn {
    private EditText mEditChangeMoney;
    private EditText mEidtPs;
    private Button mBtnDiv;
    private Button mBtnConmmit;
    private Button mBtnToCamera;
    private RadioGroup mChangRadio;
    private RadioButton mRbtn_f;//扣款
    private RadioButton mRbtn_p;//还款
    private ImageView mIvPhotoShow;

    private boolean mChangRadioF_bool;//是选的哪一个，实时更新。
    private NetMsg mNet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_money);
        mEditChangeMoney=findViewById(R.id.et_changmoney);
        mEidtPs=findViewById(R.id.et_ps);
        mBtnDiv=findViewById(R.id.btn_div2);
        mBtnConmmit=findViewById(R.id.btn_moneycommit);
        mChangRadio=findViewById(R.id.rg_1);
        mRbtn_f=findViewById(R.id.rb_f);
        mRbtn_p=findViewById(R.id.rb_p);
        mBtnToCamera=findViewById(R.id.cm_btn_toCamera);
        mIvPhotoShow=findViewById(R.id.cm_iv);

        myapp appdata= (myapp) getApplication();
        mNet=new NetMsg(ChangeMoneyActivity.this,appdata.url,2333,appdata.user,appdata.passwd);
        //查看默认扣款还是还款
        if(appdata.mChangMoneyF){
            mRbtn_f.setChecked(true);
            mEditChangeMoney.setTextColor(Color.rgb(0,255,0));
            mChangRadioF_bool=true;
        }
        else {
            mRbtn_p.setChecked(true);
            mEditChangeMoney.setTextColor(Color.rgb(255,0,0));
            mChangRadioF_bool=false;
        }


        //提交按键，开辟线程提交
        mBtnConmmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        float changemoney;String ps;
                        synchronized (this){
                            changemoney=Float.parseFloat(mEditChangeMoney.getText().toString());
                            if(mChangRadioF_bool){
                                changemoney=(-1)*changemoney;
                            }
                            ps=mEidtPs.getText().toString();
                        }

                        mNet.PayChange(changemoney,ps,false);
                    }
                }).start();
            }
        });

        //除以2
        mBtnDiv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                float changemoney=Float.parseFloat(mEditChangeMoney.getText().toString());
                changemoney=changemoney/2;
                mEditChangeMoney.setText(String.valueOf(changemoney));

            }
        });
        //相机跳转
        mBtnToCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermissionAndCamera();
            }
        });

        //扣还是加，监听事件
        mChangRadio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(i==R.id.rb_f){
                    mEditChangeMoney.setTextColor(Color.rgb(0,255,0));
                    mChangRadioF_bool=true;

                }
                else if (i==R.id.rb_p){
                    mEditChangeMoney.setTextColor(Color.rgb(255,0,0));
                    mChangRadioF_bool=false;
                }
            }
        });
    }


    @Override
    public void errorLog(final String errString) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(ChangeMoneyActivity.this,errString,Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void home() {
        setResult(RESULT_OK);
        finish();
    }

    /***-------------------相机相关------------------***/
    // 拍照的requestCode
    private static final int CAMERA_REQUEST_CODE = 0x00000010;
    // 申请相机权限的requestCode
    private static final int PERMISSION_CAMERA_REQUEST_CODE = 0x00000012;
    /**
     * 用于保存拍照图片的uri
     */
    private Uri mCameraUri;

    /**
     * 用于保存图片的文件路径，Android 10以下使用图片路径访问图片
     */
    private String mCameraImagePath;
    /**
     * 用于打开压缩后的图片文件
     */
    private File mCameraImgFile;

    /**
     *  是否是Android 10以上手机
     */
    private boolean isAndroidQ = Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q;
    /**
     * 检查权限并拍照。
     * 调用相机前先检查权限。
     */
    private void checkPermissionAndCamera() {
        int hasCameraPermission = ContextCompat.checkSelfPermission(getApplication(),
                Manifest.permission.CAMERA);
        if (hasCameraPermission == PackageManager.PERMISSION_GRANTED) {
            //有权限，调起相机拍照。
            openCamera();
        } else {
            //没有权限，申请权限。
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},
                    PERMISSION_CAMERA_REQUEST_CODE);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                if (isAndroidQ) {
                    // Android 10 使用图片uri加载
                    mCameraImagePath= Uri2PathUtil.getRealPathFromUri(this, mCameraUri);
                    final File imgfile=new File(mCameraImagePath);

                    //申请读取权限并压缩
                    if (Build.VERSION.SDK_INT >= 23) {
                        int REQUEST_CODE_CONTACT = 101;
                        String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        //验证是否许可权限
                        for (String str : permissions) {
                            if (this.checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
                                //申请权限
                                this.requestPermissions(permissions, REQUEST_CODE_CONTACT);
                                return;
                            } else {
                                //这里就是权限打开之后自己要操作的逻辑
                                //压缩图片
                                Luban.with(this).load(mCameraImagePath).ignoreBy(100)
                                        .setTargetDir(imgfile.getParent())
                                        .filter(new CompressionPredicate() {
                                            @Override
                                            public boolean apply(String path) {
                                                return !(TextUtils.isEmpty(path) || path.toLowerCase().endsWith(".gif"));
                                            }
                                        })
                                        .setCompressListener(new OnCompressListener() {
                                            @Override
                                            public void onStart() {

                                            }

                                            @Override
                                            public void onSuccess(File file) {
                                                Toast.makeText(ChangeMoneyActivity.this,"压缩后大小:"+String.valueOf(file.length()/1024)+"k",Toast.LENGTH_LONG).show();
                                                //删除原图
                                                imgfile.delete();
                                                mCameraImagePath=null;
                                                mCameraImgFile=file;
                                                mCameraUri=Uri.fromFile(file);
                                                mCameraImagePath=file.getPath();
                                            }

                                            @Override
                                            public void onError(Throwable e) {
                                                Toast.makeText(ChangeMoneyActivity.this,e.toString(),Toast.LENGTH_LONG).show();
                                            }
                                        }).launch();
                                //String base64img=Base64Utils.GetImageStr(imgPath);
                                //System.out.println(base64img);
                            }
                        }
                    }

                    //显示压缩后图片
                    mIvPhotoShow.setImageURI(mCameraUri);

                    //尝试base64
                    String imgbase64=Base64Utils.GetImageStr(mCameraImagePath);
                    System.out.println(imgbase64);
                } else {
                    // 使用图片路径加载
                    mIvPhotoShow.setImageBitmap(BitmapFactory.decodeFile(mCameraImagePath));
                }
            } else {
                Toast.makeText(this,"取消",Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * 处理权限申请的回调。
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSION_CAMERA_REQUEST_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //允许权限，有调起相机拍照。
                openCamera();
            } else {
                //拒绝权限，弹出提示框。
                Toast.makeText(this,"拍照权限被拒绝",Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * 调起相机拍照
     */
    private void openCamera() {
        Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // 判断是否有相机
        if (captureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            Uri photoUri = null;

            if (isAndroidQ) {
                // 适配android 10
                photoUri = createImageUri();
            } else {
                try {
                    photoFile = createImageFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (photoFile != null) {
                    mCameraImagePath = photoFile.getAbsolutePath();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        //适配Android 7.0文件权限，通过FileProvider创建一个content类型的Uri
                        photoUri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", photoFile);
                    } else {
                        photoUri = Uri.fromFile(photoFile);
                    }
                }
            }

            mCameraUri = photoUri;
            if (photoUri != null) {
                captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                captureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                startActivityForResult(captureIntent, CAMERA_REQUEST_CODE);
            }
        }
    }

    /**
     * 创建图片地址uri,用于保存拍照后的照片 Android 10以后使用这种方法
     *
     * @return 图片的uri
     */
    private Uri createImageUri() {
        String status = Environment.getExternalStorageState();
        // 判断是否有SD卡,优先使用SD卡存储,当没有SD卡时使用手机存储
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            return getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new ContentValues());
        } else {
            return getContentResolver().insert(MediaStore.Images.Media.INTERNAL_CONTENT_URI, new ContentValues());
        }
    }

    /**
     * 创建保存图片的文件
     * @return
     * @throws IOException
     */
    private File createImageFile() throws IOException {
        String imageName = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (!storageDir.exists()) {
            storageDir.mkdir();
        }
        File tempFile = new File(storageDir, imageName);
        if (!Environment.MEDIA_MOUNTED.equals(EnvironmentCompat.getStorageState(tempFile))) {
            return null;
        }
        return tempFile;
    }




    //键盘隐藏
    @Override
    public boolean dispatchTouchEvent(MotionEvent  ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View view = getCurrentFocus();
            if (isShouldHideInput(view, ev)) {
                InputMethodManager Object = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (Object != null) {
                    Object.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        }

        return super.dispatchTouchEvent(ev);
    }

    //判断是否隐藏键盘
    public boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = {0, 0};
            //获取输入框当前的location位置
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
            // 点击的是输入框区域，保留点击EditText的事件
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

}