package com.example.jdk.restapp.Activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.jdk.restapp.R;
import com.example.jdk.restapp.Utils.FileUtils;
import com.example.jdk.restapp.Utils.ShareUtils;
import com.example.jdk.restapp.Utils.SnackBarUtils;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by JDK on 2016/8/11.
 */
public class ShowBigBitmapActivity extends BaseActivity {
    @Bind(R.id.big_Image)
    ImageView big_ImageView;
    @Bind(R.id.activity_showbigbitmap)
    LinearLayout linearLayout;
    @Bind(R.id.webview_toolbar)
    Toolbar big_picture_toolbar;
    @Bind(R.id.desc_tv_showwebview)
    TextView desc_tv_big_picture;
    private Intent intent;
    Bitmap mBitmap;
    String url;
    String desc;
    String publishedAt;
    PhotoViewAttacher photoViewAttacher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_big_bitmap);
        intent = getIntent();
        url = intent.getStringExtra("URL");
        desc = intent.getStringExtra("DESC");
        publishedAt = intent.getStringExtra("PUBLISHEDAT");
        ButterKnife.bind(this);
        InitToolbar();
        photoViewAttacher = new PhotoViewAttacher(big_ImageView);
        showBigBitmap(url);
        initListener();
    }

    public void initListener() {
        photoViewAttacher.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onViewTap(View view, float x, float y) {
                finishAfterTransition();
            }
        });
    }

    public void showBigBitmap(final String url) {
        Picasso.with(this).load(url).resize(1000, 1000).centerCrop().into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                big_ImageView.setImageBitmap(bitmap);
                photoViewAttacher.update();
                mBitmap = bitmap;
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        while (mBitmap == null) {
            showBigBitmap(url);
        }
    }

    public void InitToolbar() {
        desc_tv_big_picture.setVisibility(View.GONE);
        big_picture_toolbar.setTitle(desc);
        setSupportActionBar(big_picture_toolbar);
        big_picture_toolbar.setNavigationIcon(R.drawable.ic_back_24dp);
        big_picture_toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();//FragmentActivity中的方法  直接用。
            }
        });
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.big_picture_toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int id = item.getItemId();
        if (id == R.id.action_share) {
            Observable.create(new Observable.OnSubscribe<Uri>() {
                @Override
                public void call(Subscriber<? super Uri> subscriber) {
                    Uri uri = FileUtils.SaveImageToSDCard(mBitmap, publishedAt);
                    if (uri == null) {
                        SnackBarUtils.makeShort(getWindow().getDecorView(), getResources().getString(R.string.share_failed)).danger();
                    } else {
                        subscriber.onNext(uri);
                        subscriber.onCompleted();
                    }

                }
            }).subscribeOn(AndroidSchedulers.mainThread())
                    .observeOn(Schedulers.io())
                    .subscribe(new Action1<Uri>() {
                        @Override
                        public void call(Uri uri) {
                            ShareUtils.getInstance(ShowBigBitmapActivity.this).shareImage(uri);
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {

                        }
                    });

        } else if (id == R.id.action_save) {
            Observable.create(new Observable.OnSubscribe<Uri>() {
                @Override
                public void call(Subscriber<? super Uri> subscriber) {
                    Uri uri = FileUtils.SaveImageToSDCard(mBitmap, publishedAt);
                    if (uri == null) {
                        SnackBarUtils.makeShort(getWindow().getDecorView(), getResources().getString(R.string.save_meizi_failed)).danger();
                    } else {
                        subscriber.onNext(uri);
                        subscriber.onCompleted();
                    }
                }
            }).subscribeOn(Schedulers.io())//Observable.create中的操作是在io线程中实现的。
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<Uri>() {//subscribe中的操作是在主线程中实现的。
                        @Override
                        public void call(Uri uri) {
                            Intent scannerIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri);
                            ShowBigBitmapActivity.this.sendBroadcast(scannerIntent);
                            SnackBarUtils.makeShort(getWindow().getDecorView(), getResources().getString(R.string.save_meizi_successful)).danger();
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            SnackBarUtils.makeShort(getWindow().getDecorView(), getResources().getString(R.string.save_meizi_failed)).danger();
                        }
                    });
        }
        return super.onOptionsItemSelected(item);
    }
}
