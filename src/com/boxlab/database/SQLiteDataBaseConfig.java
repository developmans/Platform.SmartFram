package com.boxlab.database;

/** 
 * @author Next 
 * @version 1.0
 * E-mail: caiwl2005@126.com
 * 创建时间：2015-9-15 下午6:48:39 
 * 类说明 
 */

public final class SQLiteDataBaseConfig {

	public static final int VERSION = 4;
	protected static final String DATABASE_NAME = "iot.sqlite";
	protected static final String TABLE_NAME_NET_INFO = "NetInfo";
	protected static final String TABLE_DDL_NET_INFO = "CREATE TABLE NetInfo ( "
	                                                  + "ID      INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                                                      + "C_NA    INTEGER UNIQUE,"
                                                      + "C_IEEE  INTEGER UNIQUE,"
                                                      + "P_NA    INTEGER,"
                                                      + "P_IEEE  INTEGER,"
                                                      + "C_PANID INTEGER,"
                                                      + "C_VER   INTEGER,"
                                                      + "PROFILE INTEGER )";
	
	protected static final String TABLE_NAME_SENSOR_INFO = "SensorInfo";
	protected static final String TABLE_DDL_SENSOR_INFO = "CREATE TABLE SensorInfo ( " 
			                                          + "ID          INTEGER    PRIMARY KEY AUTOINCREMENT NOT NULL,"
			                                          + "C_NA        INTEGER    REFERENCES NetInfo ( C_NA ) ON DELETE CASCADE," 
			                                          + "SENSOR_TYPE INTEGER    NOT NULL DEFAULT ( 0 ),"
			                                          + "SENSOR_DATA BLOB( 11 ) NOT NULL DEFAULT ( 0 )," 
			                                          + "POWER       INTEGER    NOT NULL DEFAULT ( 0 )," 
			                                          + "TIMESTAMP   DATETIME   NOT NULL DEFAULT ( ( datetime( 'now', 'localtime' )  )  ) )";
	
	
}
