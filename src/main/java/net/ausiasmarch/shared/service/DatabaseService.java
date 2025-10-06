package net.ausiasmarch.shared.service;


public class DatabaseService {

    public static final String DB_URL =
        "jdbc:mysql://127.0.0.1:3306/torbesa?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    public static final String DB_USER = "root";       
    public static final String DB_PASS = "root";   

    public static final int DB_MAX_POOL_SIZE = 10;
    public static final int DB_MIN_POOL_SIZE = 5;
}
