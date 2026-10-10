package mg.itu.myframework.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mg.itu.myframework.exception.ParameterBindingException;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;

public final class ParameterBinder {
    private ParameterBinder() {
    }

    public static Object[] bind(Method method, HttpServletRequest request, HttpServletResponse response)
            throws ParameterBindingException {
        Parameter[] parameters = method.getParameters();
        Object[] arguments = new Object[parameters.length];

        for (int index = 0; index < parameters.length; index++) {
            Parameter parameter = parameters[index];
            Class<?> type = parameter.getType();
            if (HttpServletRequest.class.isAssignableFrom(type)) {
                arguments[index] = request;
            } else if (HttpServletResponse.class.isAssignableFrom(type)) {
                arguments[index] = response;
            } else if (!isSimpleType(type)) {
                arguments[index] = bindObject(type, request);
            } else {
                String value = request.getParameter(parameter.getName());
                if (value == null) {
                    throw new ParameterBindingException("Parametre HTTP manquant : " + parameter.getName());
                }
                arguments[index] = convert(value, type, parameter.getName());
            }
        }
        return arguments;
    }

    private static boolean isSimpleType(Class<?> type) {
        return type == String.class
                || type.isPrimitive()
                || type.isEnum()
                || type == Integer.class
                || type == Long.class
                || type == Double.class
                || type == Float.class
                || type == Short.class
                || type == Byte.class
                || type == Boolean.class
                || type == Character.class;
    }

    private static Object bindObject(Class<?> type, HttpServletRequest request)
            throws ParameterBindingException {
        try {
            Object bean = type.getDeclaredConstructor().newInstance();
            BeanInfo beanInfo = Introspector.getBeanInfo(type, Object.class);
            boolean hasWritableProperty = false;

            for (PropertyDescriptor property : beanInfo.getPropertyDescriptors()) {
                if (property.getWriteMethod() == null) {
                    continue;
                }
                hasWritableProperty = true;
                String propertyName = property.getName();
                String value = request.getParameter(propertyName);
                if (value == null) {
                    throw new ParameterBindingException("Parametre HTTP manquant : " + propertyName);
                }
                Object convertedValue = convert(value, property.getPropertyType(), propertyName);
                property.getWriteMethod().invoke(bean, convertedValue);
            }

            if (!hasWritableProperty) {
                throw new ParameterBindingException("Aucune propriete modifiable pour : " + type.getName());
            }
            return bean;
        } catch (ParameterBindingException e) {
            throw e;
        } catch (IntrospectionException | ReflectiveOperationException e) {
            throw new ParameterBindingException("Impossible de construire : " + type.getName(), e);
        }
    }

    @SuppressWarnings("unchecked")
    private static Object convert(String value, Class<?> type, String parameterName)
            throws ParameterBindingException {
        try {
            if (type == String.class) return value;
            if (type == int.class || type == Integer.class) return Integer.valueOf(value);
            if (type == long.class || type == Long.class) return Long.valueOf(value);
            if (type == double.class || type == Double.class) return Double.valueOf(value);
            if (type == float.class || type == Float.class) return Float.valueOf(value);
            if (type == short.class || type == Short.class) return Short.valueOf(value);
            if (type == byte.class || type == Byte.class) return Byte.valueOf(value);
            if (type == boolean.class || type == Boolean.class) return Boolean.valueOf(value);
            if (type == char.class || type == Character.class) {
                if (value.length() == 1) return value.charAt(0);
                throw new IllegalArgumentException("un seul caractere attendu");
            }
            if (type.isEnum()) return Enum.valueOf(type.asSubclass(Enum.class), value);
        } catch (IllegalArgumentException e) {
            throw new ParameterBindingException(
                    "Valeur invalide pour le parametre " + parameterName + " : " + value, e);
        }
        throw new ParameterBindingException("Type de parametre non supporte : " + type.getName());
    }
}
