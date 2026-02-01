package com.example.user.domain.extension;

import com.example.user.client.exception.SysException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 扩展点执行器
 * COLA架构：根据业务场景路由到对应的扩展点实现
 *
 * JDK 21 Features:
 * - Stream.toList() - Java 16+ 特性，直接收集为不可变列表
 * - Lambda 表达式增强
 *
 * 使用示例：
 * <pre>
 * &#64;Autowired
 * private ExtensionExecutor extensionExecutor;
 *
 * // 根据业务场景自动选择扩展点
 * extensionExecutor.execute(UserValidatorExtPt.class,
 *     BizScenario.valueOf("VIP", "register"),
 *     ext -> ext.validate(user));
 * </pre>
 */
@Slf4j
@Component
public class ExtensionExecutor {

    /**
     * 扩展点实现缓存
     * Key: 扩展点接口类名
     * Value: List<扩展点实现>
     */
    private final Map<Class<?>, List<ExtensionCoordinate>> extensionCache = new ConcurrentHashMap<>();

    private final ApplicationContextHelper applicationContextHelper;

    public ExtensionExecutor(ApplicationContextHelper applicationContextHelper) {
        this.applicationContextHelper = applicationContextHelper;
    }

    /**
     * 执行扩展点
     *
     * @param extensionPoint 扩展点接口类
     * @param bizScenario    业务场景
     * @param executor       执行逻辑
     * @param <T>            扩展点类型
     * @return 扩展点执行结果
     */
    public <T extends ExtensionPointI, R> R execute(
            Class<T> extensionPoint,
            BizScenario bizScenario,
            ExtensionInvoker<T, R> executor) {

        // 获取扩展点实现
        T extension = getExtension(extensionPoint, bizScenario);

        if (extension == null) {
            throw new SysException("EXTENSION_NOT_FOUND",
                    "未找到扩展点实现: " + extensionPoint.getSimpleName() + ", 场景: " + bizScenario);
        }

        log.debug("执行扩展点: interface={}, scenario={}, impl={}",
                extensionPoint.getSimpleName(), bizScenario, extension.getClass().getSimpleName());

        return executor.invoke(extension);
    }

    /**
     * 执行扩展点（无返回值）
     */
    public <T extends ExtensionPointI> void executeVoid(
            Class<T> extensionPoint,
            BizScenario bizScenario,
            ExtensionInvokerVoid<T> executor) {

        T extension = getExtension(extensionPoint, bizScenario);

        if (extension == null) {
            throw new SysException("EXTENSION_NOT_FOUND",
                    "未找到扩展点实现: " + extensionPoint.getSimpleName() + ", 场景: " + bizScenario);
        }

        log.debug("执行扩展点: interface={}, scenario={}, impl={}",
                extensionPoint.getSimpleName(), bizScenario, extension.getClass().getSimpleName());

        executor.invoke(extension);
    }

    /**
     * 获取扩展点实现
     */
    @SuppressWarnings("unchecked")
    public <T extends ExtensionPointI> T getExtension(Class<T> extensionPoint, BizScenario bizScenario) {
        // 从缓存获取
        List<ExtensionCoordinate> extensions = extensionCache.computeIfAbsent(
                extensionPoint,
                k -> locateExtensions(k)
        );

        // 查找匹配的扩展点
        for (ExtensionCoordinate extension : extensions) {
            if (extension.match(bizScenario)) {
                return (T) extension.getExtensionImpl();
            }
        }

        // 如果没有找到，尝试查找默认实现
        for (ExtensionCoordinate extension : extensions) {
            if (extension.isDefault()) {
                log.debug("使用默认扩展点: interface={}, impl={}",
                        extensionPoint.getSimpleName(), extension.getExtensionImpl().getClass().getSimpleName());
                return (T) extension.getExtensionImpl();
            }
        }

        return null;
    }

    /**
     * 定位扩展点实现
     *
     * JDK 21 Feature: Stream.toList() - Java 16+ 方法
     * 替代 .collect(Collectors.toList())
     */
    @SuppressWarnings("unchecked")
    private List<ExtensionCoordinate> locateExtensions(Class<?> extensionPoint) {
        // 获取所有实现该扩展点接口的Bean
        Map<String, ?> extensions = applicationContextHelper.getBeansOfType(
                (Class<? extends ExtensionPointI>) extensionPoint
        );

        // JDK 21: 使用 toList() 替代 collect(Collectors.toList())
        return extensions.values().stream()
                .filter(bean -> bean.getClass().isAnnotationPresent(Extension.class))
                .map(bean -> {
                    Extension annotation = bean.getClass().getAnnotation(Extension.class);
                    return new ExtensionCoordinate(
                            annotation.bizId(),
                            annotation.useCase(),
                            annotation.scenario(),
                            (ExtensionPointI) bean
                    );
                })
                .toList();
    }

    /**
     * 扩展点坐标
     */
    @lombok.Data
    @lombok.AllArgsConstructor
    static class ExtensionCoordinate {
        private final String bizId;
        private final String useCase;
        private final String scenario;
        private final ExtensionPointI extensionImpl;

        boolean match(BizScenario bizScenario) {
            if (bizScenario == null) {
                return isDefault();
            }
            return bizScenario.matches(normalize(bizId), normalize(useCase), normalize(scenario));
        }

        boolean isDefault() {
            return "DEFAULT".equals(bizId);
        }

        String normalize(String value) {
            return (value == null || value.isEmpty()) ? null : value;
        }
    }

    /**
     * 扩展点调用器（有返回值）
     */
    @FunctionalInterface
    public interface ExtensionInvoker<T extends ExtensionPointI, R> {
        R invoke(T extension);
    }

    /**
     * 扩展点调用器（无返回值）
     */
    @FunctionalInterface
    public interface ExtensionInvokerVoid<T extends ExtensionPointI> {
        void invoke(T extension);
    }
}
