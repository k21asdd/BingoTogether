package com.example.togetbin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import com.example.togetbin.BingoGame.receiveMessage;

public class CommunicateOpponent {
	private Socket Opponent;
	private PrintWriter Oout;
	private BufferedReader Oin;
	private boolean hasOpponent = false;
	private boolean IsReady = false;
	private static CommunicateOpponent CC = null;
	private synchronized static void genIns(){
		if(CC == null)CC = new CommunicateOpponent();
	}
	public static CommunicateOpponent getInstance(){
		if(CC != null)return CC;
		genIns();
		return CC;
	};
	//Use when Create done---
	public void setChannel2Server(Socket s) throws IOException{
		if(Opponent != null && Opponent.isConnected()){
			Opponent.close(); Opponent = null;
		}
		Opponent = s;
		Oout = new PrintWriter(Opponent.getOutputStream());
		Oin = new BufferedReader(new InputStreamReader(Opponent.getInputStream()));
	}
	//Use when Create done---
	public void OpponentReady(){
		IsReady = true;
	}
	public void joinOpponent(){
		hasOpponent = true;
	}
	public boolean isExist(){
		return hasOpponent;
	}
	public Socket getOpponent(){
		return Opponent;
	}
	public boolean TearDown() throws IOException{
		if(Opponent != null && Opponent.isClosed()) return false;
		Oout.println(BingoSignal.TEARDOWN);
		Oout.flush();
		if(Integer.valueOf(Oin.readLine()) != BingoSignal.TEARDOWN){
//			Display error
			return false;
		}
		Opponent.close();
		Opponent = null;
		return true;
	}
	public void Step(int position) throws IOException{
		Oout.println(BingoSignal.STEP);
		Oout.println(position);
		Oout.flush();
	}
	public void Restart(){
		
	}
	public boolean isReady() throws NumberFormatException, IOException{
		return Integer.valueOf(Oin.readLine()) == BingoSignal.GAME_READY;
	}
	public void Tell_Opponent_Im_Ready(){
		Oout.println(BingoSignal.GAME_READY);
		Oout.flush();
	}
	public void Win(int position){
		Oout.println(BingoSignal.WIN);
		Oout.println(position);
		Oout.flush();
	}
	public void waitForOpponent(final receiveMessage rM){
		new Thread(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				super.run();
				try {
					int Signal = Integer.valueOf(new BufferedReader(new InputStreamReader(Opponent.getInputStream())).readLine());
					if(BingoSignal.CONNECT == Signal) joinOpponent();
					rM.getMessage("Get Opponent !");
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}.start();
	}
	public int OpponentStep(receiveMessage rM){
		try {
			int Signal = Integer.valueOf(Oin.readLine());
			switch(Signal){
			case BingoSignal.STEP:
				rM.getMessage("Your Turn !");
				return Integer.valueOf(Oin.readLine());
			case BingoSignal.WIN:
				rM.getMessage("Your Lose !");
				return -1;
			case BingoSignal.TEARDOWN:
				rM.getMessage("Opponent just run away !");
				return -1;
			}
		} catch (NumberFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		rM.getMessage("Something wrong !");
		return -2;
	}
}
