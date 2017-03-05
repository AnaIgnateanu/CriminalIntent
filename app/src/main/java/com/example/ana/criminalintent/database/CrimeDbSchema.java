package com.example.ana.criminalintent.database;

/**
 * Created by ana on 2/20/2017.
 */

public class CrimeDbSchema {
    public static final class CrimeTable {
        public static final String NAME = "crimes";

        public static final class Cols {
            public static final String UUID = "uuid";
            public static final String TITLE = "title";
            public static final String DATE = "date";
            public static final String TIME="timeOfDay";
            public static final String SOLVED = "solved";
            public static final String SUSPECT = "suspect";
        }
    }
}
