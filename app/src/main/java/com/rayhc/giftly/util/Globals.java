package com.rayhc.giftly.util;

import java.text.SimpleDateFormat;

/**
 * Holds all the global variables
 */
public class Globals {
    //Gift Vars
    public static final String CURR_GIFT_KEY = "curr gift";
    public static final String FROM_REVIEW_KEY = "from review";
    public static final String FILE_LABEL_KEY = "file label";
    public static final SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy@HH:mm:ss");

    public static final String TAG = "debug";
    public static final String USER_ID_KEY = "userId";
    public static final String SENT_MAP_KEY = "SENT GIFT MAP";
    public static final String REC_MAP_KEY = "RECEIVED GIFT MAP";
    public static final String GOT_GIFTS_KEY = "GOT GIFTS";
    public static final String GET_GIFTS_KEY = "GET GIFTS";
    public static final String HASH_VALUE_KEY = "HASH VALUE";
    public static final String FROM_REC_KEY = "FROM RECEIVED";
    public static final String FROM_OPEN_KEY = "FROM OPEN";
    public static final String WAS_OPENED_KEY = "WAS OPENED";
    public static final String LABEL_KEY = "LABEL";
    public static final String FRIEND_NAME_KEY = "FRIEND NAME";
    public static final String FRIEND_ID_KEY = "FRIEND ID";

    // Gift Types
    public static final String BDAY = "Birthday";
    public static final String XMAS = "Christmas";
    public static final String OTHER = "Other";
    public static final String[] GIFT_TYPE_ARRAY = {BDAY, XMAS, OTHER};

    // List adapter:
    public static final String NEW_GIFT = "NEW";
    public static final String OLD_GIFT = "OLD";

    public static final String UPDATE_TOAST = "Updated!";
}
