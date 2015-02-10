package mas.machine.behaviors;

import jade.core.behaviours.OneShotBehaviour;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import mas.machine.Simulator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class LoadSimulatorParamsBehavior extends OneShotBehaviour{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private XSSFWorkbook workBook;
	private XSSFSheet simulatorParameterSheet = null;
	private String filePath;
	private String fileName =  "machine_config.xlsx";
	private Logger log;
	private long minute2Millis = 60000;

	@Override
	public void action() {
		log = LogManager.getLogger();
		this.filePath = System.getProperty("user.dir");
		try {
			InputStream fStream = new FileInputStream (filePath + 
					"\\" + fileName); 

			workBook = new XSSFWorkbook(fStream); 

			simulatorParameterSheet = workBook.getSheetAt(0);
		} catch(IOException e){
			log.debug("Error in opening excel File!");
			e.printStackTrace();
		}

		Iterator<Row> rows = simulatorParameterSheet.rowIterator();
		XSSFRow row = (XSSFRow) rows.next();
		while( rows.hasNext() ) {

			row = (XSSFRow) rows.next();
			Iterator<Cell> cells = row.cellIterator();
			int cellNumber = 0;
			
			while(cells.hasNext()) {

				XSSFCell cell = (XSSFCell) cells.next();

				switch(cellNumber) {
				case 0:
//					Simulator.r = (double)cell.getNumericCellValue();
					break;
				case 1:
					Simulator.percent = (double)cell.getNumericCellValue();
					Simulator.percent /= 100;
//					log.info("percent is " + Simulator.percent);
					break;
				case 2:
					Simulator.meanLoadingTime = (double)cell.getNumericCellValue();
					break;
				case 3:
					Simulator.sdLoadingTime = (double)cell.getNumericCellValue();
					break;
				case 4:
					Simulator.meanUnloadingTime = (double)cell.getNumericCellValue();
					break;
				case 5:
					Simulator.sdUnloadingTime =(double)cell.getNumericCellValue();
					break;
				case 6:
					Simulator.mean_shiftInMean = (double)cell.getNumericCellValue();
					break;
				case 7:
					Simulator.sd_shiftInMean = (double)cell.getNumericCellValue();
					break;
				case 8:
					Simulator.mean_shiftInSd = (double)cell.getNumericCellValue();
					break;
				case 9:
					Simulator.sd_shiftInSd = (double)cell.getNumericCellValue();
					break;
				case 10:
					Simulator.rateShift = (double)cell.getNumericCellValue();
					break;
				case 11:
					Simulator.fractionDefective = (double)cell.getNumericCellValue();
					break;
				}
				cellNumber++;
			}
		}
		try {
			workBook.close();
		} catch (IOException e) {
			log.debug("Error in closing excel file");
			e.printStackTrace();
		}
	}
}
