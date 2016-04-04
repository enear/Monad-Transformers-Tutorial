package co.enear.transformers

import cats.data.OptionT
import cats.std.future._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by luis on 30-03-2016.
  */

class SyncEmployeeManager {
  this: SyncDBOps =>

  def getEmployeeAge(employeeId: String, companyName: String): Option[Int] = {
    for {
      company <- getCompany(companyName)
      if company.employees map(_.id) contains employeeId
      details <- getDetails(employeeId)
    } yield details.age
  }
}

class AsyncEmployeeManager {
  this: AsyncDBOps =>

  def getEmployeeAge(employeeId: String, companyName: String): Future[Option[Int]] = {
    for {
      companyOpt: Option[Company] <- getCompany(companyName)
      company: Company = companyOpt.getOrElse(Company("error", List()))
      if company.employees map(_.id) contains employeeId
      detailsOpt: Option[EmployeeWithDetails] <- getDetails(employeeId)
    } yield detailsOpt map (_.age)
  }

  def getEmployeeAgeT(employeeId: String, companyName: String): Future[Option[Int]] = {
    (for {
      company <- OptionT(getCompany(companyName))
      if company.employees map(_.id) contains employeeId
      details <- OptionT(getDetails(employeeId))
    } yield details.age).value
  }
}

class HybridEmployeeManager {
  this: HybridDBOps =>

  def getEmployeeAge(employeeId: String, companyName: String): Future[Option[Int]] = {
    (for {
      company <- OptionT.fromOption(getCompany(companyName))
      if company.employees map(_.id) contains employeeId
      details <- OptionT.liftF(getDetails(employeeId))
    } yield details.age).value
  }
}
