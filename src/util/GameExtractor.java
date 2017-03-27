package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Script to extract all available games from http://www.soongsky.com/. Created due to the unavailability of
 * Othello games from any other site or method.
 * @author Owen
 */
public class GameExtractor {

	public static void main(String[] args) {
		
		// Accept URL file if provided.
		ArrayList<String> urlsToParse = null;
		if (args.length > 0) {
			urlsToParse = FileTools.readFile(args[0]);
			System.out.println("Loading in URLs from " + args[0] + "...");
			if (urlsToParse == null) {
				System.out.println("Unable to load URLs from " + args[0] + ".");
			}
		}
		
		// Generate URLs if none.
		if (urlsToParse == null) {
			
			// Gets initial page, and generates all urls from there.
			System.out.println("Generating all URLs...");
			urlsToParse = new ArrayList<String>();
			ArrayList<String> page = trimOthelloPage(getPage("http://www.soongsky.com/en/database/index.php?year=2016"));
			ArrayList<String> allYears = getYearsList(page);
			for (String year : allYears) {
				page = trimOthelloPage(getPage("http://www.soongsky.com/en/database/index.php?year=" + year));
				ArrayList<String> tourneys = getTourneysList(page);
				for (String tournament : tourneys) {
					urlsToParse.add("http://www.soongsky.com/en/database/index.php?year=" + year + "&trn=" + tournament);
				}
			}
			
			// Writes an all URLs file for future use.
			FileTools.writeFile("games/extracted/allURLs.txt", urlsToParse);
			System.out.println("Generation complete, URLs written to games/extracted/allURLs.txt.");
		
		}
		
		// Main data extracting loop.
		System.out.println("Begining extraction on " + urlsToParse.size() + " URLs.");
		ArrayList<String> failedUrls = new ArrayList<String>();
		
		for (String url : urlsToParse) {
			
			try {
	
				// Gather data and ensure it is correct.
				ArrayList<String> page = trimOthelloPage(getPage(url));
				ArrayList<String> games = getGamesList(page);
				ArrayList<String> scores = getScoresList(page);
				
				if (games.size() != scores.size()) {
					System.out.println("Failed to extract data from " + url + ", games and scores were not of equal length.");
					failedUrls.add(url);
				} else {
					
					// Write the games to a file.
					ArrayList<String> recordedGames = new ArrayList<String>();
					for (int gameNum = 0; gameNum < games.size(); ++gameNum) {
						recordedGames.add(games.get(gameNum) + "," + scores.get(gameNum));
					}
					String fileName = "games/extracted/" + url.substring(url.indexOf("year=") + 5, url.indexOf("year=") + 9) + "-" + url.substring(url.indexOf("trn=") + 4) + ".oth";
					FileTools.writeFile(fileName, recordedGames);
					System.out.println("Success: " + recordedGames.size() + " games extracted from " + url + " to " + fileName + "!");
				}
				
			} catch (Exception e) {
				System.out.println("Failed to extract data from " + url + ", " + e.getClass().getCanonicalName() + ": " + e.getMessage());
				failedUrls.add(url);
			}
			
		}
		
		System.out.println("Extraction complete!");
		if (failedUrls.size() > 0) {
			FileTools.writeFile("games/extracted/failedURLs-" + System.currentTimeMillis() + ".txt", failedUrls);
			System.out.println(failedUrls.size() + " URLs could not be extracted from correctly; they have been written to failedURLs.txt for reference.");
		}
			
	}
	
	// Based on code found at https://docs.oracle.com/javase/tutorial/networking/urls/readingURL.html
	private static ArrayList<String> getPage(String urlString) {
		try {
			URL target = new URL(urlString);
			BufferedReader br = new BufferedReader(new InputStreamReader(target.openStream()));
			
			ArrayList<String> data = new ArrayList<String>();
			String s;
			while ((s = br.readLine()) != null) {
				data.add(s.trim());
			}
			br.close();
			return data;
		} catch (MalformedURLException e) {
			System.out.println("MalformedURLException on " + urlString + " : " + e.getMessage());
			return null;
		} catch (IOException e) {
			System.out.println("IOException on " + urlString + " : " + e.getMessage());
			return null;
		}
	}
	
	private static ArrayList<String> trimOthelloPage(ArrayList<String> page) {
		boolean trimming = true;
		ArrayList<String> newPage = new ArrayList<String>();
		for (String line : page) {
			if (line.contains("<body>")) {
				trimming = false;
			}
			if (line.contains("</body>")) {
				trimming = true;
			}
			if (!trimming) {
				newPage.add(line);
			}
		}
		return newPage;
	}
	
	private static ArrayList<String> getYearsList(ArrayList<String> page) {
		ArrayList<String> allYears = new ArrayList<String>();
		boolean found = false;
		for (String line : page) {
			if (found) {
				if (line.startsWith("</select>")) {
					break;
				} else {
					allYears.add(line.substring(line.length() - "XXXX</option>".length()).substring(0, 4));
				}
			}
			if (line.startsWith("<div class=\"nowrap\">Year")) {
				found = true;
			}
		}
		return allYears;
	}
	
	private static ArrayList<String> getTourneysList(ArrayList<String> page) {
		ArrayList<String> allTourneys = new ArrayList<String>();
		boolean found = false;
		for (String line : page) {
			if (found) {
				if (line.startsWith("</select>")) {
					break;
				} else {
					String tag = line.substring(1).split(">")[0];
					int startPoint = tag.indexOf("value=");
					allTourneys.add(tag.substring(startPoint + 6).replace("\"", ""));					
				}
			}
			if (line.startsWith("Tournament<select name=\"trn\"")) {
				found = true;
			}
		}
		return allTourneys;
	}
	
	private static ArrayList<String> getGamesList(ArrayList<String> page) {
		ArrayList<String> allGames = new ArrayList<String>();
		boolean found = false;
		for (String line : page) {
			if (found) {
				if (line.startsWith(")")) {
					break;
				} else {
					allGames.add(line.replace("\"", "").replace(",", ""));
				}
			}
			if (line.startsWith("var game")) {
				found = true;
			}
		}
		return allGames;
	}
	
	private static ArrayList<String> getScoresList(ArrayList<String> page) {
		ArrayList<String> allScores = new ArrayList<String>();
		boolean found = false;
		for (String line : page) {
			if (found) {
				if (line.startsWith("</select>")) {
					break;
				} else {
					String value = line.substring(line.indexOf(">", 1) + 1, line.indexOf("<", 1));
					allScores.add(value.substring(value.indexOf("(")).replace("(", "").replace(")", ""));
				}
			}
			if (line.startsWith("<div><select id=\"game\"")) {
				found = true;
			}
		}
		return allScores;
	}
	
}
