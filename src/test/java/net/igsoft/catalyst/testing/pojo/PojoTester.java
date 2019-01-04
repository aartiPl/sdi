package net.igsoft.catalyst.testing.pojo;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import com.openpojo.reflection.PojoClass;
import com.openpojo.validation.Validator;
import com.openpojo.validation.utils.ValidationHelper;
import nl.jqno.equalsverifier.EqualsVerifier;

public class PojoTester {
    @SuppressWarnings("rawtypes")
    private static final Class[] NO_PARAMETERS = new Class[]{};
    @SuppressWarnings("rawtypes")
    private static final Class[] OBJECT_PARAMETER = new Class[]{Object.class};

    private static final Predicate<Method> EQUALS_PREDICATE =
            method -> "equals".equals(method.getName()) &&
                      !Object.class.equals(method.getDeclaringClass()) &&
                      boolean.class.equals(method.getReturnType()) &&
                      Arrays.equals(method.getParameterTypes(), OBJECT_PARAMETER) &&
                      Modifier.isPublic(method.getModifiers());

    private static final Predicate<Method> HASHCODE_PREDICATE =
            method -> "hashCode".equals(method.getName()) &&
                      !Object.class.equals(method.getDeclaringClass()) &&
                      int.class.equals(method.getReturnType()) &&
                      Arrays.equals(method.getParameterTypes(), NO_PARAMETERS) &&
                      Modifier.isPublic(method.getModifiers());

    private static final Predicate<Method> TOSTRING_PREDICATE =
            method -> "toString".equals(method.getName()) &&
                      !Object.class.equals(method.getDeclaringClass()) &&
                      String.class.equals(method.getReturnType()) &&
                      Arrays.equals(method.getParameterTypes(), NO_PARAMETERS) &&
                      Modifier.isPublic(method.getModifiers());

    private final List<PojoClass> pojoClasses;
    private final Validator pojoValidator;
    private final Function<Object, Boolean> customTester;

    PojoTester(List<PojoClass> pojoClasses, Validator pojoValidator,
               Function<Object, Boolean> customTester) {
        this.pojoClasses = pojoClasses;
        this.pojoValidator = pojoValidator;
        this.customTester = customTester;
    }

    public static PojoTesterBuilder builder() {
        return new PojoTesterBuilder();
    }

    private static boolean hasMethod(Class<?> clazz, Predicate<Method> predicate) {
        for (Method method : clazz.getMethods()) {
            if (predicate.test(method)) {
                return true;
            }
        }

        return false;
    }

    public void execute() {
        for (PojoClass pojoClass : pojoClasses) {
            pojoValidator.validate(pojoClass);

            Class<?> clazz = pojoClass.getClazz();

            if (!hasMethod(clazz, EQUALS_PREDICATE)) {
                throw new AssertionError("'equals' not defined in class " + clazz.getSimpleName());
            }

            if (!hasMethod(clazz, HASHCODE_PREDICATE)) {
                throw new AssertionError(
                        "'hashCode' not defined in class " + clazz.getSimpleName());
            }

            if (!hasMethod(clazz, TOSTRING_PREDICATE)) {
                throw new AssertionError(
                        "'toString' not defined in class " + clazz.getSimpleName());
            }

            EqualsVerifier.forClass(clazz).usingGetClass().verify();

            Object object = ValidationHelper.getMostCompleteInstance(pojoClass);

            if (!customTester.apply(object)) {
                throw new AssertionError("Custom Tester failed for class " + clazz.getSimpleName());
            }
        }
    }
}
