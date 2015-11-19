package net.schwabe.tardis.config;

import java.util.Collections;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;

/*
 * This is the main configuration file for the dropwizard application.
 */
public class AppConfig extends Configuration {

	
	// Custom settings for this app
	@JsonProperty("settings")
	private SettingsConfig settings = new SettingsConfig();
	
    @JsonProperty("settings")
    public SettingsConfig getSettings() {
        return settings;
    }    

    // Swagger API documentation
    @JsonProperty("swagger")
    public SwaggerBundleConfiguration swaggerBundleConfiguration;

    // database configuration
    @Valid
    @NotNull
    private DataSourceFactory database = new DataSourceFactory();

    @JsonProperty("database")
    public void setDataSourceFactory(DataSourceFactory factory) {
        this.database = factory;
    }

    @JsonProperty("database")
    public DataSourceFactory getDataSourceFactory() {
        return database;
    }    

}