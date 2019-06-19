package com.ef;

import java.io.*;
import java.sql.*;


public class Parser {

    public static void main(String[] args) throws Exception {
	// write your code here
//        args[0] = "/Users/zeed/Downloads/Java_MySQL_Test";
//        args[1] = "";
//        args[3] = "";
//        args[4] = "";
        if (args.length < 3)
            throw new Exception("Arguments must contains atleast thre parameters");

        if (args.length == 4) {
            readFileAndPersistLogIfNecessary(args[0]);
        }

        try{
            Class.forName("com.mysql.jdbc.Driver");
            Connection con= Config.getCon();

            Statement stmt=con.createStatement();
            ResultSet rs=stmt.executeQuery("select * from customer");
            while(rs.next())
                System.out.println(rs.getInt(1)+"  "+rs.getString(2)+"  "+rs.getString(3));
            con.close();
        }catch(Exception e){
            System.out.println(e);}
    }


    private static void readFileAndPersistLogIfNecessary(String filePath) throws IOException {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath))) {
            String line = bufferedReader.readLine();

            while (line != null) {
                System.out.println(line);
                line = bufferedReader.readLine();
            }
            bufferedReader.close();
        }
    }

}
