package org.leialearns.utilities;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Stack;

/**
 * Provides functions to display objects in a human friendly form.
 */
public class Display {

    private Display() {
        throw new UnsupportedOperationException("This class must not be instantiated: " + getClass().getSimpleName());
    }

    /**
     * Shows a string in such a way that it can be uniquely decoded, but with a minimum of syntactic sugar.
     * @param s The string to show
     * @return The representation of the given string
     */
    public static String show(String s) {
        StringBuilder builder = new StringBuilder();
        if (s != null) {
            for (char ch : s.toCharArray()) {
                addCharacter(builder, ch);
            }
        }
        return builder.toString();
    }

    private static void addCharacter(StringBuilder builder, char ch) {
        switch (ch) {
            case ' ':
                builder.append('_');
                break;
            case '\n':
                builder.append("\\n");
                break;
            case '\t':
                builder.append("\\t");
                break;
            case '\r':
                builder.append("\\r");
                break;
            case '\\':
            case '\'':
            case '"':
            case '_':
            case '?':
            case '(':
            case ')':
            case '[':
            case ']':
            case '{':
            case '}':
            case '<':
            case '>':
                builder.append('\\');
                builder.append(ch);
                break;
            default:
                if (ch > ' ' && ch <= '~') {
                    builder.append(ch);
                } else if (ch <= 0xFF) {
                    builder.append("\\x");
                    builder.append(String.format("%02X", (int) ch));
                } else {
                    builder.append("\\u");
                    builder.append(String.format("%04X", (int) ch));
                }

        }
    }

    /**
     * Displays the given parts with a bit of syntactic sugar.
     * @param parts The parts to display
     * @return The string that displays the given parts
     */
    public static String displayParts(Object... parts) {
        StringBuilder builder = new StringBuilder("[");
        boolean first = true;
        for (Object part : parts) {
            if (first) {
                first = false;
            } else {
                builder.append('|');
            }
            if (part == null) {
                builder.append('?');
            } else {
                builder.append(display(part));
            }
        }
        builder.append(']');
        return builder.toString();
    }

    public static Object asDisplayWithTypes(final Object object) {
        return new Object() {
            public String toString() {
                return displayWithTypes(object);
            }
        };
    }

    /**
     * Displays the given object including type information.
     * @param object The object to display
     * @return The string that displays the given object
     */
    public static String displayWithTypes(Object object) {
        StringBuilder builder = new StringBuilder();
        displayRecursive(object, builder, new Stack<>(), true);
        return builder.toString();
    }

    public static Object asDisplay(final Object object) {
        return new Object() {
            public String toString() {
                return display(object);
            }
        };
    }

    /**
     * Displays the given object excluding type information.
     * @param object The object to display
     * @return The string that displays the given object
     */
    public static String display(Object object) {
        StringBuilder builder = new StringBuilder();
        displayRecursive(object, builder, new Stack<>(), false);
        return builder.toString();
    }

    /**
     * Displays the given objects as an array excluding type information.
     * @param first The first object to display
     * @param second The first object to display
     * @param remainder The remaining objects to display
     * @return The string that displays the given objects
     */
    public static String display(Object first, Object second, Object... remainder) {
        Object[] array = new Object[remainder.length + 2];
        array[0] = first;
        array[1] = second;
        System.arraycopy(remainder, 0, array, 2, remainder.length);
        return display(array);
    }

    private static void displayRecursive(Object object, StringBuilder builder, Stack<Object> parents, boolean showTypes) {
        if (object == null) {
            builder.append("null");
        } else if (object instanceof String) {
            builder.append(show(object.toString()));
        } else {
            boolean loop = false;
            for (Object parent : parents) {
                if (object == parent) {
                    loop = true;
                    break;
                }
            }
            if (loop) {
                builder.append("...");
            } else {
                parents.push(object);
                try {
                    if (object.getClass().isArray()) {
                        displayArray(object, builder, parents, showTypes);
                    } else if (object instanceof Iterable) {
                        displayIterable((Iterable<?>) object, builder, parents, showTypes);
                    } else if (object instanceof Class) {
                        displayClass((Class<?>) object, builder, parents, showTypes);
                    } else if (object instanceof Method) {
                        displayMethod((Method) object, builder, parents, showTypes);
                    } else {
                        if (showTypes) {
                            displayRecursive(object.getClass(), builder, parents, showTypes);
                        }
                        builder.append(object.toString());
                    }
                } finally {
                    parents.pop();
                }
            }
        }
    }

    private static void displayArray(Object object, StringBuilder builder, Stack<Object> parents, boolean showTypes) {
        if (showTypes) {
            if (object.getClass().getComponentType() != Object.class) {
                builder.append('<');
                builder.append(object.getClass().getComponentType().getSimpleName());
                builder.append('>');
            }
        }
        builder.append('[');
        int length = Array.getLength(object);
        for (int i = 0; i < length; i++) {
            if (i > 0) {
                builder.append(", ");
            }
            displayRecursive(Array.get(object, i), builder, parents, showTypes);
        }
        builder.append(']');
    }

    private static void displayIterable(Iterable<?> iterable, StringBuilder builder, Stack<Object> parents, boolean showTypes) {
        if (showTypes) {
            builder.append('<');
            builder.append(iterable.getClass().getSimpleName());
            if (iterable instanceof TypedIterable) {
                builder.append('[');
                displayRecursive(((TypedIterable<?>) iterable).getType(), builder, new Stack<>(), showTypes);
                builder.append(']');
            }
            builder.append('>');
        }
        builder.append('{');
        boolean first = true;
        for (Object item : iterable) {
            if (first) {
                first = false;
            } else {
                builder.append(", ");
            }
            displayRecursive(item, builder, parents, showTypes);
        }
        builder.append('}');
    }

    private static void displayClass(Class<?> type, StringBuilder builder, Stack<Object> parents, boolean showTypes) {
        builder.append('<');
        Class<?> superClass = type.getSuperclass();
        Class<?>[] interfaces = type.getInterfaces();
        if (superClass == Proxy.class) {
            builder.append("$->");
            type = null;
        } else if (superClass != null && type.getSimpleName().matches(".*[$].*CGLIB.*")) {
            builder.append("$->");
            type = superClass;
            superClass = type.getSuperclass();
        }
        if (type != null) {
            builder.append(type.getSimpleName());
        }
        if (showTypes) {
            if (superClass != null && superClass != Object.class) {
                displayRecursive(superClass, builder, parents, showTypes);
            }
        }
        if (showTypes || type == null) {
            if (interfaces != null && interfaces.length > 0) {
                builder.append('|');
                for (Class<?> interfaceType : interfaces) {
                    displayRecursive(interfaceType, builder, parents, showTypes);
                }
            }
        }
        builder.append('>');
    }

    private static void displayMethod(Method method, StringBuilder builder, Stack<Object> parents, boolean showTypes) {
        displayClass(method.getDeclaringClass(), builder, parents, showTypes);
        builder.append('.');
        builder.append(method.getName());
        builder.append('(');
        boolean first = true;
        for (Class<?> parameterType : method.getParameterTypes()) {
            if (first) {
                first = false;
            } else {
                builder.append(',');
            }
            displayClass(parameterType, builder, parents, showTypes);
        }
        builder.append(')');
        Class<?> returnType = method.getReturnType();
        if (returnType != void.class) {
            builder.append(" -> ");
            displayClass(method.getReturnType(), builder, parents, showTypes);
        }
    }
}
