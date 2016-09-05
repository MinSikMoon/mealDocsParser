package minsik.docxParser;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

public class parseTest {

	public static void main(String[] args) {
		File newJson = new File("menu.txt");
		FileWriter fw;
		BufferedWriter bfw;
		try {
			fw = new FileWriter(newJson);
			bfw = new BufferedWriter(fw);
			bfw.write("hihi");
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
