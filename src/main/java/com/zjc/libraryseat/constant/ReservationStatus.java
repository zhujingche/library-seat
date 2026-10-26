package com.zjc.libraryseat.constant;

public class ReservationStatus {
    public static final String RESERVED   = "RESERVED";
    public static final String CHECKED_IN = "CHECKED_IN";
    public static final String COMPLETED  = "COMPLETED";
    public static final String CANCELLED  = "CANCELLED";
    public static final String NO_SHOW    = "NO_SHOW";
    //防止他人new
    private ReservationStatus() {}
    public static boolean canTransit(String from,String to)
    {
        if(RESERVED.equals(from))
        {
            return CHECKED_IN.equals(to)||NO_SHOW.equals(to)||CANCELLED.equals(to);
        }
        if(CHECKED_IN.equals(from))
        {
            return COMPLETED.equals(to);
        }
        return false;
    }

}
