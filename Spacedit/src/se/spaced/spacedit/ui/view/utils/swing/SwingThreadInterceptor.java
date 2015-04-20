package se.spaced.spacedit.ui.view.utils.swing;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.awt.EventQueue;

public class SwingThreadInterceptor implements MethodInterceptor {
	@Override
	public Object invoke(final MethodInvocation methodInvocation) throws Throwable {
		if (!EventQueue.isDispatchThread()) {
			final Object[] result = new Object[1];

			EventQueue.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					try {
						result[0] = methodInvocation.proceed();
					} catch (Throwable throwable) {
						throwable.printStackTrace();
					}
				}
			});

			return result[0];

		} else {
			return methodInvocation.proceed();
		}
	}
}
