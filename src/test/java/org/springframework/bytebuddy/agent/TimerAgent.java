package org.springframework.bytebuddy.agent;

import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.JavaModule;

public class TimerAgent {
	public static void premain(String arguments, Instrumentation instrumentation) {
		new AgentBuilder.Default()
				.type(ElementMatchers.nameEndsWith("Timed"))
				.transform(new AgentBuilder.Transformer() {
					@Override
					public DynamicType.Builder<?> transform(
							DynamicType.Builder<?> builder,
							TypeDescription typeDescription,
							ClassLoader classLoader,
							JavaModule module,
							ProtectionDomain protectionDomain) {
						return builder.method(ElementMatchers.any())
								.intercept(MethodDelegation.to(TimingInterceptor.class));
					}
				})
				.installOn(instrumentation);
	}
}
