package co.enear.transformers

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scalaz._
import Scalaz._


/**
  * Created by luis on 18-04-2016.
  */

/**
  * The only difference from the cats library is the function .run instead of .value
  */
class AsyncEmployeeManagerZ {
  this: AsyncDBOps =>

  def getEmployeeAgeT(employeeId: String, companyName: String): Future[Option[Int]] = {
    (for {
      company <- OptionT(getCompany(companyName))
      if company.employees map(_.id) contains employeeId
      details <- OptionT(getDetails(employeeId))
    } yield details.age).run
  }
}

/**
  * Notice the added boilerplate in Future.successful and map(_.some). This is done because scalaz does not provide the
  * fromOption and liftF functions and we have to turn our functions into Future[Option[_]] in order for this to work.
  */
class HybridEmployeeManagerZ {
  this: HybridDBOps =>

  def getEmployeeAge(employeeId: String, companyName: String): Future[Option[Int]] = {
    (for {
      company <- OptionT(Future.successful(getCompany(companyName)))
      if company.employees map(_.id) contains employeeId
      details <- OptionT(getDetails(employeeId) map (_.some))
    } yield details.age).run
  }
}