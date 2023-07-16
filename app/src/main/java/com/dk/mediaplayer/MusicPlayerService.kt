package com.dk.mediaplayer

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.widget.Toast

@Suppress("DEPRECATION")
class MusicPlayerService : Service() {

    var mMediaPlayer : MediaPlayer? = null
    //미디어 플레이어 객체를 null로 초기화

    var mBinder : MusicPlayerBinder = MusicPlayerBinder()

    inner class MusicPlayerBinder : Binder(){
        fun getService() : MusicPlayerService {
            return this@MusicPlayerService // 바인더를 반환해 서비스 함수를 쓸수 있다.
        }
    }

    override fun onCreate(){
        super.onCreate()
        startForegroundService() // 포그라운드 서비스 시작
    } // 서비스가 실행될때 딱 한번 실행

    //바인드
    override fun onBind(intent: Intent?): IBinder? {
        return mBinder
    } // 바인더 반환

    //시작된 상태 & 백그라운드 (startService()를 호출하면 실행되는 콜백 함수0
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    //서비스 종료
    override fun onDestroy() {
        super.onDestroy()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            stopForeground(true);
        }
    }

    // 재생중인지 확인
    fun startForegroundService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val notificationManager = getSystemService(NOTIFICATION_SERVICE)
                        as NotificationManager
                val mChannel = NotificationChannel(
                    "CHANNEL_ID",
                    "CHANNEL_NAME",
                    NotificationManager.IMPORTANCE_DEFAULT
                )
                notificationManager.createNotificationChannel(mChannel)
        }

        val notification: Notification = Notification.Builder(this, "CHANNEL_ID")
            .setSmallIcon(R.drawable.ic_play) // 알림 아이콘
            .setContentTitle("뮤직 플래이어 앱") // 알림의 제목
            .setContentText("앱이 실행중입니다.") // 알림의 내용
            .build()

        startForeground(1, notification) //인수로 아이디와 일림 지정
    }

    // 재생중인지 확인
    fun isPlaying() : Boolean {
        return (mMediaPlayer != null && mMediaPlayer?.isPlaying ?: false)
    }
    // 재생
    fun play(){
        if (mMediaPlayer == null){
            //음악 파일의 리소스를 가져와 미디어 플레이어 객체에 할당해준다.
            mMediaPlayer = MediaPlayer.create(this, R.raw.esens)

            mMediaPlayer?.setVolume(1.0f , 1.0f) //볼륨지정
            mMediaPlayer?.isLooping = true // 반복재생 여부
            mMediaPlayer?.start() // 음악 재생
        } else{ //이미 음악이 재생중이다.
            if (mMediaPlayer!!.isPlaying){
                Toast.makeText(this, "이미 음악이 실행 중입니다.",
                Toast.LENGTH_SHORT).show()
            } else{
                mMediaPlayer?.start() // 음악재생
            }
        }
    }
    //일시정지
    fun pause(){
        mMediaPlayer?.let{
            if (it.isPlaying){
                it.pause() //음악 일시정지
            }
        }
    }
    //정지
    fun stop() {
        mMediaPlayer?.let {
            if (it.isPlaying) {
                it.stop() // 음악을 멈춘다
                it.release() // 미디어 플레이어에 할당 자원을 해제시켜준다.
                mMediaPlayer = null
            }
        }
    }
}