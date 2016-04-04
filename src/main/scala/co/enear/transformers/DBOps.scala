package co.enear.transformers

import scala.concurrent.Future

/**
  * Created by luis on 30-03-2016.
  */

trait SyncDBOps {
  protected def getDetails(employeeId: String): Option[EmployeeWithDetails]
  protected def getCompany(companyName: String): Option[Company]
}

trait AsyncDBOps {
  protected def getDetails(employeeId: String): Future[Option[EmployeeWithDetails]]
  protected def getCompany(companyName: String): Future[Option[Company]]
}

trait HybridDBOps {
  protected def getDetails(employeeId: String): Future[EmployeeWithDetails]
  protected def getCompany(companyName: String): Option[Company]
}

