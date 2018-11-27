package org.springframework.bytebuddy;


import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.function.Function;

import org.junit.Test;
import org.springframework.bytebuddy.intercept.GreetingInterceptor;
import org.springframework.bytebuddy.intercept.LoggerInterceptor;
import org.springframework.bytebuddy.intercept.MemoryDatabase;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;

@SuppressWarnings("unchecked")
public class ByteBuddy_Test {

	@Test
	public void test1() throws InstantiationException, IllegalAccessException {

		Class<?> dynamicType = new ByteBuddy().subclass(Object.class).method(ElementMatchers.named("toString"))
				.intercept(FixedValue.value("Hello World!")).make().load(getClass().getClassLoader()).getLoaded();

		assertThat(dynamicType.newInstance().toString(), is("Hello World!"));

	}

	@Test
	public void test2() throws InstantiationException, IllegalAccessException {

		Class<? extends Function> dynamicType = new ByteBuddy()
				.subclass(java.util.function.Function.class)
				.method(ElementMatchers.named("apply"))
				.intercept(MethodDelegation.to(new GreetingInterceptor())).make()
				.load(getClass().getClassLoader()).getLoaded();
		
		assertThat((String) dynamicType.newInstance().apply("Byte Buddy"), is("Hello from Byte Buddy"));

	}

}