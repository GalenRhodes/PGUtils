package com.projectgalen.lib.utils.test;

import com.projectgalen.lib.utils.PGProperties;
import com.projectgalen.lib.utils.PGResourceBundle;
import com.projectgalen.lib.utils.U;
import com.projectgalen.lib.utils.reflection.Reflection;
import com.projectgalen.lib.utils.test.casting.TestClass;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.TypeVariable;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SuppressWarnings({ "SameParameterValue", "MismatchedQueryAndUpdateOfCollection" })
public class Main {

    private static final PGResourceBundle msgs     = PGResourceBundle.getXMLPGBundle("com.projectgalen.lib.utils.test.test_messages");
    private static final String           DASH_STR = String.format("%c%c", (char)8211, (char)8211);

    public Main() {
    }

    public static void main(String[] args) {
        String str = "com.projectgalen.lib.utils.test.test_messages";
        System.out.printf("%s: \"%s\"\n", "    FIRST", U.getPart(str, "\\.", U.Parts.FIRST));
        System.out.printf("%s: \"%s\"\n", "     LAST", U.getPart(str, "\\.", U.Parts.LAST));
        System.out.printf("%s: \"%s\"\n", "NOT_FIRST", U.getPart(str, "\\.", U.Parts.NOT_FIRST));
        System.out.printf("%s: \"%s\"\n", " NOT_LAST", U.getPart(str, "\\.", U.Parts.NOT_LAST));
    }

    private static void propertiesTest() {
        propsTest();
        System.out.println("=================================================================================================================================");
        propsXMLTest();
    }

    private static void testDashReplacement() {
        String str = DASH_STR;

        System.out.println(str);

        for(int i = 0, j = str.length(); i < j; i++) {
            System.out.printf("%d ", (int)str.charAt(i));
        }

        System.out.println();
        System.out.println();
    }

    private static void testXMLPropertiesSave() {
        try {
            PGProperties props = PGProperties.getXMLProperties("pg_properties.xml", PGProperties.class, PGProperties.getProperties("main_settings.xml", Main.class));

            props.storeToXML(System.out, null, StandardCharsets.UTF_8);
        }
        catch(Exception e) {
            e.printStackTrace(System.err);
        }
    }

    private static void testGenerics() {
        List<String>                       myList   = new ArrayList<>();
        Class<?>                           cls      = myList.getClass();
        TypeVariable<? extends Class<?>>[] typeVars = cls.getTypeParameters();

        System.out.print(msgs.format("form01", msgs.getString("tx_name"), cls.getName()));
        System.out.print(msgs.format("form01", msgs.getString("tx_type"), cls.getTypeName()));
        System.out.print(msgs.format("form01", msgs.getString("tx_generic"), cls.toGenericString()));

        int i = 0;
        for(TypeVariable<? extends Class<?>> tv : typeVars) {
            System.out.print(msgs.format("form01", msgs.format("form07", ++i), tv.getTypeName()));
        }
    }

    private static void testMethod(@NotNull Object o) {
        System.out.print(msgs.format("form08", o.getClass().getName()));
        System.out.print(msgs.format("form09", o.getClass().isPrimitive()));
    }

    private static void checkAssignability(Field[] fields) {
        for(Field field : fields) {
            System.out.print(msgs.format("form10", field.getName(), field.getType().getName(), field.getType().isPrimitive() ? msgs.getString("tx_is") : msgs.getString("tx_isnot")));
            for(Field f1 : fields) {
                boolean flag = Reflection.isNumericallyAssignable(field.getType(), f1.getType());
                System.out.print(msgs.format("form11", f1.getType().getName(), flag ? msgs.getString("tx_yes") : msgs.getString("tx_no")));
            }
        }
    }

    private static void numbersTest() {
        Class<TestClass> cls    = TestClass.class;
        Field[]          fields = cls.getDeclaredFields();

        checkAssignability(fields);
    }

    private static void propsTest() {
        PGResourceBundle bundle = PGResourceBundle.getPGBundle("com.projectgalen.lib.utils.test.main_messages");
        PGProperties     props  = PGProperties.getProperties("main_settings.properties", Main.class);

        propsTest(bundle, props);
    }

    private static void propsXMLTest() {
        PGResourceBundle bundle = PGResourceBundle.getXMLPGBundle("com.projectgalen.lib.utils.test.main_messages");
        PGProperties     props  = PGProperties.getXMLProperties("main_settings.xml", Main.class);

        propsTest(bundle, props);
    }

    private static void propsTest(PGResourceBundle bundle, PGProperties props) {
        System.out.print(msgs.format("form04", msgs.getString("tx_msg_test"), bundle.getString("msg.test.main")));
        System.out.printf(msgs.format("form05", /* */msgs.getString("tx_bool_true"), msgs.getString("form_b")), /* */props.getBoolean("set.test.boolean.1", false));
        System.out.printf(msgs.format("form05", /**/msgs.getString("tx_bool_false"), msgs.getString("form_b")), /* */props.getBoolean("set.test.boolean.2", true));
        System.out.printf(msgs.format("form05", /*    */msgs.getString("tx_string"), msgs.getString("form_s")), /**/props.getProperty("set.test.main"));
        System.out.printf(msgs.format("form05", /*      */msgs.getString("tx_byte"), msgs.getString("form_d")), /*    */props.getByte("set.test.byte"));
        System.out.printf(msgs.format("form05", /*     */msgs.getString("tx_short"), msgs.getString("form_d")), /*   */props.getShort("set.test.short"));
        System.out.printf(msgs.format("form05", /*       */msgs.getString("tx_int"), msgs.getString("form_d")), /*     */props.getInt("set.test.int"));
        System.out.printf(msgs.format("form05", /*      */msgs.getString("tx_long"), msgs.getString("form_d")), /*    */props.getLong("set.test.long"));
        System.out.printf(msgs.format("form05", /*    */msgs.getString("tx_double"), msgs.getString("form_g")), /*  */props.getDouble("set.test.double"));
        System.out.printf(msgs.format("form05", /*     */msgs.getString("tx_float"), msgs.getString("form_g")), /*   */props.getFloat("set.test.float"));
        System.out.printf(msgs.format("form05", /*      */msgs.getString("tx_list"), msgs.getString("form_s")), /*    */props.getList("set.test.list"));
        System.out.printf(msgs.format("form05", /*       */msgs.getString("tx_map"), msgs.getString("form_s")), /*     */props.getMap("set.test.map"));

        SimpleDateFormat sdf = new SimpleDateFormat(PGProperties.DEFAULT_DATETIME_FORMAT);

        for(int i = 1; i <= 5; i++) {
            Date d = props.getDateProperty("set.test.date." + i, props.getProperty("set.test.date.format." + i, PGProperties.DEFAULT_DATETIME_FORMAT), null);
            System.out.print(msgs.format("form05", msgs.format("form06", msgs.getString("tx_date"), i), d == null ? msgs.getString("tx_na") : sdf.format(d)));
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
                list.add(msgs.format("form03", msgs.getString("tx_setter"), msgs.getString("tx_void"), m.getName(), pt[0].getSimpleName()));
            }
            else if(rt != void.class && pt.length == 0) {
                list.add(msgs.format("form03", msgs.getString("tx_getter"), rt.getSimpleName(), m.getName(), ""));
            }
        }

        list.sort(String::compareTo);
        for(String s : list) System.out.println(s);
    }

    @SuppressWarnings("UnnecessaryBoxing")
    private static void test01() throws NoSuchFieldException, IllegalAccessException {
        TestClass obj = new TestClass();
        Field     f   = TestClass.class.getDeclaredField(msgs.getString("long_obj_field"));

        f.setAccessible(true);
        f.set(obj, (long)1);
        System.out.print(msgs.format("form02", msgs.getString("long_obj_field"), obj.getLongObjField()));
        f.set(obj, (long)((byte)2));
        System.out.print(msgs.format("form02", msgs.getString("long_obj_field"), obj.getLongObjField()));

        f = TestClass.class.getDeclaredField(msgs.getString("long_field"));
        f.setAccessible(true);
        f.set(obj, Long.valueOf(3));
        System.out.print(msgs.format("form02", msgs.getString("long_field"), obj.getLongField()));
        f.set(obj, Long.valueOf((byte)4));
        System.out.print(msgs.format("form02", msgs.getString("long_field"), obj.getLongField()));
    }
}
