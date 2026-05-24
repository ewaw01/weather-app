package com.example.weather_application;

import org.springframework.stereotype.Component;

@Component
public class Utils {
    public String calculateTheTimeInterval(
            String t
    ) {
        if(0 <= Integer.parseInt(t) && Integer.parseInt(t) < 3) {
            return "00";
        } else if (3 <= Integer.parseInt(t) && Integer.parseInt(t) < 6) {
            return "03";
        } else if (6 <= Integer.parseInt(t) && Integer.parseInt(t) < 9) {
            return "06";
        } else if (9 <= Integer.parseInt(t) && Integer.parseInt(t) < 12) {
            return "09";
        } else if (12 <= Integer.parseInt(t) && Integer.parseInt(t) < 15) {
            return "12";
        } else if (15 <= Integer.parseInt(t) && Integer.parseInt(t) < 18) {
            return "15";
        } else if (18 <= Integer.parseInt(t) && Integer.parseInt(t) < 21) {
            return "18";
        } else if (21 <= Integer.parseInt(t) && Integer.parseInt(t) < 24) {
            return "21";
        }

        return "00";
    }
}
