package ua.abond.lab4.core.bean;

import org.apache.log4j.Logger;
import ua.abond.lab4.core.BeanFactoryPostProcessor;
import ua.abond.lab4.core.ConfigurableBeanFactory;
import ua.abond.lab4.core.Ordered;
import ua.abond.lab4.core.annotation.ComponentScan;
import ua.abond.lab4.core.context.AnnotationBeanFactory;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ComponentScanAnnotationBeanFactoryPostProcessor implements BeanFactoryPostProcessor, Ordered {
    private static final Logger logger = Logger.getLogger(ComponentScanAnnotationBeanFactoryPostProcessor.class);

    @Override
    public void postProcess(ConfigurableBeanFactory context) {
        AnnotationBeanFactory beanFactory = context.getBean(AnnotationBeanFactory.class);

        List<String> paths = context.getBeanDefinitionNames().stream().
                map(context::getBeanDefinition).
                map(BeanDefinition::getType).
                filter(type -> type.isAnnotationPresent(ComponentScan.class)).
                map(type -> type.getAnnotation(ComponentScan.class)).
                map(ComponentScan::value).
                flatMap(Arrays::stream).
                peek(path -> logger.debug(String.format("Scanning '%s' for components.", path))).
                collect(Collectors.toList());

        String[] pathArray = new String[paths.size()];
        paths.toArray(pathArray);
        beanFactory.scan(pathArray);
    }

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE;
    }
}
