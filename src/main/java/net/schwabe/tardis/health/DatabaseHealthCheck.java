package net.schwabe.tardis.health;

import java.util.Iterator;
import java.util.Map;

import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;

import com.codahale.metrics.health.HealthCheck;

/*
 * This implements a health check for the mySQL database connection
 */
public class DatabaseHealthCheck extends HealthCheck {

	private DBI db;
	
	public DatabaseHealthCheck(DBI db) {
        this.db = db;
    }
	
	@Override
	protected Result check() throws Exception {

		Handle h = null;

		try {			

			// Open a connection
			h = this.db.open();
			// use a system function to test availability (not table-dependant)
			h.createQuery("SELECT version()");
			h.close();
			return Result.healthy();
		} catch (Exception e)
		{
			// always try to close the connection, even if there is an error
			try {
				h.close();
			} catch (Exception ignore) {}
            return Result.unhealthy("mySQL does not appear to be online or accessible.");
		}
	}

}
