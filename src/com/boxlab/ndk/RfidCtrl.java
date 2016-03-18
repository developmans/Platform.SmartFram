package com.boxlab.ndk;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.android.serialport.SerialPortFinder;
import com.android.serialport.UartThread;
import com.boxlab.platform.ActivitySourceManage;
import com.boxlab.utils.EncodingConversionUtil;

public class RfidCtrl {
	protected static final String TAG = "RfidCtrl";
	private boolean isFoundUart;
	private boolean isInRunMode;
	private SerialPortFinder mSerialPortFinder;
	private String[] uartPortNames;
	private int decLen = 0;
	private String selectUart = "/dev/ttyUSB0";
	private UartThread uartThread = null;
	private Timer decTimer;
	// 0xFF:无效,0x01:初始化,0x02:寻卡,0x03:密钥校验,0x04:读数据,0x05:写数据
	private byte cmdState;
	private byte[] decCache = new byte[512];
	private byte rtnCmdState;
	private String selectBuadrate = "115200";
	private String readerId=null;

	public RfidCtrl() {
		// ************串口初始化************//
		isFoundUart = false;
		isInRunMode = false;
		mSerialPortFinder = new SerialPortFinder();
		uartPortNames = mSerialPortFinder.getAllDevicesPath();

		if (uartPortNames[0].equals("")) // 未能找到有效串口
		{
			Log.i(TAG, "[警告]：未能找到有效的串口！");
			isFoundUart = false;
		} else {
			String nameList = "";

			isFoundUart = true;
			for (int i = 0; i < uartPortNames.length; i++) {
				nameList += uartPortNames[i].replace("/dev/", "").trim() + " ";
			}
			Log.i(TAG, "[提示]：检测到" + uartPortNames.length + "个有效串口(" + nameList
					+ ")。");
		}
		cmdState = (byte) 0xFF;
		rtnCmdState = cmdState;
		readerId="";
		// 解析数据定时器
		decTimer = new Timer();
	}

	public void init() {
		boolean isExist;

		if (!isFoundUart) {
			Log.i(TAG, "[错误]：未能找到有效串口，无法启动服务！");
			decLen = 0;
			return;
		}
		isInRunMode = !isInRunMode;
		if (isInRunMode) {
			// ************启动串口线程************//
			uartPortNames = mSerialPortFinder.getAllDevicesPath();
			isExist = false;
			for (int i = 0; i < uartPortNames.length; i++) {
				if (uartPortNames[i].contains(selectUart)) {
					isExist = true;
					break;
				}
			}
			if (!isExist) {
				Log.i(TAG, "[错误]：串口(" + selectUart + ")已丢失，请刷新后重新选择串口。");
				isInRunMode = false;
				decLen = 0;
				return;
			}
			uartThread = new UartThread(dataHandler);
			uartThread.openUart(selectUart,
					Integer.parseInt(selectBuadrate, 10));
			// 启动线程
			uartThread.start();
			Log.i(TAG, "[提示]：串口通讯线程建立成功。");
			decTimer.cancel();
			decTimer = new Timer();
			decLen = 0;
		} else {
			// ************关闭串口线程************//
			if (uartThread != null) {
				uartThread.closeUart();
				uartThread.interrupt();
				uartThread = null;
			}
			decLen = 0;
			decTimer.cancel();
			Log.i(TAG, "[提示]：关闭串口通讯线程成功！");
		}
	}
	
	public void initReader(){
		String cmd = EncodingConversionUtil.getHexStr("55 55 00 00 00 00 00 FF 03 FD D4 14 01 17 00");
		if (uartThread != null && cmd.length() > 0) {
			uartThread.sendData(EncodingConversionUtil.hexStr2ByteArray(cmd));
			cmdState = (byte) 0x01;
			decLen = 0;
		}
	}
	
	public void findRfid(){
		Log.i(TAG,"[tip]：进行寻卡操作！");
		
		String cmd = EncodingConversionUtil.trimStr("00 00 FF 04 FC D4 4A 01 00 E1 00");

		if (uartThread != null && cmd.length() > 0) {
			readerId = EncodingConversionUtil.trimStr(readerId);
			if (readerId.length() <= 0) {
				Log.i(TAG,"[错误]：请先完成初始化后，再进行寻卡操作！");
				return;
			}
			uartThread.sendData(EncodingConversionUtil.hexStr2ByteArray(cmd));
			cmdState = (byte) 0x02;
			decLen = 0;
		}
	}

	// 数据处理与显示
	final Handler dataHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			Bundle bundle;
			byte[] dataBuf;

			super.handleMessage(msg);
			switch (msg.what) {
			// 显示信息
			case 0:
				bundle = msg.getData();
				String str = bundle.getString("Message");
				Log.i(TAG, str);
				break;

			// 串口接收的数据
			case 1:
				bundle = msg.getData();
				dataBuf = bundle.getByteArray("RecvData");
				if (dataBuf.length > 0) {
					if (isInRunMode) {
						Log.i(TAG,
								"接收："+ EncodingConversionUtil.byteArray2HexStr(dataBuf,true, " "));
						if (cmdState != (byte) 0xFF) {
							decTimer.cancel();
							for (int i = 0; i < dataBuf.length; i++) {
								decCache[decLen++] = dataBuf[i];
							}
							decTimer = new Timer();
							// 设置50ms后启动数据解析任务
							setDecTimerTask();
						}
					}
				}
				break;

			// 串口发送的数据
			case 2:
				bundle = msg.getData();
				dataBuf = bundle.getByteArray("SendData");
				if (dataBuf.length > 0) {
					if (isInRunMode) {
						Log.i(TAG,
								"发送："+ EncodingConversionUtil
												.byteArray2HexStr(dataBuf,
														true, " "));
					}
				}
				break;

			// 关闭服务
			case 3:
				decLen = 0;
				if (uartThread != null) {
					uartThread.closeUart();
					uartThread.interrupt();
				}
				isInRunMode = false;
				decTimer.cancel();
				break;

			default:
				break;
			}
			super.handleMessage(msg);
		}
	};

	// 50ms后启动解析任务
	private void setDecTimerTask() {
		decTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				Message message = new Message();
				message.what = 1;
				decodeHandler.sendMessage(message);
			}
		}, 50);
	}

	// 解析数据
	final Handler decodeHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 1:
				if (decLen > 0) {
					int len;
					int id, pos, detaLen;
					int pktSid, pktFid, pktLen;
					byte calcDCS;
					byte pktLEN, pktLCS;
					// byte ktTFI, pktDCS;

					// 设置cmd返回状态
					rtnCmdState = cmdState;
					len = decLen;
					pos = 0;
					pktLen = 0;
					pktSid = -1;
					pktFid = -1;
					for (id = 0; id < len; id++) {
						// 计算剩余长度(包含当前id)
						detaLen = decLen - id;
						if (detaLen >= 6) {
							// 查找Ack
							if (decCache[id] == (byte) 0x00
									&& decCache[id + 1] == (byte) 0x00
									&& decCache[id + 2] == (byte) 0xFF
									&& decCache[id + 3] == (byte) 0x00
									&& decCache[id + 4] == (byte) 0xFF
									&& decCache[id + 5] == (byte) 0x00) {
								// 计算剩余长度(包含当前id)
								detaLen = decLen - (id + 6);
								// 满足最小解析长度
								if (detaLen >= 7) {
									for (pos = id + 6; pos < len; pos++) {
										// 查找Preamble和Start of Packet Code
										if (decCache[pos] == (byte) 0x00
												&& decCache[pos + 1] == (byte) 0x00
												&& decCache[pos + 2] == (byte) 0xFF) {
											pktSid = pos;
											// Packet Length
											pktLEN = decCache[pos + 3];
											pktLen = 1 + 2 + 1 + 1 + pktLEN + 1
													+ 1;
											// 计算剩余长度(包含当前pos)
											detaLen = decCache.length - pos;
											// 长度检查
											if (detaLen >= pktLen) {
												// Packet Length Checksum
												pktLCS = decCache[pos + 4];
												// 校验Length Checksum
												if ((byte) (pktLCS + pktLEN) == (byte) 0x00) {
													// Specific PN532 Frame
													// Identifier
													// pktTFI = decCache[pos +
													// 5];
													// Packet Data Checksum
													// pktDCS = decCache[pos + 4
													// + pktLEN + 1];
													// 校验Data Checksum
													calcDCS = 0x00;
													for (int i = pos + 5; i <= pos
															+ 4 + pktLEN + 1; i++) {
														calcDCS += decCache[i];
													}
													// 查找Postamble
													if (decCache[pos + 4
															+ pktLEN + 2] == (char) 0x00) {
														pktFid = pos + 4
																+ pktLEN + 2;
														pos = pktFid;
														id = pos;
														cmdState = (byte) 0xFF;
														if (calcDCS == (byte) 0x00) {
															byte[] frame = new byte[pktFid
																	- pktSid
																	+ 1];

															for (int i = 0; i < pktFid
																	- pktSid
																	+ 1; i++) {
																frame[i] = decCache[pktSid
																		+ i];
															}
															Log.i(TAG,
																	"..数据帧("
																			+ frame.length
																			+ "B，Passed)："
																			+ EncodingConversionUtil
																					.byteArray2HexStr(
																							frame,
																							true,
																							" "));
															if (rtnCmdState == 0x01) {
																if (pktLEN > 1) {
																	Log.i(TAG,
																			"[提示]：读卡器初始化成功！");
																	Log.i(TAG,
																			"....设备ID："
																					+ EncodingConversionUtil
																							.byte2HexStr(decCache[pktSid + 4 + 1 + 1]));
																	readerId=EncodingConversionUtil
																			.byte2HexStr(decCache[pktSid + 4 + 1 + 1]);
																} else {
																	Log.i(TAG,
																			"[提示]：读卡器初始化失败！");
																	readerId="";
																}
															} else if (rtnCmdState == (byte) 0x02) {
																byte cardIdLen;
																if (pktLEN > 1) {
																	cardIdLen = decCache[pktSid + 4 + 1 + 7];
																	byte[] cardId = new byte[cardIdLen];
																	byte[] cache = new byte[2];

																	for (int i = 0; i < cardIdLen; i++) {
																		cardId[i] = decCache[pktSid
																				+ 4
																				+ 1
																				+ 8
																				+ i];
																	}
																	for (int i = 0; i < 2; i++) {
																		cache[i] = decCache[pktSid
																				+ 4
																				+ 1
																				+ 4
																				+ i];
																	}
																	Log.i(TAG,
																			"[提示]：寻卡成功！");
																	Log.i(TAG,
																			"....Logical Number："
																					+ EncodingConversionUtil
																							.byte2HexStr(decCache[pktSid + 4 + 1 + 3]));
																	Log.i(TAG,
																			"....SENS_RES："
																					+ EncodingConversionUtil
																							.byteArray2HexStr(
																									cache,
																									true,
																									" "));
																	Log.i(TAG,
																			"....SEL_RES："
																					+ EncodingConversionUtil
																							.byte2HexStr(decCache[pktSid + 4 + 1 + 6]));
																	Log.i(TAG,
																			"....Card ID LEN："
																					+ EncodingConversionUtil
																							.byte2HexStr(decCache[pktSid + 4 + 1 + 7]));
																	Log.i(TAG,
																			"....Card ID："
																					+ EncodingConversionUtil
																							.byteArray2HexStr(
																									cardId,
																									true,
																									" "));
																	ActivitySourceManage.setReaderId(EncodingConversionUtil
																			.byteArray2HexStr(
																					cardId,
																					true,
																					" "));
																} else {
																	Log.i(TAG,
																			"[提示]：寻卡失败！");
																	ActivitySourceManage.setReaderId("");
																}
															}
														} else {
															Log.i(TAG,
																	"..数据帧("
																			+ decCache.length
																			+ "B，Failed)："
																			+ EncodingConversionUtil
																					.byteArray2HexStr(
																							decCache,
																							true,
																							" "));
														}
														break;
													}
												}
											} else {
												return;
											}
										}
									}
								} else {
									// 包含id
									for (int i = 0; i < len - id; i++) {
										decCache[i] = decCache[id + i];
									}
									decLen = len - id;
									return;
								}
							}
						} else {
							return;
						}
					}
					if (pktFid != -1) {
						// 不包含pktFid
						for (int i = 0; i < len - (pktFid + 1); i++) {
							decCache[i] = decCache[pktFid + 1 + i];
						}
						decLen = len - (pktFid + 1);
					}
				}
				break;

			default:
				break;
			}
		}
	};
}
