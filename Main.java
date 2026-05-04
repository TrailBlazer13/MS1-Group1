package main;

import database.GuildDatabase;
import ui.Menu;

public class Main {
public static void main(String[] args) {
    GuildDatabase db = GuildDatabase.getInstance();
    db.initializeDatabase();
    db.insertSampleData();
    Menu menu = new Menu();
    menu.start();
 }
}
