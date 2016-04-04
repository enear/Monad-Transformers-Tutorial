package co.enear.transformers

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{FlatSpec, Matchers}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

/**
  * Created by luis on 30-03-2016.
  */
class EmployeeManagerTest extends FlatSpec with Matchers with ScalaFutures {

  trait dummySyncDB extends SyncDBOps {

    protected def getDetails(employeeId: String): Option[EmployeeWithDetails] = {
      if(employeeId == "employee1")
        Some(EmployeeWithDetails("employee1", "luis", "Lisbon", 26))
      else
        None
    }

    protected def getCompany(companyName: String): Option[Company] =
      if(companyName == "company1")
        Some(Company(companyName, List(EmployeeWithoutDetails("employee1"), EmployeeWithoutDetails("employee2"))))
      else
        None

  }

  trait dummyAsyncDB extends AsyncDBOps {
    protected def getDetails(employeeId: String): Future[Option[EmployeeWithDetails]] = {
      Future {
        if(employeeId == "employee1")
          Some(EmployeeWithDetails("employee1", "luis", "Lisbon", 26))
        else
          None
      }
    }
    protected def getCompany(companyName: String): Future[Option[Company]] = {
      Future {
        if(companyName == "company1")
          Some(Company(companyName, List(EmployeeWithoutDetails("employee1"), EmployeeWithoutDetails("employee2"))))
        else
          None
      }
    }
  }

  trait dummyHybridDB extends HybridDBOps {

    protected def getDetails(employeeId: String): Future[EmployeeWithDetails] = {
      Future {
        if(employeeId == "employee1")
          EmployeeWithDetails("employee1", "luis", "Lisbon", 26)
        else
          EmployeeWithDetails("employee2", "abel", "lisbon", 30)
      }
    }

    protected def getCompany(companyName: String): Option[Company] =
      if(companyName == "company1")
        Some(Company(companyName, List(EmployeeWithoutDetails("employee1"), EmployeeWithoutDetails("employee2"))))
      else
        None
  }

  val syncManager = new SyncEmployeeManager with dummySyncDB
  val asyncManager = new AsyncEmployeeManager with dummyAsyncDB
  val hybridManager = new HybridEmployeeManager with dummyHybridDB

  "A SyncManager" must "get the employee details correctly" in {
      val age = syncManager.getEmployeeAge("employee1", "company1")
      age shouldEqual Some(26)
    }

   it must "fail to get a non-existing company" in {
      val fakeCompany = syncManager.getEmployeeAge("employee1", "company2")
     fakeCompany shouldEqual None
    }

  it must "fail to get an employee without details" in {
    val employeeWithoutDetails = syncManager.getEmployeeAge("employee2", "company1")
    employeeWithoutDetails shouldEqual None
  }

  "An AsyncManager" must "get the employee details correctly" in {
    val ageFOpt = asyncManager.getEmployeeAge("employee1", "company1")
    val ageFOptT = asyncManager.getEmployeeAgeT("employee1", "company1")

    whenReady(ageFOpt) { ageOpt =>
      ageOpt shouldEqual Some(26)
    }

    whenReady(ageFOptT) { ageOpt =>
      ageOpt shouldEqual Some(26)
    }
  }

  it must "fail to get a non-existing company" in {
    val fakeCompany = asyncManager.getEmployeeAge("employee1", "company2")
    val fakeCompanyT = asyncManager.getEmployeeAgeT("employee1", "company2")

    fakeCompany map {_ => fail()} recover {
      case e => e shouldBe a [NoSuchElementException]
    } futureValue

    whenReady(fakeCompanyT) { ageOpt =>
      ageOpt shouldEqual None
    }
  }

  it must "fail to get an employee without details" in {
    val employeeWithoutDetails = asyncManager.getEmployeeAge("employee2", "company1")
    val employeeWithoutDetailsT = asyncManager.getEmployeeAge("employee2", "company1")

    whenReady(employeeWithoutDetails) { employeeOpt =>
      employeeOpt shouldEqual None
    }

    whenReady(employeeWithoutDetailsT) { employeeOpt =>
      employeeOpt shouldEqual None
    }
  }

  "A Hybrid Manager" must "get the employee details correctly" in {
    val ageFOpt = hybridManager.getEmployeeAge("employee1", "company1")

    whenReady(ageFOpt) { ageOpt =>
      ageOpt shouldEqual Some(26)
    }
  }

  it must "fail get a non-existing company" in {
    val fakeCompany = hybridManager.getEmployeeAge("employee1", "company2")

    whenReady(fakeCompany) { ageOpt =>
      ageOpt shouldEqual None
    }
  }

 }