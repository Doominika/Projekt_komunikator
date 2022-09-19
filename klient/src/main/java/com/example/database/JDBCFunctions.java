package com.example.database;

import com.example.other.DataMessage;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JDBCFunctions
{
    private Connection connection;
    private Statement stmt;

    public JDBCFunctions() throws ClassNotFoundException, SQLException
    {
        Class.forName("org.postgresql.Driver");
        connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/chat", "postgres", "1234");

    }

    public void createTable(String tableName) throws SQLException  //tableName to id z tabeli conversations rzutowane na string
    {
        stmt = connection.createStatement();

        String sql = "CREATE TABLE \"" + tableName
                + "\" ( id serial NOT NULL UNIQUE,"
                +" sender varchar NOT NULL,"
                + " message varchar NOT NULL); ";
        stmt.executeUpdate(sql);
        stmt.close();
    }

    public void add(String sender, String msg, String tableName) throws SQLException
    {
        stmt = connection.createStatement();
        String sql = "INSERT INTO \""+ tableName+ "\" (sender, message) VALUES ('" + sender + "', '" + msg + "')";
        stmt.executeUpdate(sql);
        stmt.close();
    }

    public List<DataMessage> getAllMessages(String tableName) throws SQLException
    {
        stmt = connection.createStatement();

        List<DataMessage> lista = new ArrayList<>();
        ResultSet result = stmt.executeQuery("SELECT * FROM \""+ tableName + "\" ORDER BY id ASC" );

        while(result.next())
        {
            lista.add(new DataMessage(result.getString("sender"), result.getString("message")));
            //System.out.println(result.getString("sender") + result.getString("message"));
        }

        return lista;
    }

}
