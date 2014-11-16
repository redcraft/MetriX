package net.redcraft.metrix.di;

import com.dropbox.core.DbxClient;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.name.Names;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;
import com.mycila.guice.ext.closeable.CloseableInjector;
import com.mycila.guice.ext.closeable.CloseableModule;
import com.mycila.guice.ext.jsr250.Jsr250Module;
import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;
import net.redcraft.metrix.auth.AuthFilter;
import net.redcraft.metrix.AccountAPI;
import net.redcraft.metrix.GenericExceptionMapper;
import net.redcraft.metrix.auth.AuthAPI;
import net.redcraft.metrix.node.RunnableNode;

import javax.servlet.ServletContextEvent;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Executor;


/**
 * {@link com.google.inject.Guice} dependency injection config
 *
 * @author Maxim Gurkin <redmax3d@gmail.com>
 */
public class DIConfig extends GuiceServletContextListener {

	private Injector injector;
	private static final String HTTPS_ONLY = "HTTPS_ONLY";
	private final List<String> PROPERTY_LIST = ImmutableList.of(
			"FIXED_THREAD_POOL_SIZE", "API_KEY", "API_SECRET");

	private Properties getProperties() {
		Properties properties = new Properties();
		for (String key : PROPERTY_LIST) {
			String value = System.getenv(key);
			if (value != null) {
				properties.setProperty(key, value);
			} else {
				throw new RuntimeException(String.format("Property %s can't be found in environment", key));
			}
		}
		String httpsOnly = System.getenv(HTTPS_ONLY);
		properties.setProperty(HTTPS_ONLY, httpsOnly != null ? httpsOnly : "false" );
		return properties;
	}

	@Override
	protected Injector getInjector() {
		injector = Guice.createInjector(new CloseableModule(), new Jsr250Module(), new ServletModule() {

			@Override
			protected void configureServlets() {
				filter("/api/*").through(AuthFilter.class);
				serve("/api/*").with(GuiceContainer.class, ImmutableMap.of(JSONConfiguration.FEATURE_POJO_MAPPING, "true"));
			}
		}, new AbstractModule() {
			@Override
			protected void configure() {

				Names.bindProperties(binder(), getProperties());

				bind(GenericExceptionMapper.class);

				bind(Executor.class).toProvider(FixedThreadPoolProvider.class);

				bind(DbxClient.class).toProvider(DbxClientProvider.class);
				bind(RunnableNode.class).toProvider(RootNodeProvider.class);

				bind(AuthAPI.class);
				bind(AccountAPI.class);
			}

		});
		return injector;
	}

	@Override
	public void contextDestroyed(ServletContextEvent servletContextEvent) {
		injector.getInstance(CloseableInjector.class).close();
		super.contextDestroyed(servletContextEvent);
	}
}
