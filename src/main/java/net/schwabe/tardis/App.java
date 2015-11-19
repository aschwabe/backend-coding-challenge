/**
 * 
 */
package net.schwabe.tardis;

import org.skife.jdbi.v2.DBI;

import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.jdbi.DBIFactory;
import io.dropwizard.setup.*;
import io.federecio.dropwizard.swagger.SwaggerBundle;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;
import net.schwabe.tardis.config.*;
import net.schwabe.tardis.health.DatabaseHealthCheck;
import net.schwabe.tardis.resources.ExpenseResource;

/**
 * @author aschwabe@gmail.com
 * Project for AlchemyTec
 * Date: 18-Nov-2015
 */
public class App extends Application<AppConfig> {

	/*
	 * main:  this runs when the dropwizard app is executed from the jar
	 */
	public static void main(String[] args) throws Exception {
        new App().run(args);
    }

	/*
	 * (non-Javadoc)
	 * @see io.dropwizard.Application#initialize(io.dropwizard.setup.Bootstrap)
	 */
	@Override
    public void initialize(Bootstrap<AppConfig> bootstrap) {

		// Configure static content to be served from /static
		bootstrap.addBundle(new AssetsBundle("/static", "/static", "default.html"));		 	
	
		// swagger api documentation
    	bootstrap.addBundle(new SwaggerBundle<AppConfig>() {
            @Override
            protected SwaggerBundleConfiguration getSwaggerBundleConfiguration(AppConfig configuration) {
                return configuration.swaggerBundleConfiguration;
            }
        });
	}
	
	/*
	 * (non-Javadoc)
	 * @see io.dropwizard.Application#run(io.dropwizard.Configuration, io.dropwizard.setup.Environment)
	 */
	@Override
	public void run(AppConfig conf, Environment env)	throws Exception {
		
		// setup the database connection
		final DBIFactory factory = new DBIFactory();
	    final DBI db = factory.build(env, conf.getDataSourceFactory(), "mysql");
		
	    // health checks
	    final DatabaseHealthCheck mysql = new DatabaseHealthCheck(db);
	    env.healthChecks().register("mysql", mysql);
	    
	    // set all resources to be mapped to /expenses URI prefix 
		env.jersey().setUrlPattern("/expenses/*");
		
		// expenses api 
    	env.jersey().register(new ExpenseResource(conf, db));
	
	}

}
