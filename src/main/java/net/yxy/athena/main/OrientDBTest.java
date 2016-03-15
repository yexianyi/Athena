package net.yxy.athena.main;

import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.record.impl.ODocument;

public class OrientDBTest {

	public static void main(String[] args) throws InterruptedException {
		ODatabaseDocumentTx db = new ODatabaseDocumentTx ("plocal:/tmp/databases/petshop");
		db.open("admin", "admin");
		
		// CREATE A NEW DOCUMENT AND FILL IT
		ODocument doc = new ODocument("Person");
		doc.field( "name", "Luke" );
		doc.field( "surname", "Skywalker" );
		doc.field( "city", new ODocument("City").field("name","Rome").field("country", "Italy") );

		// SAVE THE DOCUMENT
		doc.save();
		db.close();
	}

}
