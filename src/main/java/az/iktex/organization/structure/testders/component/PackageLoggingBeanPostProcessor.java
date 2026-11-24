package az.iktex.organization.structure.testders.component;

import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

@Component
public class PackageLoggingBeanPostProcessor implements BeanPostProcessor {

    private static final String PACKAGE_PREFIX = "az.iktex";

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        if (isOurBean(bean)) {
            System.out.println("[BPP] before initialization: " + beanName);
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        if (isOurBean(bean)) {
            System.out.println("[BPP] after initialization: " + beanName);
        }
        return bean;
    }

    private boolean isOurBean(Object bean) {
        return bean.getClass().getName().startsWith(PACKAGE_PREFIX);
    }
}
