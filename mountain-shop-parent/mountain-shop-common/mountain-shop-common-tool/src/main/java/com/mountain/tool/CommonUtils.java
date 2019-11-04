package com.mountain.tool;



import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * @author yuit
 * @create Time 2018/8/7 14:56
 *
 **/
@SuppressWarnings("all")
public final class CommonUtils {

    /*public static String stringFormat(String target, Object... source) {
        StringExpression expression = StringFormatter.format(target, source);
        return expression.getValue();
    }*/

    public static Date dateFormat(String dateStr) {

        SimpleDateFormat mt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {
            return mt.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 参数转换，将"1,2,3"转换为[1,2,3]集合
     *
     * @param str
     * @param type
     * @return
     */
    public static List transformStringToList(String str, Class type) {

        List<? super Object> parameters = new ArrayList<>();

        String[] tmps = str.trim().split(",");

        String typeName = type.getSimpleName();

        switch (typeName) {
            case "Byte":
                for (String item : tmps) {
                    try {
                        Byte var1 = Byte.parseByte(item);
                        parameters.add(var1);
                    } catch (Exception e) {
                        throw e;
                    }
                }
                break;
            case "Long":
                for (String item : tmps) {
                    try {
                        Long var1 = Long.parseLong(item);
                        parameters.add(var1);
                    } catch (Exception e) {
                        throw e;
                    }
                }
            default:
                for (String item : tmps) {
                    parameters.add(item);
                }
        }

        return parameters;
    }


    public static Set transformStringToSet(String str, Class type) {

        Set<? super Object> parameters = new HashSet<>();

        String[] tmps = str.trim().split(",");

        String typeName = type.getSimpleName();

        switch (typeName) {
            case "Byte":
                for (String item : tmps) {
                    try {
                        Byte var1 = Byte.parseByte(item);
                        parameters.add(var1);
                    } catch (Exception e) {
                        throw e;
                    }
                }
                break;
            case "Long":
                for (String item : tmps) {
                    try {
                        Long var1 = Long.parseLong(item);
                        parameters.add(var1);
                    } catch (Exception e) {
                        throw e;
                    }
                }
            default:
                for (String item : tmps) {
                    parameters.add(item);
                }
        }

        return parameters;
    }

    /**
     * Map转换为对象
     *
     * @param map
     * @param target
     * @return
     * @throws Exception
     */
    public static Object transformMapToObject(Map map, Class target) throws Exception {

        BeanInfo beanInfo = Introspector.getBeanInfo(target);

        // 获取所有属性
        PropertyDescriptor[] descriptors = beanInfo.getPropertyDescriptors();

        Object obj = target.newInstance();

        for (PropertyDescriptor descriptor : descriptors) {

            // map中是否包含该属性
            if (map.containsKey(descriptor.getName())) {

                String propertySimpleName = descriptor.getPropertyType().getSimpleName();

                String key = descriptor.getName();

                if (propertySimpleName.equals("Byte") || propertySimpleName.equals("byte")) {
                    Byte byteValue = Byte.parseByte(String.valueOf(map.get(key)));
                    descriptor.getWriteMethod().invoke(obj, byteValue);
                    continue;
                }

                if (propertySimpleName.equals("Long") || propertySimpleName.equals("long")) {
                    Long longValue = Long.parseLong(String.valueOf(map.get(key)));
                    descriptor.getWriteMethod().invoke(obj, longValue);
                    continue;
                }

                if (propertySimpleName.equals("Short") || propertySimpleName.equals("short")) {
                    Short shortValue = Short.parseShort(String.valueOf(map.get(key)));
                    descriptor.getWriteMethod().invoke(obj, shortValue);
                    continue;
                }

                if (propertySimpleName.equals("Double") || propertySimpleName.equals("double")) {
                    Double doubleValue = Double.parseDouble(String.valueOf(map.get(key)));
                    descriptor.getWriteMethod().invoke(obj, doubleValue);
                    continue;
                }

                if (propertySimpleName.equals("Float") || propertySimpleName.equals("float")) {
                    Float floatValue = Float.parseFloat(String.valueOf(map.get(key)));
                    descriptor.getWriteMethod().invoke(obj, floatValue);
                    continue;
                }

                // 如果不符合上述条件
                Object value = map.get(key);
                descriptor.getWriteMethod().invoke(obj, value);

            }

        }

        return obj;
    }

    /**
     * 将source中的属性值赋给target中属性；
     * 如果source中的属性值不为空就进行赋值操作
     *
     * @param source
     * @param target 目标赋值类
     * @return
     * @throws Exception
     */
    public static Object copy(Object source, Object target) throws Exception {

        // 获取对象的描述信息
        BeanInfo sf = Introspector.getBeanInfo(source.getClass());
        BeanInfo tf = Introspector.getBeanInfo(target.getClass());

        // 获取属性描述信息，其中包括 属性名称，类型，属性的读写方法
        PropertyDescriptor[] desSf = sf.getPropertyDescriptors();
        PropertyDescriptor[] desTf = tf.getPropertyDescriptors();

        for (PropertyDescriptor var1 : desSf) {
            // 如果该属性名是class 并且类型为Class 则跳过
            if (var1.getName().equals("class") && var1.getPropertyType().getSimpleName().equals("Class")) continue;

            for (PropertyDescriptor var2 : desTf) {

                // 如果该属性名是class 并且类型为Class 则跳过
                if (var1.getName().equals("class") && var2.getPropertyType().getSimpleName().equals("Class")) continue;

                if (var1.getName().equals(var2.getName())) {

                    Method methodSf = var1.getReadMethod();
                    Object obj = methodSf.invoke(source);
                    // 如果该属性的读方法返回的值不为空则进行赋值
                    if (obj != null) {
                        Method methodTf = var2.getWriteMethod();
                        // 反射 调用target 对应属性的写方法
                        methodTf.invoke(target, obj);
                    }

                }
            }
        }
        return target;
    }

    /**
     * 随机生成UUID
     */
    public static String uuid32Generator() {

        UUID uuid = UUID.randomUUID();
        String uuidStr = uuid.toString().replace("-", "");
        return uuidStr;
    }



}
