package com.boxlab.utils;

import android.util.Log;

import com.boxlab.bean.Sensor;

/** 
 * @author Next 
 * @version 1.0
 * E-mail: caiwl2005@126.com
 * ����ʱ�䣺2015-11-6 ����3:16:44 
 * ��˵�� 
 */


public class PinIO{
	
	private static final String TAG = "IOPin";
	
	public static final int IO_PORT_0 = 0;
	public static final int IO_PORT_1 = 1;
	
	public static final int IO_PIN_4 = 4;
	public static final int IO_PIN_5 = 5;
	public static final int IO_PIN_6 = 6;
	public static final int IO_PIN_7 = 7;
	
	public static final int DIRECT_INPUT = 0;
	public static final int DIRECT_OUTPUT = 1;
	
	public static final int LEVEL_LOW = 0;
	public static final int LEVEL_HIGH = 1;

	private static final int INIT_DIR = 0;
	private static final int INIT_LEV = 0;
	
	public int ioDir = INIT_DIR;
	public int ioLev = INIT_LEV;
	
	public PinIO(int iDirAndLev){
		this(iDirAndLev & 0xFF, (iDirAndLev >> 8) & 0xFF );
	}
	
	public PinIO(int iDir,int iLev){
		this.ioDir = iDir;
		this.ioLev = iLev;
	}
	
	public PinIO reset(int iDir, int iLev) {
		this.ioDir = INIT_DIR;
		this.ioLev = INIT_LEV;
		return this;
	}
	
	/**
	 * ���ýڵ�IO״̬�����뻹��������ߵ�ƽ���ǵ͵�ƽ
	 * 
	 * @param port      Ҫ���õĶ˿� 
	 *                  ��Χ����0��1 <br>
	 *                  {@link #IO_PORT_0}���˿�P0 <br>
	 *                  {@link #IO_PORT_1}���˿�P1 <br>
	 *                  
	 * @param pin      Ҫ���õ�ָ���˿ڵ����ź�
	 *                  ��Χ����7��4 <br>
	 *                  {@link #IO_PIN_7}������7 <br>
	 *                  {@link #IO_PIN_6}������6 <br>
	 *                  {@link #IO_PIN_6}������5 <br>
	 *                  {@link #IO_PIN_4}������4 <br>
	 *        ���� portΪ1��nΪ6������Ҫ���õĶ˿�����ΪP1.6 <br>
	 *               
	 * @param ioDirType  Ҫ���õĶ˿����ŵķ��� </br>
	 *                  {@link #DIRECT_INPUT}��   ����  <br>
	 *                  {@link #DIRECT_OUTPUT}�����  <br>
	 *                  
	 * @param ioLevType  ��Ҫ���õĶ˿����ŵķ���Ϊ1�������ʱ������ioLevָ��Ҫ���õĵ�ƽֵ <br>
	 *                  {@link #LEVEL_LOW}��   �ߵ�ƽ <br>
	 *                  {@link #LEVEL_HIGH}���͵�ƽ  <br>
	 *                  
	 */
	public PinIO set(int port, int pin, int ioDirType, int ioLevType) {
		// ��������Χ
		if(port < IO_PORT_0 || 
				port > IO_PORT_1 ||
				pin < IO_PIN_4 || 
				pin > IO_PIN_7 ||
				ioDirType < DIRECT_INPUT || 
				ioDirType > DIRECT_OUTPUT ||
				ioLevType < LEVEL_LOW || 
				ioLevType > LEVEL_HIGH){
			return this;
		}
		
		int bPos;
		
		if(port == IO_PORT_0){
			bPos = pin - 4;
		}else if(port == IO_PORT_1){
			bPos = pin;
		}else{
			return this;
		}
		
		// ����IO����
		if(ioDirType == DIRECT_INPUT){
			
			this.ioDir &= ~(1 << bPos);
			
		}else if(ioDirType == DIRECT_OUTPUT){
			
			this.ioDir |= (1 << bPos);
			
			// ����IO��ƽ
			if(ioLevType == LEVEL_LOW){
				this.ioLev &= ~(1 << bPos);
			}else if(ioLevType == LEVEL_HIGH){
				this.ioLev |= (1 << bPos);
			}
		}
		
		return this;
	}
	
	public int getDerct(int port, int pin) {
		
		if(port < IO_PORT_0 || 
				port > IO_PORT_1 ||
				pin < IO_PIN_4 || 
				pin > IO_PIN_7 )
			return -1;

		int bPos;
		
		if(port == IO_PORT_0){
			bPos = pin - 4;
		}else if(port == IO_PORT_1){
			bPos = pin;
		}else{
			return -1;
		}
		
		return ((this.ioDir >> bPos) & 0x01);
		
	}

	public int getLevel(int port, int pin) {

		if(port < IO_PORT_0 || 
				port > IO_PORT_1 ||
				pin < IO_PIN_4 || 
				pin > IO_PIN_7 )
			return -1;

		int bPos;
		
		if(port == IO_PORT_0){
			bPos = pin - 4;
		}else if(port == IO_PORT_1){
			bPos = pin;
		}else{
			return -1;
		}
		
		return ((this.ioLev >> bPos) & 0x01);
	}
	
	public int getSendCmdCache() {
		return ((this.ioDir & 0xff) << 8)| (this.ioLev & 0xff);
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return Sensor.ioState(this.ioDir, this.ioLev);
	}

	/** ��������	 */
	public static final int ROLL_BLIND_TYPE = 0;
	/** �������Ʒ��� ��ת	 */
	public static final int ROLL_BLIND_STATE_POSITIVE = 1;
	/** ��������ֹͣ  */
	public static final int ROLL_BLIND_STATE_STOP     = 0;
	/** ������������ ��ת	 */
	public static final int ROLL_BLIND_STATE_NEGATIVE = -1;
	
	private static final int ROLL_BLIND_PORT1 = IO_PORT_1;
	private static final int ROLL_BLIND_PIN1 = IO_PIN_6;
	private static final int ROLL_BLIND_PORT2 = IO_PORT_1;
	private static final int ROLL_BLIND_PIN2 = IO_PIN_7;
	
	public static int get(PinIO ioState, int type) {
		
		int state1 = 2;
		int state2 = -2;
		
		Log.w(TAG, "PinIO.get()");
		
		Log.w(TAG, "  Roll Pin1 Dir: " + (ioState.getDerct(ROLL_BLIND_PORT1, ROLL_BLIND_PIN1) == DIRECT_OUTPUT) + 
				 "\n  Roll Pin2 Dir: " + (ioState.getDerct(ROLL_BLIND_PORT2, ROLL_BLIND_PIN2) == DIRECT_OUTPUT) );
		
		if((ioState.getDerct(ROLL_BLIND_PORT1, ROLL_BLIND_PIN1) == DIRECT_OUTPUT) &&
		   (ioState.getDerct(ROLL_BLIND_PORT2, ROLL_BLIND_PIN2) == DIRECT_OUTPUT)){
			
			if(ioState.getLevel(ROLL_BLIND_PORT1, ROLL_BLIND_PIN1) == LEVEL_HIGH){
				state1 = 1;
			}else{
				state1 = 0;
			}
			
			if(ioState.getLevel(ROLL_BLIND_PORT2, ROLL_BLIND_PIN2) == LEVEL_HIGH){
				state2 = 1;
			}else{
				state2 = 0;
			}
			
			Log.w(TAG, "  Roll Pin1 Lev: " + state1 + 
					 "\n  Roll Pin2 Lev: " + state2 );
			
		}

		return state1 - state2;
		
	}
	
	public static void set(PinIO ioState, int type, int state) {
		
		//Log.w(TAG, "PinIO.set()");

		switch (state) {
		case ROLL_BLIND_STATE_POSITIVE:
			ioState.set(ROLL_BLIND_PORT1, ROLL_BLIND_PIN1, DIRECT_OUTPUT, LEVEL_HIGH)
			       .set(ROLL_BLIND_PORT2, ROLL_BLIND_PIN2, DIRECT_OUTPUT, LEVEL_LOW);
			break;

		case ROLL_BLIND_STATE_STOP:
			ioState.set(ROLL_BLIND_PORT1, ROLL_BLIND_PIN1, DIRECT_OUTPUT, LEVEL_HIGH)
			       .set(ROLL_BLIND_PORT2, ROLL_BLIND_PIN2, DIRECT_OUTPUT, LEVEL_HIGH);
			break;
			
		case ROLL_BLIND_STATE_NEGATIVE:
			ioState.set(ROLL_BLIND_PORT1, ROLL_BLIND_PIN1, DIRECT_OUTPUT, LEVEL_LOW)
		           .set(ROLL_BLIND_PORT2, ROLL_BLIND_PIN2, DIRECT_OUTPUT, LEVEL_HIGH);
			break;
			
		default:
			break;
		}
	}
	
}















