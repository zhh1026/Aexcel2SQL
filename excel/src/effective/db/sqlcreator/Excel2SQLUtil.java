/**
 * @user zhh
 * @date Jul 27, 2017
 * 
 */
package effective.db.sqlcreator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author zhh
 * @className effective.db.sqlcreator.Excel2SQLUtil
 *
 */
/**
 * @author VampireZHH
 *
 */
public class Excel2SQLUtil {
	private static final Log _log = LogFactory.getLog(Excel2SQLUtil.class);
	private static  final String[] REGIONIDS = new String[] { "570", "571", "572",
			"573", "574", "575", "576", "577", "578", "579", "580" };

	
	public static void transExcel2SQL_CREATE_TABLE_AND_generateTableStructureExcel(
			String excelFilePath, boolean isSplitTableByRegionId, boolean isSplitTableByBillingCycleId) throws RowsExceededException, WriteException {
		File excelFile = new File(excelFilePath);
		if (!excelFile.exists()) {
			_log.error("The Input Excel File Doesn't Exist,excel文件不存在");
			return;
		}
		try {
			// 构建Workbook对象, 只读Workbook对象
			// 直接从本地文件创建Workbook
			InputStream inStream = new FileInputStream(excelFile);
			Workbook readWb = null;
			readWb = Workbook.getWorkbook(inStream);
			int sheetCounts = readWb.getNumberOfSheets();
			// 读取所有的sheet
			for (int sheet = 0; sheet < sheetCounts; sheet++) {
				Sheet readSheet = readWb.getSheet(sheet);
				// 获取Sheet表中所包含的总列数
				int columns = readSheet.getColumns();
				// 获取Sheet表中所包含的总行数
				int rows = readSheet.getRows();
				if (rows == 0 || columns == 0) {
					_log.warn("Can't Resolve, There is zero row or column;无法解析excel，无数据，0行或0列");
					return;
				}
			
				StringBuilder sqlStr = new StringBuilder();
				// 添加create table
				sqlStr.append(SQLConsts.CREATE_TABLE);
				// excel第一行第一列为表名
				String tableName = readSheet.getCell(0, 0).getContents();
				sqlStr.append(tableName);
				sqlStr.append(SQLConsts.PRE_FIX);
				// 用于存储表的列名
				String[] columnNames = new String[rows];
				// 用于表达此列非空约束
				String[] columnIsNotNull = new String[rows];
				// 用于表达此列的数据库类型
				String[] columnTypes = new String[rows];
				// 用于表达此列的长度约束
				String[] columnLengths = new String[rows];
				// 用于描述此列的备注1
				String[] columnRemarks1 = new String[rows];
				// 用于描述此列的备注2
				String[] columnRemarks2 = new String[rows];
				_log.info("共" + rows + "行," + columns + "列");
				for (int i = 1, k = 0; i < rows && k < rows - 1; i++, k++) {
					for (int j = 0; j < columns; j++) {
						Cell cell = readSheet.getCell(j, i);
						String cellContent = cell.getContents().trim();
						switch (j) {
						case SQLConsts.COLUMN_NAME:
							columnNames[k] = cellContent.toUpperCase();
							break;
						case SQLConsts.CONSTRAINT_NOT_NULL:
							if (SQLConsts.IS_NOT_NULL.equals(cellContent)) {
								columnIsNotNull[k] = SQLConsts.ORACLE_NOT_NULL;
							} else if (SQLConsts.IS_NULL_US.equals(cellContent)
									|| SQLConsts.IS_NULL_ZN.equals(cellContent)) {
								columnIsNotNull[k] = SQLConsts.SPACE_CHAR;
							} else {
								_log.error("只允许输入1或？来表达非空约束" + sheet + "页"
										+ (i + 1) + "行" + (j + 1) + "列");
								return;
							}
							break;
						case SQLConsts.COLUMN_TYPE:
							if (SQLConsts.IS_STRING.equals(cellContent)) {
								columnTypes[k] = SQLConsts.ORACLE_VARCHAR2;
							} else if (SQLConsts.IS_NUMBER.equals(cellContent)) {
								columnTypes[k] = SQLConsts.ORACLE_NUMBER;
							} else if ( "DATE".equals(cellContent) ) {
								columnTypes[k] = " DATE ";							}
							else {
								_log.error("数据类型输入错误,只允许输入String或Num");
								return;
							}
							break;
						case SQLConsts.COLUMN_LENGTH:
							if (cellContent.length() < 1) {
								_log.error(sheet + "页" + (i + 1) + "行"
										+ (j + 1) + "列" + "长度输入错误,例如V20或F8");
								return;
							}
							if (!cellContent.startsWith(SQLConsts.IS_VARCHAR)
									&& !cellContent
											.startsWith(SQLConsts.IS_VARCHAR2)) {
								_log.error(sheet + "页" + (i + 1) + "行"
										+ (j + 1) + "列" + "长度输入错误,必须以F或V开头");
								return;
							}
							String lengthStr = cellContent.substring(1);
							if(columnTypes[k].equals(" DATE ")){
								columnLengths[k] = " ";
								break;
							}
							if (lengthStr.indexOf('.') != -1) {
								lengthStr = lengthStr.substring(0,
										lengthStr.indexOf('.'));
							}
							if (cellContent.startsWith(SQLConsts.IS_VARCHAR2)) {
								columnLengths[k] = lengthStr;
							} 
								
							else 
								/*if (cellContent
									.equals(SQLConsts.IS_DATE_YYYYMMDD)
									|| cellContent
											.equals(SQLConsts.IS_DATE_YYYYMMDDHH24MISS)) {
								columnTypes[k] = SQLConsts.ORACLE_DATE;
							} else */{
								columnTypes[k] = SQLConsts.ORACLE_VARCHAR;
								columnLengths[k] = lengthStr;
							}
							break;
						case SQLConsts.COLUMN_DESC_1:
							columnRemarks1[k] = cellContent;
							break;
						case SQLConsts.COLUMN_DESC_2:
							columnRemarks2[k] = cellContent;
							break;
						default:
							_log.error("列数据错误" + sheet + "页" + (i + 1) + "行"
									+ (j + 1) + "列");
							return;
						}
					}
				}

				// 拼接建表语句
				for (int i = 0; i < rows - 1; i++) {
					sqlStr.append(SQLConsts.WIN_NEXT_LINE)
							.append(SQLConsts.FORMAT_1).append(columnNames[i])
							.append(SQLConsts.SPACE_CHAR)
							.append(columnTypes[i]);
					if (!" DATE ".equals(columnTypes[i])) {
						sqlStr.append(SQLConsts.PRE_FIX)
								.append(columnLengths[i])
								.append(SQLConsts.SUB_FIX);
					}
					sqlStr.append(SQLConsts.SPACE_CHAR).append(
							columnIsNotNull[i]);
					if (i < rows - 2) {
						sqlStr.append(SQLConsts.COMMA);
					}
				}
				sqlStr.append(SQLConsts.WIN_NEXT_LINE);
				sqlStr.append(SQLConsts.SUB_FIX);
				sqlStr.append(SQLConsts.STATEMENT_END);

				// 把null转为""
				for (int i = 0; i < rows - 1; i++) {
					if (columnRemarks1[i] == null) {
						columnRemarks1[i] = "";
					}
					if (columnRemarks2[i] == null) {
						columnRemarks2[i] = "";
					}
				}

				/*String pkName = tableName.substring(tableName.indexOf('.') + 1)
						+ "_PK";
				sqlStr.append(SQLConsts.WIN_NEXT_LINE)
						.append(SQLConsts.SQL_ALTER_TABLE).append(tableName)
						.append(SQLConsts.SQL_ADD_CONSTRAINT).append(pkName)
						.append(SQLConsts.PRIMARY_KEY)
						.append(SQLConsts.PRE_FIX).append(columnNames[0])
						.append(SQLConsts.SUB_FIX)
						.append(SQLConsts.STATEMENT_END);*/
				
				
				sqlStr.append(SQLConsts.WIN_NEXT_LINE)
				.append(SQLConsts.SQL_ALTER_TABLE).append(tableName)
//				.append(SQLConsts.SQL_ADD_CONSTRAINT).append(pkName)
				.append(" ADD ")
				.append(SQLConsts.PRIMARY_KEY)
				.append(SQLConsts.PRE_FIX).append(columnNames[0])
				.append(SQLConsts.SUB_FIX)
				.append(SQLConsts.STATEMENT_END);

				// 拼接comments
				for (int i = 0; i < rows - 1; i++) {
					sqlStr.append(SQLConsts.WIN_NEXT_LINE)
							.append(SQLConsts.SQL_COMMENT_1).append(tableName)
							.append(SQLConsts.DOT).append(columnNames[i])
							.append(SQLConsts.SQL_COMMENT_2)
							.append(SQLConsts.QUOT).append(columnRemarks1[i])
							.append(SQLConsts.DOT).append(columnRemarks2[i])
							.append(SQLConsts.QUOT)
							.append(SQLConsts.STATEMENT_END)
							.append(SQLConsts.WIN_NEXT_LINE)
							.append(SQLConsts.WIN_NEXT_LINE);
				}
				String sql = sqlStr.toString();
				_log.info("生成的基表建表语句:\r\n" + sql);
				String parent = SQLConsts.PATH_SQL_SAVE+tableName;
				File parentDir = new File(parent);
				if(!parentDir.exists()){
					parentDir.mkdir();
				}
				//生成基表sql
//				String subFileType = ".sql";
				String baseTable = parent + File.separator + tableName + SQLConsts.FILE_TYPE_SQL;
				writeSqlFile(baseTable, sql);
				
				//用于存储各中心sql
				StringBuilder[] centers = new StringBuilder[4];
				centers[0] = new StringBuilder();//用于记录中心1的所有sql 571 572
				centers[1] = new StringBuilder();//用于记录中心2的所有sql 579 574 570
				centers[2]= new StringBuilder();//用于记录中心3的所有sql 580 578 577
				centers[3] = new StringBuilder();//用于记录中心4的所有sql 576 573 575
				
				//生成分表数据，根据参数选择分表参数
				if(isSplitTableByBillingCycleId && isSplitTableByRegionId){
					centers = splitTableByRegionIdAndBillingCycleId(centers, parent, tableName, sql);
				}else if(isSplitTableByBillingCycleId){
					splitTableByBillingCycleId(parent, tableName, sql);
				}else if(isSplitTableByRegionId){
					centers = splitTableByRegionId(centers, parent, tableName, sql);
				}else{
					_log.warn("未分表");
				}
				
				//写各中心分表语句，易于建表
				if(isSplitTableByRegionId){
					for(int i=0;i<centers.length;i++){
						String tmpCenterSqlFileName = parent + File.separator + tableName + "中心" + (i+1) + SQLConsts.FILE_TYPE_SQL;
						writeSqlFile(tmpCenterSqlFileName, centers[i].toString());
					}
				}
				
				// 生成表结构excel
				File xlsFile = new File(SQLConsts.PATH_EXCEL_SAVE
						+ tableName + ".xls");
				// 创建一个工作簿
				WritableWorkbook workbook = Workbook.createWorkbook(xlsFile);
				// 创建一个工作表
				WritableSheet sheetOut = workbook.createSheet(tableName, 0);
				Label label = null;
				for (int row = 0; row < rows - 1; row++) {
					for (int col = 0; col < 3; col++) {
						switch (col) {
						case 0:
							label = new Label(col, row, columnNames[row]);
							break;
						case 1:
							String content = "";
							content = columnTypes[row];
							if (!SQLConsts.ORACLE_DATE.equals(content)) {
								content += SQLConsts.PRE_FIX
										+ columnLengths[row]
										+ SQLConsts.SUB_FIX;
							}
							label = new Label(col, row, content);
							break;
						case 2:
							label = new Label(col, row, columnRemarks1[row]
									+ columnRemarks2[row]);
							break;
						default:
							_log.error("生成表结构失败");
							return;
						}
						// 向工作表中添加数据
						sheetOut.addCell(label);
					}
				}
				workbook.write();
				workbook.close();
				_log.info("生成结束");
			}
		} catch (BiffException | IOException e) {
			_log.error(e);
		}
	}
	private static StringBuilder[] splitTableByRegionId(StringBuilder[] centers, String fileSavePath, String baseTableName, String sql) throws IOException{
		for(int i=0;i<REGIONIDS.length;i++){
			String regionId = REGIONIDS[i];
			String tmpTableName = baseTableName + SQLConsts.SQL_UNDERLINE + regionId; 
			String tmpFileName = fileSavePath + File.separator  + tmpTableName + SQLConsts.FILE_TYPE_SQL;
			File file = new File(tmpFileName);
			OutputStream output = new FileOutputStream(file);
			String tmpSql = sql.replaceAll(baseTableName, tmpTableName);
			switch(regionId){
			case "571":
			case "572": centers[0].append(tmpSql);break;
			case "579": 
			case "574":
			case "570":centers[1].append(tmpSql);break;
			case "580":
			case "578":
			case "577":centers[2].append(tmpSql);break;
			case "576":
			case "573":
			case "575":centers[3].append(tmpSql);break;
			default:_log.error("地市编码错误");break;
			}
			IOUtils.write(tmpSql, output);	
			_log.info("生成的sql文件为" + tmpFileName);
		}
		return centers;
	}
	
	private static void splitTableByBillingCycleId(String fileSavePath, String baseTableName, String sql) throws IOException{
			for(int j=SQLConsts.SQL_BILLING_CYCLE_ID_START;j<=SQLConsts.SQL_BILLING_CYCLE_ID_END;j++){
				String tmpTableName = (baseTableName +  SQLConsts.SQL_UNDERLINE + j);
				String tmpFileName = fileSavePath + File.separator  + tmpTableName + SQLConsts.FILE_TYPE_SQL;
				File file = new File(tmpFileName);
				OutputStream output = new FileOutputStream(file);
				String tmpSql = sql.replaceAll(baseTableName, tmpTableName);
				IOUtils.write(tmpSql, output);
				_log.info("生成的sql文件为" + tmpFileName);
			}
	}
	
	private static StringBuilder[] splitTableByRegionIdAndBillingCycleId(StringBuilder[] centers, String fileSavePath, String baseTableName, String sql) throws IOException{
		for(int i=0;i<REGIONIDS.length;i++){
			String regionId = REGIONIDS[i];
			String tmpTableName = baseTableName + SQLConsts.SQL_UNDERLINE + regionId; 
			for(int j=SQLConsts.SQL_BILLING_CYCLE_ID_START;j<=SQLConsts.SQL_BILLING_CYCLE_ID_END;j++){
				String tmpTableName2 = (tmpTableName +  SQLConsts.SQL_UNDERLINE + j);
				String tmpFileName = fileSavePath + File.separator  + tmpTableName2 + SQLConsts.FILE_TYPE_SQL;
				File file = new File(tmpFileName);
				OutputStream output = new FileOutputStream(file);
				String tmpSql = sql.replaceAll(baseTableName, tmpTableName2);
				switch(regionId){
				case "571":
				case "572": centers[0].append(tmpSql);break;
				case "579": 
				case "574":
				case "570":centers[1].append(tmpSql);break;
				case "580":
				case "578":
				case "577":centers[2].append(tmpSql);break;
				case "576":
				case "573":
				case "575":centers[3].append(tmpSql);break;
				default:_log.error("地市编码错误");break;
				}
				IOUtils.write(tmpSql, output);
				_log.info("生成的sql文件为" + tmpFileName);
			}
		}
		return centers;
	}
	private static void writeSqlFile(String sqlFileName, String sql) throws IOException {
		File file = new File(sqlFileName);
		OutputStream output = new FileOutputStream(file);
		IOUtils.write(sql, output);
		_log.info("生成的sql文件为" + sqlFileName);
	}
}
