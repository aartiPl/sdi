package net.igsoft.catalyst.testing.pojo;

import java.util.List;
import java.util.function.Function;

import com.google.common.collect.Lists;
import com.openpojo.reflection.PojoClass;
import com.openpojo.reflection.impl.PojoClassFactory;
import com.openpojo.validation.ValidatorBuilder;
import com.openpojo.validation.rule.Rule;
import com.openpojo.validation.rule.impl.GetterMustExistRule;
import com.openpojo.validation.rule.impl.NoNestedClassRule;
import com.openpojo.validation.rule.impl.NoPublicFieldsRule;
import com.openpojo.validation.rule.impl.NoStaticExceptFinalRule;
import com.openpojo.validation.test.Tester;
import com.openpojo.validation.test.impl.DefaultValuesNullTester;
import com.openpojo.validation.test.impl.GetterTester;
import com.openpojo.validation.test.impl.SetterTester;

public class PojoTesterBuilder {

    private final List<PojoClass> pojoClasses;
    private final ValidatorBuilder pojoValidator;
    private Function<Object, Boolean> customTester;

    public PojoTesterBuilder() {
        pojoClasses = Lists.newArrayList();

        pojoValidator = ValidatorBuilder.create()
                                        .with(new NoPublicFieldsRule())
                                        .with(new NoStaticExceptFinalRule())
                                        .with(new GetterMustExistRule())
                                        .with(new NoNestedClassRule())
                                        .with(new DefaultValuesNullTester())
                                        .with(new GetterTester())
                                        .with(new SetterTester());

        customTester = input -> true;
    }

    public PojoTesterBuilder withRule(Rule rule) {
        pojoValidator.with(rule);
        return this;
    }

    public PojoTesterBuilder withTester(Tester tester) {
        pojoValidator.with(tester);
        return this;
    }

    public PojoTesterBuilder withCustomTester(Function<Object, Boolean> customTester) {
        this.customTester = customTester::apply;
        return this;
    }

    public PojoTesterBuilder withClass(Class<?> clazz) {
        pojoClasses.add(PojoClassFactory.getPojoClass(clazz));
        return this;
    }

    public PojoTester build() {
        return new PojoTester(pojoClasses, pojoValidator.build(), customTester);
    }
}
