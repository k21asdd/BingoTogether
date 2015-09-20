package com.example.togetbin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.example.togetbin.BingoGame.stepClick;

public class CommunicateOpponent {
	private Socket Opponent;
	private PrintWriter Oout;
	private BufferedReader Oin;
	private boolean hasOpponent = false;
	private boolean ImReady = false;
	private boolean OppReady = false;
	private boolean OppTurn = false;
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
		Log.d("Net", "Start conn to server");
		if(Opponent != null && Opponent.isConnected()){
			Opponent.close(); Opponent = null;
		}
		Opponent = s;
		Oout = new PrintWriter(Opponent.getOutputStream());
		Oin = new BufferedReader(new InputStreamReader(Opponent.getInputStream()));
		Log.d("Net", "Start conn to server done");
	}
	//Use when Create done---
	public void joinOpponent(){
		hasOpponent = true;
	}
	public void joinOpponent(final int index, final Messenger mMessenger){
		new Thread(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				super.run();
				try {
					
					CommunicateOpponent.getInstance().setChannel2Server
							(CommunicateServer.getInstance().Connection
								(index, CommunicateServer.getInstance().Connect(BingoSignal.CONNECT)));
					Oout.println(BingoSignal.CONNECT);
					Oout.flush();
					Message msg = new Message();
					msg.what = BingoSignal.CONNECT;
					mMessenger.send(msg);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
//				rM.getMessage("Connect successful");
			}
		}.start();
		hasOpponent = true;
		
	}
	public void waitForOpponent(final Messenger mMessenger){
		new Thread(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				super.run();
				try {
					int Signal = Integer.valueOf(Oin.readLine());
					if(BingoSignal.CONNECT == Signal) joinOpponent();
					Message msg = new Message();
					msg.what = BingoSignal.CONNECT;
					mMessenger.send(msg);
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}.start();
	}
	public boolean isExist(){
		return hasOpponent;
	}
	public void IsReady(){
		OppReady = true;
	}
	public void Wait_Opponent_Ready(final Messenger mMessenger){
		new Thread (){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				super.run();
				Log.d("Net", this.getName() + " READY123");
				try {
					String s = Oin.readLine();
					Log.d("Net", s);
					if(Integer.valueOf(s) == BingoSignal.GAME_READY){
						Log.d("Net", "READY");
						OppReady = true;
						Message msg = new Message();
						msg.what = BingoSignal.GAME_READY;
						mMessenger.send(msg);
					}else{
						Log.d("Net", "Not READY");
					}
					//should catch exit
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}.start();
	}
	public boolean isReady(){
		return OppReady;
	}
	public void Tell_Opponent_Im_Ready(){
		if(ImReady) return;
		Log.d("Net", "Told him im ready");
		Oout.println(BingoSignal.GAME_READY);
		Oout.flush();
		ImReady = true;
	}
	public boolean TearDown(){
		if(Opponent != null && Opponent.isClosed()) return false;
		Oout.println(BingoSignal.TEARDOWN);
		Oout.flush();
		new Thread(){
			public void run() {
				try {
					if(Integer.valueOf(Oin.readLine()) != BingoSignal.TEARDOWN){
//					Display error
						return;
					}
					Opponent.close();
					Opponent = null;
					hasOpponent = false;
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			};
		}.start();
		return true;
	}
	public boolean OppTurn(){
		return OppTurn;
	}
	public void Step(int position) throws IOException{
		Oout.println(BingoSignal.STEP);
		Oout.println(position);
		Oout.flush();
	}
	public void Exit() throws IOException{
		if(Opponent == null)return;
		hasOpponent = false;
		ImReady = false;
		OppReady = false;
		OppTurn = false;
		Opponent.close();
		Opponent = null;
	}
	
	public void Win(int position){
		Oout.println(BingoSignal.WIN);
		Oout.println(position);
		Oout.flush();
	}
	
	public void OpponentStep(final Messenger mMessenger ){
		OppTurn = true;
		new Thread(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				super.run();
				try {
					int Signal = Integer.valueOf(Oin.readLine());
					switch(Signal){
					case BingoSignal.STEP:{
							OppTurn = false;
							Message msg = new Message();
							Bundle data = new Bundle();
							data.putString("INDEX", Oin.readLine());
							msg.what = BingoSignal.STEP;
							msg.setData(data);
							mMessenger.send(msg);
							return;
						}
					case BingoSignal.WIN:{
							Oin.readLine();
							Message msg = new Message();
							msg.what = BingoSignal.WIN;
							mMessenger.send(msg);
							Exit();
							return;
					}
					case BingoSignal.TEARDOWN:
						hasOpponent = false;
						Oout.println(BingoSignal.TEARDOWN);
						Oout.flush();
						Message msg = new Message();
						msg.what = BingoSignal.TEARDOWN;
						mMessenger.send(msg);
						Exit();
						return;
					}
				} catch (NumberFormatException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}.start();
		
	}
}
