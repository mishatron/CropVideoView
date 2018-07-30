package video.lib.mishatronic.cropvideoview;

/**
 * Created by mishatron on 30.07.2018.
 * Trigger 'onError', 'onVideoPrepared' and 'onVideoEnd'
 */

public interface MediaPlayerListener {

    void onVideoPrepared();

    void onVideoEnd();

    void onError(Exception e);
}
