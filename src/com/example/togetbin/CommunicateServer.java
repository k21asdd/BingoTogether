package com.example.togetbin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;

import android.util.Log;

public class CommunicateServer {
	private static final String Addr = "163.20.34.159";
	private static final int port = 5566;
	private static CommunicateServer CC = null;
	private CommunicateServer(){};
	private synchronized static void genIns(){
		if(CC == null)CC = new CommunicateServer();
	}
	public static CommunicateServer getInstance(){
		if(CC != null)return CC;
		genIns();
		return CC;
	};
	public Socket Connect(int signal){
		Socket control;
		PrintWriter out;
		try {
			control = new Socket(Addr, port);
			out = new PrintWriter(control.getOutputStream());
			out.println(signal);
			out.flush();
			return control;
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	public boolean Query(String param[], List<HashMap<String, String>> list, Socket control){
		BufferedReader in;
		try {
			in = new BufferedReader(new InputStreamReader(control.getInputStream()));
			if(Integer.valueOf(in.readLine()) == BingoSignal.QUERY){
				String data = in.readLine();
				while(data.compareTo("Q_DONE") != 0){
					String [] arr = data.split(" ");
					HashMap<String, String> newItem = new HashMap<String, String>();
					for(int i = 0; i < arr.length ; i++)
						newItem.put(param[i], arr[i]);
					list.add(newItem);
					data = in.readLine();
				}
				control.close();
				return true;
			}
			control.close();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	public int Create(String info, Socket control){
		int index = -1;
		BufferedReader in;
		PrintWriter out;
		try {
			in = new BufferedReader(new InputStreamReader(control.getInputStream()));
			out = new PrintWriter(control.getOutputStream());
			out.println(info);
			out.flush();
			
			int newPort;
			Socket Opponent;
			String pp;
			
			pp = in.readLine();
			Log.d("NewPort",  pp);
			newPort = Integer.valueOf(pp);
			Opponent = new Socket("163.20.34.159", newPort);
			index = connect2Server(Opponent);
			if(index == -1){
				//failed
			}else{
				CommunicateOpponent.getInstance().setChannel2Server(Opponent);
			}
			control.close();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return index;
	}
	private int connect2Server(Socket s){
		BufferedReader in;
		try {
			in = new BufferedReader(new InputStreamReader(s.getInputStream()));
			if((Integer.valueOf(in.readLine()) == 
					BingoSignal.CREATE) && 
					(in.readLine().compareTo("Q_OK") == 0)){
				return Integer.valueOf(in.readLine());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
	}
	public boolean Teardown(int index, Socket control){
		try {
			new PrintWriter(control.getOutputStream(), true).println(index);
			String sig = new BufferedReader(new InputStreamReader(control.getInputStream())).readLine();
			control.close();
			if(Integer.valueOf(sig) == BingoSignal.TEARDOWN)
				return true;
			else
				return false;
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
//	public boolean Connect(){
//		
//	}
}
