package com.boxlab.utils;

import android.util.Log;

import com.boxlab.bean.Sensor;

/** 
 * @author Next 
 * @version 1.0
 * E-mail: caiwl2005@126.com
 * 创建时间：2015-11-6 下午3:16:44 
 * 类说明 
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
	 * 设置节点IO状态：输入还是输出、高电平还是低电平
	 * 
	 * @param port      要设置的端口 
	 *                  范围：从0到1 <br>
	 *                  {@link #IO_PORT_0}：端口P0 <br>
	 *                  {@link #IO_PORT_1}：端口P1 <br>
	 *                  
	 * @param pin      要设置的指定端口的引脚号
	 *                  范围：从7到4 <br>
	 *                  {@link #IO_PIN_7}：引脚7 <br>
	 *                  {@link #IO_PIN_6}：引脚6 <br>
	 *                  {@link #IO_PIN_6}：引脚5 <br>
	 *                  {@link #IO_PIN_4}：引脚4 <br>
	 *        例如 port为1，n为6，代表要设置的端口引脚为P1.6 <br>
	 *               
	 * @param ioDirType  要设置的端口引脚的方向 </br>
	 *                  {@link #DIRECT_INPUT}：   输入  <br>
	 *                  {@link #DIRECT_OUTPUT}：输出  <br>
	 *                  
	 * @param ioLevType  当要设置的端口引脚的方向为1，即输出时，参数ioLev指定要设置的电平值 <br>
	 *                  {@link #LEVEL_LOW}：   高电平 <br>
	 *                  {@link #LEVEL_HIGH}：低电平  <br>
	 *                  
	 */
	public PinIO set(int port, int pin, int ioDirType, int ioLevType) {
		// 检查参数范围
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
		
		// 处理IO方向
		if(ioDirType == DIRECT_INPUT){
			
			this.ioDir &= ~(1 << bPos);
			
		}else if(ioDirType == DIRECT_OUTPUT){
			
			this.ioDir |= (1 << bPos);
			
			// 处理IO电平
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

	/** 卷帘控制	 */
	public static final int ROLL_BLIND_TYPE = 0;
	/** 卷帘控制放下 正转	 */
	public static final int ROLL_BLIND_STATE_POSITIVE = 1;
	/** 卷帘控制停止  */
	public static final int ROLL_BLIND_STATE_STOP     = 0;
	/** 卷帘控制收起 反转	 */
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















