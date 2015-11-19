package net.schwabe.tardis.resources;

import java.sql.Timestamp;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.joda.time.DateTime;
import org.json.JSONObject;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import net.schwabe.tardis.responses.MessageResponse;
import net.schwabe.tardis.config.AppConfig;
import net.schwabe.tardis.core.Expense;
import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponses;
import com.wordnik.swagger.annotations.ApiResponse;

/**
 * @author aschwabe
 * purpose: this resource exposes the API to submit and retrieve expense records.
 */

@Api(value = "/expenses", tags = "Expenses API")
@Path("/expenses")
@Produces(MediaType.APPLICATION_JSON)
public class ExpenseResource {

	private final AppConfig config;
	private final DBI db;
	private boolean setupTable = false;
	
	
	public ExpenseResource(AppConfig config, DBI db) {
		this.config = config;
		this.db = db;
    }

	/*
	 * function: getExpense()
	 * purpose: retrieves a list of recent expenses
	 */
	@GET
    @Timed
    @ApiOperation(value = "Retrieve a list of recent posted expenses", response = Expense.class)
    @ApiResponses(value = {
      @ApiResponse(code = 400, message = "Error while processing request")
    })
	public Response getExpenses() {

		Handle h = null;
    	try {

    		// create the table if necessary
    		if (!this.setupTable) checkCreateTable();
    		
    		// mock value
    		List<Expense> expenseList = Lists.newArrayList();

    		// check user's authorization
    		// TO-DO: implement header based security
    		String userId = this.config.getSettings().getDefaultUserId();
    		
    		// query the database for recent n records
    		h = this.db.open();

    		String sql = "select * from "+this.config.getSettings().getExpenseTable()+" where userId = :userId order by ts DESC limit " + this.config.getSettings().getMaxRecords();
    		Iterator<Map<String, Object>> results = h.createQuery(sql)
       				.bind("userId", userId)
       				.iterator();    		

    		while (results.hasNext()) {
    			Map<String, Object> result = results.next();

        		Expense ex = Expense.ExpenseBuilder.createExpense()
        				.withAmount((Double)result.get("amount"))
        				.withVat((Double)result.get("amount"))
        				.withBase((Double)result.get("amount"))
        				.withTimestamp((Timestamp)result.get("date"))
        				.withReason((String)result.get("reason"))
        				.build();
        		expenseList.add(ex);    			
    		
    		}
    		
    		// loop and create list of expense objects
    		h.close();

    		
    		// return JSON representation of the List
    		return Response.ok(new ObjectMapper().writer().writeValueAsString(expenseList)).build();

    	} catch (Exception e) {
			try {
				h.close();	// always try to close the handle, even if it hasn't been opened yet
			} catch (Exception ignore) {}
    		e.printStackTrace();	// local debug
    		return Response.status(Response.Status.BAD_REQUEST).entity(new MessageResponse(500, e.getMessage())).build();
    	}    
    
    }	

	
	@POST
    @Timed
    /*
     * function saveExpense()
     * purpose: submits an expense via raw JSON post.
     * 
     * Note:
     * The built-in decoder couldn't seem to get the right media type:
     * {"code":400,"message":"Unable to process JSON"}
     * So, changed the input type to String, and then parsed it within try{} block;
     * If I had access to the whole stack, I would debug the mime type of the client (html/js).
     */
    @ApiOperation(
    		value = "Submit a new expense to be stored", 
    		notes = "Receives a JSON Object in the following format (you may copy/paste to test): {date: \"2015-11-10T05:00:00.000Z\", amount: \"12\", reason: \"test\"}",
    		response = MessageResponse.class)
    @ApiResponses(value = {
      @ApiResponse(code = 400, message = "Error while processing request")
    })
	public Response saveExpense(@ApiParam(value = "JSON Object") String postReq) {		

		Handle h = null;

    	try {
    		
    		// create the table if necessary
    		if (!this.setupTable) checkCreateTable();

    		// parse the user input
    		JSONObject jsonPostObj = new JSONObject(postReq);
    		
    		// check that the keys exist:
    		if (!jsonPostObj.has("date") || !jsonPostObj.has("amount") || !jsonPostObj.has("reason"))
    		{
    			throw new Exception("JSON is malformatted.  date, amount and reason must all be provided.");
    		}    		
    		
    		// check user's authorization
    		// TO-DO: implement header based security
    		String userId = jsonPostObj.has("userId") ? jsonPostObj.getString("userId") : this.config.getSettings().getDefaultUserId();
    		
    		// parse the user supplied date
    		DateTime dtExpenseDate = new DateTime(jsonPostObj.getString("date"));
    		
    		// calculate vat
    		double dblVat = jsonPostObj.getDouble("amount") * .2;
    		double dblAmountLessVat = jsonPostObj.getDouble("amount") - dblVat;
    		
    		// save the object to the database
    		h = this.db.open();
    		String sql = "insert into " + this.config.getSettings().getExpenseTable() + " (userId, amount, base, vat, date, reason, ts) values (:userId, :amount, :base, :vat, :date, :reason, :ts)";
    		h.execute(sql, 
    				userId, 
    				jsonPostObj.getDouble("amount"), 
    				dblAmountLessVat, 
    				dblVat, 
    				dtExpenseDate, 
    				jsonPostObj.getString("reason"), 
    				new DateTime());
    		h.close();    		
    		
    		// return a representation with return data
    		return Response.ok().entity(new MessageResponse("OK")).build();

    	} catch (Exception e) {
			try {
				h.close();	// always try to close the handle, even if it hasn't been opened yet
			} catch (Exception ignore) {}
    		e.printStackTrace();	// local debug
    		return Response.status(Response.Status.BAD_REQUEST).entity(new MessageResponse(500, e.getMessage())).build();
    	} 
    
    }	
	
	/*
	 * Check the existance of the target database table (defined in yml file)
	 */
	private void checkCreateTable() {		
	
		this.setupTable = true;
		Handle h = this.db.open();

		try {
			// check if this table exists
			Map<String, Object> result = h.createQuery("select count(*) as count FROM information_schema.tables WHERE table_schema = :database AND table_name = :table")
	   				.bind("database", "expenses")
	   				.bind("table", this.config.getSettings().getExpenseTable()).first();
	
			if ((Long)result.get("count") == 0)
			{
				System.out.println("DEBUG: creating table");
				String sql = "create table "+this.config.getSettings().getExpenseTable()+" (id int(16) AUTO_INCREMENT NOT NULL PRIMARY KEY, userId varchar(100), amount double, base double, vat double, date datetime, reason varchar(100), ts datetime)";
				h.execute(sql);
			}
			else
			{
				System.out.println("DEBUG: table already exists");			
			}
			
			h.close();
		} catch (Exception e) {
			try {
				h.close();	// always try to close the handle, even if it hasn't been opened yet
			} catch (Exception ignore) {}
		}
		
	}


}
