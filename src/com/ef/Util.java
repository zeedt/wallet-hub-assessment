package com.ef;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Util {

    public static List<String> getFilteredAccessLogs(Date startDate, Date endDate, Integer treshold, Connection connection) throws SQLException {

        final List<String> ipAddresses = new ArrayList<>();

        String sql = "select ipAddress from access_log where requestTime between ? and ? GROUP BY ipAddress HAVING count(ipAddress) > " + treshold;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setTimestamp(1, new Timestamp(startDate.getTime()));
            preparedStatement.setTimestamp(2, new Timestamp(endDate.getTime()));

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                ipAddresses.add(resultSet.getString("ipAddress"));
            }

        } catch (SQLException e) {
            System.out.println("SQL error occurred due to " + e);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error occurred due to " + e);
        }

        return ipAddresses;
    }

    public static void saveBlockedIpsWithComment(List<String> ipAddresses, String blockReason, Connection connection) throws SQLException {

        try {
            connection.setAutoCommit(false);
            StringBuffer insertBuffer = new StringBuffer();
            insertBuffer.append("insert into blocked_ips (id, ipAddress, comment) values (?, ?, ?)");
            String inserSql = insertBuffer.toString();
            PreparedStatement pStmt = connection.prepareStatement(inserSql);
            for (String ipAdress:ipAddresses) {
                pStmt.setString(1, null );
                pStmt.setString(2, ipAdress);
                pStmt.setString(3, blockReason);
                pStmt.addBatch();
            }
            int successfulArray[] = pStmt.executeBatch();
            int successCount = successfulArray.length;
            System.out.println(String.format("%d records successfully persisted with reasons for blocking", successCount));
            connection.commit();
            pStmt.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error occurred due to " + e);
        } finally {
            if (connection != null)
                connection.close();
        }
    }

    public static Date getStartDateFromString(String dateString) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd.HH:mm:ss");
            return dateFormat.parse(dateString);
        } catch (Exception e) {
            System.out.println("Error occurred while parsing date " + dateString + " due to " + e);
        }
        return null;
    }


    public static List<AccessLog> readFileAndPersistLogIfNecessary(String filePath) throws IOException {
        List<AccessLog> accessLogs = new ArrayList<>();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath))) {
            String line = bufferedReader.readLine();

            while (line != null) {
                line = bufferedReader.readLine();
                if (line != null && !line.trim().equals("")) {
                    String[] lineArray = line.split("\\|");
                    accessLogs.add(new AccessLog(lineArray[1], getDateFromString(lineArray[0])));
                }
            }
            bufferedReader.close();
        }
        return accessLogs;
    }

    public static Date getDateFromString(String dateString) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            return dateFormat.parse(dateString);
        } catch (Exception e) {
            System.out.println("Error occurred while parsing date " + dateString + " due to " + e);
        }
        return null;
    }

    public static Date getEndDate(Date startDate, String interval) {

        if (interval != null && interval.equals("hourly")) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(startDate);
            calendar.add(Calendar.HOUR_OF_DAY, 1);
            return calendar.getTime();
        }

        if (interval != null && interval.equals("daily")) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(startDate);
            calendar.add(Calendar.HOUR_OF_DAY, 24);
            return calendar.getTime();
        }

        return startDate;

    }

}
