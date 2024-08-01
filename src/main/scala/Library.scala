import javafx.application.Application
import javafx.application.Application.launch
import javafx.scene.Scene
import javafx.scene.control.Alert.AlertType
import javafx.scene.control._
import javafx.scene.layout._
import javafx.stage.Stage

import java.io._
import scala.collection.JavaConverters._

object Library extends Application {

  def main(args: Array[String]): Unit = {
    launch(args: _*)
  }

  override def start(primaryStage: Stage): Unit = {
    primaryStage.setTitle("Library Management System")

    val root = new VBox()
    root.setSpacing(10)

    val addBookButton = new Button("Add Book")
    val removeBookButton = new Button("Remove Book")
    val showAllBooksButton = new Button("Show All Books")
    val checkInBookButton = new Button("Check In Book")
    val checkOutBookButton = new Button("Check Out Book")
    val addCustomerButton = new Button("Add Customer")
    val searchBookButton = new Button("Search Book")
    val updateBookPriceButton = new Button("Update Book Price")
    val showAllCustomersButton = new Button("Show All Customers")

    root.getChildren.addAll(
      addBookButton, removeBookButton, showAllBooksButton,
      checkInBookButton, checkOutBookButton, addCustomerButton,
      searchBookButton, updateBookPriceButton, showAllCustomersButton
    )

    addBookButton.setOnAction(_ => showAddBookDialog())
    removeBookButton.setOnAction(_ => showRemoveBookDialog())
    showAllBooksButton.setOnAction(_ => showAllBooksDialog())
    checkInBookButton.setOnAction(_ => showCheckInBookDialog())
    checkOutBookButton.setOnAction(_ => showCheckOutBookDialog())
    addCustomerButton.setOnAction(_ => showAddCustomerDialog())
    searchBookButton.setOnAction(_ => showSearchBookDialog())
    updateBookPriceButton.setOnAction(_ => showUpdateBookPriceDialog())
    showAllCustomersButton.setOnAction(_ => showAllCustomersDialog())

    val scene = new Scene(root, 400, 400)
    primaryStage.setScene(scene)
    primaryStage.show()
  }

  def showAddBookDialog(): Unit = {
    val dialog = new TextInputDialog()
    dialog.setTitle("Add Book")
    dialog.setHeaderText("Enter Book Details:")
    dialog.setContentText("Title:")
    val resultTitle = dialog.showAndWait()

    resultTitle.ifPresent(title => {
      dialog.setContentText("Author:")
      val resultAuthor = dialog.showAndWait()

      resultAuthor.ifPresent(author => {
        dialog.setContentText("Price:")
        val resultPrice = dialog.showAndWait()

        resultPrice.ifPresent(price => {
          dialog.setContentText("ISBN:")
          val resultISBN = dialog.showAndWait()

          resultISBN.ifPresent(isbn => {
            dialog.setContentText("Quantity:")
            val resultQuantity = dialog.showAndWait()

            resultQuantity.ifPresent(quantity => {
              // Now you have all the details, you can proceed to add the book
              val bookEntry = s"$title,$author,$price,$isbn,$quantity\n"
              writeToDBFile(bookEntry)
              println("Book added successfully.")
            })
          })
        })
      })
    })
  }

  def writeToDBFile(bookEntry: String): Unit = {
    try {
      val file = new File("DB.txt")
      val writer = new BufferedWriter(new FileWriter(file, true))
      writer.write(bookEntry)
      writer.close()
    } catch {
      case e: IOException =>
        println(s"Error writing to the file: ${e.getMessage}")
    }
  }

  def showRemoveBookDialog(): Unit = {
    val dialog = new TextInputDialog()
    dialog.setTitle("Remove Book")
    dialog.setHeaderText("Enter the Title of the Book to Remove:")
    dialog.setContentText("Title:")
    val resultTitle = dialog.showAndWait()

    resultTitle.ifPresent(bookTitleToRemove => {
      // Now you have the book title to remove, you can proceed to remove the book
      removeBookFromDB(bookTitleToRemove)
    })
  }

  def removeBookFromDB(bookTitleToRemove: String): Unit = {
    // Read the contents of the file into a list of lines
    val lines: List[String] = try {
      val source = scala.io.Source.fromFile("DB.txt")
      val linesList = source.getLines().toList
      source.close()
      linesList
    } catch {
      case e: Exception =>
        println(s"Error reading the file: ${e.getMessage}")
        return
    }

    // Filter out the line with the specified title
    val updatedLines = lines.filterNot(line => line.toLowerCase.startsWith(bookTitleToRemove.toLowerCase))

    // Write the updated lines back to the file
    try {
      val writer = new PrintWriter(new File("DB.txt"))
      updatedLines.foreach(writer.println)
      writer.close()
      println(s"Book with title '$bookTitleToRemove' removed successfully.")
    } catch {
      case e: Exception =>
        println(s"Error writing to the file: ${e.getMessage}")
    }
  }

  def showAllBooksDialog(): Unit = {
    // Read the contents of the file into a list of lines
    val lines: List[String] = try {
      val source = scala.io.Source.fromFile("DB.txt")
      val linesList = source.getLines().toList
      source.close()
      linesList
    } catch {
      case e: Exception =>
        println(s"Error reading the file: ${e.getMessage}")
        return
    }

    if (lines.isEmpty) {
      val alert = new Alert(AlertType.INFORMATION)
      alert.setTitle("No Books")
      alert.setHeaderText(null)
      alert.setContentText("There are no books in the library.")
      alert.showAndWait()
    } else {
      val bookListTextArea = new TextArea(lines.mkString("\n"))
      bookListTextArea.setEditable(false)
      bookListTextArea.setWrapText(true)

      val alert = new Alert(AlertType.INFORMATION)
      alert.setTitle("All Books")
      alert.setHeaderText(null)
      alert.getDialogPane.setContent(bookListTextArea)
      alert.showAndWait()
    }
  }

  def showCheckInBookDialog(): Unit = {
    val dialog = new TextInputDialog()
    dialog.setTitle("Check In Book")
    dialog.setHeaderText("Enter the Title of the Book to Check In:")
    dialog.setContentText("Title:")
    val resultTitle = dialog.showAndWait()

    resultTitle.ifPresent(bookTitleToCheckIn => {
      // Now you have the book title to check in, you can proceed to check in the book
      checkInBookToDB(bookTitleToCheckIn)
    })
  }

  def checkInBookToDB(bookTitleToCheckIn: String): Unit = {
    // Read the contents of the file into a list of lines
    val lines: List[String] = try {
      val source = scala.io.Source.fromFile("DB.txt")
      val linesList = source.getLines().toList
      source.close()
      linesList
    } catch {
      case e: Exception =>
        println(s"Error reading the file: ${e.getMessage}")
        return
    }

    // Check if the book title is found in the file
    val updatedLines = lines.map { line =>
      val values = line.split(",")
      if (values.length >= 5 && values(0).toLowerCase == bookTitleToCheckIn.toLowerCase) {
        // Increase the quantity by 1 if the title matches
        s"${values(0)},${values(1)},${values(2)},${values(3)},${values(4).toInt + 1}"
      } else {
        line
      }
    }

    // Write the updated lines back to the file
    try {
      val writer = new PrintWriter(new File("DB.txt"))
      updatedLines.foreach(writer.println)
      writer.close()
      println(s"Book '$bookTitleToCheckIn' checked in successfully.")
    } catch {
      case e: Exception =>
        println(s"Error writing to the file: ${e.getMessage}")
    }
  }

  def showCheckOutBookDialog(): Unit = {
    val dialog = new TextInputDialog()
    dialog.setTitle("Check Out Book")
    dialog.setHeaderText("Enter the Title of the Book to Check Out:")
    dialog.setContentText("Title:")
    val resultTitle = dialog.showAndWait()

    resultTitle.ifPresent(bookTitleToCheckOut => {
      // Now you have the book title to check out, you can proceed to check out the book
      checkOutBookFromDB(bookTitleToCheckOut)
    })
  }

  def checkOutBookFromDB(bookTitleToCheckOut: String): Unit = {
    // Read the contents of the file into a list of lines
    val lines: List[String] = try {
      val source = scala.io.Source.fromFile("DB.txt")
      val linesList = source.getLines().toList
      source.close()
      linesList
    } catch {
      case e: Exception =>
        println(s"Error reading the file: ${e.getMessage}")
        return
    }

    // Check if the book title is found in the file
    val updatedLines = lines.map { line =>
      val values = line.split(",")
      if (values.length >= 5 && values(0).toLowerCase == bookTitleToCheckOut.toLowerCase) {
        // Decrease the quantity by 1 if the title matches
        val updatedQuantity = math.max(values(4).toInt - 1, 0)
        s"${values(0)},${values(1)},${values(2)},${values(3)},$updatedQuantity"
      } else {
        line
      }
    }

    // Write the updated lines back to the file
    try {
      val writer = new PrintWriter(new File("DB.txt"))
      updatedLines.foreach(writer.println)
      writer.close()
      println(s"Book '$bookTitleToCheckOut' checked out successfully.")
    } catch {
      case e: Exception =>
        println(s"Error writing to the file: ${e.getMessage}")
    }
  }

  def showAddCustomerDialog(): Unit = {
    val dialog = new TextInputDialog()
    dialog.setTitle("Add Customer")
    dialog.setHeaderText("Enter Customer Details:")
    dialog.setContentText("Customer Name:")
    val resultCustomerName = dialog.showAndWait()

    resultCustomerName.ifPresent(customerName => {
      // Now you have the customer name, you can proceed to get the customer ID
      dialog.setContentText("Customer ID:")
      val resultCustomerId = dialog.showAndWait()

      resultCustomerId.ifPresent(customerId => {
        // Now you have both the customer name and ID, you can proceed to add the customer
        addCustomerToDB(customerName, customerId)
      })
    })
  }

  def addCustomerToDB(customerName: String, customerId: String): Unit = {
    // Create a string representing the customer entry
    val customerEntry = s"$customerName,$customerId\n"

    // Open the file in append mode and write the customer entry
    try {
      val file = new File("Customers.txt")
      val writer = new BufferedWriter(new FileWriter(file, true))
      writer.write(customerEntry)
      writer.close()
      println("Customer added successfully.")
    } catch {
      case e: IOException =>
        println(s"Error writing to the file: ${e.getMessage}")
    }
  }

  def showSearchBookDialog(): Unit = {
    val dialog = new TextInputDialog()
    dialog.setTitle("Search Book")
    dialog.setHeaderText("Enter the Title of the Book to Search:")
    dialog.setContentText("Title:")
    val resultTitle = dialog.showAndWait()

    resultTitle.ifPresent(bookTitleToSearch => {
      // Now you have the book title to search, you can proceed to search for the book
      val matchingBooks: List[String] = searchBookInDB(bookTitleToSearch)

      if (matchingBooks.nonEmpty) {
        val alert = new Alert(AlertType.INFORMATION)
        alert.setTitle("Matching Books")
        alert.setHeaderText(null)
        alert.setContentText(s"Matching Book(s):\n${matchingBooks.mkString("\n")}")
        alert.showAndWait()
      } else {
        val alert = new Alert(AlertType.INFORMATION)
        alert.setTitle("No Match")
        alert.setHeaderText(null)
        alert.setContentText(s"No book with title '$bookTitleToSearch' found.")
        alert.showAndWait()
      }
    })
  }

  def searchBookInDB(bookTitleToSearch: String): List[String] = {
    // Read the contents of the file into a list of lines
    try {
      val source = scala.io.Source.fromFile("DB.txt")
      val linesList = source.getLines().toList
      source.close()

      // Search for the book title in the file
      linesList.filter { line =>
        val values = line.split(",")
        values.length >= 5 && values(0).toLowerCase == bookTitleToSearch.toLowerCase
      }
    } catch {
      case e: Exception =>
        println(s"Error reading the file: ${e.getMessage}")
        List.empty[String]
    }
  }

  def showUpdateBookPriceDialog(): Unit = {
    val dialog = new TextInputDialog()
    dialog.setTitle("Update Book Price")
    dialog.setHeaderText("Enter the Title of the Book to Update:")
    dialog.setContentText("Title:")
    val resultTitle = dialog.showAndWait()

    resultTitle.ifPresent(bookTitleToUpdate => {
      // Now you have the book title to update, you can proceed to get the new price
      dialog.setContentText("Enter the New Price:")
      val resultNewPrice = dialog.showAndWait()

      resultNewPrice.ifPresent(newPrice => {
        // Now you have both the book title and new price, you can proceed to update the book price
        updateBookPriceInDB(bookTitleToUpdate, newPrice)
      })
    })
  }

  def updateBookPriceInDB(bookTitleToUpdate: String, newPrice: String): Unit = {
    // Read the contents of the file into a list of lines
    val lines: List[String] = try {
      val source = scala.io.Source.fromFile("DB.txt")
      val linesList = source.getLines().toList
      source.close()
      linesList
    } catch {
      case e: Exception =>
        println(s"Error reading the file: ${e.getMessage}")
        return
    }

    // Update the book price in the file
    val updatedLines = lines.map { line =>
      val values = line.split(",")
      if (values.length >= 5 && values(0).toLowerCase == bookTitleToUpdate.toLowerCase) {
        // Update the price if the title matches
        s"${values(0)},${values(1)},${values(2)},$newPrice,${values(4)}"
      } else {
        line
      }
    }

    // Write the updated lines back to the file
    try {
      val writer = new PrintWriter(new File("DB.txt"))
      updatedLines.foreach(writer.println)
      writer.close()
      println(s"Book price for '$bookTitleToUpdate' updated successfully.")
    } catch {
      case e: Exception =>
        println(s"Error writing to the file: ${e.getMessage}")
    }
  }

  def showAllCustomersDialog(): Unit = {
    // Read the contents of the file into a list of lines
    val lines: List[String] = try {
      val source = scala.io.Source.fromFile("Customers.txt")
      val linesList = source.getLines().toList
      source.close()
      linesList
    } catch {
      case e: Exception =>
        println(s"Error reading the file: ${e.getMessage}")
        return
    }

    if (lines.isEmpty) {
      val alert = new Alert(AlertType.INFORMATION)
      alert.setTitle("No Customers")
      alert.setHeaderText(null)
      alert.setContentText("There are no customers in the system.")
      alert.showAndWait()
    } else {
      val customerListTextArea = new TextArea(lines.mkString("\n"))
      customerListTextArea.setEditable(false)
      customerListTextArea.setWrapText(true)

      val alert = new Alert(AlertType.INFORMATION)
      alert.setTitle("All Customers")
      alert.setHeaderText(null)
      alert.getDialogPane.setContent(customerListTextArea)
      alert.showAndWait()
    }
  }
}
