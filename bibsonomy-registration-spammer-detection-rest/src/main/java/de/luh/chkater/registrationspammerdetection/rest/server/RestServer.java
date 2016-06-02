package de.luh.chkater.registrationspammerdetection.rest.server;

import java.io.BufferedInputStream;
import java.util.Collections;
import java.util.Properties;

import org.eclipse.jetty.security.ConstraintMapping;
import org.eclipse.jetty.security.ConstraintSecurityHandler;
import org.eclipse.jetty.security.HashLoginService;
import org.eclipse.jetty.security.LoginService;
import org.eclipse.jetty.security.authentication.BasicAuthenticator;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.security.Constraint;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;

import de.luh.chkater.registrationspammerdetection.rest.servlet.RegistrationSpammerDetectionServlet;

/**
 * Starts the REST server
 *
 * @author kater
 */
public class RestServer {

	/**
	 * 
	 */
	private static final String REGISTRATION_SPAMMER_DETECTION_GROUP = "registration-spammer-detection";

	public static void main(String[] args) throws Exception {
		Properties properties = new Properties();
		BufferedInputStream stream = new BufferedInputStream(RestServer.class.getResourceAsStream("/config.properties"));
		properties.load(stream);
		stream.close();
		for (String key : properties.stringPropertyNames()) {
			System.setProperty(key, properties.getProperty(key));
		}
		
		RegistrationSpammerDetectionServlet.rebuildClassifier();


		Server server = new Server(Integer.parseInt(System.getProperty("rest.port")));

		LoginService loginService = new HashLoginService("MyRealm", "src/main/resources/realm.properties");
		server.addBean(loginService);

		//SecurityHandler from https://github.com/eclipse/jetty.project/blob/master/examples/embedded/src/main/java/org/eclipse/jetty/embedded/SecuredHelloHandler.java
		ConstraintSecurityHandler security = new ConstraintSecurityHandler();
        server.setHandler(security);
		
        Constraint constraint = new Constraint();
        constraint.setName("auth");
        constraint.setAuthenticate(true);
        constraint.setRoles(new String[] { "user", "admin" });
        
        ConstraintMapping mapping = new ConstraintMapping();
        mapping.setPathSpec("/*");
        mapping.setConstraint(constraint);
        
        security.setConstraintMappings(Collections.singletonList(mapping));
        security.setAuthenticator(new BasicAuthenticator());
        security.setLoginService(loginService);
        
		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);

		context.setContextPath("/");
		
		ServletHolder jerseyServlet = context.addServlet(

				org.glassfish.jersey.servlet.ServletContainer.class, "/*");

		jerseyServlet.setInitOrder(0);

		jerseyServlet.setInitParameter(

				"jersey.config.server.provider.classnames",

				RegistrationSpammerDetectionServlet.class.getCanonicalName());
		
		security.setHandler(context);
		
		JobDetail job = JobBuilder.newJob(RebuildCron.class)
				.withIdentity("rebuild", REGISTRATION_SPAMMER_DETECTION_GROUP).build();

		Trigger trigger = TriggerBuilder
				.newTrigger()
				.withIdentity("rebuildTrigger", REGISTRATION_SPAMMER_DETECTION_GROUP)
				.withSchedule(
					CronScheduleBuilder.cronSchedule(System.getProperty("rebuild.cron")))
				.build();
		try {

			server.start();

			server.join();

		} finally {

			server.destroy();

		}
		
		
	}

}
