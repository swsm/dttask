package com.swsm.dttask.server.controller;

import com.swsm.dttask.common.exception.BusinessException;
import com.swsm.dttask.server.protocol.ProtocolManager;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author swsm
 * @date 2023-12-07
 */
@RestController
@Slf4j
public class ProtocolController {

    @Autowired
    private DefaultListableBeanFactory defaultListableBeanFactory;
    @Autowired
    private ProtocolManager protocolManager;

    @GetMapping("/flushProtocol")
    public void flushProtocol(@RequestParam(value = "jarAddress", required = false) String jarAddress) {
        if (jarAddress == null) {
            jarAddress = "D:\\workspaces\\dttask\\protocolJar\\dttask-protocol-simulator-1.0-SNAPSHOT.jar";
        }
        String jarPath = "file:/" + jarAddress;
        hotDeployWithSpring(jarAddress, jarPath);
    }

    /**
     * 加入jar包后 动态注册bean到spring容器，包括bean的依赖
     */
    public void hotDeployWithSpring(String jarAddress, String jarPath) {
        Set<String> classNameSet = readJarFile(jarAddress);
        try (URLClassLoader urlClassLoader = new URLClassLoader(new URL[]{new URL(jarPath)}, Thread.currentThread().getContextClassLoader())) {
            for (String className : classNameSet) {
                Class<?> clazz = urlClassLoader.loadClass(className);
                if (isSpringBeanClass(clazz)) {
                    BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(clazz);
                    String beanName = transformName(className);
                    defaultListableBeanFactory.registerBeanDefinition(beanName, beanDefinitionBuilder.getBeanDefinition());
                }
            }
        } catch (ClassNotFoundException e) {
            throw new BusinessException("hotDeployWithSpring ClassNotFoundException", e);
        } catch (MalformedURLException e) {
            throw new BusinessException("hotDeployWithSpring MalformedURLException", e);
        } catch (IOException e) {
            throw new BusinessException("hotDeployWithSpring IOException", e);
        } finally {
            protocolManager.refreshMap();
        }


    }

    /**
     * 读取jar包中所有类文件
     */
    public static Set<String> readJarFile(String jarAddress) {
        Set<String> classNameSet = new HashSet<>();
        Enumeration<JarEntry> entries;
        try (JarFile jarFile = new JarFile(jarAddress)) {
            //遍历整个jar文件
            entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry jarEntry = entries.nextElement();
                String name = jarEntry.getName();
                if (name.endsWith(".class")) {
                    String className = name.replace(".class", "").replace("/", ".");
                    classNameSet.add(className);
                }
            }
        } catch (IOException e) {
            log.error("readJarFile exception:", e);
            throw new BusinessException("readJarFile exception", e);
        }
        return classNameSet;
    }

    /**
     * 方法描述 判断class对象是否带有spring的注解
     */
    public static boolean isSpringBeanClass(Class<?> cla) {
        if (cla == null) {
            return false;
        }
        // 不是抽象类 接口 且 没有以下注解
        return (cla.getAnnotation(Component.class) != null
                || cla.getAnnotation(Repository.class) != null
                || cla.getAnnotation(Service.class) != null)
                && !Modifier.isAbstract(cla.getModifiers())
                && !cla.isInterface();
    }

    /**
     * 类名首字母小写 作为spring容器beanMap的key
     */
    public static String transformName(String className) {
        String tmpstr = className.substring(className.lastIndexOf(".") + 1);
        return tmpstr.substring(0, 1).toLowerCase() + tmpstr.substring(1);
    }


    /**
     * 删除jar包时 需要在spring容器删除注入
     */
    public void delete(String jarAddress, String jarPath) {
        Set<String> classNameSet = readJarFile(jarAddress);
        try (URLClassLoader urlClassLoader = new URLClassLoader(new URL[]{new URL(jarPath)},
                Thread.currentThread().getContextClassLoader())) {
            for (String className : classNameSet) {
                Class<?> clazz = urlClassLoader.loadClass(className);
                if (isSpringBeanClass(clazz)) {
                    defaultListableBeanFactory.removeBeanDefinition(transformName(className));
                }
            }
        } catch (MalformedURLException e) {
            throw new BusinessException("delete MalformedURLException", e);
        } catch (IOException | ClassNotFoundException e) {
            throw new BusinessException("delete IOException or ClassNotFoundException", e);
        } 
    }


}
