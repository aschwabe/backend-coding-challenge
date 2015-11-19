Build and test instructions:
------------------------------

Environment:
-----------------

- Eclipse IDE
- Dropwizard + Maven
- MySQL

Requirements:
-----------------

- A working MySQL 
- Credentials to remotely connect to a database

How to build:
-----------------

1. Clone this into a workspace (eclipse or other) 
2. Edit src/main/resources/test.yml 
	i. Modify the database connection creds to point to any accessible mysql server
	ii. Modify the app settings:
		a. expenseTable (string) is the database table name of your choice
		b. maxRecords (integer) is the max number of records to display on "submitted expenses"
		c. defaultUserId (string) is a DEFAULT user Identifier for the expenses to be associated with assuming that authentication would be added to the app.   
3. Optionally change the server port (currently 8080)
4. launch using Maven:
	mvn clean compile exec:java -e

5. Open in browser at:
	Test the app here:
	http://localhost:8080/expenses/
	
	Monitor app health at:
	http://localhost:8080/admin/
	
	View API documentation/manually test API calls here:
	http://localhost:8080/swagger
	
	
Notes:
-----------------
- Upon first load, the app will check to see if it needs to create the database table
- The app will cowardly refuse to start if the database is unreachable
