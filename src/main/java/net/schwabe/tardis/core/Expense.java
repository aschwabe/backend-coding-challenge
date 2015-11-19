package net.schwabe.tardis.core;

import java.sql.Timestamp;
import java.util.Date;

import org.hibernate.validator.constraints.NotEmpty;
import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

// https://github.com/kubamarchwicki/micro-java/blob/master/examples/dropwizard/src/main/java/pl/marchwicki/microjava/model/Todo.java

public class Expense {

	@JsonProperty
	private long id;

	@JsonProperty
	@NotEmpty
	private double amount;

	@JsonProperty
	@NotEmpty
	private double vat;

	@JsonProperty
	@NotEmpty
	private double base;

	@JsonProperty
	@NotEmpty
	private String reason;

	@JsonProperty
	@NotEmpty
	private Date date;
	
	public Expense() {
		// default empty constructor
	}

	private Expense(long id, double amount, double vat, double base, Date date, String reason) {
        this.id = id;
        this.amount = amount;
        this.base = base;
        this.vat = vat;
        this.date = date;
        this.reason = reason;
    }


    public long getId() {
        return id;
    }

    public String getReason() {
        return reason;
    }

    public double getAmount() {
        return amount;
    }	

    public double getVat() {
        return vat;
    }	

    public double getBase() {
        return base;
    }	

    public Date getDate() {
        return date;
    }	

    public static class ExpenseBuilder {
        private int id;
        private String reason;
        private double amount;
        private double base;
        private double vat;
        private Date date;

        private ExpenseBuilder() {

        }

        public static ExpenseBuilder createExpense() {
            return new ExpenseBuilder();
        }

        public ExpenseBuilder withId(int id) {
            this.id = id;
            return this;
        }

        public ExpenseBuilder withReason(String reason) {
            this.reason = reason;
            return this;
        }

        public ExpenseBuilder withAmount(double amount) {
            this.amount = amount;
            return this;
        }

        public ExpenseBuilder withVat(double vat) {
            this.vat = vat;
            return this;
        }

        public ExpenseBuilder withBase(double base) {
            this.base = base;
            return this;
        }

        public ExpenseBuilder calcVat() {
        	this.vat = this.amount * .2;
        	this.base = this.amount - this.vat;
        	return this;
        }

        public ExpenseBuilder withDate(Date date) {
            this.date = date;
            return this;
        }

        public ExpenseBuilder withDateTime(DateTime dt) {
            this.date = dt.toDate();
            return this;
        }

        public ExpenseBuilder withTimestamp(Timestamp ts) {
            this.date = ts;
            return this;
        }

        public Expense build() {
            return new Expense(id, amount, vat, base, date, reason);
            
        }
    }

}
