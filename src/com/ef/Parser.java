package com.ef;

import java.sql.*;
import java.util.Date;
import java.util.List;


public class Parser {

    public static void main(String[] args) throws Exception {

        Connection connection = null;
        Date startDate = null;
        String interval = "";
        Integer threshold = null;
        List<AccessLog> accessLogs = null;
        if (args.length < 3)
            throw new Exception("Arguments must contain atleast three parameters");
        connection = Config.getCon();
        if (args.length == 4) {
             accessLogs = Util.readFileAndPersistLogIfNecessary(args[0]);
             startDate = Util.getStartDateFromString(args[1]);
             interval = args[2];
             threshold = Integer.valueOf(args[3]);
             if (accessLogs != null)
                 System.out.println("Access log size is " + accessLogs.size());


        } else {
            startDate = Util.getStartDateFromString(args[0]);
            interval = args[1];
            threshold = Integer.valueOf(args[2]);
        }

        try{

            if (accessLogs != null && accessLogs.size()>0) {
                System.out.println(String.format("About to persist %d records", accessLogs.size()));
                connection.setAutoCommit(false);
                StringBuffer insertBuffer = new StringBuffer();
                insertBuffer.append("insert into access_log (id, ipAddress, requestTime) values (?, ?, ?)");
                String inserSql = insertBuffer.toString();
                PreparedStatement pStmt = connection.prepareStatement(inserSql);
                for (AccessLog accessLog:accessLogs) {
                    pStmt.setString(1, null );
                    pStmt.setString(2, accessLog.getIpAddress());
                    pStmt.setTimestamp(3, (accessLog.getDate() == null) ? null :
                            new Timestamp(accessLog.getDate().getTime()));
                    pStmt.addBatch();
                }
                int successfulArray[] = pStmt.executeBatch();
                int successCount = successfulArray.length;
                System.out.println(String.format("%d records successfully persisted", successCount));
                connection.commit();
                pStmt.close();
            }
        }  catch(Exception e){
            System.out.println(e);
        }
        List<String> ipAddresses = Util.getFilteredAccessLogs(startDate, Util.getEndDate(startDate, interval), threshold, connection);

        if (ipAddresses != null && !ipAddresses.isEmpty()) {
            Util.saveBlockedIpsWithComment(ipAddresses, String.format("%s threshold crossed", interval), connection);
        }

    }


}
