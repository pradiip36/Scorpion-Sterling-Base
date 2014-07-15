package com.kohls.ibm.ocf.pca.printUtils;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.w3c.dom.Element;

import com.yantra.yfc.rcp.YRCConstants;
import com.yantra.yfc.rcp.YRCEditorInput;
import com.yantra.yfc.rcp.YRCEditorPart;
import com.yantra.yfc.rcp.YRCPlatformUI;
import com.yantra.yfc.rcp.YRCXmlUtils;

public class KohlesPrintReportBrowserEditor extends YRCEditorPart {
	public static final String ID_Editor = "com.kohls.ibm.ocf.pca.printUtils.KohlesPrintReportBrowserEditor";
	public static final String titleKey = "Report Details";
	private Composite pnlroot = null;
	public String url;
	public KohlesPrintReportBrowserEditor(){
		super();
		//url=this.url;
	}
	public Composite createPartControl(Composite parent, String arg1) {
			
		
			YRCEditorInput ip = ((YRCEditorInput)getEditorInput());
			Element ele = ip.getXml();
			url = YRCXmlUtils.getAttribute(ele, "JasperUrl");
			
		pnlroot=new KohlesPrintReportBrowserPage(parent,SWT.NONE,url);
		pnlroot.setData(YRCConstants.YRC_OWNERPART, this);
		setPartName(YRCPlatformUI.getString(titleKey));
		return pnlroot;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yantra.yfc.rcp.YRCEditorPart#postSetFocus()
	 */
	public void postSetFocus() {
		pnlroot.setFocus();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.ISaveablePart#isDirty()
	 */
	public boolean isDirty() {

		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.ISaveablePart#isSaveAsAllowed()
	 */
	public boolean isSaveAsAllowed() {

		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.ISaveablePart#doSave(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void doSave(IProgressMonitor monitor) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.ISaveablePart#doSaveAs()
	 */
	public void doSaveAs() {

	}

}
