package com.projectgalen.lib.utils.test;

import com.projectgalen.lib.utils.PGProperties;
import com.projectgalen.lib.utils.PGResourceBundle;
import com.projectgalen.lib.utils.reflection.Reflection;
import com.projectgalen.lib.utils.test.casting.TestClass;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.TypeVariable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Main {
    public Main() {
    }

    public static void main(String[] args) {
        try {
            testMethod(3);
        }
        catch(Exception e) {
            e.printStackTrace(System.err);
        }
    }

    private static void testGenerics() {
        List<String>                       myList   = new ArrayList<>();
        Class<?>                           cls      = myList.getClass();
        TypeVariable<? extends Class<?>>[] typeVars = cls.getTypeParameters();

        System.out.printf("%15s: %s%n", "Name", cls.getName());
        System.out.printf("%15s: %s%n", "Type name", cls.getTypeName());
        System.out.printf("%15s: %s%n", "Generic String", cls.toGenericString());

        int i = 0;
        for(TypeVariable<? extends Class<?>> tv : typeVars) {
            System.out.printf("%15s: %s%n", String.format("Type Var #%d", ++i), tv.getTypeName());
        }
    }

    private static void testMethod(@NotNull Object o) {
        System.out.printf("%n%n       Parameter Class Type: %s%n", o.getClass().getName());
        System.out.printf("Parameter Class isPrimitive: %s%n%n", o.getClass().isPrimitive());
    }

    private static void checkAssignability(Field[] fields) {
        for(Field field : fields) {
            System.out.printf("Field \"%s\" is type \"%s\" and %s primitive.%n", field.getName(), field.getType().getName(), field.getType().isPrimitive() ? "is" : "is not");
            for(Field f1 : fields) {
                boolean flag = Reflection.isNumericallyAssignable(field.getType(), f1.getType());
                System.out.printf("\tAssignable from \"%s\"? %s%n", f1.getType().getName(), flag ? "YES" : "NO");
            }
        }
    }

    private static void numbersTest() {
        Class<TestClass> cls    = TestClass.class;
        Field[]          fields = cls.getDeclaredFields();

        checkAssignability(fields);
    }

    private static void propsTest() {
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

    private static void showMethods() {
        Class<TestClass> cls     = TestClass.class;
        Method[]         methods = cls.getDeclaredMethods();
        List<String>     list    = new ArrayList<>();

        for(Method m : methods) {
            Class<?>   rt = m.getReturnType();
            Class<?>[] pt = m.getParameterTypes();

            if(rt == void.class && pt.length == 1) {
                list.add(String.format("Setter \"void %s(%s)\"", m.getName(), pt[0].getSimpleName()));
            }
            else if(rt != void.class && pt.length == 0) {
                list.add(String.format("Getter \"%s %s()\"", rt.getSimpleName(), m.getName()));
            }
        }

        list.sort(String::compareTo);
        for(String s : list) System.out.println(s);
    }

    @SuppressWarnings("UnnecessaryBoxing") private static void test01() throws NoSuchFieldException, IllegalAccessException {
        TestClass obj = new TestClass();
        Field     f   = TestClass.class.getDeclaredField("longObjField");

        f.setAccessible(true);
        f.set(obj, (long)1);
        System.out.printf("longObjField value: %d%n", obj.getLongObjField());
        f.set(obj, (long)((byte)2));
        System.out.printf("longObjField value: %d%n", obj.getLongObjField());

        f = TestClass.class.getDeclaredField("longField");
        f.setAccessible(true);
        f.set(obj, Long.valueOf(3));
        System.out.printf("longField value: %d%n", obj.getLongField());
        f.set(obj, Long.valueOf((byte)4));
        System.out.printf("longField value: %d%n", obj.getLongField());
    }
}
