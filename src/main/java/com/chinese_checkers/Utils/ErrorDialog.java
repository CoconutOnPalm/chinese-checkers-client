package com.chinese_checkers.Utils;

/**
 * @brief A simple-to-use error popup
 */
public class ErrorDialog
{
	static System.Logger logger = System.getLogger(ErrorDialog.class.getName());

	/**
	 * @brief Don't use the constructor - this is a static class
	 */
	private ErrorDialog() throws InstantiationException
	{
		throw new InstantiationException("Static class cannot be instantiated");
	}

	/**
	 * @brief Show an error dialog
	 * @param title The title (header) of the dialog
	 * @param message The message of the dialog
	 */
	public static void show(final String title, final String message)
	{
		javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
		alert.setHeaderText(title);
		alert.setContentText(message);
		alert.showAndWait();

		logger.log(System.Logger.Level.INFO, title + ": " + message);
	}
}
