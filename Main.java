package main;

import database.DatabaseManager;
import ui.Menu;

public class Main {
    public static void main(String[] args) {
        DatabaseManager db = DatabaseManager.getInstance();
        db.initializeDatabase();
        db.insertSampleData();

        Menu menu = new Menu();
        menu.start();
    }
}
