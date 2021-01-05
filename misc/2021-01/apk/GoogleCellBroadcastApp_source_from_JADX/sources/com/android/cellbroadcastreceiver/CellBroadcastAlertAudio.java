package com.android.cellbroadcastreceiver;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.media.AudioAttributes;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import com.android.cellbroadcastreceiver.CellBroadcastAlertService;
import com.android.cellbroadcastreceiver.module.R;
import java.io.IOException;
import java.util.Locale;

public class CellBroadcastAlertAudio extends Service implements TextToSpeech.OnInitListener, TextToSpeech.OnUtteranceCompletedListener, AudioManager.OnAudioFocusChangeListener {
    public static final String ACTION_START_ALERT_AUDIO = "ACTION_START_ALERT_AUDIO";
    private int mAlertDuration = -1;
    private CellBroadcastAlertService.AlertType mAlertType;
    private AudioManager mAudioManager;
    private boolean mEnableAudio;
    private boolean mEnableLedFlash;
    private boolean mEnableVibrate;
    /* access modifiers changed from: private */
    public Handler mHandler;
    /* access modifiers changed from: private */
    public int mInitialCallState;
    private MediaPlayer mMediaPlayer;
    /* access modifiers changed from: private */
    public String mMessageBody;
    private String mMessageLanguage;
    private boolean mOverrideDnd;
    private PhoneStateListener mPhoneStateListener;
    private boolean mResetAlarmVolumeNeeded;
    /* access modifiers changed from: private */
    public int mState;
    /* access modifiers changed from: private */
    public int mSubId;
    private TelephonyManager mTelephonyManager;
    /* access modifiers changed from: private */
    public TextToSpeech mTts;
    /* access modifiers changed from: private */
    public boolean mTtsEngineReady;
    /* access modifiers changed from: private */
    public boolean mTtsLanguageSupported;
    private int mUserSetAlarmVolume;
    private int[] mVibrationPattern;
    private Vibrator mVibrator;

    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onInit(int i) {
        log("onInit() TTS engine status: " + i);
        if (i == 0) {
            this.mTtsEngineReady = true;
            this.mTts.setOnUtteranceCompletedListener(this);
            setTtsLanguage();
            return;
        }
        this.mTtsEngineReady = false;
        this.mTts = null;
        loge("onInit() TTS engine error: " + i);
    }

    private void setTtsLanguage() {
        Locale locale;
        if (!TextUtils.isEmpty(this.mMessageLanguage)) {
            locale = new Locale(this.mMessageLanguage);
        } else {
            locale = Locale.getDefault();
        }
        log("Setting TTS language to '" + locale + '\'');
        int language = this.mTts.setLanguage(locale);
        log("TTS setLanguage() returned: " + language);
        this.mTtsLanguageSupported = language >= 0;
    }

    public void onUtteranceCompleted(String str) {
        if (str.equals("com.android.cellbroadcastreceiver.UTTERANCE_ID") && this.mState == 3) {
            log("TTS completed. Stop CellBroadcastAlertAudio service");
            stopSelf();
        }
    }

    public void onCreate() {
        this.mVibrator = (Vibrator) getSystemService("vibrator");
        this.mAudioManager = (AudioManager) getSystemService("audio");
        this.mTelephonyManager = (TelephonyManager) getSystemService("phone");
        this.mHandler = new Handler(Looper.getMainLooper()) {
            public void handleMessage(Message message) {
                int i = 1;
                switch (message.what) {
                    case 1000:
                        CellBroadcastAlertAudio.log("ALERT_SOUND_FINISHED");
                        CellBroadcastAlertAudio.this.stop();
                        if (CellBroadcastAlertAudio.this.mMessageBody == null || !CellBroadcastAlertAudio.this.mTtsEngineReady || !CellBroadcastAlertAudio.this.mTtsLanguageSupported) {
                            StringBuilder sb = new StringBuilder();
                            sb.append("MessageEmpty = ");
                            sb.append(CellBroadcastAlertAudio.this.mMessageBody == null);
                            sb.append(", mTtsEngineReady = ");
                            sb.append(CellBroadcastAlertAudio.this.mTtsEngineReady);
                            sb.append(", mTtsLanguageSupported = ");
                            sb.append(CellBroadcastAlertAudio.this.mTtsLanguageSupported);
                            CellBroadcastAlertAudio.log(sb.toString());
                            CellBroadcastAlertAudio.this.stopSelf();
                            int unused = CellBroadcastAlertAudio.this.mState = 0;
                        } else {
                            sendMessageDelayed(CellBroadcastAlertAudio.this.mHandler.obtainMessage(1001), 1000);
                            int unused2 = CellBroadcastAlertAudio.this.mState = 2;
                        }
                        CellBroadcastAlertReminder.queueAlertReminder(CellBroadcastAlertAudio.this.getApplicationContext(), CellBroadcastAlertAudio.this.mSubId, true);
                        return;
                    case 1001:
                        CellBroadcastAlertAudio.log("ALERT_PAUSE_FINISHED");
                        int i2 = -1;
                        if (CellBroadcastAlertAudio.this.mMessageBody != null && CellBroadcastAlertAudio.this.mTtsEngineReady && CellBroadcastAlertAudio.this.mTtsLanguageSupported) {
                            CellBroadcastAlertAudio.log("Speaking broadcast text: " + CellBroadcastAlertAudio.this.mMessageBody);
                            CellBroadcastAlertAudio.this.mTts.setAudioAttributes(CellBroadcastAlertAudio.this.getAlertAudioAttributes());
                            i2 = CellBroadcastAlertAudio.this.mTts.speak(CellBroadcastAlertAudio.this.mMessageBody, 2, (Bundle) null, "com.android.cellbroadcastreceiver.UTTERANCE_ID");
                            int unused3 = CellBroadcastAlertAudio.this.mState = 3;
                        }
                        if (i2 != 0) {
                            CellBroadcastAlertAudio.loge("TTS engine not ready or language not supported or speak() failed");
                            CellBroadcastAlertAudio.this.stopSelf();
                            int unused4 = CellBroadcastAlertAudio.this.mState = 0;
                            return;
                        }
                        return;
                    case 1002:
                        if (CellBroadcastAlertAudio.this.enableLedFlash(message.arg1 != 0)) {
                            Handler access$400 = CellBroadcastAlertAudio.this.mHandler;
                            if (message.arg1 != 0) {
                                i = 0;
                            }
                            sendMessageDelayed(access$400.obtainMessage(1002, i, 0), 250);
                            return;
                        }
                        return;
                    default:
                        CellBroadcastAlertAudio.loge("Handler received unknown message, what=" + message.what);
                        return;
                }
            }
        };
        C02722 r0 = new PhoneStateListener() {
            public void onCallStateChanged(int i, String str) {
                if (i != 0 && i != CellBroadcastAlertAudio.this.mInitialCallState) {
                    CellBroadcastAlertAudio.log("Call interrupted. Stop CellBroadcastAlertAudio service");
                    CellBroadcastAlertAudio.this.stopSelf();
                }
            }
        };
        this.mPhoneStateListener = r0;
        this.mTelephonyManager.listen(r0, 32);
    }

    public void onDestroy() {
        log("onDestroy");
        stop();
        this.mTelephonyManager.listen(this.mPhoneStateListener, 0);
        TextToSpeech textToSpeech = this.mTts;
        if (textToSpeech != null) {
            try {
                textToSpeech.shutdown();
            } catch (IllegalStateException unused) {
                loge("exception trying to shutdown text-to-speech");
            }
        }
        if (this.mEnableAudio) {
            this.mAudioManager.abandonAudioFocus(this);
        }
    }

    public int onStartCommand(Intent intent, int i, int i2) {
        if (intent == null) {
            log("Null intent. Stop CellBroadcastAlertAudio service");
            stopSelf();
            return 2;
        }
        this.mMessageBody = intent.getStringExtra("com.android.cellbroadcastreceiver.ALERT_AUDIO_MESSAGE_BODY");
        this.mMessageLanguage = intent.getStringExtra("com.android.cellbroadcastreceiver.ALERT_AUDIO_MESSAGE_LANGUAGE");
        this.mSubId = intent.getIntExtra("com.android.cellbroadcastreceiver.ALERT_AUDIO_SUB_INDEX", -1);
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        this.mOverrideDnd = intent.getBooleanExtra("com.android.cellbroadcastreceiver.ALERT_OVERRIDE_DND_EXTRA", false);
        this.mEnableVibrate = defaultSharedPreferences.getBoolean("enable_alert_vibrate", true) || this.mOverrideDnd;
        this.mVibrationPattern = intent.getIntArrayExtra("com.android.cellbroadcastreceiver.ALERT_AUDIO_VIBRATION_PATTERN");
        this.mEnableLedFlash = CellBroadcastSettings.getResources(getApplicationContext(), this.mSubId).getBoolean(R.bool.enable_led_flash);
        this.mAlertDuration = intent.getIntExtra("com.android.cellbroadcastreceiver.ALERT_AUDIO_DURATION", -1);
        this.mAlertType = CellBroadcastAlertService.AlertType.DEFAULT;
        if (intent.getSerializableExtra("com.android.cellbroadcastreceiver.ALERT_AUDIO_TONE_TYPE") != null) {
            this.mAlertType = (CellBroadcastAlertService.AlertType) intent.getSerializableExtra("com.android.cellbroadcastreceiver.ALERT_AUDIO_TONE_TYPE");
        }
        int ringerMode = this.mAudioManager.getRingerMode();
        if (ringerMode == 0) {
            log("Ringer mode: silent");
            if (!this.mOverrideDnd) {
                this.mEnableVibrate = false;
            }
            this.mEnableAudio = this.mOverrideDnd;
        } else if (ringerMode != 1) {
            log("Ringer mode: normal");
            this.mEnableAudio = true;
        } else {
            log("Ringer mode: vibrate");
            this.mEnableAudio = this.mOverrideDnd;
        }
        if (this.mMessageBody != null && this.mEnableAudio) {
            if (this.mTts == null) {
                this.mTts = new TextToSpeech(this, this);
            } else if (this.mTtsEngineReady) {
                setTtsLanguage();
            }
        }
        if (this.mEnableAudio || this.mEnableVibrate) {
            playAlertTone(this.mAlertType, this.mVibrationPattern);
            this.mInitialCallState = this.mTelephonyManager.getCallState();
            return 1;
        }
        log("No audio/vibrate playing. Stop CellBroadcastAlertAudio service");
        stopSelf();
        return 2;
    }

    private void playAlertTone(CellBroadcastAlertService.AlertType alertType, int[] iArr) {
        stop();
        log("playAlertTone: alertType=" + alertType + ", mEnableVibrate=" + this.mEnableVibrate + ", mEnableAudio=" + this.mEnableAudio + ", mOverrideDnd=" + this.mOverrideDnd + ", mSubId=" + this.mSubId);
        Resources resources = CellBroadcastSettings.getResources(getApplicationContext(), this.mSubId);
        int i = this.mAlertDuration;
        boolean z = false;
        long j = 0;
        if (this.mEnableVibrate) {
            long[] jArr = new long[iArr.length];
            for (int i2 = 0; i2 < iArr.length; i2++) {
                jArr[i2] = (long) iArr[i2];
                j += (long) iArr[i2];
            }
            AudioAttributes.Builder builder = new AudioAttributes.Builder();
            builder.setUsage(4);
            if (this.mOverrideDnd) {
                builder.setFlags(192);
            }
            AudioAttributes build = builder.build();
            VibrationEffect createWaveform = VibrationEffect.createWaveform(jArr, i < 0 ? -1 : 0);
            log("vibrate: effect=" + createWaveform + ", attr=" + build + ", duration=" + i);
            this.mVibrator.vibrate(createWaveform, build);
        }
        if (this.mEnableLedFlash) {
            log("Start LED flashing");
            Handler handler = this.mHandler;
            handler.sendMessage(handler.obtainMessage(1002, 1, 0));
        }
        if (this.mEnableAudio) {
            MediaPlayer mediaPlayer = new MediaPlayer();
            this.mMediaPlayer = mediaPlayer;
            mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                public boolean onError(MediaPlayer mediaPlayer, int i, int i2) {
                    CellBroadcastAlertAudio.loge("Error occurred while playing audio.");
                    CellBroadcastAlertAudio.this.mHandler.sendMessage(CellBroadcastAlertAudio.this.mHandler.obtainMessage(1000));
                    return true;
                }
            });
            if (i >= 0) {
                Handler handler2 = this.mHandler;
                handler2.sendMessageDelayed(handler2.obtainMessage(1000), (long) i);
            } else {
                this.mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        CellBroadcastAlertAudio.log("Audio playback complete.");
                        CellBroadcastAlertAudio.this.mHandler.sendMessage(CellBroadcastAlertAudio.this.mHandler.obtainMessage(1000));
                    }
                });
            }
            try {
                log("Locale=" + resources.getConfiguration().getLocales() + ", alertType=" + alertType);
                int i3 = C02755.f11x97d16d5c[alertType.ordinal()];
                if (i3 == 1) {
                    setDataSourceFromResource(resources, this.mMediaPlayer, R.raw.etws_earthquake);
                } else if (i3 == 2) {
                    setDataSourceFromResource(resources, this.mMediaPlayer, R.raw.etws_tsunami);
                } else if (i3 == 3) {
                    setDataSourceFromResource(resources, this.mMediaPlayer, R.raw.etws_other_disaster);
                } else if (i3 == 4) {
                    setDataSourceFromResource(resources, this.mMediaPlayer, R.raw.etws_default);
                } else if (i3 != 5) {
                    setDataSourceFromResource(resources, this.mMediaPlayer, R.raw.default_tone);
                } else {
                    setDataSourceFromResource(resources, this.mMediaPlayer, R.raw.info);
                }
                this.mAudioManager.requestAudioFocus(this, new AudioAttributes.Builder().setLegacyStreamType(4).build(), 2, 1);
                this.mMediaPlayer.setAudioAttributes(getAlertAudioAttributes());
                setAlertVolume();
                MediaPlayer mediaPlayer2 = this.mMediaPlayer;
                if (i >= 0) {
                    z = true;
                }
                mediaPlayer2.setLooping(z);
                this.mMediaPlayer.prepare();
                this.mMediaPlayer.start();
            } catch (Exception e) {
                loge("Failed to play alert sound: " + e);
                Handler handler3 = this.mHandler;
                handler3.sendMessage(handler3.obtainMessage(1000));
            }
        } else {
            Handler handler4 = this.mHandler;
            Message obtainMessage = handler4.obtainMessage(1000);
            if (i >= 0) {
                j = (long) i;
            }
            handler4.sendMessageDelayed(obtainMessage, j);
        }
        this.mState = 1;
    }

    /* renamed from: com.android.cellbroadcastreceiver.CellBroadcastAlertAudio$5 */
    static /* synthetic */ class C02755 {

        /* renamed from: $SwitchMap$com$android$cellbroadcastreceiver$CellBroadcastAlertService$AlertType */
        static final /* synthetic */ int[] f11x97d16d5c;

        /* JADX WARNING: Can't wrap try/catch for region: R(14:0|1|2|3|4|5|6|7|8|9|10|11|12|(3:13|14|16)) */
        /* JADX WARNING: Can't wrap try/catch for region: R(16:0|1|2|3|4|5|6|7|8|9|10|11|12|13|14|16) */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Missing exception handler attribute for start block: B:11:0x003e */
        /* JADX WARNING: Missing exception handler attribute for start block: B:13:0x0049 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:3:0x0012 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:5:0x001d */
        /* JADX WARNING: Missing exception handler attribute for start block: B:7:0x0028 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:9:0x0033 */
        static {
            /*
                com.android.cellbroadcastreceiver.CellBroadcastAlertService$AlertType[] r0 = com.android.cellbroadcastreceiver.CellBroadcastAlertService.AlertType.values()
                int r0 = r0.length
                int[] r0 = new int[r0]
                f11x97d16d5c = r0
                com.android.cellbroadcastreceiver.CellBroadcastAlertService$AlertType r1 = com.android.cellbroadcastreceiver.CellBroadcastAlertService.AlertType.ETWS_EARTHQUAKE     // Catch:{ NoSuchFieldError -> 0x0012 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0012 }
                r2 = 1
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0012 }
            L_0x0012:
                int[] r0 = f11x97d16d5c     // Catch:{ NoSuchFieldError -> 0x001d }
                com.android.cellbroadcastreceiver.CellBroadcastAlertService$AlertType r1 = com.android.cellbroadcastreceiver.CellBroadcastAlertService.AlertType.ETWS_TSUNAMI     // Catch:{ NoSuchFieldError -> 0x001d }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x001d }
                r2 = 2
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x001d }
            L_0x001d:
                int[] r0 = f11x97d16d5c     // Catch:{ NoSuchFieldError -> 0x0028 }
                com.android.cellbroadcastreceiver.CellBroadcastAlertService$AlertType r1 = com.android.cellbroadcastreceiver.CellBroadcastAlertService.AlertType.OTHER     // Catch:{ NoSuchFieldError -> 0x0028 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0028 }
                r2 = 3
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0028 }
            L_0x0028:
                int[] r0 = f11x97d16d5c     // Catch:{ NoSuchFieldError -> 0x0033 }
                com.android.cellbroadcastreceiver.CellBroadcastAlertService$AlertType r1 = com.android.cellbroadcastreceiver.CellBroadcastAlertService.AlertType.ETWS_DEFAULT     // Catch:{ NoSuchFieldError -> 0x0033 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0033 }
                r2 = 4
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0033 }
            L_0x0033:
                int[] r0 = f11x97d16d5c     // Catch:{ NoSuchFieldError -> 0x003e }
                com.android.cellbroadcastreceiver.CellBroadcastAlertService$AlertType r1 = com.android.cellbroadcastreceiver.CellBroadcastAlertService.AlertType.INFO     // Catch:{ NoSuchFieldError -> 0x003e }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x003e }
                r2 = 5
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x003e }
            L_0x003e:
                int[] r0 = f11x97d16d5c     // Catch:{ NoSuchFieldError -> 0x0049 }
                com.android.cellbroadcastreceiver.CellBroadcastAlertService$AlertType r1 = com.android.cellbroadcastreceiver.CellBroadcastAlertService.AlertType.TEST     // Catch:{ NoSuchFieldError -> 0x0049 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0049 }
                r2 = 6
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0049 }
            L_0x0049:
                int[] r0 = f11x97d16d5c     // Catch:{ NoSuchFieldError -> 0x0054 }
                com.android.cellbroadcastreceiver.CellBroadcastAlertService$AlertType r1 = com.android.cellbroadcastreceiver.CellBroadcastAlertService.AlertType.DEFAULT     // Catch:{ NoSuchFieldError -> 0x0054 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0054 }
                r2 = 7
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0054 }
            L_0x0054:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.cellbroadcastreceiver.CellBroadcastAlertAudio.C02755.<clinit>():void");
        }
    }

    private static void setDataSourceFromResource(Resources resources, MediaPlayer mediaPlayer, int i) throws IOException {
        AssetFileDescriptor openRawResourceFd = resources.openRawResourceFd(i);
        if (openRawResourceFd != null) {
            mediaPlayer.setDataSource(openRawResourceFd.getFileDescriptor(), openRawResourceFd.getStartOffset(), openRawResourceFd.getLength());
            openRawResourceFd.close();
        }
    }

    /* access modifiers changed from: private */
    public boolean enableLedFlash(boolean z) {
        log("enbleLedFlash=" + z);
        CameraManager cameraManager = (CameraManager) getSystemService("camera");
        if (cameraManager == null) {
            return false;
        }
        try {
            boolean z2 = false;
            for (String str : cameraManager.getCameraIdList()) {
                try {
                    Boolean bool = (Boolean) cameraManager.getCameraCharacteristics(str).get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
                    if (bool != null && bool.booleanValue()) {
                        cameraManager.setTorchMode(str, z);
                        z2 = true;
                    }
                } catch (CameraAccessException e) {
                    log("Can't flash. e=" + e);
                }
            }
            return z2;
        } catch (CameraAccessException unused) {
            log("Can't get camera id");
            return false;
        }
    }

    public void stop() {
        TextToSpeech textToSpeech;
        log("stop()");
        this.mHandler.removeMessages(1000);
        this.mHandler.removeMessages(1001);
        this.mHandler.removeMessages(1002);
        resetAlarmStreamVolume();
        int i = this.mState;
        if (i == 1) {
            MediaPlayer mediaPlayer = this.mMediaPlayer;
            if (mediaPlayer != null) {
                try {
                    mediaPlayer.stop();
                    this.mMediaPlayer.release();
                } catch (IllegalStateException unused) {
                    loge("exception trying to stop media player");
                }
                this.mMediaPlayer = null;
            }
            this.mVibrator.cancel();
            if (this.mEnableLedFlash) {
                enableLedFlash(false);
            }
        } else if (i == 3 && (textToSpeech = this.mTts) != null) {
            try {
                textToSpeech.stop();
            } catch (IllegalStateException unused2) {
                loge("exception trying to stop text-to-speech");
            }
        }
        this.mState = 0;
    }

    public void onAudioFocusChange(int i) {
        log("onAudioFocusChanged: " + i);
    }

    /* access modifiers changed from: private */
    public AudioAttributes getAlertAudioAttributes() {
        AudioAttributes.Builder builder = new AudioAttributes.Builder();
        builder.setContentType(4);
        builder.setUsage(4);
        if (this.mOverrideDnd) {
            builder.setFlags(192);
        }
        return builder.build();
    }

    private void setAlertVolume() {
        if (this.mTelephonyManager.getCallState() != 0 || isOnEarphone()) {
            log("in call: reducing volume");
            this.mMediaPlayer.setVolume(0.125f, 0.125f);
        } else if (this.mOverrideDnd) {
            setAlarmStreamVolumeToFull();
        }
    }

    private boolean isOnEarphone() {
        for (AudioDeviceInfo type : this.mAudioManager.getDevices(2)) {
            int type2 = type.getType();
            if (type2 == 3 || type2 == 4 || type2 == 7 || type2 == 8) {
                return true;
            }
        }
        return false;
    }

    private void setAlarmStreamVolumeToFull() {
        log("setting alarm volume to full for cell broadcast alerts.");
        this.mUserSetAlarmVolume = this.mAudioManager.getStreamVolume(4);
        this.mResetAlarmVolumeNeeded = true;
        AudioManager audioManager = this.mAudioManager;
        audioManager.setStreamVolume(4, audioManager.getStreamMaxVolume(4), 0);
    }

    private void resetAlarmStreamVolume() {
        if (this.mResetAlarmVolumeNeeded) {
            log("resetting alarm volume to back to " + this.mUserSetAlarmVolume);
            this.mAudioManager.setStreamVolume(4, this.mUserSetAlarmVolume, 0);
            this.mResetAlarmVolumeNeeded = false;
        }
    }

    /* access modifiers changed from: private */
    public static void log(String str) {
        Log.d("CellBroadcastAlertAudio", str);
    }

    /* access modifiers changed from: private */
    public static void loge(String str) {
        Log.e("CellBroadcastAlertAudio", str);
    }
}
