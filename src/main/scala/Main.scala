import javafx.application.Application
import javafx.application.Application.launch
import javafx.scene.Scene
import javafx.scene.control._
import javafx.scene.layout.VBox
import javafx.stage.Stage;

object Main extends App {
  launch(classOf[LibraryManagementApp], args: _*)
}

class LibraryManagementApp extends Application {
//  val library = Library()

  override def start(primaryStage: Stage): Unit = {
    primaryStage.setTitle("Library Management System")

    val menuLabel = new Label(
      """


         Please select an option from following menu:
         -----------------------------------------------------------------
     1.  Add a Book                 |   2.  Remove a Book            |
     3.  Show all books             |   4.  Check In a book          |
     5.  Check Out a book           |   6.  Add Customer             |
     7.  Search a book              |   8.  Update book price        |
     9.  Show all customers         |   10. Exit                     |
         -----------------------------------------------------------------

         """)

    val menuTextArea = new TextArea()
    val choiceTextField = new TextField()
    val submitButton = new Button("Submit")

    val vBox = new VBox(menuLabel, menuTextArea, choiceTextField, submitButton)

    val scene = new Scene(vBox, 400, 400)

    primaryStage.setScene(scene)
    primaryStage.show()

    submitButton.setOnAction(_ => handleButtonClick(choiceTextField.getText, menuTextArea))
  }

  private def handleButtonClick(choice: String, resultTextArea: TextArea): Unit = {
    try {
      val choiceInt = choice.toInt

      val result = choice match {
        case "1" => Library.showAddBookDialog()
        case "2" => Library.showRemoveBookDialog()
        case "3" => Library.showAllBooksDialog()
        case "4" => Library.showCheckInBookDialog()
        case "5" => Library.showCheckOutBookDialog()
        case "6" => Library.showAddCustomerDialog()
        case "7" => Library.showSearchBookDialog()
        case "8" => Library.showUpdateBookPriceDialog()
        case "9" => Library.showAllCustomersDialog()
        case "10" =>
          resultTextArea.setText("Thank You for using Library Management System!")
          return
        case _ => "Invalid choice. Please enter a valid number."
      }

      result match {
        case list: List[_] =>
          val resultText = if (list.nonEmpty) list.mkString("\n") else "No data found."
          resultTextArea.setText(resultText)
        case _ => resultTextArea.setText(result.toString)
      }
    } catch {
      case _: NumberFormatException => resultTextArea.setText("Invalid input. Please enter a number.")
    }
  }
}
