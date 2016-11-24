package org.rm3umf.persistenza.postgreSQL;
import org.apache.log4j.Logger;
import org.rm3umf.domain.Period;
import org.rm3umf.persistenza.PeriodDAO;
import org.rm3umf.persistenza.PersistenceException;


public class PeriodProxy extends Period {
	
	private Logger logger = Logger.getLogger("persistenza.postgreSQL.PeriodProxy");
	private boolean caricatoDataInizio = false;
	private boolean caricatoDataFine=false;
	
	public String getDataInizio() { 
        
        logger.info("PeriodProxy called");
		if (!this.caricatoDataInizio) {
			PeriodDAO dao = new PeriodDAOpostgreSQL();
			try {
				this.setDataInizioPeriodo(dao.retriveDataInizio(this.getIdPeriodo()));
				this.caricatoDataInizio = true;
			}
			catch (PersistenceException e) {
				throw new RuntimeException(e.getMessage() + "");
			}
		}
		return super.getDataInizioPeriodo(); 
	}
	
public String getDataFine() { 
        
        logger.info("PeriodProxy called");
		if (!this.caricatoDataFine) {
			PeriodDAO dao = new PeriodDAOpostgreSQL();
			try {
				this.setDataFinePeriodo( dao.retriveDataFine(this.getIdPeriodo()));
				this.caricatoDataFine = true;
			}
			catch (PersistenceException e) {
				throw new RuntimeException(e.getMessage() + "");
			}
		}
		return super.getDataFinePeriodo(); 
	}

}
