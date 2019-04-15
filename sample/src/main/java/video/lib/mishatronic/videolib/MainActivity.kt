package video.lib.mishatronic.videolib

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import video.lib.mishatronic.cropvideoview.CropVideoView
import video.lib.mishatronic.cropvideoview.MediaPlayerListener
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        showProgress()

        video_view?.background = ContextCompat.getDrawable(this, R.drawable.ic_launcher_background)
        video_view?.setListener(object : MediaPlayerListener
        {
            override fun onError(e: Exception?) {
                hideProgress()
            }

            override fun onVideoPrepared() {
                hideProgress()
            }

            override fun onVideoEnd()  {
                video_view?.removeBackground()
            }

        })
        video_view?.setRawData(R.raw.videoplayback)
        video_view?.keepScreenOn = true
        video_view?.requestFocus()
//        video_view?.isLooping = true

//        video_view?.setScaleType(ScaleType.FIT_CENTER)

        video_view?.play()


    }
    private fun showProgress(){

        progress_bar?.visibility = View.VISIBLE
    }
    private fun hideProgress(){

        progress_bar?.visibility = View.GONE
    }
}
