package com.alexgrig;

import com.alexgrig.util.ConnectionManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class BlobRunner {
    public static void main(String[] args) throws IOException, SQLException {

        // blob - binary large object, bytea - in Postgres
        // clob - character large object, TEXT - in Postgres
        //saveImage();
        getImage();

    }

    private static void saveImage() throws SQLException, IOException {
        var sql = "UPDATE aircraft SET image = ? WHERE id = 1";

        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(sql)) {
            byte[] bytesImage = Files.readAllBytes(Path.of("resources", "boing777.jpg"));
            preparedStatement.setBytes(1, bytesImage);
            preparedStatement.executeUpdate();
        }
    }

    private static void getImage() throws SQLException, IOException {
        var sql = "SELECT image FROM aircraft WHERE id = ?";

        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, 1);
            var resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                var image = resultSet.getBytes("image");
                Files.write(Path.of("resources", "boing777_new.jpg"), image, StandardOpenOption.CREATE);
            }
        }
    }


//    // Этот вариант работает для баз данных, поддерживающих Blob, не для Postgres
//    private static void saveImage() throws SQLException {
//
//        var sql = "UPDATE aircraft SET image = ? WHERE id = 1";
//
//        try (Connection connection = ConnectionManager.open();
//             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
//
//            Blob blob = connection.createBlob();
//            byte[] bytesImage = Files.readAllBytes(Path.of("resources", "boing777.jpg"));
//            blob.setBytes(1, bytesImage);
//            preparedStatement.setBlob(1, blob);  // 1 - это первый знак вопроса в sql
//            preparedStatement.executeUpdate();
//
//        } catch (IOException e) {
//            System.out.println("ERROR");
//            e.printStackTrace();
//        }
//    }
}
