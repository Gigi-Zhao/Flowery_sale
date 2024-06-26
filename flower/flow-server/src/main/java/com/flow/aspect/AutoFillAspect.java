package com.flow.aspect;

import com.flow.annotation.AutoFill;
import com.flow.constant.AutoFillConstant;
import com.flow.context.BaseContext;
import com.flow.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * 自定义切面，实现公共字段自动填充处理逻辑
 */
@Aspect        // 切面
@Component     // bean, 交给spring容器管理
@Slf4j         // 注释
public class AutoFillAspect {
    /**
     * 切入点
     */
    // 切点表达式：对哪些类的哪些方法实施拦截
    // 1. 第一个*：所有返回值，后面的.*：所有类、所有方法
    // 2. @annotation表示只作用在有 AutoFill 注解的类/方法上，而非mapper的所有方法上
    @Pointcut("execution(* com.flow.mapper.*.*(..)) && @annotation(com.flow.annotation.AutoFill)")
    public void autoFillPointCut(){}

    /**
     * 前置通知：在通知中进行公共字段的赋值
     * 因为需要先填充，后进行SQL操作
     */
    @Before("autoFillPointCut()")
    public void autoFill(JoinPoint joinPoint){     // 传入连接点：目标方法

        log.info("开始进行公共字段填充...");

        //1. 获取到当前被拦截的方法上的数据库操作类型
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();  // 方法签名对象
        AutoFill autoFill= signature.getMethod().getAnnotation(AutoFill.class);  // 获得方法上的注解对象
        OperationType operationType= autoFill.value();                           // 获得数据库操作类型

        //2. 获取到当前被拦截的方法的参数 -- 实体对象（如Employee）
        Object[] args = joinPoint.getArgs();
        if(args == null || args. length == 0){
            return;
        }

        Object entity = args[0];     // 指定为第一个参数

        //3. 根据当前不同的操作类型,为对应的属性通过反射来赋值（因为实体类不确定，所以只能用反射来动态且通用地获取实体类的函数）

        //准备赋值的数据
        LocalDateTime now = LocalDateTime.now();
        Long currentId = BaseContext.getCurrentId();

        // 使用反射赋值
        if(operationType == OperationType.INSERT){
            //为4个公共字段赋值
            try {
                Method setCreateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
                Method setCreateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

                //通过反射为对象属性赋值
                setCreateTime.invoke(entity, now);
                setCreateUser.invoke(entity, currentId);
                setUpdateTime.invoke(entity, now);
                setUpdateUser.invoke(entity, currentId);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }else if(operationType == OperationType.UPDATE) {
            //为2个公共字段赋值
            try {
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

                //通过反射为对象属性赋值
                setUpdateTime.invoke(entity, now);
                setUpdateUser.invoke(entity, currentId);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
