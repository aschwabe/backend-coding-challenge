package net.schwabe.tardis.config;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.dropwizard.Configuration;

/*
 * This class implements custom settings for this dropwizard app.
 */
public class SettingsConfig extends Configuration {

	// The database table to use for expenses
	@NotNull
    private String expenseTable;

    public String getExpenseTable() {
        return expenseTable;
    } 

    // the maximum records to display on the UX 
    @NotNull
    private int maxRecords;

    public int getMaxRecords() {
        return maxRecords;
    } 

    // the default user Id to associate submitted records with
    @NotNull
    private String defaultUserId;

    public String getDefaultUserId() {
        return defaultUserId;
    } 
}