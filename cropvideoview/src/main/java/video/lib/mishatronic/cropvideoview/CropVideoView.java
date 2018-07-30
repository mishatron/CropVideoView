package video.lib.mishatronic.cropvideoview;

/**
 * Created by mishatron on 18.04.2018.
 */

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RawRes;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.FrameLayout;

import java.io.IOException;


public class CropVideoView extends FrameLayout implements TextureView.SurfaceTextureListener{

    // Indicate if logging is on
    private boolean LOG_ON = true;

    // Log tag
    private static final String TAG = CropVideoView.class.getName();

    private MediaPlayer mMediaPlayer;

    private float mVideoHeight;
    private float mVideoWidth;

    private boolean mIsDataSourceSet;
    public boolean mIsViewAvailable;
    public boolean mIsVideoPrepared;
    public boolean mIsPlayCalled;

    private ScaleType mScaleType;
    private State mState;
    private TextureView mTextureView;
    private FrameLayout mBackground;

    public MediaPlayerListener mListener;


    public enum State {
        UNINITIALIZED, PLAY, STOP, PAUSE, END
    }
    /**
     * constructor
     */
    public CropVideoView(Context context) {
        super(context);
        if (!isInEditMode()) {
            initView();
        }
    }

    /**
     * constructor
     */
    public CropVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            initView();
        }
    }

    /**
     * constructor
     */
    public CropVideoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if (!isInEditMode()) {
            initView();
        }
        if (attrs == null) {
            return;
        }

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.scaleStyle, 0, 0);
        if (a == null) {
            return;
        }

        int scaleType = a.getInt(R.styleable.scaleStyle_scaleType, ScaleType.NONE.ordinal());
        a.recycle();
        mScaleType = ScaleType.values()[scaleType];
    }

    private void initView() {
        initPlayer();
        mTextureView = new TextureView(getContext());
        mBackground = new FrameLayout(getContext());
        removeAllViews();
        addView(mBackground);
        addView(mTextureView);
        setScaleType(ScaleType.FIT_CENTER);
        mTextureView.setSurfaceTextureListener(this);
    }

    private void initPlayer() {
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
        } else {
            mMediaPlayer.reset();
        }
        mIsVideoPrepared = false;
        mIsPlayCalled = false;
        mState = State.UNINITIALIZED;
    }
    /**
     * set logging enabled/disabled
     */
    public void setLogOn(Boolean b){
        LOG_ON = b;
    }
    /**
     * set scale type
     */
    public void setScaleType(ScaleType scaleType) {
        mScaleType = scaleType;
    }

    /**
     * @see MediaPlayer#isPlaying()
     */
    public boolean isPlaying() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.isPlaying();
        }
        return false;
    }

    /**
     * set resource as data source
     */
    public void setRawData(@RawRes int id) {
        initPlayer();

        try {

            AssetFileDescriptor afd = getResources().openRawResourceFd(id);
            setDataSource(afd);
        }
        catch (Exception e){
            if(mListener!=null)
                mListener.onError(e);
            log(e.getMessage());
        }
    }

    /**
     * set asset as data source
     */
    public void setAssetData(@NonNull String assetName) {
        initPlayer();

        try {
            AssetManager manager = getContext().getAssets();
            AssetFileDescriptor afd = manager.openFd(assetName);
            setDataSource(afd);
        }
        catch (Exception e){
            if(mListener!=null)
                mListener.onError(e);
            log(e.getMessage());
        }
    }

    /**
     * @see MediaPlayer#setDataSource(String)
     */
    public void setDataSource(String path) {
        initPlayer();

        try {
            mMediaPlayer.setDataSource(path);
            mIsDataSourceSet = true;
            prepare();
        } catch (IOException e) {
            if(mListener!=null)
                mListener.onError(e);
            log(e.getMessage());
        }
    }

    /**
     * @see MediaPlayer#setDataSource(android.content.Context, android.net.Uri)
     */
    public void setDataSource(Context context, Uri uri) {
        initPlayer();

        try {
            mMediaPlayer.setDataSource(context, uri);
            mIsDataSourceSet = true;
            prepare();
        } catch (IOException e) {
            if(mListener!=null)
                mListener.onError(e);
            log(e.getMessage());
        }
    }

    /**
     * @see MediaPlayer#setDataSource(java.io.FileDescriptor)
     */
    public void setDataSource(AssetFileDescriptor afd) {
        initPlayer();

        try {
            long startOffset = afd.getStartOffset();
            long length = afd.getLength();
            mMediaPlayer.setDataSource(afd.getFileDescriptor(), startOffset, length);
            mIsDataSourceSet = true;
            prepare();
        } catch (IOException e) {
            if(mListener!=null)
                mListener.onError(e);
            log(e.getMessage());
        }
    }

    private void prepare() {
        try {
            mMediaPlayer.setOnVideoSizeChangedListener(
                    new MediaPlayer.OnVideoSizeChangedListener() {
                        @Override
                        public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
                            mVideoWidth = width;
                            mVideoHeight = height;
                            scaleVideoSize(width,height);
                        }
                    }
            );
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mState = State.END;
                    log("Video has ended.");

                    if (mListener != null) {
                        mListener.onVideoEnd();
                    }
                }
            });

            mMediaPlayer.prepareAsync();

            // Play video when the media source is ready for playback.
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mIsVideoPrepared = true;
                    if (mIsPlayCalled && mIsViewAvailable) {
                        log("Player is prepared and play() was called.");
                        play();
                    }

                    if (mListener != null) {
                        mListener.onVideoPrepared();
                    }
                }
            });

            mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mediaPlayer, int i, int i2) {
                    return false;
                }
            });

        } catch (Exception e) {
            if(mListener!=null)
                mListener.onError(e);
            log(e.getMessage());
        }
    }

    /**
     * Play or resume video. Video will be played as soon as view is available and media player is
     * prepared.
     *
     * If video is stopped or ended and play() method was called, video will start over.
     */
    public void play() {
        play(0);
    }

    /**
     * Play or resume video from a position. Video will be played as soon as view is available and media player is
     * prepared.
     *
     * If video is stopped or ended and play() method was called, video will start over.
     */
    public void play(int startPosition) {
        if (!mIsDataSourceSet) {
            log("play() was called but data source was not set.");
            return;
        }

        mIsPlayCalled = true;

        if (!mIsVideoPrepared) {
            log("play() was called but video is not prepared yet, waiting.");
            return;
        }

        if (!mIsViewAvailable) {
            log("play() was called but view is not available yet, waiting.");
            return;
        }

        if (mState == State.PLAY) {
            log("play() was called but video is already playing.");
            return;
        }

        if (mState == State.PAUSE) {
            log("play() was called but video is paused, resuming.");
            mState = State.PLAY;
            mMediaPlayer.start();
            return;
        }

        mState = State.PLAY;
        mMediaPlayer.seekTo(startPosition);
        mMediaPlayer.start();
    }
    /**
     * Get video height.
     */
    public float getVideoHeight() {
        return mVideoHeight;
    }
    /**
     * Get video width.
     */
    public float getVideoWidth() {
        return mVideoWidth;
    }


    /**
     * Pause video. If video is already paused, stopped or ended nothing will happen.
     */
    public void pause() {
        if (mState == State.PAUSE) {
            log("pause() was called but video already paused.");
            return;
        }

        if (mState == State.STOP) {
            log("pause() was called but video already stopped.");
            return;
        }

        if (mState == State.END) {
            log("pause() was called but video already ended.");
            return;
        }

        mState = State.PAUSE;
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
        }
    }

    /**
     * Stop video (pause and seek to beginning). If video is already stopped or ended nothing will
     * happen.
     */
    public void stop() {
        if (mState == State.STOP) {
            log("stop() was called but video already stopped.");
            return;
        }

        if (mState == State.END) {
            log("stop() was called but video already ended.");
            return;
        }

        mState = State.STOP;
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
            mMediaPlayer.seekTo(0);
        }
    }

    /**
     * @see MediaPlayer#setLooping(boolean)
     */
    public void setLooping(boolean looping) {
        mMediaPlayer.setLooping(looping);
    }

    /**
     * @see MediaPlayer#seekTo(int)
     */
    public void seekTo(int milliseconds) {
        mMediaPlayer.seekTo(milliseconds);
    }

    /**
     * @see MediaPlayer#getDuration()
     */
    public int getDuration() {
        return mMediaPlayer.getDuration();
    }
    /**
     * get millis, seconds, minutes or hours duration
     */
    public int getFullDuration(TimeUnit unit) {
        int res = mMediaPlayer.getDuration();
        switch (unit){
            case MILLIS:{
                break;
            }
            case SECONDS:{
                res=res/1000;
                break;
            }
            case MINUTES:{
                res=res/1000/60;
                break;
            }
            case HOURS:{
                res=res/1000/60/60;
                break;
            }
        }
        return res;
    }

    /**
     * @see MediaPlayer#getCurrentPosition()
     */
    public int getCurrentPosition() { return mMediaPlayer.getCurrentPosition(); }

    /**
     * @see MediaPlayer#setVolume(float, float)
     */
    public void setVolume(float leftVolume, float rightVolume) {
        mMediaPlayer.setVolume(leftVolume, rightVolume);
    }

    /**
     * @see MediaPlayer#isLooping()
     */
    public boolean isLooping() {
        return mMediaPlayer.isLooping();
    }
    private void log(String message) {
        if (LOG_ON) {
            Log.d(TAG, message);
        }
    }
    /**
     * @see MediaPlayer#reset()
     */
    public void reset() {
        mMediaPlayer.reset();
    }
    /**
     * @see MediaPlayer#release()
     */
    public void release() {
        reset();
        mMediaPlayer.release();
        mMediaPlayer = null;
    }

    private void scaleVideoSize(int videoWidth, int videoHeight) {
        if (videoWidth == 0 || videoHeight == 0) {
            return;
        }

        Size viewSize = new Size(getWidth(), getHeight());
        Size videoSize = new Size(videoWidth, videoHeight);
        ScaleManager scaleManager = new ScaleManager(viewSize, videoSize);
        Matrix matrix = scaleManager.getScaleMatrix(mScaleType);
        if (matrix != null) {
            mTextureView.setTransform(matrix);
        }
    }
    /**
     * Listener trigger 'onError', 'onVideoPrepared' and `onVideoEnd` events
     */
    public void setListener(MediaPlayerListener listener) {
        mListener = listener;
    }


    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
        Surface surface = new Surface(surfaceTexture);
        mMediaPlayer.setSurface(surface);
        mIsViewAvailable = true;
        if (mIsDataSourceSet && mIsPlayCalled && mIsVideoPrepared) {
            log("View is available and play() was called.");
            play();
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mMediaPlayer == null) {
            return;
        }

        if (isPlaying()) {
            stop();
        }
        release();
        log("view detached from window");
    }

    //customize background
    private void addBackground(){
        mBackground.setVisibility(View.VISIBLE);
    }
    /**
     * set background drawable for view
     */
    @Override
    public void setBackground(Drawable d){
        addBackground();
        mBackground.setBackground(d);
    }
    /**
     * set background color for view
     */
    public void setBackgroundColor(int color){
        addBackground();
        mBackground.setBackgroundColor(color);
    }
    /**
     * set background resource for view
     */
    @Override
    public void setBackgroundResource(int resid){
        addBackground();
        mBackground.setBackgroundResource(resid);
    }
    /**
     * set background tint list for view
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void setBackgroundTintList(ColorStateList tintList)
    {
        addBackground();
        mBackground.setBackgroundTintList(tintList);
    }
    /**
     * set background tint mode for view
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void setBackgroundTintMode(PorterDuff.Mode tintMode)
    {
        addBackground();
        mBackground.setBackgroundTintMode(tintMode);
    }
    /**
     * set a solid background color for the drawing cache's bitmaps will improve performance and memory usage. Note, though that this should only be used if this view will always be drawn on top of a solid color.
     */
    @Override
    public void setDrawingCacheBackgroundColor(int color)
    {
        addBackground();
        mBackground.setDrawingCacheBackgroundColor(color);
    }
    /**
     * remove background from view
     */
    public void removeBackground(){
        mBackground.setBackground(null);
        mBackground.setVisibility(View.INVISIBLE);
    }
}