/**
 * 
 */


import java.net.UnknownHostException;
import java.util.Set;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;

/**
 * @author tony
 *
 */
public class MongoFilesOP {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		MongoFilesOP mop = new MongoFilesOP();
		mop.getFiles(null);
	}
	
	void getFiles(String fName){
		try {
			MongoClient mdb = new MongoClient();
			DB db =  mdb.getDB("test");
			Set<String> colls = db.getCollectionNames();

			for (String s : colls) {
			    System.out.println(s);
			}
			//get a collection and insert a object
			DBCollection coll = db.getCollection("xdd");
			BasicDBObject doc = new BasicDBObject("name", "MongoDB").
                    append("type", "database").
                    append("count", 1).
                    append("info", new BasicDBObject("x", 203).append("y", 102));

			coll.insert(doc);
			
			//get fs count
			coll = db.getCollection("fs.files");
			DBCursor cursor = coll.find().skip(234);
			int count = 0;
			try {
			   while(cursor.hasNext()) {
				   System.out.println("There has count:"+cursor.count());
//				   System.out.println("Skip:"+cursor.skip(14));
				   if(count++==0){
					   System.out.println(cursor.next());
					   break;
				   }
			   }
			} finally {
			   cursor.close();
			}
			mdb.close();
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
