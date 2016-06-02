package de.luh.chkater.registrationspammerdetection.rest.server;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import de.luh.chkater.registrationspammerdetection.rest.servlet.RegistrationSpammerDetectionServlet;

/**
 * Job for calling the classifier rebuild
 *
 * @author kater
 */
public class RebuildCron implements Job{

	/* (non-Javadoc)
	 * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
	 */
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		try {
			RegistrationSpammerDetectionServlet.rebuildClassifier();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
//			log.error("TODO", e);
		}		
	}
	
}
