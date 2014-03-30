package sdis;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

public class DBS {
	private static SplitMessage m;
	private static Menu menu;
	public static void main(String[] args) throws Exception {
		menu=new Menu();
		menu.menu(args);
		
	}
}
