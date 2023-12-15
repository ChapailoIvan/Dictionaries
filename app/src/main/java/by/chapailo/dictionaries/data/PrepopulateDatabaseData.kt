package by.chapailo.dictionaries.data

import android.database.sqlite.SQLiteDatabase
import by.chapailo.dictionaries.domain.safeQuery

fun createDatabaseTables(database: SQLiteDatabase?) = safeQuery {
    if (database == null) {
        return@safeQuery
    }

    database.execSQL(
        """
            CREATE TABLE sparePart (
	            id INTEGER NOT NULL,
	            manufactureId INTEGER NOT NULL,
	            description TEXT NOT NULL,
	            dateProduction TEXT NOT NULL,
	            price REAL NOT NULL,
	            PRIMARY KEY (id, manufactureId),
	            FOREIGN KEY (manufactureId) REFERENCES manufacture(id)
            );          
        """.trimIndent()
    )

    database.execSQL(
        """
                CREATE TABLE manufacture (
                	id INTEGER PRIMARY KEY NOT NULL,
                	name TEXT NOT NULL,
                	country TEXT NOT NULL,
                	email TEXT NOT NULL,
                	website TEXT NOT NULL,
                	zipCode INTEGER NOT NULL,
                	dateFounding TEXT NOT NULL,
                	revenue NUMERIC(5,2)
                );
            """.trimIndent()
    )
}

fun prepopulateData(database: SQLiteDatabase?) = safeQuery {
    if (database == null) {
        return@safeQuery
    }

    database.execSQL(
        """
            INSERT INTO manufacture (id, name, country, email, website, zipCode, dateFounding, revenue) VALUES
                (23982, 'PartMaster Inc.', 'USA', 'info@partmaster.com', 'www.partmaster.com', 12345, date('2000-01-15'), 50000.00),
                (48054, 'AutoTech Components', 'Canada', 'info@autotech.com', 'www.autotechcomponents.com', 54321, date('1995-03-22'), 75000.50),
                (78346, 'Precision Parts Solutions', 'UK', 'info@precisionpartssolutions.com', 'www.precisionpartssolutions.com', 67890, date('2010-12-10'), 10000.75),
                (35895, 'SpareMakers Co.', 'Germany', 'info@sparemakers.com', 'www.sparemakers.com', 98765, date('2005-07-05'), 80000.25);
        """.trimIndent()
    )

    database.execSQL(
        """
        INSERT INTO sparePart (id, manufactureId, description, dateProduction, price) VALUES 
                (503798, 23982, 'Spark Plug', '2023-01-10', 5.99),
                (257476, 48054, 'Brake Pad', '2023-02-15', 19.99),
                (443175, 48054, 'Oil Filter', '2023-03-20', 7.49),
                (778749, 78346, 'Air Filter', '2023-04-25', 8.99),
                (184468, 35895, 'Tire', '2023-05-30', 49.99);
        """.trimIndent()
    )

}