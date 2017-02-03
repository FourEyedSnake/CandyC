package com.bluearc.dev.candyc;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import java.util.Locale;
import java.util.Random;

@SuppressWarnings("deprecation")
public class MainActivity extends ActionBarActivity implements
        TextToSpeech.OnInitListener {

    public RemoteSpeach http_stuff;
    private ImageView candyImage;
    static private TextToSpeech tts;
    private Locale appLocale = Locale.UK;
    private Button speakButton;
    private Button tDriveButton;
    private Toolbar toolbar;
    private static final int REQUEST_CODE = 1234;

    private Handler periodicStuff = new Handler();
    private boolean backgroundStuffOn = true;
    private int backgroundStuffDelayMS = 10000;
    private int count = 0;
    private int glassesCount = 0;
    private Boolean angryFace = false;
    private Boolean withGlasses = false;
    private Runnable backGroundTasks = new Runnable() {
        @Override
        public void run() {

            if (backgroundStuffOn) {
                if (count < 10) {
                    VoiceReactions.MOOD mood = VoiceReactions.getRandomMood();
                    saySomething(VoiceReactions.getRandomResponse(mood), mood);
                    count++;
                } else {
                    MediaPlayer mPlayer = MediaPlayer.create(MainActivity.this, VoiceReactions.getRandomSound());
                    mPlayer.setLooping(false);
                    mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            Log.i("Media", "onCompletion");
                            mp.release();
                        }
                    });
                    mPlayer.start();
                    count = 0;
                }
                periodicStuff.postDelayed(backGroundTasks, backgroundStuffDelayMS);
            }
            if (glassesCount == 3) {
                withGlasses = true;
                glassesCount = 0;
            } else {
                withGlasses = false;
                glassesCount++;
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        candyImage = (ImageView) findViewById(R.id.candycFace);
        candyImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                candycTouch();
            }
        });

        speakButton = (Button) findViewById(R.id.speakButton);
        checkVoiceRecognition();
        tts = new TextToSpeech(MainActivity.this, this);

        tDriveButton = (Button) findViewById(R.id.t_drive);

        (new Thread(new Server())).start();

    }

    @SuppressLint("NewApi")
    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            tts.setLanguage(appLocale);
            tts.setPitch(0.4f);
            tts.setSpeechRate(0.5f);
            tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                @Override
                public void onStart(String utteranceId) {
                    Log.i("TTS", "Start");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (angryFace) {
                                if (withGlasses) {
                                    candyImage.setImageResource(R.drawable.candycface4angryglasses);
                                    Log.i("mood", "angryWithGlasses");
                                } else {
                                    candyImage.setImageResource(R.drawable.candycface4angry);
                                    Log.i("mood", "angry");
                                }
                            } else {
                                if (withGlasses) {
                                    candyImage.setImageResource(R.drawable.candycface4glasses);
                                    Log.i("mood", "glasses");
                                } else {
                                    candyImage.setImageResource(R.drawable.candycface4);
                                    Log.i("mood", "original");
                                }

                            }

                        }
                    });
                }

                @Override
                public void onDone(String utteranceId) {
                    Log.i("TTS", "Done!");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (angryFace) {
                                candyImage.setImageResource(R.drawable.candycfaceangry);
                            } else {
                                candyImage.setImageResource(R.drawable.candyfaceoriginal);
                            }
                        }
                    });
                }

                @Override
                public void onError(String utteranceId) {
                    Log.e("TTS", "Failed!");
                }
            });
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menubar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_mute) {
            /*CheckBox checkBox = (CheckBox) item.getActionView();
            if (checkBox.isChecked()) {
                checkBox.setChecked(false);
            }
            else {
                checkBox.setChecked(true);
            }*/
            return true;
        } else if (id == R.id.backgroundNever) {
            backgroundStuffOn = false;
        } else if (id == R.id.backgroundTenSeconds) {
            turnBackgroundOn(10 * 1000);
        } else if (id == R.id.backgroundThirtySeconds) {
            turnBackgroundOn(30 * 1000);
        } else if (id == R.id.backgroundSixtySeconds) {
            turnBackgroundOn(60 * 1000);
        } else if (id == R.id.backgroundRandom) {
            Random rand = new Random();
            turnBackgroundOn((rand.nextInt(9) + 1) * 5 * 60 * 1000);
        }

        return super.onOptionsItemSelected(item);
    }

    private void turnBackgroundOn(int delayMS) {
        backgroundStuffOn = true;
        backgroundStuffDelayMS = delayMS;
        periodicStuff.postDelayed(backGroundTasks, backgroundStuffDelayMS);
    }

    @Override
    protected void onDestroy() {
        //Close the Text to Speech Library
        if (tts != null) {

            tts.stop();
            tts.shutdown();
            Log.d("TTS", "TTS Destroyed. Barin.");
        }
        super.onDestroy();
    }

    public void candycTouch() {
        saySomething("Don't touch me!", VoiceReactions.MOOD.ANGRY);
    }





    @SuppressWarnings("deprecation")
    public void saySomething(final String message) {
        final VoiceReactions.MOOD mood = VoiceReactions.MOOD.GRUMPY;
        saySomething(message, mood);
    }

    public static void saySomethingStatic(final String message, final float pitch, final float speed, final String lang) {

        tts.setPitch(pitch);
        tts.setSpeechRate(speed);
        tts.setLanguage(Locale.UK);
        String upperLang = lang.toUpperCase();

        Locale locale = Locale.UK;
        if (upperLang.equals("US") || upperLang.equals("ENGLISH")) {
            locale = Locale.UK;
        }
        if (upperLang.equals("US") || upperLang.equals("AMERICAN")) {
            locale = Locale.US;
        }
        if (upperLang.equals("FRENCH") || upperLang.equals("FRANCE")) {
            locale = Locale.FRENCH;
        }
        if (upperLang.equals("JAPAN") || upperLang.equals("JAPANESE")) {
            locale = Locale.JAPANESE;
        }
        if (upperLang.equals("CHINA") || upperLang.equals("CHINESE")) {
            locale = Locale.CHINESE;
        }
        if (upperLang.equals("GERMAN") || upperLang.equals("GERMANY")) {
            locale = Locale.GERMAN;
        }
        if (upperLang.equals("ITALY") || upperLang.equals("ITALIAN")) {
            locale = Locale.ITALIAN;
        }
        if (upperLang.equals("KOREA") || upperLang.equals("KOREAN")) {
            locale = Locale.KOREAN;
        }
        tts.setLanguage(locale);

        saySomethingStatic(message);
        tts.setLanguage(Locale.UK);
    }

    public static void saySomethingStatic(final String message) {

        HashMap<String, String> map = new HashMap<String, String>();
        map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "messageID");
        tts.speak(message, TextToSpeech.QUEUE_FLUSH, map);
    }

    public void saySomething(final String message, final VoiceReactions.MOOD mood) {
        Log.i("moodInSaySomething", mood.toString());
        if (mood.equals(VoiceReactions.MOOD.ANGRY)) {
            Log.i("setAngry", "true");
            angryFace = true;
        } else {
            Log.i("setAngry", "false");
            angryFace = false;
        }

        HashMap<String, String> map = new HashMap<String, String>();
        map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "messageID");
        tts.speak(message, TextToSpeech.QUEUE_FLUSH, map);
    }

    public void tryTDrive(MenuItem item) {
        Log.d("MainActivity", "Trying T-drive");
        TDrive.Engage();
    }

    public void speakButtonClicked(View v) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now or forever hold your peace...");
        startActivityForResult(intent, REQUEST_CODE);
    }

    private void checkVoiceRecognition() {
        // Disable button if no recognition service is present
        PackageManager pm = getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
        if (activities.size() == 0) {
            speakButton.setEnabled(false);
            speakButton.setText("Recognizer not present");
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            // Populate the wordsList with the String values the recognition engine thought it heard
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            final VoiceReactions.MOOD mood = VoiceReactions.getRandomMood();
            final String toSay = VoiceReactions.getReaction(matches, mood);
            saySomething(toSay, mood);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
