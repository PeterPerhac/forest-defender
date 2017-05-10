package com.perhac.forestdefender.utils;

import java.util.Scanner;
import pulpcore.Assets;
import pulpcore.math.Path;

public class PathReader {

    public static Path readSVGPath(String resource) {
	StringBuilder sb = new StringBuilder();
	Scanner scanner = new Scanner(Assets.getAsStream(resource));
	while (scanner.hasNextLine()) {
	    sb.append(scanner.nextLine());
	}
	return new Path(sb.toString());
    }

}
