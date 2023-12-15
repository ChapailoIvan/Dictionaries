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
                	revenue REAL
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
            INSERT INTO manufacture (id, name, country, email, website, zipCode, dateFounding) VALUES
            (1, 'ivanioo', 'Belarus', 'vchapailo@gmail.com', 'by.chapailo', 220140, '24.07.2002'),
            (2, 'ivanioo', 'Belarus', 'vchapailo@gmail.com', 'by.chapailo', 220140, '24.07.2002');
        """.trimIndent()
    )

    database.execSQL(
        """
                INSERT INTO sparePart (id, manufactureId, description, dateProduction, price) VALUES (503798, 1, 'Spark Plug', '10.01.2023', 5.99);
            """.trimIndent()
    )

    database.execSQL(
        """
                INSERT INTO sparePart (id, manufactureId, description, dateProduction, price) VALUES (257476, 2, 'Brake Pad', '15.02.2023', 19.99);
            """.trimIndent()
    )

}