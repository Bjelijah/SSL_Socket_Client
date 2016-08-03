package com.howell.utils;

public interface IConst {

	final int MSG_LOGIN_CAM_OK       = 0x00;
	
	
//	final String TEST_ACCOUNT   	= "bxm555";
	final String TEST_ACCOUNT   	= "howelltest";
	final String TEST_PASSWORD		= "howelltest";
	
	
	
	
	final String TEST_IP = "192.168.18.245";//no used
	final int TEST_TURN_SERCICE_PORT = 8862;
	
	
	
	final static int MSG_LOGIN_OK 	=  0xa0;
	final static int MSG_LOGIN_FAIL = 0xa1;
	final static int MSG_CONNECT_OK = 0xa2;
	final static int MSG_SOCK_CONNECT = 0xa3;
	final static int MSG_TURN_CONNECT_OK = 0xa4;
	final static int MSG_TURN_CONNECT_FAIL = 0xa5;
	
	final static int MSG_HTTPS_TEST = 0xea;
}
