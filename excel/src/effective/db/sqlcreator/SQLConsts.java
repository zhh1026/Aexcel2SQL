/**
 * @user zhh
 * @date Jul 27, 2017
 * 
 */
package effective.db.sqlcreator;

/**
 * @author zhh
 * @className effective.db.sqlcreator.SQLstatements
 *
 */
public interface SQLConsts {
	//创表语句格式、关键字等
	public static final String PRIMARY_KEY = " PRIMARY KEY ";
	public static final String SQL_ALTER_TABLE = "ALTER TABLE ";
	public static final String SQL_ADD_CONSTRAINT = " ADD CONSTRAINT ";
	public static final String CREATE_TABLE = " CREATE TABLE ";
	public static final String PRE_FIX = "(";
	public static final String SUB_FIX = ")";
	public static final String SPACE_CHAR = " ";
	public static final String COMMA = ",";
	public static final String WIN_NEXT_LINE = "\r\n";
	public static final String UNIX_NEXT_LINE = "\n";
	public static final String STATEMENT_END = ";";
	public static final String SQL_COMMENT_1 = "COMMENT ON COLUMN ";
	public static final String SQL_COMMENT_2 = " IS ";
	public static final String QUOT = "'";
	public static final String DOT = ".";
	public static final String FORMAT_1 = "\t";
	
	//约束、数据库类型、字段长度
	public static final String IS_NULL_US = "?";
	public static final String IS_NULL_ZN = "？";
	public static final String IS_NOT_NULL = "1";
	public static final String IS_STRING = "String";
	public static final String IS_STRING_UP = "STRING";
	public static final String IS_NUMBER = "Num";
	public static final String IS_VARCHAR = "F";
	public static final String IS_VARCHAR2 = "V";
	public static final String IS_DATE_YYYYMMDD = "F8";
	public static final String IS_DATE_YYYYMMDDHH24MISS = "F14";
	
	//oracle数据库类型
	public static final String ORACLE_DATE = "DATE";
	public static final String ORACLE_VARCHAR = "VARCHAR";
	public static final String ORACLE_VARCHAR2 = "VARCHAR2";
	public static final String ORACLE_NOT_NULL = "NOT NULL";
	public static final String ORACLE_NUMBER = "NUMBER";
	
	//excel对应的列
	public static final int COLUMN_NAME = 0;
	public static final int CONSTRAINT_NOT_NULL = 1;
	public static final int COLUMN_TYPE = 2;
	public static final int COLUMN_LENGTH = 3;
	public static final int COLUMN_DESC_1 = 4;
	public static final int COLUMN_DESC_2 = 5;
	
	//用户
	public static final String ACCOUNT_AICBS = "AICBS";
	
	//保存位置
	public static final String PATH_SQL_SAVE = "E:/asiainfo/sql/sql/";
	public static final String PATH_EXCEL_SAVE = "E:/asiainfo/sql/excel/";
	public static final String PATH_SQL_SPLIT_SAVE = "E:/asiainfo/sql/split/";
	public static final String PATH_SQL_SEQUENCE_SAVE = "E:/asiainfo/sql/sequence/";
	public static final String PATH_SQL_SAVE_TEST = "F:/";
	public static final String PATH_EXCEL_SAVE_TEST = "F:/AEXCEL/";
	
	
	//分表语句
	public static final String SQL_SPLIT_STAMENT_PRE = "";
	public static final String SQL_INSERT = "INSERT INTO ";
	public static final String SQL_SPLIT_STAMENT_1 = "CFG_TABLE_SPLIT";
	public static final String SQL_SPLIT_STAMENT_2 = "TABLE_NAME, TABLE_NAME_EXPR, STATE, REMARKS";
	public static final String SQL_VALUES = " VALUES ";
	public static final String SQL_SPLIT_PARAM1 = " T[TABLE]";
	public static final String SQL_UNDERLINE = "_";
	public static final String SQL_SPLIT_PARAM2 = "C[REGION_ID]";
	public static final String SQL_SPLIT_PARAM3 = "C[BILLING_CYCLE_ID]";
	public static final String SQL_STATE_U = "U";//有效
	public static final String SQL_STATE_E = "E";//失效
	
	//账期
	public static final int SQL_BILLING_CYCLE_ID_START = 201708;//今年开始的账期
	public static final int SQL_BILLING_CYCLE_ID_END = 201712;//今年开始的账期
	
	//文件类型
	public static final String FILE_TYPE_SQL =".sql";
}
