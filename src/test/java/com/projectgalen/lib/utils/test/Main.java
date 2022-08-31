package com.projectgalen.lib.utils.test;

import com.projectgalen.lib.utils.PGProperties;
import com.projectgalen.lib.utils.PGResourceBundle;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Main {
    public Main() {
    }

    public static void main(String[] args) {
        PGResourceBundle bundle = PGResourceBundle.getSharedBundle("com.projectgalen.lib.utils.test.main_messages");
        PGProperties     props  = PGProperties.getSharedInstanceForNamedResource("main_settings.properties", Main.class);

        System.out.printf("                 Message Test: \"%s\"%n", bundle.getString("msg.test.main"));
        System.out.printf("Settings Test  boolean (true): \"%b\"%n", props.getBooleanProperty("set.test.boolean.1", false));
        System.out.printf("Settings Test boolean (false): \"%b\"%n", props.getBooleanProperty("set.test.boolean.2", true));
        System.out.printf("Settings Test          String: \"%s\"%n", props.getProperty("set.test.main"));
        System.out.printf("Settings Test            byte: \"%d\"%n", props.getByteProperty("set.test.byte"));
        System.out.printf("Settings Test           short: \"%d\"%n", props.getShortProperty("set.test.short"));
        System.out.printf("Settings Test             int: \"%d\"%n", props.getIntProperty("set.test.int"));
        System.out.printf("Settings Test            long: \"%d\"%n", props.getLongProperty("set.test.long"));
        System.out.printf("Settings Test          double: \"%g\"%n", props.getDoubleProperty("set.test.double"));
        System.out.printf("Settings Test           float: \"%g\"%n", props.getFloatProperty("set.test.float"));
        System.out.printf("Settings Test            List: \"%s\"%n", props.getList("set.test.list"));
        System.out.printf("Settings Test             Map: \"%s\"%n", props.getMap("set.test.map"));

        SimpleDateFormat sdf = new SimpleDateFormat(PGProperties.DEFAULT_DATETIME_FORMAT);

        for(int i = 1; i <= 5; i++) {
            Date d = props.getDateProperty("set.test.date." + i, props.getProperty("set.test.date.format." + i, PGProperties.DEFAULT_DATETIME_FORMAT), null);
            System.out.printf("Settings Test         Date %d: %s%n", i, d == null ? "-N/A-" : sdf.format(d));
        }
    }
}
