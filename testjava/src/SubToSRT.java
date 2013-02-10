import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

public class SubToSRT {
	
	static final int DELAY_MS = 2000;
	static final int MIN_SHOW_MS = 7000;  // minimal show time for a block of text.
	
	public static void main(String[] args) throws Exception {
		// output file
		PrintStream out = new PrintStream(new File("/Users/weixu/Desktop/lecture11.srt"));
		System.setErr(out);
		
		JSONReader("/Users/weixu/Desktop/test.srt.sjson");	
	}
	
	public static void JSONReader(String fileName) throws FileNotFoundException {
		
		FileReader reader = new FileReader(new File(fileName));
		
		// read the objects
		JsonParser parser = new JsonParser();
	    JsonObject obj = parser.parse(reader).getAsJsonObject();
	    JsonArray startJArr = obj.get("start").getAsJsonArray();
	    JsonArray endJArr = obj.get("end").getAsJsonArray();
	    JsonArray textJArr = obj.get("text").getAsJsonArray();
	    
	    // JSON -> Java
	    Gson gson = new Gson();
	    TypeToken<List<Long>> longList = new TypeToken<List<Long>>() {};
	    TypeToken<List<String>> stringList = new TypeToken<List<String>>() {};
		List<Long> starts = gson.fromJson(startJArr, longList.getType());
		List<Long> ends = gson.fromJson(endJArr, longList.getType());
		List<String> texts = gson.fromJson(textJArr, stringList.getType());
		
		// these arrays should be of the same size.
		assert starts.size() > 0;
		assert starts.size() == ends.size();
		assert ends.size() == texts.size();
		
		StringBuffer sb = new StringBuffer();
		SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss,SSS");
		format.setTimeZone(TimeZone.getTimeZone("GMT"));
		int cnt = 1;
		long blockStart = -1;
		
		for (int i = 0; i < ends.size(); i++) {
			if (blockStart < 0) {
				blockStart = starts.get(i);
			}
			sb.append(texts.get(i)).append(" ");
			if (ends.get(i) - blockStart < MIN_SHOW_MS) {
				continue;
			} else {
				String startTimeStr = format.format(new Date(blockStart));
				String endTimeStr = format.format(new Date(ends.get(i)));
				System.err.print((cnt++) + "\r\n");
				System.err.print(startTimeStr + " --> " + endTimeStr + "\r\n");
				System.err.print(sb.toString() + "\r\n");
				System.err.print("\r\n");
				sb.delete(0, sb.length());
				blockStart = -1;
			}
		}
	}

}
