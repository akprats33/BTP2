package mas.localScheduling.plan;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import mas.job.OperationType;
import mas.job.jobAttribute;
import mas.job.jobDimension;
import mas.job.jobOperation;
import mas.util.ID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import jade.core.behaviours.OneShotBehaviour;
import bdi4jade.plan.PlanBody;
import bdi4jade.plan.PlanInstance;
import bdi4jade.plan.PlanInstance.EndState;

public class ReadFile extends OneShotBehaviour implements PlanBody {

	private String jobFilePath;
	private XSSFSheet localSheet;
	private PlanInstance PI;
	private Logger log=LogManager.getLogger();

	@Override
	public EndState getEndState() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void init(PlanInstance arg0) {
		this.jobFilePath = System.getProperty("user.dir");
		
		XSSFWorkbook wb;
		try{
			FileInputStream file=new FileInputStream(this.jobFilePath +
					"\\LSAparams.xlsx");	
			wb = new XSSFWorkbook(file);

			localSheet = wb.getSheetAt(0);

		}catch(IOException e){
			e.printStackTrace();
		}
		
		PI=arg0;
	}

		

	@Override
	public void action() {
		Iterator<Row> rows = localSheet.rowIterator();
		

		
		int rowCount=0;
		while( rows.hasNext() ) {
			XSSFRow row = (XSSFRow) rows.next();
			Iterator<Cell> cells = row.cellIterator();
			int count = 0; 
			while(cells.hasNext()) {
				XSSFCell cell = (XSSFCell) cells.next();

				switch(count) {
				case 0:
					//parameter name
					break;

				case 1:
					switch(rowCount){
					case 0:
						PI.getBeliefBase().updateBelief(ID.LocalScheduler.BeliefBaseConst.ProcessingCost,
								cell.getNumericCellValue());
						
						break;
					
					case 1:
						String[] operations=cell.getStringCellValue().split(",");
						log.info(operations);
						PI.getBeliefBase().updateBelief(ID.LocalScheduler.BeliefBaseConst.supportedOperations,
								operations);
						
					}
					break;
			}
				count++;
			}
			rowCount++;
		}
	}
}
			
		


