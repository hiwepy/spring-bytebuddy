package org.springframework.bytebuddy.bytecode;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import org.apache.commons.lang3.builder.Builder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.bytebuddy.bytecode.definition.MvcBound;
import org.springframework.bytebuddy.bytecode.definition.MvcMapping;
import org.springframework.bytebuddy.bytecode.definition.MvcMethod;
import org.springframework.bytebuddy.bytecode.definition.MvcParam;
import org.springframework.bytebuddy.utils.EndpointApiUtils;
import org.springframework.web.bind.annotation.RequestMethod;

import com.github.vindell.javassist.bytecode.CtAnnotationBuilder;
import com.github.vindell.javassist.bytecode.CtFieldBuilder;
import com.github.vindell.javassist.utils.ClassPoolFactory;
import com.github.vindell.javassist.utils.JavassistUtils;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewConstructor;
import javassist.NotFoundException;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;

/**
 * 动态构建Controller接口
 */
public class EndpointApiCtClassBuilder implements Builder<Class<? extends Object>> {
	
	// 构建动态类
	protected ClassPool pool = null;
	protected CtClass declaring  = null;
	protected ClassFile classFile = null;
	//private Loader loader = new Loader(pool);
	
	public EndpointApiCtClassBuilder(final String classname) throws CannotCompileException, NotFoundException  {
		this(ClassPoolFactory.getDefaultPool(), classname);
	}
	
	public EndpointApiCtClassBuilder(final ClassPool pool, final String classname) throws CannotCompileException, NotFoundException {
		
		this.pool = pool;
		this.declaring = EndpointApiUtils.makeClass(pool, classname);
		
		/* 获得 JaxwsHandler 类作为动态类的父类 */
		CtClass superclass = pool.get(EndpointApi.class.getName());
		declaring.setSuperclass(superclass);
		
		// 默认添加无参构造器  
		declaring.addConstructor(CtNewConstructor.defaultConstructor(declaring));
		
		this.classFile = this.declaring.getClassFile();
		
	}
	
	/**
	 * 添加类注解 @Api
	 * @param tags
	 * @return
	 */
	public EndpointApiCtClassBuilder api(String... tags) {
		
		if(tags != null && tags.length > 0) {
			ConstPool constPool = this.classFile.getConstPool();
			JavassistUtils.addClassAnnotation(declaring, EndpointApiUtils.annotApi(constPool, tags));
		}
		
		return this;
	}

	/**
	 * 添加类注解  @ApiIgnore
	 * @return
	 */
	public EndpointApiCtClassBuilder apiIgnore() {
		
		ConstPool constPool = this.classFile.getConstPool();
		JavassistUtils.addClassAnnotation(declaring, EndpointApiUtils.annotApiIgnore(constPool, "Ignore"));
		
		return this;
	}
	
	/**
	 * 添加类注解 @Controller
	 * @param name Controller名称：必须唯一
	 * @return
	 */
	public EndpointApiCtClassBuilder controller(String name) {
		ConstPool constPool = this.classFile.getConstPool();
		JavassistUtils.addClassAnnotation(declaring, EndpointApiUtils.annotController(constPool, name));
		return this;
	}
	
	/**
	 * 添加类注解 @RestController
	 * @param name Controller名称：必须唯一
	 * @return
	 */
	public EndpointApiCtClassBuilder restController(String name) {
		
		ConstPool constPool = this.classFile.getConstPool();
		JavassistUtils.addClassAnnotation(declaring, EndpointApiUtils.annotRestController(constPool, name));
		
		return this;
	}
	
	/**
	 * 添加类注解 @RequestMapping
	 * @return
	 */
	public EndpointApiCtClassBuilder requestMapping(MvcMapping mapping) {

		ConstPool constPool = this.classFile.getConstPool();
		JavassistUtils.addClassAnnotation(declaring, EndpointApiUtils.annotRequestMapping(constPool, mapping));

		return this;
	}

	/**
	 * 添加类注解 @RequestMapping
	 * @return
	 */
	public EndpointApiCtClassBuilder requestMapping(String name, String[] path, RequestMethod[] method,
			String[] params, String[] headers, String[] consumes, String[] produces) {

		ConstPool constPool = this.classFile.getConstPool();
		JavassistUtils.addClassAnnotation(declaring, EndpointApiUtils.annotRequestMapping(constPool, name, path, method,
				params, headers, consumes, produces));

		return this;
	}
	
	/**
	 * 添加字段注解 @Autowired 实现对象注入
	 * @param type
	 * @param name
	 * @param required 	： Declares whether the annotated dependency is required.
	 * @param qualifier ： 
	 * @return
	 * @throws CannotCompileException 
	 * @throws NotFoundException 
	 */
	public <T> EndpointApiCtClassBuilder autowired(Class<T> type, String name, boolean required) throws CannotCompileException, NotFoundException {
		
		ConstPool constPool = this.classFile.getConstPool();
		
		// 属性字段
        CtField field = new CtField(pool.get(type.getName()), name, declaring);
        field.setModifiers(Modifier.PRIVATE);

        // 在属性上添加注解(Autowired)
        CtAnnotationBuilder.create(Autowired.class, constPool).addBooleanMember("required", required).markField(field);

        //新增Field
        declaring.addField(field);

		return this;
	}
	
	/**
	 * 添加字段注解 @Autowired @Qualifier 实现对象注入
	 * @param type
	 * @param name
	 * @param required 	： Declares whether the annotated dependency is required.
	 * @param qualifier ： 
	 * @return
	 * @throws CannotCompileException 
	 * @throws NotFoundException 
	 */
	public <T> EndpointApiCtClassBuilder autowired(Class<T> type, String name, boolean required, String qualifier) throws CannotCompileException, NotFoundException {
		
		ConstPool constPool = this.classFile.getConstPool();
		
		// 属性字段
        CtField field = new CtField(pool.get(type.getName()), name, declaring);
        field.setModifiers(Modifier.PRIVATE);

        // 在属性上添加注解(Autowired)
        CtAnnotationBuilder.create(Autowired.class, constPool).addBooleanMember("required", required).markField(field);
        
        // 在属性上添加注解(Qualifier)
        CtAnnotationBuilder.create(Qualifier.class, constPool).addStringMember("value", qualifier).markField(field);
        
        //新增Field
        declaring.addField(field);

		return this;
	}
	
	
	/**
	 * 通过给动态类增加 <code>@WebBound</code>注解实现，数据的绑定
	 */
	public EndpointApiCtClassBuilder bind(final String uid, final String json) {
		return bind(new MvcBound(uid, json));
	}
	
	/**
	 * 通过给动态类增加 <code>@WebBound</code>注解实现，数据的绑定
	 */
	public EndpointApiCtClassBuilder bind(final MvcBound bound) {

		ConstPool constPool = this.classFile.getConstPool();
		Annotation annot = EndpointApiUtils.annotWebBound(constPool, bound);
		JavassistUtils.addClassAnnotation(declaring, annot);
		
		return this;
	}
	
	/**
     * Compiles the given source code and creates a field.
     * Examples of the source code are:
     * 
     * <pre>
     * "public String name;"
     * "public int k = 3;"</pre>
     *
     * <p>Note that the source code ends with <code>';'</code>
     * (semicolon).
     *
     * @param src               the source text.
     */
	public <T> EndpointApiCtClassBuilder makeField(final String src) throws CannotCompileException {
		//创建属性
        declaring.addField(CtField.make(src, declaring));
		return this;
	}
	
	public <T> EndpointApiCtClassBuilder newField(final Class<T> fieldClass, final String fieldName, final String fieldValue) throws CannotCompileException, NotFoundException {
		CtFieldBuilder.create(declaring, this.pool.get(fieldClass.getName()), fieldName, fieldValue);
		return this;	
	}
	
	public <T> EndpointApiCtClassBuilder removeField(final String fieldName) throws NotFoundException {
		
		// 检查字段是否已经定义
		if(!JavassistUtils.hasField(declaring, fieldName)) {
			return this;
		}
		
		declaring.removeField(declaring.getDeclaredField(fieldName));
		
		return this;
	}

	/**
	 * @param methodName   	： 方法名称
	 * @param path   		： 发布地址
	 * @param method 		： 请求方式(GET/POST)
	 * @param contentType 	： 响应类型及编码
	 * @param bound
	 * @param params
	 * @return
	 * @throws CannotCompileException
	 * @throws NotFoundException
	 */
	public <T> EndpointApiCtClassBuilder newMethod(String methodName, String path, RequestMethod method, String contentType,
			MvcBound bound, MvcParam<?>... params) throws CannotCompileException, NotFoundException {
		
		//ResponseEntity.class
		
		ConstPool constPool = this.classFile.getConstPool();
		// 创建方法
		CtClass returnType = pool.get(Object.class.getName());
		CtMethod ctMethod = null;
		// 方法参数
		CtClass[] parameters = EndpointApiUtils.makeParams(pool, params);
		// 有参方法
		if(parameters != null && parameters.length > 0) {
			ctMethod = new CtMethod(returnType, methodName, parameters, declaring);
		} 
		// 无参方法 
		else {
			ctMethod = new CtMethod(returnType, methodName , null, declaring);
		}
        // 设置方法体
        EndpointApiUtils.methodBody(ctMethod, methodName);
        // 设置方法异常捕获逻辑
        EndpointApiUtils.methodCatch(pool, ctMethod);
        // 为方法添加  @GetMapping | @PostMapping | @PutMapping | @DeleteMapping | @PatchMapping 注解
        EndpointApiUtils.methodAnnotations(ctMethod, constPool, path, method, contentType, bound, params);
        
        //新增方法
        declaring.addMethod(ctMethod);
        
        return this;

	}
		
	
	/**
	 * 
	 * 根据参数构造一个新的方法
	 * @param rtClass ：返回对象类型
	 * @param method ：方法注释信息
	 * @param bound  ：方法绑定数据信息
	 * @param params ： 参数信息
	 * @return
	 * @throws CannotCompileException
	 * @throws NotFoundException 
	 */ 
	public <T> EndpointApiCtClassBuilder newMethod(final Class<T> rtClass, final MvcMethod method, final MvcBound bound, MvcParam<?>... params) throws CannotCompileException, NotFoundException {
	       
		ConstPool constPool = this.classFile.getConstPool();
		
		// 创建抽象方法
		CtClass returnType = rtClass != null ? pool.get(rtClass.getName()) : CtClass.voidType;
		CtMethod ctMethod = null;
		// 方法参数
		CtClass[] parameters = EndpointApiUtils.makeParams(pool, params);
		// 有参方法
		if(parameters != null && parameters.length > 0) {
			ctMethod = new CtMethod(returnType, method.getName(), parameters, declaring);
		} 
		// 无参方法 
		else {
			ctMethod = new CtMethod(returnType, method.getName() , null, declaring);
		}
        // 设置方法体
        EndpointApiUtils.methodBody(ctMethod, method);
        // 设置方法异常捕获逻辑
        EndpointApiUtils.methodBody(ctMethod, method);
        // @GetMapping | @PostMapping | @PutMapping | @DeleteMapping | @PatchMapping 注解
        EndpointApiUtils.methodAnnotations(ctMethod, constPool, method, bound, params);
        
        //新增方法
        declaring.addMethod(ctMethod);
        
        return this;
	}
	 
	public <T> EndpointApiCtClassBuilder removeMethod(final String methodName, MvcParam<?>... params) throws NotFoundException {
		
		// 有参方法
		if(params != null && params.length > 0) {
			
			// 方法参数
			CtClass[] parameters = EndpointApiUtils.makeParams(pool, params);
			
			// 检查方法是否已经定义
			if(!JavassistUtils.hasMethod(declaring, methodName, parameters)) {
				return this;
			}
			
			declaring.removeMethod(declaring.getDeclaredMethod(methodName, parameters));
			
		}
		else {
			
			// 检查方法是否已经定义
			if(!JavassistUtils.hasMethod(declaring, methodName)) {
				return this;
			}
			
			declaring.removeMethod(declaring.getDeclaredMethod(methodName));
			
		}
		
		return this;
	}
	
	@Override
	public Class<? extends Object> build() {
        return declaring;
	}
	
	/**
	 * 
	 * javassist在加载类时会用Hashtable将类信息缓存到内存中，这样随着类的加载，内存会越来越大，甚至导致内存溢出。如果应用中要加载的类比较多，建议在使用完CtClass之后删除缓存
	 * @author 		： <a href="https://github.com/vindell">vindell</a>
	 * @return
	 * @throws CannotCompileException
	 */
	public Class<?> toClass() throws CannotCompileException {
        try {
        	// 通过类加载器加载该CtClass
			return declaring.toClass();
		} finally {
			// 将该class从ClassPool中删除
			declaring.detach();
		} 
	}
	
	@SuppressWarnings("unchecked")
	public Object toInstance(final InvocationHandler handler) throws CannotCompileException, NotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
        try {
        	// 设置InvocationHandler参数构造器
			declaring.addConstructor(EndpointApiUtils.makeConstructor(pool, declaring));
			// 通过类加载器加载该CtClass，并通过构造器初始化对象
			return declaring.toClass().getConstructor(InvocationHandler.class).newInstance(handler);
		} finally {
			// 将该class从ClassPool中删除
			declaring.detach();
		} 
	}

}