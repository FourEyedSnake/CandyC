package com.bluearc.dev.candyc;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class VoiceReactions {

    public enum MOOD {
        HAPPY(0),
        GRUMPY(1),
        ANGRY(2);

        private int val;

        MOOD(int value) {
            val = value;
        }

        public int getVal() {
            return val;
        }

        public static MOOD fromValue(int val) {
            for(MOOD v : values()) {
                if (v.getVal() == val) {
                    return v;
                }
            }
            return HAPPY;
        }
    }

    private static Map<String, String> speechMap = new HashMap<>();
    private static List<String> happyList = new ArrayList<>();
    private static List<String> grumpyList = new ArrayList<>();
    private static List<String> angryList = new ArrayList<>();
    private static List<Integer> soundList = new ArrayList<>();
    private static Map<MOOD, List<String>> moodMap = new HashMap<>();

    static {
        speechMap.put("broken", "sigh");
        speechMap.put("well then", "best be off");
        speechMap.put("lunch", "sausage and mash");
        speechMap.put("knock knock", "who's there?");
        speechMap.put("candy", "bugger off");
        speechMap.put("andy", "get lost");
        speechMap.put("greg", "Urgh Greg is such a pain. I need a drink.");
        speechMap.put("joke", "Want to hear a joke? This year's gip payout");

        moodMap.put(MOOD.HAPPY, happyList);
        moodMap.put(MOOD.GRUMPY, grumpyList);
        moodMap.put(MOOD.ANGRY, angryList);

        happyList.add("ship it");
        happyList.add("time to go to the pub");
        happyList.add("arrgghh");
        happyList.add("mines a pint please");
        happyList.add("boo");
        happyList.add("Greg stop slacking");
        happyList.add("Ah good, Martin has come online. I can stop working now!");
        happyList.add("That isn't the behaviour I'd expect from an HDS employee");
        happyList.add("The thing with Arsenal is they always try and walk it in");
        happyList.add("you what mate");

        grumpyList.add("do some work");
        grumpyList.add("we're doomed");
        grumpyList.add("sigh");
        grumpyList.add("this is fundamentally broken");
        grumpyList.add("computer says noooooooo");
        grumpyList.add("Does not compute");
        grumpyList.add("I could crush a grape");
        grumpyList.add("Managing Greg has forced me in to retirement");
        grumpyList.add("The networking team has gone way down hill since I left");
        grumpyList.add("What a miserable day");
        grumpyList.add("Hackathon was my idea. Simon is just taking the credit");

        angryList.add("be quiet");
        angryList.add("what");
        angryList.add("go away");
        angryList.add("f this I'm going home");
        angryList.add("Want to hear a joke? This year's gip payout");
        angryList.add("Where are the pay rises?");
        angryList.add("Urgh another daily meeting. Obsidian is so dull");
        angryList.add("Obsidian will never ship");

        soundList.add(R.raw.groan);
        soundList.add(R.raw.hound);
        soundList.add(R.raw.moo);
        soundList.add(R.raw.sheep);
    }

    public static String getRandomResponse(MOOD mood) {
        Random rand = new Random();
        List<String> responses =  moodMap.get(mood);
        String response = responses.get(rand.nextInt(responses.size() - 1));
        if (response == null || response.isEmpty()) {
            return "boom";
        }
        return response;
    }

    public static String getReaction(List<String> inputList, MOOD mood) {
        int i = 0;
        while (i < inputList.size()) {
            String input = inputList.get(i);
            for (String react : speechMap.keySet()) {
                if (input.contains(react)) {
                    return speechMap.get(react);
                }
            }
            i++;
        }
        return getRandomResponse(mood);
    }

    public static MOOD getRandomMood() {
        Random rand = new Random();
        MOOD mood = MOOD.fromValue(rand.nextInt(3));
        Log.i("Mood",mood.toString());
        return mood;
    }

    public static  int getRandomSound() {
        Random rand = new Random();
        return soundList.get(rand.nextInt(soundList.size() - 1));
    }
}
