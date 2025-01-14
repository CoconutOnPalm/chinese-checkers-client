package com.chinese_checkers;

import com.chinese_checkers.Utils.ErrorDialog;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

//public class Main {
//    public static void main(String[] args)
//    {
//        Game game = new Game();
//        game.run();
//    }
//}

public class Main extends Application
{

	@Override
	public void start(Stage stage) throws Exception
	{
		System.out.println("file: " + getClass().getResource("/com/chinese_checkers/hello-view.fxml"));

		try
		{
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/chinese_checkers/loginScene.fxml"));
			Scene scene = loader.load();
			stage.setScene(scene);
		} catch (final Exception e)
		{
			System.out.println("Could not load the loginScene.fxml file.");
			ErrorDialog.show("FXML ERROR", "Could not load the loginScene.fxml file.");
			e.printStackTrace();
		}

		stage.setTitle("Chinese Checkers");
		stage.show();
	}

	@Override
	public void stop()
	{
	}
}