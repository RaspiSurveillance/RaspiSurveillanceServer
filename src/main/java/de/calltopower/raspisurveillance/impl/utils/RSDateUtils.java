package de.calltopower.raspisurveillance.impl.utils;

import java.util.Date;

import org.springframework.stereotype.Component;

import de.calltopower.raspisurveillance.api.utils.RSUtils;
import lombok.NoArgsConstructor;

/**
 * Date utilities
 */
@Component
@NoArgsConstructor
public class RSDateUtils implements RSUtils {

    public Date getDateMinusMinutes(long minutes) {
        return new Date(System.currentTimeMillis() - (Math.abs(minutes) * 60 * 1000));
    }

    public Date getDatePlusMinutes(long minutes) {
        return new Date(System.currentTimeMillis() + (Math.abs(minutes) * 60 * 1000));
    }

}
