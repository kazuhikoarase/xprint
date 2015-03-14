package com.d_project.xprint.barcode;

import java.util.ArrayList;
import java.util.List;

/**
 * ITF
 * @author Kazuhiko Arase
 */
public class ITF extends Barcode {

    private static final Double NARROW = new Double(1);
    private static final Double WIDE = new Double(2.5);

    private static final Double[][] NUMBER = {
        {NARROW, NARROW, WIDE, WIDE, NARROW},   // 0
        {WIDE, NARROW, NARROW, NARROW, WIDE},   // 1
        {NARROW, WIDE, NARROW, NARROW, WIDE},   // 2
        {WIDE, WIDE, NARROW, NARROW, NARROW},   // 3
        {NARROW, NARROW, WIDE, NARROW, WIDE},   // 4
        {WIDE, NARROW, WIDE, NARROW, NARROW},   // 5
        {NARROW, WIDE, WIDE, NARROW, NARROW},   // 6
        {NARROW, NARROW, NARROW, WIDE, WIDE},   // 7
        {WIDE, NARROW, NARROW, WIDE, NARROW},   // 8
        {NARROW, WIDE, NARROW, WIDE, NARROW}    // 9
    };
    
    public Double[] createPattern() {

        String data = getData();
        
        if (data.length() % 2 != 0) {
            char checkDigit = ModUtil.calcMod10_3W(data);
            data = data + checkDigit;
        }
        
        List pattern = new ArrayList();

        // quiet
        pattern.add(new Double(NARROW.doubleValue() * 10) );

        // start        
        pattern.add(NARROW);
        pattern.add(NARROW);
        pattern.add(NARROW);
        pattern.add(NARROW);

        for (int i = 0; i + 1 < data.length(); i += 2) {

            char c1 = data.charAt(i);
            char c2 = data.charAt(i + 1);

            if (c1 < '0' || '9' < c1) {
                throw new RuntimeException("invalid char " + data + " at " + (i + 1) );
            }            
            if (c2 < '0' || '9' < c2) {
                throw new RuntimeException("invalid char " + data + " at " + (i + 2) );
            }

            for (int p = 0; p < 5; p++) {
                pattern.add(NUMBER[c1 - '0'][p]);       
                pattern.add(NUMBER[c2 - '0'][p]);       
            }
        }

        // stop
        pattern.add(WIDE);
        pattern.add(NARROW);
        pattern.add(NARROW);

        // quiet
        pattern.add(new Double(NARROW.doubleValue() * 10) );

        return (Double[])pattern.toArray(new Double[pattern.size()]);
    }
            
}
