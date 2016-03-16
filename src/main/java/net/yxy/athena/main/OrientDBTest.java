package net.yxy.athena.main;

import java.io.File;

import com.orientechnologies.orient.server.OServer;
import com.orientechnologies.orient.server.OServerMain;

public class OrientDBTest {

	public static void main(String[] args) throws Exception {

		OServer server = OServerMain.create();
//		OServerConfiguration cfg = new OServerConfiguration();
		// FILL THE OServerConfiguration OBJECT
		server.startup(new File("src/main/resources/db.config.xml"));
		server.activate();

		// ODatabaseDocumentTx db = new ODatabaseDocumentTx
		// ("plocal:/tmp/databases/petshop");
		// db.open("admin", "admin");
		//
		// // CREATE A NEW DOCUMENT AND FILL IT
		// ODocument doc = new ODocument("Person");
		// doc.field( "name", "Luke" );
		// doc.field( "surname", "Skywalker" );
		// doc.field( "city", new
		// ODocument("City").field("name","Rome").field("country", "Italy") );
		//
		// // SAVE THE DOCUMENT
		// doc.save();
		// db.close();
	}

}
