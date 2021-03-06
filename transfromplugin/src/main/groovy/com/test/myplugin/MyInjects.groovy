package com.test.myplugin

import javassist.ClassPool
import javassist.CtClass
import javassist.CtMethod
import javassist.bytecode.AnnotationsAttribute
import javassist.bytecode.MethodInfo
import org.gradle.api.Project

import java.lang.annotation.Annotation;

/**
 * Created by 刘镓旗 on 2017/8/31.
 */

public class MyInjects {
    //初始化类池
    private final static ClassPool pool = ClassPool.getDefault();

    public static void inject(String path,Project project) {
        //将当前路径加入类池,不然找不到这个类
        pool.appendClassPath(path);
        //project.android.bootClasspath 加入android.jar，不然找不到android相关的所有类
        pool.appendClassPath(project.android.bootClasspath[0].toString());
        //引入android.os.Bundle包，因为onCreate方法参数有Bundle
        pool.importPackage("android.os.Bundle");

        File dir = new File(path);
        if (dir.isDirectory()) {
            //遍历文件夹
            dir.eachFileRecurse { File file ->
                String filePath = file.absolutePath
                println("filePath = " + filePath)
                if (file.getName().equals("MainActivity.class")) {

                    //获取MainActivity.class
                    CtClass ctClass = pool.getCtClass("com.test.myapplication.MainActivity");
                    println("ctClass = " + ctClass)
                    //解冻
                    if (ctClass.isFrozen())
                        ctClass.defrost()

                    //获取到OnCreate方法
                    CtMethod ctMethod = ctClass.getDeclaredMethod("onCreate")
                    //拿到所有注解
                    Object[] annotations = ctMethod.getAnnotations()
                    String insetBeforeStr = ""

                    if (annotations != null && annotations.length > 0){
                        MethodInfo methodInfo = currentMethod.getMethodInfo()
                        AnnotationsAttribute attribute = (AnnotationsAttribute) methodInfo.getAttribute(AnnotationsAttribute.visibleTag)
                        Annotation annotation = attribute.getAnnotation("Author")

                        if (annotation != null) {
                            //拿到注解的值
                            String name = annotation.getMemberValue("name")
                            String time = annotation.getMemberValue("time")
                            insetBeforeStr = " android.widget.Toast.makeText(this,"+name+time+",android.widget.Toast.LENGTH_SHORT).show();"
                            ""
                        }
                    }






                    println("方法名 = " + ctMethod)

//                    String insetBeforeStr = """ android.widget.Toast.makeText(this,"我是被插入的Toast代码~!!",android.widget.Toast.LENGTH_SHORT).show();
//                                                """

//                    String insetBeforeStr = " android.widget.Toast.makeText(this,"+name+time+",android.widget.Toast.LENGTH_SHORT).show();"
//                                                ""
                    //在方法开头插入代码
                    for (CtMethod method : ctClass.getDeclaredMethods()) {
                        //找到 onClick(View) 方法
                        if (checkOnClickMethod(method)) {
                            method.insertBefore(insetBeforeStr)
                        }
                    }

                    ctMethod.insertBefore(insetBeforeStr);
                    ctClass.writeFile(path)
                    ctClass.detach()//释放
                }
            }
        }

    }

    private static boolean checkOnClickMethod(CtMethod method) {
        return method.getName().endsWith("onClick") && method.getParameterTypes().length == 1 &&
                method.getParameterTypes()[0].getName().equals("android.view.View")
    }
}