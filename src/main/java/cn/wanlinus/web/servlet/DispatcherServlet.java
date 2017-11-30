package cn.wanlinus.web.servlet;

import cn.wanlinus.web.annotation.Controller;
import cn.wanlinus.web.annotation.RequestMapping;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author wanli
 */
public class DispatcherServlet extends HttpServlet {
    private List<String> clzNames;
//    private Map<String, String> initConfig;

    @Override
    public void init() throws ServletException {
        Properties properties = new Properties();
        try {
            properties.load(DispatcherServlet.class.getClassLoader().getResourceAsStream("mvc.properties"));
            String clzPack = properties.getProperty("controller");
            clzNames = scan(clzPack);
            /*//遍历所有类获取Controller注解,将Controller注解的initParam信息存到map集合
            for (String s : clzNames) {
                Class clz = Class.forName(s);
                Object o = clz.newInstance();
                Controller c = o.getClass().getAnnotation(Controller.class);
                WebInitParam[] initParams = c.initParams();
                for (WebInitParam initParam : initParams) {
                    initConfig.put(initParam.name(), initParam.value());
                }
            }*/
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void service(HttpServletRequest request, HttpServletResponse response) {
        try {
            String ask = request.getRequestURI();
            //ask = ask.substring(ask.lastIndexOf("/"));//0.2version遗留代码

            //0.3 Version
            for (String s : clzNames) {
                Class clz = Class.forName(s);
                Object obj = clz.newInstance();
                Controller controller = obj.getClass().getAnnotation(Controller.class);
                if (controller != null) {
                    RequestMapping clzMappings = obj.getClass().getAnnotation(RequestMapping.class);
                    Method[] methods = clz.getDeclaredMethods();
                    RequestMapping methodMapping;
                    if (clzMappings == null) {
                        for (Method m : methods) {
                            methodMapping = m.getAnnotation(RequestMapping.class);
                            boolean methodFlag = false;
                            if (methodMapping != null) {
                                for (String ms : methodMapping.value()) {
                                    if (ask.endsWith(ms)) {
                                        m.invoke(obj, request, response);
                                        return;
                                    }
                                }
                            }
                        }
                    } else {
                        for (Method m : methods) {
                            methodMapping = m.getAnnotation(RequestMapping.class);
                            for (String cs : clzMappings.value()) {
                                if (methodMapping != null) {
                                    for (String ms : methodMapping.value()) {
                                        if (ask.endsWith(cs + ms)) {
                                            m.invoke(obj, request, response);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            //0.2 version
/*            for (String s : clzName) {
                Class clz = Class.forName(s);
                Annotation[] annotations = clz.getAnnotations();
                for (Annotation a : annotations) {
                    if (a instanceof Controller) {
                        Object obj = clz.newInstance();
                        Method[] methods = clz.getDeclaredMethods();
                        for (Method m : methods) {
                            RequestMapping methodMapping = m.getAnnotation(RequestMapping.class);
                            for (String s1 : methodMapping.value())
                                if (ask.equals(s1)) {
                                    m.invoke(obj, request, response);
                                }
                        }
                    }
                }
            }*/

            //0.1 Version
/*            Class clz = Class.forName(properties.getProperty("className"));
            Object obj = clz.newInstance();
            Method[] methods = clz.getDeclaredMethods();
            for (Method m : methods) {
                RequestMapping mapping = m.getAnnotation(RequestMapping.class);
                if (ask.equals(mapping.value())) {
                    m.invoke(obj, request, response);
                }
            }*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 扫描local包下面所有的类
     *
     * @param local 包路径
     * @return 类集合
     */
    private List<String> scan(String local) {
        List<String> classNames = new ArrayList<>();
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        try {
            String resourceName = local.replace(".", "/");
            URL url = loader.getResource(resourceName);
            File urlFile = new File((url.toURI()));
            File[] files = urlFile.listFiles();
            for (File f : files) {
                getClassName(local, f, classNames);
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return classNames;
    }

    /**
     * 给定指定包名,遍历此包下所有类添加到list
     *
     * @param packageName 给定包名,也可以是类名
     * @param packageFile 给定包下的文件
     * @param list        用于保存包的集合
     */
    private void getClassName(String packageName, File packageFile, List<String> list) {
        if (packageFile.isFile()) {
            list.add(packageName + "." + packageFile.getName().replace(".class", ""));
        } else {
            File[] files = packageFile.listFiles();
            String tmPackageName = packageName + "." + packageFile.getName();
            for (File f : files) {
                getClassName(tmPackageName, f, list);
            }
        }
    }
}
