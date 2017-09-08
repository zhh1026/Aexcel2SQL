/**

 * @user zhh
 * @date Jul 27, 2017
 * 
 */
package effective;

import org.junit.Test;

import effective.db.sqlcreator.Excel2SQLUtil;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

/**
 * @author zhh
 * @className effective.ExcelUse
 *
 */
public class TestExcelUse {

	@Test
	public void excel() {
		try {
			Excel2SQLUtil.transExcel2SQL_CREATE_TABLE_AND_generateTableStructureExcel("F:/错误表.xls", false, false);
		} catch (RowsExceededException e) {
			e.printStackTrace();
		} catch (WriteException e) {
			e.printStackTrace();
		}
	}
	
}
