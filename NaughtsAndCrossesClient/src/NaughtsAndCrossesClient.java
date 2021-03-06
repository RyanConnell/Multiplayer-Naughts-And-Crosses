import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

import org.json.JSONObject;


public class NaughtsAndCrossesClient {
    
    
    //0 is vacant position
    //1 is occupied by X
    //2 is occupied by O
    public static ArrayList<Integer> board;
    public static Scanner in = new Scanner(System.in);

    public static String gameID = "No Game";
    public static String turn = "0";
    
    public static String gameAddress = "http://cpssd5-web.computing.dcu.ie/TicTacToeWeb/";

    public static String newGameAddress = "newGame?";
    public static String nextAddress = "next?";		//TODO: change to next once jennifer updates server side
    public static String moveAddress = "move?";
    
    public static boolean debug = false;
    
    public static void main(String[] args){
    	commandLine();
   	}
    
    public static void print(String out){
    	System.out.println(out);
    }
    
     public static void newGame(String userName){
   	 	String webText = readFromURL(gameAddress + newGameAddress + "name=" + userName);
   	 	if(debug){
   	 		print("          Starting new game with link:");
   	 		print("          " + gameAddress + newGameAddress + "name=" + userName);
   	 	}
   	 	
   	 	JSONObject JSONWebText = new JSONObject(webText);
   	 	
   	 	if(JSONWebText.has("id")){
   	   	 	gameID = JSONWebText.getString("id");
   	   	 	print("Game is started with ID: " + gameID);
   	 	}
   	 	else{
   	 		print("          Unable to start game");
   	 	}
   	 	if(JSONWebText.has("letter")){
   	   	 	turn = String.valueOf(JSONWebText.getInt("letter"));
   	   	 	print("You are player: " + turn);
   	 	}
    }
    
    
    public static String getBoard(){
    	JSONObject JSONWebText = new JSONObject(readFromURL(gameAddress + nextAddress + "id=" + gameID));
   	 	if(debug){
    		print("          Retreiving board with link:");
   	 		print("          " + gameAddress + nextAddress + "id=" + gameID);
   	 	}
   	 	
    	if(JSONWebText.has("board")){
   	   	 	return String.valueOf(JSONWebText.get("board"));
   	 	}
    	else{
    		print("Unable to retrieve board");
    		return "";
    	}
    }
    
    public static void place(String pos){
    	JSONObject JSONWebText = new JSONObject(readFromURL(gameAddress + moveAddress + "id=" + gameID + "&position=" + pos));
    	if(JSONWebText.has("status")){
    		
   	   	 	if(JSONWebText.getString("status").equalsIgnoreCase("okay")){
   	   	 		print("          Big Success! Placed at " + pos);
   	   	 	}
   	   	 	else if(JSONWebText.getString("status").equalsIgnoreCase("error")){
   	   	 		print("          NOT BIG SUCCESS!! :(");
   	   	 		print(String.valueOf(JSONWebText.getInt("code")) + ": " + JSONWebText.getString("message"));
   	   	 	}
   	 	}
    	else{
    		print("Error in status report");
    	}
    }
    
    public static String getEmptyPositions(){
    	String board = getBoard(); //Loads the page once
		String emptyPositions = "";
		
		for(int i = 0; i < board.length(); i++){
			if(board.substring(i, i+1).equalsIgnoreCase("0")){
				emptyPositions = emptyPositions + String.valueOf((i-1)/2 + 1) + ",";
			}
		}
		
    	return emptyPositions.substring(0, emptyPositions.length() - 1);
    }
    
    public static boolean isTurn(){
    	String webText = readFromURL(gameAddress + nextAddress + "id=" + gameID);
    	boolean isTurn = false;
    	if(debug){
    		print("          Checking for turns with link:");
       	 	print("          " + gameAddress + nextAddress + "id=" + gameID);
    	}

    	JSONObject JSONWebText = new JSONObject(webText);
   	 	
   	 	if(JSONWebText.has("turn")){
   	   	 	isTurn = String.valueOf(JSONWebText.getInt("turn")).equalsIgnoreCase(turn);
   	 	}
   	 	
   	 	return isTurn;
    }
    
    public static void commandLine(){
    	print("Enter a username:");
		String userName = in.nextLine();
   	 	print("Welcome " + userName.toUpperCase());
   	 	
   	 	print("would you like debug mode enabled? (Y/N)");
   	 	String Debug = in.nextLine();
   	 	
   	 	if(Debug.equalsIgnoreCase("y")){
   	 		debug = true;
   	 	}
   	 	
   	 	newGame(userName);
   	 	
   	 	game();
    }
    public static void game(){
    	print("Select Position");
   	 	print(getEmptyPositions());
   	 	String position = in.nextLine();
	   	place(position);

   	 	double startTime = System.currentTimeMillis();
		double curTime = 0;
		
		boolean isTurn = false;
		
		while (!isTurn) {
			curTime = System.currentTimeMillis();
			if (curTime - startTime > 1000.0) {
				print("TICKING...");
				startTime = curTime;
				isTurn = isTurn();
			}
		}
		
   	 	print("TICKED");
   	 	game();
    }
    
    public static String readFromURL(String url){
    	String output = "";
    	try {
    		URL page = new URL(url);
    		BufferedReader in = new BufferedReader(new InputStreamReader(page.openConnection().getInputStream()));
    		String input;
    		while ((input = in.readLine()) != null){
    			output += input;
    		}	
    		in.close();
    	}
    	catch (Exception e){
    		e.printStackTrace();
    		System.exit(0);
    	}
    	return output;
    }
}
