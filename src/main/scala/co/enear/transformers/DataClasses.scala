package co.enear.transformers

/**
  * Created by luis on 21-03-2016.
  */

sealed trait Employee {
  val id: String
}
final case class EmployeeWithoutDetails(id: String) extends Employee
final case class EmployeeWithDetails(id: String, name: String, city: String, age: Int) extends Employee

case class Company(companyName: String, employees: List[EmployeeWithoutDetails])