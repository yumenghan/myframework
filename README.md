1.概述

本项目是仿照<<架构探险-从零开始写Java Web框架>>这本书实现的一个类似于Spring的框架，实现了IOC容器、依赖注入、AOP的功能。

一开始写这个小的framework是为了学习Spring的原理，在使用Spring的过程中对Spring是如何实现的IOC和AOP不理解，在阅读学习Spring源码的过程中了解到了IOC容器的加载过程，和Bean的实例化过程，但是由于Spring的源码太多的细节，调用的层次又特别深，所以才有了自己实现一个小的IOC框架的想法，实现最基本的IOC和AOP还有MVC的功能，旨在提高自己对IOC和AOP的理解。

开发大致分为以下几个过程：

1. 类加载器开发
2. 实现一个简单的IOC容器
3. 实现依赖注入
4. 实现mvc功能

2.类加载器的开发

目的：需要开发一个类加载器来加载该基础包名下的所有类，比如使用了某注解的类

1.功能

1. 加载指定的类
2. 获取指定包名下的所有类

比较复杂的是获取指定包名下的所有类，需要根据传入包名并将其转换为文件路径，读取class文件或jar包，获取指定的类名去加载类，并将加载好的类放入一个Set中进行保存

代码：com.myframework.util.ClassUtil

3.注解开发

目的：仿照Spring，在控制器类上使用Controller注解，在控制器类的方法上使用Action注解，根据请求调用方法进行处理，在服务类上使用Service注解，并使用Inject注解将所依赖的类注入进来

代码：com.myframework.annotation

4.实现IOC容器

目的：现在实现了自己的类加载器，能够获取指定包名下的class文件并加载进JVM中，但是还没有进行实例化，所以需要实例化对象保存到容器中

1.反射工具类

在开发IOC容器的时候需要一个工具类实现反射实例化对象，所以对JDK提供的反射API做了一个简单的包装，根据传入的Class对象反射生成Object对象（Bean对象），并且需要能够对成员变量进行反射赋值操作，以便实现后面的依赖注入功能。

2.IOC容器

1. 首先需要定义一个Map保存Bean类与Bean实例的映射关系
2. 然后我们需要在加载IOC容器的时候初始化Bean实例，所以在BeanHelper写了一个static块，以便能够在启动IOC容器时，将所有的Bean创建完成
3. 我们也仿照Spring的BeanFactory接口，可以通过传入特定的Class对象返回一个实例，所以也需要实现一个getBean的方法

代码：com.myframework.helper.BeanHelper

5.实现依赖注入功能

在使用Spring进行开发的过程中，不是开发者自己通过new的方式来实例化对象的，而是通过框架来对对象的创建和依赖进行管理，称为IOC，依赖注入作为一种实现IOC的手段，可以将一个对象所需要的类动态的注入到对象中。

1. 先通过之前定义好的BeanHelper容器获取所有的BeanMap（在IOC容器中定义的属性，保存Bean类与Bean实例的映射关系）
2. 遍历这个映射关系，分别取出Bean类与Bean实例
3. 通过反射获取类中所有成员变量，继续遍历这些成员变量，在循环中判断当前成员是否带有Inject注解
4. 如果有，则从Bean Map中根据Bean类取出Bean实例，最后通过反射工具类中定义的方法修改当前成员变量的值

代码：com.myframework.helper.IocHelper



6.实现MVC功能

目的：获取请求方法和请求路径，然后根据请求的方法找到实际的Handler对象进行处理

在Spring中，是通过一个DispatcherServlet，接收处理所有请求，通过HandlerMapping找到对应的HandlerAdapter，然后调用Handler处理请求。

而自己的MVC实现的思路是：

1. 先获取所有Controller注解的类，然后通过反射获取该类中所有带有Action注解的方法
2. 获取Action注解中的表达式，进而获取请求方法和请求路径进行封装
3. 最后将Request与Handler建立一个映射关系，放入map中

1.Request

在Request对象中定义两个属性，一个是requestMethod请求方法，另一个是requestPath请求路径。

代码：com.myframework.bean

2.Handler

在handler中需要定义两个属性，一个是controllerClass，控制器类的class对象，另一个是actionMethod，

代码：com.myframework.bean

3.ControllerHelper

在Helper中定义属性Map，用于存放请求与处理器的映射关系，接收请求后根据map中的映射查找相应的handler进行处理。

1. 首先在初始化ControllerHelper时，需要完成map中的映射，将初始化map的代码放在static块中
2. 获取所有的controller的class对象，遍历这些对象中定义的方法，查看是否带有Action注解
3. 若有，则从Action注解中获取请求的路径和方法信息，封装成Request对象
4. 将Request对象与Handler放入MAP中

代码：com.myframework.helper

4.请求转发器

类似DispatcherServlet的功能，用来处理所有的请求

1.Param

先从HttpServletRequest对象中获取所有的请求参数，并将其初始化到一个名为Param的对象中

代码：com.myframework.bean

2.View

可以从Handler对象中获取Action方法的返回值，该返回值有可能有两种情况：

1. 若返回值是View类型的视图对象，则返回一个jsp页面
2. 若返回值是一个Data类型的数据对象，返回一个JSON数据

代码：com.myframework.bean

3.请求转发器

1. 在service方法中，根据请求的方法和请求的路径获取handler对象
2. 从request对象中获取参数并封装到map中
3. 调用handler的action方法
4. 根据返回结果判断是JSP页面还是数据，分别进行处理

7.实现AOP

概述：在spring中，定义一个切面Aspect类来编写需要横切业务逻辑的代码，使得业务逻辑能够与系统功能分离，在切面中写上通知advice和切入点表达式就能够实现AOP的功能了，所以目标是仿照Spring AOP + Aspect 的风格实现一个自己的AOP功能

1.搭建代理框架

    public interface Proxy{
    	
    	Object doProxy(ProxyChain proxyChain);
    }

这个Proxy接口中包含了一个doProxy方法，传入一个ProxyChain用于执行链式代理，可以将多个代理通过一条链子串起来，一个个的去执行。

ProxyChain：

在ProxyChain中，定义了一系列成员变量：

1. targetClass 目标类
2. targetObject 目标对象
3. targetMethod 目标方法
4. targetProxy 方法代理
5. methodParams 方法参数
6. methodProxy 方法代理
7. proxyList 代理列表
8. proxyIndex 代理索引

还定义了一个doProxyChain方法，在该方法中，通过proxyIndex来充当代理对象的计数器，若尚未达到ProxyList上限，则从list中取出相应的Proxy对象，并调用其中doProxy方法，在Proxy接口的实现中会提供相应的横切逻辑

代码：com.myframework.proxy

2.ProxyManager 代理对象的创建

目标：通过输入一个目标类和一组Proxy接口的实现，输出一个代理对象

我们使用CGLIB提供的Enhancer对象来创建代理对象，将Intercept的参数传入ProxyChain的构造器返回即可。

3.切面类

目标：在切面类中，需要在目标方法被调用的前后增加相应的逻辑，所以必须要写一个抽象类，并提供一个模板方法，并在抽象类具体实现中扩展相应的抽象方法

    public abstract class AspectProxy implements Proxy{
        public final Object doProxy(ProxyChain proxyChain){
            Object result = null;
            Class cls = proxyChain.getTargetClass();
            Method method = proxyChain,getTargetMethod();
            Object[] params = proxyChain.getMethodParams();
            begin();
            try{
    		if (intercept(cls, method , params)){
                before(cls, method , params);
                result = proxyChain.doProxyChain();
                after(cls, method, params, result);
    		}else {
                result = proxyChain.doProxyChain();
    		}catch(Exception e){
                error(cls, method, params, e);
    		}finally{
                end();
    		}
    		return result;
            }
        }
    }

需要注意的是doProxy方法，我们从proxyChain参数中获取了目标类、目标方法与目标参数，随后通过一个try...catch....finally代码块实现调用框架，从框架中抽象出一系列的钩子方法，这些抽象方法可以在子类中有选择的进行实现

4.AopHelper

目标：需要获取代理类和目标类之间的映射关系，一个代理类可以对应一个或者多个目标类

代码：com.myframework.helper


