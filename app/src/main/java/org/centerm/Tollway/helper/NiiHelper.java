package org.centerm.Tollway.helper;

public class NiiHelper {

    public static final String ON_US = "246";
    public static final String OFF_US = "245";

    private static String[] onUsList = {"0060", "50436709", "504367", "990006"};
//    private String[] offUsList = {"449932", "453215", "453216", "473252", "473254", "473256", "484830", "484831", "621654", "931006", "9310061"};

    public static String checkCardNo(String cardNo) {
        String type = OFF_US;
        for (String anOnUsList : onUsList) {
            if (anOnUsList.contains(cardNo)) {
                type = ON_US;
                break;
            }
        }
        return type;
    }

}
