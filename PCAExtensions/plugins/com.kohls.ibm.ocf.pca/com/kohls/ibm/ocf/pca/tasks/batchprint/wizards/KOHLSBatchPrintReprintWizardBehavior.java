/**
 *  © Copyright  2009 Sterling Commerce, Inc.
 */
package com.kohls.ibm.ocf.pca.tasks.batchprint.wizards;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.w3c.dom.Element;

import com.kohls.ibm.ocf.pca.tasks.batchprint.wizardpages.KOHLSBatchPrintReprintScreen;
import com.kohls.ibm.ocf.pca.tasks.batchprint.wizardpages.KOHLSBatchPrintScreen;
import com.yantra.yfc.rcp.IYRCComposite;
import com.yantra.yfc.rcp.IYRCPanelHolder;
import com.yantra.yfc.rcp.YRCWizardBehavior;

/**
 * @author Kiran Potnuru
 * 
 */
public class KOHLSBatchPrintReprintWizardBehavior extends YRCWizardBehavior
		
{

	private Object inputObject;
	private String currentCustomerMessage;
	private String csrMessage;
	private Element inputElement;

	/**
	 * @param ownerComposite
	 * @param formId
	 * @param wizardInput
	 */
	public KOHLSBatchPrintReprintWizardBehavior(Composite ownerComposite,
			String formId, Object inputObject)
	{
		super(ownerComposite, formId, inputObject);
		this.inputObject = inputObject;
		init();
	}

	@Override
	public void pageBeingDisposed(String arg0)
	{
		// TODO Auto-generated method stub

	}

	
	public boolean canMoveToNextEditor()
	{
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yantra.pca.ycd.rcp.tasks.relatedTask.IYCDTaskContributor#canPerformNextTask(org.w3c.dom.Element)
	 */
	public boolean canPerformNextTask(Element elemTask)
	{
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yantra.pca.ycd.rcp.tasks.relatedTask.IYCDTaskContributor#getInputForTask(java.lang.String,
	 *      java.lang.String)
	 */
	public Element getInputForTask(String categoryId, String taskId)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yantra.yfc.rcp.foundation.containers.callcenter.IYRCCallCenterMessageNew#getCustomerName()
	 */
	public String getCustomerName()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yantra.yfc.rcp.foundation.containers.callcenter.IYRCCallCenterMessageNew#setCurrentCsrMessage(java.lang.String)
	 */
	public void setCurrentCsrMessage(String csrMessage)
	{
		this.csrMessage = csrMessage;
		pageChanged(IYRCPanelHolder.PROP_MESSAGES);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yantra.yfc.rcp.foundation.containers.callcenter.IYRCCallCenterMessageNew#setCurrentCustomerMessage(java.lang.String)
	 */
	public void setCurrentCustomerMessage(String customerMessage)
	{
		this.currentCustomerMessage = customerMessage;
		pageChanged(IYRCPanelHolder.PROP_MESSAGES);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yantra.yfc.rcp.IYRCStatusMessageProvider#getCustomerMessage()
	 */
	public String getCustomerMessage()
	{
		return currentCustomerMessage;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yantra.yfc.rcp.IYRCStatusMessageProvider#getStatusMessage()
	 */
	public String getStatusMessage()
	{
		return csrMessage;
	}


	@Override
	public IYRCComposite createPage(String pageIdToBeShown,
			Composite ownerComposite)
	{
		if (pageIdToBeShown.equals(KOHLSBatchPrintReprintScreen.FORM_ID))
		{
			KOHLSBatchPrintReprintScreen page = new KOHLSBatchPrintReprintScreen(
					ownerComposite, SWT.None, inputObject);
			page.setWizBehavior(this);

			return page;
		}
		return null;
	}

	
}
