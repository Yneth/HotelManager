package ua.abond.lab4.core;

import ua.abond.lab4.core.bean.BeanDefinition;

public interface BeanConstructor {
    boolean canCreate(ConfigurableBeanFactory context, String bean, BeanDefinition beanDefinition);
    Object create(ConfigurableBeanFactory context, String bean, BeanDefinition beanDefinition);
}
