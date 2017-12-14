/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Properties;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import com.jfoenix.controls.JFXPasswordField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;
/**
 *
 * Credits for @author https://gist.github.com/drguildo/ba2834bf52d624113041
 */
public class PasswordDialog extends Dialog<String>{
    private JFXPasswordField passwordField;

    public PasswordDialog() {
      setTitle("Cambiar contraseña");
      setHeaderText("Evire: Solicitud de cambio de contraseña");

      ButtonType passwordButtonType = new ButtonType("Continuar", ButtonData.OK_DONE);
      getDialogPane().getButtonTypes().addAll(passwordButtonType, ButtonType.CANCEL);

      passwordField = new JFXPasswordField();
      passwordField.setPromptText("Nueva Contraseña");
      passwordField.setFont(Font.font(14));

      HBox hBox = new HBox();
      hBox.getChildren().add(passwordField);
      hBox.setPadding(new Insets(20));

      HBox.setHgrow(passwordField, Priority.ALWAYS);

      getDialogPane().setContent(hBox);

      Platform.runLater(() -> passwordField.requestFocus());

      setResultConverter(dialogButton -> {
        if (dialogButton == passwordButtonType) {
          return passwordField.getText();
        }
        return null;
      });
    }

    public JFXPasswordField getPasswordField() {
      return passwordField;
  }
}
