package com.example.donlucho;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;

@SpringBootTest
class DonluchoApplicationTests {

	@Autowired
	private DataSource dataSource;

	@Test
	void contextLoads() throws Exception {
		try (Connection conn = dataSource.getConnection()) {
			DatabaseMetaData meta = conn.getMetaData();
			String[] tables = {"usuario", "clientes", "reservas", "rol"};
			for (String table : tables) {
				System.out.println("--- COLUMNS FOR TABLE: " + table + " ---");
				try (ResultSet rs = meta.getColumns(null, null, table, null)) {
					while (rs.next()) {
						String colName = rs.getString("COLUMN_NAME");
						String colType = rs.getString("TYPE_NAME");
						int colSize = rs.getInt("COLUMN_SIZE");
						System.out.println("  " + colName + " (" + colType + ", size: " + colSize + ")");
					}
				}
			}
		}
	}

}



