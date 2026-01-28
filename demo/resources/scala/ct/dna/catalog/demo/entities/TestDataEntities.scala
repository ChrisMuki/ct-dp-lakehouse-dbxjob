package ct.dna.catalog.demo.entities

import java.sql.Date
import ct.dna.lakehouse.entity.Entity.Entity
import ct.dna.lakehouse.entity.Entity.Entity._

/** Entity case classes for the test-data-typed directory with proper Date types. These match the converted test data in test-data-typed/
  */
object TestDataEntities {

  // analytics/data/customers.parquet
  case class Customer(
      @pk customer_id: String,
      name: String,
      email: String,
      tier: String,
      total_spent: Double,
      join_date: Date
  ) extends Entity

  // analytics/data/orders.jsonl
  // Note: JSON format doesn't support native Date types, so order_date remains String
  case class Order(
      @pk order_id: String,
      @fk(classOf[Customer]) customer_id: String,
      order_date: String,
      total: Double,
      status: String
  ) extends Entity

  // employees.xlsx
  case class Employee(
      @pk id: Int,
      name: String,
      department: String,
      salary: Double,
      start_date: String // Still String in Excel
  ) extends Entity

  // sales/products/catalog.xlsx - products sheet
  case class Product(
      @pk product_id: String,
      name: String,
      category: String,
      price: Double,
      stock: Long
  ) extends Entity

  // sales/2024/q1/transactions.xlsx - january/february/march sheets
  // sales/2024/q2/transactions.xlsx - april/may/june sheets
  case class SalesTransaction(
      @pk tx_id: String,
      date: Date,
      @fk(classOf[Customer]) customer_id: String,
      @fk(classOf[Product]) product_id: String,
      amount: Double,
      status: String
  ) extends Entity

  // budget.xlsx - budgets sheet
  case class Budget(
      @pk department: String,
      @pk year: Int,
      budget: Double
  ) extends Entity

  // locations.xlsx - offices sheet
  case class Location(
      @pk office_id: String,
      city: String,
      country: String,
      employees: Int
  ) extends Entity

  // analytics/reports/monthly.xlsx - summary sheet
  case class MonthlySummary(
      @pk month: String,
      revenue: Double,
      expenses: Double,
      profit: Double
  ) extends Entity
}
